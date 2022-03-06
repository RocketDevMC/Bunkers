package me.redis.bunkers.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

    private static DateFormat MATCH_DATE_FORMAT = new SimpleDateFormat("mm:ss");

    private static int adjustTime(long timestamp, TimeUnit from, TimeUnit to) {
        return (int) to.convert(timestamp, from);
    }

    public static String formatElapsingNanoseconds(long timestamp) {
        timestamp = System.nanoTime() - timestamp;

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.SECOND, adjustTime(timestamp, TimeUnit.NANOSECONDS, TimeUnit.SECONDS));
        timestamp -= TimeUnit.SECONDS.toNanos(TimeUnit.NANOSECONDS.toSeconds(timestamp));

        cal.set(Calendar.MINUTE, adjustTime(timestamp, TimeUnit.NANOSECONDS, TimeUnit.MINUTES));
        timestamp -= TimeUnit.MINUTES.toNanos(TimeUnit.NANOSECONDS.toMinutes(timestamp));

        cal.set(Calendar.HOUR_OF_DAY, adjustTime(timestamp, TimeUnit.NANOSECONDS, TimeUnit.HOURS));

        return MATCH_DATE_FORMAT.format(cal.getTime());
    }

    public static String formatSeconds(int seconds) {
        int minutes = seconds / 60;

        if (minutes == 0) {
            return seconds + " seconds";
        }

        seconds %= 60;

        return minutes + " minutes and " + seconds + " seconds";
    }

    public static long parseDuration(String string) {
        if (string == null || string.isEmpty()) {
            return -1L;
        }

        long result = 0L;
        StringBuilder number = new StringBuilder();

        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (Character.isDigit(c)) {
                number.append(c);
            } else {
                String str;
                if (Character.isLetter(c) && !(str = number.toString()).isEmpty()) {
                    result += convert(Integer.parseInt(str), c);
                    number = new StringBuilder();
                }
            }
        }

        return result;
    }

    private static long convert(int value, char unit) {
        switch (unit) {
            case 'y':
                return value * TimeUnit.DAYS.toMillis(365L);
            case 'M':
                return value * TimeUnit.DAYS.toMillis(30L);
            case 'd':
                return value * TimeUnit.DAYS.toMillis(1L);
            case 'h':
                return value * TimeUnit.HOURS.toMillis(1L);
            case 'm':
                return value * TimeUnit.MINUTES.toMillis(1L);
            case 's':
                return value * TimeUnit.SECONDS.toMillis(1L);
        }

        return -1L;
    }
}