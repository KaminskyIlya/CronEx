package com.habr.cron;

import java.util.Arrays;
import java.util.Iterator;

/**
 * List of ranges in schedule for calendar element: 1,2,4-11/2,...
 */
final class RangeList implements Iterable<Range>
{
    /**
     * Predefined range type '*'
     */
    public static final RangeList ASTERISK = new RangeList(Range.ASTERISK);

    private final Range[] list;
    private int count = 0;

    public RangeList(int count)
    {
        list = new Range[count];
    }

    public RangeList(Range range)
    {
        list = new Range[]{range};
        count = 1;
    }

    public void add(Range range)
    {
        list[count++] = range;
    }


    /**
     * @return true, if it is single range
     */
    public boolean isAlone()
    {
        return list.length == 1;
    }

    /**
     * @return true, if it is a list of ranges
     */
    public boolean isList()
    {
        return list.length > 1;
    }

    /**
     * @return as simple range
     */
    public Range getSingle()
    {
        assert count == 1;
        return list[0];
    }

    /**
     * @return maximum value of all ranges
     */
    public int getMinimum()
    {
        assert count > 1 || list[0] != Range.ASTERISK;

        int min = Integer.MAX_VALUE;
        for (Range range : list)
            min = Math.min(min, range.min);
        return min;
    }

    /**
     * @return minimum value of all ranges
     */
    public int getMaximum()
    {
        assert count > 1 || list[0] != Range.ASTERISK;

        int max = Integer.MIN_VALUE;
        for (Range range : list)
            max = Math.max(max, range.max);
        return max;
    }

    /**
     * Shifts all values in range by `shift`.
     *
     * @param shift can be positive or negative.
     */
    public void shiftBy(int shift)
    {
        for (Range range : list)
            if ( !range.isAsterisk() )
                range.shiftBy(shift);
    }


    @Override
    public String toString()
    {
        return Arrays.toString(list).replaceAll("[ \\[\\]]", "");
    }

    /**
     * @return iterator of list of ranges
     */
    public Iterator<Range> iterator()
    {
        return new LocalIterator();
    }

    private final class LocalIterator implements Iterator<Range>
    {
        private int pos = 0;

        public boolean hasNext()
        {
            return pos < list.length;
        }

        public Range next()
        {
            return list[pos++];
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
