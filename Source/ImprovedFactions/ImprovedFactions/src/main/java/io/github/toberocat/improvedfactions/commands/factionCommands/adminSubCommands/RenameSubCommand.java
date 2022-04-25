package io.github.toberocat.improvedfactions.commands.factionCommands.adminSubCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class RenameSubCommand extends SubCommand {
    public RenameSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "rename", "");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (args.length != 2) {
            CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
            return;
        }

        var faction = factionsHandler.getFaction(args[0]);
        if (faction == null) {
            player.sendMessage(Language.getPrefix() + "§cCouldn't find faction to update");
            return;
        }

        faction.setDisplayName(args[1]);
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        if (args.length == 1) {
            return factionsHandler.getFactionNames();
        }
        return null;
    }
}
