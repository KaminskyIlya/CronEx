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

    private Range[] list;
    private int count = 0;

    public RangeList(int capacity)
    {
        list = new Range[capacity];
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

    public int getCount()
    {
        return count;
    }

    /**
     * Checks when we can use ListOfRangesMatcher.
     * NOTE: MUST be sorted before.
     *
     * @return true if all ranges are without step or have a step, but not intersects
     */
    public boolean isSimpleRanges()
    {
        if ( !isList() ) return false;

        Range prev = null;

        for (Range range : list)
        {
            if (range.isAsterisk()) return false;

            if ( prev != null )
            {
                boolean intersects = range.isIntersects(prev);
                boolean stepped = range.step > 1 || prev.step > 1;

                if ( intersects && stepped )
                    return false;
            }
            prev = range;
        }

        return true;
    }

    /**
     * Checks when we can use ListOfIntervalsMatcher.
     * NOTE: MUST be sorted before.
     *
     * @return true if all ranges are without step
     */
    public boolean isSimpleIntervals()
    {
        if ( !isList() ) return false;

        for (Range range : list)
            if ( range.isAsterisk() || range.isStepped() )
                return false;

        return true;
    }


    /**
     * @return true, if it is single range
     */
    public boolean isAlone()
    {
        return count == 1;
    }

    /**
     * @return true, if it is a list of ranges
     */
    public boolean isList()
    {
        return count > 1;
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


    /**
     * Sorts ranges in ascending order and merges overlapping ranges.
     * Ranges MUST be a simple intervals (isSimpleRanges() == true)
     */
    public void optimize()
    {
        Arrays.sort(list);

        assert isSimpleRanges();

        int f = 0, n = 1;
        Range first = list[f];

        do
        {
            Range next = list[n++];

            if ( !first.isIntersects(next) )
            {
                first = next; f++;
            }
            else
                first.merge(next);

            list[f] = first;
        }
        while ( n < count );

        count = f + 1;
        list = Arrays.copyOf(list, count);
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
            return pos < count;
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
