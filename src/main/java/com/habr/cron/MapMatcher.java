package com.habr.cron;

/**
 * Common interface of all map's matchers: HashMap, TreeMap, BitMap
 */
interface MapMatcher
{
    /**
     * Adds a values range in schedule map.
     * Ranges can overlap without problems.
     *
     * @param from begin value (include)
     * @param to end value (include)
     * @param step between values
     */
    void addRange(int from, int to, int step);

    /**
     * Utility function for complete initialization of map.
     */
    void finishRange();

    /**
     * Adds a single value to the bit map.
     *
     * @param value to add in
     */
    void addValue(int value);
}
