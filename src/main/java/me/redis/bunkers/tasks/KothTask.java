package me.redis.bunkers.tasks;

import me.redis.bunkers.Bunkers;
import me.redis.bunkers.game.GameManager;
import me.redis.bunkers.game.status.GameStatus;
import me.redis.bunkers.koth.Koth;
import me.redis.bunkers.profiles.Profile;
import me.redis.bunkers.profiles.status.PlayerStatus;
import me.redis.bunkers.team.Team;
import me.redis.bunkers.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class KothTask extends BukkitRunnable {
    private Koth koth = Bunkers.getPlugin().getKoth();
    boolean started = false;
    Profile profile;

    @Override public void run() {
        if (!started) {
            profile = new Profile(koth.getController().getUniqueId());
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6[KingOfTheHill] &eThe &9Koth &ecan now be contested. &7(10:00)"));
            started = true;
        }

        if (koth.getCapSeconds() <= 0) {
            Team team = Bunkers.getPlugin().getTeamManager().getByPlayer(koth.getController());
            new GameManager().setWon(team);
            cancel();
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (Bunkers.getPlugin().getSpectatorManager().getSpectators().contains(player)) return;

            if (koth.isInsideArea(player.getLocation())) {
                if(profile.getStatus() == PlayerStatus.SPECTATOR){
                    return;
                }
                if (koth.getController() == null) {
                    koth.setController(player);
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6[KingOfTheHill] &eSomeone has started controlling the &9Koth&e. &7(" + BukkitUtils.niceTime(koth.getSeconds(), false) + ")"));
                }

                if (koth.getCapSeconds() % 30 == 0 && koth.getCapSeconds() != 600) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6[KingOfTheHill] &eSomeone is trying to control the &9Koth&e. &7(" + BukkitUtils.niceTime(koth.getCapSeconds(), false) + ")"));
                }
            } else {
                if (koth.getController() == player) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&6[KingOfTheHill] &eSomeone has been knocked off the &9Koth&e. &7(" + BukkitUtils.niceTime(koth.getCapSeconds(), false) + ")"));
                    koth.setController(null);
                    koth.setCapSeconds(koth.getSeconds());
                }
            }
        }

        if (koth.getController() != null) {
            koth.setCapSeconds(koth.getCapSeconds() - 1);
        }
    }
}
