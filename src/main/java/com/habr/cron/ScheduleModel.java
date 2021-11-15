package com.habr.cron;

import java.util.GregorianCalendar;

import static com.habr.cron.ScheduleElements.*;

/**
 * Schedule model
 * Note: tested in ParserTest
 */
class ScheduleModel
{
    private final RangeList[] model;

    public ScheduleModel()
    {
        model = new RangeList[ScheduleElements.values().length];
    }



    public RangeList getModelFor(ScheduleElements element)
    {
        return model[element.ordinal()];
    }

    public void setModelFor(ScheduleElements element, RangeList ranges)
    {
        model[element.ordinal()] = ranges;
    }


    /**
     * Initializes missing schedule conditions by default.
     * For example, if day of week is missing in schedule, we use asterisk (any day).
     * Also, we use asterisk for date, and use zero for milliseconds, if these fields not present.
     */
    public void initDefaults()
    {
        if ( !isDatePresent() ) // skipped date is equals to '*.*.*' template
        {
            setModelFor(YEAR, RangeList.ASTERISK);
            setModelFor(MONTH, RangeList.ASTERISK);
            setModelFor(DAY_OF_MONTH, RangeList.ASTERISK);
        }

        if ( !isWeekDayPresent() ) // skipped weekday's is equals to '*' template
            setModelFor(DAY_OF_WEEK, RangeList.ASTERISK);

        if ( !isMillisecondsPresent() ) // skipped milliseconds is equals to '0' exactly
            setModelFor(MILLIS, new RangeList(new Range(0)));

    }
    public boolean isDatePresent()
    {
        return model[YEAR.ordinal()] != null;
    }
    public boolean isTimePresent()
    {
        return model[HOURS.ordinal()] != null;
    }
    public boolean isWeekDayPresent()
    {
        return model[DAY_OF_WEEK.ordinal()] != null;
    }
    public boolean isMillisecondsPresent()
    {
        return model[MILLIS.ordinal()] != null;
    }

    /**
     * Checks logical correctness of the model.
     *
     * @param sourceSchedule the string presentation of source schedule (for diagnostic messages)
     * @throws ScheduleFormatException
     */
    public void check(String sourceSchedule) throws ScheduleFormatException
    {
        checkAsteriskConstInList(sourceSchedule); // asterisk valid only as single '*'
        checkMinMaxRanges(sourceSchedule); // min <= max for all ranges
        checkValidRanges(sourceSchedule); // check correctness of min,max values for all calendar elements
        checkCorrectLeapDay(sourceSchedule); // check that schedule ?.02.29 is present only in leap year
    }

    /**
     * Makes some fixes in model for compatibility with Java's Calendar
     */
    public void fixup()
    {
        // Shifts day of week ranges by one, for presentation of weekday (1..7) in Java's Calendar
        getModelFor(DAY_OF_WEEK).shiftBy(1);

        // Shifts months ranges by one, for presentation of month (0..11) in Java's Calendar
        //getModelFor(MONTH).shiftBy(-1);
    }



    /**
     * Checks for invalid leap days in schedule. For example, schedule '2021.02.29 12:*:*' is invalid,
     * because 2021 is not leap year, and 29 is not valid day for February.
     */
    private void checkCorrectLeapDay(String sourceSchedule) throws ScheduleFormatException
    {
        RangeList mList = getModelFor(MONTH);
        RangeList dList = getModelFor(DAY_OF_MONTH);
        RangeList yList = getModelFor(YEAR);

        if ( mList.isAlone() && dList.isAlone() && yList.isAlone() )
        {
            Range month = mList.getSingle();
            Range day = dList.getSingle();
            Range year = yList.getSingle();

            if ( month.isFebruary() && day.isLeapDay() && year.isConstant() )
            {
                if ( !(new GregorianCalendar().isLeapYear(year.getValue())) )
                    throw new ScheduleFormatException("Invalid schedule for leap day", sourceSchedule);

            }
        }
    }

    /**
     * Checks for all calendar elements has valid ranges
     */
    private void checkValidRanges(String sourceSchedule) throws ScheduleFormatException
    {
        for (ScheduleElements element : ScheduleElements.values())
            for (Range range: getModelFor(element))
                if ( !range.isAsterisk() )
                {
                    boolean valid = (range.min >= element.min) && (range.max <= element.max);

                    if ( !valid && element == DAY_OF_MONTH ) valid = range.isLastDay() || range.isByLastDay();

                    if ( !valid )
                        throw new ScheduleFormatException("The item's schedule is out of range.\n" +
                                "See the schedule constraints for '" + element.toString() + "'.",
                                range, sourceSchedule
                        );
                }
    }

    /**
     * Checks that all ranges is correct min..max
     */
    private void checkMinMaxRanges(String sourceSchedule) throws ScheduleFormatException
    {
        for (RangeList list : model)
            for (Range range : list)
            {
                if ( !range.isAsterisk() && range.min > range.max )
                    throw new ScheduleFormatException(
                    "The range of the element value is set incorrectly. It was expected from less to more.",
                    range, sourceSchedule);
            }
    }

    /**
     * Checks that asterisk follow in list of values. Throws exception, if true.
     * For example, in schedule for year: '2020,*,2021'
     * But '2021,* /4' is valid.
     */
    private void checkAsteriskConstInList(String sourceSchedule) throws ScheduleFormatException
    {
        for (RangeList list : model)
        if ( list.isList() )
            for (Range range : list)
            {
                if ( range.isAsterisk() && !range.isStepped() )
                {
                    throw new ScheduleFormatException(
                            "Asterisk (*) MUST NOT follow in values list.",
                            range, sourceSchedule);
                }
            }
    }





    @Override
    public String toString()
    {
        return String.format("[%s.%s.%s] %s [%s:%s:%s.%s]",
                getModelFor(YEAR), getModelFor(MONTH), getModelFor(DAY_OF_MONTH), getModelFor(DAY_OF_WEEK),
                getModelFor(HOURS), getModelFor(MINUTES), getModelFor(SECONDS), getModelFor(MILLIS)
        );
    }
}
