package io.github.toberocat.improvedfactions.listeners;

import io.github.toberocat.improvedfactions.BlockWatcher;
import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.factions.Faction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class OnBlockPlace implements Listener {
    private FactionsHandler factionsHandler;
    private BlockWatcher blockWatcher;

    public OnBlockPlace(FactionsHandler factionsHandler, BlockWatcher blockWatcher) {
        this.factionsHandler = factionsHandler;
        this.blockWatcher = blockWatcher;
    }

    @EventHandler
    public void OnPlace(BlockPlaceEvent event) {
        if (!factionsHandler.getConfig().getBoolean("general.allowClaimProtection")) {
            return;
        }

        var playerData = factionsHandler.getPlayerData(event.getPlayer());
        if (playerData.getBypass()) {
            return;
        }

        blockWatcher.addBlock(event.getBlock().getLocation(), event.getPlayer().getUniqueId());

        var chunkFaction = factionsHandler.getFaction(event.getBlock().getChunk());
        if (chunkFaction == null) {
            return;
        }

        if (playerData.getPlayerFaction() == null) {
            event.setCancelled(true);
        } else if (!chunkFaction.hasPermission(event.getPlayer(), Faction.BUILD_PERMISSION)) {
            event.setCancelled(true);
        }
    }
}
