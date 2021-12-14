package com.habr.cron;

/**
 * Bit map of days in week for months, years and weeks.
 *
 * The class is used to quickly check if the current date matches the schedule and the selected days
 * of the week at the same time.
 */
class DaysMap
{
    /**
     * The days bits map
     */
    private byte map = 0;
    /**
     * The week's days bits map for schedule '*'
     */
    public static final byte FULL_MAP = (byte)0x7F;


    /**
     * Adds a value into map
     *
     * @param value 1..7  (1 - Sunday, 2 - Monday, ..., 7 - Saturday)
     */
    public void addValue(int value)
    {
        map |= (byte) (1 << ((value-1) % 7));
    }


    /**
     * @return a weekly bit map for a month or year
     */
    public byte getMap()
    {
        return map;
    }

    /**
     * Sets a new weekly bit map for a month or year
     *
     * @param value then new weekly bit map
     */
    public void setMap(byte value)
    {
        map = value;
    }

    /**
     * Adds another bit to the weekly map.
     * It is used to form an annual bit map from a series of monthly maps.
     *
     * @param map to add
     */
    public void addMap(byte map)
    {
        this.map |= map;
    }

    /**
     * Check whether the weekly bit maps overlap.
     * It helps to quickly determine whether there are days in a month or year
     * that fall on the required days of the week and correspond to the schedule.
     *
     * @param map tested map
     * @return <b>true</b> if at least one day of the month/year falls on the required day of the week
     */
    public boolean intersects(byte map)
    {
        return (this.map & map) != 0;
    }

    /**
     * Checks whether the weekdays map contains the specified bit
     *
     * @param bit to test
     * @return <b>true</b>, if contains
     */
    public boolean contains(int bit)
    {
        return (map & (1 << bit)) != 0;
    }

    /**
     * @return <b>true</b> if all days in week is available
     */
    public boolean isAsterisk()
    {
        return map == FULL_MAP;
    }








    /**
     * Scrolls the weekly bit map by the specified number of positions to the left (cyclically to the highest bits).
     * For example, the input is 0010010b, which means: Monday and Thursday (the lowest bit is Sunday).
     * It is necessary to scroll 3 days ahead. We will get: 0010001b (Sunday, Thursday).
     * If again, we get 0001001b (Sunday, Wednesday).
     *
     * @param map the source map
     * @param shift the any positive number
     * @return cycled rotated 7-day bits map
     */
    public static byte rollWeekMap(byte map, int shift) // makes the '<<' cycled operator in loop of 7 bites
    {
        if ( shift > 0 )
        {
            shift %= 7;
            int mask = 0x7f >> shift;

            int lo = map >> (7 - shift);
            int hi = (map & mask) << shift;

            map = (byte) ( lo | hi );
        }
        return map;
    }

    /**
     * Scrolls the weekly bit map to the specified year.
     *
     * @param map the initial map
     * @param year to scroll
     * @return weekdays map for this year
     */
    public static byte rollMapByYear(byte map, int year)
    {
        /*
            We believe that we set the days of January 1995.
            Because for 01.01.1995 fell on Sunday, that is,
            the first day of the month has position 0 in the map.
            In the future, to get a map for a specific year / month,
            we simply scroll to a given number of years / months
         */
        int years = year - 1995; // how many years have passed since 1995?
        int leaps = (year - 1993) / 4; // and how many leap years?
        int shift = years + leaps;

        return rollWeekMap(map, shift);
    }


    // the number of days since the beginning of the year to the beginning of each month (modulo 7)
    private static final int MONTH_SHIFTS[] = new int[]{0, 3, 3, 6, 1, 4, 6, 2, 5, 0, 3, 5};

    /**
     * Scrolls the weekly bit map to the specified month
     *
     * @param map the source map
     * @param month 1..12
     * @param isLeapYear true, for leap years
     * @return weekdays map for this month
     * @throws ArrayIndexOutOfBoundsException if month not in range [1..12]
     */
    public static byte rollMapByMonth(byte map, int month, boolean isLeapYear)
    {
        if ( month == 1 ) return map;
        int shift = MONTH_SHIFTS[month-1] + (month > 2 && isLeapYear ? 1 : 0);

        return rollWeekMap(map, shift);
    }

}