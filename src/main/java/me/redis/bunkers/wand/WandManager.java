package me.redis.bunkers.wand;

import lombok.Getter;
import me.redis.bunkers.Bunkers;
import me.redis.bunkers.profiles.Profile;
import me.redis.bunkers.protocol.ClaimPillar;
import me.redis.bunkers.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class WandManager implements Listener {
    @Getter private Map<Player, Wand> wands = new HashMap<>();
    private ItemStack wand = new ItemBuilder(Material.GOLD_HOE).setDisplayName("&6Area wand").create();

    public WandManager() {
        Bukkit.getPluginManager().registerEvents(this, Bunkers.getPlugin());
    }

    public Wand getWand(Player player) {
        return wands.get(player);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);

        if (getWand(player) != null && event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().isSimilar(wand)) {
            if (action == Action.LEFT_CLICK_BLOCK) {
                getWand(player).setFirstLocation(event.getClickedBlock().getLocation());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have selected '&7" + getWand(player).getFirstLocation().getBlockX() + ", " + getWand(player).getFirstLocation().getBlockY() + ", " + getWand(player).getFirstLocation().getBlockZ() + "&e' as your first location."));

                if (profile.getFirstPillar() != null) {
                    profile.getFirstPillar().removePillar();
                    profile.setFirstPillar(null);
                }

                ClaimPillar pillar = new ClaimPillar(player, Material.ENDER_STONE, (byte) 0, event.getClickedBlock().getLocation());
                pillar.sendPillar();
                profile.setFirstPillar(pillar);

                event.setCancelled(true);
            } else if (action == Action.RIGHT_CLICK_BLOCK) {
                getWand(player).setSecondLocation(event.getClickedBlock().getLocation());
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have selected '&7" + getWand(player).getSecondLocation().getBlockX() + ", " + getWand(player).getSecondLocation().getBlockY() + ", " + getWand(player).getSecondLocation().getBlockZ() + "&e' as your second location."));

                if (profile.getSecondPillar() != null) {
                    profile.getSecondPillar().removePillar();
                    profile.setSecondPillar(null);
                }

                ClaimPillar pillar = new ClaimPillar(player, Material.ENDER_STONE, (byte) 0, event.getClickedBlock().getLocation());
                pillar.sendPillar();
                profile.setSecondPillar(pillar);

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().equalsIgnoreCase("/wand")) {
            new Wand(event.getPlayer());
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You have been given a wand.");
            event.setCancelled(true);
        }
    }
}
