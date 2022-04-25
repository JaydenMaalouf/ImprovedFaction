package io.github.toberocat.improvedfactions.listeners;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.data.Permissions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class OnBlockBreak implements Listener {
    private FactionsHandler factionsHandler;
    public OnBlockBreak(FactionsHandler factionsHandler) {
        this.factionsHandler = factionsHandler;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void OnBreak(BlockBreakEvent event) {
        if (!factionsHandler.getConfig().getBoolean("general.allowClaimProtection")) {
            return;
        }

        var playerData = factionsHandler.getPlayerData(event.getPlayer());
        if (playerData.getBypass()) {
            return;
        }

        var chunkFaction = factionsHandler.getFaction(event.getBlock().getChunk());
        if (chunkFaction == null) {
            return;
        }

        if (playerData.getPlayerFaction() == null) {
            event.setCancelled(true);
        } else if (!chunkFaction.hasPermission(event.getPlayer(), Permissions.BREAK_PERMISSION)) {
            event.setCancelled(true);
        }
    }
}
