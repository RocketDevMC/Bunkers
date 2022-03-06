package me.redis.bunkers.menu;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.PacketPlayOutOpenWindow;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class InventoryUtil {

    public static void changeTitle(Player player, String title) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        int windowId = entityPlayer.activeContainer.windowId;

        PacketPlayOutOpenWindow packet = new PacketPlayOutOpenWindow(windowId, 0, title, player.getOpenInventory().getTopInventory().getSize(), true);

        entityPlayer.playerConnection.sendPacket(packet);

        player.updateInventory();
    }
}