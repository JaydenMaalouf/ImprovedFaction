package io.github.toberocat.improvedfactions.listeners;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.data.Permissions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class OnInteract implements Listener {
    private FactionsHandler factionsHandler;

    public OnInteract(FactionsHandler factionsHandler) {
        this.factionsHandler = factionsHandler;
    }

    @EventHandler
    public void Interact(PlayerInteractEvent event) {
        if (!factionsHandler.getConfig().getBoolean("general.allowClaimProtection")) {
            return;
        }

        var playerData = factionsHandler.getPlayerData(event.getPlayer());
        if (playerData.getBypass()) {
            return;
        }

        if (event.getClickedBlock() == null) {
            return;
        }

        var chunkFaction = factionsHandler.getFaction(event.getClickedBlock().getChunk());
        if (chunkFaction == null) {
            return;
        }

        var playerFaction = playerData.getPlayerFaction();
        if (playerFaction == null) {
            return;
        }
        if (playerFaction != null && !chunkFaction.getRegistryName().equals(playerFaction.getRegistryName())) {
            event.setCancelled(true);
        } else if (!chunkFaction.hasPermission(event.getPlayer(), Permissions.BUILD_PERMISSION)) {
            event.setCancelled(true);
        }
    }
}
