package io.github.toberocat.improvedfactions.commands.faction.owner;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.faction.JoinPrivateFactionSubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class InviteAcceptSubCommand extends SubCommand {
    public InviteAcceptSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "inviteaccept", "");
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings()
            .setNeedsAdmin(true)
            .setNeedsFaction(SubCommandSettings.NYI.Yes);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (args.length != 1) {
            CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
            return;
        }

        if (factionsHandler.getFaction(args[0]) == null) {
            player.sendMessage(Language.getPrefix() + "§cCoudln't find faction searching for");
            return;
        }

        new JoinPrivateFactionSubCommand(factionsHandler).joinPrivate(player, args[0]);
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return factionsHandler.getPlayerData(player).getInvitations();
    }
}
