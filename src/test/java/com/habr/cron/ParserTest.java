package com.habr.cron;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ParserTest
{
    @Test(expectedExceptions = {ScheduleFormatException.class},
            dataProvider = "parseInvalid_DataProvider")
    public void testParse_WhenScheduleHasInvalidFormat(String schedule) throws Exception
    {
        Parser parser = new Parser();
        parser.parse(schedule);
    }

    @DataProvider
    private Object[][] parseInvalid_DataProvider()
    {
        return new Object[][] {
            // an unspecified schedule
            {""}, {null}, {"  \t \n "}, // empty or "pseudo"-an empty string
            // anything but the schedule
            {"tarabarschina"}, {"-"}, {"3-.32.32"}, {"a-b"},
            // incorrect order of date components
            {"*:*:* *.*.*"}, {"*.*.*"}, {"*.*.* *"}, {"*:*:* *"}, {"*:*:* * *"},
            // incorrect number of items in the schedule
            {"*:*:*:*"}, {"*.*"}, {"*.*.*.*"},
            // an asterisk in the list ,*, but at the same time ,*/n, is allowed
            {"*:*:*.100,200,*,400"},
            // less > more
            {"15-7:00:00"},
            // out of range of the element
            {"1-8 12:00:00"},
            // going beyond the acceptable limits in the element schedule
            {"24:00:00"}, {"1999.01.12 12:00:00"}, {"2000.13.01 12:00:00"}, {"2000.12.33 12:00:00"},
            // the leap day 02.29 is explicitly set for a non-leap year
            {"2021.2.29 12:*:*"},
            // missing date or time component
            {"2021.2 12:*:*"}, {"2021.2.29 12:*"}, {"12:*.123"},
        };
    }



    @Test(dataProvider = "scheduleAndItsModels_DataProvider")
    public void testModelBuilding(String schedule, String expected) throws Exception
    {
        Parser parser = new Parser();
        parser.parse(schedule);
        ScheduleModel model = parser.getScheduleModel();
        Assert.assertEquals(model.toString(), expected);
    }

    @DataProvider
    private Object[][] scheduleAndItsModels_DataProvider()
    {
        return new Object[][] {
            {"*.*.* * *:*:*.*",         "[*.*.*] * [*:*:*.*]"},
            {"*.*.* 1,3,6 *:*:*.*",     "[*.*.*] 2,4,7 [*:*:*.*]"},
            {"*.*.* 1-5/2 *:*:*.*",     "[*.*.*] 2-6/2 [*:*:*.*]"},
            {"*:*:*.*",                 "[*.*.*] * [*:*:*.*]"},
            {"*:*:*",                   "[*.*.*] * [*:*:*.0]"},
            {"*/4.1-4/4.32 12:*:*",     "[*/4.1-4/4.32] * [12:*:*.0]"},
            {"*.*.* 1-5 *:*:*.*",       "[*.*.*] 2-6 [*:*:*.*]"},
            {"*.2.29 12:0-50/10:*",     "[*.2.29] * [12:0-50/10:*.0]"},
            {"2020.2.29 12:*:*",        "[2020.2.29] * [12:*:*.0]"},
            {"*.1-6/2.1,4-13/2,17,27 12:*/3:*",        "[*.1-6/2.1,4-13/2,17,27] * [12:*/3:*.0]"},
            {"002020.001.3 * 1:1:1.004",         "[2020.1.3] * [1:1:1.4]"},
        };
    }
}