package me.redis.bunkers.menu.menu;

import me.redis.bunkers.Bunkers;
import me.redis.bunkers.menu.type.ChestMenu;
import me.redis.bunkers.profiles.Profile;
import me.redis.bunkers.utils.ItemBuilder;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.ChatPaginator;

public class EnchanterMenu extends ChestMenu<Bunkers> {
    private Player player;

    public EnchanterMenu(Player player) {
        super(9);
        this.player = player;

        update();
    }

    public void update() {
        inventory.clear();

        inventory.addItem(new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName("&3Sharpness I").setLore("&7&m-------------------", "&7Sharpness I for your sword", "&7&m-------------------", "&ePrice: &a$300").create());
        inventory.addItem(new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName("&3Protection I").setLore("&7&m-------------------", "&7Protection I for your armor", "&7&m-------------------", "&ePrice: &a$1200").create());
        inventory.addItem(new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName("&3Feather Falling IV").setLore("&7&m-------------------", "&7Feather Falling IV for your boots", "&7&m-------------------", "&ePrice: &a$200").create());

        for (int i = 0; i < 9; i++) {
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

                    if (cost > balance) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis costs &e$" + cost + "&c, you only have &e$" + balance + "&c."));
                    } else {
                        if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Sharpness")) {
                            int counter = 0;

                            for (ItemStack itemStack : player.getInventory().getContents()) {
                                if (itemStack != null && itemStack.getType() == Material.DIAMOND_SWORD && counter == 0) {
                                    itemStack.addEnchantment(Enchantment.DAMAGE_ALL, 1);

                                    counter++;
                                }
                            }
                        } else if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Feather")) {
                            int counter = 0;

                            for (ItemStack itemStack : player.getInventory().getContents()) {
                                if (itemStack != null && itemStack.getType() == Material.DIAMOND_BOOTS && counter == 0) {
                                    itemStack.addEnchantment(Enchantment.PROTECTION_FALL, 4);

                                    counter++;
                                }else{
                                    for(ItemStack armor : player.getInventory().getArmorContents()) {
                                        if (armor != null && armor.getType() == Material.DIAMOND_BOOTS) {
                                            armor.addEnchantment(Enchantment.PROTECTION_FALL, 4);
                                        }
                                    }
                                }
                            }
                        } else if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Protection")) {

                            for (ItemStack itemStack : player.getInventory().getContents()) {
                                if (itemStack != null && (itemStack.getType() == Material.DIAMOND_HELMET || itemStack.getType() == Material.DIAMOND_CHESTPLATE || itemStack.getType() == Material.DIAMOND_LEGGINGS || itemStack.getType() == Material.DIAMOND_BOOTS)) {
                                    itemStack.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                                }else{
                                    for(ItemStack armor : player.getInventory().getArmorContents()){
                                        if (armor != null && (armor.getType() == Material.DIAMOND_HELMET || armor.getType() == Material.DIAMOND_CHESTPLATE || armor.getType() == Material.DIAMOND_LEGGINGS || armor.getType() == Material.DIAMOND_BOOTS)) {
                                            armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                                        }
                                    }
                                }
                            }
                        }
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