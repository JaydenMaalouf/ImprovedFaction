package io.github.toberocat.improvedfactions.event.faction;

import io.github.toberocat.improvedfactions.factions.Faction;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FactionJoinEvent extends Event implements Cancellable {
    private Faction faction;
    private UUID player;

    private boolean isCancelled = false;
    private String cancelMessage = "";

    public FactionJoinEvent(Faction faction, Player player) {
        this.faction = faction;
        this.player = player.getUniqueId();
    }

    public FactionJoinEvent(Faction faction, UUID uuid) {
        this.faction = faction;
        this.player = uuid;
    }

    @Override
    public HandlerList getHandlers() {
        return new HandlerList();
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public Faction getFaction() {
        return faction;
    }
    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    public UUID getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        setPlayer(player.getUniqueId());
    }

    public void setPlayer(UUID uuid) {
        player = uuid;
    }

    public String getCancelMessage() {
        return cancelMessage;
    }

    public void setCancelMessage(String cancelMessage) {
        this.cancelMessage = cancelMessage;
    }
}
