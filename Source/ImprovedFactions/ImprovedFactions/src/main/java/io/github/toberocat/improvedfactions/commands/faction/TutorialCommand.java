package io.github.toberocat.improvedfactions.commands.faction;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class TutorialCommand extends SubCommand {
    public TutorialCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "tutorial", "");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        int i = 0;
        while (Language.hasMessage(LangMessage.TUTORIAL_DESCRIPTION + "." + i, player))  {
            Language.sendMessage(LangMessage.TUTORIAL_DESCRIPTION + "." + i, player);
            i++;
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
