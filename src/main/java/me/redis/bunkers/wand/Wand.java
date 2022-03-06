package me.redis.bunkers.wand;

import lombok.Getter;
import lombok.Setter;
import me.redis.bunkers.Bunkers;
import me.redis.bunkers.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@Getter
public class Wand {
    @Setter private Location firstLocation;
    @Setter private Location secondLocation;

    public Wand(Player player) {
        if (Bunkers.getPlugin().getWandManager().getWand(player) != null) {
            player.getInventory().addItem(new ItemBuilder(Material.GOLD_HOE).setDisplayName("&6Area wand").create());
        } else {
            player.getInventory().addItem(new ItemBuilder(Material.GOLD_HOE).setDisplayName("&6Area wand").create());
            Bunkers.getPlugin().getWandManager().getWands().put(player, this);
        }
    }
}
