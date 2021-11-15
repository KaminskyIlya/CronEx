package com.habr.cron;

import java.util.Arrays;

/**
 * Matcher of calendar element for list of ranges: a-b,c-d/n,e,f-g.
 *
 * Designed for checking months, days, hours, minutes, seconds.
 * The maximum range of acceptable values is 0-63.
 * It is used when the number of acceptable values is set by a small number of ranges.
 *
 * Difficulty:
 *  matching one value - O(1)
 *  find nearest value - O(1)
 * Used memory:
 *  144 bytes maximum, 26 bytes minimum
 */
class HashMapMatcher implements DigitMatcher, MapMatcher
{
    public static final int RANGE_LIMIT = 64;

    private static final byte NO_NEXT = 127;
    private static final byte NO_PREV = -1;

    private int min; // minimal & maximal valid values of calendar's element
    private int max;
    private int low = NO_PREV; // minimal & maximal allowed values according the schedule
    private int high = NO_NEXT;

    /**
     * Bitmap with valid values. Each restricted value has bit 1.
     */
    private long map;

    /**
     * Links of indexes for a values that follows next.
     * For example, if after 5-th day must be 7-th day, and after 7-th day must be 11-th,
     * then next[4] = next[5] = 7,  next[6] = next[7] = next[8] = next[9] = 11.
     * If there is no next value, then NO_NEXT is used.
     */
    private final byte[] next;
    /**
     *
     * The same table as the 'next', only in the opposite direction.
     * If there is no next value, then NO_PREV is used.
     */
    private final byte[] prev;



    public HashMapMatcher(int min, int max)
    {
        this.min = min;
        this.max = max;

        next = new byte[max - min + 1]; Arrays.fill(next, NO_NEXT);
        prev = new byte[max - min + 1]; Arrays.fill(prev, NO_PREV);
    }



    public boolean match(int value)
    {
        long bit = 1L << (value - min);
        return (map & bit) != 0;
    }

    public boolean isAbove(int value)
    {
        return value > high;
    }

    public boolean isBelow(int value)
    {
        return value < low;
    }

    public int getNext(int value)
    {
        return next[value - min];
    }

    public int getPrev(int value)
    {
        return prev[value - min];
    }

    public boolean hasNext(int value)
    {
        return value < high;
    }

    public boolean hasPrev(int value)
    {
        return value > low;
    }

    public int getLow()
    {
        return low;
    }

    public int getHigh()
    {
        return high;
    }




    public void addRange(int from, int to, int step)
    {
        for (int offset = from - min; offset <= to - min; offset += step)
        {
            map |= 1L << offset;
        }
    }

    public void addValue(int value)
    {
        map |= 1L << (value - min);
    }

    public void finishRange()
    {
        byte p = NO_PREV;
        low = min - 1;
        for (int i = min, j = 1; i < max; i++, j++)
        {
            if ( match(i) )
            {
                if ( low < min ) low = i;
                p = (byte) i;
            }
            prev[j] = p;
        }

        byte n = NO_NEXT;
        high = max + 1;
        for (int i = max, j = next.length-1; i >= min; i--, j--)
        {
            next[j] = n;
            if ( match(i) )
            {
                n = (byte) i;
                if ( high > max ) high = i;
            }
        }
    }

}
