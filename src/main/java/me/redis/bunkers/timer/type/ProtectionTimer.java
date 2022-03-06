package me.redis.bunkers.timer.type;

import me.redis.bunkers.timer.PlayerTimer;
import me.redis.bunkers.timer.TimerCooldown;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ProtectionTimer extends PlayerTimer implements Listener {
    public ProtectionTimer() {
        super("Protection", TimeUnit.SECONDS.toMillis(10L), false);
    }


    @Override
    public String getScoreboardPrefix() {
        return ChatColor.DARK_AQUA.toString() + ChatColor.BOLD;
    }

    @Override
    public TimerCooldown clearCooldown(UUID uuid) {
        TimerCooldown runnable = super.clearCooldown(uuid);
        if (runnable != null) {
            return runnable;
        }

        return null;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            if (getRemaining((Player) entity) > 0) {
                event.setCancelled(true);

                if (event instanceof EntityDamageByEntityEvent) {
                    if (((EntityDamageByEntityEvent) event).getDamager() instanceof Player) {
                        ((Player) ((EntityDamageByEntityEvent) event).getDamager()).sendMessage(ChatColor.RED + "That player has pvp timer.");
                    }
                }
            }
        }
    }
}
