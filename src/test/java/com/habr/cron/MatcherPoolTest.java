package com.habr.cron;

import org.testng.annotations.Test;

import static com.habr.cron.ScheduleElements.*;
import static org.testng.Assert.*;

public class MatcherPoolTest
{
    private ScheduleModel getDefaultModel()
    {
        ScheduleModel model = new ScheduleModel();
        model.setModelFor(HOURS, RangeList.ASTERISK);
        model.setModelFor(MINUTES, RangeList.ASTERISK);
        model.setModelFor(SECONDS, RangeList.ASTERISK);
        model.initDefaults();
        return model;
    }


    @Test
    public void testWeekMap() throws Exception
    {
        ScheduleModel model = getDefaultModel();
        MatcherPool pool;

        model.setModelFor(DAY_OF_WEEK, RangeList.ASTERISK);
        model.fixup();
        pool = new MatcherPool(model);
        assertEquals(pool.getWeekDaysMap().getMap(), DaysMap.FULL_MAP);


        model.setModelFor(DAY_OF_WEEK, new RangeList(new Range(0))); // Sunday
        model.fixup();
        pool = new MatcherPool(model);
        assertEquals(pool.getWeekDaysMap().getMap(), Byte.valueOf("0000001", 2).byteValue());


        model.setModelFor(DAY_OF_WEEK, new RangeList(new Range(2, true))); // even day
        model.fixup();
        pool = new MatcherPool(model);
        assertEquals(pool.getWeekDaysMap().getMap(), Byte.valueOf("1010101", 2).byteValue());


        model.setModelFor(DAY_OF_WEEK, new RangeList(new Range(3, 5))); // from Wednesday to Friday
        model.fixup();
        pool = new MatcherPool(model);
        assertEquals(pool.getWeekDaysMap().getMap(), Byte.valueOf("0111000", 2).byteValue());


        model.setModelFor(DAY_OF_WEEK, new RangeList(new Range(1, 5, 2))); // '1-5/2'
        model.fixup();
        pool = new MatcherPool(model);
        assertEquals(pool.getWeekDaysMap().getMap(), Byte.valueOf("0101010", 2).byteValue());
    }


    @Test
    public void testIsAnyWeekDay() throws Exception
    {
        ScheduleModel model = getDefaultModel();
        MatcherPool pool;


        model.setModelFor(DAY_OF_WEEK, RangeList.ASTERISK);
        model.fixup();
        pool = new MatcherPool(model);
        assertTrue(pool.isAnyWeekDay());


        model.setModelFor(DAY_OF_WEEK, new RangeList(new Range(0))); // Sunday
        model.fixup();
        pool = new MatcherPool(model);
        assertFalse(pool.isAnyWeekDay());
    }


    @Test
    public void testIsAnyDate() throws Exception
    {
        ScheduleModel model = getDefaultModel();
        MatcherPool pool;

        pool = new MatcherPool(model);
        assertTrue(pool.isAnyDate());

        model.setModelFor(MONTH, new RangeList(new Range(1, 3))); // First quarter of the any year
        pool = new MatcherPool(model);
        assertFalse(pool.isAnyDate());
    }


    @Test
    public void testMonthMap() throws Exception
    {
        ScheduleModel model = getDefaultModel();

        model.setModelFor(DAY_OF_WEEK, RangeList.ASTERISK);
        model.fixup();

        MatcherPool pool;


        // constant
        model.setModelFor(DAY_OF_MONTH, new RangeList(new Range(1))); // 1th
        pool = new MatcherPool(model);
        assertEquals(pool.getMonthDaysMap().getMap(), Byte.valueOf("0000001", 2).byteValue());


        // asterisk
        model.setModelFor(DAY_OF_MONTH, RangeList.ASTERISK); // every 3 days
        pool = new MatcherPool(model);
        assertEquals(pool.getMonthDaysMap().getMap(), DaysMap.FULL_MAP);


        // asterisk with step
        model.setModelFor(DAY_OF_MONTH, new RangeList(new Range(3, true))); // every 3 days
        pool = new MatcherPool(model);
        assertEquals(pool.getMonthDaysMap().getMap(), DaysMap.FULL_MAP);

        // asterisk with step
        model.setModelFor(DAY_OF_MONTH, new RangeList(new Range(7, true))); // every 7 days
        pool = new MatcherPool(model);
        assertEquals(pool.getMonthDaysMap().getMap(), Byte.valueOf("0000001", 2).byteValue());


        // range with step
        model.setModelFor(DAY_OF_MONTH, new RangeList(new Range(2, 31, 7))); // every 7 days
        pool = new MatcherPool(model);
        assertEquals(pool.getMonthDaysMap().getMap(), Byte.valueOf("0000010", 2).byteValue());


        // interval without step
        model.setModelFor(DAY_OF_MONTH, new RangeList(new Range(1, 4))); // first four days
        pool = new MatcherPool(model);
        assertEquals(pool.getMonthDaysMap().getMap(), Byte.valueOf("0001111", 2).byteValue());


        // last day of month
        model.setModelFor(DAY_OF_MONTH, new RangeList(new Range(LAST_DAY_OF_MONTH_CODE))); // 28-31
        pool = new MatcherPool(model);
        assertEquals(pool.getMonthDaysMap().getMap(), Byte.valueOf("1000111", 2).byteValue());


        // by last day
        model.setModelFor(DAY_OF_MONTH, new RangeList(new Range(27, LAST_DAY_OF_MONTH_CODE))); // 27-31
        pool = new MatcherPool(model);
        assertEquals(pool.getMonthDaysMap().getMap(), Byte.valueOf("1100111", 2).byteValue());


        // by last day with step
        model.setModelFor(DAY_OF_MONTH, new RangeList(new Range(27, LAST_DAY_OF_MONTH_CODE, 2))); // 27-31/2
        pool = new MatcherPool(model);
        assertEquals(pool.getMonthDaysMap().getMap(), Byte.valueOf("0100101", 2).byteValue());
    }
}