package io.github.toberocat.improvedfactions.commands.factionCommands.adminSubCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class FrozeAdminCommand extends SubCommand {
    public FrozeAdminCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "freeze", "");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (args.length != 1) {
            CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
            return;
        }

        var faction = factionsHandler.getFaction(args[0]);
        if (faction == null) {
            player.sendMessage(Language.getPrefix() + "§cCouldn't find faction to freeze");
            return;
        }

        faction.setFrozen(!faction.isFrozen());
        player.sendMessage(Language.getPrefix() + "§fFaction " + (faction.isFrozen() ? "is now frozen" : "isn't frozen any more"));
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        if (args.length == 1){
            return factionsHandler.getFactionNames();
        }

        return null;
    }
}
