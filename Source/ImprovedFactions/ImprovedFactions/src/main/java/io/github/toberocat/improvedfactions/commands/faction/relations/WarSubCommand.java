package io.github.toberocat.improvedfactions.commands.faction.relations;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarSubCommand extends SubCommand {
    public WarSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "war", "");
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings().setNeedsAdmin(true).setNeedsFaction(SubCommandSettings.NYI.Yes);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (args.length != 1) {
            CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
            return;
        }

        var requestedFaction = factionsHandler.getFaction(args[0]);
        if (requestedFaction == null) {
            Language.sendMessage(LangMessage.JOIN_ERROR_NO_FACTION_FOUND, player);
            return;
        }

        var playerFaction = factionsHandler.getFaction(player);
        if (playerFaction.getRegistryName().equals(requestedFaction.getRegistryName())) {
            player.sendMessage(Language.getPrefix() + Language.format("&fCannot be at war with yourself"));
            return;
        }

        if (requestedFaction.getRelationManager().isEnemies(playerFaction)) {
            player.sendMessage(Language.getPrefix() + Language.format("&fYou are already in war"));
            return;
        }

        requestedFaction.getRelationManager().beginWar(playerFaction);
        playerFaction.getRelationManager().beginWar(requestedFaction);
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        var playerFaction = factionsHandler.getFaction(player);
        if (playerFaction == null) {
            return null;
        }

        var registryList = new ArrayList<String>();
        for (var faction : factionsHandler.getFactions()) {
            if (faction.getRegistryName().equals(playerFaction.getRegistryName())) {
                continue;
            }
            if (playerFaction.getRelationManager().isAllies(faction)) {
                continue;
            }

            registryList.add(faction.getRegistryName());
        }
        return registryList;
    }
}
