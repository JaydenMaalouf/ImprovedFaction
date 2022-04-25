package io.github.toberocat.improvedfactions;

import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.ranks.Rank;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ImprovedFactionsMain extends JavaPlugin {
	public static String VERSION = "BETAv5.0.7";

	private Logger logger;
	private FactionsHandler factionsHandler;

	@Override
	public void onEnable() {
		logger = Bukkit.getLogger();

		if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
			logger.log(Level.INFO, "§cCan't load improved factions. Need to install protocolLib");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		logger.log(Level.INFO, Language.format("[Factions] Running " + VERSION + " of Improved Factions (Factions)"));

		// Create extension folder
		var extensionsDirectory = new File(getDataFolder().getPath(), "Extensions");
		extensionsDirectory.mkdir();

		Rank.Init();

		// Data Managers / Config
		// Language.yml
		factionsHandler = new FactionsHandler(this);
		factionsHandler.setupConfig();
		factionsHandler.setupCommands();
		factionsHandler.setupEvents();
		factionsHandler.loadFactions();
		factionsHandler.setupEconomy();

		for (var onlinePlayers : Bukkit.getOnlinePlayers()) {
			factionsHandler.addPlayerData(onlinePlayers.getPlayer());
		}
	}

	@Override
	public void onDisable() {
		factionsHandler.saveFactions();
	}

	public String getVersion() {
		return VERSION;
	}
}
