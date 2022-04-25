package io.github.toberocat.improvedfactions.listeners;

import io.github.toberocat.improvedfactions.FactionsHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnLeave implements Listener {
    private FactionsHandler factionsHandler;

    public OnLeave(FactionsHandler factionsHandler) {
        this.factionsHandler = factionsHandler;
    }

    @EventHandler
    public void Leave(PlayerQuitEvent event) {
        factionsHandler.removePlayerData(event.getPlayer());
    }
}
