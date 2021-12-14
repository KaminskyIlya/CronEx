package com.habr.cron;

/**
 * Matcher of calendar element for type values as 'range without step' (a-b)
 * and also 'asterisk'(*)
 * Unmodified object. Thread-safe.
 *
 * Difficulty:
 *  matching one value - O(1)
 *  find nearest value - O(1)
 * Used memory:
 *  8 bytes
 */
class IntervalMatcher implements DigitMatcher
{
    private final int min;
    private final int max;

    public IntervalMatcher(int min, int max)
    {
        this.min = min;
        this.max = max;
    }

    public boolean match(int value)
    {
        //return min <= value && value <= max; //IMPORTANT fix
        return true;
        //NOTE: According to the contract, the function is not obliged to check
        // the output of the value beyond the range.
        // Therefore, you can return true here.
    }

    public boolean isAbove(int value)
    {
        return value > max;
    }

    public boolean isBelow(int value)
    {
        return value < min;
    }

    public int getNext(int value)
    {
        return value + 1;
    }

    public int getPrev(int value)
    {
        return value - 1;
    }

    public boolean hasNext(int value)
    {
        return value < max;
    }

    public boolean hasPrev(int value)
    {
        return value > min;
    }

    public int getLow()
    {
        return min;
    }

    public int getHigh()
    {
        return max;
    }
}
