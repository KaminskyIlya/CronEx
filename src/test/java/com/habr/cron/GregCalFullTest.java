package com.habr.cron;

import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 * Stupid full test of GregCall (custom quick implementation of Gregorian Calendar)
 */
public class GregCalFullTest
{
    @Test
    public void testAllDays() throws Exception
    {
        TimeZone UTC = TimeZone.getTimeZone("UTC");

        Calendar javaCal = new GregorianCalendar(UTC);
        javaCal.set(2000, Calendar.JANUARY, 1, 12, 30, 40); // starts from 01.01.2000 12:30:40.177
        javaCal.set(Calendar.MILLISECOND, 177);


        while ( javaCal.get(Calendar.YEAR) < 2101 ) // until 31.12.2100
        {
            GregCalendar gregCal = new GregCalendar(javaCal.getTime(), UTC);

            assertEquals(gregCal.year, javaCal.get(Calendar.YEAR), "Year not equals " + javaCal.getTime());
            assertEquals(gregCal.month, javaCal.get(Calendar.MONTH)+1, "Month not equals " + javaCal.getTime());
            assertEquals(gregCal.day, javaCal.get(Calendar.DAY_OF_MONTH), "Days not equals " + javaCal.getTime());
            assertEquals(gregCal.getDayOfWeek(), javaCal.get(Calendar.DAY_OF_WEEK)-1, "Weekday not equals " + javaCal.getTime());
            assertEquals(gregCal.hours, javaCal.get(Calendar.HOUR_OF_DAY), "Hours not equals " + javaCal.getTime());
            assertEquals(gregCal.minutes, javaCal.get(Calendar.MINUTE), "Minutes not equals " + javaCal.getTime());
            assertEquals(gregCal.seconds, javaCal.get(Calendar.SECOND), "Seconds not equals " + javaCal.getTime());
            assertEquals(gregCal.milliseconds, javaCal.get(Calendar.MILLISECOND), "Millis not equals " + javaCal.getTime());

            Date actual = gregCal.asDate();
            Date expected = javaCal.getTime();
            assertEquals(actual, expected, "The reverse conversion to the date was performed incorrectly.");

            javaCal.add(Calendar.DAY_OF_YEAR, 1);
        }
    }
}
