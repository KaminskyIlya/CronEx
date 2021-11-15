package com.habr.cron;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


import static org.testng.Assert.*;

/**
 * Test for HashMapMatcher
 */
public class HashMapMatcherTest
{
    private HashMapMatcher matcher;

    @BeforeMethod
    public void setUp() throws Exception
    {
        // Setup schedule as "1,2,4,7-17/3,20-24/5,27"
        matcher = new HashMapMatcher(1, 27); // IMPORTANT: 1 - minimal available value
        matcher.addRange(1,2,1);    // 1-2/1 = 1,2
        matcher.addRange(4,4,1);    // 4
        matcher.addRange(7,17,3);   // 7-17/3 = 7,10,13,16
        matcher.addRange(20,24,5);  // 20-24/5 = 20
        matcher.addRange(27,27,1);  // 27
        matcher.finishRange();
    }

    @Test
    public void testAddValue() throws Exception
    {
        HashMapMatcher matcher = new HashMapMatcher(1, 5);
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
    public void testGetMajor(int value, int expected) throws Exception
    {
        int actual = matcher.getNext(value);
        assertEquals(actual, expected, "HashMapMatcher return wrong next value.");
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
                {27, 127},
        };
    }



    @Test(dataProvider = "getPrevDataProvider")
    public void testGetMinor(int value, int expected) throws Exception
    {
        int actual = matcher.getPrev(value);
        assertEquals(actual, expected, "HashMapMatcher return wrong prev value.");
    }

    @DataProvider
    public Object[][] getPrevDataProvider()
    {
        // value, expected result
        return new Object[][]{
                {1, -1},
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
        HashMapMatcher matcher = new HashMapMatcher(1, 30);
        matcher.addRange(1, 5, 1);
        matcher.addRange(20, 29, 2);
        matcher.finishRange();
        assertEquals(matcher.getHigh(), 28);


        matcher = new HashMapMatcher(1, 31);
        matcher.addRange(1, 5, 1);
        matcher.addRange(21, 31, 2);
        matcher.finishRange();
        assertEquals(matcher.getHigh(), 31);
    }

    @Test
    public void testGetLow() throws Exception
    {
        HashMapMatcher matcher = new HashMapMatcher(1, 31);
        matcher.addRange(2, 5, 1);
        matcher.addRange(21, 31, 2);
        matcher.finishRange();
        assertEquals(matcher.getLow(), 2);
    }
}