package io.github.toberocat.improvedfactions.factions.rank;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import io.github.toberocat.improvedfactions.BaseManager;
import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.data.Permissions;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.ranks.AdminRank;
import io.github.toberocat.improvedfactions.ranks.OwnerRank;
import io.github.toberocat.improvedfactions.ranks.Rank;

public class RankManager extends BaseManager {
    private Map<Rank, List<String>> rankPermissions;

    public RankManager(Faction faction, FactionsHandler factionsHandler) {
        super("rank", faction, factionsHandler);
        this.faction = faction;
        this.factionsHandler = factionsHandler;
        this.rankPermissions = new HashMap<Rank, List<String>>();
        this.rankPermissions.put(Rank.fromString(OwnerRank.registry), List.of(
                Permissions.BREAK_PERMISSION,
                Permissions.BUILD_PERMISSION,
                Permissions.INTERACT_PERMISSION,
                Permissions.CLAIM_CHUNK_PERMISSION,
                Permissions.UNCLAIM_CHUNK_PERMISSION,
                Permissions.INVITE_PERMISSION,
                Permissions.LIST_BANNED_PERMISSION));
        this.rankPermissions.put(Rank.fromString(AdminRank.registry), List.of(
                Permissions.BREAK_PERMISSION,
                Permissions.BUILD_PERMISSION,
                Permissions.INTERACT_PERMISSION,
                Permissions.CLAIM_CHUNK_PERMISSION,
                Permissions.UNCLAIM_CHUNK_PERMISSION));
    }

    public Rank getRank(Player player) {
        var factionMember = faction.getFactionMember(player);
        if (factionMember == null) {
            return null;
        }

        return factionMember.getRank();
    }

    public List<String> getPermissions(Rank rank) {
        return rankPermissions.get(rank);
    }

    public List<String> getPlayerPermissions(Player player) {
        var rank = getRank(player);
        if (rank == null) {
            return null;
        }

        return getPermissions(rank);
    }

    @Override
    public void save() throws IOException {
        var config = new YamlConfiguration();
        var sanitizedRanks = new HashMap<String, List<String>>();
        for (var rank : rankPermissions.entrySet()) {
            sanitizedRanks.put(rank.getKey().getRegistryName(), rank.getValue());
        }
        config.set("rankPermissions", sanitizedRanks);
        super.internalSave(config);
    }

    @Override
    public void load() throws FileNotFoundException, IOException, InvalidConfigurationException {
        var config = super.internalLoad();
        for (var permission : config.getConfigurationSection("rankPermissions").getKeys(false)) {
            var rank = Rank.fromString(permission);
            rankPermissions.put(rank, config.getStringList("rankPermissions." + permission));
        }
    }
}
