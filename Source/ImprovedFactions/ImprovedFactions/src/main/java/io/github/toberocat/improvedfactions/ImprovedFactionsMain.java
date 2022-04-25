package io.github.toberocat.improvedfactions;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.toberocat.improvedfactions.bar.Bar;
import io.github.toberocat.improvedfactions.bstat.Metrics;
import io.github.toberocat.improvedfactions.commands.FDelCommand;
import io.github.toberocat.improvedfactions.commands.FDelPCommand;
import io.github.toberocat.improvedfactions.commands.FJoin;
import io.github.toberocat.improvedfactions.commands.FactionCommand;
import io.github.toberocat.improvedfactions.data.PlayerData;
import io.github.toberocat.improvedfactions.extentions.Extension;
import io.github.toberocat.improvedfactions.extentions.ExtensionContainer;
import io.github.toberocat.improvedfactions.extentions.list.ExtensionListLoader;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionMember;
import io.github.toberocat.improvedfactions.factions.FactionSettings;
import io.github.toberocat.improvedfactions.gui.GuiListener;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.listeners.*;
import io.github.toberocat.improvedfactions.papi.FactionExpansion;
import io.github.toberocat.improvedfactions.ranks.Rank;
import io.github.toberocat.improvedfactions.reports.Report;
import io.github.toberocat.improvedfactions.reports.Warn;
import io.github.toberocat.improvedfactions.tab.FactionCommandTab;
import io.github.toberocat.improvedfactions.utility.*;
import io.github.toberocat.improvedfactions.utility.ChunkUtils;
import io.github.toberocat.improvedfactions.utility.configs.DataManager;
import io.github.toberocat.improvedfactions.utility.configs.JsonUtility;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.xeustechnologies.jcl.JarClassLoader;
import org.xeustechnologies.jcl.JclObjectFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ImprovedFactionsMain extends JavaPlugin {

	public static Map<String, ExtensionContainer> extensions = new HashMap<>();

	public static String VERSION = "BETAv5.0.7";

	public static UpdateChecker updateChecker;

	private PlayerMessages playerMessages;

	private SignMenuFactory signMenuFactory;
	private ProtocolManager protocolManager;
	private Economy economy;

	private DataManager languageData;
	private DataManager factionData;
	private DataManager messagesData;
	private DataManager extensionConfigData;
	private DataManager commandData;
	private DataManager chunkData;

	public static Warn WARNS;

	private Logger logger;
	private FactionsHandler factionsHandler;

	@Override
	public void onEnable() {
		logger = Bukkit.getLogger();
		logger.log(Level.INFO, Language.format("[Factions] Running " + VERSION + " of Improved Factions (Factions)"));

		// Create extension folder
		var extensionsDirectory = new File(getDataFolder().getPath(), "Extensions");
		extensionsDirectory.mkdir();

		var guiListener = new GuiListener();

		Rank.Init();

		// Data Managers / Config
		// Language.yml
		languageData = new DataManager(this, "language.yml");
		factionData = new DataManager(this, "Data/factions.yml");
		messagesData = new DataManager(this, "Data/messages.yml");
		chunkData = new DataManager(this, "Data/chunkData.yml");
		extensionConfigData = new DataManager(this, "extensionConfig.yml");
		commandData = new DataManager(this, "commands.yml");

		playerMessages = new PlayerMessages(this);
		factionsHandler = new FactionsHandler(this);

		factionsHandler.setupConfig();

		// Language defaults
		languageData.getConfig().addDefault("prefix", "&c&lFactions:");

		languageData.getConfig().options().copyDefaults(true);
		languageData.saveConfig();

		// Add commands
		factionsHandler.setupCommands();

		// Add listeners
		factionsHandler.setupEvents();

		ClickActions.init(this);

		if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
			System.out.println("§cCan't load improved factions. Need to install protocolLib");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
			getLogger().info("Found PlaceholderAPI");
			FactionExpansion.init();
			new FactionExpansion().register();
		} else {
			getLogger().info("Found PlaceholderAPI");
		}

		Bukkit.getScheduler().runTaskLater(this, () -> {
			protocolManager = ProtocolLibrary.getProtocolManager();
			signMenuFactory = new SignMenuFactory(this);
			getLogger().info("IF enabled correctly");
		}, 1);

		// Load extentions
		try {
			ExtensionListLoader.RegenerateExtensionList();
		} catch (IOException e) {
			e.printStackTrace();
		}

		boolean exit = false;

		if (extensionsDirectory.exists()) {
			// try {
			// ExtensionLoader<Extension> loader = new ExtensionLoader<Extension>();
			for (var jar : extensionsDirectory.listFiles()) {
				if (jar.getName().endsWith(".jar")) {
					JarClassLoader jcl = new JarClassLoader();
					JclObjectFactory factory = JclObjectFactory.getInstance();
					jcl.add(jar.getPath());
					// Extension extension = loader.LoadClass(jar, "extension.Main",
					// Extension.class);
					Extension extension = (Extension) factory.create(jcl,
							"extension." + jar.getName().split("\\.")[0].toLowerCase() + ".Main");
					if (!extension.preLoad(this)) {
						getServer().getConsoleSender()
								.sendMessage("§7[Factions] §cExtension §6" + extension.getRegistry().getName()
										+ "§c disabled the loading. Remove it if this shouldn't happened");
						exit = true;
						return;
					}
					extensions.put(extension.getRegistry().getName(), new ExtensionContainer(extension, jcl));
				}
			}

			if (exit)
				return;
			// } catch (ClassNotFoundException e) {
			// getServer().getConsoleSender().sendMessage("§7[Factions] §cDidn't find any
			// extensions to be loaded");
			// }
		}

		ChunkUtils.Init();

		// Load language files
		File langFile = new File(getDataFolder().getPath() + "/lang");
		langFile.mkdir();

		try {
			File en_us = new File(langFile.getPath() + "/en_us.lang");
			if (en_us.createNewFile()) {
				LangMessage defaultMessages = new LangMessage();

				ObjectMapper mapper = new ObjectMapper();
				mapper.writerWithDefaultPrettyPrinter().writeValue(en_us, defaultMessages);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		Language.init(this, langFile);

		Bukkit.getScheduler().runTaskLater(this, () -> {
			if (!setupEconomy()) {
				getLogger().warning(
						Language.format(
								"Disabled faction economy! Needs Vault and an Economy plugin installed to enable it"));
			} else {
				getLogger().info(Language.format("Enabled faction economy"));
			}
		}, 0);

		// Others
		FactionSettings.Init();
		factionsHandler.loadFactions();

		try {
			if (getConfig().getBoolean("general.updateChecker")) {
				updateChecker = new UpdateChecker(VERSION,
						new URL("https://raw.githubusercontent.com/ToberoCat/ImprovedFaction/master/version.json"));
				if (!updateChecker.isNewestVersion()) {
					getConsoleSender().sendMessage(Language.getPrefix() +
							"§fA newer version of this plugin is available. Check it out: https://www.spigotmc.org/resources/improved-factions.95617/");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Player on : Bukkit.getOnlinePlayers()) {
			AddPlayerData(on);
		}

		int loaded = 0;
		for (ExtensionContainer container : extensions.values()) {
			container.getExtension().Enable(this);
			if (container.getExtension().isEnabled())
				loaded++;
		}

		if (loaded == 0) {
			getServer().getConsoleSender().sendMessage("§7[Factions] §cDidn't find any extensions to be loaded");
		} else {
			getServer().getConsoleSender().sendMessage("§7[Factions] §aSuccessfully loaded " + loaded +
					(loaded == 1 ? " extension" : " extensions"));
		}

		File reports = new File(getDataFolder().getPath() + "/Data/reports.json");
		if (!reports.exists()) {
			try {
				reports.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		File warns = new File(getDataFolder().getPath() + "/Data/warns.json");
		if (!warns.exists()) {
			try {
				warns.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		REPORTS = new ArrayList<>();
		WARNS = new Warn();

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			REPORTS = Arrays.asList(objectMapper.readValue(reports, Report[].class));
		} catch (IOException e) {
			getServer().getConsoleSender().sendMessage(
					"§7[Factions] §6Reports couldn't get loaded. File is probably empty");
			REPORTS = new ArrayList<>();
		}

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			WARNS = objectMapper.readValue(warns, Warn.class);
		} catch (IOException e) {
			getServer().getConsoleSender().sendMessage(
					"§7[Factions] §6Warns couldn't get loaded. File is probably empty");
			WARNS = new Warn();
		}

		Metrics metrics = new Metrics(this, 14810);
	}

	@Override
	public void onDisable() {
		factionsHandler.saveFactions();
		ChunkUtils.Save();
		Bar.Disable();
		playerMessages.SavePlayerMessages();

		for (String key : extensions.keySet()) {
			ExtensionContainer container = extensions.get(key);
			container.getExtension().Disable(this);
		}

		File reports = new File(getDataFolder().getPath() + "/Data/reports.json");
		JsonUtility.SaveObject(reports, REPORTS.toArray(new Report[0]));

		File warns = new File(getDataFolder().getPath() + "/Data/warns.json");
		JsonUtility.SaveObject(warns, WARNS);
	}

	public DataManager getFactionData() {
		if (factionData == null) {
			logger.log(Level.WARNING, "Instance of factionData is null");
		}
		return factionData;
	}

	public DataManager getLanguageData() {
		if (languageData == null) {
			logger.log(Level.WARNING, "Instance of languageData is null");
		}
		return languageData;
	}

	public DataManager getMessagesData() {
		if (messagesData == null) {
			logger.log(Level.WARNING, "Instance of messageData is null");
		}
		return messagesData;
	}

	public DataManager getExtConfigData() {
		if (extensionConfigData == null) {
			logger.log(Level.WARNING, "Instance of extConfigDaat is null");
		}
		return extensionConfigData;
	}

	public void reloadConfigs() {
		reloadConfig();
		languageData.reloadConfig();
		messagesData.reloadConfig();
		extensionConfigData.reloadConfig();
		factionData.reloadConfig();
	}

	public SignMenuFactory getSignMenuFactory() {
		return signMenuFactory;
	}

	public PlayerMessages getPlayerMessages() {
		return playerMessages;
	}

	public String getVersion() {
		return VERSION;
	}

	public DataManager getCommandData() {
		return commandData;
	}

	public void removeExtension(String extension) {
		ExtensionContainer container = extensions.get(extension);
		container.getExtension().Disable(this);
		extensions.remove(extension);
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}

		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager()
				.getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		economy = rsp.getProvider();
		return economy != null;
	}

	public DataManager getChunkData() {
		return chunkData;
	}

	public Economy getEconomy() {
		return economy;
	}

	public ProtocolManager getProtocolManager() {
		return protocolManager;
	}

	public void setProtocolManager(ProtocolManager protocolManager) {
		this.protocolManager = protocolManager;
	}
}
