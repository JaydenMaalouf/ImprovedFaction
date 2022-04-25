package io.github.toberocat.improvedfactions.commands.factionCommands.adminSubCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.ranks.NewMemberRank;
import io.github.toberocat.improvedfactions.ranks.Rank;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class ForceJoin extends SubCommand {
    public ForceJoin(FactionsHandler factionsHandler) {
        super(factionsHandler, "forcejoin", "");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (args.length != 2) {
            return;
        }

        var offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        if (offlinePlayer == null) {
            CommandExecuteError(CommandExecuteError.PlayerNotFound, player);
            return;
        }

        if (!offlinePlayer.isOnline()) {
            player.sendMessage(Language.getPrefix() + "§Play is offline. Can't do this with a offline player");
            return;
        }

        var onlinePlayer = offlinePlayer.getPlayer();

        var existingFaction = factionsHandler.getFaction(onlinePlayer);
        if (existingFaction != null) {
            existingFaction.leave(onlinePlayer);
        }

        var newFaction = factionsHandler.getFaction(args[1]);
        if (newFaction == null){
            player.sendMessage(Language.getPrefix() + "§Can't find this faction");
            return;
        }

        newFaction.join(onlinePlayer, Rank.fromString(NewMemberRank.registry));
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        if (args.length == 2) {
            return factionsHandler.getFactionNames();
        } else {
            return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
        }
    }
}
