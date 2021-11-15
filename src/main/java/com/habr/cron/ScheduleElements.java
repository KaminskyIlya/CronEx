package com.habr.cron;

import java.util.Calendar;

/**
 *  Constants and constraints for schedule elements and used format.
 */
enum ScheduleElements
{
    // Java Calendar field name, bounds according schedule format,  bounds specified for Java Calendar

    YEAR(Calendar.YEAR, 2000, 2100),

    MONTH(Calendar.MONTH, 1, 12),

    DAY_OF_MONTH(Calendar.DAY_OF_MONTH, 1, 31),

    DAY_OF_WEEK(Calendar.DAY_OF_WEEK, 0, 6),

    HOURS(Calendar.HOUR_OF_DAY, 0, 23),

    MINUTES(Calendar.MINUTE, 0, 59),

    SECONDS(Calendar.SECOND, 0, 59),

    MILLIS(Calendar.MILLISECOND, 0, 999);

    public static final int LAST_DAY_OF_MONTH_CODE = 32;
    public static final int FEBRUARY_LAST_DAY = 28; // NOTE: Only for Gregorian calendar
    public static final int FEBRUARY_LEAP_DAY = 29;

    /**
     * field of java.util.Calendar
     */
    final int fieldId;
    /**
     * The minimum value according to the schedule format
     */
    final int min;
    /**
     * The maximum value according to the schedule format
     */
    final int max;

    ScheduleElements(int fieldId, int min, int max)
    {
        this.fieldId = fieldId;
        this.min = min;
        this.max = max;
    }
}
