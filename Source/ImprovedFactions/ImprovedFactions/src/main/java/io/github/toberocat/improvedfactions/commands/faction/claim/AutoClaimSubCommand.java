package io.github.toberocat.improvedfactions.commands.faction.claim;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;

import org.bukkit.entity.Player;

import java.util.List;

public class AutoClaimSubCommand extends SubCommand {
    public AutoClaimSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "auto", "claim.auto", LangMessage.AUTO_CLAIM_DESCRIPTION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        var faction = factionsHandler.getFaction(player);
        if (faction == null) {
            CommandExecuteError(CommandExecuteError.NoFaction, player);
        }

        var playerData = factionsHandler.getPlayerData(player);
        if (playerData.getAutoClaim()) {
            disable(player);
        } else {
            new AutoUnclaimSubCommand(factionsHandler).disable(player);
            playerData.setAutoClaim(true);
            Language.sendMessage(LangMessage.AUTO_CLAIM_ENABLED, player);
        }
    }

    public void disable(Player player) {
        var playerData = factionsHandler.getPlayerData(player);
        if (!playerData.getAutoClaim()) {
            return;
        }

        playerData.setAutoClaim(false);
        Language.sendMessage(LangMessage.AUTO_CLAIM_DISABLED, player);
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
