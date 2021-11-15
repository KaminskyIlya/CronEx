package com.habr.cron;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class BitMapMatcherTest
{
    private BitMapMatcher matcher;

    @Test(expectedExceptions = ArrayIndexOutOfBoundsException.class)
    public void testOutOfRange() throws Exception
    {
        BitMapMatcher matcher = new BitMapMatcher(1, 100);
        matcher.addRange(101, 200, 1);
    }

    @BeforeMethod
    public void setUp() throws Exception
    {
        // Setup schedule as "1,2,7-17/3,204,320-624/5,827-927"
        matcher = new BitMapMatcher(1, 927); // IMPORTANT: 1 - minimal available value
        matcher.addRange(1,2,1);        // 1-2/1 = 1,2
        matcher.addRange(7,17,3);       // 7-17/3 = 7,10,13,16
        matcher.addRange(204,204,1);    // 204
        matcher.addRange(320,624,5);    // 320-624/5
        matcher.addRange(827,927,1);    // 827-927
        matcher.finishRange();
    }

    @Test
    public void testMatch() throws Exception
    {
        assertTrue(matcher.match(1));
        assertTrue(matcher.match(2));
        assertTrue(matcher.match(7));
        assertTrue(matcher.match(10));
        assertTrue(matcher.match(13));
        assertTrue(matcher.match(16));
        assertTrue(matcher.match(204));
        assertTrue(matcher.match(320));
        assertTrue(matcher.match(325));
        assertTrue(matcher.match(620));
        assertTrue(matcher.match(827));
        assertTrue(matcher.match(927));

        assertFalse(matcher.match(-1));
        assertFalse(matcher.match(0));
        assertFalse(matcher.match(3));
        assertFalse(matcher.match(8));
        assertFalse(matcher.match(321));
        assertFalse(matcher.match(624));
        assertFalse(matcher.match(928));
        assertFalse(matcher.match(1000));
    }





    @Test
    public void testGetNext() throws Exception
    {
        assertEquals(matcher.getNext(3), 7);
        assertEquals(matcher.getNext(4), 7);
        assertEquals(matcher.getNext(7), 10);
        assertEquals(matcher.getNext(8), 10);
        assertEquals(matcher.getNext(17), 204);
        assertEquals(matcher.getNext(204), 320);
        assertEquals(matcher.getNext(320), 325);
        assertEquals(matcher.getNext(620), 827);
        assertEquals(matcher.getNext(926), 927);
        assertEquals(matcher.getNext(927), 928);
        assertEquals(matcher.getNext(1000), 1001);
    }



    @Test
    public void testGetPrev() throws Exception
    {
        assertEquals(matcher.getPrev(-1), -2);
        assertEquals(matcher.getPrev(0), -1);
        assertEquals(matcher.getPrev(1), 0);
        assertEquals(matcher.getPrev(2), 1);
        assertEquals(matcher.getPrev(3), 2);
        assertEquals(matcher.getPrev(4), 2);
        assertEquals(matcher.getPrev(7), 2);
        assertEquals(matcher.getPrev(8), 7);
        assertEquals(matcher.getPrev(10), 7);
        assertEquals(matcher.getPrev(11), 10);
        assertEquals(matcher.getPrev(12), 10);
        assertEquals(matcher.getPrev(13), 10);
        assertEquals(matcher.getPrev(14), 13);
        assertEquals(matcher.getPrev(203), 16);
        assertEquals(matcher.getPrev(204), 16);
        assertEquals(matcher.getPrev(205), 204);
        assertEquals(matcher.getPrev(827), 620);
        assertEquals(matcher.getPrev(927), 926);
        assertEquals(matcher.getPrev(1000), 927);
    }




    @Test
    public void testGetLow() throws Exception
    {
        BitMapMatcher m = new BitMapMatcher(1, 927);
        m.addRange(3, 5, 1);        // 3-5/1 = 3,4,5
        m.addRange(7, 17, 3);       // 7-17/3 = 7,10,13,16
        m.addRange(204, 204, 1);    // 204
        m.addRange(320, 624, 5);    // 320-624/5
        m.addRange(827, 927, 1);    // 827-927
        m.finishRange();

        assertEquals(m.getLow(), 3);
        assertEquals(matcher.getLow(), 1);
    }

    @Test
    public void testGetHigh() throws Exception
    {
        BitMapMatcher m = new BitMapMatcher(1, 1000);
        m.addRange(3, 5, 1);        // 3-5/1 = 3,4,5
        m.addRange(7, 17, 3);       // 7-17/3 = 7,10,13,16
        m.addRange(204, 204, 1);    // 204
        m.addRange(320, 624, 5);    // 320-624/5
        m.addRange(827, 900, 1);    // 827-927
        m.finishRange();

        assertEquals(m.getHigh(), 900);
        assertEquals(matcher.getHigh(), 927);

        m = new BitMapMatcher(1, 600);
        m.addRange(1, 600, 3);
        m.finishRange();
        assertEquals(m.getHigh(), 598);
    }


}
