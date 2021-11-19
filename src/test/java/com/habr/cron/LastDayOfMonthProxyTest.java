package com.habr.cron;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class LastDayOfMonthProxyTest
{
    @Test
    public void testNextOnLastFebDay() throws Exception
    {
        DigitMatcher matcher = new HashMapMatcher(1, 31);
        MapMatcher map = (MapMatcher) matcher;
        map.addRange(23, 27, 1);
        map.addRange(29, 29, 1);
        map.finishRange();

        GregCalendar calendar = new GregCalendar(0);
        calendar.year = 2021;
        calendar.month = 2; // February

        LastDayOfMonthProxy proxy = new LastDayOfMonthProxy(matcher, calendar);

        assertTrue(proxy.hasNext(23)); // next is the 24th
        assertTrue(proxy.hasNext(24)); // next is the 25th
        assertTrue(proxy.hasNext(25)); // next is the 26th
        assertTrue(proxy.hasNext(26)); // next is the 27th
        assertFalse(proxy.hasNext(27)); // according the schedule '23-27,29' the next value is a 29th, but
                                          // now non-leap year (see calendar above), and we can't return 29th.
        //but it's true:
        assertTrue(matcher.hasNext(27)); // the source (non proxied) matcher will returns the 29th
        assertEquals(matcher.getNext(27), 29);

        assertFalse(proxy.match(28)); // the 28th not in a source schedule
        assertFalse(matcher.match(28));

        assertTrue(matcher.match(29)); // The 29th is valid for source matcher
        assertFalse(proxy.match(29)); // but is not valid for proxy

        calendar.year = 2020; // set a leap year
        assertTrue(proxy.hasNext(27)); // next is the 29th February
        assertEquals(proxy.getNext(27), 29);
    }

    @Test
    public void testNextOnLastApril() throws Exception
    {
        DigitMatcher matcher = new HashMapMatcher(1, 31);
        MapMatcher map = (MapMatcher) matcher;
        map.addRange(23, 27, 1);
        map.addRange(31, 31, 1);
        map.finishRange();


        GregCalendar calendar = new GregCalendar(0);
        calendar.year = 2021;
        calendar.month = 4; // April

        LastDayOfMonthProxy proxy = new LastDayOfMonthProxy(matcher, calendar);

        assertTrue(proxy.hasNext(23)); // next is the 24th
        assertTrue(proxy.hasNext(24)); // next is the 25th
        assertTrue(proxy.hasNext(25)); // next is the 26th
        assertTrue(proxy.hasNext(26)); // next is the 27th
        assertFalse(proxy.hasNext(27)); // according the schedule '23-27,31' the next value is a 31th, but
                                        // is overflow for April, and we can't return 31th.

        //but it's true:
        assertTrue(matcher.hasNext(27)); // the source (non proxied) matcher will returns the 31th
        assertEquals(matcher.getNext(27), 31);
    }



    @Test
    public void testPrevOnLastFebDay() throws Exception
    {
        DigitMatcher matcher = new HashMapMatcher(1, 31);
        MapMatcher map = (MapMatcher) matcher;
        map.addRange(29, 31, 1);
        map.finishRange();

        GregCalendar calendar = new GregCalendar(0);
        calendar.year = 2021; // non leap year
        calendar.month = 2; // February

        LastDayOfMonthProxy proxy = new LastDayOfMonthProxy(matcher, calendar);

        assertTrue(matcher.hasPrev(31)); // prev is the 30th
        assertFalse(proxy.hasPrev(31)); // but 30th is overflow on February; there is no previous value

        calendar.year = 2020; // set a leap year
        assertTrue(proxy.hasPrev(31)); // prev is the 29th
        assertEquals(proxy.getPrev(31), 29);
    }

    @Test
    public void testPrevOnLatAprilDay() throws Exception
    {
        DigitMatcher matcher = new HashMapMatcher(1, 33);
        MapMatcher map = (MapMatcher) matcher;
        map.addRange(31, 33, 1); // synthetic range for test only
        map.finishRange();

        GregCalendar calendar = new GregCalendar(0);
        calendar.year = 2021; // non leap year
        calendar.month = 4; // April

        LastDayOfMonthProxy proxy = new LastDayOfMonthProxy(matcher, calendar);

        assertTrue(matcher.hasPrev(33)); // prev is the 32th
        assertEquals(matcher.getPrev(33), 32);
        assertFalse(proxy.hasPrev(33)); // but 32th is overflow on February; there is no previous value in schedule

        // now let's add another range
        matcher = new HashMapMatcher(1, 33);
        map = (MapMatcher) matcher;
        map.addRange(23, 27, 1);
        map.addRange(31, 33, 1); // synthetic range for test only
        map.finishRange();

        proxy = new LastDayOfMonthProxy(matcher, calendar);
        assertTrue(proxy.hasPrev(33)); // now we have previous value
        assertEquals(proxy.getPrev(33), 27);
    }

    @Test
    public void testPrevTroughLastFebDay() throws Exception
    {
        DigitMatcher matcher = new HashMapMatcher(1, 31);
        MapMatcher map = (MapMatcher) matcher;
        map.addRange(23, 27, 1);
        map.addRange(29, 31, 1);
        map.finishRange();

        GregCalendar calendar = new GregCalendar(0);
        calendar.year = 2021;
        calendar.month = 2; // February

        LastDayOfMonthProxy proxy = new LastDayOfMonthProxy(matcher, calendar);

        assertTrue(proxy.hasPrev(31));
        assertEquals(proxy.getPrev(31), 27);

        assertTrue(proxy.hasPrev(29));
        assertEquals(proxy.getPrev(29), 27);

        assertTrue(proxy.hasPrev(28));
        assertEquals(proxy.getPrev(28), 27);

        assertTrue(proxy.hasPrev(27));
        assertEquals(proxy.getPrev(27), 26);
    }


    @Test
    public void testIsAboveTroughLastFebDay() throws Exception
    {
        DigitMatcher matcher = new HashMapMatcher(1, 31);
        MapMatcher map = (MapMatcher) matcher;
        map.addRange(23, 27, 1);
        map.addRange(29, 29, 1);
        map.finishRange();

        GregCalendar calendar = new GregCalendar(0);
        calendar.year = 2021;
        calendar.month = 2; // February

        LastDayOfMonthProxy proxy = new LastDayOfMonthProxy(matcher, calendar);

        assertFalse(proxy.isAbove(27)); // the isAbove() checks according schedule
        assertFalse(proxy.isAbove(28));
        assertTrue(proxy.isAbove(29)); // above
        assertTrue(proxy.isAbove(30)); // above

        calendar.year = 2020; // set a leap year

        assertFalse(proxy.isAbove(27)); // the isAbove() checks according schedule
        assertFalse(proxy.isAbove(28));
        assertFalse(proxy.isAbove(29));
        assertTrue(proxy.isAbove(30)); // above
    }
}
