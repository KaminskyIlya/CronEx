package com.habr.cron;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ConstantMatcherTest
{
    @Test
    public void testMatch() throws Exception
    {
        ConstantMatcher matcher = new ConstantMatcher(20);

        assertFalse(matcher.match(21));
        assertTrue(matcher.match(20));
    }


    @Test
    public void testGetNext() throws Exception
    {
        ConstantMatcher matcher = new ConstantMatcher(20);

        assertEquals(matcher.getNext(1), 20);
        assertEquals(matcher.getNext(19), 20);
        assertEquals(matcher.getNext(20), 20);
        assertEquals(matcher.getNext(22), 20);
    }

    @Test
    public void testHasNext() throws Exception
    {
        ConstantMatcher matcher = new ConstantMatcher(20);
        assertFalse(matcher.hasNext(19));
        assertFalse(matcher.hasNext(20));
        assertFalse(matcher.hasNext(21));
    }



    @Test
    public void testGetPrev() throws Exception
    {
        ConstantMatcher matcher = new ConstantMatcher(20);

        assertEquals(matcher.getPrev(19), 20);
        assertEquals(matcher.getPrev(20), 20);
        assertEquals(matcher.getPrev(21), 20);
        assertEquals(matcher.getPrev(22), 20);
    }

    @Test
    public void testHasPrev() throws Exception
    {
        ConstantMatcher matcher = new ConstantMatcher(20);
        assertFalse(matcher.hasPrev(19));
        assertFalse(matcher.hasPrev(20));
        assertFalse(matcher.hasPrev(21));
    }


    @Test
    public void testIsAbove() throws Exception
    {
        ConstantMatcher matcher = new ConstantMatcher(20);

        assertTrue(matcher.isAbove(21));
        assertFalse(matcher.isAbove(20));
        assertFalse(matcher.isAbove(19));
    }


    @Test
    public void testIsBelow() throws Exception
    {
        ConstantMatcher matcher = new ConstantMatcher(20);

        assertTrue(matcher.isBelow(19));
        assertFalse(matcher.isBelow(20));
        assertFalse(matcher.isBelow(21));
    }


    @Test
    public void testGetLow() throws Exception
    {
        ConstantMatcher matcher = new ConstantMatcher(20);
        assertEquals(matcher.getLow(), 20);
    }

    @Test
    public void testGetHigh() throws Exception
    {
        ConstantMatcher matcher = new ConstantMatcher(20);
        assertEquals(matcher.getHigh(), 20);
    }
}