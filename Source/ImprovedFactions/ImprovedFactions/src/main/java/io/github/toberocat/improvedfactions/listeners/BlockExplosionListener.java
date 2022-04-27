package io.github.toberocat.improvedfactions.listeners;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.utility.BlockWatcher;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class BlockExplosionListener implements Listener {

    private FactionsHandler factionsHandler;
    private BlockWatcher blockWatcher;
    public BlockExplosionListener(FactionsHandler factionsHandler, BlockWatcher blockWatcher) {
        this.factionsHandler = factionsHandler;
        this.blockWatcher = blockWatcher;
    }

    @EventHandler
    public void EntityExplode(EntityExplodeEvent event) {
        var explosionCauser = blockWatcher.getBlockOwner(event.getEntity().getLocation());
        if (explosionCauser == null){
            return;
        }

        var explosionCauserData = factionsHandler.getPlayerData(explosionCauser);
        if (explosionCauserData.getBypass()) {
            return;
        }
        
        var explosionCauserFaction = factionsHandler.getFaction(explosionCauser);
        if (explosionCauserFaction == null){
            return;
        }

        for (var block : event.blockList()) {
            var explosionFaction = factionsHandler.getFaction(block.getChunk());
            if (explosionFaction.getRelationManager().isEnemies(explosionCauserFaction)) {
                event.setCancelled(false);
            }
            
            event.setCancelled(true);
        }
    }
}
