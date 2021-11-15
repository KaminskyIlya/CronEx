package com.habr.cron;

/**
 * Bitmap utils.
 */
final class BitmapUtils
{
    private BitmapUtils() {}

    /**
     * Search in bitmap the first non-null bit (from low to high)
     *
     * @param map the 64-bits value to search
     * @param start the bit number to start from
     * @return position of first low non-null bit (zero's order),
     *         or 64, if no bit found
     */
    public static int forwardScanBit(long map, int start)
    {
        if ( start < 64 )
        {
            start = Math.max(start, 0);
            long bit = 1L << start;

            for (int i = start; i <= 63; i++, bit <<= 1)
            {
                if ((map & bit) != 0) return i;
            }
        }
        return 64;
    }

    /**
     * Search in bitmap the last non-null bit (from high to low)
     *
     * @param map the 64-bits value to search
     * @param start the bit number to start from
     * @return position of first high non-null bit (zero's order),
     *         or -1, if no bit found
     */
    public static int backwardScanBit(long map, int start)
    {
        if ( start >= 0 )
        {
            start = Math.min(start, 63);
            long bit = 1L << start;

            for (int i = start; i >= 0; i--, bit >>= 1)
            {
                if ((map & bit) != 0) return i;
            }
        }
        return -1;
    }
}
