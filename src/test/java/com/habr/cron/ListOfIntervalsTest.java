package com.habr.cron;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ListOfIntervalsTest
{
    private ListOfIntervalsMatcher list;

    @BeforeMethod
    public void setUp() throws Exception
    {
        // 1-10,12-18,21-30,41-50
        list = new ListOfIntervalsMatcher(1, 50, 4);
        list.addRange(1, 10, 1);
        list.addRange(12, 18, 1);
        list.addRange(21, 30, 1);
        list.addRange(41, 50, 1);

        list.finishRange();
    }

    @Test(dataProvider = "matchDP")
    public void testMatch(int value, boolean expected) throws Exception
    {
        boolean actual = list.match(value);
        assertEquals(actual, expected);
    }
    @DataProvider
    public Object[][] matchDP()
    {
        return new Object[][]
        {
                {0, false},
                {1, true},
                {2, true},
                {10, true},
                {11, false},
                {12, true},
                {13, true},
                {18, true},
                {19, false},
                {20, false},
                {21, true},
                {51, false},
                {100, false},
        };
    }

    @Test
    public void testBounds() throws Exception
    {
        assertEquals(list.getLow(), 1);
        assertEquals(list.getHigh(), 50);
    }

    @Test(dataProvider = "nextDP")
    public void testNext(int value, int expected) throws Exception
    {
        int actual = list.getNext(value);
        assertEquals(actual, expected);
    }
    @DataProvider
    public Object[][] nextDP()
    {
        return new Object[][]{
                {-10, 1},
                {0, 1},
                {1, 2},
                {2, 3},
                {9, 10},
                {10, 12},
                {12, 13},
                {17, 18},
                {18, 21},
                {27, 28},
                {29, 30},
                {30, 41},
                {49, 50},
                {50, 51},
        };
    }

    @Test(dataProvider = "prevDP")
    public void testPrev(int value, int expected) throws Exception
    {
        int actual = list.getPrev(value);
        assertEquals(actual, expected);
    }
    @DataProvider
    public Object[][] prevDP()
    {
        // 1-10,12-18,21-30,41-50
        return new Object[][]{
                {-10, -11},
                {60, 50},
                {50, 49},
                {49, 48},
                {42, 41},
                {41, 30},
                {30, 29},
                {22, 21},
                {21, 18},
                {18, 17},
                {12, 10},
                {10, 9},
                {9, 8},
                {2, 1},
                {1, 0},
                {0, -1},
        };
    }

    @Test
    public void testSteppedRanges() throws Exception
    {
        // 1-10,12-18,21-30,41-60
        ListOfIntervalsMatcher list = new ListOfIntervalsMatcher(1, 60, 4);
        list.addRange(1, 10, 1);
        list.addRange(12, 18, 1);
        list.addRange(21, 30, 1);
        list.addRange(41, 60, 1);
        list.finishRange();

        assertEquals(list.getNext(0), 1);
        assertEquals(list.getNext(1), 2);
        assertEquals(list.getNext(2), 3);
        assertEquals(list.getNext(9), 10);
        assertEquals(list.getNext(10), 12);
        assertEquals(list.getNext(18), 21);
        assertEquals(list.getNext(21), 22);
        assertEquals(list.getNext(25), 26);
        assertEquals(list.getNext(29), 30);
        assertEquals(list.getNext(30), 41);
        assertEquals(list.getNext(60), 61); //out of bounds

        assertEquals(list.getPrev(70), 60);
        assertEquals(list.getPrev(60), 59);
        assertEquals(list.getPrev(56), 55);
        assertEquals(list.getPrev(42), 41);
        assertEquals(list.getPrev(41), 30);
        assertEquals(list.getPrev(21), 18);
        assertEquals(list.getPrev(10), 9);
        assertEquals(list.getPrev(11), 10);
        assertEquals(list.getPrev(12), 10);
        assertEquals(list.getPrev(1), 0); // out of bounds

        assertTrue(list.match(1));
        assertTrue(list.match(3));
        assertTrue(list.match(15));
        assertTrue(list.match(29));
        assertTrue(list.match(56));

        assertFalse(list.match(0));
        assertTrue(list.match(2));
        assertTrue(list.match(4));
        assertTrue(list.match(30));
        assertTrue(list.match(60));
        assertFalse(list.match(61));
    }

    @Test
    public void testSpecialCase() throws Exception
    {
        ListOfIntervalsMatcher matcher = new ListOfIntervalsMatcher(1, 10, 1);
        matcher.addRange(5, 5, 1);
        matcher.finishRange();

        assertTrue(matcher.match(5));
        assertFalse(matcher.match(4));
        assertFalse(matcher.match(6));

        assertEquals(matcher.getNext(3), 5);
        assertTrue(matcher.hasNext(3));

        assertEquals(matcher.getNext(4), 5);
        assertTrue(matcher.hasNext(4));

        assertEquals(matcher.getNext(5), 6);
        assertFalse(matcher.hasNext(5));

        assertEquals(matcher.getNext(6), 7);
        assertFalse(matcher.hasNext(6));



        matcher = new ListOfIntervalsMatcher(5, 5, 1);
        matcher.addRange(5, 5, 1);
        matcher.finishRange();

        assertTrue(matcher.match(5));
        assertFalse(matcher.match(4));
        assertFalse(matcher.match(6));

        assertEquals(matcher.getNext(3), 5);
        assertTrue(matcher.hasNext(3));

        assertEquals(matcher.getNext(4), 5);
        assertTrue(matcher.hasNext(4));

        assertEquals(matcher.getNext(5), 6);
        assertFalse(matcher.hasNext(5));

        assertEquals(matcher.getNext(6), 7);
        assertFalse(matcher.hasNext(6));
    }
}
