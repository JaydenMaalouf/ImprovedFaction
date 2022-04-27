package io.github.toberocat.improvedfactions.commands.faction;

import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.Parseable;
import org.bukkit.entity.Player;

public class LeaveSubCommand extends SubCommand {
    public LeaveSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "leave", LangMessage.LEAVE_DESCRIPTION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        var playerFaction = factionsHandler.getFaction(player);
        if (playerFaction != null) {
            if (playerFaction.isFrozen()) {
                CommandExecuteError(CommandExecuteError.Frozen, player);
                return;
            }

            var canLeave = playerFaction.isPermanent() || !playerFaction.getPlayerRank(player).isOwner();
            if (canLeave) {
                var result = playerFaction.leave(player);
                if (result) {
                    Language.sendMessage(LangMessage.LEAVE_SUCCESS, player,
                            new Parseable("{faction_displayname}", playerFaction.getDisplayName()));
                } else {
                    CommandExecuteError(CommandExecuteError.OtherError, player);
                }
            } else {
                player.sendMessage(Language.getPrefix()
                        + "§cCannot leave your own faction. Delete it or give someone else owner rights");
            }
        } else {
            CommandExecuteError(CommandExecuteError.NoFaction, player);
        }
    }

    @Override
    protected boolean CommandDisplayCondition(Player player, String[] args) {
        var result = super.CommandDisplayCondition(player, args);
        if (factionsHandler.getFaction(player) == null) {
            return false;
        }
        return result;
    }
}
