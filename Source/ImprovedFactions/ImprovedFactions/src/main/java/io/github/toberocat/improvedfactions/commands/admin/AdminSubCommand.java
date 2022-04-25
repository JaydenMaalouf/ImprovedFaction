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
        subCommands.add(new AdminDisbandSubCommand(factionsHandler));
        subCommands.add(new AdminJoinPrivateSubCommand(factionsHandler));
        subCommands.add(new AdminPowerSubCommand(factionsHandler));
        subCommands.add(new AdminUnclaimSubCommand(factionsHandler));
        subCommands.add(new AdminSafezoneSubCommand(factionsHandler));
        subCommands.add(new AdminOnlinePlayersSubCommand(factionsHandler));
        //subCommands.add(new AdminViewReportsSubCommand(factionsHandler));
        subCommands.add(new AdminRenameSubCommand(factionsHandler));
        subCommands.add(new AdminForceJoinSubCommand(factionsHandler));
        //subCommands.add(new RemoveReport(factionsHandler));
        //subCommands.add(new AdminClearReportsSubCommand(factionsHandler));
        subCommands.add(new AdminFreezeAdminSubCommand(factionsHandler));
        subCommands.add(new AdminPermanentSubCommand(factionsHandler));
        subCommands.add(new AdminByPassSubCommand(factionsHandler));
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
