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
        map.addValue(1); // we will add the 1st day on Sunday (01.01.1995)

        Byte actual, expected;

        // let's see how this map will change in 1996:
        actual = DaysMap.rollMapByYear(map.getMap(), 1996);
        expected = Byte.valueOf("0000010", 2);  // Monday
        assertEquals(actual, expected);

        // let's see how this map will change in 1997:
        actual = DaysMap.rollMapByYear(map.getMap(), 1997);
        expected = Byte.valueOf("0001000", 2);  // Wednesday
        assertEquals(actual, expected);

        // let's see how this map will change in 1998:
        actual = DaysMap.rollMapByYear(map.getMap(), 1998);
        expected = Byte.valueOf("0010000", 2); // Thursday
        assertEquals(actual, expected);

        // let's see how this map will change in 1999:
        actual = DaysMap.rollMapByYear(map.getMap(), 1999);
        expected = Byte.valueOf("0100000", 2); // Friday
        assertEquals(actual, expected);

        // let's see how this map will change in 2000:
        actual = DaysMap.rollMapByYear(map.getMap(), 2000);
        expected = Byte.valueOf("1000000", 2);  // Saturday
        assertEquals(actual, expected);



        map.addValue(4); // we will add another 4th on Wednesday (01.01.1995)

        // let's see how this map will change in 1996:
        actual = DaysMap.rollMapByYear(map.getMap(), 1996);
        expected = Byte.valueOf("0010010", 2); // Monday, Thursday
        assertEquals(actual, expected);

        // let's see how this map will change in 1997:
        actual = DaysMap.rollMapByYear(map.getMap(), 1997);
        expected = Byte.valueOf("1001000", 2); // Wednesday, Saturday
        assertEquals(actual, expected);

        // let's see how this map will change in 1998:
        actual = DaysMap.rollMapByYear(map.getMap(), 1998);
        expected = Byte.valueOf("0010001", 2); // Thursday, Sunday
        assertEquals(actual, expected);

        // let's see how this map will change in 1999:
        actual = DaysMap.rollMapByYear(map.getMap(), 1999);
        expected = Byte.valueOf("0100010", 2); // Friday, Monday
        assertEquals(actual, expected);

        // let's see how this map will change in 2000:
        actual = DaysMap.rollMapByYear(map.getMap(), 2000);
        expected = Byte.valueOf("1000100", 2); // Saturday, Tuesday
        assertEquals(actual, expected);
    }



    @Test
    public void testRollMapByMonth() throws Exception
    {
        Byte actual, expected, initial;

        DaysMap map = new DaysMap();
        map.addValue(1); // we will add the 1st day on Sunday (01.01.1995)
        initial = DaysMap.rollMapByYear(map.getMap(), 2021); // let's bring the map to 2021

        // scrolling to January doesn't change the map
        actual = DaysMap.rollMapByMonth(initial, Calendar.JANUARY + 1, false);
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




        initial = DaysMap.rollMapByYear(map.getMap(), 1996); // let's bring the map to 1996
        actual = DaysMap.rollMapByMonth(initial, Calendar.JANUARY + 1, true);  // scrolling to January doesn't change the map
        assertEquals(actual, initial, "01.01.1996 MUST be a Monday and have map 0010000");

        actual = DaysMap.rollMapByMonth(initial, Calendar.APRIL + 1, true);
        expected = Byte.valueOf("0000010", 2);
        assertEquals(actual, expected, "01.04.1996 MUST be a Monday and have map 0000010"); // same as january for leap years

        actual = DaysMap.rollMapByMonth(initial, Calendar.JULY + 1, true);
        expected = Byte.valueOf("0000010", 2);
        assertEquals(actual, expected, "01.07.1996 MUST be a Monday and have map 0000010"); // same as january for leap years


        initial = DaysMap.rollMapByYear(map.getMap(), 1997); // let's bring the map to 1997
        actual = DaysMap.rollMapByMonth(initial, Calendar.JANUARY + 1, true);  // scrolling to January doesn't change the map
        assertEquals(actual, initial, "01.01.1997 MUST be a Wednesday and have map 0001000");

        actual = DaysMap.rollMapByMonth(initial, Calendar.OCTOBER + 1, false);
        expected = Byte.valueOf("0001000", 2);
        assertEquals(actual, expected, "01.10.1997 MUST be a Wednesday and have map 0001000"); // same as january for non-leap years




        initial = DaysMap.rollMapByYear(map.getMap(), 2020); // let's bring the map to 2020

        actual = DaysMap.rollMapByMonth(initial, Calendar.JANUARY + 1, true); // scrolling to January doesn't change the map
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