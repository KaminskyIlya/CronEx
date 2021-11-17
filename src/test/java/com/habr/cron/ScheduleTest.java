package com.habr.cron;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.testng.Assert.assertEquals;

/**
 * Standard test for correct work.
 */
public class ScheduleTest
{
    private static final SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");


    @Test(dataProvider = "nextEvent_DataProvider")
    public void testNextEvent(String schedule, String sourceDate, String expectedDate) throws Exception
    {
        Schedule s = new Schedule(schedule);
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = f.parse(sourceDate);

        Date actual = s.NextEvent(date);
        Date expected = f.parse(expectedDate);
        assertEquals(actual, expected,
                "\n" +
                        f.format(expected) + " <- expected" +
                        "\n" +
                        f.format(actual) + " <- actual ");
    }
    @DataProvider
    private Object[][] nextEvent_DataProvider() throws ParseException
    {
        return new Object[][] {
                {"*.*.* *:*:*.*",          "30.09.2021 12:00:00.002",
                                           "30.09.2021 12:00:00.003",
                },

                // checking the correctness of processing intervals x-32 for days of the month
                {"*.*.20-32 12:00:00", // schedule
                                            "30.04.2021 12:00:00.000", // current date
                                            "20.05.2021 12:00:00.000"  // expected date
                },
                {"*.*.20-32 12:00:00", // schedule
                                            "31.01.2021 12:00:00.000", // current date
                                            "20.02.2021 12:00:00.000"  // expected date
                },
                {"*.*.20-32 12:00:00", // schedule
                                            "31.01.2021 11:00:00.000", // current date
                                            "31.01.2021 12:00:00.000"  // expected date
                },
                // jump through February, because there are 29 days in it
                {"*.*.29-32 12:00:00", // schedule
                                            "31.01.2021 12:00:00.000", // current date
                                            "29.03.2021 12:00:00.000"  // expected date
                },
                {"*.*.29-32 12:00:00", // schedule
                                            "31.01.2020 12:00:00.000", // current date
                                            "29.02.2020 12:00:00.000"  // expected date
                },
                // The 32nd day means the last day of the month
                {"*.*.32 12:00:00", // schedule
                                            "28.04.2021 12:00:00.000", // current date
                                            "30.04.2021 12:00:00.000"  // expected date
                },
                {"*.*.32 12:00:00", // schedule
                                            "29.04.2021 12:00:00.000", // current date
                                            "30.04.2021 12:00:00.000"  // expected date
                },
                {"*.*.32 12:00:00", // schedule
                                            "30.04.2021 12:00:00.000", // current date
                                            "31.05.2021 12:00:00.000"  // expected date
                },
                {"*.*.32 12:00:00", // schedule
                                            "31.03.2021 12:00:00.000", // current date
                                            "30.04.2021 12:00:00.000"  // expected date
                },

                {"*.2.32 12:00:00", // schedule
                                            "31.03.2020 12:00:00.000", // current date
                                            "28.02.2021 12:00:00.000"  // expected date
                },
                {"*.2.32 12:00:00", // schedule
                                            "31.01.2021 12:00:00.000", // current date
                                            "28.02.2021 12:00:00.000"  // expected date
                },
                {"*.2.32 12:00:00", // schedule
                                            "31.03.2021 12:00:00.000", // current date
                                            "28.02.2022 12:00:00.000"  // expected date
                },
                {"*.2.32 12:00:00", // schedule
                                            "31.01.2020 12:00:00.000", // current date
                                            "29.02.2020 12:00:00.000"  // expected date
                },
                {"*.*.32 12:00:00", // schedule
                                            "31.01.2020 12:00:00.000", // current date
                                            "29.02.2020 12:00:00.000"  // expected date
                },
                {"*.*.32 12:00:00", // schedule
                                            "31.01.2021 12:00:00.000", // current date
                                            "28.02.2021 12:00:00.000"  // expected date
                },
                {"*.*.32 12:00:00", // schedule
                                            "31.01.2020 11:00:00.000", // current date
                                            "31.01.2020 12:00:00.000"  // expected date
                },
                // *.*.01 01:30:00 means 01:30 on the first days of each month
                {"*.*.01 01:30:00", // schedule
                                            "31.12.2020 13:30:00.000", // current date
                                            "01.01.2021 01:30:00.000"  // expected date
                },
                {"*.*.01 01:30:00", // schedule
                                            "01.01.2020 01:00:00.000", // current date
                                            "01.01.2020 01:30:00.000"  // expected date
                },
                // *:00:00 means the beginning of any hour
                {"*:00:00", // schedule
                                            "31.12.2020 23:59:59.999", // current date
                                            "01.01.2021 00:00:00.000"  // expected date
                },
                {"*:00:00", // schedule
                                            "01.01.2020 00:00:00.000", // current date
                                            "01.01.2020 01:00:00.000"  // expected date
                },
                // for hours */4 means 0,4,8,12,16,20
                {"*.*.* * */4:*:*", // schedule
                                            "01.01.2020 00:00:00.000", // current date
                                            "01.01.2020 00:00:01.000"  // expected date
                },
                {"*.*.* * */4:*:*", // schedule
                                            "31.12.2020 21:00:00.000", // current date
                                            "01.01.2021 00:00:00.000"  // expected date
                },
                // 1,2,3-5,10-20/3 means a list of 1,2,3,4,5,10,13,16,19
                {"*.*.* * *:*:*.1,2,3-5,10-20/3", // schedule
                                            "31.12.2020 23:59:59.020", // current date
                                            "01.01.2021 00:00:00.001"  // expected date
                },
                {"*.*.* * *:*:*.1,2,3-5,10-20/3", // schedule
                                            "01.01.2020 00:00:00.011", // current date
                                            "01.01.2020 00:00:00.013"  // expected date
                },
                // 100-600/3 checking the correctness of BitMapMatcher
                {"*.*.* * *:*:*.3-5,100-600/3", // schedule
                                            "01.01.2021 23:59:59.001", // current date
                                            "01.01.2021 23:59:59.003"  // expected date
                },
                // checking the step in 1 millisecond
                {"*.*.* * *:*:*.100", // schedule
                                            "01.01.2020 00:00:00.099", // current date
                                            "01.01.2020 00:00:00.100"  // expected date
                },
                {"*.*.* * *:*:*.*", // schedule
                                            "31.12.2099 23:59:59.999", // current date
                                            "01.01.2100 00:00:00.000"  // expected date
                },
                {"*.*.* * *:*:*.*", // schedule
                                            "01.01.2000 00:00:00.001", // current date
                                            "01.01.2000 00:00:00.002"  // expected date
                },
                // check the maximum number of iterations
                {"2100.12.31 23:59:59.999", // schedule
                                            "01.01.2000 00:00:00.000", // current date
                                            "31.12.2100 23:59:59.999"
                },
                // overflow protection test of the day of the month (February, and others)
                {"*.*.29,30 12:00:00", // schedule
                                            "31.01.2021 12:00:00.000", // current date
                                            "29.03.2021 12:00:00.000"  // expected date
                },
                {"*.*.30 12:00:00", // schedule
                                            "04.02.2021 12:00:00.000", // current date
                                            "30.03.2021 12:00:00.000"  // expected date
                },
                {"*.*.29 12:00:00", // schedule
                                            "29.01.2021 13:00:00.000", // current date
                                            "29.03.2021 12:00:00.000"  // expected date
                },
                {"*.*.29 12:00:00", // schedule
                                            "29.01.2020 13:00:00.000", // current date
                                            "29.02.2020 12:00:00.000"  // expected date
                },
                {"*.*.31 12:00:00", // schedule
                                            "31.01.2021 12:00:00.001", // current date
                                            "31.03.2021 12:00:00.000"  // expected date
                },
                {"*.*.31 12:00:00", // schedule
                                            "31.03.2021 12:00:00.001", // current date
                                            "31.05.2021 12:00:00.000"  // expected date
                },
                {"*.2.4,29,30 12:00:00", // schedule
                                            "04.02.2021 12:00:00.000", // expected date
                                            "04.02.2022 12:00:00.000", // current date
                },
                {"*.*.4,29,30 12:00:00", // schedule
                                            "04.02.2021 12:00:00.000", // expected date
                                            "04.03.2021 12:00:00.000", // current date
                },
                {"*.*.31 12:00:00", // schedule
                                            "31.01.2021 12:00:00.000", // expected date
                                            "31.03.2021 12:00:00.000", // current date
                },
                // tests from articles https://habr.com/ru/post/572726/, https://habr.com/ru/post/572726/
                {"*/4.01.01 12:00:00.000", // schedule
                                            "01.01.2012 12:00:00.001", // expected date
                                            "01.01.2016 12:00:00.000", // current date
                },
                {"*.*.* *:*:*.*", // schedule
                                            "09.30.2021 12:00:00.002", // expected date
                                            "09.30.2021 12:00:00.003", // current date
                },
                {"*.4.6,7 * *:*:*.1,2,3-5,10-20/3", // schedule
                                            "01.01.2001 00:00:00.000", // expected date
                                            "06.04.2001 00:00:00.001", // current date
                },
        };
    }

    @Test(dataProvider = "prevEvent_DataProvider")
    public void testPrevEvent(String schedule, String sourceDate, String expectedDate) throws Exception
    {
        Schedule s = new Schedule(schedule);
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = f.parse(sourceDate);

        Date actual = s.PrevEvent(date);
        Date expected = f.parse(expectedDate);
        assertEquals(actual, expected,
                "\n" +
                f.format(expected) + " <- expected" +
                "\n" +
                f.format(actual) + " <- actual ");
    }
    @DataProvider
    private Object[][] prevEvent_DataProvider()
    {
        return new Object[][] {
                // checking the correctness of processing intervals x-32 for days of the month
                {"*.*.20-32 12:00:00", // schedule
                        "20.05.2021 12:00:00.000", // current date
                        "30.04.2021 12:00:00.000", // expected date
                },
                {"*.*.20-32 12:00:00", // schedule
                        "20.02.2021 12:00:00.000", // current date
                        "31.01.2021 12:00:00.000", // expected date
                },
                {"*.*.20-32 12:00:00", // schedule
                        "31.01.2021 13:00:00.000", // current date
                        "31.01.2021 12:00:00.000", // expected date
                },
                // jump through February, because there are 28/29 days in it
                {"*.*.29-32 12:00:00", // schedule
                        "29.03.2021 12:00:00.000", // current date
                        "31.01.2021 12:00:00.000", // expected date
                },
                {"*.*.29-32 12:00:00", // schedule
                        "29.02.2020 12:00:00.000", // current date
                        "31.01.2020 12:00:00.000", // expected date
                },
                {"2021.2.4,29,30 12:00:00", // schedule
                        "29.03.2021 12:00:00.000",  // current date
                        "04.02.2021 12:00:00.000"}, // expected date
                {"2021.*.4,29,30 12:00:00", // schedule
                        "03.03.2021 12:00:00.000",  // current date
                        "04.02.2021 12:00:00.000"}, // expected date
                {"*.*.31 12:00:00", // schedule
                        "30.03.2021 12:00:00.000",  // current date
                        "31.01.2021 12:00:00.000"}, // expected date
                // the 32nd day means the last day of the month
                {"*.*.32 12:00:00", // schedule
                        "31.05.2021 12:00:00.000",  // current date
                        "30.04.2021 12:00:00.000",  // expected date
                },
                {"*.*.32 12:00:00", // schedule
                        "30.04.2021 12:00:00.000",  // current date
                        "31.03.2021 12:00:00.000",  // expected date
                },
                {"*.*.32 12:00:00", // schedule
                        "28.02.2021 12:00:00.000",  // current date
                        "31.01.2021 12:00:00.000",  // expected date
                },
                {"*.*.32 12:00:00", // schedule
                        "31.01.2020 13:00:00.000",  // current date
                        "31.01.2020 12:00:00.000",  // current date
                },
                {"*.2.32 12:00:00", // schedule
                        "28.02.2021 12:00:00.000",  // expected date
                        "29.02.2020 12:00:00.000",  // current date
                },
                {"*.2.32 12:00:00", // schedule
                        "27.02.2021 12:00:00.000",  // current date
                        "29.02.2020 12:00:00.000",  // expected date
                },
                {"*.2.32 12:00:00", // schedule
                        "28.02.2022 12:00:00.000",  // current date
                        "28.02.2021 12:00:00.000",  // expected date
                },
                // *.*.01 01:30:00 means 01:30 on the first days of each month
                {"*.*.01 01:30:00", // schedule
                        "01.01.2021 01:30:00.000",  // current date
                        "01.12.2020 01:30:00.000",  // expected date
                },
                {"*.*.01 01:30:00", // schedule
                        "01.01.2020 01:50:00.000",  // current date
                        "01.01.2020 01:30:00.000",  // expected date
                },
                // *:00:00 means the beginning of any hour
                {"*:00:00", // schedule
                        "01.01.2021 00:00:00.000",  // current date
                        "31.12.2020 23:00:00.000",  // expected date
                },
                {"*:00:00", // schedule
                        "01.01.2020 01:00:00.000",  // current date
                        "01.01.2020 00:00:00.000",  // expected date
                },
                // (for hours) */4 means the list of 0,4,8,12,16,20
                {"*.*.* * */4:*:*", // schedule
                        "01.01.2020 00:00:03.000",  // current date
                        "01.01.2020 00:00:02.000",  // expected date
                },
                {"*.*.* * */4:*:*", // schedule
                        "01.01.2020 00:03:00.000",  // current date
                        "01.01.2020 00:02:59.000",  // expected date
                },
                {"*.*.* * */4:*:*", // schedule
                        "01.01.2020 03:00:00.000",  // current date
                        "01.01.2020 00:59:59.000",  // expected date
                },
                {"*.*.* * */4:*:*", // schedule
                        "01.01.2021 01:00:00.000",  // current date
                        "01.01.2021 00:59:59.000",  // expected date
                },
                {"*.*.* * */4:00:00", // schedule
                        "01.01.2021 00:00:00.000",  // current date
                        "31.12.2020 20:00:00.000",  // expected date
                },
                // *.*.* * 1-16:00:00 means every hour from one in the morning to four in the afternoon
                {"*.*.* * 1-16:00:00", // schedule
                        "01.01.2021 00:00:00.000",  // current date
                        "31.12.2020 16:00:00.000",  // expected date
                },
                // *.*.* * 1-16/4:00:00 means the list 4,5,9,13
                {"*.*.* * 1-16/4:00:00", // schedule
                        "01.01.2021 00:00:00.000",  // current date
                        "31.12.2020 13:00:00.000",  // expected date
                },
                // 1,2,3-5,10-20/3 means the list of 1,2,3,4,5,10,13,16,19
                {"*.*.* * *:*:*.1,2,3-5,10-20/3", // schedule
                        "01.01.2021 00:00:00.000",  // current date
                        "31.12.2020 23:59:59.019",  // expected date
                },
                {"*.*.* * *:*:*.1,2,3-5,10-20/3", // schedule
                        "01.01.2020 00:00:00.015",  // current date
                        "01.01.2020 00:00:00.013",  // expected date
                },
                // 100-600/3 checking the correctness of BitMapMatcher
                {"*.*.* * *:*:*.30-50/2,100-600/3", // schedule
                        "01.01.2021 23:59:59.020", // expected date
                        "01.01.2021 23:59:58.598", // current date
                },
                // checking the step in 1 millisecond
                {"*.*.* * *:*:*.100", // schedule
                        "01.01.2020 00:00:00.199",  // expected date
                        "01.01.2020 00:00:00.100",  // current date
                },
                {"*.*.* * *:*:*.*", // schedule
                        "01.01.2100 00:00:00.000",  // expected date
                        "31.12.2099 23:59:59.999",  // current date
                },
                {"*.*.* * *:*:*.*", // schedule
                        "01.01.2000 00:00:00.002",  // expected date
                        "01.01.2000 00:00:00.001", // current date
                },
                {"2000.12.31 23:00:00.999", // schedule
                        "31.12.2100 22:59:59.999", // expected date
                        "31.12.2000 23:00:00.999", // current date
                },
                // overflow protection test of the day of the month (February, and others)
                {"*.*.29,30 12:00:00", // schedule
                        "29.03.2021 12:00:00.000",  // expected date
                        "30.01.2021 12:00:00.000",  // current date
                },
                {"*.*.31 12:00:00", // schedule
                        "30.03.2021 12:00:00.000",  // expected date
                        "31.01.2021 12:00:00.000",  // current date
                },
                {"*.*.29 12:00:00", // schedule
                        "29.03.2021 12:00:00.000",  // expected date
                        "29.01.2021 12:00:00.000",  // current date
                },
                {"*.*.29 12:00:00", // schedule
                        "29.02.2020 12:00:00.000",  // expected date
                        "29.01.2020 12:00:00.000",  // current date
                },
                {"*.*.31 12:00:00", // schedule
                        "31.03.2021 12:00:00.000",  // expected date
                        "31.01.2021 12:00:00.000",  // current date
                },
                {"*.*.31 12:00:00", // schedule
                        "31.05.2021 12:00:00.000",  // expected date
                        "31.03.2021 12:00:00.000",  // current date
                },
                {"*.2.4,29,30 12:00:00", // schedule
                        "04.02.2022 12:00:00.000", // current date
                        "04.02.2021 12:00:00.000", // expected date
                },
                {"*.*.4,29,30 12:00:00", // schedule
                        "04.02.2021 12:00:00.000", // expected date
                        "30.01.2021 12:00:00.000", // current date
                },
                {"*.*.31 12:00:00", // schedule
                        "31.03.2021 12:00:00.000",  // expected date
                        "31.01.2021 12:00:00.000",  // current date
                },
        };
    }





    //
    // Special cases
    //

    @Test(dataProvider = "nextEvent_WithOverflow", expectedExceptions = IllegalStateException.class)
    public void testNextEvent_WithOutOfRange(String schedule, String sourceDate, String expectedDate) throws Exception
    {
        f.setTimeZone(TimeZone.getTimeZone("UTC"));

        Schedule s = new Schedule(schedule);
        Date date = f.parse(sourceDate);

        Date actual = s.NextEvent(date);
        Date expected = f.parse(expectedDate);
        assertEquals(actual, expected);
    }
    @DataProvider
    private Object[][] nextEvent_WithOverflow() throws ParseException
    {
        return new Object[][] {
                {"*.*.* * *:*:*.*", // schedule
                        "31.12.2100 23:59:59.999", // current date
                        "01.01.2101 00:00:00.000" // expected date - out of valid range (according format)
                },
                {"2021.2.4,29,30 12:00:00", // schedule
                        "04.02.2021 12:00:00.000", // expected date
                        "04.02.2022 12:00:00.000", // current date - out of valid range (according schedule)
                },
        };
    }



    @Test(dataProvider = "specialWeekDayCases_Next")
    public void testWeekDays_Forward(String schedule, String sourceDate, String expectedDate) throws Exception
    {
        f.setTimeZone(TimeZone.getTimeZone("UTC"));

        Schedule s = new Schedule(schedule);
        Date date = f.parse(sourceDate);

        Date actual = s.NextEvent(date);
        Date expected = f.parse(expectedDate);
        assertEquals(actual, expected,
                "\n" +
                        f.format(expected) + " <- expected" +
                        "\n" +
                        f.format(actual) + " <- actual ");
    }
    @DataProvider
    private Object[][] specialWeekDayCases_Next()
    {
        return new Object[][]{
                // *.9.*/2 1-5 10:00:00.000 means 10:00 on all days from Mon. to Fri. on odd numbers in September
                {"*.9.*/2 1-5 10:00:00.000", // schedule
                                            "03.09.2020 12:00:00.000", // current date
                                            "07.09.2020 10:00:00.000"  // expected date
                },
                {"*.9.*/2 1-5 10:00:00.000", // schedule
                                            "03.09.2020 00:00:00.000", // current date
                                            "03.09.2020 10:00:00.000"  // expected date
                },
                {"*.9.*/2 1-5 10:00:00.000", // schedule
                                            "30.09.2020 12:00:00.000", // current date
                                            "01.09.2021 10:00:00.000"  // expected date
                },
                // every millisecond on Mondays, Wednesdays and Fridays
                {"*.*.* 1,3,5 *:*:*.*", // schedule
                                            "05.08.2021 00:00:00.500", // current date
                                            "06.08.2021 00:00:00.000"  // expected date
                },
                {"*.*.* 1,3,5 *:*:*.*", // schedule
                                            "02.08.2021 00:00:00.500", // current date
                                            "02.08.2021 00:00:00.501"  // expected date
                },
                // once a week every Friday, starting from the 1st
                {"*.*.*/7 5 14:12:13.567", // schedule
                                            "08.11.2021 12:00:00.000", // current date
                                            "01.04.2022 14:12:13.567"  // expected date
                },
                // January and October, numbers 5, 12, 19, 26, on Mondays
                {"*.1,10.5-26/7 1 12:15:11.320", // schedule
                                            "05.10.2021 12:15:11.319", // current date
                                            "05.01.2026 12:15:11.320"  // expected date
                },
                {"*.1,10.5-26/7 1 12:15:11.320", // schedule
                                            "19.11.2021 12:15:11.319", // current date
                                            "05.01.2026 12:15:11.320"  // expected date
                },
                // Saturday, February 29th (11 checks, 6 iterations to search for the year)
                {"*.02.29 6 12:00:00", // schedule
                                            "01.01.2021 12:00:00.000", // current date
                                            "29.02.2048 12:00:00.000"  // expected date
                },
                // 31st on Tuesday
                {"*.*.31 2 12:14:34", // schedule
                                            "31.05.2021 12:14:33.177", // current date
                                            "31.08.2021 12:14:34.000"  // expected date
                },
                // the last day of the month is Sunday
                {"*.*.32 0 12:14:34", // schedule
                                            "31.05.2021 12:14:33.177", // current date
                                            "31.10.2021 12:14:34.000"  // expected date
                },
                // the last day of the month is Thursday
                {"*.*.32 4 12:14:34", // schedule
                                            "31.01.2021 12:14:33.177", // current date
                                            "30.09.2021 12:14:34.000"  // expected date
                },
                // 31st on Saturday
                {"*.*.31 6 12:14:34", // schedule
                                            "31.01.2021 12:14:33.177", // current date
                                            "31.07.2021 12:14:34.000"  // expected date
                },
                // 30th on Thursday
                {"*.*.30 4 12:14:34", // schedule
                                            "31.01.2021 12:14:33.177", // current date
                                            "30.09.2021 12:14:34.000"  // expected date
                },
                // 31st on Wednesday (the trick is that the conditional "February 31st" falls on Wednesday,
                // but resetDate() automatically takes us to 31.03, which is the date satisfying the condition)
                {"*.*.31 3 12:14:34", // schedule
                                            "31.01.2021 12:14:33.177", // current date
                                            "31.03.2021 12:14:34.000"  // expected date
                },
                // any of the numbers 20-32/5 on Friday (the 32nd - is the last day of the month)
                {"*.*.20-32/5 5 12:14:34", // schedule
                                            "31.01.2021 12:14:33.177", // current date
                                            "30.04.2021 12:14:34.000"  // expected date
                },
                // any of the numbers 20-32/5 on Monday (the 32nd - is the last day of the month)
                {"*.*.27-32/2 1 12:14:34", // schedule
                                            "31.01.2021 12:14:33.177", // current date
                                            "29.03.2021 12:14:34.000"  // expected date
                },
        };
    }



    @Test(dataProvider = "specialWeekDayCases_Prev")
    public void testWeekDays_Backward(String schedule, String sourceDate, String expectedDate) throws Exception
    {
        f.setTimeZone(TimeZone.getTimeZone("UTC"));

        Schedule s = new Schedule(schedule);
        Date date = f.parse(sourceDate);

        Date actual = s.PrevEvent(date);
        Date expected = f.parse(expectedDate);
        assertEquals(actual, expected,
                "\n" +
                        f.format(expected) + " <- expected" +
                        "\n" +
                        f.format(actual) + " <- actual ");
    }
    @DataProvider
    private Object[][] specialWeekDayCases_Prev()
    {
        return new Object[][]{
                // *.9.*/2 1-5 10:00:00.000 означает 10:00 во все дни с пн. по пт. по нечетным числам в сентябре
                {"*.9.*/2 1-5 10:00:00.000", // 1 extra check
                        "01.09.2021 08:00:00.000",  // current date
                        "29.09.2020 10:00:00.000",  // expected date
                },
                {"*.9.*/2 1-5 10:00:00.000", // 3 extra check
                        "07.09.2020 08:00:00.000",  // current date
                        "03.09.2020 10:00:00.000",  // expected date
                },
                {"*.9.*/2 1-5 10:00:00.000", // 1 extra checks
                        "03.09.2020 12:00:00.000",  // current date
                        "03.09.2020 10:00:00.000",  // expected date
                },
                // каждую миллисекунду по понедельникам, средам и пятницам
                {"*.*.* 1,3,5 *:*:*.*", // 3 extra checks
                        "06.08.2021 00:00:00.000",  // current date
                        "04.08.2021 23:59:59.999",  // expected date
                },
                {"*.*.* 1,3,5 *:*:*.*", // 1 extra checks
                        "02.08.2021 00:00:00.501",  // current date
                        "02.08.2021 00:00:00.500",  // expected date
                },
                // once a week every Friday, starting from the 1st
                {"*.*.*/7 5 14:12:13.567", // 13 extra checks
                        "01.04.2022 14:12:13.567",  // current date
                        "29.10.2021 14:12:13.567",  // expected date
                },
                // January and October, numbers 5, 12, 19, 26, on Mondays
                {"*.1,10.5-26/7 1 12:15:11.320", // 13 extra checks
                        "05.01.2026 12:15:11.320",  // current date
                        "26.10.2020 12:15:11.320",  // expected date
                },
                {"*.1,10.5-26/7 1 12:15:11.320", // 13 extra checks
                        "05.01.2026 12:15:11.320",  // current date
                        "26.10.2020 12:15:11.320",  // expected date
                },
                // Saturday, February 29th (11 checks, 6 iterations to search for the year)
                {"*.02.29 6 12:00:00", // 10 extra checks
                        "29.02.2048 12:00:00.000",  // current date
                        "29.02.2020 12:00:00.000",  // expected date
                },
                // 31st on Tuesday
                {"*.*.31 2 12:14:34", // 20 extra checks
                        "31.08.2021 12:14:33.177",  // current date
                        "31.03.2020 12:14:34.000",  // expected date
                },
                // the last day of the month is Sunday
                {"*.*.32 0 12:14:34", // 5 extra checks
                        "31.05.2021 12:14:33.177",  // current date
                        "28.02.2021 12:14:34.000",  // expected date
                },
                // the last day of the month is Thursday
                {"*.*.32 4 12:14:34", // 17 extra checks
                        "30.09.2021 12:14:33.177",  // current date
                        "31.12.2020 12:14:34.000",  // expected date
                },
                // 31st on Saturday
                {"*.*.31 6 12:14:34", // 5 extra checks
                        "31.01.2021 12:00:00.000",  // current date
                        "31.10.2020 12:14:34.000",  // expected date
                },
                // 30th on Thursday
                {"*.*.30 4 12:14:34", // 17 extra checks
                        "30.09.2021 12:00:00.000",  // current date
                        "30.07.2020 12:14:34.000",  // expected date
                },
                // 31st on Wednesday (the trick is that the conditional "June, 2020 31st" falls on Wednesday,
                // but resetDate() automatically takes us to 31.03, which is the date satisfying the condition)
                {"*.*.31 3 12:14:34", // 25 extra checks
                        "31.03.2021 12:00:00.000",  // current date
                        "31.07.2019 12:14:34.000",  // expected date
                },
                // any of the numbers 20-32/5 on Friday (the 32nd - is the last day of the month)
                {"*.*.20-32/5 5 12:14:34", // 11 extra checks
                        "30.04.2021 12:00:00.000",  // current date
                        "25.12.2020 12:14:34.000",  // expected date
                },
                // any of the numbers 20-32/5 on Monday (the 32nd - is the last day of the month)
                {"*.*.27-32/2 1 12:14:34", // 14 extra checks
                        "29.03.2021 12:00:00.000",  // expected date
                        "31.08.2020 12:14:34.000",  // current date
                },
        };
    }


    @Test(dataProvider = "generatorDataProvider")
    public void testGenerator(String schedule, String startDate, String[] expectedList) throws Exception
    {
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date start = f.parse(startDate);

        Schedule s = new Schedule(schedule);
        ScheduleEventsGenerator generator = s.getEventsGenerator(start, true);

        for (String item : expectedList)
        {
            Date expected = f.parse(item);
            Date actual = generator.next();

            assertEquals(actual, expected,
                    "\n" +
                            f.format(expected) + " <- expected" +
                            "\n" +
                            f.format(actual) + " <- actual ");
        }
    }
    @DataProvider
    public Object[][] generatorDataProvider()
    {
        return new Object[][]{
                {"*:*:*", "17.11.2021 14:00:00.000",
                        new String[]{
                                "17.11.2021 14:00:01.000",
                                "17.11.2021 14:00:02.000",
                                "17.11.2021 14:00:03.000",
                                "17.11.2021 14:00:04.000",
                                "17.11.2021 14:00:05.000",
                        }
                },
                {"*:*:*.*/100", "17.11.2021 14:00:00.000",
                        new String[]{
                                "17.11.2021 14:00:00.100",
                                "17.11.2021 14:00:00.200",
                                "17.11.2021 14:00:00.300",
                                "17.11.2021 14:00:00.400",
                                "17.11.2021 14:00:00.500",
                        }
                },
                {"*.*.32 12:00:00", "01.01.2020 11:30:23.157",
                        new String[]{
                                "31.01.2020 12:00:00.000",
                                "29.02.2020 12:00:00.000",
                                "31.03.2020 12:00:00.000",
                                "30.04.2020 12:00:00.000",
                                "31.05.2020 12:00:00.000",
                                "30.06.2020 12:00:00.000",
                                "31.07.2020 12:00:00.000",
                                "31.08.2020 12:00:00.000",
                                "30.09.2020 12:00:00.000",
                                "31.10.2020 12:00:00.000",
                                "30.11.2020 12:00:00.000",
                                "31.12.2020 12:00:00.000",
                        }
                },
                {"*.*.31 3 12:14:34", "01.03.2019 12:00:00.000",
                        new String[]{
                                "31.07.2019 12:14:34.000",
                                "31.03.2021 12:14:34.000",
                                "31.08.2022 12:14:34.000",
                                "31.05.2023 12:14:34.000",
                        }
                },
        };
    }
}
