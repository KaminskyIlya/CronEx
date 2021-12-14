package com.habr.cron;

import java.util.Date;

/**
 * Generator of scheduled events.
 *
 */
public interface ScheduleEventsGenerator
{
    /**
     * @return the date of last event
     */
    Date last();

    /**
     * @return the date of next event after last
     * @throws IllegalStateException when the next value if out of schedule
     */
    Date next();

    /**
     * @return string presentation of source schedule
     */
    String schedule();
}
