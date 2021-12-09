package com.habr.cron;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ParserTest
{
    @Test(expectedExceptions = {ScheduleFormatException.class, IllegalArgumentException.class},
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
            // не заданное расписание
            {""}, {null}, {"  \t \n "}, // пустая или "псевдо"-пустая строка
            // что угодно, но только не расписание
            {"tarabarschina"}, {"-"}, {"3-.32.32"}, {"a-b"},
            // неверный порядок следования компонентов даты
            {"*:*:* *.*.*"}, {"*.*.*"}, {"*.*.* *"}, {"*:*:* *"}, {"*:*:* * *"},
            // неверное число элементов в раписании
            {"*:*:*:*"}, {"*.*"},
            // звездочка среди списка ,*,   но при этом ,*/n, допустимо
            {"*:*:*.100,200,*,400"},
            // меньшее > большего
            {"15-7:00:00"},
            // выход за допустимые границы в расписании элемента
            {"24:00:00"}, {"1999.01.12 12:00:00"}, {"2000.13.01 12:00:00"}, {"2000.12.33 12:00:00"},
            // задан диапазон "28-32" and "28-32/n" для дней
            //solved {"2020.*.28-32 *:*:*"}, {"2020.*.28-32/2 *:*:*"},
            // явно задан високосный день 02.29 для невисокосного года
            {"2021.2.29 12:*:*"},
            // пропущен компонент даты или времени
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
            {"*.*.* 1,3,6 *:*:*.*",         "[*.*.*] 2,4,7 [*:*:*.*]"},
            {"*.*.* 1-5/2 *:*:*.*",         "[*.*.*] 2-6/2 [*:*:*.*]"},
            {"*:*:*.*",                 "[*.*.*] * [*:*:*.*]"},
            {"*:*:*",                   "[*.*.*] * [*:*:*.0]"},
            {"*/4.1-4/4.32 12:*:*",     "[*/4.1-4/4.32] * [12:*:*.0]"},
            {"*.*.* 1-5 *:*:*.*",       "[*.*.*] 2-6 [*:*:*.*]"},
            {"*.2.29 12:0-50/10:*",     "[*.2.29] * [12:0-50/10:*.0]"},
            {"2020.2.29 12:*:*",        "[2020.2.29] * [12:*:*.0]"},
            {"*.1-6/2.1,4-13/2,17,27 12:*/3:*",        "[*.1-6/2.1,4-13/2,17,27] * [12:*/3:*.0]"},
        };
    }
}