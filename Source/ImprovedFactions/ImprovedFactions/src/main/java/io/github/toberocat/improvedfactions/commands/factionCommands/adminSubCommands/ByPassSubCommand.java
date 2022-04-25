package io.github.toberocat.improvedfactions.commands.factionCommands.adminSubCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class ByPassSubCommand extends SubCommand {

    public ByPassSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "bypass", "");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        Player selectedPlayer = null;
        if (args.length == 0) {
            selectedPlayer = player;
        } else {
            var existingPlayer = Bukkit.getPlayer(args[0]);
            if (existingPlayer == null) {
                CommandExecuteError(CommandExecuteError.PlayerNotFound, player);
                return;
            }

            if (existingPlayer.isOnline()) {
                selectedPlayer = existingPlayer;
            }
        }

        if (selectedPlayer == null) {
            return;
        }

        var playerData = factionsHandler.getPlayerData(selectedPlayer);
        if (playerData == null) {
            return;
        }

        if (playerData.getBypass()) {
            playerData.setBypass(false);
            Language.sendRawMessage("Disabled bypass for " + args[0], selectedPlayer);
        } else {
            playerData.setBypass(true);
            Language.sendRawMessage("Enabled bypass for " + args[0], selectedPlayer);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return Arrays.stream(Bukkit.getOfflinePlayers()).map(OfflinePlayer::getName).toList();
    }
}
