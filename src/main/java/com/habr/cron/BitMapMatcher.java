package com.habr.cron;

/**
 * Matcher of calendar element for list of ranges: a-b,c-d/n,e,f-g.
 *
 * Designed for checking milliseconds and years.
 * The maximum range of acceptable values is 0-1000.
 * It is used when the number of acceptable values is set by a large number of ranges.
 *
 * Difficulty:
 *  matching one value - O(1)
 *  find nearest value - O(n)
 * Used memory:
 *  144 bytes maximum, 24 bytes minimum
 *
 *  Maximum loop are: 17 (through long) + 63 (through bits)
 */
class BitMapMatcher implements DigitMatcher, MapMatcher
{
    private final int min; // the boundaries of calendar's element
    private final int max;
    private int low = Integer.MIN_VALUE; // minimal & maximal allowed values according the schedule
    private int high = Integer.MAX_VALUE; // MUST initialized in finishRange()

    /**
     * bit map for allow calendar element values
     */
    private final long map[];

    private final static int POWER = 6; // = log_2(64) for quick divide by 64
    private final static int MASK = 63; // = 64 - 1, for quick divide by module 64
    private final static int BITES_IN_MAP_ELEMENT = 64; // bits count in single map element




    public BitMapMatcher(int min, int max)
    {
        this.min = min;
        this.max = max;
        this.map = new long[((max - min) >> POWER) + 1];
    }




    public boolean match(int value)
    {
        if ( value < min || value > max ) return false;

        int v = value - min;
        int el = v >> POWER; // number of map's cell
        int bt = v & MASK; // position of bit
        long b = 1L << bt; // bit in cell

        return (map[el] & b) != 0;
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
        if ( value >= max ) return value + 1; // out of bound
        if ( value < min ) return min; // edge value

        int v = value - min + 1; // search starts a next value
        int el = v >> POWER; // number of map's cell
        int start = v & MASK; // position of bit to start searching (value + 1)

        // now we are looking for the first low non-zero bit (from the 'start')
        int pos = 64;
        while ( pos == 64 && el < map.length )
        {
            if ( map[el] != 0 )
                pos = BitmapUtils.forwardScanBit(map[el], start);
            el++; start = 0; // go to next map cell
        }

        return ((el-1) << POWER) + pos + min;
    }


    public int getPrev(int value)
    {
        if ( value <= min ) return value - 1; // out of bound
        if ( value > max  ) return max; // edge value

        int v = value - min - 1; // search starts a next value
        int el = v >> POWER; // number of map's cell
        int start = v & MASK; // position of bit to start searching (value - 1)

        // now we are looking for the first high non-zero bit (from the 'start')
        int pos = -1;
        while (pos == -1 && el >= 0)
        {
            if ( map[el] != 0 )
                pos = BitmapUtils.backwardScanBit(map[el], start);

            el--; start = BITES_IN_MAP_ELEMENT-1; // go to next map cell
        }

        return ((el+1) << POWER) + pos + min;
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
        for (int value = from; value <= to; value += step)
        {
            addValue(value);
        }
    }

    public void finishRange()
    {
        low = match(min) ? min : getNext(min);
        high = match(max) ? max : getPrev(max);
    }

    public void addValue(int value)
    {
        int v = value - min;
        int el = v >> POWER; // number of map's cell
        int bt = v & MASK; // position of bit
        long b = 1L << bt; // bit in cell

        map[el] |= b;
    }

}