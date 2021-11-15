package com.habr.cron;

/**
 * A common interface for classes that check the compliance of a calendar digit
 * with a condition set for it.  The calendar digit can be: year, month, day of the month,
 * day of the week, hours, minutes, seconds, milliseconds.
 */
interface DigitMatcher
{
    /**
     * Match the current value by schedule for this calendar element.
     * IMPORTANT: not control value overflow
     *
     * @param value the calendar element value (year, month, ... hour,...)
     */
    boolean match(int value);

    /**
     * Checks whether the calendar element goes beyond the upper border.
     * For example, seconds should be in the range 1-10 by condition, but the current value is 12.
     * So this function will return true.
     *
     * @param value the calendar element value (year, month, ... hour,...)
     * @return true, if above
     */
    boolean isAbove(int value);

    /**
     * Checks whether the calendar element goes beyond the lower border.
     * For example, the clock should be conditionally in the range 0-23, but the current value is -1.
     * So this function will return true.
     *
     * @param value the calendar element value (year, month, ... hour,...)
     * @return true, if below
     */
    boolean isBelow(int value);

    /**
     * Returns a value, lager the current,
     * according to the step of changing the calendar item.
     *
     * For example, if we have "* /3" in schedule of 'days' (each 3-th day of month),
     * then after 5-th day returns 7-th, according to the series: 1,4,7,11,14...
     *
     * IMPORTANT: This function does not check for overflow; we MUST call hasNext first
     *
     * @param value the calendar element value (year, month, ... hour,...)
     * @return nearest lager value
     */
    int getNext(int value);

    /**
     * Returns a value, less then the current,
     * according to the step of changing the calendar item.
     *
     * For example, if we have "* /3" in schedule of 'days' (each 3-th day of month),
     * then after 6-th day returns 4-th, according to the series: 1,4,7,11,14...
     *
     * IMPORTANT: This function does not check for overflow; we MUST call hasNext first
     *
     * @param value the calendar element value (year, month, ... hour,...)
     * @return nearest smaller value
     */
    int getPrev(int value);

    /**
     * Checks that we can get a next value (after specified) according schedule.
     *
     * @param value specified value
     * @return <b>true</b>, if we can
     */
    boolean hasNext(int value);

    /**
     * Checks that we can get a previous value (after specified) according schedule.
     *
     * @param value specified value
     * @return <b>true</b>, if we can
     */
    boolean hasPrev(int value);

    /**
     * @return the minimum value for this calendar element, according schedule and current date
     */
    int getLow();

    /**
     * @return the maximum value for this calendar element, according schedule and current date
     */
    int getHigh();
}
