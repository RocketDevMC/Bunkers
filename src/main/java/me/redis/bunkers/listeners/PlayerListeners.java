package me.redis.bunkers.listeners;

import me.redis.bunkers.Bunkers;
import me.redis.bunkers.events.GameStatusChangeEvent;
import me.redis.bunkers.game.GameManager;
import me.redis.bunkers.game.status.GameStatus;
import me.redis.bunkers.profiles.Profile;
import me.redis.bunkers.profiles.status.PlayerStatus;
import me.redis.bunkers.team.Team;
import me.redis.bunkers.team.TeamManager;
import me.redis.bunkers.utils.ItemBuilder;
import me.redis.bunkers.utils.LocationUtils;
import net.minecraft.server.v1_7_R4.EnumClickAction;
import net.minecraft.server.v1_7_R4.Village;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerListeners implements Listener {
    private Bunkers bunkers = Bunkers.getPlugin();

    ItemStack red = new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(14).setDisplayName("&c&lRed Team").create();
    ItemStack blue = new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(3).setDisplayName("&9&lBlue Team").create();
    ItemStack green = new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(5).setDisplayName("&a&lGreen Team").create();
    ItemStack yellow = new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(4).setDisplayName("&e&lYellow Team").create();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        //NOTE TO SELF, ADD CHEATBREAKER CHECK. Update 4/14/2020: LMAOOOOOO

        event.setJoinMessage(null);

        if (bunkers.getInformationManager().getInformation().getLobbyLocation() != null) {
            player.teleport(LocationUtils.getLocation(bunkers.getInformationManager().getInformation().getLobbyLocation()));
        } else {
            player.sendMessage(ChatColor.RED + "You must set the spawn location.");
        }

        if (bunkers.getGameManager().canBePlayed()) {
            if (bunkers.getInformationManager().getInformation().getMinPlayers() <= Bukkit.getOnlinePlayers().size() && bunkers.getGameManager().getStatus() == GameStatus.WAITING) {
                bunkers.getGameManager().startCooldown();
            }

            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setHealth(20);
            player.setSaturation(20);
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(false);

            player.getInventory().setItem(1, red);
            player.getInventory().setItem(3, blue);
            player.getInventory().setItem(5, green);
            player.getInventory().setItem(7, yellow);
        } else {
            player.sendMessage(ChatColor.RED + "You must set-up the bunkers.");
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();
        Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);


        if(profile.getStatus().equals(PlayerStatus.SPECTATOR)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot hit players while a spectator.");
        }

        if (itemStack != null && (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) {
            if (itemStack.isSimilar(red)) {
                Team team = Bunkers.getPlugin().getTeamManager().getByName("Red");

                if (team.getMembers().size() >= 5) {
                    player.sendMessage(ChatColor.RED + "The red team is full.");
                    return;
                }

                if (Bunkers.getPlugin().getTeamManager().getByPlayer(player) != null) {
                    Bunkers.getPlugin().getTeamManager().getByPlayer(player).getMembers().remove(player.getUniqueId());
                }

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have joined the &cred &eteam."));
                team.getMembers().add(player.getUniqueId());
            } else if (itemStack.isSimilar(blue)) {
                Team team = Bunkers.getPlugin().getTeamManager().getByName("Blue");

                if (team.getMembers().size() >= 5) {
                    player.sendMessage(ChatColor.RED + "The blue team is full.");
                    return;
                }

                if (Bunkers.getPlugin().getTeamManager().getByPlayer(player) != null) {
                    Bunkers.getPlugin().getTeamManager().getByPlayer(player).getMembers().remove(player.getUniqueId());
                }

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have joined the &9blue &eteam."));
                team.getMembers().add(player.getUniqueId());
            } else if (itemStack.isSimilar(green)) {
                Team team = Bunkers.getPlugin().getTeamManager().getByName("Green");

                if (team.getMembers().size() >= 5) {
                    player.sendMessage(ChatColor.RED + "The green team is full.");
                    return;
                }

                if (Bunkers.getPlugin().getTeamManager().getByPlayer(player) != null) {
                    Bunkers.getPlugin().getTeamManager().getByPlayer(player).getMembers().remove(player.getUniqueId());
                }

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have joined the &agreen &eteam."));
                team.getMembers().add(player.getUniqueId());
            } else if (itemStack.isSimilar(yellow)) {
                Team team = Bunkers.getPlugin().getTeamManager().getByName("Yellow");

                if (team.getMembers().size() >= 5) {
                    player.sendMessage(ChatColor.RED + "The yellow team is full.");
                    return;
                }

                if (Bunkers.getPlugin().getTeamManager().getByPlayer(player) != null) {
                    Bunkers.getPlugin().getTeamManager().getByPlayer(player).getMembers().remove(player.getUniqueId());
                }

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have joined the &6yellow &eteam."));
                team.getMembers().add(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onAsyncJoin(AsyncPlayerPreLoginEvent event) {
        if (Bukkit.getServer().getOnlinePlayers().size() >= 20) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "The match is full.");
            return;
        }

        if (bunkers.getGameManager().getStatus() != GameStatus.WAITING) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "You can't join a started match.");
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (bunkers.getGameManager().getStatus() != GameStatus.PLAYING) {
            Player player = event.getPlayer();

            if (player.isOp() && player.getGameMode() == GameMode.CREATIVE) {
                event.setCancelled(false);
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (bunkers.getGameManager().getStatus() != GameStatus.PLAYING) {
            Player player = event.getPlayer();

            if (player.isOp() && player.getGameMode() == GameMode.CREATIVE) {
                event.setCancelled(false);
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (player.isOp() && player.getGameMode() == GameMode.CREATIVE) return;

        if (bunkers.getGameManager().getStatus() != GameStatus.PLAYING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChange(GameStatusChangeEvent event){
        if(event.getNewStatus() == GameStatus.ENDING){
            Bukkit.getOnlinePlayers().forEach(player ->{
                Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);
                profile.save();
            });
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();
        Team team = Bunkers.getPlugin().getTeamManager().getByPlayer(player);
        Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);

        team.setDtr(team.getDtr() - 1);
        profile.setDeaths(profile.getDeaths() + 1);

        if (event.getEntity().getKiller() != null) {
            Bunkers.getPlugin().getProfileManager().getProfile(event.getEntity().getKiller()).setKills(Bunkers.getPlugin().getProfileManager().getProfile(event.getEntity().getKiller()).getKills() + 1);
            Bunkers.getPlugin().getProfileManager().getProfile(event.getEntity().getKiller()).setMatchKills(Bunkers.getPlugin().getProfileManager().getProfile(event.getEntity().getKiller()).getMatchKills() + 1);
        }

        if (team.getDtr() <= 0) {
            Bunkers.getPlugin().getSpectatorManager().setSpectator(player);
        } else {
            player.spigot().respawn();
        }

        for (UUID dead : Bunkers.getPlugin().getTeamManager().getByPlayer(player).getMembers()) {
            System.out.println("Status' is: " + Bunkers.getPlugin().getProfileManager().getProfile(dead).getStatus());
            if(Bunkers.getPlugin().getProfileManager().getProfile(dead).getStatus() == PlayerStatus.SPECTATOR){
                Bunkers.getPlugin().getGameManager().getTeamsLeft().remove(team);
                Bukkit.broadcastMessage(ChatColor.RED.toString() + Bunkers.getPlugin().getTeamManager().getByPlayer(player).getName() + " team has been eliminated!");
            }
        }
        if(Bunkers.getPlugin().getGameManager().getTeamsLeft().size() == 1){
            new GameManager().setWon(Bunkers.getPlugin().getGameManager().getTeamsLeft().listIterator().next());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (Bunkers.getPlugin().getTeamManager().getByPlayer(player) != null) {
            Team team = Bunkers.getPlugin().getTeamManager().getByPlayer(player);

            if (bunkers.getGameManager().getStatus() == GameStatus.WAITING) {
                team.getMembers().remove(player.getUniqueId());
            } else if (bunkers.getGameManager().getStatus() == GameStatus.PLAYING) {
                team.setDtr(team.getDtr() - 1);
            } else if (bunkers.getGameManager().getStatus() == GameStatus.STARTING) {
                team.getMembers().remove(player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(LocationUtils.getLocation(Bunkers.getPlugin().getTeamManager().getByPlayer(event.getPlayer()).getSpawnLocation()));
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
/*
        if (((EntityDamageByEntityEvent) event).getDamager() instanceof Player && event.getEntity() instanceof Villager){
            Player damager = (Player) ((EntityDamageByEntityEvent) event).getDamager();

            Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(damager);

            if(profile.getStatus().equals(PlayerStatus.SPECTATOR)){
                event.setCancelled(true);
            }
        }*/

        if (Bunkers.getPlugin().getGameManager().getStatus() != GameStatus.PLAYING) {
            event.setCancelled(true);
        } else {
            if (event instanceof EntityDamageByEntityEvent) {
                if (((EntityDamageByEntityEvent) event).getDamager() instanceof Player && event.getEntity() instanceof Player) {
                    Player damaged = (Player) event.getEntity();
                    Player damager = (Player) ((EntityDamageByEntityEvent) event).getDamager();

                    Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(damager);

                    if (profile.getStatus().equals(PlayerStatus.SPECTATOR)) {
                        event.setCancelled(true);
                        System.out.println("Canceled Damage");
                        damager.sendMessage(ChatColor.RED + "You cannot hit players while a spectator.");
                    }

                    if (Bunkers.getPlugin().getTeamManager().getByPlayer(damaged) == Bunkers.getPlugin().getTeamManager().getByPlayer(damager)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMotd(ServerListPingEvent event) {
        event.setMotd(Bukkit.getServerName() + ";" + Bunkers.getPlugin().getGameManager().getStatus().name() + ";" + Bunkers.getPlugin().getKoth().getCapSeconds() + ";" + Bukkit.getOnlinePlayers().size());
    }
}
