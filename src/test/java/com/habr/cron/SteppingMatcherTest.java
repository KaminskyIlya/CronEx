package com.habr.cron;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class SteppingMatcherTest
{

    @Test(dataProvider = "matchDataProvider")
    public void testMatch(Range range, int value, boolean expected) throws Exception
    {
        SteppingMatcher matcher = new SteppingMatcher(range.min, range.max, range.step);
        boolean actual = matcher.match(value);
        assertEquals(actual, expected);
    }

    @DataProvider
    private Object[][] matchDataProvider()
    {
        return new Object[][] {
            {new Range(5, 63, 3),   5,      true},
            {new Range(5, 63, 3),   6,      false},
            {new Range(5, 63, 3),   7,      false},
            {new Range(5, 63, 3),   8,      true},
            {new Range(5, 63, 3),   9,      false},
            {new Range(5, 63, 3),  10,      false},
            {new Range(5, 63, 3),  11,      true},
            {new Range(5, 63, 3),  62,      true},
            {new Range(5, 63, 3),  63,      false},
            {new Range(5, 63, 3),  64,      false},
        };
    }

    @Test(dataProvider = "nextDataProvider")
    public void testGetNext(Range range, int value, int expected) throws Exception
    {
        SteppingMatcher matcher = new SteppingMatcher(range.min, range.max, range.step);
        int next = matcher.getNext(value);
        assertEquals(next, expected);
    }

    @DataProvider
    private Object[][] nextDataProvider()
    {
        return new Object[][] {
            {new Range(5, 63, 3),   5,      8},
            {new Range(5, 63, 3),   6,      8},
            {new Range(5, 63, 3),   7,      8},
            {new Range(5, 63, 3),   8,      11},
            {new Range(5, 63, 3),   9,      11},
            {new Range(5, 63, 3),   10,     11},
            {new Range(5, 63, 3),   63,     65}, // correct for this method (although overflowed)
            {new Range(5, 63, 3),   65,     68}, // correct for this method (although overflowed)
        };
    }

    @Test(dataProvider = "prevDataProvider")
    public void testGetPrev(Range range, int value, int expected) throws Exception
    {
        SteppingMatcher matcher = new SteppingMatcher(range.min, range.max, range.step);
        int next = matcher.getPrev(value);
        assertEquals(next, expected);
    }

    @DataProvider
    private Object[][] prevDataProvider()
    {
        return new Object[][] {
            {new Range(5, 63, 3),   5,      2},
            {new Range(5, 63, 3),   6,      5},
            {new Range(5, 63, 3),   7,      5},
            {new Range(5, 63, 3),   8,      5},
            {new Range(5, 63, 3),   9,      8},
            {new Range(5, 63, 3),   10,     8},
            {new Range(5, 63, 3),   11,     8},
            {new Range(5, 63, 3),   12,     11},
            {new Range(5, 63, 3),   13,     11},
            {new Range(5, 63, 3),   14,     11},
            {new Range(5, 63, 3),   15,     14},
            {new Range(5, 63, 3),   63,     62},
            {new Range(5, 63, 3),   64,     62}, // not happens in production code
            {new Range(5, 63, 3),   65,     62}, // not happens in production code
            {new Range(5, 63, 3),   66,     65}, // not happens in production code
        };
    }

    @Test
    public void testIsAbove() throws Exception
    {
        SteppingMatcher matcher = new SteppingMatcher(1, 30, 2);
        assertTrue(matcher.isAbove(30));
        assertTrue(matcher.isAbove(31));
        assertFalse(matcher.isAbove(29));
        assertFalse(matcher.isAbove(28));
    }

    @Test
    public void testIsBelow() throws Exception
    {
        SteppingMatcher matcher = new SteppingMatcher(1, 30, 2);
        assertTrue(matcher.isBelow(0));
        assertFalse(matcher.isBelow(1));
        assertFalse(matcher.isBelow(2));
    }

    @Test
    public void testHasNext() throws Exception
    {
        SteppingMatcher matcher = new SteppingMatcher(1, 30, 2);
        assertTrue(matcher.hasNext(28));
        assertFalse(matcher.hasNext(29));
    }

    @Test
    public void testHasPrev() throws Exception
    {
        SteppingMatcher matcher = new SteppingMatcher(1, 30, 2);
        assertTrue(matcher.hasPrev(2));
        assertFalse(matcher.hasPrev(1));
    }

    @Test
    public void testGetHigh() throws Exception
    {
        SteppingMatcher matcher = new SteppingMatcher(1, 30, 2);
        assertEquals(matcher.getHigh(), 29);
    }

    @Test
    public void testGetLow() throws Exception
    {
        SteppingMatcher matcher = new SteppingMatcher(1, 30, 2);
        assertEquals(matcher.getLow(), 1);
    }
}