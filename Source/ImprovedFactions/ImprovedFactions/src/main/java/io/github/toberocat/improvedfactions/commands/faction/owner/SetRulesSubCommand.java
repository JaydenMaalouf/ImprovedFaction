package io.github.toberocat.improvedfactions.commands.faction.owner;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class SetRulesSubCommand extends SubCommand {

    public SetRulesSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "setRules", LangMessage.RULES_SET_DESCRIPTION);
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings()
                .setNeedsAdmin(true)
                .setNeedsFaction(SubCommandSettings.NYI.Yes);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        var faction = factionsHandler.getFaction(player);
        if (faction.isFrozen()) {
            CommandExecuteError(CommandExecuteError.Frozen, player);
            return;
        }

        faction.setRules(String.join(" ", args));
        Language.sendMessage(LangMessage.RULES_SET_COMMAND, player);
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
