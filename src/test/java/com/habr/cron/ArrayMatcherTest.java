package com.habr.cron;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ArrayMatcherTest
{
    private ArrayMatcher matcher;

    @BeforeMethod
    public void setUp() throws Exception
    {
        // Setup schedule as "1,2,4,7-17/3,20-24/5,27"
        matcher = new ArrayMatcher(1, 27); // IMPORTANT: 1 - minimal available value
        matcher.addRange(1,2,1);    // 1-2/1 = 1,2
        matcher.addRange(4,4,1);    // 4
        matcher.addRange(7,17,3);   // 7-17/3 = 7,10,13,16
        matcher.addRange(20,24,5);  // 20-24/5 = 20
        matcher.addRange(27,27,1);  // 27
        matcher.finishRange();
    }

    @Test
    public void testIsCanApplied() throws Exception
    {
        assertTrue(ArrayMatcher.isCanApplied(1, 60));
        assertTrue(ArrayMatcher.isCanApplied(1, 64));
        assertTrue(ArrayMatcher.isCanApplied(100, 163));
        assertFalse(ArrayMatcher.isCanApplied(1, 65));
        assertTrue(ArrayMatcher.isCanApplied(900, 961));

    }

    @Test
    public void testAddValue() throws Exception
    {
        ArrayMatcher matcher = new ArrayMatcher(1, 5);
        matcher.addValue(4);
        matcher.finishRange();
        assertTrue(matcher.match(4));
        assertFalse(matcher.match(1));
        assertFalse(matcher.match(5));
    }

    @Test(dataProvider = "matchData")
    public void testMatch(int value, boolean expected) throws Exception
    {
        boolean actual = matcher.match(value);
        assertEquals(actual, expected);
    }
    @DataProvider(name = "matchData")
    public Object[][] matchDataProvider()
    {
        // value, expected result
        return new Object[][]{
                {1, true},
                {2, true},
                {3, false},
                {4, true},
                {7, true},
                {8, false},
                {10, true},
                {13, true},
                {16, true},
                {17, false},
                {19, false},
                {20, true},
                {21, false},
                {27, true},
        };
    }


    @Test(dataProvider = "getNextDataProvider")
    public void testGetNext(int value, int expected) throws Exception
    {
        int actual = matcher.getNext(value);
        assertEquals(actual, expected, "ArrayMatcher return wrong next value.");
    }

    @DataProvider
    public Object[][] getNextDataProvider()
    {
        // value, expected result
        return new Object[][]{
                {1, 2},
                {2, 4},
                {3, 4},
                {4, 7},
                {5, 7},
                {6, 7},
                {7,  10},
                {8,  10},
                {9,  10},
                {10, 13},
                {11, 13},
                {12, 13},
                {13, 16},
                {14, 16},
                {15, 16},
                {16, 20},
                {17, 20},
                {18, 20},
                {19, 20},
                {20, 27},
                {21, 27},
                {22, 27},
                {23, 27},
                {24, 27},
                {25, 27},
                {26, 27},
                {27, 127 + 1},
        };
    }



    @Test(dataProvider = "getPrevDataProvider")
    public void testGetPrev(int value, int expected) throws Exception
    {
        int actual = matcher.getPrev(value);
        assertEquals(actual, expected, "ArrayMatcher return wrong prev value.");
    }

    @DataProvider
    public Object[][] getPrevDataProvider()
    {
        // value, expected result
        return new Object[][]{
                {1, 1 - 128},
                {2, 1},
                {3, 2},
                {4, 2},
                {5, 4},
                {6, 4},
                {7,  4},
                {8,  7},
                {9,  7},
                {10, 7},
                {11, 10},
                {12, 10},
                {13, 10},
                {14, 13},
                {15, 13},
                {16, 13},
                {17, 16},
                {18, 16},
                {19, 16},
                {20, 16},
                {21, 20},
                {22, 20},
                {23, 20},
                {24, 20},
                {25, 20},
                {26, 20},
                {27, 20},
        };
    }

    @Test
    public void testGetHigh() throws Exception
    {
        ArrayMatcher matcher = new ArrayMatcher(1, 30);
        matcher.addRange(1, 5, 1);
        matcher.addRange(20, 29, 2);
        matcher.finishRange();
        assertEquals(matcher.getHigh(), 28);


        matcher = new ArrayMatcher(1, 31);
        matcher.addRange(1, 5, 1);
        matcher.addRange(21, 31, 2);
        matcher.finishRange();
        assertEquals(matcher.getHigh(), 31);
    }

    @Test
    public void testGetLow() throws Exception
    {
        ArrayMatcher matcher = new ArrayMatcher(1, 31);
        matcher.addRange(2, 5, 1);
        matcher.addRange(21, 31, 2);
        matcher.finishRange();
        assertEquals(matcher.getLow(), 2);
    }

    @Test
    public void testFullRange() throws Exception
    {
        ArrayMatcher matcher = new ArrayMatcher(1, 64);
        matcher.addRange(1, 20, 1);
        matcher.addRange(40, 64, 1);
        matcher.finishRange();

        assertTrue(matcher.match(1));
        assertTrue(matcher.match(20));
        assertFalse(matcher.match(21));
        assertFalse(matcher.match(39));
        assertTrue(matcher.match(40));
        assertTrue(matcher.match(64));

        assertEquals(matcher.getNext(1), 2);
        assertEquals(matcher.getNext(19), 20);
        assertEquals(matcher.getNext(20), 40);
        assertEquals(matcher.getNext(25), 40);
        assertEquals(matcher.getNext(45), 46);
        assertEquals(matcher.getNext(63), 64);
        assertEquals(matcher.getNext(64), (int)(Byte.MAX_VALUE + 1));

        assertEquals(matcher.getPrev(64), 63);
        assertEquals(matcher.getPrev(63), 62);
        assertEquals(matcher.getPrev(40), 20);
        assertEquals(matcher.getPrev(20), 19);
        assertEquals(matcher.getPrev(2), 1);
        assertEquals(matcher.getPrev(1), (int)(Byte.MIN_VALUE + 1));
    }
}