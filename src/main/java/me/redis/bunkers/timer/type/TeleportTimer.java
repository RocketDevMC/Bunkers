package me.redis.bunkers.timer.type;

import me.redis.bunkers.Bunkers;
import me.redis.bunkers.timer.PlayerTimer;
import me.redis.bunkers.timer.TimerCooldown;
import me.redis.bunkers.utils.LocationUtils;
import me.redis.bunkers.utils.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TeleportTimer extends PlayerTimer implements Listener {
    private final Bunkers plugin;

    public TeleportTimer(Bunkers plugin) {
        super("Home", TimeUnit.SECONDS.toMillis(10L), false);

        this.plugin = plugin;
    }

    /**
     * Gets the {@link Location} this {@link TeleportTimer} will send to.
     *
     *
     * @return the {@link Location}
     */

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

    /**
     * Gets the amount of enemies nearby a {@link Player}.
     *
     * @param player the {@link Player} to get for
     * @param distance the radius to get within
     *
     * @return the amount of players within enemy distance
     */

    /**
     * Teleports a {@link Player} to a {@link Location} with a delay.
     *
     * @param player the {@link Player} to teleport
     * @param location the {@link Location} to teleport to
     * @param millis the time in milliseconds until teleport
     * @param warmupMessage the message to send whilst waiting
     * @param cause the cause for teleporting
     *
     * @return true if {@link Player} was successfully teleported
     */

    /**
     * Cancels a {@link Player}s' teleport process for a given reason.
     *
     * @param player the {@link Player} to cancel for
     * @param reason the reason for cancelling
     */
    public void cancelTeleport(Player player, String reason) {
        UUID uuid = player.getUniqueId();
        if (getRemaining(uuid) > 0L) {
            clearCooldown(uuid);
            if (reason != null && !reason.isEmpty()) {
                player.sendMessage(reason);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        cancelTeleport(event.getPlayer(), ChatColor.YELLOW + "You moved a block, therefore cancelling your teleport.");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            cancelTeleport((Player) entity, ChatColor.YELLOW + "You took damage, therefore cancelling your teleport.");
        }
    }

    @Override
    public void onExpire(UUID userUUID) {
        Player player = Bukkit.getPlayer(userUUID);
        if (player == null) {
            return;
        }

        Location destination = LocationUtils.getLocation(Bunkers.getPlugin().getTeamManager().getByPlayer(player).getSpawnLocation());
        if (destination != null) {
            destination.getChunk();
            player.teleport(destination, PlayerTeleportEvent.TeleportCause.COMMAND);
        }
    }
}
