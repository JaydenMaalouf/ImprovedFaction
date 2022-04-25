package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.factionCommands.adminSubCommands.*;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class AdminSubCommand extends SubCommand {
    public AdminSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "admin", LangMessage.ADMIN_DESCRIPTION);
        subCommands.add(new DisbandFactionCommand(factionsHandler));
        subCommands.add(new JoinPrivateAdminSubCommand(factionsHandler));
        subCommands.add(new GPowerSubCommand(factionsHandler));
        subCommands.add(new UUnclaimSubCommand(factionsHandler));
        subCommands.add(new SafezoneSubCommand(factionsHandler));
        subCommands.add(new FonlineSubCommand(factionsHandler));
        subCommands.add(new ViewReportsSubCommand(factionsHandler));
        subCommands.add(new RenameSubCommand(factionsHandler));
        subCommands.add(new ForceJoin(factionsHandler));
        subCommands.add(new RemoveReport(factionsHandler));
        subCommands.add(new ClearReports(factionsHandler));
        subCommands.add(new FrozeAdminCommand(factionsHandler));
        subCommands.add(new Permanent(factionsHandler));
        subCommands.add(new ByPassSubCommand(factionsHandler));
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
