package io.github.toberocat.core.commands.admin;

import io.github.toberocat.core.utility.command.SubCommand;
import io.github.toberocat.core.utility.command.SubCommandSettings;
import io.github.toberocat.core.utility.language.LangMessage;
import org.bukkit.entity.Player;

import java.util.List;

public class AdminSubCommand extends SubCommand {
    public AdminSubCommand() {
        super("admin", LangMessage.COMMAND_ADMIN_DESCRIPTION, true);
        subCommands.add(new AdminHardResetSubCommand());
        subCommands.add(new AdminDisbandSubCommand());
        subCommands.add(new AdminGetPlayerFactionSubCommand());
        subCommands.add(new AdminIsPlayerInFactionSubCommand());
        subCommands.add(new AdminRegenerateSubCommand());
        subCommands.add(new AdminPermanentSubCommand());
        subCommands.add(new AdminFreezeSubCommand());
        subCommands.add(new JoinPrivateFactionSubCommand());
        subCommands.add(new AdminTimeoutSubCommand());
        subCommands.add(new AdminRemoveTimeoutSubCommand());
        subCommands.add(new AdminBypassSubCommand());
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings().setUseWhenFrozen(true);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {

    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
