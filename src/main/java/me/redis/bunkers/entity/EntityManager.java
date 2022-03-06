package me.redis.bunkers.entity;

import me.redis.bunkers.Bunkers;
import me.redis.bunkers.entity.types.BuildEntity;
import me.redis.bunkers.entity.types.CombatEntity;
import me.redis.bunkers.entity.types.EnchantEntity;
import me.redis.bunkers.entity.types.SellEntity;
import me.redis.bunkers.menu.MenuListener;
import me.redis.bunkers.menu.menu.BuildMenu;
import me.redis.bunkers.menu.menu.CombatMenu;
import me.redis.bunkers.menu.menu.EnchanterMenu;
import me.redis.bunkers.menu.menu.SellMenu;
import me.redis.bunkers.team.Team;
import me.redis.bunkers.utils.BukkitUtils;
import me.redis.bunkers.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EntityManager implements Listener {
    public Map<Entity, ShopType> killedVillagers = new HashMap<>();

    public EntityManager() {
        Bukkit.getPluginManager().registerEvents(this, Bunkers.getPlugin());
        Bukkit.getPluginManager().registerEvents(new MenuListener(), Bunkers.getPlugin());
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if (event.getRightClicked() != null && event.getRightClicked() instanceof Villager && ((Villager) event.getRightClicked()).getCustomName() != null) {
            String name = ChatColor.stripColor(((Villager) event.getRightClicked()).getCustomName());

            if (name.toLowerCase().contains("combat")) {
                new CombatMenu(player).open(player);
            } else if (name.toLowerCase().contains("sell")) {
                new SellMenu(player).open(player);
            } else if (name.toLowerCase().contains("build")) {
                new BuildMenu(player).open(player);
            } else if (name.toLowerCase().contains("enchant")) {
                new EnchanterMenu(player).open(player);
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Villager && ((Villager) event.getEntity()).getCustomName() != null && ((Villager) event.getEntity()).getCustomName().toLowerCase().contains("dead"))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Villager && event.getEntity().getCustomName() != null) {
            if (event.getEntity().getCustomName().toLowerCase().contains("combat") || event.getEntity().getCustomName().toLowerCase().contains("enchant") || event.getEntity().getCustomName().toLowerCase().contains("build") || event.getEntity().getCustomName().toLowerCase().contains("sell")) {
                final int[] counter = {15};
                ShopType type = null;

                if (event.getEntity().getCustomName().toLowerCase().contains("combat")) type = ShopType.COMBAT;
                else if (event.getEntity().getCustomName().toLowerCase().contains("enchant")) type = ShopType.ENCHANT;
                else if (event.getEntity().getCustomName().toLowerCase().contains("sell")) type = ShopType.SELL;
                else if (event.getEntity().getCustomName().toLowerCase().contains("build")) type = ShopType.BUILD;

                killedVillagers.put(event.getEntity(), type);

                Villager villager = (Villager) event.getEntity().getLocation().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.VILLAGER);
                villager.setFallDistance(0);
                villager.setRemoveWhenFarAway(false);
                villager.setAdult();
                villager.setProfession(Villager.Profession.BLACKSMITH);
                villager.setCanPickupItems(false);
                villager.setMaxHealth(40);
                villager.setHealth(40);
                villager.setCustomNameVisible(true);
                villager.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, -6, true), true);

                new BukkitRunnable() {
                    @Override public void run() {
                        if (counter[0] <= 0) {
                            Team team = Bunkers.getPlugin().getTeamManager().getByColor(ChatColor.getByChar(ChatColor.getLastColors(event.getEntity().getCustomName()).replace("§", "").replace("&", "")));
                            if (killedVillagers.get(event.getEntity()) == ShopType.SELL) {
                                new SellEntity(team, LocationUtils.getLocation(team.getSellShop()));
                            } else if (killedVillagers.get(event.getEntity()) == ShopType.BUILD) {
                                new BuildEntity(team, LocationUtils.getLocation(team.getBuildShop()));
                            } else if (killedVillagers.get(event.getEntity()) == ShopType.ENCHANT) {
                                new EnchantEntity(team, LocationUtils.getLocation(team.getEnchantShop()));
                            } else if (killedVillagers.get(event.getEntity()) == ShopType.COMBAT) {
                                new CombatEntity(team, LocationUtils.getLocation(team.getCombatShop()));
                            }

                            killedVillagers.remove(event.getEntity());
                            villager.remove();

                            cancel();
                            return;
                        }

                        villager.setCustomName(ChatColor.RED + "Dead for" + ChatColor.GRAY + ": " + ChatColor.WHITE + BukkitUtils.niceTime(counter[0], false));
                        counter[0]--;
                    }
                }.runTaskTimer(Bunkers.getPlugin(), 0L, 20L);
            }
        }
    }
}
