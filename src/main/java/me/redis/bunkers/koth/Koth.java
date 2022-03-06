package me.redis.bunkers.koth;

import lombok.Getter;
import lombok.Setter;
import me.redis.bunkers.Bunkers;
import me.redis.bunkers.tasks.GameTimeTask;
import me.redis.bunkers.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class Koth  {
    @Getter @Setter private int seconds = 600;
    @Getter @Setter private int capSeconds = 600;
    @Getter @Setter Player controller;

    public Koth() {
        new BukkitRunnable() {
            @Override public void run() {
                if (GameTimeTask.getNumOfSeconds() == 600) {
                    setSeconds(300);
                    if (capSeconds > 300) {
                        setCapSeconds(300);
                    }
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7[&6KingOfTheHill&7] &eThe &9Koth &etime has been decreased. &7(05:00)"));
                }
            }
        }.runTaskTimerAsynchronously(Bunkers.getPlugin(), 0L, 20L);
    }

    public boolean isInsideArea(Location location) {
        return Bunkers.getPlugin().getInformationManager().getInformation().getKothCuboid().contains(location);
    }
}
