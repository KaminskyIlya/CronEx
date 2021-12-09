package com.habr.cron;


import java.util.Calendar;

import static com.habr.cron.ScheduleElements.FEBRUARY_LEAP_DAY;
import static com.habr.cron.ScheduleElements.LAST_DAY_OF_MONTH_CODE;

/**
 * Range of valid values.
 * Simple element of schedule (any expression between comma).
 */
final class Range implements Comparable<Range>
{
    public static final Range ASTERISK = new Range();

    int min = -1;
    int max = -1;
    int step = 1;
    boolean asterisk = false;

    /**
     * Create asterisk range '*'
     */
    public Range()
    {
        this.asterisk = true;
    }

    /**
     * Create asterisk range '* /n'
     */
    public Range(int step, boolean dummy)
    {
        this.step = step;
        this.asterisk = true;
    }

    /**
     * Create range with step: 'a-b/n'
     */
    public Range(int min, int max, int step)
    {
        this.min = min;
        this.max = max;
        this.step = step;
    }

    /**
     * Create range without step: 'a-b'
     */
    public Range(int min, int max)
    {
        this.min = min;
        this.max = max;
    }

    /**
     * Create single value range 'x'
     */
    public Range(int value)
    {
        this.min = value;
        this.max = value;
    }



    /**
     * @return true, if this range is asterisk ('*') or asterisk with step ('* /n')
     */
    public boolean isAsterisk()
    {
        return asterisk;
    }

    /**
     * @return true, if this range has a step ('a/n', 'a-b/n')
     */
    public boolean isStepped()
    {
        return step > 1;
    }

    /**
     * @return the range value if it is a constant ('a')
     */
    public int getValue()
    {
        assert isConstant() && step == 1;

        return min;
    }

    /**
     * @return true, if range is single value 'x'
     */
    public boolean isConstant()
    {
        return min == max && !asterisk;
    }

    /**
     * @return true, if range a single 'magic' value for day
     */
    public boolean isLastDay()
    {
        return isConstant() && max == LAST_DAY_OF_MONTH_CODE;
    }

    /**
     * @return true, if range is single 'leap' day of year
     */
    public boolean isLeapDay()
    {
        return isConstant() && max == FEBRUARY_LEAP_DAY;
    }

    /**
     * @return true, if range is single february
     */
    public boolean isFebruary()
    {
        return isConstant() && max == Calendar.FEBRUARY+1;
    }

    /**
     * @return true, if range is 'x-32'
     */
    public boolean isByLastDay()
    {
        return min != max && max == LAST_DAY_OF_MONTH_CODE;
    }

    /**
     * Do shift range by a value
     */
    public void shiftBy(int value)
    {
        min += value; max += value;
    }

    /**
     * Merges two ranges together.
     * All ranges MUST have step = 1
     * The result is equivalent: this.merge(o) == o.merge(this).
     *
     * @param o another range
     */
    public void merge(Range o)
    {
        assert step == 1 && o.step == 1;
        assert isIntersects(o);

        if ( o.min < min ) min = o.min;
        if ( o.max > max ) max = o.max;
    }


    /**
     * Checks that ranges intersects
     *
     * @param o another range
     * @return true, if intersects
     */
    public boolean isIntersects(Range o)
    {
        return !(max < o.min || o.max < min); // not: full on left or full on right
    }

    @Override
    public String toString()
    {
        if ( isAsterisk() )
            return isStepped() ? "*/" + step : "*";

        if ( isConstant() ) return Integer.toString(min);

        return isStepped() ? min + "-" + max + "/" + step : min + "-" + max;
    }


    public int compareTo(Range o)
    {
        return min < o.min ? -1 : min == o.min ? 0 : 1;
    }
}
