package me.redis.bunkers.events;

import lombok.Getter;
import me.redis.bunkers.game.status.GameStatus;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStatusChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter private GameStatus oldStatus;
    @Getter private GameStatus newStatus;

    public GameStatusChangeEvent(GameStatus oldStatus, GameStatus newStatus) {
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
