package com.habr.cron;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.testng.Assert.*;

public class CalendarDigitsTest
{
    private final MatcherPool pool;
    private final GregCalendar calendar;
    private CalendarDigits digits;


    /**
     * Setup fixed schedule
     */
    public CalendarDigitsTest() throws ScheduleFormatException
    {
        Parser parser = new Parser();
        parser.parse("2021.12.09 12:30:51.100");
        pool = new MatcherPool(parser.getScheduleModel());
        calendar = new GregCalendar(new Date().getTime());
    }

    @BeforeMethod
    public void setUp() throws Exception
    {
        digits = new CalendarDigits(pool, calendar, false);
    }

    @Test
    public void testNext() throws Exception
    {
        digits.gotoYear();
        assertTrue(digits.match(2021));
        assertFalse(digits.isLast());
        digits.next(); //month
        assertTrue(digits.match(12));
        digits.next(); //day
        assertTrue(digits.match(9));
        digits.next(); //hour
        assertTrue(digits.match(12));
        digits.next(); //minute
        assertTrue(digits.match(30));
        digits.next(); //second
        assertTrue(digits.match(51));
        digits.next(); //millis is last
        assertTrue(digits.match(100));
        assertTrue(digits.isLast());

    }

    @Test
    public void testPrev() throws Exception
    {
        digits.gotoMonth();
        assertFalse(digits.match(2021));
        digits.prev(); // to year
        assertTrue(digits.match(2021));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testPrev_MustThrownException_ForYear() throws Exception
    {
        digits.gotoYear();
        digits.prev(); // throws here
    }

    @Test
    public void testIsLast() throws Exception
    {
        digits.gotoYear();
        assertFalse(digits.isLast());

        digits.gotoLastDigit();
        assertTrue(digits.isLast());

        digits.gotoHours();
        assertFalse(digits.isLast());
    }

    @Test
    public void testGotoYear() throws Exception
    {
        digits.gotoYear();
        assertTrue(digits.match(2021));
    }

    @Test
    public void testGotoMonth() throws Exception
    {
        digits.gotoMonth();
        assertTrue(digits.match(12));
    }

    @Test
    public void testGotoDay() throws Exception
    {
        digits.gotoDay();
        assertTrue(digits.match(9));
    }

    @Test
    public void testGotoHours() throws Exception
    {
        digits.gotoHours();
        assertTrue(digits.match(12));
    }


    @Test
    public void testGotoLastDigit() throws Exception
    {
        digits.gotoLastDigit();
        assertTrue(digits.match(100));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void testIncrement_MustThrownException_ForFixedSchedule() throws Exception
    {
        digits.increment();
    }

    @Test
    public void testGetValue() throws Exception
    {
        calendar.minutes = 1;

        digits.gotoHours();
        digits.next();
        assertEquals(digits.getValue(), 1);
    }

    @Test
    public void testInitialize_ForFixedSchedule() throws Exception
    {
        // set calendar to any date before date in schedule ("09.12.2021 12:30:51.100")
        calendar.year = 2000;
        calendar.month = 1;
        calendar.day = 1;
        calendar.hours = 0;
        calendar.minutes = 0;
        calendar.seconds = 0;
        calendar.milliseconds = 0;

        digits.initialize(); // initializes according fixed schedule
        // and change calendar object

        // checks it

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        String actual = format.format(calendar.asDate()); // calendar was changed

        assertEquals(actual, "09.12.2021 12:30:51.100");
    }
}