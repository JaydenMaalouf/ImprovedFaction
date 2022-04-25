package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class VersionSubCommand extends SubCommand {
    public VersionSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "version", LangMessage.VERSION_DESCRIPTION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        player.sendMessage(Language.getPrefix() + Language.format("&fRunning version " + factionsHandler.getVersion()));
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
