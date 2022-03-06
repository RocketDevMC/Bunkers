package me.redis.bunkers.menu.menu;

import me.redis.bunkers.Bunkers;
import me.redis.bunkers.menu.type.ChestMenu;
import me.redis.bunkers.profiles.Profile;
import me.redis.bunkers.utils.ItemBuilder;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.ChatPaginator;

public class CombatMenu extends ChestMenu<Bunkers> {
    private Player player;

    public CombatMenu(Player player) {
        super(6 * 9);
        this.player = player;

        update();
    }

    public void update() {
        inventory.clear();

        inventory.setItem(10, new ItemBuilder(Material.DIAMOND_HELMET).setDisplayName("&aDiamond Helmet").setLore("&7&m-------------------", "&71 x Diamond Helmet", "&7&m-------------------", "&7Price: &a$50").create());
        inventory.setItem(11, new ItemBuilder(Material.BOW).setDisplayName("&aBow").setLore("&7&m-------------------", "&71 x Bow", "&7&m-------------------", "&7Price: &a$150").create());
        inventory.setItem(12, new ItemBuilder(Material.ARROW).setDisplayName("&aArrows").setLore("&7&m-------------------", "&71 x Arrow", "  &e- &aRight click to buy 16", "&7&m-------------------", "&7Price: &a$10").create());
        inventory.setItem(14, new ItemBuilder(Material.POTION).setDurability(8226).setDisplayName("&aSpeed II Potion").setLore("&7&m-------------------", "&71 x Speed II Potion", "&7&m-------------------", "&7Price: &a$15").create());
        inventory.setItem(15, new ItemBuilder(Material.POTION).setDurability(8227).setDisplayName("&aFire Resistant Potion").setLore("&7&m-------------------", "&71 x Fire Resistance Potion", "&7&m-------------------", "&7Price: &a$25").create());
        inventory.setItem(23, new ItemBuilder(Material.POTION).setDurability(16421).setDisplayName("&aInstant Health II Potion").setLore("&7&m-------------------", "&71 x Instant Health II Potion", "  &e- &aRight click fill your inventory", "&7&m-------------------", "&7Price: &a$5").create());
        inventory.setItem(21, new ItemBuilder(Material.ENDER_PEARL).setDisplayName("&aEnder Pearls").setLore("&7&m-------------------", "&71 x Ender Pearl", "  &e- &aRight click to buy 16", "&7&m-------------------", "&7Price: &a$25").create());
        inventory.setItem(19, new ItemBuilder(Material.DIAMOND_CHESTPLATE).setDisplayName("&aDiamond Chestplate").setLore("&7&m-------------------", "&71 x Diamond Chestplate", "&7&m-------------------", "&7Price: &a$200").create());
        inventory.setItem(20, new ItemBuilder(Material.DIAMOND).setDisplayName("&aFull Diamond Set").setLore("&7&m-------------------", "&71 x Full Diamond Set", "&7&m-------------------", "&7Price: &a$600").create());
        inventory.setItem(28, new ItemBuilder(Material.DIAMOND_LEGGINGS).setDisplayName("&aDiamond Leggings").setLore("&7&m-------------------", "&71 x Diamond Leggings", "&7&m-------------------", "&7Price: &a$150").create());
        inventory.setItem(37, new ItemBuilder(Material.DIAMOND_BOOTS).setDisplayName("&aDiamond Boots").setLore("&7&m-------------------", "&71 x Diamond Boots", "&7&m-------------------", "&7Price: &a$65").create());
        inventory.setItem(39, new ItemBuilder(Material.POTION).setDisplayName("&aAntidote").setLore("&7&m-------------------", "&71 x Antidote", "&7&m-------------------", "&7Price: &a$150").create());
        inventory.setItem(41, new ItemBuilder(Material.COOKED_BEEF).setDisplayName("&aCooked Beef").setLore("&7&m-------------------", "&71 x Beef", "  &e- &aRight click to buy 16", "&7&m-------------------", "&7Price: &a$5").create());
        inventory.setItem(18, new ItemBuilder(Material.DIAMOND_SWORD).setDisplayName("&aDiamond Sword").setLore("&7&m-------------------", "&71 x Diamond Sword", "&7&m-------------------", "&7Price: &a$150").create());

        for (int i = 0; i < 6 * 9; i++) {
            if (inventory.getItem(i) != null) continue;

            inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setDisplayName(" ").create());
        }
    }

    @Override
    public String getTitle() {
        return ChatColor.RED + ChatColor.BOLD.toString() + "Combat Shop";
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        Inventory topInventory = event.getView().getTopInventory();
        if (clickedInventory == null || topInventory == null || !topInventory.equals(inventory)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (topInventory.equals(clickedInventory)) {
            event.setCancelled(true);

            if (event.getCurrentItem().getType() != Material.AIR) {
                if (event.getCurrentItem().getItemMeta() != null && event.getCurrentItem().getItemMeta().getDisplayName() != null && event.getCurrentItem().getType() != Material.STAINED_GLASS_PANE) {
                    Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);
                    int balance = Bunkers.getPlugin().getProfileManager().getProfile(player).getBalance();

                    if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Instant")) {
                        int cost = Integer.parseInt(stripNonDigits(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(4))));

                        if (cost > balance) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis costs &e$" + cost + "&c, you only have &e$" + balance + "&c."));
                            return;
                        }

                        if (event.getClick() == ClickType.RIGHT) {
                            for (ItemStack itemStack : player.getInventory().getContents()) {
                                if (itemStack == null) {
                                    cost += 5;
                                }
                            }

                            if (cost > balance) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis costs &e$" + cost + "&c, you only have &e$" + balance + "&c."));
                                return;
                            }

                            for (int i = 0; i < 36; i++) {
                                if (player.getInventory().getItem(i) == null) {
                                    player.getInventory().setItem(i, new ItemStack(Material.POTION, 1, (short) 16421));
                                }
                            }
                            Bunkers.getPlugin().getProfileManager().getProfile(player).setBalance(Bunkers.getPlugin().getProfileManager().getProfile(player).getBalance() - cost);
                        } else if (event.getClick() == ClickType.LEFT) {
                            if (cost > balance) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis costs &e$" + cost + "&c, you only have &e$" + balance + "&c."));
                                return;
                            }

                            player.getInventory().addItem(new ItemStack(Material.POTION, 1, (short) 16421));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have bought &ea " + WordUtils.capitalizeFully(event.getCurrentItem().getType().name().replace("_", " ")) + " &afor &e$" + cost + "&a."));
                            profile.setBalance(profile.getBalance() - cost);
                        }
                        return;
                    }

                    if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Ender")) {
                        int cost = Integer.parseInt(stripNonDigits(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(4))));

                        if (event.getClick() == ClickType.RIGHT) {
                            cost = cost * 16;

                            if (cost > balance) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis costs &e$" + cost + "&c, you only have &e$" + balance + "&c."));
                                return;
                            }

                            player.getInventory().addItem(new ItemStack(event.getCurrentItem().getType(), 16));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have bought &ea " + WordUtils.capitalizeFully(event.getCurrentItem().getType().name().replace("_", " ")) + " &afor &e$" + cost + "&a."));
                            profile.setBalance(profile.getBalance() - cost);
                        } else if (event.getClick() == ClickType.LEFT) {
                            if (cost > balance) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis costs &e$" + cost + "&c, you only have &e$" + balance + "&c."));
                                return;
                            }

                            player.getInventory().addItem(new ItemStack(event.getCurrentItem().getType()));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have bought &ea " + WordUtils.capitalizeFully(event.getCurrentItem().getType().name().replace("_", " ")) + " &afor &e$" + cost + "&a."));
                            profile.setBalance(profile.getBalance() - cost);
                        }

                        return;
                    }

                    if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Beef")) {
                        int cost = Integer.parseInt(stripNonDigits(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(4))));

                        if (event.getClick() == ClickType.RIGHT) {
                            cost = cost * 16;

                            if (cost > balance) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis costs &e$" + cost + "&c, you only have &e$" + balance + "&c."));
                                return;
                            }

                            player.getInventory().addItem(new ItemStack(event.getCurrentItem().getType(), 16));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have bought &ea " + WordUtils.capitalizeFully(event.getCurrentItem().getType().name().replace("_", " ")) + " &afor &e$" + cost + "&a."));
                            profile.setBalance(profile.getBalance() - cost);
                        } else if (event.getClick() == ClickType.LEFT) {
                            if (cost > balance) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis costs &e$" + cost + "&c, you only have &e$" + balance + "&c."));
                                return;
                            }

                            player.getInventory().addItem(new ItemStack(event.getCurrentItem().getType()));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have bought &ea " + WordUtils.capitalizeFully(event.getCurrentItem().getType().name().replace("_", " ")) + " &afor &e$" + cost + "&a."));
                            profile.setBalance(profile.getBalance() - cost);
                        }
                        return;
                    }

                    if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Arrow")) {
                        int cost = Integer.parseInt(stripNonDigits(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(4))));

                        if (event.getClick() == ClickType.RIGHT) {
                            cost = cost * 16;

                            if (cost > balance) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis costs &e$" + cost + "&c, you only have &e$" + balance + "&c."));
                                return;
                            }

                            player.getInventory().addItem(new ItemStack(event.getCurrentItem().getType(), 16));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have bought &ea " + WordUtils.capitalizeFully(event.getCurrentItem().getType().name().replace("_", " ")) + " &afor &e$" + cost + "&a."));
                            profile.setBalance(profile.getBalance() - cost);
                        } else if (event.getClick() == ClickType.LEFT) {
                            if (cost > balance) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis costs &e$" + cost + "&c, you only have &e$" + balance + "&c."));
                                return;
                            }

                            player.getInventory().addItem(new ItemStack(event.getCurrentItem().getType()));
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have bought &ea " + WordUtils.capitalizeFully(event.getCurrentItem().getType().name().replace("_", " ")) + " &afor &e$" + cost + "&a."));
                            profile.setBalance(profile.getBalance() - cost);
                        }
                        return;
                    }

                    int cost = Integer.parseInt(stripNonDigits(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(3))));

                    if (cost > balance) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThis costs &e$" + cost + "&c, you only have &e$" + balance + "&c."));
                        return;
                    }

                    if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Full")) {
                        player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                        player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                        player.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                        player.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have bought &ea " + "full diamond set" + " &afor &e$" + cost + "&a."));
                        profile.setBalance(profile.getBalance() - cost);
                        return;
                    }

                    if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Antidote")) {
                        player.getInventory().addItem(new ItemBuilder(Material.POTION).setDisplayName("&aAntidote").create());
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have bought &ean " + "antidote" + " &afor &e$" + cost + "&a."));
                        profile.setBalance(profile.getBalance() - cost);
                        return;
                    }

                    player.getInventory().addItem(new ItemStack(event.getCurrentItem().getType(), 1, event.getCurrentItem().getDurability()));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have bought &ea " + WordUtils.capitalizeFully(event.getCurrentItem().getType().name().replace("_", " ")) + " &afor &e$" + cost + "&a."));
                    profile.setBalance(profile.getBalance() - cost);
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
            final CharSequence input /* inspired by seh's comment */) {
        final StringBuilder sb = new StringBuilder(
                input.length() /* also inspired by seh's comment */);
        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);
            if (c > 47 && c < 58) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}