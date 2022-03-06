package me.redis.bunkers.timer;

import me.redis.bunkers.Bunkers;
import lombok.Getter;
import me.redis.bunkers.timer.type.ProtectionTimer;
import me.redis.bunkers.timer.type.TeleportTimer;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.LinkedHashSet;
import java.util.Set;

public class TimerManager {
    @Getter private final Bunkers plugin;

    @Getter private ProtectionTimer protectionTimer;
    @Getter private TeleportTimer teleportTimer;

    @Getter private final Set<Timer> timers = new LinkedHashSet<>();

    public TimerManager(Bunkers plugin) {
        this.plugin = plugin;

        registerTimer(teleportTimer = new TeleportTimer(plugin));
        registerTimer(protectionTimer = new ProtectionTimer());
    }

    public void registerTimer(Timer timer) {
        timers.add(timer);
        if (timer instanceof Listener) {
            Bukkit.getPluginManager().registerEvents((Listener) timer, plugin);
        }
    }

    public void unregisterTimer(Timer timer) {
        timers.remove(timer);
    }
}