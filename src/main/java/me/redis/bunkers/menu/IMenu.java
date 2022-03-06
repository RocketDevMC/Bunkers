package me.redis.bunkers.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public interface IMenu extends InventoryHolder {

    default String getTitle() { return getInventory().getType().getDefaultTitle(); }

    default void open(Player player) { player.openInventory(getInventory()); };

    void onInventoryClick(InventoryClickEvent event);

    default void onInventoryDrag(InventoryDragEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (topInventory.equals(getInventory())) {
            event.setCancelled(true);
        }
    }

    default void onInventoryClose(InventoryCloseEvent event) {};
}