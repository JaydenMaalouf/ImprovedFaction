package io.github.toberocat.improvedfactions.commands.faction.claim;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.data.Permissions;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class ClaimSubCommand extends SubCommand {

    public ClaimSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "claim", LangMessage.CLAIM_DESCRIPTION);
        addSubCommand(new ClaimOneSubCommand(factionsHandler));
        addSubCommand(new AutoClaimSubCommand(factionsHandler));
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings()
                .setNeedsAdmin(false)
                .setAllowAliases(true)
                .setNeedsFaction(SubCommandSettings.NYI.Yes)
                .setFactionPermission(Permissions.CLAIM_CHUNK_PERMISSION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        var faction = factionsHandler.getFaction(player);
        if (faction.isFrozen()) {
            CommandExecuteError(CommandExecuteError.Frozen, player);
            return;
        }

        player.sendMessage(args.length + " arguments");
        if (args.length == 0) {
            subCommands.get(0).CallSubCommand(player, args);
            return;
        }

        if (!this.CallSubCommands(player, args)) {
            Language.sendMessage(LangMessage.THIS_COMMAND_DOES_NOT_EXIST, player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return this.CallSubCommandsTab(player, args);
    }

    @Override
    protected boolean CommandDisplayCondition(Player player, String[] args) {
        var result = super.CommandDisplayCondition(player, args);
        var faction = factionsHandler.getFaction(player);
        if (faction == null) {
            return false;
        }

        if (!faction.hasPermission(player, Permissions.CLAIM_CHUNK_PERMISSION)) {
            return false;
        }

        return result;
    }
}
