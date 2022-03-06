package io.github.toberocat.core.commands.extension;

import io.github.toberocat.core.utility.command.SubCommand;
import io.github.toberocat.core.utility.command.SubCommandSettings;
import org.bukkit.entity.Player;

import java.util.List;

public class ExtensionDownloadSubCommand extends SubCommand {
    public ExtensionDownloadSubCommand() {
        super("download", "extension.download", "", false);
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings().setArgLength(0).setUseWhenFrozen(true);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {

    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
