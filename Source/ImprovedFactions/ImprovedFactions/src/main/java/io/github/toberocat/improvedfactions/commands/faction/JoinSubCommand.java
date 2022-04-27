package io.github.toberocat.improvedfactions.commands.faction;

import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.Parseable;
import io.github.toberocat.improvedfactions.ranks.MemberRank;
import io.github.toberocat.improvedfactions.ranks.Rank;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class JoinSubCommand extends SubCommand {
    public JoinSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "join", LangMessage.JOIN_DESCRIPTION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        var playerFaction = factionsHandler.getFaction(player);
        if (playerFaction == null) {
            if (args.length != 1) {
                CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
                return;
            }
            var faction = factionsHandler.getFaction(args[0]);
            if (faction == null) {
                Language.sendMessage(LangMessage.JOIN_ERROR_NO_FACTION_FOUND, player);
                return;
            }

            if (!faction.isOpen()) {
                Language.sendMessage(LangMessage.JOIN_ERROR_FACTION_PRIVATE, player);
                return;
            }

            if (faction.getBannedPlayers().contains(player.getUniqueId())) {
                Language.sendMessage(LangMessage.JOIN_ERROR_FACTION_BANNED, player);
                return;
            }

            if (faction.isFrozen()) {
                CommandExecuteError(CommandExecuteError.Frozen, player);
                return;
            }

            if (faction.join(player, Rank.fromString(MemberRank.registry))) {
                Language.sendMessage(LangMessage.JOIN_SUCCESS, player,
                        new Parseable("{faction_displayname}", faction.getDisplayName()));
            } else {
                if (faction.hasMaxMembers()) {
                    Language.sendMessage(LangMessage.JOIN_FULL, player);
                }
            }
        } else {
            player.sendMessage(Language.getPrefix()
                    + "§cYou have already joined a faction. Please leave before joining another faction");
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        List<String> arguments = new ArrayList<>();
        if (args.length != 1)
            return arguments;
        for (var faction : factionsHandler.getFactions()) {
            if (faction.isOpen()) {
                arguments.add(ChatColor.stripColor(faction.getDisplayName()));
            }
        }
        return arguments;
    }

    @Override
    protected boolean CommandDisplayCondition(Player player, String[] args) {
        boolean result = super.CommandDisplayCondition(player, args);
        if (factionsHandler.getFaction(player) != null) {
            return false;
        }
        return result;
    }
}