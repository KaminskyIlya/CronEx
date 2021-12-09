package com.habr.cron;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Calendar;

import static org.testng.Assert.assertEquals;

public class RollMaps
{

    @Test
    public void testRollMapByYear() throws Exception
    {
        DaysMap map = new DaysMap();
        map.addValue(1); // добавим 1-е число в воскресенье (01.01.1995)

        Byte actual, expected;

        // посмотрим как изменится эта карта в 1996-м году:
        actual = DaysMap.rollMapByYear(map.getMap(), 1996);
        expected = Byte.valueOf("0000010", 2);  // понедельник
        assertEquals(actual, expected);

        // посмотрим как изменится эта карта в 1997-м году:
        actual = DaysMap.rollMapByYear(map.getMap(), 1997);
        expected = Byte.valueOf("0001000", 2);  // среда
        assertEquals(actual, expected);

        // посмотрим как изменится эта карта в 1998-м году:
        actual = DaysMap.rollMapByYear(map.getMap(), 1998);
        expected = Byte.valueOf("0010000", 2); // четверг
        assertEquals(actual, expected);

        // посмотрим как изменится эта карта в 1999-м году:
        actual = DaysMap.rollMapByYear(map.getMap(), 1999);
        expected = Byte.valueOf("0100000", 2); // пятница
        assertEquals(actual, expected);

        // посмотрим как изменится эта карта в 2000-м году:
        actual = DaysMap.rollMapByYear(map.getMap(), 2000);
        expected = Byte.valueOf("1000000", 2);  // суббота
        assertEquals(actual, expected);



        map.addValue(4); // добавим ещё 4-е число в среду (01.01.1995)

        // посмотрим как изменится эта карта в 1996-м году:
        actual = DaysMap.rollMapByYear(map.getMap(), 1996);
        expected = Byte.valueOf("0010010", 2); // понедельник, четверг
        assertEquals(actual, expected);

        // посмотрим как изменится эта карта в 1997-м году:
        actual = DaysMap.rollMapByYear(map.getMap(), 1997);
        expected = Byte.valueOf("1001000", 2); // среда, суббота
        assertEquals(actual, expected);

        // посмотрим как изменится эта карта в 1998-м году:
        actual = DaysMap.rollMapByYear(map.getMap(), 1998);
        expected = Byte.valueOf("0010001", 2); // четверг, воскресенье
        assertEquals(actual, expected);

        // посмотрим как изменится эта карта в 1999-м году:
        actual = DaysMap.rollMapByYear(map.getMap(), 1999);
        expected = Byte.valueOf("0100010", 2); // пятница, понедельник
        assertEquals(actual, expected);

        // посмотрим как изменится эта карта в 2000-м году:
        actual = DaysMap.rollMapByYear(map.getMap(), 2000);
        expected = Byte.valueOf("1000100", 2); // суббота, вторник
        assertEquals(actual, expected);
    }



    @Test
    public void testRollMapByMonth() throws Exception
    {
        Byte actual, expected, initial;

        DaysMap map = new DaysMap();
        map.addValue(1); // добавим 1-е число в воскресенье (01.01.1995)
        initial = DaysMap.rollMapByYear(map.getMap(), 2021); // приведем карту к 2021-му году

        actual = DaysMap.rollMapByMonth(initial, Calendar.JANUARY + 1, false); // прокрутка до января не меняет карту
        assertEquals(actual, initial, "01.01.2021 MUST be a Friday and have map 0100000");

        actual = DaysMap.rollMapByMonth(initial, Calendar.FEBRUARY + 1, false);
        expected = Byte.valueOf("0000010", 2);
        assertEquals(actual, expected, "01.02.2021 MUST be a Monday and has map 0000010");

        actual = DaysMap.rollMapByMonth(initial, Calendar.MARCH + 1, false);
        expected = Byte.valueOf("0000010", 2);
        assertEquals(actual, expected, "01.03.2021 MUST be a Monday and has map 0000010");

        actual = DaysMap.rollMapByMonth(initial, Calendar.APRIL + 1, false);
        expected = Byte.valueOf("0010000", 2);
        assertEquals(actual, expected, "01.04.2021 MUST be a Thursday and has map 0010000");

        actual = DaysMap.rollMapByMonth(initial, Calendar.MAY + 1, false);
        expected = Byte.valueOf("1000000", 2);
        assertEquals(actual, expected, "01.05.2021 MUST be a Saturday and has map 1000000");

        actual = DaysMap.rollMapByMonth(initial, Calendar.JUNE + 1, false);
        expected = Byte.valueOf("0000100", 2);
        assertEquals(actual, expected, "01.06.2021 MUST be a Tuesday and has map 0000100");

        actual = DaysMap.rollMapByMonth(initial, Calendar.JULY + 1, false);
        expected = Byte.valueOf("0010000", 2);
        assertEquals(actual, expected, "01.07.2021 MUST be a Thursday and has map 0010000");

        actual = DaysMap.rollMapByMonth(initial, Calendar.AUGUST + 1, false);
        expected = Byte.valueOf("0000001", 2);
        assertEquals(actual, expected, "01.08.2021 MUST be a Sunday and has map 0000001");

        actual = DaysMap.rollMapByMonth(initial, Calendar.SEPTEMBER + 1, false);
        expected = Byte.valueOf("0001000", 2);
        assertEquals(actual, expected, "01.09.2021 MUST be a Wednesday and has map 0001000");

        actual = DaysMap.rollMapByMonth(initial, Calendar.OCTOBER + 1, false);
        expected = Byte.valueOf("0100000", 2);
        assertEquals(actual, expected, "01.10.2021 MUST be a Friday and has map 0100000");

        actual = DaysMap.rollMapByMonth(initial, Calendar.NOVEMBER + 1, false);
        expected = Byte.valueOf("0000010", 2);
        assertEquals(actual, expected, "01.11.2021 MUST be a Monday and has map 0000010");

        actual = DaysMap.rollMapByMonth(initial, Calendar.DECEMBER + 1, false);
        expected = Byte.valueOf("0001000", 2);
        assertEquals(actual, expected, "01.12.2021 MUST be a Wednesday and has map 0001000");


        initial = DaysMap.rollMapByYear(map.getMap(), 1996); // приведем карту к 1996-му году
        actual = DaysMap.rollMapByMonth(initial, Calendar.JANUARY + 1, true); // прокрутка до января не меняет карту
        assertEquals(actual, initial, "01.01.1996 MUST be a Thursday and have map 0010000");

        actual = DaysMap.rollMapByMonth(initial, Calendar.OCTOBER + 1, true); // прокрутка до января не меняет карту
        expected = Byte.valueOf("0000100", 2);
        assertEquals(actual, expected, "01.10.1996 MUST be a Tuesday and have map 0000100");




        initial = DaysMap.rollMapByYear(map.getMap(), 2020); // приведем карту к 2020-му году

        actual = DaysMap.rollMapByMonth(initial, Calendar.JANUARY + 1, true); // прокрутка до января не меняет карту
        assertEquals(actual, initial, "01.01.2020 MUST be a Wednesday and have map 0001000");

        actual = DaysMap.rollMapByMonth(initial, Calendar.FEBRUARY + 1, true);
        expected = Byte.valueOf("1000000", 2);
        assertEquals(actual, expected, "01.02.2020 MUST be a Saturday and has map 1000000");

        actual = DaysMap.rollMapByMonth(initial, Calendar.MARCH + 1, true);
        expected = Byte.valueOf("0000001", 2);
        assertEquals(actual, expected, "01.03.2020 MUST be a Saturday and has map 0000001");

        actual = DaysMap.rollMapByMonth(initial, Calendar.APRIL + 1, true);
        expected = Byte.valueOf("0001000", 2);
        assertEquals(actual, expected, "01.04.2020 MUST be a Wednesday and has map 0001000");

        actual = DaysMap.rollMapByMonth(initial, Calendar.MAY + 1, true);
        expected = Byte.valueOf("0100000", 2);
        assertEquals(actual, expected, "01.05.2020 MUST be a Friday and has map 0100000");
    }


    @Test(dataProvider = "rollWeekToLeft_DataProvider")
    public void testRollWeekToLeft(String bitMap, int shift, String expected) throws Exception
    {
        byte map = Byte.valueOf(bitMap, 2);
        byte result = DaysMap.rollWeekMap(map, shift); // do cyclic '<<' operation
        String s = "0000000" + Integer.toBinaryString(result);
        String actual = s.substring(s.length() - 7);
        assertEquals(actual, expected);
    }

    @DataProvider
    private Object[][] rollWeekToLeft_DataProvider()
    {
        // source bitmap, shift, expected map
        return new Object[][] {
                {"0100100", 3, "0100010"},
                {"1000100", 3, "0100100"},
                {"1001000", 3, "1000100"},
                {"0001001", 3, "1001000"},
                {"0010001", 3, "0001001"},
                {"0010010", 3, "0010001"},
                {"0100010", 3, "0010010"},

                {"0100101", 2, "0010101"},
                {"0101001", 2, "0100101"},
                {"0101010", 2, "0101001"},
                {"1001010", 2, "0101010"},
                {"1010010", 2, "1001010"},
                {"1010100", 2, "1010010"},
                {"0010101", 2, "1010100"},

                {"0100101", 1, "1001010"},
                {"1010010", 1, "0100101"},
                {"0101001", 1, "1010010"},
                {"1010100", 1, "0101001"},
                {"0101010", 1, "1010100"},
                {"0010101", 1, "0101010"},
                {"1001010", 1, "0010101"},

                {"0100100", 10, "0100010"}, // test shift overflow (10 same 3)
        };
    }


}