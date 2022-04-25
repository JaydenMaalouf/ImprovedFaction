package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.language.Parseable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KickSubCommand extends SubCommand {
    public KickSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "kick", LangMessage.KICK_DESCRIPTION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        var playerFaction = factionsHandler.getFaction(player);
        if (playerFaction != null) {
            if (playerFaction.getPlayerRank(player).isAdmin()) {
                if (args.length >= 1) {
                    kick(player, Bukkit.getOfflinePlayer(args[0]));
                } else {
                    CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
                }
            } else {
                CommandExecuteError(CommandExecuteError.OnlyAdminCommand, player);
            }
        } else {
            CommandExecuteError(CommandExecuteError.NoFactionPermission, player);
        }
    }

    public void kick(Player player, OfflinePlayer kicked) {
        var faction = factionsHandler.getFaction(player);
        if (faction.isFrozen()) {
            CommandExecuteError(CommandExecuteError.Frozen, player);
            return;
        }

        if (faction.leave(kicked)) {
            Language.sendMessage(LangMessage.KICK_SUCCESS_SENDER, player,
                    new Parseable("{kicked}", kicked.getName()));
            if (kicked.isOnline()) {
                var kickedPlayer = kicked.getPlayer();
                Language.sendMessage(LangMessage.KICK_SUCCESS_RECEIVER, kickedPlayer,
                        new Parseable("{faction_displayname}", faction.getDisplayName()));
            }
        } else {
            CommandExecuteError(CommandExecuteError.OtherError, player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        List<String> arguments = new ArrayList<>();
        if (args.length == 1) {
            var playerFaction = factionsHandler.getFaction(player);
            for (var factionMember : playerFaction.getMembers()) {
                if (factionMember != null) {
                    var offlinePlayer = Bukkit.getPlayer(factionMember.getUuid());
                    if (offlinePlayer == null) {
                        continue;
                    }
                    arguments.add(offlinePlayer.getName());
                }
            }
        }
        return arguments;
    }

    @Override
    protected boolean CommandDisplayCondition(Player player, String[] args) {
        var result = super.CommandDisplayCondition(player, args);
        var playerFaction = factionsHandler.getFaction(player);
        if (playerFaction == null) {
            result = false;
            return result;
        }
        if (!playerFaction.getPlayerRank(player).isAdmin()) {
            result = false;
        }
        return result;
    }
}
