package com.habr.cron;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class DaysMapTest
{

    @Test
    public void testAddValue() throws Exception
    {
        DaysMap map = new DaysMap();

        map.addValue((byte) 1); // add Sunday
        map.addValue((byte) 2); // add Monday
        assertEquals(map.getMap(), (byte)3); // = Sunday and Monday

        map.addValue((byte) 3); // add Tuesday
        assertEquals(map.getMap(), (byte)7); // = Sunday + Monday + Tuesday
    }

    @Test
    public void testSetMap() throws Exception
    {
        DaysMap map = new DaysMap();
        map.setMap((byte) 7);
        assertEquals(map.getMap(), (byte)7);

        map.setMap((byte) 5);
        assertEquals(map.getMap(), (byte)5);
    }

    @Test
    public void testAddMap() throws Exception
    {
        DaysMap map = new DaysMap();

        map.addMap((byte) 1);
        map.addMap((byte) 2);
        assertEquals(map.getMap(), (byte)3);

        map.addMap((byte) 3);
        assertEquals(map.getMap(), (byte)3);

        map.addMap((byte) 4);
        assertEquals(map.getMap(), (byte)7);
    }

    @Test
    public void testIntersects() throws Exception
    {
        DaysMap map = new DaysMap();
        map.setMap(Byte.valueOf("0000011", 2)); //selected: Sunday and Monday

        assertTrue(map.intersects(Byte.valueOf("0000001", 2))); //intersects with (Sunday)
        assertTrue(map.intersects(Byte.valueOf("0000011", 2))); //intersects with (Sunday,Monday)
        assertTrue(map.intersects(Byte.valueOf("0000101", 2))); //intersects with (Sunday,Tuesday)
        assertTrue(map.intersects(Byte.valueOf("0000110", 2))); //intersects with (Monday,Tuesday)
        assertTrue(map.intersects(Byte.valueOf("0100001", 2))); //intersects with (Sunday, Friday)

        assertFalse(map.intersects(Byte.valueOf("0100000", 2)));//not intersects with Friday
    }

    @Test
    public void testContains() throws Exception
    {
        DaysMap map = new DaysMap();
        map.setMap(Byte.valueOf("0001101", 2)); //selected: Sunday, TuesDay, Wednesday

        assertTrue(map.contains(0)); // contains Sunday
        assertFalse(map.contains(1));
        assertTrue(map.contains(2)); // contains Tuesday
        assertTrue(map.contains(3)); // contains Wednesday
        assertFalse(map.contains(4));
        assertFalse(map.contains(5));
        assertFalse(map.contains(6));
    }

    @Test
    public void testIsAsterisk() throws Exception
    {
        DaysMap map = new DaysMap();

        map.setMap(Byte.valueOf("0001101", 2)); // some weekdays selected
        assertFalse(map.isAsterisk());

        map.setMap(Byte.valueOf("1111111", 2)); // all weekdays selected
        assertTrue(map.isAsterisk());
    }
}