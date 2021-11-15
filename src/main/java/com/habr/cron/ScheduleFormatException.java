package com.habr.cron;

public class ScheduleFormatException extends Exception
{
    private final String schedule;

    public ScheduleFormatException(String message, String schedule)
    {
        super(message + "\nTrouble in this schedule: " + schedule);
        this.schedule = schedule;
    }

    public ScheduleFormatException(String message, Range range, String schedule)
    {
        super(message +
                "\nProblem in element: " + range +
                "\nTrouble in this schedule: " + schedule);

        this.schedule = schedule;
    }

    public String getSchedule()
    {
        return schedule;
    }
}
