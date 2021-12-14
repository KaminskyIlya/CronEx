package com.habr.cron;

import java.util.GregorianCalendar;

import static com.habr.cron.DaysMap.rollMapByMonth;
import static com.habr.cron.ScheduleElements.*;

/**
 * Builder of calendar elements' matcher's set.
 */
class MatcherPool
{
    /**
     * all matchers for calendar elements (month, year, ... except days of the week)
     */
    private final DigitMatcher pool[] = new DigitMatcher[8];
    /**
     * the bitmap of weekdays according the schedule
     */
    private final DaysMap weekMap;
    /**
     * the bitmap for days of any month according the schedule
     */
    private final DaysMap monthMap; // default for January
    /**
     * full bitmap for days in year
     */
    private final DaysMap normalYearMap; // default for 1995
    private final DaysMap leapYearMap; // default for 1996

    /**
     * Has true, if date skipped in schedule or equals to '*.*.*'
     */
    private final boolean anyDate;
    /**
     * Has true, if weekday not present in schedule or equals to '*'
     */
    private final boolean anyWeekDay;


    /**
     * Creates matchers pool for current schedule model.
     * Also creates days maps for week, for any month and any year.
     *
     * @param model current schedule model
     * @throws ScheduleFormatException
     */
    public MatcherPool(ScheduleModel model) throws ScheduleFormatException
    {
        // create matcher's for schedule model
        for ( ScheduleElements element : ScheduleElements.values() )
        {
            if ( element == DAY_OF_WEEK ) continue;

            RangeList ranges = model.getModelFor(element);
            DigitMatcher matcher = MatcherFactory.createInstance(ranges, element);
            pool[element.ordinal()] = matcher;
        }

        weekMap = createWeekMap(model.getModelFor(DAY_OF_WEEK)); // 1..7
        monthMap = createMonthMap(model.getModelFor(DAY_OF_MONTH)); // 1..31

        normalYearMap = createYearMap(monthMap, model.getModelFor(MONTH), false);
        leapYearMap = createYearMap(monthMap, model.getModelFor(MONTH), true);

        anyDate = model.isAnyDate();
        anyWeekDay = model.isAnyWeekDay();

        try {
            fixYearsForLastFebruaryDay(model); // fix schedule for "????.02.29"
        }
        catch (IllegalStateException e)
        {
            throw new ScheduleFormatException(e.getMessage(), model.toString());
        }
    }

    /**
     * @return matcher for current schedule model
     */
    public DigitMatcher[] getMatchersForSchedule()
    {
        return pool;
    }

    public DaysMap getWeekDaysMap()
    {
        return weekMap;
    }

    public DaysMap getMonthDaysMap()
    {
        return monthMap;
    }

    public DaysMap getNormalYearMap()
    {
        return normalYearMap;
    }

    public DaysMap getLeapYearMap()
    {
        return leapYearMap;
    }

    public boolean isAnyDate()
    {
        return anyDate;
    }

    public boolean isAnyWeekDay()
    {
        return anyWeekDay;
    }






    /**
     * Creates bits map for all days in week according schedule
     *
     * @param ranges selected days in schedule
     * @return days bits map for week
     */
    private DaysMap createWeekMap(RangeList ranges)
    {
        DaysMap map = new DaysMap();
        for (Range range : ranges)
        {
            if ( range.isAsterisk() && range.isStepped() )
            {
                for (int i = 1; i <= 7; i += range.step)
                    map.addValue(i);
            }
            else if ( range.isAsterisk() )  // all days of the week
            {
                map.setMap(DaysMap.FULL_MAP);
            }
            else if ( range.isConstant() ) // specific day of the week
            {
                map.addValue(range.getValue());
            }
            else // range of days of the week with step
            {
                for (int day = range.min; day <= range.max; day += range.step)
                    map.addValue(day);
            }
        }
        return map;
    }


    /**
     * Creates bits map for all days in any month according the schedule.
     *
     * @param ranges selected days in schedule
     * @return days bits map for every month
     */
    private DaysMap createMonthMap(RangeList ranges)
    {
        DaysMap map = new DaysMap();
        for (Range range : ranges)
        {
            if ( range.isStepped() && range.isAsterisk() )
            {
                for (int i = 1; i <= 31; i += range.step)
                    map.addValue(i);
            }
            else if ( range.isAsterisk() )  // all days of the week
            {
                map.setMap(DaysMap.FULL_MAP);
            }
            else if ( range.isLastDay() ) // last day of the month
            {
                for (int i = 28; i <= 31; i++)
                    map.addValue(i);
            }
            else if ( range.isByLastDay() ) // range 'x-32/n' for days of the month
            {
                // add all range days
                for (int i = range.min; i <= 31; i += range.step)
                    map.addValue(i);

/*        // perhaps an extra code; need find special tests for detect this; for range 29-32

                // gets valid start range for the 'last days' enumeration
                int m = Math.max(range.min, 28); // 28th - is the first valid 'last' day
                m -= (m - range.min) % range.step; // normalize to step

                // adds the all possible last days numbers according step
                for (int i = m; i <= 31; i += range.step)
                    map.addValue(i);*/
            }
            else if ( range.isConstant() ) // specific day of the month
            {
                map.addValue(range.getValue());
            }
            else // range of days of the month with step
            {
                for (int i = range.min; i <= range.max; i += range.step)
                    map.addValue(i);
            }
        }
        return map;
    }


    /**
     * Creates bits map for all days in a year according the schedule.
     *
     * @param monthMap bits map for all days in a month
     * @param months selected months in schedule
     * @param forLeap must be true, if we calculates map for leap year.
     * @return days bits map for year
     */
    private DaysMap createYearMap(DaysMap monthMap, RangeList months, boolean forLeap)
    {
        DaysMap result = new DaysMap();
        byte janMap = monthMap.getMap();

        for (Range range : months)
        {
            if ( range.isAsterisk() )
            {
                for (int month = 1; month <= 12; month += range.step) // from January to December
                {
                    byte map = rollMapByMonth(janMap, month, forLeap); // get map for this month
                    result.addMap(map); // combine maps for all months
                }
            }
            else if ( range.isConstant() )
            {
                byte map = rollMapByMonth(janMap, range.getValue(), forLeap);
                result.addMap(map);
            }
            else
            {
                for (int month = range.min; month <= range.max; month += range.step)
                {
                    byte map = rollMapByMonth(janMap, month, forLeap);
                    result.addMap(map); // combine maps for selected months
                }
            }
        }
        return result;
    }








    /**
     * Checks special schedule situation: User want only 29-th day in February.
     * This condition forces us to search only for leap years.
     * Leave only leap years in model.
     */
    private void fixYearsForLastFebruaryDay(ScheduleModel model)
    {
        RangeList monthRanges = model.getModelFor(MONTH);
        RangeList dayRanges = model.getModelFor(DAY_OF_MONTH);

        if ( monthRanges.isAlone() & dayRanges.isAlone() )
        {
            Range m = monthRanges.getSingle();
            Range d = dayRanges.getSingle();

            if ( m.isFebruary() && d.isLeapDay() ) // schedule is ?.2.29 ?
            {
                DigitMatcher matcher = pool[YEAR.ordinal()]; // current year matcher, created by schedule
                pool[YEAR.ordinal()] = applyLeapYearsForLastFebruaryDay(matcher);

            }
        }
    }

    /**
     * Setup only leaps years constraints, if user write ?.02.29 schedule (only Feb, and only 29)
     *
     * @param planned current year matcher
     * @return new schedule for year
     * @throws IllegalStateException
     */
    private DigitMatcher applyLeapYearsForLastFebruaryDay(DigitMatcher planned)
    {
        int min = findNearestLeapYearFrom(YEAR.min, +1); // for 2000 it returns 2000; for 2001 it returns 2004
        int max = findNearestLeapYearFrom(YEAR.max, -1); // for 2100 it returns 2096

        BitMapMatcher filtered = new BitMapMatcher(min, max);
        int count = 0;
        for (int year = min; year <= max; year += 4) // intersect all leap years with source schedule
            if ( planned.match(year) )
            {
                filtered.addValue(year);
                count++;
            }
        filtered.finishRange();

        if ( count == 0 )
            throw new IllegalStateException("The schedule specifies only a leap day (02.29), " +
                    "but there is no selected year is a leap year. " +
                    "The schedule does not contain any events.");

        return filtered;
    }


    /**
     * Find nearest or current leap year for 'start' (includes 'start').
     *
     * For example, for (2021, true) returns 2024,
     * for (2021, false) returns 2020.
     *
     * @param year to start search
     * @param v search direction (+1 - forward, -1 - backward)
     * @return start, if it's leap, of next leap year
     */
    private int findNearestLeapYearFrom(int year, int v)
    {
        while ( GregCalendar.isLeap(year) == 0 )
            year += v;

        return year;
    }
}
