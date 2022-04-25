package io.github.toberocat.improvedfactions.listeners;

import io.github.toberocat.improvedfactions.FactionsHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class OnEntityInteract implements Listener {
    private FactionsHandler factionsHandler;

    public OnEntityInteract(FactionsHandler factionsHandler) {
        this.factionsHandler = factionsHandler;
    }

    @EventHandler
    public void EntityInteract(PlayerInteractEntityEvent event) {
        if (!factionsHandler.getConfig().getBoolean("general.allowClaimProtection")) {
            return;
        }

        var playerData = factionsHandler.getPlayerData(event.getPlayer());
        if (playerData.getBypass()) {
            return;
        }

        var chunkFaction = factionsHandler.getFaction(event.getRightClicked().getLocation().getChunk());
        if (chunkFaction == null) {
            return;
        }

        var playerFaction = playerData.getPlayerFaction();
        if (playerFaction == null) {
            event.setCancelled(true);
            return;
        }

        if (!chunkFaction.getRegistryName().equals(playerFaction.getRegistryName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void HangingBreak(HangingBreakByEntityEvent event) {
        if (event.getRemover() instanceof Player removerPlayer) {
            if (!factionsHandler.getConfig().getBoolean("general.allowClaimProtection")) {
                return;
            }

            var removerPlayerData = factionsHandler.getPlayerData(removerPlayer);
            if (removerPlayerData.getBypass()) {
                return;
            }

            var chunkFaction = factionsHandler.getFaction(event.getEntity().getLocation().getChunk());
            if (chunkFaction == null) {
                return;
            }

            if (!chunkFaction.getRegistryName().equals(removerPlayerData.getPlayerFaction().getRegistryName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void ArmorStandManipulate(PlayerArmorStandManipulateEvent event) {
        if (!factionsHandler.getConfig().getBoolean("general.allowClaimProtection")) {
            return;
        }

        var playerData = factionsHandler.getPlayerData(event.getPlayer());
        if (playerData.getBypass()) {
            return;
        }

        var chunkFaction = factionsHandler.getFaction(event.getRightClicked().getLocation().getChunk());
        if (chunkFaction == null) {
            return;
        }

        var playerFaction = playerData.getPlayerFaction();
        if (playerFaction == null) {
            event.setCancelled(true);
            return;
        }

        if (!chunkFaction.getRegistryName().equals(playerFaction.getRegistryName())) {
            event.setCancelled(true);
        }
    }
}
