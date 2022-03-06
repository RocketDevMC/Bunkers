package me.redis.bunkers.timer;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import me.redis.bunkers.events.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PlayerTimer extends Timer {

    protected final boolean persistable;
    protected final Map<UUID, TimerCooldown> cooldowns = new ConcurrentHashMap<>();

    public PlayerTimer(String name, long defaultCooldown, boolean persistable) {
        super(name, defaultCooldown);
        
        this.persistable = persistable;
    }

    public PlayerTimer(String name, long defaultCooldown) {
        this(name, defaultCooldown, true);
    }
    
    public void onExpire(UUID userUUID) { }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimerExpireLoadReduce(TimerExpireEvent event) {
        if (event.getTimer() == this) {
            Optional<UUID> optionalUserUUID = event.getUserUUID();
            if (optionalUserUUID.isPresent()) {
                UUID userUUID = optionalUserUUID.get();
                onExpire(userUUID);
                clearCooldown(userUUID);
            }
        }
    }

    public void clearCooldown(Player player) {
        clearCooldown(player.getUniqueId());
    }

    public TimerCooldown clearCooldown(UUID playerUUID) {
        TimerCooldown runnable = cooldowns.remove(playerUUID);
        if (runnable != null) {
            runnable.cancel();
            Bukkit.getPluginManager().callEvent(new TimerClearEvent(playerUUID, this));
            return runnable;
        }
        return null;
    }

    public boolean isPaused(Player player) {
        return isPaused(player.getUniqueId());
    }

    public boolean isPaused(UUID playerUUID) {
        TimerCooldown runnable = cooldowns.get(playerUUID);
        return runnable != null && runnable.isPaused();
    }

    public void setPaused(UUID playerUUID, boolean paused) {
        TimerCooldown runnable = cooldowns.get(playerUUID);
        if (runnable != null && runnable.isPaused() != paused) {
            TimerPauseEvent event = new TimerPauseEvent(playerUUID, this, paused);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                runnable.setPaused(paused);
            }
        }
    }

    public long getRemaining(Player player) {
        return getRemaining(player.getUniqueId());
    }

    public long getRemaining(UUID playerUUID) {
        TimerCooldown runnable = cooldowns.get(playerUUID);
        return runnable == null ? 0L : runnable.getRemaining();
    }

    public boolean setCooldown(@Nullable Player player, UUID playerUUID) {
        return setCooldown(player, playerUUID, defaultCooldown, false);
    }

    public boolean setCooldown(@Nullable Player player, UUID playerUUID, long duration, boolean overwrite) {
        return setCooldown(player, playerUUID, duration, overwrite, null);
    }

    public boolean setCooldown(@Nullable Player player, UUID playerUUID, long duration, boolean overwrite, @Nullable Predicate<Long> currentCooldownPredicate) {
        TimerCooldown runnable = duration > 0L ? cooldowns.get(playerUUID) : this.clearCooldown(playerUUID);
        if (runnable != null) {
            long remaining = runnable.getRemaining();
            if (!overwrite && remaining > 0L && duration <= remaining) {
                return false;
            }

            TimerExtendEvent event = new TimerExtendEvent(player, playerUUID, this, remaining, duration);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }

            boolean flag = true;
            if (currentCooldownPredicate != null) {
                flag = currentCooldownPredicate.apply(remaining);
            }

            if (flag) {
                runnable.setRemaining(duration);
            }

            return flag;
        } else {
            Bukkit.getPluginManager().callEvent(new TimerStartEvent(player, playerUUID, this, duration));
            runnable = new TimerCooldown(this, playerUUID, duration);
        }

        cooldowns.put(playerUUID, runnable);
        return true;
    }
}