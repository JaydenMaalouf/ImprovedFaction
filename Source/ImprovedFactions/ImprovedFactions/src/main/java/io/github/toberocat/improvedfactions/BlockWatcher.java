package io.github.toberocat.improvedfactions;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;

public class BlockWatcher {
  private Map<Location, UUID> PlacedBlocks;

  public UUID getBlockOwner(Location location) {
    return PlacedBlocks.get(location);
  }

  public void addBlock(Location location, UUID owner) {
    PlacedBlocks.putIfAbsent(location, owner);
  }
}
