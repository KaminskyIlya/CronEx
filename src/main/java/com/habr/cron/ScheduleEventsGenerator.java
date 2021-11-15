package com.habr.cron;

import java.util.Date;
import java.util.Iterator;

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
     */
    Date next();

    /**
     * @return string presentation of source schedule
     */
    String schedule();
}
