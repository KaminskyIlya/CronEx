package com.habr.cron;

import org.testng.annotations.Test;

import static com.habr.cron.ScheduleElements.*;
import static org.testng.Assert.*;

public class ScheduleModelTest
{

    @Test
    public void testSetGetModelComponent() throws Exception
    {
        ScheduleModel model = new ScheduleModel();
        RangeList ranges = new RangeList(new Range(1, 10, 3));

        model.setModelFor(YEAR, ranges);
        assertEquals(model.getModelFor(YEAR), ranges);
    }

    @Test
    public void testInitDefaults() throws Exception
    {
        ScheduleModel model = new ScheduleModel();
        assertFalse(model.isDatePresent());
        assertFalse(model.isWeekDayPresent());
        assertFalse(model.isMillisecondsPresent());

        model.initDefaults();
        assertTrue(model.isDatePresent());
        assertTrue(model.isWeekDayPresent());
        assertTrue(model.isMillisecondsPresent());
    }

    @Test
    public void testIsDatePresent() throws Exception
    {
        ScheduleModel model = new ScheduleModel();
        assertFalse(model.isDatePresent());

        model.setModelFor(YEAR, RangeList.ASTERISK);
        model.setModelFor(MONTH, RangeList.ASTERISK);
        model.setModelFor(DAY_OF_MONTH, RangeList.ASTERISK);
        assertTrue(model.isDatePresent());
    }

    @Test
    public void testIsTimePresent() throws Exception
    {
        ScheduleModel model = new ScheduleModel();
        assertFalse(model.isTimePresent());

        model.setModelFor(HOURS, RangeList.ASTERISK);
        model.setModelFor(MINUTES, RangeList.ASTERISK);
        model.setModelFor(SECONDS, RangeList.ASTERISK);
        assertTrue(model.isTimePresent());
    }

    @Test
    public void testIsWeekDayPresent() throws Exception
    {
        ScheduleModel model = new ScheduleModel();
        assertFalse(model.isWeekDayPresent());

        model.setModelFor(DAY_OF_WEEK, RangeList.ASTERISK);
        assertTrue(model.isWeekDayPresent());
    }

    @Test
    public void testIsMillisecondsPresent() throws Exception
    {
        ScheduleModel model = new ScheduleModel();
        assertFalse(model.isMillisecondsPresent());

        model.setModelFor(MILLIS, RangeList.ASTERISK);
        assertTrue(model.isMillisecondsPresent());

    }

    @Test
    public void testIsAnyDate() throws Exception
    {
        ScheduleModel model = new ScheduleModel();
        model.initDefaults();
        assertTrue(model.isAnyDate()); // true because the date was not present in a schedule

        model = new ScheduleModel();
        model.setModelFor(YEAR, RangeList.ASTERISK);
        model.setModelFor(MONTH, RangeList.ASTERISK);
        model.setModelFor(DAY_OF_MONTH, RangeList.ASTERISK);
        model.initDefaults();
        assertTrue(model.isAnyDate()); // '*.*.*'

        model = new ScheduleModel();
        model.setModelFor(YEAR, RangeList.ASTERISK);
        model.setModelFor(MONTH, RangeList.ASTERISK);
        model.setModelFor(DAY_OF_MONTH, new RangeList(new Range(1)));
        model.initDefaults();
        assertFalse(model.isAnyDate()); // false because we chose '*.*.1'
    }

    @Test
    public void testIsAnyWeekDay() throws Exception
    {
        ScheduleModel model = new ScheduleModel();
        model.initDefaults();
        assertTrue(model.isAnyWeekDay()); // true because the day of week was not present in a schedule

        model = new ScheduleModel();
        model.setModelFor(DAY_OF_WEEK, RangeList.ASTERISK);
        model.initDefaults();
        assertTrue(model.isAnyWeekDay()); // '*'

        model = new ScheduleModel();
        model.setModelFor(DAY_OF_WEEK, new RangeList(new Range(3)));
        model.initDefaults();
        assertFalse(model.isAnyWeekDay()); // false because we chose '3' (Wednesday)
    }
}