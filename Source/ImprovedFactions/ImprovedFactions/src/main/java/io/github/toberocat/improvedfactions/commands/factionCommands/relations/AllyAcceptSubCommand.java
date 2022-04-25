package io.github.toberocat.improvedfactions.commands.factionCommands.relations;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class AllyAcceptSubCommand extends SubCommand {
    public AllyAcceptSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "allyaccept", "");
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
        if (!playerFaction.getRelationManager().hasInvite(requestedFaction)) {
            player.sendMessage(Language.getPrefix() + Language.format("&fYou have no invites from this faction"));
            return;
        }

        playerFaction.getRelationManager().acceptInvite(requestedFaction);
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        if (args.length != 0){
            return null;
        }

        var faction = factionsHandler.getFaction(player);
        if (faction == null){
            return null;
        }

        return faction.getRelationManager().getInvites();
    }
}
