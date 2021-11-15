package com.habr.cron;

import org.testng.annotations.Test;

import static com.habr.cron.ScheduleElements.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class MatcherPoolTest
{
    /*
     * Test for correctness of creation
     */
    @Test
    public void testCreationCorrectness() throws Exception
    {
        ScheduleModel model = new ScheduleModel();
        model.setModelFor(YEAR, new RangeList(new Range(2021, 2030))); // will be used IntervalMatcher
        model.setModelFor(MONTH, new RangeList(new Range(3, true))); // will be used SteppingMatcher
        model.setModelFor(SECONDS, new RangeList(new Range(32))); // will be used ConstantMatcher
        model.setModelFor(DAY_OF_WEEK, RangeList.ASTERISK); // will be used IntervalMatcher
        model.setModelFor(HOURS, new RangeList(new Range(10, 17, 3))); // will be used SteppingMatcher

        RangeList minutes = new RangeList(2);
        minutes.add(new Range(1, 5));
        minutes.add(new Range(9, 32));
        model.setModelFor(MINUTES, minutes); // will be used HashMapMatcher

        RangeList days = new RangeList(2);
        days.add(new Range(2, 7));
        days.add(new Range(28, 32));
        model.setModelFor(DAY_OF_MONTH, days);//will be used LastDayOfMonthProxy

        RangeList millis = new RangeList(4);
        millis.add(new Range(1, 10));
        millis.add(new Range(100, 200));
        millis.add(new Range(310, 390));
        millis.add(new Range(400));
        model.setModelFor(MILLIS, millis); // will be used BitMapMatcher

        model.initDefaults();
        model.check("--test--");
        model.fixup();

        MatcherPool pool = new MatcherPool(model);
        Class actual;

        actual = pool.getMatcherPool()[YEAR.ordinal()].getClass();
        assertEquals(actual, IntervalMatcher.class);

        actual = pool.getMatcherPool()[MONTH.ordinal()].getClass();
        assertEquals(actual, SteppingMatcher.class);

        actual = pool.getMatcherPool()[DAY_OF_MONTH.ordinal()].getClass();
        assertEquals(actual, HashMapMatcher.class);

        assertNull(pool.getMatcherPool()[DAY_OF_WEEK.ordinal()]);

        actual = pool.getMatcherPool()[HOURS.ordinal()].getClass();
        assertEquals(actual, SteppingMatcher.class);

        actual = pool.getMatcherPool()[MINUTES.ordinal()].getClass();
        assertEquals(actual, HashMapMatcher.class);

        actual = pool.getMatcherPool()[SECONDS.ordinal()].getClass();
        assertEquals(actual, ConstantMatcher.class);

        actual = pool.getMatcherPool()[MILLIS.ordinal()].getClass();
        assertEquals(actual, BitMapMatcher.class);



        model.setModelFor(DAY_OF_MONTH, new RangeList(new Range(32))); // will be used LastDayOfMonthMatcher
        pool = new MatcherPool(model);

        actual = pool.getMatcherPool()[DAY_OF_MONTH.ordinal()].getClass();
        assertEquals(actual, ConstantMatcher.class);
    }
}