package com.habr.cron;

/**
 * Helper class.
 * Help to select the best map matcher.
 */
class MatcherFactory
{
    /**
     * Help to select the best matcher for specified ranges list.
     * Constructs and initializes matcher.
     *
     * @param ranges source ranges for initialize
     * @param element schedule element for which the matcher is being created
     * @return the best instance that uses the source ranges.
     */
    public static DigitMatcher createInstance(RangeList ranges, ScheduleElements element)
    {
        return ranges.isAlone() ?
                    createSimpleMatcher(ranges.getSingle(), element)
                :
                    createMapMatcher(ranges, element);
    }



    private static DigitMatcher createSimpleMatcher(Range range, ScheduleElements element)
    {
        if ( range.isAsterisk() ) // * or */n
            return !range.isStepped() ?
                    new IntervalMatcher(element.min, element.max)
                    :
                    new SteppingMatcher(element.min, element.max, range.step);

        if ( range.isConstant() ) // single const value
            return new ConstantMatcher(range.min);

        return range.isStepped() ? // interval
                new SteppingMatcher(range.min, range.max, range.step)
                :
                new IntervalMatcher(range.min, range.max);
    }



    private static DigitMatcher createMapMatcher(RangeList ranges, ScheduleElements element)
    {
        int min = ranges.getMinimum();
        int max = ranges.getMaximum();

        // for small ranges we can use simple hashMap
        if ( (max - min) < HashMapMatcher.RANGE_LIMIT )
        {
            HashMapMatcher hash = new HashMapMatcher(min, max);
            setRanges(hash, ranges);
            return hash;
        }

        // if ranges have complexity - we use bit map
        if ( !ranges.isSimpleRanges() )
        {
            BitMapMatcher bits = new BitMapMatcher(min, max);
            setRanges(bits, ranges);
            return bits;
        }

        // sorts ranges and combines overlapped
        ranges.optimize();

        // after optimize we can get only one range (for example: '10-20,15-30' = '10-30')
        if ( ranges.isAlone() )
            return createSimpleMatcher(ranges.getSingle(), element);


        //
        // Ok. Now we will choose one of two: bits or list.
        //
        BitMapMatcher bits = new BitMapMatcher(min, max);
        setRanges(bits, ranges);

        // When count of ranges over than 8 the bits map will always better
        if ( ranges.getCount() > 10 ) return bits;

        // different intervals with and without steps requires different algo
        DigitMatcher list = ranges.isSimpleIntervals() ?
                new ListOfIntervalsMatcher(min, max, ranges.getCount())
            :
                new ListOfRangesMatcher(min, max, ranges.getCount());

        setRanges((MapMatcher) list, ranges);

        return list;
    }


    private static void setRanges(MapMatcher matcher, RangeList ranges)
    {
        for (Range range : ranges)
        {
            matcher.addRange(range.min, range.max, range.step);
        }
        matcher.finishRange();
    }
}
