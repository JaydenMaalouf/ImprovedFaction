package io.github.toberocat.improvedfactions.listeners;

import io.github.toberocat.improvedfactions.FactionsHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldSaveEvent;

public class OnWorldSaveListener implements Listener {

    private FactionsHandler _factionsHandler;
    public OnWorldSaveListener(FactionsHandler factionsHandler){
        _factionsHandler = factionsHandler;
    }

    @EventHandler
    public void onWorldSave(WorldSaveEvent event) {
        _factionsHandler.saveFactions();
    }

}
