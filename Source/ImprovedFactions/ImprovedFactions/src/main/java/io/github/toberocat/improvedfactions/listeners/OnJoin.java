package io.github.toberocat.improvedfactions.listeners;

import io.github.toberocat.improvedfactions.FactionsHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoin implements Listener {
    private FactionsHandler factionsHandler;

    public OnJoin(FactionsHandler factionsHandler) {
        this.factionsHandler = factionsHandler;
    }

    @EventHandler
    public void Join(PlayerJoinEvent event) {
        factionsHandler.addPlayerData(event.getPlayer());

        //TODO: implement messagebox
        //ImprovedFactionsMain.getPlugin().getPlayerMessages().ReceiveMessages(event.getPlayer());
    }
}
