package me.redis.bunkers.utils.command;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public abstract class CommandArgument {

    private String name;
    private String description;

    @Setter private boolean requiresPermission;

    private String[] aliases;

    public CommandArgument(String name) {
        this(name, null);
    }

    public CommandArgument(String name, String description) {
        this(name, description, ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public CommandArgument(String name, String description, String... aliases) {
        this.name = name;
        this.description = description;

        this.aliases = Arrays.copyOf(aliases, aliases.length);
    }

    public String getPermission(PluginCommand command) {
        return requiresPermission ? (command.getPermission() == null ? command.getPlugin().getName() + ".command." + command.getName() + ".argument." + name : command.getPermission() + ".argument." + name) : null;
    }

    public abstract String getUsage(String label);

    public abstract boolean onCommand(CommandSender sender , Command command, String label, String[] args);

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}