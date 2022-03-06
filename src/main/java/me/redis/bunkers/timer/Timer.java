package me.redis.bunkers.timer;

import lombok.Getter;

public abstract class Timer {

    @Getter protected final String name;
    @Getter protected final long defaultCooldown;

    public Timer(String name, long defaultCooldown) {
        this.name = name;
        this.defaultCooldown = defaultCooldown;
    }

    public abstract String getScoreboardPrefix();

    public final String getDisplayName() { return getScoreboardPrefix() + name; }
}