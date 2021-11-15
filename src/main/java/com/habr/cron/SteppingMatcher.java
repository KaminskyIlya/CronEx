package com.habr.cron;

/**
 * Matcher of calendar element for type values as 'range with step' (a-b/n)
 * and also 'asterisk with step' (* / n)
 * Unmodified object. Thread-safe.
 *
 * Difficulty:
 *  matching one value - O(1)
 *  find nearest value - O(1)
 * Used memory:
 *  12 bytes
 */
class SteppingMatcher implements DigitMatcher
{
    private final int min;
    private final int max;
    private final int step;

    public SteppingMatcher(int start, int stop, int step)
    {
        min = start;
        max = stop - (stop - min) % step;
        this.step = step;
    }

    public boolean match(int value)
    {
        return (value - min) % step == 0;
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
        int r = (value - min) % step;
        return value - r + step;
    }

    public int getPrev(int value)
    {
        int r = (value - min) % step;
        return r > 0 ? value - r : value - step;
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
