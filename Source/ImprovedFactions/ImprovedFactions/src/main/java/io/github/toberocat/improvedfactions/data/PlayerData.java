package io.github.toberocat.improvedfactions.data;

import io.github.toberocat.improvedfactions.factions.Faction;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;

public class PlayerData {
    private Faction playerFaction;
    private boolean autoClaim;
    private boolean autoUnclaim;
    private boolean bypass;
    private List<String> invitations;
    private Chunk currentChunk;
    private Chunk previousChunk;

    public PlayerData() {
        autoClaim = false;
        autoUnclaim = false;
        invitations = new ArrayList<>();
    }

    public boolean getAutoClaim() {
        return autoClaim;
    }

    public void setAutoClaim(boolean autoClaim) {
        this.autoClaim = autoClaim;
        if (autoClaim){
            autoUnclaim = false;
        }
    }

    public boolean getAutoUnclaim() {
        return autoUnclaim;
    }

    public void setAutoUnclaim(boolean autoUnclaim) {
        this.autoUnclaim = autoUnclaim;
        if (autoUnclaim){
            autoClaim = false;
        }
    }

    public Faction getPlayerFaction() {
        return playerFaction;
    }

    public List<String> getInvitations() {
        return invitations;
    }

    public void setPlayerFaction(Faction faction) {
        playerFaction = faction;
        if (faction == null){
            setAutoClaim(false);
            setAutoUnclaim(false);
        }
    }

    public void addInvitation(String invitation) {
        invitations.add(invitation);
    }

    public boolean getBypass() {
        return bypass;
    }

    public void setBypass(boolean bypass) {
        this.bypass = bypass;
    }

    public boolean setChunk(Chunk chunk) {
        if (currentChunk == chunk){
            return false;
        }

        previousChunk = currentChunk;
        currentChunk = chunk;

        return true;
    }

    public Chunk getCurrentChunk() {
        return currentChunk;
    }

    public Chunk getPreviousChunk() {
        return previousChunk;
    }
}
