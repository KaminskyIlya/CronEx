package com.habr.cron;

/**
 * Base class for list matchers: list of intervals, list of ranges.
 */
abstract class ListsMatcher implements DigitMatcher, MapMatcher
{
    /**
     * Arrays of intervals bounds
     */
    protected final int[] min;
    protected final int[] max;

    protected int low = Integer.MIN_VALUE; // minimal & maximal allowed values according the schedule
    protected int high = Integer.MAX_VALUE; // MUST initialized in finishRange()

    private final BitMapMatcher bitsmap;

    protected static final int NOT_FOUND = -1; // index not existing interval
    protected static final int FIRST = 0; // index of first interval
    protected final int LAST; // index of last interval

    /**
     * Creates list matcher for list of ranges.
     *
      * @param from minimal possible value in all ranges
     * @param to maximal possible value in all ranges
     * @param count count of ranges
     */
    public ListsMatcher(int from, int to, int count)
    {
        LAST = count-1;
        min = new int[count];
        max = new int[count];
        bitsmap = new BitMapMatcher(from, to);
    }

    /**
     * Trying to search the interval that contains a value
     *
     * @return index of interval or -1 (NOT_FOUND)
     */
    protected int search(int value)
    {
        // use binary search algorithm
        int left = NOT_FOUND, right = LAST;
        while (left < right)
        {
            int i = (left + right + 1)/2; // probe this index

            if ( min[i] <= value )
                left = i;

            else
                right = i-1;
        }
        return left;
    }


    public boolean match(int value)
    {
        return bitsmap.match(value);
    }

    public boolean isAbove(int value)
    {
        return value > high;
    }

    public boolean isBelow(int value)
    {
        return value < low;
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



    protected int top = FIRST-1;

    /**
     * Intervals MUST be ordered in ascending and not be intersects!
     *
     * @param from begin value (include)
     * @param to end value (include)
     * @param dist distance between values
     */
    public void addRange(int from, int to, int dist)
    {
        assert top < LAST;

        top++;
        min[top] = from;
        max[top] = to;

        bitsmap.addRange(from, to, dist);
    }

    public void finishRange()
    {
        low = min[FIRST];
        high = max[LAST];
    }

    public void addValue(int value)
    {
        throw new UnsupportedOperationException(); // a single constant not supported
    }
}
