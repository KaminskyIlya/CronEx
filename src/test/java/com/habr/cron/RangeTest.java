package com.habr.cron;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class RangeTest {

    @Test
    public void testIsAsterisk() throws Exception
    {
        assertEquals(true, Range.ASTERISK.isAsterisk());

        Range range = new Range();
        assertEquals(true, range.isAsterisk());
    }

    @Test
    public void testIsStepped() throws Exception
    {
        Range range = new Range(2, false);
        assertEquals(true, range.isStepped());

        range = new Range(1, 10, 2);
        assertEquals(true, range.isStepped());

        range = new Range(1, 10, 1);
        assertEquals(false, range.isStepped());
    }

    @Test
    public void testGetValue() throws Exception
    {
        Range range = new Range(50);
        assertEquals(50, range.getValue());
    }

    @Test(expectedExceptions = AssertionError.class)
    public void getValueMustException() throws Exception
    {
        Range range = new Range(1, 50, 10);
        assertEquals(50, range.getValue());
    }

    @Test
    public void testIsConstant() throws Exception
    {
        Range range = new Range(2, 2, 1);
        assertEquals(true, range.isConstant());

        range = new Range(2, 2, 10);
        assertEquals(true, range.isConstant());

        range = new Range(2, 5, 1);
        assertEquals(false, range.isConstant());

        range = new Range(2, 5, 2);
        assertEquals(false, range.isConstant());
    }

    @Test
    public void testIsLastDay() throws Exception
    {
        Range range = new Range(ScheduleElements.LAST_DAY_OF_MONTH_CODE);
        assertEquals(true, range.isLastDay());
    }

    @Test
    public void testIsLeapDay() throws Exception
    {
        Range range = new Range(ScheduleElements.FEBRUARY_LEAP_DAY);
        assertEquals(true, range.isLeapDay());
    }

    @Test
    public void testIsFebruary() throws Exception
    {
        Range range = new Range(2);
        assertEquals(true, range.isFebruary());
    }

    @Test
    public void testIsByLastDay() throws Exception
    {
        Range range = new Range(1, ScheduleElements.LAST_DAY_OF_MONTH_CODE, 3);
        assertEquals(true, range.isByLastDay());
    }

    @Test
    public void testShiftBy() throws Exception
    {
        Range range = new Range(1, 2, 3);
        range.shiftBy(2);
        assertEquals(3, range.min);
        assertEquals(4, range.max);
    }

    @Test(dataProvider = "toString_dataProvider")
    public void testToString(Range range, String expected) throws Exception
    {
        assertEquals(expected, range.toString());
    }
    @DataProvider
    private Object[][] toString_dataProvider()
    {
        return new Object[][] {
                {Range.ASTERISK, "*"},
                {new Range(3, false), "*/3"},
                {new Range(3), "3"},
                {new Range(1, 3), "1-3"},
                {new Range(1, 10, 2), "1-10/2"},
        };
    }
}