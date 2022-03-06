package me.redis.bunkers.spectator;

import lombok.Getter;
import me.redis.bunkers.Bunkers;
import me.redis.bunkers.profiles.Profile;
import me.redis.bunkers.profiles.status.PlayerStatus;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Spectator {
    @Getter public List<Player> spectators = new ArrayList<>();

    public void setSpectator(Player player) {
        Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);

        profile.setStatus(PlayerStatus.SPECTATOR);
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setSaturation(20);
        player.setHealth(20);
        player.spigot().setCollidesWithEntities(false);

        Bukkit.getOnlinePlayers().forEach(online -> {
            online.hidePlayer(player);
        });
    }
}
