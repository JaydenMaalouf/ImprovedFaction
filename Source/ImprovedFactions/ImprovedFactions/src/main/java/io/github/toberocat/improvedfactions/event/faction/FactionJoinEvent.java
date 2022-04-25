package io.github.toberocat.improvedfactions.event.faction;

import io.github.toberocat.improvedfactions.factions.Faction;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class FactionJoinEvent extends Event implements Cancellable {

    private boolean isCancelled = false;
    private static final HandlerList HANDLERS = new HandlerList();

    private Faction faction;
    private String cancelMessage = "";

    private UUID playerId;
    public FactionJoinEvent(Faction faction, Player player) {
        this.faction = faction;
        playerId = player.getUniqueId();
    }

    public FactionJoinEvent(Faction faction, UUID uuid) {
        this.faction = faction;
        playerId = uuid;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
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
        return playerId;
    }

    public void setPlayer(Player player) {
        setPlayer(player.getUniqueId());
    }

    public void setPlayer(UUID uuid) {
        playerId = uuid;
    }

    public String getCancelMessage() {
        return cancelMessage;
    }

    public void setCancelMessage(String cancelMessage) {
        this.cancelMessage = cancelMessage;
    }
}
