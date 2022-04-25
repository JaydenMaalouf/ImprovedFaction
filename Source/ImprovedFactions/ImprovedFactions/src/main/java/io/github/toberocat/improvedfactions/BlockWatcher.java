package io.github.toberocat.improvedfactions;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;

public class BlockWatcher {
  private Map<Location, UUID> placedBlocks;

  public UUID getBlockOwner(Location location) {
    return placedBlocks.get(location);
  }

  public void addBlock(Location location, UUID owner) {
    placedBlocks.putIfAbsent(location, owner);
  }

  public void removeBlock(Location location) {
    placedBlocks.remove(location);
  }
}
