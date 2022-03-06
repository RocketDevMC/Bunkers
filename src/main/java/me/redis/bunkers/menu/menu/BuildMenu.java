package me.redis.bunkers.menu.menu;

import me.redis.bunkers.Bunkers;
import me.redis.bunkers.menu.type.ChestMenu;
import me.redis.bunkers.profiles.Profile;
import me.redis.bunkers.utils.ItemBuilder;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.ChatPaginator;

public class BuildMenu extends ChestMenu<Bunkers> {
    private Player player;

    public BuildMenu(Player player) {
        super(6 * 9);
        this.player = player;

        update();
    }

    public void update() {
        inventory.clear();

        inventory.addItem(new ItemBuilder(Material.CHEST).setAmount(16).setDisplayName("&aChest").setLore("&7&m-------------------", "&716 x Chest", "&7&m-------------------", "&ePrice: &a$50").create());
        inventory.addItem(new ItemBuilder(Material.STONE).setAmount(16).setDisplayName("&aStone").setLore("&7&m-------------------", "&716 x Stone", "&7&m-------------------", "&ePrice: &a$50").create());
        inventory.addItem(new ItemBuilder(Material.COBBLESTONE).setAmount(16).setDisplayName("&aCobblestone").setLore("&7&m-------------------", "&716 x Cobblestone", "&7&m-------------------", "&ePrice: &a$50").create());
        inventory.addItem(new ItemBuilder(Material.FENCE_GATE).setAmount(16).setDisplayName("&aFence Gate").setLore("&7&m-------------------", "&716 x Fence Gate", "&7&m-------------------", "&ePrice: &a$50").create());
        inventory.addItem(new ItemBuilder(Material.STONE_PLATE).setAmount(16).setDisplayName("&aPressure Plate").setLore("&7&m-------------------", "&716 x Pressure Plate", "&7&m-------------------", "&ePrice: &a$50").create());
        inventory.addItem(new ItemBuilder(Material.LADDER).setAmount(16).setDisplayName("&aLadder").setLore("&7&m-------------------", "&716 x Ladder", "&7&m-------------------", "&ePrice: &a$50").create());
        inventory.addItem(new ItemBuilder(Material.STONE_BUTTON).setAmount(16).setDisplayName("&aStone Button").setLore("&7&m-------------------", "&716 x Stone Button", "&7&m-------------------", "&ePrice: &a$50").create());
        inventory.addItem(new ItemBuilder(Material.TRAPPED_CHEST).setAmount(16).setDisplayName("&aTrapped Chest").setLore("&7&m-------------------", "&716 x Trapped Chest", "&7&m-------------------", "&ePrice: &a$50").create());
        inventory.addItem(new ItemBuilder(Material.GLASS).setAmount(16).setDisplayName("&aGlass").setLore("&7&m-------------------", "&716 x Glass", "&7&m-------------------", "&ePrice: &a$50").create());

        inventory.setItem(48, new ItemBuilder(Material.DIAMOND_PICKAXE).setDisplayName("&aDiamond Pickaxe").setLore("&7&m-------------------", "&71 x Diamond Pickaxe", "&7&m-------------------", "&ePrice: &a$75").create());
        inventory.setItem(49, new ItemBuilder(Material.DIAMOND_AXE).setDisplayName("&aDiamond Axe").setLore("&7&m-------------------", "&71 x Diamond Axe", "&7&m-------------------", "&ePrice: &a$65").create());
        inventory.setItem(50, new ItemBuilder(Material.DIAMOND_SPADE).setDisplayName("&aDiamond Shovel").setLore("&7&m-------------------", "&71 x Diamond Shovel", "&7&m-------------------", "&ePrice: &a$55").create());

        for (int i = 0; i < 6*9; i++) {
            if (inventory.getItem(i) != null) continue;

            inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setDisplayName(" ").create());
        }
    }

    @Override
    public String getTitle() {
        return ChatColor.RED + ChatColor.BOLD.toString() + "Block Shop";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (clickedInventory == null || topInventory == null || !topInventory.equals(inventory)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);

        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);

            if (event.getCurrentItem().getType() != Material.AIR) {
                if (event.getCurrentItem().getItemMeta() != null && event.getCurrentItem().getType() != Material.STAINED_GLASS_PANE) {
                    int balance = profile.getBalance();
                    int cost = Integer.parseInt(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(3).split(" ")[1].replace("$", "")));
                    int amount = Integer.parseInt(stripNonDigits(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(1))));

                    if (cost > balance) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis costs &e$" + cost + "&c, you only have &e$" + balance + "&c."));
                    } else {
                        player.getInventory().addItem(new ItemStack(event.getCurrentItem().getType(), amount));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have bought &ea " + WordUtils.capitalizeFully(event.getCurrentItem().getType().name().replace("_", " ")) + " &afor &e$" + cost + "&a."));
                        profile.setBalance(profile.getBalance() - cost);
                    }
                }
            }
        } else if (!topInventory.equals(clickedInventory) && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onInventoryDrag(InventoryDragEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        if (topInventory.equals(getInventory())) {
            event.setCancelled(true);
        }
    }

    public static String stripNonDigits(
            final CharSequence input /* inspired by seh's comment */){
        final StringBuilder sb = new StringBuilder(
                input.length() /* also inspired by seh's comment */);
        for(int i = 0; i < input.length(); i++){
            final char c = input.charAt(i);
            if(c > 47 && c < 58){
                sb.append(c);
            }
        }
        return sb.toString();
    }
}