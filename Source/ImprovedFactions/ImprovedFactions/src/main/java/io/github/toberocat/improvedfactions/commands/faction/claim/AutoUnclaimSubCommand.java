package io.github.toberocat.improvedfactions.commands.faction.claim;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class AutoUnclaimSubCommand extends SubCommand {
    public AutoUnclaimSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "auto", "unclaim.auto", LangMessage.AUTO_UNCLAIM_DESCRIPTION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        var faction = factionsHandler.getFaction(player);
        if (faction == null) {
            CommandExecuteError(CommandExecuteError.NoFaction, player);
        }

        var playerData = factionsHandler.getPlayerData(player);
        if (playerData.getAutoUnclaim()){
            disable(player);
        }
        else{            
            new AutoClaimSubCommand(factionsHandler).disable(player);
            playerData.setAutoUnclaim(true);
            Language.sendMessage(LangMessage.AUTO_UNCLAIM_ENABLED, player);
        }
    }

    public void disable(Player player) {
        var playerData = factionsHandler.getPlayerData(player);
        if (!playerData.getAutoUnclaim()) {
            return;
        }

        playerData.setAutoUnclaim(false);
        Language.sendMessage(LangMessage.AUTO_UNCLAIM_DISABLED, player);

    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
