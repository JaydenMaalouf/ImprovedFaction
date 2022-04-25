package io.github.toberocat.improvedfactions.listeners;

import io.github.toberocat.improvedfactions.FactionsHandler;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ArrowHitListener implements Listener {

    private FactionsHandler factionsHandler;

    public ArrowHitListener(FactionsHandler factionsHandler) {
        this.factionsHandler = factionsHandler;
    }

    public void ArrayHit(ProjectileHitEvent event) {
        var hitEntity = event.getHitEntity();
        if (hitEntity == null) {
            return;
        }

        if (event.getEntity() instanceof Arrow arrow) {
            if (!factionsHandler.getConfig().getBoolean("general.allowClaimProtection")) {
                return;
            }

            if (hitEntity instanceof Player hitPlayer) {
                var hitPlayerData = factionsHandler.getPlayerData(hitEntity.getUniqueId());
                if (hitPlayerData != null && hitPlayerData.getBypass()) {
                    return;
                }

                if (arrow.getShooter() instanceof Player shootingPlayer) {
                    var shootingPlayerData = factionsHandler.getPlayerData(shootingPlayer);
                    if (shootingPlayerData.getPlayerFaction() == hitPlayerData.getPlayerFaction()) {
                        event.setCancelled(true);
                    }
                } else {
                    var claimedFaction = factionsHandler.getFaction(event.getEntity().getLocation().getChunk());
                    if (claimedFaction == null)
                        return;

                    if (factionsHandler.getFaction(hitPlayer) == null) {
                        event.setCancelled(true);
                        return;
                    }

                    if (!claimedFaction.getRegistryName().equals(hitPlayerData.getPlayerFaction().getRegistryName())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
