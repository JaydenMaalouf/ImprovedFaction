package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.event.faction.FactionDeleteEvent;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.language.Parseable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DeleteSubCommand extends SubCommand {
    public DeleteSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "delete", LangMessage.DELETE_DESCRIPTION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        var faction = factionsHandler.getFaction(player);
        if (faction != null) {
            if (faction.isFrozen()) {
                CommandExecuteError(CommandExecuteError.Frozen, player);
                return;
            }

            if (faction.getPlayerRank(player).isAdmin()) {
                var deleteEvent = new FactionDeleteEvent(faction, player);
                Bukkit.getPluginManager().callEvent(deleteEvent);
                if (faction.deleteFaction() && !deleteEvent.isCancelled()) {
                    Language.sendMessage(LangMessage.DELETE_SUCCESS, player,
                            new Parseable("{faction_displayname}", faction.getDisplayName()));
                } else {
                    if (deleteEvent.isCancelled()) {
                        Language.sendMessage(LangMessage.DELETE_ERROR, player,
                                new Parseable("{error}", deleteEvent.getCancelMessage()));
                    } else {
                        CommandExecuteError(CommandExecuteError.OtherError, player);
                    }
                }
            } else {
                CommandExecuteError(CommandExecuteError.OnlyAdminCommand, player);
            }
        } else {
            CommandExecuteError(CommandExecuteError.NoFaction, player);
        }
    }

    @Override
    protected boolean CommandDisplayCondition(Player player, String[] args) {
        var result = super.CommandDisplayCondition(player, args);
        var faction = factionsHandler.getFaction(player);
        if (faction == null) {
            result = false;
        }

        var playerRank = faction.getPlayerRank(player);
        if (faction != null && playerRank != null && !playerRank.isAdmin()) {
            result = false;
        }
        return result;
    }
}
