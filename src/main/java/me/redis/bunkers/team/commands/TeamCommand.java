package me.redis.bunkers.team.commands;

import me.redis.bunkers.team.commands.arguments.*;
import me.redis.bunkers.utils.command.ExecutableCommand;

public class TeamCommand extends ExecutableCommand {
    public TeamCommand() {
        super("team", null, "t", "f", "faction", "factions", "sq", "squad");

        addArgument(new InfoArgument());
        addArgument(new SetAreaArgument());
        addArgument(new SetSpawnLocationArgument());
        addArgument(new HomeArgument());
        addArgument(new SetVillagerArgument());
    }
}
