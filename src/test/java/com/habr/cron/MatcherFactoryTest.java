package com.habr.cron;

import org.testng.annotations.Test;

import static com.habr.cron.ScheduleElements.*;
import static org.testng.Assert.assertEquals;

public class MatcherFactoryTest
{
    @Test
    public void testCreateInstance() throws Exception
    {
        DigitMatcher matcher;
        RangeList ranges;

        ranges = new RangeList(new Range(2021, 2030));
        matcher = MatcherFactory.createInstance(ranges, YEAR);
        assertEquals(matcher.getClass(), IntervalMatcher.class);

        ranges = new RangeList(new Range(3, true));
        matcher = MatcherFactory.createInstance(ranges, MONTH);
        assertEquals(matcher.getClass(), SteppingMatcher.class);

        ranges = new RangeList(new Range(30));
        matcher = MatcherFactory.createInstance(ranges, SECONDS);
        assertEquals(matcher.getClass(), ConstantMatcher.class);

        ranges = RangeList.ASTERISK;
        matcher = MatcherFactory.createInstance(ranges, HOURS);
        assertEquals(matcher.getClass(), IntervalMatcher.class);

        ranges = new RangeList(Range.ASTERISK);
        matcher = MatcherFactory.createInstance(ranges, MINUTES);
        assertEquals(matcher.getClass(), IntervalMatcher.class);

        ranges = new RangeList(new Range(3, true));
        matcher = MatcherFactory.createInstance(ranges, MILLIS);
        assertEquals(matcher.getClass(), SteppingMatcher.class);

        ranges = new RangeList(2);
        ranges.add(new Range(1, 5));
        ranges.add(new Range(9, 32));
        matcher = MatcherFactory.createInstance(ranges, HOURS);
        assertEquals(matcher.getClass(), ArrayMatcher.class);

        ranges = new RangeList(2);
        ranges.add(new Range(100, 101));
        ranges.add(new Range(150, 151));
        matcher = MatcherFactory.createInstance(ranges, MILLIS);
        assertEquals(matcher.getClass(), ArrayMatcher.class);

        ranges = new RangeList(2);
        ranges.add(new Range(0, 20));
        ranges.add(new Range(30, 63));
        matcher = MatcherFactory.createInstance(ranges, MILLIS);
        assertEquals(matcher.getClass(), ArrayMatcher.class);

        ranges = new RangeList(2);
        ranges.add(new Range(900, 920));
        ranges.add(new Range(930, 963));
        matcher = MatcherFactory.createInstance(ranges, MILLIS);
        assertEquals(matcher.getClass(), ArrayMatcher.class);

        ranges = new RangeList(2);
        ranges.add(new Range(100, 200));
        ranges.add(new Range(150, 300));
        matcher = MatcherFactory.createInstance(ranges, MILLIS);
        assertEquals(matcher.getClass(), IntervalMatcher.class);

        // 1,3,5,7,9,11,13,15,170-180,190-999/3
        ranges = new RangeList(12);
        ranges.add(new Range(1));
        ranges.add(new Range(3));
        ranges.add(new Range(5));
        ranges.add(new Range(7));
        ranges.add(new Range(9));
        ranges.add(new Range(11));
        ranges.add(new Range(13));
        ranges.add(new Range(15));
        ranges.add(new Range(170, 180));
        ranges.add(new Range(190, 899, 3));
        ranges.add(new Range(910, 920, 2));
        ranges.add(new Range(950, 999, 3));
        matcher = MatcherFactory.createInstance(ranges, MILLIS);
        assertEquals(matcher.getClass(), BitMapMatcher.class);

        ranges = new RangeList(2);
        ranges.add(new Range(1, 2, 1));
        ranges.add(new Range(990, 999, 1));
        matcher = MatcherFactory.createInstance(ranges, MILLIS);
        assertEquals(matcher.getClass(), ListOfIntervalsMatcher.class);

        ranges = new RangeList(2);
        ranges.add(new Range(100, 200, 3));
        ranges.add(new Range(250, 300, 4));
        matcher = MatcherFactory.createInstance(ranges, MILLIS);
        assertEquals(matcher.getClass(), ListOfRangesMatcher.class);

        ranges = new RangeList(3);
        ranges.add(new Range(900, 950, 2));
        ranges.add(new Range(250, 300, 4));
        ranges.add(new Range(100, 200, 3));
        matcher = MatcherFactory.createInstance(ranges, MILLIS);
        assertEquals(matcher.getClass(), ListOfRangesMatcher.class);

        ranges = new RangeList(3);
        ranges.add(new Range(900, 950, 2));
        ranges.add(new Range(250, 300, 4));
        ranges.add(new Range(910, 920, 3));
        matcher = MatcherFactory.createInstance(ranges, MILLIS);
        assertEquals(matcher.getClass(), BitMapMatcher.class); // because contains overlapped ranges with different steps
/*
        ranges = new RangeList(2);
        ranges.add(new Range(100, 200, 3)); // this ranges was merged
        ranges.add(new Range(151, 300, 3));
        matcher = MatcherFactory.createInstance(ranges, MILLIS);
        assertEquals(matcher.getClass(), ListOfRangesMatcher.class); //TODO: for future realization
*/

    }
}