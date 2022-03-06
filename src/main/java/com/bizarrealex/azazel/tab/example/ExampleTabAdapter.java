package com.bizarrealex.azazel.tab.example;

import com.bizarrealex.azazel.tab.TabAdapter;
import com.bizarrealex.azazel.tab.TabTemplate;
import me.redis.bunkers.Bunkers;
import me.redis.bunkers.information.Information;
import me.redis.bunkers.tasks.GameTimeTask;
import me.redis.bunkers.team.Team;
import me.redis.bunkers.utils.BukkitUtils;
import net.minecraft.util.org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.util.UUID;

public class ExampleTabAdapter implements TabAdapter {
    Information information = Bunkers.getPlugin().getInformationManager().getInformation();

    public TabTemplate getTemplate(Player player) {
        TabTemplate template = new TabTemplate();

        template.left(0, "&6&l" + information.getServerName());
        template.middle(0, "&e&lBunkers");
        template.right(0, "&6&l" + information.getServerName());

        if (Bunkers.getPlugin().getTeamManager().getByPlayer(player) != null) {
            Team team = Bunkers.getPlugin().getTeamManager().getByPlayer(player);

            template.left(2, "&6&lTeam Info");
            template.left(3, "&eDTR&7: &f" + team.getDtr());
            template.left(4, "&eOnline&7: &f" + team.getMembers().size());
        } else {
            template.left(2, "&6&lTeam Info");
            template.left(3, "&eSelect a team...");
        }

        template.left(6, "&6&lLocation");
        if (Bunkers.getPlugin().getTeamManager().getByLocation(player.getLocation()) == null) {
            template.left(7, "&4Warzone");
        } else {
            template.left(7, Bunkers.getPlugin().getTeamManager().getByLocation(player.getLocation()).getColor() + Bunkers.getPlugin().getTeamManager().getByLocation(player.getLocation()).getName() + " Base");
        }
        template.left(8, "(" + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockZ() + ")");

        template.left(10, "&6&lGame Info");
        template.left(11, "&eKoth&7: &f" + (GameTimeTask.getNumOfSeconds() < 300 ? "Starts in " + BukkitUtils.niceTime(300 - GameTimeTask.getNumOfSeconds(), false) : BukkitUtils.niceTime(Bunkers.getPlugin().getKoth().getCapSeconds(), false)));

        template.left(13, "&6&lTeams DTR");
        template.left(14, "&cRed&7: &f" + Bunkers.getPlugin().getTeamManager().getByName("Red").getDtr());
        template.left(15, "&9Blue&7: &f" + Bunkers.getPlugin().getTeamManager().getByName("Blue").getDtr());
        template.left(16, "&aGreen&7: &f" + Bunkers.getPlugin().getTeamManager().getByName("Green").getDtr());
        template.left(17, "&eYellow&7: &f" + Bunkers.getPlugin().getTeamManager().getByName("Yellow").getDtr());

        template.middle(2, "&c&lRed Team");
        for (String member : Bunkers.getPlugin().getTeamManager().getByName("Red").getMembersNames()) {
            if (member != null) {
                template.middle("&c" + member);
            }
        }

        template.middle(10, "&a&lGreen Team");
        for (String member : Bunkers.getPlugin().getTeamManager().getByName("Green").getMembersNames()) {
            if (member != null) {
                template.middle("&a" + member);
            }
        }

        template.right(2, "&9&lBlue Team");
        for (String member : Bunkers.getPlugin().getTeamManager().getByName("Blue").getMembersNames()) {
            if (member != null) {
                template.right("&9" + member);
            }
        }

        template.right(10, "&e&lYellow Team");
        for (String member : Bunkers.getPlugin().getTeamManager().getByName("Yellow").getMembersNames()) {
            if (member != null) {
                template.right("&e" + member);
            }
        }

        if (player.hasPermission("bunkers.admin")) {
            template.farRight(2, "&6&lServer Information");
            template.farRight("&eTicks Per Sec&7: &f" + new DecimalFormat("#.##").format(Bukkit.spigot().getTPS()[0]));
            template.farRight("&ePerformance&7: &f" + new DecimalFormat("#.##").format(getLag()) + "%");
            template.farRight("&eRam&7: &f" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576L) + "/" + Runtime.getRuntime().totalMemory() / 1048576L + "MB");
        }

        return template;
    }

    public double getLag() {
        return (Bukkit.spigot().getTPS()[0] / 20 * 100 > 100 ? 100 : Bukkit.spigot().getTPS()[0] / 20 * 100);
    }
}
