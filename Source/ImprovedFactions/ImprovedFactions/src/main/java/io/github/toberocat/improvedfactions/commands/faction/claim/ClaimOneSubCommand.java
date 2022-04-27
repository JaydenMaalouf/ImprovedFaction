package io.github.toberocat.improvedfactions.commands.faction.claim;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.data.Permissions;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.utility.ClaimStatus.Status;

import org.bukkit.entity.Player;

import java.util.List;

public class ClaimOneSubCommand extends SubCommand {
    public ClaimOneSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "one", "claim.one", LangMessage.CLAIM_ONE_DESCRIPTION);
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings()
                .setNeedsFaction(SubCommandSettings.NYI.Yes)
                .setAllowAliases(true)
                .setNeedsAdmin(false)
                .setFactionPermission(Permissions.CLAIM_CHUNK_PERMISSION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        player.sendMessage("Trynna claim faction");
        var faction = factionsHandler.getFaction(player);
        if (faction == null) {
            return;
        }

        faction.claimChunk(player.getLocation().getChunk(), result ->
        {            
            if (result.getClaimStatus() == Status.SUCCESS) {
                player.sendMessage("Claimed chunk!");
            }
            else{
                player.sendMessage("Failed to claim chunk. Tell the admin.");
            }
        });
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
