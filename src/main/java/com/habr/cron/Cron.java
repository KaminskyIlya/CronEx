package com.habr.cron;

import java.util.Date;

/**
 * Event scheduler interface.
 */
public interface Cron
{
    /**
     * Returns the next moment in the schedule closest to the specified time,
     * or the specified time itself, if it is in the schedule.
     *
     * @param d the specified time
     * @return the nearest time in the schedule
     */
    Date NearestEvent(Date d);

    /**
     * Returns the previous moment in the schedule closest to the specified time,
     * or the specified time itself, if it is in the schedule.
     *
     * @param d the specified time
     * @return the nearest time in the schedule
     */
    Date NearestPrevEvent(Date d);

    /**
     * Returns the next time point in the schedule.
     *
     * @param d the specified time
     * @return the nearest time in the schedule
     */
    Date NextEvent(Date d);

    /**
     * Returns the previous time point in the schedule.
     *
     * @param d the specified time
     * @return the nearest time in the schedule
     */
    Date PrevEvent(Date d);
}
