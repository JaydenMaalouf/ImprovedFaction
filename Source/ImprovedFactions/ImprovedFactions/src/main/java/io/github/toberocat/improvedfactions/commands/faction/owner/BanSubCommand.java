package io.github.toberocat.improvedfactions.commands.faction.owner;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.language.Parseable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BanSubCommand extends SubCommand {
    public BanSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "ban", LangMessage.BANNED_PLAYER_COMMAND_DESCRIPTION);
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings()
            .setNeedsAdmin(true)
            .setNeedsFaction(SubCommandSettings.NYI.Yes);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (args.length == 1) {
            var offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
            if (offlinePlayer == null) {
                CommandExecuteError(CommandExecuteError.PlayerNotFound, player);
                return;
            }

            banPlayer(player, offlinePlayer);
        } else {
            CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
        }
    }

    private void banPlayer(Player player, OfflinePlayer banned) {
        var faction = factionsHandler.getFaction(player);
        var result = faction.banPlayer(player, banned);
        if (result){
            Language.sendMessage(LangMessage.BANNED_PLAYER_COMMAND_SUCCESS, player,
                    new Parseable("{banned}", banned.getName()));
        }
        else {
            Language.sendMessage(LangMessage.BANNED_PLAYER_COMMAND_LEAVE, banned.getPlayer(),
                    new Parseable("{faction_displayName}", faction.getDisplayName()));
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        List<String> arguments = new ArrayList<>();
        if (args.length == 1) {
            var data = factionsHandler.getPlayerData(player);
            var faction = data.getPlayerFaction();
            for (var onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (onlinePlayer != null && faction.isBanned(onlinePlayer)) {
                    var offlinePlayer = Bukkit.getOfflinePlayer(onlinePlayer.getUniqueId());
                    if (offlinePlayer == null) {
                        continue;
                    }
                    arguments.add(offlinePlayer.getName());
                }
            }
        }
        return arguments;
    }
}
