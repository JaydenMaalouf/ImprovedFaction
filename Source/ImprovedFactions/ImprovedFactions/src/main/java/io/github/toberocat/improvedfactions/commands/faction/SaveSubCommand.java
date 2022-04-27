package io.github.toberocat.improvedfactions.commands.faction;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class SaveSubCommand extends SubCommand {
    public SaveSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "save", "save", LangMessage.SAVE_DESCRIPTION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        factionsHandler.saveFactions();
        player.sendMessage(Language.getPrefix() + "§fSaved faction data");
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
