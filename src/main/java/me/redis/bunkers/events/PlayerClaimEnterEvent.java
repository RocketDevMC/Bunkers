package me.redis.bunkers.events;

import me.redis.bunkers.team.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerClaimEnterEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private final Player player;
    private final Team fromTeam;
    private final Team toTeam;
    private final Location from;
    private final Location to;

    public PlayerClaimEnterEvent(Player player, Location from, Location to, Team fromTeam, Team toTeam) {
        this.player = player;
        this.from = from;
        this.to = to;
        this.fromTeam = fromTeam;
        this.toTeam = toTeam;
    }

    public Team getFromTeam() {
        return fromTeam;
    }

    public Team getToTeam() {
        return toTeam;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}