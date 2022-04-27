package io.github.toberocat.improvedfactions.commands.admin;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class AdminSubCommand extends SubCommand {
    public AdminSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "admin", LangMessage.ADMIN_DESCRIPTION);
        addSubCommand(new AdminDisbandSubCommand(factionsHandler));
        addSubCommand(new AdminJoinPrivateSubCommand(factionsHandler));
        addSubCommand(new AdminPowerSubCommand(factionsHandler));
        addSubCommand(new AdminUnclaimSubCommand(factionsHandler));
        addSubCommand(new AdminSafezoneSubCommand(factionsHandler));
        addSubCommand(new AdminOnlinePlayersSubCommand(factionsHandler));
        //addSubCommand(new AdminViewReportsSubCommand(factionsHandler));
        addSubCommand(new AdminRenameSubCommand(factionsHandler));
        addSubCommand(new AdminForceJoinSubCommand(factionsHandler));
        //addSubCommand(new RemoveReport(factionsHandler));
        //addSubCommand(new AdminClearReportsSubCommand(factionsHandler));
        addSubCommand(new AdminFreezeAdminSubCommand(factionsHandler));
        addSubCommand(new AdminPermanentSubCommand(factionsHandler));
        addSubCommand(new AdminByPassSubCommand(factionsHandler));
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if(!this.CallSubCommands(player, args)) {
            Language.sendMessage(LangMessage.THIS_COMMAND_DOES_NOT_EXIST, player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return this.CallSubCommandsTab(player, args);
    }
}
