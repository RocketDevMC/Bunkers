package me.redis.bunkers.tasks;

import org.bukkit.scheduler.BukkitRunnable;

public class GameTimeTask extends BukkitRunnable {
    public static boolean initialized = false;
    public static int seconds = 0;
    public static int minutes = 0;
    public static int hours = 0;

    @Override
    public void run() {
        if (++GameTimeTask.seconds == 60) {
            ++GameTimeTask.minutes;
            GameTimeTask.seconds = 0;
        }

        if (GameTimeTask.minutes == 60) {
            ++GameTimeTask.hours;
            GameTimeTask.minutes = 0;
        }

        if (!GameTimeTask.initialized || seconds == 0 || seconds % 5 == 0) {
            boolean initializing = false;
            if (!GameTimeTask.initialized) {
                --GameTimeTask.seconds;
                initializing = true;
            }

            if (initializing) {
                ++GameTimeTask.seconds;
            }

            GameTimeTask.initialized = true;
        }
    }

    public static int getNumOfSeconds() {
        return GameTimeTask.seconds + (GameTimeTask.minutes * 60) + (GameTimeTask.hours * 60 * 60);
    }
}
