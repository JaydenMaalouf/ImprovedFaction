package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.language.LangMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class UnbanSubCommand extends SubCommand {
    public UnbanSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "unban", "unban", LangMessage.UNBAN_DESCRIPTION);
    }

    @Override
    public SubCommandSettings getSettings() {
        return getSettings().setNeedsAdmin(true).setNeedsFaction(SubCommandSettings.NYI.Yes);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        var faction = factionsHandler.getFaction(player);
        if (args.length == 1) {
            var unbannedPlayer = Bukkit.getPlayer(args[0]);
            if (unbannedPlayer == null) {
                CommandExecuteError(CommandExecuteError.PlayerNotFound, player);
                return;
            }

            faction.unbanPlayer(player, unbannedPlayer);
        } else {
            CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        List<String> arguments = new ArrayList<>();
        if (args.length == 1) {
            var faction = factionsHandler.getFaction(player);
            if (faction == null) {
                return arguments;
            }

            for (var banned : faction.getBannedPlayers()) {
                var bannedPlayer = Bukkit.getOfflinePlayer(banned);
                if (bannedPlayer == null){
                    continue;
                }

                arguments.add(bannedPlayer.getName());
            }
        }
        return arguments;
    }
}
