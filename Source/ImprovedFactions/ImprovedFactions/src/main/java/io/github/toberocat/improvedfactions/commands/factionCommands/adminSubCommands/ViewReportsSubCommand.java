package io.github.toberocat.improvedfactions.commands.factionCommands.adminSubCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.utility.async.AsyncCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ViewReportsSubCommand extends SubCommand {
    public ViewReportsSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "viewreports", "");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (args.length != 1) {
            CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
            return;
        }

        AsyncCore.Run(() -> {
            for (var report : factionsHandler.getReports()) {
                if (report.getFaction().equals(args[0])) {
                    player.sendMessage(Language.getPrefix() + report.getReason() + " - " + Bukkit.getOfflinePlayer(report.getPlayer()).getName());
                }
            }
            player.sendMessage(Language.getPrefix() + "All reports received for this faction");
        });
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        var results = new ArrayList<String>();
        for (var report : factionsHandler.getReports()) {
            results.add(report.getFaction());
        }

        return results;
    }
}
