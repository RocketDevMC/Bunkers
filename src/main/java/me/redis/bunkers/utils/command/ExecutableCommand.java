package me.redis.bunkers.utils.command;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import me.redis.bunkers.utils.BukkitUtils;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public abstract class ExecutableCommand implements CommandExecutor, TabCompleter {

    private final String name;

    private String description;

    private String[] aliases;

    private final List<CommandArgument> arguments = new ArrayList<>();

    public ExecutableCommand(String name) {
        this(name, null);
    }

    public ExecutableCommand(String name, String description) {
        this(name, description, ArrayUtils.EMPTY_STRING_ARRAY);
    }

    public ExecutableCommand(String name, String description, String... aliases) {
        this.name = name;
        this.description = description;

        this.aliases = Arrays.copyOf(aliases, aliases.length);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Available sub-command(s) for '" + label + "' are " + ChatColor.GRAY +
                    arguments.stream().filter(argument -> {
                                String permission = argument.getPermission((PluginCommand) command);

                                return permission == null || sender.hasPermission(permission);
                            }
                    ).map(CommandArgument::getName).collect(Collectors.joining(ChatColor.GRAY + ", ")) + ChatColor.RED + ".");
            sender.sendMessage(ChatColor.RED + "You must specify a sub-command.");
            return true;
        }

        CommandArgument argument = getArgument(args[0]);
        String permission = argument == null ? null : argument.getPermission((PluginCommand) command);
        if (argument == null || permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + WordUtils.capitalizeFully(name) + " sub-command '" + args[0] + "' not found.");
        } else {
            argument.onCommand(sender, command, label, args);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length < 2) {
            for (CommandArgument argument : arguments) {
                String permission = argument.getPermission((PluginCommand) command);
                if (permission == null || sender.hasPermission(permission)) {
                    results.add(argument.getName());
                }
            }

            if (results.isEmpty()) {
                return null;
            }
        } else {
            CommandArgument argument = getArgument(args[0]);
            if (argument == null) {
                return results;
            }

            String permission = argument.getPermission((PluginCommand) command);
            if (permission == null || sender.hasPermission(permission)) {
                results = argument.onTabComplete(sender, command, label, args);

                if (results == null) {
                    return null;
                }
            }
        }

        return BukkitUtils.getCompletions(args, results);
    }

    public List<CommandArgument> getArguments() {
        return ImmutableList.copyOf(arguments);
    }

    public void addArgument(CommandArgument argument) {
        arguments.add(argument);
    }

    public void removeArgument(CommandArgument argument) {
        arguments.remove(argument);
    }

    public CommandArgument getArgument(String name) {
        return arguments.stream().filter(argument -> argument.getName().equalsIgnoreCase(name) || Arrays.asList(argument.getAliases()).contains(name.toLowerCase())).findFirst().orElse(null);
    }

    public void sendUsage(CommandSender sender, String usage) {
        ComponentBuilder builder = new ComponentBuilder("Usage: /" + usage);
        builder.color(net.md_5.bungee.api.ChatColor.RED);
        if (description != null && !description.isEmpty()) {
            builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(description).color(net.md_5.bungee.api.ChatColor.YELLOW).create()));
        }

        if (sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(builder.create());
        } else {
            sender.sendMessage(TextComponent.toPlainText(builder.create()));
        }
    }
}