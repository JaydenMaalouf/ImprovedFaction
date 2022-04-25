package io.github.toberocat.improvedfactions.commands.admin;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.ranks.AdminRank;
import io.github.toberocat.improvedfactions.ranks.Rank;
import org.bukkit.entity.Player;

import java.util.List;

public class AdminJoinPrivateSubCommand extends SubCommand {
    public AdminJoinPrivateSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "sJoin", LangMessage.ADMIN_JOIN_DESCRIPTION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (1 == args.length) {
            var faction = factionsHandler.getFaction(args[0]);
            if (faction == null) {
                player.sendMessage(Language.getPrefix() + "§cCouldn't find faction to delete");
                return;
            }

            var existingFaction = factionsHandler.getFaction(player);
            if (existingFaction != null) {
                existingFaction.leave(player);
                player.sendMessage(Language.getPrefix() + "§cYou left your old faction");
            }

            faction.joinSilent(player, Rank.fromString(AdminRank.registry));
            player.sendMessage(
                    Language.getPrefix() + "§fYou successfully joined " + faction.getDisplayName() + " silently");
        } else {
            CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        if (args.length == 1){
            return factionsHandler.getFactionNames();
        }
        return null;
    }
}
