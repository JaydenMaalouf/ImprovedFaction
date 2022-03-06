package io.github.toberocat.core.utility.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.toberocat.MainIF;
import io.github.toberocat.core.utility.Result;
import io.github.toberocat.core.utility.Utility;
import io.github.toberocat.core.utility.async.AsyncCore;
import io.github.toberocat.core.utility.data.DataAccess;
import io.github.toberocat.core.utility.events.ConfigSaveEvent;
import io.github.toberocat.core.utility.factions.members.FactionMemberManager;
import io.github.toberocat.core.utility.json.JsonUtility;
import io.github.toberocat.core.debug.Debugger;
import io.github.toberocat.core.utility.settings.type.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerSettings {

    public enum TitlePosition implements SettingEnum {
        TITLE("Title"), SUBTITLE("Subtitle"),
        ACTIONBAR("Actionbar"), CHAT("Chat");

        String display;

        TitlePosition(String display) {
            this.display = display;
        }

        @Override
        public String getDisplay() {
            return display;
        }
    }

    @JsonIgnore
    private static final Map<UUID, PlayerSettings> SETTINGS = new HashMap();
    @JsonIgnore
    public static final Map<String, Setting> DEFAULT_SETTINGS = new HashMap<>();

    private UUID playerUUID;
    private String name;
    private Map<String, Setting> playerSettings;

    public PlayerSettings() {}

    private PlayerSettings(UUID playerUUID, String name) {
        this.playerUUID = playerUUID;
        this.name = name;
        this.playerSettings = new HashMap<>(DEFAULT_SETTINGS);
    }

    private PlayerSettings(UUID playerUUID) {
        this.playerUUID = playerUUID;
        this.name = Bukkit.getOfflinePlayer(playerUUID).getName();
        this.playerSettings = new HashMap<>(DEFAULT_SETTINGS);
    }

    private PlayerSettings(String name) {
        this.playerUUID = Bukkit.getOfflinePlayer(name).getUniqueId();
        this.name = name;
        this.playerSettings = new HashMap<>(DEFAULT_SETTINGS);
    }

    public static PlayerSettings getSettings(UUID uuid) {
        if (!SETTINGS.containsKey(uuid)) PlayerJoined(uuid);

        return SETTINGS.get(uuid);
    }


    public static void PlayerJoined(final UUID joinedPlayer) {
        PlayerSettings settings = null;
        if (DataAccess.exists("Players", joinedPlayer.toString())) {
            settings = DataAccess.getFile("Players", joinedPlayer.toString(), PlayerSettings.class);

            if (settings == null || settings.playerSettings == null) {
                Debugger.log("Couldn't load player settings. The old settings got overwritten");
                settings = new PlayerSettings(joinedPlayer);
            } else {
                Debugger.log("Loaded player settings for " + Bukkit.getOfflinePlayer(joinedPlayer).getName());
                Setting.populateSettings(DEFAULT_SETTINGS, settings.playerSettings);
            }
        } else {
            Debugger.log("Generating new player settings for " + Bukkit.getOfflinePlayer(joinedPlayer).getName());
            settings = new PlayerSettings(joinedPlayer);
        }
        SETTINGS.put(joinedPlayer, settings);
    }
    public static void PlayerLeave(UUID leftPlayer) {
        AsyncCore.Run(() -> {
           DataAccess.addFile("Players", leftPlayer.toString(), SETTINGS.get(leftPlayer));
           Debugger.log("Saved " +  Bukkit.getOfflinePlayer(leftPlayer).getName()
                   + "'s player settings");
           SETTINGS.remove(leftPlayer);
        });
    }


    public static void register() {
        DEFAULT_SETTINGS.put("bossBars", new BoolSetting(true,
                Utility.createItem(Material.HEART_OF_THE_SEA, "&eDisplay boss bar", new String[] {
                        "&8The bossbar will appear when", "&8your faction power changes.", "&8Through losing or gaining power"
                })));
        DEFAULT_SETTINGS.put("hideCommandDescription", new BoolSetting(false,
                Utility.createItem(Material.COMMAND_BLOCK, "&eHide command descriptions", new String[] {
                "&8You think the auto", "&8descriptions annoy you?", "&8Disable them"
        })));
        DEFAULT_SETTINGS.put("displayTitle", new BoolSetting(true,
                Utility.createItem(Material.NAME_TAG, "&eDisplay territory titles")));
        DEFAULT_SETTINGS.put("titlePosition", new EnumSetting(TitlePosition.values(),
                Utility.createItem(Material.AMETHYST_SHARD, "&eDisplay position", new String[] {
                        "&8Change where you want", "&8Territory changes be announced"
                })));
        DEFAULT_SETTINGS.put("factionJoinTimeout", new HiddenSetting<>(FactionMemberManager.NONE_TIMEOUT));


        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerJoined(player.getUniqueId());
        }

        //ToDo: Stop using save events, use Dynamic loaders
        MainIF.getIF().getSaveEvents().add(new ConfigSaveEvent() {
            @Override
            public SaveType isSingleCall() {
                return SaveType.DataAccess;
            }

            @Override
            public Result SaveDataAccess() {
                for (UUID player : SETTINGS.keySet()) {
                    if (!DataAccess.addFile("Players", player.toString(), SETTINGS.get(player)))
                        return new Result<String>(false)
                                .setPaired(JsonUtility.SaveObject(player.toString()))
                                .setMachineMessage("Players/" + player + ".json");
                }
                SETTINGS.clear();
                return new Result<>(true).setMachineMessage("Players/*.json");
            }
        });
    }

    public Setting getSetting(String key) {
        return playerSettings.get(key);
    }

    @JsonIgnore
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    @JsonIgnore
    public PlayerSettings setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
        return this;
    }

    public Map<String, Setting> getPlayerSetting() {
        return playerSettings;
    }

    public PlayerSettings setPlayerSetting(Map<String, Setting> playerSetting) {
        this.playerSettings = playerSetting;
        return this;
    }

    public String getName() {
        return name;
    }

    public PlayerSettings setName(String name) {
        this.name = name;
        return this;
    }

    public static Map<UUID, PlayerSettings> getLoadedSettings() {
        return SETTINGS;
    }
}