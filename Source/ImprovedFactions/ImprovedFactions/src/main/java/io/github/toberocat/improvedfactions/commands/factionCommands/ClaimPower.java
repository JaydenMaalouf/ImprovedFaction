package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class ClaimPower extends SubCommand {
    public ClaimPower(FactionsHandler factionsHandler) {
        super(factionsHandler, "claimpower", "");
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings().setNeedsFaction(SubCommandSettings.NYI.Yes);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        var faction = factionsHandler.getFaction(player);
        player.sendMessage(Language.getPrefix()+"§eLeft claimpower: §b" + (faction.getPowerManager().getPower() - faction.getClaimedChunks()));
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
