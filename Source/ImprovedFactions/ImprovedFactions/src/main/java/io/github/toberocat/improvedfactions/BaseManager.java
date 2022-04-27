package io.github.toberocat.improvedfactions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.toberocat.improvedfactions.factions.Faction;

public class BaseManager {
  private String fileName;
  protected Faction faction;
  protected FactionsHandler factionsHandler;

  public BaseManager(String fileName, Faction faction, FactionsHandler factionsHandler) {
    this.fileName = fileName;
    this.faction = faction;
    this.factionsHandler = factionsHandler;
  }

  private String getFilePath() {
    var basePath = factionsHandler.getPlugin().getDataFolder();
    var settingsDirectory = Paths.get(basePath.getPath(), "faction", faction.getRegistryName(), fileName + ".yml");
    return settingsDirectory.toString();
  }

  public void save() throws IOException {
  }
  public void load() throws FileNotFoundException, IOException, InvalidConfigurationException {
  }

  protected YamlConfiguration internalLoad() throws FileNotFoundException, IOException, InvalidConfigurationException {
    var config = new YamlConfiguration();
    config.load(getFilePath());
    return config;
  }

  protected void internalSave(YamlConfiguration config) throws IOException {
    config.save(getFilePath());
  }
}
