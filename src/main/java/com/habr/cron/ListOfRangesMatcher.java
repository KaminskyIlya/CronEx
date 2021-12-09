package com.habr.cron;

/**
 * Special matcher for milliseconds.
 * Handles a complex schedules, such as '40,100-120/2,200-300/3,500-501'.
 * Intervals MUST be ordered in ascending and not be intersects if they have a different step!
 * Used if a small set of ranges is specified (not over 8).
 *
 * Difficulty:
 *  matching one value - O(1)
 *  find nearest value - O(log n)
 * Used memory:
 *  104 bytes maximum, 20 bytes minimum
 *
 *  Maximum loop are: 3 for search range.
 *
 * The concept was suggested by the user @mayorovp in
 * https://habr.com/ru/post/589667/comments/#comment_23717693
 */
class ListOfRangesMatcher extends ListsMatcher
{
    /**
     * Steps for intervals
     */
    private final int[] step;


    public ListOfRangesMatcher(int min, int max, int count)
    {
        super(min, max, count);
        step = new int[count];
    }


    public int getNext(int value)
    {
        int index = search(value);

        if ( index != NOT_FOUND && value < max[index] )
        {
            int s = step[index];
            int r = (value - min[index]) % s;
            return value - r + s;
        }

        if ( index < LAST )
            return min[index+1];

        return value+1; // this should not happen if you call hasNext() before
    }


    public int getPrev(int value)
    {
        int index = search(value);

        if ( index == NOT_FOUND )
            return value-1; // this should not happen if you call hasPrev() before

        if ( value > max[LAST] )
            return max[LAST];

        if ( value > min[index] )
        {
            int s = step[index];
            int r = (value - min[index]) % s;
            return r > 0 ? value - r : value - s;
        }

        // this should not happen if you call hasPrev() before
        return index > FIRST ? max[index-1] : value-1;
    }


    /**
     * Intervals MUST be ordered in ascending and not be intersects!
     * Some intervals may have step > 1.
     *
     * @param from begin value (include)
     * @param to end value (include)
     * @param dist distance between values
     */
    public void addRange(int from, int to, int dist)
    {
        super.addRange(from, to - (to - from) % dist, dist);
        step[top] = dist;
    }
}
