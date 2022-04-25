package io.github.toberocat.improvedfactions.commands.admin;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class AdminPermanentSubCommand extends SubCommand {
    public AdminPermanentSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "permanent", "");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (args.length != 1) {
            CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
            return;
        }

        var faction = factionsHandler.getFaction(args[0]);
        if (faction == null) {
            player.sendMessage(Language.getPrefix() + "§cCouldn't find faction to make permanent");
            return;
        }

        faction.setPermanent(!faction.isPermanent());
        player.sendMessage(Language.getPrefix() + "§fFaction " + (faction.isPermanent() ? "is now permanent" : "isn't permanent any more"));
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        if (args.length == 1) {
            return factionsHandler.getFactionNames();
        }
        return null;
    }
}
