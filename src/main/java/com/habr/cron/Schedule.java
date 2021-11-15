package com.habr.cron;

import java.util.Date;
import java.util.TimeZone;


/**
 * Implementation of events scheduler with resolution of step in milliseconds.
 * Searches dates of event for used defined schedule (see format below).
 * The source schedule can be of any complexity.
 * The initial formulation of the problem is described in the article https://habr.com/ru/post/571342/.
 * Used optimized algorithm. It works fast and requires minimal memory.
 * The average time of event search is 400ns.
 * Thread-Safe. Unmodifiable.
 *
 * Format or schedule:
 *     yyyy.MM.dd w HH:mm:ss.fff            date, day of week, time with milliseconds
 *     yyyy.MM.dd HH:mm:ss.fff              date & time with milliseconds
 *     HH:mm:ss.fff                         time only
 *     yyyy.MM.dd w HH:mm:ss
 *     yyyy.MM.dd HH:mm:ss                  date & time
 *     HH:mm:ss                             time only
 * Where are:
 *     yyyy - year (2000-2100)
 *     MM - month (1-12)
 *     dd - day of month (1-31 or 32). The 32th means a last day of month.  означает последнее число месяца
 *     w - day of weed (0-6). 0 - sunday, 1 - monday, ... 6 - saturday
 *     HH - hours (0-23)
 *     mm - minutes (0-59)
 *     ss - seconds (0-59)
 *     fff - milliseconds (0-999). If not present, default is 0.
 * Each part of the date/time can be set as lists and ranges.
 * For example:
 *     1,2,3-5,10-20/3
 *     means the list of values: 1,2, 3,4,5, 10,13,16,19
 * Step in range defines via slash. Asterisk means any possible value.
 * For example:
 *     * /4
 *     means list of values: 0,4,8,12,16,20
 * You can specify 32 for a last day of month.
 * For example:
 *     *.*.32 12:00:00 - means exactly at 12:00 on the last day of the month
 *     *.*.20-32/3 12:00:00 - means exactly at 12:00 on the 20th, 23th, 26th, ...
 *                 until the end of the month in increments of 3.
 *
 * Schedule examples:
 *     *.9.* /2 1-5 10:00:00.000 means exactly at 10:00 on all days from Mon. to Fri. on odd numbers in September
 *     *:00:00 means the beginning of any hour
 *     *.*.01 01:30:00 means exactly at 01:30 on the first days of each month
 */
public class Schedule implements Cron
{
    private enum Direction { FORWARD, BACKWARD }
    private enum Equality { OR_EQUAL, NO_EQUAL }

    private static final class SearchMode
    {
        public final Direction direction;
        public final Equality equality;

        public SearchMode(Direction direction, Equality equality) {
            this.direction = direction;
            this.equality = equality;
        }

        public boolean toZero()
        {
            return direction == Direction.FORWARD;
        }
        public boolean canEqual()
        {
            return equality == Equality.OR_EQUAL;
        }
    }



    /**
     * Creates an empty instance that will match
     * the schedule as "*.*.* * *:*:*.*" (every 1 ms).
     */
    public Schedule() throws ScheduleFormatException
    {
        this("*.*.* * *:*:*.*");
    }

    /**
     * Creates instance for specified schedule defined by string.
     *
     * @param schedule see format in class description.
     * @throws ScheduleFormatException
     */
    public Schedule(String schedule) throws ScheduleFormatException
    {
        this.schedule = schedule;

        Parser parser = new Parser();
        parser.parse(schedule);

        ScheduleModel model = parser.getScheduleModel();
        pool = new MatcherPool(model);
    }

    public Date NearestEvent(Date d) {
        return findEvent(d, new SearchMode(Direction.FORWARD, Equality.OR_EQUAL));
    }

    public Date NearestPrevEvent(Date d) {
        return findEvent(d, new SearchMode(Direction.BACKWARD, Equality.OR_EQUAL));
    }

    public Date NextEvent(Date d) {
        return findEvent(d, new SearchMode(Direction.FORWARD, Equality.NO_EQUAL));
    }

    public Date PrevEvent(Date d) {
        return findEvent(d, new SearchMode(Direction.BACKWARD, Equality.NO_EQUAL));
    }




    /**
     * Create instance for quick serial generation events.
     * Generator does not consume memory. Works faster. No memory leaks.
     * Generator non thread safe and mutable. Don't cache generator.
     *
     * @param start date to start serial
     * @param forward direction mode; true - is forward, false - is backward.
     * @return generator instance. Not thread safe.
     * The outside process must work with generator with synchronized instruction.
     */
    public ScheduleEventsGenerator getEventsGenerator(Date start, boolean forward)
    {
        SearchMode mode = new SearchMode(forward ? Direction.FORWARD : Direction.BACKWARD, Equality.NO_EQUAL);
        return new EventsGenerator(start, mode);
    }







    private final MatcherPool pool; // pool of schedule's matchers
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC"); // default work timezone

    /**
     * The main function of finding a date that meets the schedule and search mode.
     * It works for a maximum of 8 checks (if no days of the week are specified).
     * Days of the week can add a few more checks.
     *
     * @param date the start date of the search
     * @param mode the search mode (direction and severity)
     * @return suitable date for the conditions (can be equal to the original if mode.equality == OR_EQUAL)
     * @throws IllegalStateException it is not possible to find a date that meets the schedule,
     * for example, when a schedule of the form is set "20.01.02 10-20/2:*:*.*",
     * and now it's 2021 and mode.direction == FORWARD.
     */
    private Date findEvent(Date date, SearchMode mode)
    {
        GregCalendar calendar = new GregCalendar(date, UTC);
        CalendarDigits digits = new CalendarDigits(pool, calendar, mode.toZero());

        while ( isCanSearchDown(digits, calendar, mode.canEqual()) )
        {
            digits.next();
        }

        return fixWeekDay(digits, calendar);
    }


    /**
     * Makes events generator.
     * TODO: test it
     */
    private final class EventsGenerator implements ScheduleEventsGenerator
    {
        // mutable objects
        private final GregCalendar calendar;
        private final CalendarDigits digits;
        private Date date;

        public EventsGenerator(Date start, SearchMode mode)
        {
            calendar = new GregCalendar(start, UTC);
            digits = new CalendarDigits(pool, calendar, mode.toZero());

            while ( isCanSearchDown(digits, calendar, mode.canEqual()) )
            {
                digits.next();
            }

            date = fixWeekDay(digits, calendar);
        }

        public Date last()
        {
            return date;
        }

        public Date next()
        {
            digits.gotoLastDigit();
            digits.increment();
            return date = fixWeekDay(digits, calendar);
        }

        public String schedule()
        {
            return schedule;
        }

        @Override
        public String toString() {
            return schedule;
        }
    }

    /**
     * Implements a direct search for the nearest date from a given date in the schedule.
     *
     * @param digit digits of calendar
     * @return false, if you can not continue further and an unambiguous result is obtained
     *         true, if you need to go down to a lower level
     *
     * @throws IllegalStateException if the current date is out of the range of acceptable values
     * and we have no more options that we could offer.
     */
    private boolean isCanSearchDown(CalendarDigits digit, GregCalendar calendar, boolean canEqual)
    {
        int value = digit.getValue();

        if ( digit.isBelow(value) ) // the current value of the element is less than the allowed lower limit
        {
            digit.initialize();
            return false;
        }

        if ( digit.isAbove(value) ) // the current value of the element is above the allowed upper limit
        {
            digit.prev(); // for YEAR throws IllegalStateException
            digit.increment();
            return false;
        }

        // the current value is within the boundaries
        if ( digit.match(value) && calendar.isCorrect() ) // if the item matches the schedule
        {
            boolean isLast = digit.isLast();

            // we end the search if this is the last element and we are allowed to return the exact equivalent
            if ( isLast && canEqual ) return false;

            // if this is not the last level, let's go down below
            if ( !isLast ) return true;
        }

        digit.increment();
        return false; // search is complete
    }



    /**
     * Corrects the found date in accordance with the restrictions set by the acceptable days of the week.
     *
     * @param digits digits of calendar
     * @return fixed date
     * @throws IllegalStateException if a suitable date cannot be found in the schedule
     */
    private Date fixWeekDay(CalendarDigits digits, GregCalendar calendar)
    {
        DaysMap weekMap = pool.getWeekDaysMap();
        if ( !weekMap.isAsterisk() )
        {
            if ( !weekMap.contains(calendar.getDayOfWeek()) )
            {
                findBestDate(digits, calendar, weekMap);

                digits.gotoHours();
                digits.initialize();
            }
        }
        return calendar.asDate();
    }





    private void findBestDate(CalendarDigits digits, GregCalendar calendar, DaysMap weekMap)
    {
        do
        {
            // find a day in the month that corresponds to the schedule of days of the week
            if ( findBestDay(digits, calendar, weekMap) ) break;

            // find a month in the year that exactly contains at least one day that falls on the selected days of the week
            if ( !findBestMonth(digits, calendar, weekMap, false) )
            {
                // find the next year that exactly contains at least one day that falls on the selected days of the week
                findBestYear(digits, calendar, weekMap);

                // find a month in the next year that exactly contains at least one day that falls on the selected days of the week
                findBestMonth(digits, calendar, weekMap, true);
            }
            /*
            There may be a situation where findBestMonth has found a suitable month,
            but the resetDate() automatically moved to another month,
            because it takes into account the schedule and overflows.
            And in the new month there are no dates falling on the selected days of the week.
            Therefore, we are forced to repeat the cycle again.
            In other words, this cycle can be performed more than 2 times (in extremely rare cases).
            */
        }
        while ( true ); // until we get an IllegalStateException when searching for the year
    }


    private void findBestYear(CalendarDigits digits, GregCalendar calendar, DaysMap weekMap)
    {
        byte ly = pool.getLeapYearMap().getMap();
        byte ny = pool.getNormalYearMap().getMap();
        int year = calendar.year;
        byte ym;

        digits.gotoYear();
        do
        {
            if ( !digits.hasNext(year) )
                throw new IllegalStateException(); // we went beyond the schedule

            year = digits.getNext(year);
            byte yearMap = GregCalendar.isLeap(year) == 1 ? ly : ny;
            ym = DaysMap.rollMapByYear(yearMap, year);
        }
        while ( !weekMap.intersects(ym) );

        calendar.year = year;
        digits.gotoMonth();
        digits.resetDate(); // here sometimes it can be calendar.year != year
        // this is not a problem, because the findBestDate() will correct this in a loop
    }


    private boolean findBestMonth(CalendarDigits digits, GregCalendar calendar, DaysMap weekMap, boolean repeat)
    {
        boolean isLeap = calendar.isLeap();
        int month = calendar.month;

        byte janMap = DaysMap.rollMapByYear(pool.getMonthDaysMap().getMap(), calendar.year);
        byte mm = DaysMap.rollMapByMonth(janMap, month, isLeap);

        digits.gotoMonth();

        // the 'repeat' suppresses the check for 'intersects' in the first iteration of the loop
        // when we need to start the search from the current calendar value, repeat = false
        // when we need to start the search strictly from the beginning of the year, then repeat = true
        while ( !(repeat && weekMap.intersects(mm)) )
        {
            if ( !digits.hasNext(month) ) return false;

            month = digits.getNext(month);
            mm = DaysMap.rollMapByMonth(janMap, month, isLeap);

            repeat = true; // now you can do a check intersects
        }

        calendar.month = month;
        digits.gotoDay();
        digits.resetDate(); // here sometimes it can be calendar.month != month
        return true; // this is not a problem, because the findBestDate() will correct this in a loop
    }


    private boolean findBestDay(CalendarDigits digits, GregCalendar calendar, DaysMap weekMap)
    {
        int day = calendar.day;
        byte dm = (byte)(1 << calendar.getDayOfWeek());

        digits.gotoDay();

        while ( !weekMap.intersects(dm) )
        {
            if ( !digits.hasNext(day) ) return false;

            int next = digits.getNext(day);
            int shift;
            if ( digits.toZero )
            {
                shift = next - day;
            }
            else
            {
                shift = 7 - (day - next) % 7; // reverse to roll '>>'
            }
            dm = DaysMap.rollWeekMap(dm, shift);
            day = next;
        }

        calendar.day = day;
        return true;
    }











    private final String schedule;

    @Override
    public String toString() {
        return schedule;
    }

}
