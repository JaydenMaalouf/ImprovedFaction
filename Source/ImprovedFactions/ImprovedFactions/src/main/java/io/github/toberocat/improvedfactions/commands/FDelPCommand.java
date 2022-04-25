package io.github.toberocat.improvedfactions.commands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FDelPCommand implements TabExecutor {
    private FactionsHandler factionsHandler;
    public FDelPCommand(FactionsHandler factionsHandler){
        this.factionsHandler = factionsHandler;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage("Please add a faction name to delete");
            return false;
        }

        var faction = factionsHandler.getFaction(args[0]);
        if (faction == null) {
            commandSender.sendMessage("Faction does not exist");
            return false;
        }

        faction.deleteFaction();
        commandSender.sendMessage("Successfully deleted the faction");
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length != 1) {
            return null;
        }

        return factionsHandler.getFactionNames();
    }
}