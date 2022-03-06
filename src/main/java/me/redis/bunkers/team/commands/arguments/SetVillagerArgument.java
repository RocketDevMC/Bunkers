package me.redis.bunkers.team.commands.arguments;

import me.redis.bunkers.Bunkers;
import me.redis.bunkers.team.Team;
import me.redis.bunkers.utils.LocationUtils;
import me.redis.bunkers.utils.command.CommandArgument;
import me.redis.bunkers.wand.Wand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetVillagerArgument extends CommandArgument {
    public SetVillagerArgument() {
        super("setvillager", null, "villager");
        setRequiresPermission(true);
    }

    @Override public String getUsage(String label) {
        return ChatColor.RED + "/" + label + " " + getName() + " <team> <combat/sell/enchant/build>";
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length != 3) {
                player.sendMessage(getUsage(label));
                return true;
            }

            Team team = Bunkers.getPlugin().getTeamManager().getByName(args[1]);

            if (team == null) {
                player.sendMessage(ChatColor.RED + "That team does not exist.");
                return true;
            }

            String type = args[2];

            if (type.equalsIgnoreCase("combat")) {
                team.setCombatShop(LocationUtils.getString(player.getLocation()));
            } else if (type.equalsIgnoreCase("sell")) {
                team.setSellShop(LocationUtils.getString(player.getLocation()));
            } else if (type.equalsIgnoreCase("enchant")) {
                team.setEnchantShop(LocationUtils.getString(player.getLocation()));
            } else if (type.equalsIgnoreCase("build")) {
                team.setBuildShop(LocationUtils.getString(player.getLocation()));
            } else {
                player.sendMessage(getUsage(label));
                return true;
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eYou have saved the " + team.getColor() + team.getName() + " &eteam."));
        }
        return true;
    }
}
