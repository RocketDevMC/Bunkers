package me.redis.bunkers.information.commands.argments;

import me.redis.bunkers.Bunkers;
import me.redis.bunkers.information.Information;
import me.redis.bunkers.team.Team;
import me.redis.bunkers.utils.Cuboid;
import me.redis.bunkers.utils.JavaUtils;
import me.redis.bunkers.utils.LocationUtils;
import me.redis.bunkers.utils.command.CommandArgument;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetMinimumPlayersArgument extends CommandArgument {
    public SetMinimumPlayersArgument() {
        super("setminplayers", null, "minplayers");
    }

    @Override public String getUsage(String label) {
        return ChatColor.RED + "/" + label + " " + getName() + " <number>";
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length != 2) {
                player.sendMessage(getUsage(label));
                return true;
            }

            if (!JavaUtils.isInteger(args[1])) {
                player.sendMessage(ChatColor.RED + "You must introduce a number");
                return true;
            }

            Information information = Bunkers.getPlugin().getInformationManager().getInformation();
            information.setMinPlayers(Integer.parseInt(args[1]));
            information.save();
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have saved the minimum players to start. &7&o(You can check them by /information show)"));
        }
        return true;
    }
}
