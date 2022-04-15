package io.github.toberocat;

import io.github.toberocat.core.bstat.Bstat;
import io.github.toberocat.core.commands.FactionCommand;
import io.github.toberocat.core.factions.FactionUtility;
import io.github.toberocat.core.factions.permission.FactionPerm;
import io.github.toberocat.core.factions.rank.Rank;
import io.github.toberocat.core.listeners.*;
import io.github.toberocat.core.utility.Result;
import io.github.toberocat.core.utility.Utility;
import io.github.toberocat.core.utility.calender.TimeCore;
import io.github.toberocat.core.utility.claim.ClaimManager;
import io.github.toberocat.core.utility.config.Config;
import io.github.toberocat.core.utility.config.ConfigManager;
import io.github.toberocat.core.utility.config.DataManager;
import io.github.toberocat.core.utility.data.DataAccess;
import io.github.toberocat.core.utility.dynamic.loaders.DynamicLoader;
import io.github.toberocat.core.utility.events.ConfigSaveEvent;
import io.github.toberocat.core.utility.events.bukkit.PlayerJoinOnReloadEvent;
import io.github.toberocat.core.utility.items.ItemCore;
import io.github.toberocat.core.utility.json.JsonUtility;
import io.github.toberocat.core.utility.language.LangMessage;
import io.github.toberocat.core.utility.language.Language;
import io.github.toberocat.core.utility.map.MapHandler;
import io.github.toberocat.core.utility.messages.MessageSystem;
import io.github.toberocat.core.utility.settings.FactionSettings;
import io.github.toberocat.core.utility.settings.PlayerSettings;
import io.github.toberocat.core.utility.version.Version;
import io.github.toberocat.versions.nms.NMSFactory;
import io.github.toberocat.versions.nms.NMSInterface;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getPluginManager;

/**
 * This is the main class of the Improved Factions plugin
 */
public final class MainIF extends JavaPlugin {

    private static final Version VERSION = Version.from("1.0");
    //<editor-fold desc="Variables">
    private static MainIF INSTANCE;
    private static Economy economy;
    private static ConfigManager configManager;
    private final List<ConfigSaveEvent> saveEvents = new ArrayList<>();
    private final Map<String, Config> configMap = new HashMap<>();
    private final Map<String, ArrayList<String>> backupFile = new HashMap<>(); // Delete the backup map after backup got restored
    private final Map<String, DataManager> dataManagers = new HashMap<>();
    private NMSInterface nms;
    private boolean standby = false;
    private ClaimManager claimManager;
    //</editor-fold>

    //<editor-fold desc="Overrides">

    /**
     * Send a message to the server.
     * This won't send a message, if the level isn't represented in debug.logLevel (config.yml)
     *
     * @param level   That's the level you want to log
     * @param message The message you want to get logged
     */
    public static void LogMessage(Level level, String message) {
        Utility.run(() -> {
            List<String> values;
            if (!INSTANCE.isEnabled()) {
                values = Arrays.asList("INFO", "WARNING", "SEVERE");
            } else {
                values = configManager.getValue("debug.logLevel");
            }

            if (!values.contains(level.toString())) return;

            if (configManager.getValue("general.colorConsole")) {
                //Bukkit.getLogger().log(level, );
                Bukkit.getLogger().log(level, Language.format("&7[&e&lImprovedFactions&7] " + message));
            } else {
                Bukkit.getLogger().log(level,
                        ChatColor.stripColor(Language.format("&7[&e&lImprovedFactions&7] " + message)));
            }
        });
    }

    /**
     * Get the instance of this plugin
     *
     * @return The instance of this plugin
     */
    public static MainIF getIF() {
        return INSTANCE;
    }
    //</editor-fold>

    //<editor-fold desc="Public functions">

    /**
     * Get the economy for this plugin
     * The economy can be null, if Vault + A economy supoorting pluign (e.g.: Essentails) is not found
     */
    public static Economy getEconomy() {
        return economy;
    }

    /**
     * Get the current plugin version
     *
     * @return The version
     */
    public static Version getVersion() {
        return VERSION;
    }

    /**
     * Get the manager to add, load and reload config data
     *
     * @return The configManager instance
     */
    public static ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Don't call this manually.
     * This will get called by the minecraft server
     */
    @Override
    public void onEnable() {
        Utility.run(() -> {
            INSTANCE = this;

            GenerateConfigs();

            LoadListeners();

            if (!InitializeCores()) return;
            if (!LoadPluginVersion()) return;

            LoadPluginDependencies();

            FactionCommand command = new FactionCommand();
            getServer().getPluginCommand("faction").setExecutor(command);
            getServer().getPluginCommand("faction").setTabCompleter(command);

            for (Player player : getServer().getOnlinePlayers()) {
                Bukkit.getPluginManager().callEvent(new PlayerJoinOnReloadEvent(player));
                PlayerJoinListener.PLAYER_JOINS.put(player.getUniqueId(), System.currentTimeMillis());
            }

            DynamicLoader.enable();
        });
    }

    /**
     * Don't call this manually.
     * This will get called by the minecraft server
     */
    @Override
    public void onDisable() {
        Utility.run(() -> {
            SaveConfigs();
            DataAccess.disable();
            DynamicLoader.disable();

            saveEvents.clear();
            backupFile.clear();
            dataManagers.clear();
            configMap.clear();
            PlayerJoinListener.PLAYER_JOINS.clear();

            INSTANCE = null;
        });
    }

    //</editor-fold>

    //<editor-fold desc="Loading functions">

    /**
     * This saves everything that can be saved to prevent data loss when an error happens.
     * In a normal case, it will only put the plugin in standby, to enable simple land protection
     * This formats the error message
     *
     * @param shutdownMessage This message will be printed to console before disabling the plugin
     * @see Language#format(String)
     */
    public void SaveShutdown(String shutdownMessage) {
        Utility.run(() -> {
            if (standby) {
                LogMessage(Level.SEVERE, "&c" + shutdownMessage);
                return;
            }
            standby = true;

            LogMessage(Level.SEVERE, "&c" + shutdownMessage);
            LogMessage(Level.WARNING, "ImprovedFactions put it self in standby. All commands will be disabled. Only simple claim protection is working");

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (standby && player.hasPermission("factions.messages.standby")) {
                    Language.sendMessage(LangMessage.PLUGIN_STANDBY_MESSAGE, player);
                }
            }

            ArrayList<String> standbyCommands = configManager.getValue("commands.standby");

            getServer().getScheduler().runTaskLater(this, () -> {
                for (String command : standbyCommands) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            }, 0);
        });
    }

    /**
     * Save all configs and extra data. If something happens while saving, it will save a backup.
     * That can seen when using /f config backup ingame
     *
     * @return Returns a list of all successfully saved configs
     */
    public List<String> SaveConfigs() {
        List<String> savedConfigs = new ArrayList<>();

        for (Config config : configMap.values()) {
            boolean autoSave = config.isAutoSave();
            config.setAutoSave(false);
            config.setChanges(false);

            if (!CallSaveEvents(config)) {
                saveConfigBackup(config);
            } else if (!savedConfigs.contains(config.getConfigFile())) {
                savedConfigs.add(config.getConfigFile());
            }

            if (config.hasChanged()) {
                config.getManager().saveConfig();
            }
            config.setChanges(false);
            config.setAutoSave(autoSave);
        }

        for (ConfigSaveEvent event : saveEvents) {
            if (event.isSingleCall() == ConfigSaveEvent.SaveType.DataAccess) {
                Result result = event.Save(null);
                if (!result.isSuccess())
                    saveDataAccessBackup(result.getMachineMessage(), result.getPaired());
                else savedConfigs.add(result.getMachineMessage());
            }
        }

        return savedConfigs;
    }

    private <T> void saveDataAccessBackup(String file, T value) {
        LogMessage(Level.WARNING, "&cCouldn't save &6" + file + "&c. File got saved in datAcc_backup folder. Please restart the plugin so the files can be compared without data loss");
        File pathAsFile = new File(getDataFolder().getPath() + "/.temp/datAcc_backups/");

        if (!pathAsFile.exists()) {
            Utility.run(() -> {
                if (!pathAsFile.mkdirs() || !new File(pathAsFile.getPath() + "/" + file).createNewFile()) {
                    LogMessage(Level.SEVERE, "&cCouldn't save &6" + pathAsFile.getPath() + "&c to backups");
                }
            });
        }

        JsonUtility.SaveObject(pathAsFile, value);
    }

    private void saveConfigBackup(Config config) {
        Utility.run(() -> {
            LogMessage(Level.WARNING, "&cCouldn't save &6" + config.getPath() + "&c. File got saved in config_backup folder. Please restart the plugin so the files can be compared without data loss");
            File pathAsFile = new File(getDataFolder().getPath() + "/.temp/config_backups");

            if (!Files.exists(Paths.get(pathAsFile.getPath()))) {
                Utility.run(() -> {
                    if (!pathAsFile.mkdirs()) {
                        LogMessage(Level.SEVERE, "&cCouldn't save &6" + pathAsFile.getPath() + "&c to backups");
                    }
                });
            }

            File backupFile = new File(pathAsFile.getPath() + "/" + config.getManager().getFileName() + "_" + LocalTime.now().toSecondOfDay() + ".backup");

            List<String> paths = null;
            if (Files.exists(Paths.get(backupFile.getPath()))) {
                paths = (List<String>) JsonUtility.ReadObject(backupFile, List.class);
            }
            paths = paths == null ? new ArrayList<>() : paths;

            String toSave = config.getPath() + ":" + config.getValue();

            paths.add(toSave);

            JsonUtility.SaveObject(backupFile, paths);
        });
    }

    private boolean CallSaveEvents(Config config) {
        for (ConfigSaveEvent event : saveEvents) {
            if (event.isSingleCall() == ConfigSaveEvent.SaveType.Config && !event.Save(config).isSuccess())
                return false;
        }
        return true;
    }

    private void GenerateConfigs() {
        Utility.run(() -> {
            configManager = new ConfigManager(this);
            configManager.register();

            //<editor-fold desc="Loading backups">
            File backupFolder = new File(getDataFolder().getPath() + "/.temp/backups");

            if (!backupFolder.exists()) backupFolder.mkdirs();

            for (File file : backupFolder.listFiles()) {
                ArrayList<String> data = (ArrayList<String>) JsonUtility.ReadObject(file, ArrayList.class);

                LogMessage(Level.WARNING, "&cLoaded " + file.getName() + " backup. Please use &7/f config backup&c to decide what should be finally used");
                backupFile.put(file.getName(), data);

                file.delete();
            }
            //</editor-fold>
        });
    }

    private void LoadListeners() {
        getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getPluginManager().registerEvents(new PlayerLeaveListener(), this);
        getPluginManager().registerEvents(new GuiListener(), this);
        getPluginManager().registerEvents(new PlayerMoveListener(), this);
        getPluginManager().registerEvents(new BlockBreakListener(), this);
        getPluginManager().registerEvents(new BlockPlaceListener(), this);
    }

    private boolean LoadPluginVersion() {
        String sVersion = Bukkit.getBukkitVersion();
        NMSInterface nms;

        if (sVersion.contains("1.18")) {
            nms = NMSFactory.create_1_18();
        } else if (sVersion.contains("1.17")) {
            nms = NMSFactory.create_1_17();
        } else if (sVersion.contains("1.16")) {
            nms = NMSFactory.create_1_16();
        } else {
            SaveShutdown("§cCouldn't load ImprovedFactions &6" + VERSION +
                    "&c. The plugin didn't find a version for your server. Your server version: &6"
                    + sVersion + "&c. Available versions: &6" + Arrays.toString(NMSFactory.versions));
            getPluginManager().disablePlugin(this);
            return false;
        }

        nms.EnableInterface();
        return true;
    }

    //</editor-fold>

    //<editor-fold desc="Getters and Setters">

    private void LoadPluginDependencies() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!setupEconomy()) {
                    LogMessage(Level.WARNING, "&eDisabled faction economy! Needs Vault and an Economy plugin" +
                            " installed to enable it");
                } else {
                    LogMessage(Level.INFO, "&aEnabled faction economy");
                }
            }
        }.runTaskLater(this, 0);
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> eco = getServer().getServicesManager().getRegistration(Economy.class);
        if (eco != null) {
            economy = eco.getProvider();
        }
        return economy != null;
    }

    private boolean InitializeCores() {
        if (!Language.init(this, getDataFolder())) return false;
        if (!TimeCore.init()) return false;
        if (!DataAccess.init()) return false;

        FactionSettings.register();
        PlayerSettings.register();
        FactionPerm.register();
        ItemCore.register();
        MapHandler.register();
        Bstat.register(this);

        claimManager = new ClaimManager();
        new FactionUtility();
        new MessageSystem();

        Rank.Init();

        return true;
    }

    /**
     * Add a listener for events while the plugin is running
     * NOTE: This could cause some troubles if the event is getting called while adding
     *
     * @param listener The listener that should be added
     */
    public void RegisterListener(Listener listener) {
        getPluginManager().registerEvents(listener, this);
    }

    /**
     * Get the data managers. These are needed to load config settings
     *
     * @return A map of String:Datamanager pairs. The string is the datamanager file name
     */
    public Map<String, DataManager> getDataManagers() {
        return dataManagers;
    }

    /**
     * Get if the plugin is in standby. If it is, disable everything that could load data, access factions and manage settings
     *
     * @return A value that tells if the plugin ha put itself into standby
     */
    public boolean isStandby() {
        return standby;
    }

    /**
     * Get the list of backups read while enabling / reloading the plugin
     *
     * @return This will be empty if no .backup will be found
     */
    public Map<String, ArrayList<String>> getBackupFile() {
        return backupFile;
    }

    /**
     * Get all loaded config settings
     *
     * @return config map. String is for path. E.g: general.prefix
     */
    public Map<String, Config> getConfigMap() {
        return configMap;
    }

    /**
     * Get the save events that will be called when something gets saved.
     * You can tell if the file should be saved as backup, or add your own backup system
     *
     * @return a list of conifg save events
     */
    public List<ConfigSaveEvent> getSaveEvents() {
        return saveEvents;
    }

    public ClaimManager getClaimManager() {
        return claimManager;
    }

    //</editor-fold>
}
