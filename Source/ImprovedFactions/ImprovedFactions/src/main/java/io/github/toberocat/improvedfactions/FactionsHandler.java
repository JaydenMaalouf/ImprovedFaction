package io.github.toberocat.improvedfactions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import io.github.toberocat.improvedfactions.commands.FactionCommand;
import io.github.toberocat.improvedfactions.data.PlayerData;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.listeners.ArrowHitListener;
import io.github.toberocat.improvedfactions.listeners.BlockExplosionListener;
import io.github.toberocat.improvedfactions.listeners.OnBlockBreak;
import io.github.toberocat.improvedfactions.listeners.OnBlockPlace;
import io.github.toberocat.improvedfactions.listeners.OnEntityDamage;
import io.github.toberocat.improvedfactions.listeners.OnEntityInteract;
import io.github.toberocat.improvedfactions.listeners.OnInteract;
import io.github.toberocat.improvedfactions.listeners.OnJoin;
import io.github.toberocat.improvedfactions.listeners.OnLeave;
import io.github.toberocat.improvedfactions.listeners.OnPlayerDeathListener;
import io.github.toberocat.improvedfactions.listeners.OnPlayerMove;
import io.github.toberocat.improvedfactions.listeners.OnWorldSaveListener;
import io.github.toberocat.improvedfactions.tab.FactionCommandTab;
import io.github.toberocat.improvedfactions.utility.BlockWatcher;
import net.milkbowl.vault.economy.Economy;
import io.github.toberocat.improvedfactions.commands.FDelCommand;
import io.github.toberocat.improvedfactions.commands.FDelPCommand;
import io.github.toberocat.improvedfactions.commands.FJoin;

public class FactionsHandler {
	private List<Faction> factions = new ArrayList<Faction>();
	private Map<UUID, PlayerData> playerData = new HashMap<>();

	private Economy economy;
	private Logger logger;
	private ImprovedFactionsMain factionsPlugin;
	private BlockWatcher blockWatcher;

	public static NamespacedKey FACTION_CLAIMED_KEY;

	public FactionsHandler(ImprovedFactionsMain factionsPlugin) {
		this.factionsPlugin = factionsPlugin;
		this.logger = factionsPlugin.getLogger();

		FACTION_CLAIMED_KEY = new NamespacedKey(factionsPlugin, "faction-claimed");

		this.blockWatcher = new BlockWatcher();
	}

	public ImprovedFactionsMain getPlugin() {
		return factionsPlugin;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setupCommands() {
		factionsPlugin.getServer().getPluginCommand("f").setExecutor(new FactionCommand(this));
		factionsPlugin.getServer().getPluginCommand("f").setTabCompleter(new FactionCommandTab(this));
		factionsPlugin.getServer().getPluginCommand("fdel").setExecutor(new FDelCommand(this));
		factionsPlugin.getServer().getPluginCommand("fdel").setTabCompleter(new FDelCommand(this));
		factionsPlugin.getServer().getPluginCommand("fdelP").setExecutor(new FDelPCommand(this));
		factionsPlugin.getServer().getPluginCommand("fdelP").setTabCompleter(new FDelPCommand(this));
		factionsPlugin.getServer().getPluginCommand("fjoin").setExecutor(new FJoin(this));
		factionsPlugin.getServer().getPluginCommand("fjoin").setTabCompleter(new FJoin(this));
	}

	public void setupConfig() {

		// Config defaults
		getConfig().addDefault("factions.maxMembers", 50);
		getConfig().addDefault("factions.startClaimPower", 10);
		getConfig().addDefault("factions.powerPerPlayer", 5);
		getConfig().addDefault("factions.powerLossPerDeath", 5);
		getConfig().addDefault("factions.regenerationPerRate", 1);
		getConfig().addDefault("factions.regenerationRate", 3600000);
		getConfig().addDefault("factions.minPower", 0);

		getConfig().addDefault("factions.permanent", false);

		getConfig().addDefault("general.noFactionPapi", "None");
		getConfig().addDefault("general.updateChecker", true);
		getConfig().addDefault("general.mapViewDistanceW", 7);
		getConfig().addDefault("general.mapViewDistanceH", 5);

		getConfig().addDefault("general.disableProtectionWhenFirstMemberGetsOnline", true);
		getConfig().addDefault("general.protectionEnableTime", "900");

		getConfig().addDefault("general.commandDescriptions", true);
		getConfig().addDefault("general.connectedChunks", false);
		getConfig().addDefault("general.allowClaimProtection", true);
		getConfig().addDefault("general.debugMode", false);
		getConfig().addDefault("general.allowNegativeBalance", false);
		getConfig().addDefault("general.messageType", "TITLE"); // TITLE, SUBTITLE, ACTIONBAR, ITEM
		getConfig().addDefault("general.wildnessText", "&2Wildness");
		getConfig().addDefault("general.safezoneText", "&bSafezone");
		getConfig().addDefault("general.worlds", new String[] {
				"world",
				"world_nether",
				"world_the_end"
		});

		getConfig().addDefault("performance.threadingMethod", "SYNC");
		// SYNC, THREADS, FUTURE_TASK, COMPLETABLE_FUTURE
		getConfig().addDefault("performance.stopwatch", false);

		getConfig().options().copyDefaults(true);
		factionsPlugin.saveConfig();
	}

	public FileConfiguration getConfig() {
		return factionsPlugin.getConfig();
	}

	public void setupEvents() {
		factionsPlugin.getServer().getPluginManager().registerEvents(new OnWorldSaveListener(this), factionsPlugin);
		factionsPlugin.getServer().getPluginManager().registerEvents(new OnPlayerMove(this), factionsPlugin);

		factionsPlugin.getServer().getPluginManager().registerEvents(new OnBlockBreak(this), factionsPlugin);
		factionsPlugin.getServer().getPluginManager().registerEvents(new OnBlockPlace(this, blockWatcher), factionsPlugin);
		factionsPlugin.getServer().getPluginManager().registerEvents(new OnInteract(this), factionsPlugin);
		factionsPlugin.getServer().getPluginManager().registerEvents(new OnEntityInteract(this), factionsPlugin);
		factionsPlugin.getServer().getPluginManager().registerEvents(new OnEntityDamage(this), factionsPlugin);
		factionsPlugin.getServer().getPluginManager().registerEvents(new ArrowHitListener(this), factionsPlugin);

		factionsPlugin.getServer().getPluginManager().registerEvents(new OnJoin(this), factionsPlugin);
		factionsPlugin.getServer().getPluginManager().registerEvents(new OnLeave(this), factionsPlugin);
		factionsPlugin.getServer().getPluginManager().registerEvents(new OnPlayerDeathListener(this), factionsPlugin);
		factionsPlugin.getServer().getPluginManager().registerEvents(new BlockExplosionListener(this, blockWatcher),
				factionsPlugin);
	}

	public void saveFactions() {
		for (var faction : getFactions()) {
			faction.save();
		}
		// var factionData = factionsPlugin.getFactionData();
		// factionData.getConfig().set("f", null);
		// factionData.saveConfig();
		// for (var faction : getFactions()) {
		// List<String> _members = new ArrayList<String>();
		// for (var factionMember : faction.getMembers()) {
		// if (factionMember != null) {
		// _members.add(factionMember.toString());
		// }
		// }

		// List<String> permissions = new ArrayList<>();
		// for (var key : faction.getSettings().getRanks().keySet()) {
		// var permission = faction.getPermission(key);
		// permissions.add(key + "::" + permission.toString());
		// }

		// List<String> flags = new ArrayList<>();
		// for (var key : faction.getSettings().getFlags().keySet()) {
		// flags.add(key + "::" + faction.getSettings().getFlags().get(key).toString());
		// }

		// factionData.getConfig().set("f." + faction.getRegistryName() + ".allies",
		// faction.getRelationManager().getAllies());
		// factionData.getConfig().set("f." + faction.getRegistryName() + ".enemies",
		// faction.getRelationManager().getEnemies());
		// factionData.getConfig().set("f." + faction.getRegistryName() + ".invites",
		// faction.getRelationManager().getInvites());

		// factionData.getConfig().set("f." + faction.getRegistryName() + ".permanent",
		// faction.isPermanent());
		// factionData.getConfig().set("f." + faction.getRegistryName() + ".frozen",
		// faction.isFrozen());

		// factionData.getConfig().set("f." + faction.getRegistryName() + ".owner",
		// faction.getOwner().toString());
		// factionData.getConfig().set("f." + faction.getRegistryName() +
		// ".claimedChunks", faction.getClaimChunks());
		// factionData.getConfig().set("f." + faction.getRegistryName() + ".maxPower",
		// faction.getPowerManager().getMaxPower());
		// factionData.getConfig().set("f." + faction.getRegistryName() +
		// ".displayName", faction.getDisplayName());
		// factionData.getConfig().set("f." + faction.getRegistryName() +
		// ".description", faction.getDescription());
		// factionData.getConfig().set("f." + faction.getRegistryName() + ".motd",
		// faction.getMotd());
		// factionData.getConfig().set("f." + faction.getRegistryName() + ".power",
		// faction.getPowerManager().getPower());
		// factionData.getConfig().set("f." + faction.getRegistryName() +
		// ".settings.flags", flags);
		// factionData.getConfig().set("f." + faction.getRegistryName() +
		// ".settings.permissions", permissions);
		// factionData.getConfig().set("f." + faction.getRegistryName() + ".members",
		// _members.toArray());
		// factionData.getConfig().set("f." + faction.getRegistryName() + ".banned",
		// Utils.listToStringList(faction.getBannedPlayers()));

		// factionData.saveConfig();
		// // for (FactionData dat : Faction.data) {
		// // dat.Save(faction, factionData);
		// // }
		// }
	}

	public void loadFactions() {
		for (var faction : getFactions()) {
			faction.load();
		}
		// var factionData = factionsPlugin.getFactionData();
		// if (factionData.getConfig().getConfigurationSection("f") == null) {
		// return;
		// }
		// for (var key :
		// factionData.getConfig().getConfigurationSection("f").getKeys(false)) {
		// var displayName = ChatColor.translateAlternateColorCodes('&',
		// factionData.getConfig().getString("f." + key + ".displayName"));

		// var members = new ArrayList<FactionMember>();
		// List<String> raw = factionData.getConfig().getStringList("f." + key +
		// ".members");
		// for (int i = 0; i < raw.size(); i++) {
		// var rawMember = raw.get(i);
		// members.set(i, FactionMember.fromString(rawMember));
		// }

		// List<String> rawFlags = factionData.getConfig().getStringList("f." + key +
		// ".settings.flags");
		// List<Flag> flags = new ArrayList<>();
		// for (String str : rawFlags) {
		// flags.add(Flag.fromString(str));
		// }

		// var owner = factionData.getConfig().getString("f." + key + ".owner");
		// if (owner == null) {
		// for (var member : members) {
		// if (member.getRank().getRegistryName().equals(OwnerRank.registry)) {
		// owner = member.getUuid().toString();
		// }
		// }
		// }

		// var uuid = UUID.fromString(owner);
		// Faction faction = new Faction(this, displayName);
		// faction.setOwner(uuid);
		// faction.setMembers(members);
		// for (int i = 0; i < flags.size(); i++) {
		// faction.getSettings().getFlags().put(rawFlags.get(i).split("::")[0],
		// flags.get(i));
		// }

		// List<String> rawBanned = factionData.getConfig().getStringList("f." + key +
		// ".banned");
		// List<UUID> bannedPlayers = new ArrayList<>();

		// for (String rawBan : rawBanned) {
		// try {
		// rawBan = rawBan.replace("]", "").replace("[", "");
		// bannedPlayers.add(UUID.fromString(rawBan.trim()));
		// } catch (IllegalArgumentException exception) {
		// logger.warning("&cCouldn't load banned");
		// }
		// }

		// faction.setPowerManager(new PowerManager(faction));
		// faction.getPowerManager().setPower(factionData.getConfig().getInt("f." + key
		// + ".power"));
		// faction.getPowerManager().setMaxPower(factionData.getConfig().getInt("f." +
		// key + ".maxPower"));
		// faction.getPowerManager().startRegenerationThread();

		// faction.setRelationManager(new RelationManager(faction));
		// faction.getRelationManager()
		// .setAllies((ArrayList<String>) factionData.getConfig().getStringList("f." +
		// key + ".allies"));
		// faction.getRelationManager()
		// .setEnemies((ArrayList<String>) factionData.getConfig().getStringList("f." +
		// key + ".enemies"));
		// faction.getRelationManager()
		// .setInvites((ArrayList<String>) factionData.getConfig().getStringList("f." +
		// key + ".invites"));

		// if (factionData.getConfig().contains("f." + key + ".permanent")) {
		// faction.setPermanent(factionData.getConfig().getBoolean("f." + key +
		// ".permanent"));
		// }
		// if (factionData.getConfig().contains("f." + key + ".frozen")) {
		// faction.setFrozen(factionData.getConfig().getBoolean("f." + key +
		// ".frozen"));
		// }

		// faction.setClaimChunks(factionData.getConfig().getInt("f." + key +
		// ".claimedChunks"));
		// faction.setBannedPlayers(bannedPlayers);
		// faction.setMotd(factionData.getConfig().getString("f." + key + ".motd"));
		// faction.setDescription(factionData.getConfig().getString("f." + key +
		// ".description"));
		// for (var perm : factionData.getConfig().getStringList("f." + key +
		// ".settings.permissions")) {
		// var perms = perm.split("::");
		// faction.getSettings().getRanks().put(perms[0],
		// FactionRankPermission.fromString(perm));
		// }

		// addFaction(faction);
		// // for (FactionData dat : Faction.data) {
		// // dat.Load(faction, data);
		// // }
		// }
	}

	public List<String> getWorlds() {
		return getConfig().getStringList("general.worlds");
	}

	public boolean hasWorld(World world) {
		return getWorlds().contains(world.getName());
	}

	public boolean removeFaction(Faction faction) {
		return factions.remove(faction);
	}

	public PlayerData getPlayerData(Player player) {
		return getPlayerData(player.getUniqueId());
	}

	public PlayerData getPlayerData(UUID player) {
		return playerData.get(player);
	}

	public Faction createFaction(String factionName, Player owner) {
		return createFaction(factionName, owner.getUniqueId());
	}

	public Faction createFaction(String factionName, UUID owner) {
		var faction = new Faction(factionName, this, logger);
		faction.setOwner(owner);
		addFaction(faction);
		return faction;
	}

	public void addFaction(Faction faction) {
		factions.add(faction);
	}

	public String getVersion() {
		return factionsPlugin.getVersion();
	}

	public ConsoleCommandSender getConsoleSender() {
		return factionsPlugin.getServer().getConsoleSender();
	}

	public NamespacedKey createNamespacedKey(String key) {
		return new NamespacedKey(factionsPlugin, key);
	}

	public boolean setupEconomy() {
		if (factionsPlugin.getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}

		var rsp = factionsPlugin.getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}

		economy = rsp.getProvider();
		return economy != null;
	}

	public Economy getEconomy() {
		return economy;
	}

	public List<Faction> getFactions() {
		return factions;
	}

	public List<String> getFactionNames() {
		return getFactions().stream().map(Faction::getRegistryName).toList();
	}

	public List<String> getOnlinePlayerNames() {
		return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
	}

	public List<String> getFactionDisplayNames() {
		return getFactions().stream().map(Faction::getDisplayName).toList();
	}

	public Faction getFaction(String factionName) {
		var sanitizedName = ChatColor.stripColor(factionName);
		for (var faction : getFactions()) {
			if (faction.getRegistryName().equals(sanitizedName)) {
				return faction;
			}
		}
		return null;
	}

	public Faction getFaction(Player player) {
		return getFaction(player.getUniqueId());
	}

	public Faction getFaction(UUID player) {
		for (var faction : getFactions()) {
			var isMember = faction.isMember(player);
			if (isMember) {
				return faction;
			}
		}

		return null;
	}

	public Faction getFaction(Chunk chunk) {
		if (chunk == null) {
			return null;
		}

		var container = chunk.getPersistentDataContainer();
		if (container.has(FACTION_CLAIMED_KEY, PersistentDataType.STRING)) {
			var factionRegistry = container.get(FACTION_CLAIMED_KEY, PersistentDataType.STRING);
			return getFaction(factionRegistry);
		}

		return null;
	}

	public void addPlayerData(Player player) {
		var data = new PlayerData();
		data.setPlayerFaction(getFaction(player));
		playerData.put(player.getUniqueId(), data);
	}

	public void removePlayerData(Player player) {
		playerData.remove(player.getUniqueId());
	}
}
