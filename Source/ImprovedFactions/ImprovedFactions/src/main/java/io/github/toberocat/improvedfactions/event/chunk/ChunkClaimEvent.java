package io.github.toberocat.improvedfactions.event.chunk;

import io.github.toberocat.improvedfactions.factions.Faction;
import org.bukkit.Chunk;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChunkClaimEvent extends Event {
    private Chunk chunk;
    private Faction faction;

    public ChunkClaimEvent(Chunk chunk, Faction faction) {
        this.chunk = chunk;
        this.faction = faction;
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }
}
