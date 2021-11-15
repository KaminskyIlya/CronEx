package com.habr.cron;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Iterator;

import static org.testng.Assert.*;

public class RangeListTest
{
    RangeList list;
    private final RangeList single = new RangeList(new Range(1));

    private final int LIST_SIZE = 5;

    @BeforeMethod
    public void setUp() throws Exception
    {
        list = new RangeList(LIST_SIZE);
        list.add(new Range(2, 6));
        list.add(new Range(10, 14));
        list.add(new Range(47, 51));
        list.add(new Range(20, 24));
        list.add(new Range(30, 34));
    }

    @Test
    public void testIsAlone() throws Exception
    {
        assertTrue(single.isAlone());
        assertTrue(RangeList.ASTERISK.isAlone());
        assertTrue(new RangeList(Range.ASTERISK).isAlone());
        assertFalse(list.isAlone());
    }

    @Test
    public void testIsList() throws Exception
    {
        assertTrue(list.isList());
        assertFalse(RangeList.ASTERISK.isList());
        assertFalse(single.isList());
    }

    @Test
    public void testGetSingle() throws Exception
    {
        assertNotNull(single.getSingle());
    }

    @Test(expectedExceptions = AssertionError.class)
    public void testGetSingleForList_MustAssertError()
    {
        assertNotNull(list.getSingle());
    }



    @Test
    public void testGetMinimum() throws Exception
    {
        assertEquals(2, list.getMinimum());
        assertEquals(1, single.getMinimum());
    }
    @Test(expectedExceptions = AssertionError.class)
    public void getMinimumForAsterisk_MustAssertError()
    {
        RangeList.ASTERISK.getMinimum();
    }
    @Test(expectedExceptions = AssertionError.class)
    public void getMinimumForSingleAsterisk_MustAssertError()
    {
        RangeList ranges = new RangeList(Range.ASTERISK);
        ranges.getMinimum(); // assert where
    }



    @Test
    public void testGetMaximum() throws Exception
    {
        assertEquals(51, list.getMaximum());
        assertEquals(1, single.getMaximum());
    }

    @Test(expectedExceptions = AssertionError.class)
    public void getMaximumForAsterisk_MustAssertionError()
    {
        RangeList.ASTERISK.getMaximum();
    }

    @Test(expectedExceptions = AssertionError.class)
    public void getMaximumForSingleAsterisk_MustAssertionError()
    {
        RangeList ranges = new RangeList(Range.ASTERISK);
        ranges.getMaximum(); // assert where
    }


    @Test
    public void testShiftBy() throws Exception
    {
        RangeList ranges = new RangeList(5);
        ranges.add(Range.ASTERISK);         // '*'
        ranges.add(new Range(2, true));     // '*/2'
        ranges.add(new Range(3));           // '3'
        ranges.add(new Range(7, 8));        // '7-8'
        ranges.add(new Range(10, 20, 3));   // '10-20/3'

        ranges.shiftBy(1); // test this function


        Iterator<Range> iterator = ranges.iterator();
        Range tester;

        tester = iterator.next();
        assertEquals(tester.min, -1); // asterisk was not change
        assertEquals(tester.max, -1);

        tester = iterator.next();
        assertEquals(tester.min, -1); // asterisk range was not change
        assertEquals(tester.max, -1);

        tester = iterator.next();
        assertEquals(tester.getValue(), 4); // constant was shifted by 1

        tester = iterator.next();
        assertEquals(tester.min, 8); // bounds of the range was shifted by 1
        assertEquals(tester.max, 9);

        tester = iterator.next();
        assertEquals(tester.min, 11); // stepped range was shifted by 1
        assertEquals(tester.max, 21);
    }

    @Test
    public void testToString() throws Exception
    {
        RangeList ranges = new RangeList(5);
        ranges.add(Range.ASTERISK);
        ranges.add(new Range(47, 51));
        ranges.add(new Range(3, true));
        ranges.add(new Range(20, 24, 2));
        ranges.add(new Range(30));

        String expect = "*,47-51,*/3,20-24/2,30";
        assertEquals(expect, ranges.toString());
    }




    @Test
    public void testIterator() throws Exception
    {
        Iterator<Range> iterator = list.iterator();
        for (int i = 0; i < LIST_SIZE; i++)
        {
            assertTrue(iterator.hasNext());
            assertNotNull(iterator.next());
        }
        assertFalse(iterator.hasNext());
    }

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void whenIteratorFinished_Next_MustThrownException() throws Exception
    {
        Iterator<Range> iterator = list.iterator();
        while ( iterator.hasNext() )
            iterator.next();
        iterator.next(); // thrown here
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testIteratorNotSupportRemove()
    {
        Iterator<Range> iterator = list.iterator();
        iterator.next();
        iterator.remove();
    }
}