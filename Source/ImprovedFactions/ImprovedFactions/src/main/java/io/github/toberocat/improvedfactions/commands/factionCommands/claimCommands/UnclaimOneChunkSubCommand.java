package io.github.toberocat.improvedfactions.commands.factionCommands.claimCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.LangMessage;
import org.bukkit.entity.Player;

import java.util.List;

public class UnclaimOneChunkSubCommand extends SubCommand {
    public UnclaimOneChunkSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "one", "unclaim.one", LangMessage.UNCLAIM_ONE_DESCRIPTION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        var faction = factionsHandler.getFaction(player);
        if (faction == null) {
            return;
        }

        faction.unclaimChunk(player.getLocation().getChunk(), null);
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
