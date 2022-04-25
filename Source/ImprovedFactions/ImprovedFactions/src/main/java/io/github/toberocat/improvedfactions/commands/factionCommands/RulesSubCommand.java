package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class RulesSubCommand extends SubCommand {
    public RulesSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "rules", LangMessage.RULES_DESCRIPTION);
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings()
                .setNeedsAdmin(false)
                .setNeedsFaction(SubCommandSettings.NYI.Yes);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        var faction = factionsHandler.getFaction(player);
        if (faction.getRules() == null || faction.getRules().length() <= 0) {
            Language.sendMessage(LangMessage.RULES_ARE_EMPTY, player);
        } else {
            player.sendMessage(Language.format(faction.getRules()));
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
