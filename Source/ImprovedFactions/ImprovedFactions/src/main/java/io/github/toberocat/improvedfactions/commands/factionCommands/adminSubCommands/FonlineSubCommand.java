package io.github.toberocat.improvedfactions.commands.factionCommands.adminSubCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class FonlineSubCommand extends SubCommand {
    public FonlineSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "fonline", "");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (args.length != 1) {
            CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
            return;
        }

        var faction = factionsHandler.getFaction(args[0]);
        if (faction == null) {
            player.sendMessage(Language.getPrefix() + "§cCouldn't find faction");
            return;
        }

        var onlineCount = faction.getPlayersOnline().size();
        player.sendMessage(Language.getPrefix() + onlineCount + (onlineCount == 1 ? " person " : " people ") +
                (onlineCount == 1 ? "is" : "are") + " online in §e" + faction.getDisplayName());
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        if (args.length == 1) {
            return factionsHandler.getFactionNames();
        }
        return null;
    }
}
