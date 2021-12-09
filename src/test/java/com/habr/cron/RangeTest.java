package com.habr.cron;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;

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


    @Test
    public void testCompareTo() throws Exception
    {
        assertEquals(new Range(1, 7).compareTo(new Range(9, 10)), -1); // full in left
        assertEquals(new Range(9, 10).compareTo(new Range(1, 7)), 1); // full in right

        assertEquals(new Range(1, 7).compareTo(new Range(7, 10)), -1); // full left
        assertEquals(new Range(7, 10).compareTo(new Range(1, 7)), 1); // full in right

        assertEquals(new Range(1, 7).compareTo(new Range(5, 10)), -1); // partial left
        assertEquals(new Range(5, 10).compareTo(new Range(1, 7)),  1); // partial right

        assertEquals(new Range(1, 7).compareTo(new Range(3, 5)), -1); // contains
        assertEquals(new Range(3, 5).compareTo(new Range(1, 7)),  1); // included

        assertEquals(new Range(1, 7).compareTo(new Range(1, 5)), 0); // contain
        assertEquals(new Range(1, 7).compareTo(new Range(5, 7)), -1); // contain

        assertEquals(new Range(1, 5).compareTo(new Range(1, 7)), 0); // included
        assertEquals(new Range(5, 7).compareTo(new Range(1, 7)), 1); // included

        assertEquals(new Range(1, 5).compareTo(new Range(1, 5)), 0); // equals

        assertEquals(new Range(31, 39).compareTo(new Range(30, 40)), 1);
    }

    @Test
    public void testSortRanges() throws Exception
    {
        Range list[] = new Range[]{
                new Range(31, 39),
                new Range(5, 15),
                new Range(30, 40, 2),
                new Range(4, 5),
                new Range(50),
                new Range(8, 21),
        };
        Arrays.sort(list);

        assertEquals(list[0].min, 4);
        assertEquals(list[0].max, 5);

        assertEquals(list[1].min, 5);
        assertEquals(list[1].max, 15);

        assertEquals(list[2].min, 8);
        assertEquals(list[2].max, 21);

        assertEquals(list[3].min, 30);
        assertEquals(list[3].max, 40);

        assertEquals(list[4].min, 31);
        assertEquals(list[4].max, 39);

        assertEquals(list[5].min, 50);
        assertEquals(list[5].max, 50);
    }


    @Test(dataProvider = "intersects_DataProvider")
    public void testIntersects(Range a, Range b, boolean expected) throws Exception
    {
        assertEquals(a.isIntersects(b), expected);
    }
    @DataProvider
    private Object[][] intersects_DataProvider()
    {
        return new Object[][]{
                {new Range(1, 10), new Range(9, 11), true}, // on right
                {new Range(1, 10), new Range(10, 11), true},
                {new Range(1, 10), new Range(9, 10), true},
                {new Range(1, 10), new Range(10, 10), true},
                {new Range(1, 10), new Range(1, 12), true},
                {new Range(1, 10), new Range(11, 12), false}, // full in right

                {new Range(20, 30), new Range(10, 20), true}, // on left
                {new Range(20, 30), new Range(20, 20), true},
                {new Range(20, 30), new Range(10, 21), true},
                {new Range(20, 30), new Range(10, 30), true},
                {new Range(20, 30), new Range(10, 19), false}, // full in left

                {new Range(20, 30), new Range(20, 21), true}, // inside
                {new Range(20, 30), new Range(20, 30), true},
                {new Range(20, 30), new Range(21, 29), true},
                {new Range(20, 30), new Range(30, 30), true},
                {new Range(20, 30), new Range(20, 20), true},
                {new Range(20, 30), new Range(10, 40), true}, // outside
        };
    }


    @Test(dataProvider = "merge_DataProvider")
    public void testMerge(Range a, Range b, int min, int max) throws Exception
    {
        Range left = new Range(a.min, a.max);
        Range right = new Range(b.min, b.max);

        left.merge(right);
        assertEquals(left.min, min);
        assertEquals(left.max, max);

        // inverses merge operation

        left = new Range(b.min, b.max);
        right = new Range(a.min, a.max);

        left.merge(right);
        assertEquals(left.min, min);
        assertEquals(left.max, max);
    }
    @DataProvider
    private Object[][] merge_DataProvider()
    {
        return new Object[][]{
                { new Range(1, 10), new Range(9, 20), 1, 20 }, //right
                { new Range(1, 10), new Range(2, 8), 1, 10 }, // inside
                { new Range(10, 40), new Range(1, 10), 1, 40 }, // left
                { new Range(10, 40), new Range(1, 50), 1, 50 }, // includes
        };
    }

    @Test(expectedExceptions = AssertionError.class, dataProvider = "mergeAssert_DataProvider")
    public void testMergeNoIntersectsRanges_MustThrownAssertionError(Range left, Range right) throws Exception
    {
        left.merge(right);
    }
    @DataProvider
    private Object[][] mergeAssert_DataProvider()
    {
        return new Object[][]{
                { new Range(10, 40), new Range(50, 60) },
                { new Range(50, 60), new Range(10, 40) },
        };
    }

}