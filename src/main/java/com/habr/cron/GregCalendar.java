package com.habr.cron;

import java.util.Date;
import java.util.TimeZone;

/**
 * Very optimized version of Gregorian calendar system.
 * IMPORTANT NOTICE: works correct only range 2000..2100; Don't use out of interval
 */
class GregCalendar
{
    public int year;
    public int month;
    public int day;

    public int hours;
    public int minutes;
    public int seconds;
    public int milliseconds;


    private static final int[] ELAPSED_DAYS = new int[]{0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334};
    private static final int MAX_DAYS[] = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    /**
     * Creates and initializes calendar with specified date.
     *
     * @param date to initialize
     * @param tz time zone
     */
    public GregCalendar(Date date, TimeZone tz)
    {
        this(date.getTime(), tz.getRawOffset());
    }

    /**
     * Creates calendar and initializes via timestamp.
     * IMPORTANT: timestamp in UTC.
     *
     * @param timestamp of date; UTC timezone used.
     */
    public GregCalendar(long timestamp)
    {
        this(timestamp, 0);
    }




    private GregCalendar(long timestamp, int tzOffset)
    {
        int time = (int) (timestamp % 86400000);
        int days = (int) (timestamp / 86400000);     // days since January 1, 1970

        time += tzOffset;

        milliseconds = time % 1000;     time /= 1000;
        seconds =      time % 60;       time /= 60;
        minutes =      time % 60;       time /= 60;
        hours =        time % 24;

        int y = (days<<2)/1461; // Gregorian years elapsed  since 1970 ( = days / 365.25 )
        int l = (y+1)>>2;     // Gregorian leap days since 1970  ( = (y+1)/4 )
        int d = days - y*365 - l;   // Number of day passed from begin of year
        if ( d > 364 && ((y+2)&3) > 0 ) // overflow control ( (y+2)&3 = is this a leap year?)
        {
            y++;
            l = (y+1)>>2;
            d = days - y*365 - l;
        }

        y += 1970;

        int m;
        int lp = (y & 3) == 0 && y != 2100 ? 1 : 0;
        int lm = 59+lp;

        if ( d < lm ) // until 28/29th Feb
        {
            m = d / 31;
            d -= ELAPSED_DAYS[m] - 1;
        }
        else // since 1st March
        {
            m = (d - lm) / 31 + 2; // March
            int max = MAX_DAYS[m];

            d -= ELAPSED_DAYS[m] - 1 + lp;
            if ( d > max ) { d -= max; m++; }
        }

        year = y;
        month = m + 1;
        day = d;
    }


    /**
     * @return calendar as date form; IMPORTANT: returns always in UTC time!
     */
    public Date asDate()
    {
        int y = year - 1970;
        int leaps = (y + 1)>>2;
        int days = y * 365 + leaps - 1;
        int lp = month > 2 && (year & 3) == 0 && year != 2100 ? 1 : 0;

        days += ELAPSED_DAYS[month-1] + lp;

        days += day; // days since 1 Jan 1970

        int time = hours;
        time = time * 60 + minutes;
        time = time * 60 + seconds;
        time = time * 1000 + milliseconds;

        return new Date(days * 86400000L + time);
    }


    /**
     * @return true, if current year is leap.
     */
    public boolean isLeap()
    {
        return (year & 3) == 0 && year != 2100;
    }

    /**
     * Checks year for leap.
     *
     * @param year
     * @return 1 for leap year, and 0 - for another.
     */
    public static int isLeap(int year)
    {
        return (year & 3) == 0 && year != 2100 ? 1 : 0;
    }

    /**
     * Checks what current day is correct.
     *
     * @return true if current day is correct.
     */
    public boolean isCorrect()
    {
        return day <= maxDays(year, month);
    }

    /**
     * Checks what this day number is correct for current month & year.
     *
     * @param day the number of day
     * @return true, if day is correct
     */
    public boolean isCorrectDay(int day)
    {
        return day <= maxDays(year, month);
    }

    /**
     * @return the last day number in current month & year.
     */
    public int getMaxDay()
    {
        return maxDays(year, month);
    }

    /**
     * @param year 2000..2100
     * @param month 1..12
     * @return the number of days in a month (numbering months from 1)
     */
    public static int maxDays(int year, int month)
    {
        int result = MAX_DAYS[month-1];
        if ( month == 2 ) result += isLeap(year);
        return result;
    }

    /**
     * Day of week
     * @return 0 - sunday, 1 - monday, ... 6 - saturday
     */
    public int getDayOfWeek()
    {
        int a = (14 - month)/12;
        int y = year - a;
        int m = month + 12 * a - 2;
        return (day + (31 * m)/12 + y + y/4 - y/100 + y/400) % 7;
    }



    public void setValue(int fieldId, int value)
    {
        switch (fieldId)
        {
            // this numeric constants synchronized with CalendarDigits.YEAR_IDX
            // see CalendarDigits.matchers[]
            case 0:
                year = value;       break;

            case 1:
                month = value;      break;

            case 2:
                day = value;        break;

            case 3:
                hours = value;      break;

            case 4:
                minutes = value;    break;

            case 5:
                seconds = value;    break;

            case 6:
                milliseconds = value;   break;
        }
    }

    public int getValue(int fieldId)
    {
        switch (fieldId)
        {
            case 0:
                return year;

            case 1:
                return month;

            case 2:
                return day;

            case 3:
                return hours;

            case 4:
                return minutes;

            case 5:
                return seconds;

            case 6:
                return milliseconds;
        }

        throw new AssertionError("This code is MUST BE unreachable!");
    }
}
