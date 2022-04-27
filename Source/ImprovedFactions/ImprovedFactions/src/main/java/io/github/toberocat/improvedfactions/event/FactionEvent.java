package io.github.toberocat.improvedfactions.event;

import io.github.toberocat.improvedfactions.factions.Faction;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class FactionEvent extends Event implements Cancellable {
    protected boolean isCancelled = false;
    protected Faction faction;

    public FactionEvent(Faction faction) {
        this.faction = faction;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return new HandlerList();
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }
}
