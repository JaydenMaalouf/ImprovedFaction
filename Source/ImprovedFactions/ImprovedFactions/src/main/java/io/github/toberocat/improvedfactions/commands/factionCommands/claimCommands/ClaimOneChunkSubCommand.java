package io.github.toberocat.improvedfactions.commands.factionCommands.claimCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.language.LangMessage;

import org.bukkit.entity.Player;

import java.util.List;

public class ClaimOneChunkSubCommand extends SubCommand {
    public ClaimOneChunkSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "one", "claim.one", LangMessage.CLAIM_ONE_DESCRIPTION);
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings()
                .setNeedsFaction(SubCommandSettings.NYI.Yes)
                .setAllowAliases(true)
                .setNeedsAdmin(false)
                .setFactionPermission(Faction.CLAIM_CHUNK_PERMISSION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        var faction = factionsHandler.getFaction(player);
        if (faction == null) {
            return;
        }

        faction.claimChunk(player.getLocation().getChunk(), null);
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
