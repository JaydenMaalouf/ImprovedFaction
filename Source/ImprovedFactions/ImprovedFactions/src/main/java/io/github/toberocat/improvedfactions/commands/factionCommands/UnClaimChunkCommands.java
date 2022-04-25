package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.factionCommands.claimCommands.UnclaimAutoChunkSubCommand;
import io.github.toberocat.improvedfactions.commands.factionCommands.claimCommands.UnclaimOneChunkSubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.data.Permissions;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class UnClaimChunkCommands extends SubCommand {

    public UnClaimChunkCommands(FactionsHandler factionsHandler) {
        super(factionsHandler, "unclaim", LangMessage.UNCLAIM_DESCRIPTION);
        subCommands.add(new UnclaimOneChunkSubCommand(factionsHandler));
        subCommands.add(new UnclaimAutoChunkSubCommand(factionsHandler));
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        var faction = factionsHandler.getFaction(player);
        if (faction == null){
            CommandExecuteError(CommandExecuteError.NoFaction, player);
            return;
        }

        if (faction.isFrozen()) {
            CommandExecuteError(CommandExecuteError.Frozen, player);
            return;
        }

        if (args.length == 0) {
            subCommands.get(0).CallSubCommand(player, args);
            return;
        }

        if (faction.hasPermission(player, Permissions.UNCLAIM_CHUNK_PERMISSION)) {
            if(!this.CallSubCommands(player, args)) {
                player.sendMessage(Language.getPrefix() + "§cThis command doesn't exist");
            }
        } else {
            CommandExecuteError(CommandExecuteError.NoFactionPermission, player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return this.CallSubCommandsTab(player, args);
    }

    @Override
    protected boolean CommandDisplayCondition(Player player, String[] args) {
        var result = super.CommandDisplayCondition(player, args);
        var faction = factionsHandler.getFaction(player);
        if (faction == null) {
            return false;
        }
        
        if (!faction.hasPermission(player, Permissions.UNCLAIM_CHUNK_PERMISSION)) {
            return false;
        }

        return result;
    }
}
