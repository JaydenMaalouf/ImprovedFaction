package io.github.toberocat.improvedfactions.utility;

import org.bukkit.Chunk;

public class ChunkUtils {

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
