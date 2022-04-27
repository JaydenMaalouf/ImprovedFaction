package io.github.toberocat.improvedfactions.commands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.faction.JoinPrivateFactionSubCommand;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FJoin implements TabExecutor {
    private FactionsHandler factionsHandler;

    public FJoin(FactionsHandler factionsHandler) {
        this.factionsHandler = factionsHandler;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
            @NotNull String[] args) {
        if (args.length != 2) {
            return false;
        }

        var player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage(Language.getPrefix() + "Player not found");
            return false;
        }

        if (!player.isOnline()) {
            commandSender.sendMessage(Language.getPrefix() + "Player is offline. Can't do this with a offline player");
            return false;
        }

        var faction = factionsHandler.getFaction(player);
        if (faction != null) {
            faction.leave(player);
        }

        new JoinPrivateFactionSubCommand(factionsHandler).joinPrivate(player, args[1]);
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 2) {
            return factionsHandler.getFactionNames();
        } else {
            return factionsHandler.getOnlinePlayerNames();
        }
    }
}
