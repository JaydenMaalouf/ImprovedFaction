package io.github.toberocat.improvedfactions.listeners;

import io.github.toberocat.improvedfactions.FactionsHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class OnPlayerDeathListener implements Listener {
    private FactionsHandler factionsHandler;

    public OnPlayerDeathListener(FactionsHandler factionsHandler) {
        this.factionsHandler = factionsHandler;
    }

    @EventHandler
    public void OnDeath(PlayerDeathEvent event) {
        var playerData = factionsHandler.getPlayerData(event.getEntity());
        if (playerData == null) {
            return;
        }

        var playerFaction = playerData.getPlayerFaction();
        if (playerFaction == null) {
            return;
        }

        playerFaction.getPowerManager().playerDeath();
    }
}
