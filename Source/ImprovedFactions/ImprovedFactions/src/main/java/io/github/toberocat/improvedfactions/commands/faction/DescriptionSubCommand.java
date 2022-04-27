package io.github.toberocat.improvedfactions.commands.faction;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

public class DescriptionSubCommand extends SubCommand {
    public DescriptionSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "description", LangMessage.DESCRIPTION_DESCRIPTION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        var faction = factionsHandler.getFaction(player);
        if (faction != null) {
            if (args.length >= 1) {
                if (faction.isFrozen()) {
                    CommandExecuteError(CommandExecuteError.Frozen, player);
                    return;
                }

                var builder = new StringBuilder();
                for (String arg : args) {
                    builder.append(Language.format(arg) + " ");
                }
                faction.setDescription(builder.toString().trim());
                Language.sendMessage(LangMessage.DESCRIPTION_SUCCESS, player);
            } else {
                CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
            }
        } else {
            CommandExecuteError(CommandExecuteError.NoFaction, player);
        }
    }

    @Override
    protected boolean CommandDisplayCondition(Player player, String[] args) {
        var result = super.CommandDisplayCondition(player, args);
        if (factionsHandler.getFaction(player) == null) {
            return false;
        }
        return result;
    }
}
