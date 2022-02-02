package io.github.toberocat.core.utility.events.faction;

import io.github.toberocat.core.utility.factions.Faction;
import org.bukkit.entity.Player;

public class FactionCreateEvent extends FactionEventCancelledable {

    private Player player;

    public FactionCreateEvent(Faction faction, Player player) {
        super(faction);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
