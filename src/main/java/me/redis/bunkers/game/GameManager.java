package me.redis.bunkers.game;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.redis.bunkers.Bunkers;
import me.redis.bunkers.events.GameStatusChangeEvent;
import me.redis.bunkers.game.status.GameStatus;
import me.redis.bunkers.profiles.Profile;
import me.redis.bunkers.profiles.status.PlayerStatus;
import me.redis.bunkers.tasks.GameTimeTask;
import me.redis.bunkers.tasks.KothTask;
import me.redis.bunkers.team.Team;
import me.redis.bunkers.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class GameManager {
    @Getter
    private Bunkers bunkers = Bunkers.getPlugin();
    @Getter
    private GameStatus status;
    @Getter
    @Setter
    private Boolean started = false;
    @Getter
    @Setter
    private long gameTime;
    @Getter
    @Setter
    private boolean event = false;
    @Getter
    private int scoreboard = 0;
    @Getter
    @Setter
    private String eventName = "Custom Event";
    @Getter
    @Setter
    private Team winnerTeam;
    @Getter
    @Setter
    public ArrayList<Team> teamsLeft = new ArrayList<>();
    private World world;


    public GameManager() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Bunkers.getPlugin(), () -> {
            if (event) {
                if (scoreboard == 0) {
                    scoreboard = 1;
                } else if (scoreboard == 1) {
                    scoreboard = 0;
                }
            }
        }, 20L, 20 * 3L);

    }

    public void setStatus(GameStatus status) {
        Bukkit.getPluginManager().callEvent(new GameStatusChangeEvent(this.status, status));
        this.status = status;
    }

    public boolean canBePlayed() {
        return bunkers.getTeamManager().canBePlayed();
    }

    @Getter private final int[] cooldown = {15};
    @Getter private final int[] endtimer = {100};

    public void startCooldown() {
        setStatus(GameStatus.STARTING);
        setStarted(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (cooldown[0] <= 0) {
                    startGame();

                    cancel();
                } else {
                    if (cooldown[0] % 5 == 0) {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&eThe match will start in &9" + cooldown[0] + " seconds&e."));
                    }
                }

                cooldown[0]--;
            }
        }.runTaskTimer(Bunkers.getPlugin(), 20L, 20L);
    }

    public void startGame() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Bunkers.getPlugin().getTeamManager().getByPlayer(player) != null) {
                Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);
                Team team = Bunkers.getPlugin().getTeamManager().getByPlayer(player);

                player.teleport(LocationUtils.getLocation(team.getSpawnLocation()));
                player.getInventory().clear();
                player.getInventory().setItem(0, new ItemStack(Material.STONE_PICKAXE));
                player.getInventory().setItem(1, new ItemStack(Material.STONE_AXE));
                setStatus(GameStatus.PLAYING);
                profile.setStatus(PlayerStatus.PLAYING);
                profile.setGamesPlayed(profile.getGamesPlayed() + 1);
                profile.setBalance(500);
                profile.save();
                team.setDtr(team.getMembers().size());
                teamsLeft.add(team);

                if (team.getDtr() == 0 && team.getMembers().size() == 0) {
                    teamsLeft.remove(team);
                }

                player.sendMessage(ChatColor.GREEN + "The match has started...");
            } else {
                player.kickPlayer(ChatColor.RED + "You must have a team to play.");
            }
        }

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(Bunkers.getPlugin(), () -> {
            getServer().getOnlinePlayers().forEach(player -> {
                Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);

                if (profile.getStatus() == PlayerStatus.PLAYING) {
                    profile.setBalance(profile.getBalance() + 3);
                }
            });
        }, 0L, 20 * 3L);

        new GameTimeTask().runTaskTimerAsynchronously(Bunkers.getPlugin(), 20L, 20L);
        Bukkit.getScheduler().runTaskLater(Bunkers.getPlugin(), () -> new KothTask().runTaskTimer(Bunkers.getPlugin(), 20L, 20L), 20 * 60 * 5);

    }

    public void setWon(Team team) {
        Bunkers.getPlugin().getGameManager().setWinnerTeam(team);

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "The " + team.getColor() + team.getName() + " &ehas won the &9Bunkers&e!"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "The " + team.getColor() + team.getName() + " &ehas won the &9Bunkers&e!"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "The " + team.getColor() + team.getName() + " &ehas won the &9Bunkers&e!"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "The " + team.getColor() + team.getName() + " &ehas won the &9Bunkers&e!"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "The " + team.getColor() + team.getName() + " &ehas won the &9Bunkers&e!"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "The " + team.getColor() + team.getName() + " &ehas won the &9Bunkers&e!"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "The " + team.getColor() + team.getName() + " &ehas won the &9Bunkers&e!"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "The " + team.getColor() + team.getName() + " &ehas won the &9Bunkers&e!"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "The " + team.getColor() + team.getName() + " &ehas won the &9Bunkers&e!"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "The " + team.getColor() + team.getName() + " &ehas won the &9Bunkers&e!"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "The " + team.getColor() + team.getName() + " &ehas won the &9Bunkers&e!"));

        Bunkers.getPlugin().getGameManager().getWinnerTeam().getMembers().forEach(player -> {
            Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);
            profile.setGamesWon(profile.getGamesWon() + 1);
        });

        Bunkers.getPlugin().getGameManager().setStatus(GameStatus.ENDING);
        startEndTimer();
    }
    public void startEndTimer() {

        new BukkitRunnable() {
            @Override
            public void run() {
                if (endtimer[0] <= 0) {
                    Bukkit.getServer().shutdown();

                    cancel();
                } else {
                    if (endtimer[0] % 5 == 0) {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&eThe server will close in &9" + endtimer[0] + " seconds&e."));
                    }
                }

                endtimer[0]--;
            }
        }.runTaskTimer(Bunkers.getPlugin(), 20L, 20L);
    }
}