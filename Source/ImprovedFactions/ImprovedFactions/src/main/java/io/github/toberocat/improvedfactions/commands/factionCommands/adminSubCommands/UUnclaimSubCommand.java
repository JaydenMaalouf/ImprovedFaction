package io.github.toberocat.improvedfactions.commands.factionCommands.adminSubCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.LangMessage;
import org.bukkit.entity.Player;

import java.util.List;

public class UUnclaimSubCommand extends SubCommand {
    public UUnclaimSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "uUnclaim", LangMessage.ADMIN_UNCLAIM_DESCRIPTION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        var chunk = player.getLocation().getChunk();
        var faction = factionsHandler.getFaction(chunk);
        if (faction == null) {
            return;
        }

        faction.unclaimChunk(chunk, null);
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
