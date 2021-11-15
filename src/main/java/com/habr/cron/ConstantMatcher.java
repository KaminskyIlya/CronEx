package com.habr.cron;

/**
 * Matcher of calendar element for fixed constant value.
 * Unmodified object. Thread-safe.
 *
 * Difficulty:
 *  matching one value - O(1)
 *  find nearest value - O(1)
 * Used memory:
 *  4 bytes
 */
class ConstantMatcher implements DigitMatcher
{
    private final int value;

    public ConstantMatcher(int value)
    {
        this.value = value;
    }

    public boolean match(int value) {
        return this.value == value;
    }

    public boolean hasNext(int value) {
        return false;
    }

    public boolean hasPrev(int value) {
        return false;
    }

    public int getNext(int v) {
        return value;
    }

    public int getPrev(int v) {
        return value;
    }

    public boolean isAbove(int value) {
        return value > this.value;
    }

    public boolean isBelow(int value)
    {
        return value < this.value;
    }

    public int getLow() {
        return value;
    }

    public int getHigh() {
        return value;
    }
}
