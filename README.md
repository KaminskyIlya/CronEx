# CronEx
Ultra quick scheduler with milliseconds resolution.
Basic problem described in [https://habr.com/ru/post/571342/](article).
The average event search time is up to 400ns. 
Thread-Safe. Unmodifiable.
The source schedule can be of any complexity.

**Usage:**
```java
import com.habr.cron.Cron;
import com.habr.cron.Schedule;
import com.habr.cron.ScheduleFormatException;
import com.habr.cron.ScheduleEventsGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
 
/*
 * Classic usage
 */
public static void main(String args[]) throws ScheduleFormatException
{
  Cron cron = new Schedule("*.*.20-32 12:00:00");
  
  SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
  f.setTimeZone(TimeZone.getTimeZone("UTC"));
    
  // searches date of the next event at this moment
  Date date = f.parse("30.04.2021 12:00:00.000");
  Date next = cron.NextEvent(date);
} 

/*
 * Use generator of events
 */
public static void main(String args[]) throws Exception
{
  Schedule schedule = new Schedule("*.*.20-32 12:00:00");

  SimpleDateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
  f.setTimeZone(TimeZone.getTimeZone("UTC"));
  Date date = f.parse("30.04.2021 12:00:00.000");

  // gets a generator for events series, starting from specified date
  ScheduleEventsGenerator generator = schedule.getEventsGenerator(date, true);

  // generate series of next events
  for (int i = 0; i < 1000; i++)
  {
    System.out.println(generator.next());
  }
}
```

**Format of schedule:**

    yyyy.MM.dd w HH:mm:ss.fff
    
    yyyy.MM.dd HH:mm:ss.fff
    
    HH:mm:ss.fff
    
    yyyy.MM.dd w HH:mm:ss    
    
    yyyy.MM.dd HH:mm:ss    
    
    HH:mm:ss    
    
**Where are:**

    yyyy - year (2000-2100)
    
    MM - month (1-12)
    
    dd - day of month (1-31 or 32). The 32th means a last day of month.
    
    w - day of weed (0-6). 0 - sunday, 1 - monday, ... 6 - saturday
    
    HH - hours (0-23)
    
    mm - minutes (0-59)
    
    ss - seconds (0-59)
    
    fff - milliseconds (0-999). If not present, default is 0.


Each part of the date/time can be set as lists and ranges:

    1,2,3-5,10-20/3 - means the list of values: 1,2, 3,4,5, 10,13,16,19
    
Step in range defines via slash. Asterisk means any possible value:

    */4 - means list of values: 0,4,8,12,16,20

You can specify 32 for a last day of month:

    *.*.32 12:00:00 - means exactly at 12:00 on the last day of the month
    
    *.*.20-32/3 12:00:00 - means exactly at 12:00 on the 20th, 23th, 26th, ... until the end of the month in increments of 3.
    
**Schedule examples:**

    *.9.*/2 1-5 10:00:00.000 - means exactly at 10:00 on all days from Mon. to Fri. on odd numbers in September    
    
    *:00:00 - means the beginning of any hour    
    
    *.*.01 01:30:00 - means exactly at 01:30 on the first days of each month    
