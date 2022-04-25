package io.github.toberocat.improvedfactions.commands.factionCommands.adminSubCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.language.Parseable;
import org.bukkit.entity.Player;

import java.util.List;

public class DisbandFactionCommand extends SubCommand {

    public DisbandFactionCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "disband", LangMessage.ADMIN_DISBAND_DESCRIPTION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (args.length == 1) {
            var foundFaction = factionsHandler.getFaction(args[0]);
            if (foundFaction == null) {
                player.sendMessage(Language.getPrefix() + "§cCouldn't find faction to delete");
                return;
            }
            if (foundFaction.deleteFaction()) {
                Language.sendMessage(LangMessage.DELETE_SUCCESS, player,
                        new Parseable("{faction_displayname}", foundFaction.getDisplayName()));
            } else {
                CommandExecuteError(CommandExecuteError.OtherError, player);
            }
        } else {
            CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        if (args.length == 1) {
            return factionsHandler.getFactionNames();
        }
        return null;
    }
}
