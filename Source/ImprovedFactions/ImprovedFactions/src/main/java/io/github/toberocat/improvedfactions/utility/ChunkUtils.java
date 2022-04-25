package io.github.toberocat.improvedfactions.utility;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.event.chunk.ChunkClaimEvent;
import io.github.toberocat.improvedfactions.event.chunk.ChunkUnclaimEvent;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChunkUtils {
    private static boolean canOverclaim(Chunk chunk, Faction wantToClaimFaction) {
        Faction faction = GetFactionClaimedChunk(chunk);

        if (faction == null) return true;
        if (faction.getRegistryName().equals(wantToClaimFaction.getRegistryName())) return false;

        if (faction.getPowerManager().getPower() >= faction.getClaimedChunks()) return false;

        if (!isCorner(chunk, wantToClaimFaction.getRegistryName())) return false;
        for (Player player : FactionUtils.getPlayersOnline(faction)) {
            player.sendMessage(Language.getPrefix() +
                    Language.format("&6&lWarning: &e" +
                            wantToClaimFaction.getDisplayName() + "&f claimed a chunk from your land!"));
        }
        return true;
    }

    private static boolean isCorner(Chunk chunk, String rg) {
        var neighbours = GetNeighbourChunks(chunk);
        for (var neighbour : neighbours) {
            if (GetFactionClaimedChunk(neighbour) == null ||
                    GetFactionClaimedChunk(chunk).getRegistryName().equals(rg)) return true;
        }
        return false;
    }

    public static Vector2[] GetNeighbourChunks(Vector2 chunk) {
        Vector2[] neighbours = new Vector2[4];

        neighbours[0] = new Vector2(chunk.getX() - 1, chunk.getY());
        neighbours[2] = new Vector2(chunk.getX() + 1, chunk.getY());

        neighbours[1] = new Vector2(chunk.getX(), chunk.getY() - 1);
        neighbours[3] = new Vector2(chunk.getX(), chunk.getY() + 1);

        return neighbours;
    }

    public static Chunk[] GetNeighbourChunks(Chunk chunk) {
        Chunk[] neighbours = new Chunk[4];
        int centerX = chunk.getX();
        int centerZ = chunk.getZ();

        neighbours[0] = chunk.getWorld().getChunkAt(centerX - 1, centerZ);
        neighbours[2] = chunk.getWorld().getChunkAt(centerX + 1, centerZ);

        neighbours[1] = chunk.getWorld().getChunkAt(centerX, centerZ - 1);
        neighbours[3] = chunk.getWorld().getChunkAt(centerX, centerZ + 1);

        return neighbours;
    }
}
