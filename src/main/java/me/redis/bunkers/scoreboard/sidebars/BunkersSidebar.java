package me.redis.bunkers.scoreboard.sidebars;

import me.redis.bunkers.Bunkers;
import me.redis.bunkers.game.status.GameStatus;
import me.redis.bunkers.profiles.Profile;
import me.redis.bunkers.profiles.status.PlayerStatus;
import me.redis.bunkers.scoreboard.scoreboard.Board;
import me.redis.bunkers.scoreboard.scoreboard.BoardAdapter;
import me.redis.bunkers.scoreboard.scoreboard.cooldown.BoardCooldown;
import me.redis.bunkers.tasks.GameTimeTask;
import me.redis.bunkers.team.Team;
import me.redis.bunkers.utils.BukkitUtils;
import me.redis.bunkers.utils.DurationFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BunkersSidebar implements BoardAdapter, Listener {
    public BunkersSidebar(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public String getTitle(Player player) {
        return "&6&l" + Bunkers.getPlugin().getInformationManager().getInformation().getServerName() +" &7｜ &fBunkers";
    }

    @Override
    public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> cooldowns) {
        List<String> strings = new ArrayList<>();
        Team team = Bunkers.getPlugin().getTeamManager().getByPlayer(player);
        Profile profile = Bunkers.getPlugin().getProfileManager().getProfile(player);

        strings.add(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------&r"));
        if (Bunkers.getPlugin().getGameManager().getStatus() == GameStatus.WAITING || Bunkers.getPlugin().getGameManager().getStatus() == GameStatus.STARTING) {
            strings.add("&e&lOnline&7: &f" + Bukkit.getOnlinePlayers().size());
            strings.add("&6&lTeam&7: &f" + (team == null ? "None" : team.getColor() + team.getName()));

            if (Bunkers.getPlugin().getGameManager().getStarted()) {
                strings.add("&9Starting in &l" + (Bunkers.getPlugin().getGameManager().getCooldown()[0] + 1) + "s");
            }
        } else if (Bunkers.getPlugin().getGameManager().getStatus() == GameStatus.PLAYING && profile.getStatus() == PlayerStatus.PLAYING) {
            strings.add("&6&lGame Time&7: &f" + BukkitUtils.niceTime(GameTimeTask.getNumOfSeconds(), false));
            if (Bunkers.getPlugin().getTimerManager().getProtectionTimer().getRemaining(player) > 0) {
                strings.add("&a&lImmunity&7: &f" + DurationFormatter.getRemaining(Bunkers.getPlugin().getTimerManager().getProtectionTimer().getRemaining(player), true));
            }
            strings.add("&9&lKoth&7: &f" + (GameTimeTask.getNumOfSeconds() < 300 ? "Starts in " + BukkitUtils.niceTime(300 - GameTimeTask.getNumOfSeconds(), false) : BukkitUtils.niceTime(Bunkers.getPlugin().getKoth().getCapSeconds(), false)));
            strings.add("&e&lTeam&7: &f" + team.getName());
            strings.add("&4&lDTR&7: &f" + team.getDtr() + ".0");
            strings.add("&a&lBalance&7: &f$" + profile.getBalance());
            if (Bunkers.getPlugin().getTimerManager().getTeleportTimer().getRemaining(player) > 0) {
                strings.add("&9&lHome&7: &f" + DurationFormatter.getRemaining(Bunkers.getPlugin().getTimerManager().getTeleportTimer().getRemaining(player), true));
            }
        } else if (Bunkers.getPlugin().getGameManager().getStatus() == GameStatus.PLAYING && profile.getStatus() == PlayerStatus.SPECTATOR) {
            strings.add("&6&lGame Time&7: &f" + BukkitUtils.niceTime(GameTimeTask.getNumOfSeconds(), false));
            strings.add("&a&nTeams&7:");
            for (Team teams : Bunkers.getPlugin().getTeamManager().getTeams().values()) {
                strings.add("  &7- " + teams.getColor() + teams.getName() + "&7: &f" + teams.getDtr());
            }
        } else if (Bunkers.getPlugin().getGameManager().getStatus() == GameStatus.ENDING) {
            strings.add("&6&lWinner Team&7: " + Bunkers.getPlugin().getGameManager().getWinnerTeam().getColor() + Bunkers.getPlugin().getGameManager().getWinnerTeam().getName());
        }

        if (Bunkers.getPlugin().getGameManager().isEvent()) {
            strings.add(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------&l"));
            if (Bunkers.getPlugin().getGameManager().getScoreboard() == 0) {
                strings.add("&e" + Bunkers.getPlugin().getGameManager().getEventName());
            } else {
                strings.add("&eAddress&7: &f" + Bunkers.getPlugin().getInformationManager().getInformation().getAddress());
            }
        }

        strings.add(ChatColor.translateAlternateColorCodes('&', "&7&m----------------------"));

        if (strings.size() == 2) {
            return null;
        }

        return strings;
    }
}
