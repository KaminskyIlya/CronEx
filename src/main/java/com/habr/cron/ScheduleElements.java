package com.habr.cron;

/**
 *  Constants and constraints for schedule elements and used format.
 */
enum ScheduleElements
{
    YEAR(2000, 2100),

    MONTH(1, 12),

    DAY_OF_MONTH(1, 31),

    DAY_OF_WEEK(0, 6),

    HOURS(0, 23),

    MINUTES(0, 59),

    SECONDS(0, 59),

    MILLIS(0, 999);

    public static final int LAST_DAY_OF_MONTH_CODE = 32;

    public static final int FEBRUARY_LAST_DAY = 28; // NOTE: all above values valid only for Gregorian calendar
    public static final int FEBRUARY_LEAP_DAY = 29;
    public static final int MIN_LAST_DAY = 28;
    public static final int MAX_LAST_DAY = 31;

    /**
     * The minimum value according to the schedule format
     */
    final int min;
    /**
     * The maximum value according to the schedule format
     */
    final int max;

    ScheduleElements(int min, int max)
    {
        this.min = min;
        this.max = max;
    }
}
