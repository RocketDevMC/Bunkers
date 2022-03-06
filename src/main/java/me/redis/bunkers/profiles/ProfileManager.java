package me.redis.bunkers.profiles;

import lombok.Getter;
import me.redis.bunkers.Bunkers;
import me.redis.bunkers.profiles.Profile;
import me.redis.bunkers.profiles.status.PlayerStatus;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileManager implements Listener {
    public ProfileManager() {
        Bukkit.getPluginManager().registerEvents(this, Bunkers.getPlugin());
    }

    @Getter
    private Map<UUID, Profile> profiles = new HashMap<>();

    public Profile getProfile(UUID uniqueId) {
        return profiles.get(uniqueId);
    }

    public Profile getNotCachedProfile(UUID uniqueId) {
        for (Object object : Bunkers.getPlugin().getProfilesCollection().find()) {
            Document document = (Document) object;

            if (document.get("_id").equals(uniqueId)) {
                return new Profile(uniqueId);
            }
        }

        return null;
    }

    public Profile getProfile(Player player) {
        return getProfile(player.getUniqueId());
    }

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        Profile profile = new Profile(event.getUniqueId());

        if (profile.getName() == null) {
            profile.setName(event.getName());
        }

        profile.save();
        profile.setStatus(PlayerStatus.LOBBY);

        Bunkers.getPlugin().getProfileManager().getProfiles().put(event.getUniqueId(), profile);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(Bunkers.getPlugin(), () -> getProfile(event.getPlayer().getUniqueId()).save());
    }
}
