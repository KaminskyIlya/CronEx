package speed;

import com.habr.cron.Cron;
import com.habr.cron.Schedule;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Algorithm execution speed test.
 */
public class Benchmark
{
    private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");
    private static final int LOOP_COUNT = (int) 1e6;

    public static void main(String args[]) throws Exception
    {
        for (String data[] : TEST_DATES)
        {
            Date date = fmt.parse(data[1]);
            String schedule = data[0];
            Cron cron;
            long nanos;

            cron = new Schedule(schedule);
            nanos = runCronBenchmark(cron, date);
            System.out.println(
                    String.format("[Dev] %s %s  - %d nsec",
                            schedule,
                            data[1],
                            nanos)
            );
        }
    }


    private static long runCronBenchmark(Cron schedule, Date date) throws Exception
    {
        long n1 = System.nanoTime();
        for (int i = 0; i < LOOP_COUNT; i++)
        {
            schedule.NearestEvent(date);
        }
        long n2 = System.nanoTime();

        return (n2 - n1) / LOOP_COUNT;
    }


    private static final String[][] TEST_DATES = new String[][]
    {
            // the nearest event is in 4 years; expected to receive 2016.01.01 12:00:00.000
            {"*/4.01.01 12:00:00.000",              "2012.01.01 12:00:00.001"},

            // generation of events with a frequency of 1ms; expected to receive 2021.30.09 12:00:00.003
            {"*.*.* *:*:*.*",                       "2021.30.09 12:00:00.002"},

            // the nearest event in April; expected to receive 2001.04.06 00:00:00.001
            {"*.4.6,7 * *:*:*.1,2,3-5,10-20/3",     "2001.01.01 00:00:00.000"},

            // the nearest event in next year; expected to receive 2081.04.06 00:00:00.001
            {"*.4.6,7 * *:*:*.1,2,3-5,10-20/3",     "2080.05.05 12:00:00.000"},

            // the next event in the last of possible range; expected 2100.12.31 23:59:59.999
            {"2100.12.31 23:59:59.999",             "2000.01.01 00:00:00.000"},
            {"2100.12.31 23:59:59.999",             "2080.05.05 00:00:00.000"},

            //
            // Synthetic schedules: very complexity schedules.
            // Will not used in real cases.
            //

            // the last day of February in Saturday; expected 29.02.2048 12:00:00.000
            {"*.02.29 6 12:00:00",                  "2021.01.01 12:00:00.000"},

            // on Friday with float days top limit; expected 30.04.2021 12:14:34.000
            {"*.*.20-32/5 5 12:14:34",              "2021.01.31 12:14:33.177"},
    };

}
