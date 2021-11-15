package com.habr.cron;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class IntervalMatcherTest
{
    @Test(dataProvider = "matchDataProvider")
    public void testMatch(Range range, int value, boolean expected) throws Exception
    {
        IntervalMatcher matcher = new IntervalMatcher(range.min, range.max);
        boolean actual = matcher.match(value);
        assertEquals(actual, expected);
    }

    @DataProvider
    private Object[][] matchDataProvider()
    {
        return new Object[][] {
                {new Range(5, 63),    5,      true},
                {new Range(5, 63),    6,      true},
                {new Range(5, 63),   63,      true},
                {new Range(5, 63),    4,      false},
                {new Range(5, 63),   64,      false},
        };
    }

    @Test
    public void testGetMajor() throws Exception
    {
        IntervalMatcher matcher = new IntervalMatcher(1, 5);
        assertEquals(matcher.getNext(1), 2);
        assertEquals(matcher.getNext(5), 6);
        assertEquals(matcher.getNext(0), 1);
    }

    @Test
    public void testGetMinor() throws Exception
    {
        IntervalMatcher matcher = new IntervalMatcher(1, 5);
        assertEquals(matcher.getPrev(1), 0);
        assertEquals(matcher.getPrev(5), 4);
        assertEquals(matcher.getPrev(0), -1);
    }

    @Test
    public void testIsAbove() throws Exception
    {
        IntervalMatcher matcher = new IntervalMatcher(6, 8);
        assertTrue(matcher.isAbove(9));
        assertFalse(matcher.isAbove(8));
        assertFalse(matcher.isAbove(7));
        assertFalse(matcher.isAbove(6));
        assertFalse(matcher.isAbove(5));
    }

    @Test
    public void testIsBelow() throws Exception
    {
        IntervalMatcher matcher = new IntervalMatcher(6, 8);
        assertTrue(matcher.isBelow(5));
        assertFalse(matcher.isBelow(6));
        assertFalse(matcher.isBelow(7));
        assertFalse(matcher.isBelow(8));
        assertFalse(matcher.isBelow(9));
    }


    @Test
    public void testGetHigh() throws Exception
    {
        IntervalMatcher matcher = new IntervalMatcher(1, 30);
        assertEquals(matcher.getHigh(), 30);
    }

    @Test
    public void testGetLow() throws Exception
    {
        IntervalMatcher matcher = new IntervalMatcher(1, 30);
        assertEquals(matcher.getLow(), 1);
    }
}