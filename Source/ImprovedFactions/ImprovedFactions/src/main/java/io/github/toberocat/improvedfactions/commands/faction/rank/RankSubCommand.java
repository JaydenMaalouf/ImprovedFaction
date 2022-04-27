package io.github.toberocat.improvedfactions.commands.faction.rank;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

public class RankSubCommand extends SubCommand {
    public RankSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "rank", LangMessage.RANK_DESCRIPTION);
        //addSubCommand(new SetUserRankSubCommand(factionsHandler);
        //addSubCommand(new PermissionsRankSubCommand(factionsHandler));
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        var faction = factionsHandler.getFaction(player);
        if (faction == null) {
            CommandExecuteError(CommandExecuteError.NoFaction, player);
            return;
        }

        if (faction.isFrozen()) {
            CommandExecuteError(CommandExecuteError.Frozen, player);
            return;
        }

        if (faction.getPlayerRank(player).isAdmin()) {
            if (!this.CallSubCommands(player, args)) {
                player.sendMessage(Language.getPrefix() + "§cThis command doesn't exist");
            }
        } else {
            CommandExecuteError(CommandExecuteError.NoFactionPermission, player);
        }
    }

    @Override
    protected boolean CommandDisplayCondition(Player player, String[] args) {
        var result = super.CommandDisplayCondition(player, args);
        var faction = factionsHandler.getFaction(player);
        if (faction == null) {
            return false;
        }

        var playerRank = faction.getPlayerRank(player);
        if (playerRank != null && !playerRank.isAdmin()) {
            return false;
        }

        return result;
    }
}
