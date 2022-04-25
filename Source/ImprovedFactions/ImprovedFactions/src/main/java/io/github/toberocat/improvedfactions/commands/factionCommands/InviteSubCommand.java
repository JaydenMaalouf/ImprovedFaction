package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.language.LangMessage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class InviteSubCommand extends SubCommand {
    public InviteSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "invite", LangMessage.INVITE_DESCRIPTION);
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings().setNeedsFaction(SubCommandSettings.NYI.Yes);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (args.length == 1) {
            invite(player, Bukkit.getPlayer(args[0]));
        } else {
            CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
        }
    }

    public void invite(Player player, Player playerToInvite) {
        var faction = factionsHandler.getFaction(player);
        if (faction.isFrozen()) {
            CommandExecuteError(CommandExecuteError.Frozen, player);
            return;
        }
        if (playerToInvite != null && playerToInvite.isOnline()) {
            CommandExecuteError(CommandExecuteError.PlayerNotFound, player);
        }

        faction.invitePlayer(player, playerToInvite);
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        var arguments = new ArrayList<String>();
        if (args.length == 1) {
            for (var onlinePlayer : Bukkit.getOnlinePlayers()) {
                arguments.add(onlinePlayer.getName());
            }
        }
        return arguments;
    }

    @Override
    protected boolean CommandDisplayCondition(Player player, String[] args) {
        var result = super.CommandDisplayCondition(player, args);
        var faction = factionsHandler.getFaction(player);
        if (faction == null) {
            result = false;
        } else if (!faction.hasPermission(player, Faction.INVITE_PERMISSION)) {
            result = false;
        }
        return result;
    }
}
