package com.habr.cron;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Schedules parser.
 * Constructs the schedule model.
 */
class Parser
{
    private ScheduleModel model;

    public ScheduleModel getScheduleModel()
    {
        if ( model == null )
            throw new IllegalStateException();

        return model;
    }


    public void parse(String schedule) throws ScheduleFormatException
    {
        if ( schedule == null || schedule.trim().isEmpty() )
            throw new ScheduleFormatException("Empty schedule.", schedule);



        model = new ScheduleModel();


        StringTokenizer parts = new StringTokenizer(schedule, " "); // split by 'date', 'week day', 'time'
        while ( parts.hasMoreTokens() )
        {
            String part = parts.nextToken();

            if ( isDateSchedule(part) )
            {
                if ( model.isTimePresent() || model.isDatePresent() )
                    throw new ScheduleFormatException("Invalid schedule. Time MUST follow AFTER date.", schedule);

                parseDate(part);
            }
            else if ( isTimeSchedule(part) )
            {
                if ( model.isTimePresent() )
                    throw new ScheduleFormatException("Invalid schedule. The Time element is redundant.", schedule);

                parseTime(part);
            }

            else if ( isWeekDaySchedule(part) )
            {
                if ( !model.isDatePresent() || model.isTimePresent() || model.isWeekDayPresent() )
                    throw new ScheduleFormatException("Invalid schedule. Weekday MUST follow AFTER date.", schedule);

                parseWeekDay(part);
            }
            else
                throw new ScheduleFormatException("Unexpected element '" + part + "' in schedule.", schedule);
        }



        if ( !model.isTimePresent() )
            throw new ScheduleFormatException("Time is not present in schedule (it required).", schedule);


        model.initDefaults();
        model.check(schedule);
        model.fixup();
    }





    private boolean isDateSchedule(String schedule)
    {
        return schedule.contains(".") && !schedule.contains(":");
    }

    private boolean isTimeSchedule(String schedule)
    {
        return schedule.contains(":");
    }

    private boolean isWeekDaySchedule(String schedule)
    {
        return !schedule.contains(".") && !schedule.contains(":");
    }



    private void parseDate(String scheduleDate) throws ScheduleFormatException
    {
        String elements[] = scheduleDate.split("\\."); // date elements separates via dot
        if ( elements.length != 3 )
            throw new ScheduleFormatException("Wrong date in schedule (expected exactly 3 elements).", scheduleDate);

        processElement(ScheduleElements.YEAR, elements[0]);
        processElement(ScheduleElements.MONTH, elements[1]);
        processElement(ScheduleElements.DAY_OF_MONTH, elements[2]);
    }

    private void parseTime(String scheduleTime) throws ScheduleFormatException
    {
        String elements[] = scheduleTime.split(":"); // time elements separates via colon
        if ( elements.length != 3 )
            throw new ScheduleFormatException("Wrong time in schedule (expected exactly 3 elements).", scheduleTime);

        processElement(ScheduleElements.HOURS, elements[0]);
        processElement(ScheduleElements.MINUTES, elements[1]);

        if ( elements[2].contains(".") ) // seconds and milliseconds separates via dot
        {
            String last[] = elements[2].split("\\.");

            processElement(ScheduleElements.SECONDS, last[0]);
            processElement(ScheduleElements.MILLIS, last[1]);
        }
        else
            processElement(ScheduleElements.SECONDS, elements[2]);
    }

    private void parseWeekDay(String scheduleDayOfWeek) throws ScheduleFormatException
    {
        processElement(ScheduleElements.DAY_OF_WEEK, scheduleDayOfWeek);
    }

    private void processElement(ScheduleElements element, String range) throws ScheduleFormatException
    {
        RangeList ranges = extractRanges(range);
        model.setModelFor(element, ranges);
    }



    /**
     * Parse a list of values for a single date element: a,b/n,c-d,e-f/n,...
     *
     * @param scheduleValues list of values for the date element
     * @return a group of value ranges for a date element
     */
    private RangeList extractRanges(String scheduleValues) throws ScheduleFormatException
    {
        if ( !scheduleValues.contains(",") ) // simple range or single value
        {
            return new RangeList(parseRange(scheduleValues));
        }
        else // list of ranges/values
        {
            StringTokenizer items = new StringTokenizer(scheduleValues, ",");
            RangeList result = new RangeList(items.countTokens());

            while ( items.hasMoreTokens() )
                result.add( parseRange( items.nextToken() ) );

            return result;
        }
    }


    private static final Pattern ASTERISK = Pattern.compile("\\*(/(\\d+))?");
    private static final Pattern VALUE = Pattern.compile("\\d+");
    private static final Pattern INTERVAL = Pattern.compile("(\\d+)\\-(\\d+)(/(\\d+))?");

    /**
     * Parses the schedule element (single range), values of the form: "*", "a", "a-b", "* /3", "a-b/3".
     *
     * @param scheduleElement schedule element
     * @return range of values
     */
    private Range parseRange(String scheduleElement) throws ScheduleFormatException
    {
        try
        {
            Matcher matcher;

            matcher = ASTERISK.matcher(scheduleElement);
            if ( matcher.matches() )
            {
                int step = matcher.group(2) != null ? Integer.valueOf(matcher.group(2)) : 1;

                return step > 1 ?  new Range(step, true) : Range.ASTERISK;
            }

            matcher = VALUE.matcher(scheduleElement);
            if ( matcher.matches() )
            {
                int v = Integer.valueOf(scheduleElement);

                return new Range(v);
            }

            matcher = INTERVAL.matcher(scheduleElement);
            if ( matcher.matches() )
            {
                int min = Integer.valueOf(matcher.group(1));
                int max = Integer.valueOf(matcher.group(2));
                int step = matcher.group(4) != null ? Integer.valueOf(matcher.group(4)) : 1;

                return new Range(min, max, step);
            }
        }
        catch (NumberFormatException e)
        {
            throw new ScheduleFormatException("Invalid format of the schedule element (expected number).", scheduleElement);
        }

        throw new ScheduleFormatException("Invalid format of the schedule element.", scheduleElement);
    }
}
