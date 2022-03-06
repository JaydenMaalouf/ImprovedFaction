package io.github.toberocat.core.commands.zones;

import io.github.toberocat.MainIF;
import io.github.toberocat.core.utility.claim.ClaimManager;
import io.github.toberocat.core.utility.command.SubCommand;
import io.github.toberocat.core.utility.command.SubCommandSettings;
import io.github.toberocat.core.utility.language.LangMessage;
import io.github.toberocat.core.utility.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class SafeZoneSubCommand extends SubCommand {
    public SafeZoneSubCommand() {
        super("safezone", "zones.safezone", LangMessage.COMMAND_ZONES_SAFEZONE_DESCRIPTION, false);
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings().setUseWhenFrozen(true);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        MainIF.getIF().getClaimManager().protectChunk(ClaimManager.SAFEZONE_REGISTRY, player.getLocation().getChunk());
        Language.sendMessage(LangMessage.COMMAND_ZONES_SAFEZONE_CLAIM, player);
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
