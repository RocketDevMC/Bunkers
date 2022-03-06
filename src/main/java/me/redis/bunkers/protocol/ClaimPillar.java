package me.redis.bunkers.protocol;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@Getter
public class ClaimPillar {
    private Player player;
    private Material blockType;
    private byte data;
    private ArrayList<Integer> blocks;
    private Location location;

    public ClaimPillar(Player player, Material blockType, byte data, Location location) {
        this.player = player;
        this.location = location;
        this.blockType = blockType;
        this.data = data;

        blocks = new ArrayList<>();
    }


    public void sendPillar() {
        int x = location.getBlockX();
        int z = location.getBlockZ();

        for (int i = 0; i <= 256; i++) {
            Location location = new Location(this.location.getWorld(), x, i, z);
            if (location.getBlock().getType() == Material.AIR) {
                if (blocks.contains(location.getBlockY())) {
                    player.getPlayer().sendBlockChange(location, blockType, data);
                    player.getPlayer().sendBlockChange(location.add(0, 2, 0), Material.GLASS, (byte) 0);
                } else {
                    player.getPlayer().sendBlockChange(location, Material.GLASS, (byte) 0);
                    blocks.add(location.getBlockY() + 1);
                }
            }
        }
    }

    public void removePillar() {
        int x = location.getBlockX();
        int z = location.getBlockZ();

        for (int i = 0; i <= 256; i++) {
            Location location = new Location(this.location.getWorld(), x, i, z);
            if (location.getBlock().getType() == Material.AIR) {
                player.getPlayer().sendBlockChange(location, Material.AIR, (byte) 0);
            }
        }
    }
}
