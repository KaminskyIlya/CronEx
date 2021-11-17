package com.habr.cron;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.testng.Assert.assertEquals;

public class SpecialTestsFromHabr
{
    private static final SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");

    @Test(dataProvider = "nextEvent_DataProvider")
    public void testSchedule(String schedule, String startDate, String expectedDate) throws Exception
    {
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = f.parse(startDate);

        Schedule s = new Schedule(schedule);
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
        return new Object[][]{
                {"2021.*.*/3 2 12:00:00", // test to fix #0002
                        "30.10.2021 12:00:00.000",
                        "16.11.2021 12:00:00.000",
                },

                {"2021.*.23-27,29 0-3,5 12:00:00.1", // tests to fix #0003
                        "24.02.2021 11:00:00.000",
                        "24.02.2021 12:00:00.001",
                },
                {"2021.*.23-27,29 0-3,5 12:00:00.1",
                        "24.02.2021 12:00:00.001",
                        "26.02.2021 12:00:00.001",
                },
                {"2021.*.23-27,29 0-3,5 12:00:00.1",
                        "26.02.2021 12:00:00.001",
                        "23.03.2021 12:00:00.001",
                },
                {"2021.*.23-27,29 0-3,5 12:00:00.1",
                        "23.03.2021 12:00:00.001",
                        "24.03.2021 12:00:00.001",
                },
                {"2021.*.23-27,29 0-3,5 12:00:00.1",
                        "24.03.2021 12:00:00.001",
                        "26.03.2021 12:00:00.001",
                },
                {"2021.*.23-27,29 0-3,5 12:00:00.1",
                        "26.03.2021 12:00:00.001",
                        "29.03.2021 12:00:00.001",
                },
        };
    }

}