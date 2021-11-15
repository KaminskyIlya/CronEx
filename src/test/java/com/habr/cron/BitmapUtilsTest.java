package com.habr.cron;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class BitmapUtilsTest
{
    @Test(dataProvider = "forwardScanBit_DataProvider")
    public void testForwardScanBit(long map, int offset, int expectedPos) throws Exception
    {
        int actual = BitmapUtils.forwardScanBit(map, offset);
        assertEquals(actual, expectedPos);
    }

    @DataProvider
    private Object[][] forwardScanBit_DataProvider()
    {
        return new Object[][] {
            // map,     offset, expected pos
            {0x0000L,   -1,         64},
            {0x0000L,   10,         64},
            {0x7148L,   -1,         3},
            {0x7148L,   0,          3},
            {0x7148L,   1,          3},
            {0x7148L,   2,          3},
            {0x7148L,   3,          3},
            {0x7148L,   4,          6},
            {0x7148L,   5,          6},
            {0x7148L,   6,          6},
            {0x7148L,   7,          8},
            {0x7148L,   8,          8},
            {0x7148L,   9,          12},
            {0x7148L,   10,         12},
            {0x7148L,   11,         12},
            {0x7148L,   12,         12},
            {0x7148L,   13,         13},
            {0x7148L,   14,         14},
            {0x7148L,   15,         64},
            {0x7148L,   63,         64},
            {0x7148L,   64,         64},
            {0x7148L,   123,        64},
            {0x8000000000007148L,   23,         63},
        };
    }



    @Test(dataProvider = "backwardScanBit_DataProvider")
    public void testBackwardScanBit(long map, int offset, int expectedPos) throws Exception
    {
        int actual = BitmapUtils.backwardScanBit(map, offset);
        assertEquals(actual, expectedPos);
    }

    @DataProvider
    public Object[][] backwardScanBit_DataProvider() throws Exception
    {
        return new Object[][] {
            // map,     offset, expected pos
            {0x0001L,   0,           0},
            {0x0001L,   1,           0},
            {0x0001L,  -1,          -1},

            {0x7148L,   1000,        14},
            {0x7148L,   64,          14},
            {0x7148L,   16,          14},
            {0x7148L,   15,          14},

            {0x7148L,   14,          14},
            {0x7148L,   13,          13},
            {0x7148L,   12,          12},
            {0x7148L,   11,          8},
            {0x7148L,   10,          8},
            {0x7148L,   9,           8},
            {0x7148L,   8,           8},
            {0x7148L,   7,           6},
            {0x7148L,   6,           6},
            {0x7148L,   5,           3},
            {0x7148L,   4,           3},
            {0x7148L,   3,           3},
            {0x7148L,   2,           -1},
            {0x7148L,   1,           -1},
            {0x7148L,   0,           -1},
            {0x7148L,   -1,          -1},
            {0x7148L,   -1001,       -1},
            {0x0000L,   64,          -1},
            {0x0000L,   63,          -1},
            {-1L,       63,          63},
            {-1L,       64,          63},
        };
    }
}