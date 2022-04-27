package io.github.toberocat.improvedfactions.factions;

import io.github.toberocat.improvedfactions.event.chunk.ChunkClaimEvent;
import io.github.toberocat.improvedfactions.event.chunk.ChunkUnclaimEvent;
import io.github.toberocat.improvedfactions.event.faction.FactionJoinEvent;
import io.github.toberocat.improvedfactions.event.faction.FactionLeaveEvent;
import io.github.toberocat.improvedfactions.factions.economy.Bank;
import io.github.toberocat.improvedfactions.factions.power.PowerManager;
import io.github.toberocat.improvedfactions.factions.rank.RankManager;
import io.github.toberocat.improvedfactions.factions.relation.RelationManager;
import io.github.toberocat.improvedfactions.ranks.AllyRank;
import io.github.toberocat.improvedfactions.ranks.GuestRank;
import io.github.toberocat.improvedfactions.ranks.OwnerRank;
import io.github.toberocat.improvedfactions.utility.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.language.Parseable;
import io.github.toberocat.improvedfactions.ranks.Rank;
import io.github.toberocat.improvedfactions.reports.Report;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class Faction {
    private String rules;

    private String displayName;
    private String registryName;

    private String description = "A improved faction faction";
    private String motd = "";

    private PowerManager powerManager;
    private RelationManager relationManager;
    private Bank bankManager;
    private RankManager rankManager;

    private boolean permanent;
    private boolean frozen;

    private int claimedChunks;

    private UUID owner;
    private List<FactionMember> members;
    private List<UUID> bannedPlayers;
    private List<String> warnings;
    private List<Report> reports;
    private boolean isOpen;

    private FactionsHandler factionsHandler;
    private Logger logger;

    public Faction(String factionName, FactionsHandler factionsHandler, Logger logger) {
        this.factionsHandler = factionsHandler;
        this.logger = logger;

        this.displayName = factionName;
        this.registryName = ChatColor.stripColor(factionName);
        this.claimedChunks = 0;

        // ImprovedFactionsMain.getPlugin().getConfig().getInt("factions.maxMembers")
        // members = new
        // FactionMember[factionsHandler.getConfig().getInt("factions.maxMembers")];

        this.members = new ArrayList<>();
        this.bannedPlayers = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.reports = new ArrayList<>();

        this.relationManager = new RelationManager(this, factionsHandler);
        this.powerManager = new PowerManager(this, factionsHandler);
        this.bankManager = new Bank(this, factionsHandler);
        this.rankManager = new RankManager(this, factionsHandler);

        this.permanent = factionsHandler.getConfig().getBoolean("faction.permanent");
        this.frozen = false;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * This function checks if the player has the permission
     *
     * @param player     The player who is trying to use the command
     * @param permission The permission you want to check.
     * @return A boolean.
     */
    public boolean hasPermission(Player player, String permission) {
        var member = getFactionMember(player);
        if (member == null) {
            for (var ally : relationManager.getAllies()) {
                var alliedFaction = factionsHandler.getFaction(ally);
                var alliedPlayer = alliedFaction.getFactionMember(player);
                if (alliedPlayer == null) {
                    continue;
                }
                member = new FactionMember(player.getUniqueId(), Rank.fromString(AllyRank.registry));
            }

            if (member == null) {
                member = new FactionMember(player.getUniqueId(), Rank.fromString(GuestRank.registry));
            }
        }

        return rankManager.getPlayerPermissions(player).contains(permission);
    }

    public FactionMember getFactionMember(Player player) {
        return getFactionMember(player.getUniqueId());
    }

    public FactionMember getFactionMember(UUID uuid) {
        for (var factionMember : getMembers()) {
            if (factionMember != null && factionMember.getUuid().equals(uuid)) {
                return factionMember;
            }
        }

        return null;
    }

    public List<Player> getPlayersOnline() {
        var members = new ArrayList<Player>();
        for (var member : getMembers()) {
            if (member == null) {
                continue;
            }

            var offlinePlayer = Bukkit.getOfflinePlayer(member.getUuid());
            if (offlinePlayer.isOnline()) {
                members.add(offlinePlayer.getPlayer());
            }
        }

        return members;
    }

    public List<FactionMember> getOnlineMembers() {
        var onlineMembers = new ArrayList<FactionMember>();
        for (var member : getMembers()) {
            if (member == null) {
                continue;
            }

            var offlinePlayer = Bukkit.getOfflinePlayer(member.getUuid());
            if (offlinePlayer.isOnline()) {
                onlineMembers.add(member);
            }
        }

        return onlineMembers;
    }

    public List<Player> getAllPlayers() {
        var players = new ArrayList<Player>();
        for (var member : getMembers()) {
            if (member == null) {
                continue;
            }

            players.add(member.getOfflinePlayer().getPlayer());
        }
        return players;
    }

    public boolean isMember(Player player) {
        return isMember(player.getUniqueId());
    }

    public boolean isMember(UUID uuid) {
        return getFactionMember(uuid) != null;
    }

    public boolean isBanned(Player player) {
        return isBanned(player.getUniqueId());
    }

    public boolean isBanned(UUID uuid) {
        return bannedPlayers.contains(uuid);
    }

    public boolean hasMaxMembers() {
        return members.size() >= factionsHandler.getConfig().getInt("factions.maxMembers");
    }

    public void setRank(Player player, Rank rank) {
        for (FactionMember factionMember : members) {
            if (factionMember != null && factionMember.getUuid().equals(player.getUniqueId())) {
                factionMember.setRank(rank);
            }
        }
    }

    public boolean join(Player player, Rank rank) {
        return join(player.getUniqueId(), rank);
    }

    public boolean join(UUID uuid, Rank rank) {
        var result = joinSilent(uuid, rank);
        if (!result) {
            return false;
        }

        var joinEvent = new FactionJoinEvent(this, uuid);
        Bukkit.getPluginManager().callEvent(joinEvent);

        return true;
    }

    public boolean joinSilent(Player player, Rank rank) {
        return joinSilent(player.getUniqueId(), rank);
    }

    public boolean joinSilent(UUID uuid, Rank rank) {
        if (frozen) {
            factionsHandler.getLogger().warning("Faction frozen");
            return false;
        }
        if (bannedPlayers.contains(uuid)) {
            factionsHandler.getLogger().warning("Faction has banned player");
            return false;
        }

        if (hasMaxMembers()) {
            return false;
        }

        members.add(new FactionMember(uuid, rank));

        factionsHandler.getPlayerData(uuid).setPlayerFaction(this);
        powerManager.addFactionMember();

        return true;
    }

    public void claimChunk(Chunk chunk, TCallback<ClaimStatus> callback) {
        if (!powerManager.canClaimChunk()) {
            logger.warning("Power Manager reckons we can't claim this chunk");
            callback.Callback(new ClaimStatus(ClaimStatus.Status.NOT_CLAIMED, null));
            return;
        }

        internalClaimChunk(chunk, result -> {
            if (result.getClaimStatus() == ClaimStatus.Status.SUCCESS) {
                powerManager.claimChunk();
                claimedChunks++;
                logger.warning("Somehow we actually claimed it");
            }
            if (callback != null) {
                callback.Callback(result);
            }
        });
    }

    public void unclaimChunk(Chunk chunk, TCallback<ClaimStatus> callback) {
        internalUnclaimChunk(chunk, result -> {
            if (result.getClaimStatus() == ClaimStatus.Status.SUCCESS) {
                powerManager.unclaimChunk();
                claimedChunks--;
            }
            if (callback != null) {
                callback.Callback(result);
            }
        });
    }

    private void internalUnclaimChunk(Chunk chunk, TCallback<ClaimStatus> callback) {
        var container = chunk.getPersistentDataContainer();

        /*
         * if
         * (!ImprovedFactionsMain.getPlugin().getConfig().getStringList("general.worlds"
         * ).contains(chunk.getWorld().getName())) {
         * callback.Callback(new ClaimStatus(ClaimStatus.Status.NOT_ALLOWED_WORLD,
         * null));
         * return;
         * }
         */

        if (!container.has(FactionsHandler.FACTION_CLAIMED_KEY, PersistentDataType.STRING)) {
            callback.Callback(new ClaimStatus(ClaimStatus.Status.NOT_CLAIMED, null));
            return;
        }

        var chunkFaction = factionsHandler.getFaction(chunk);
        if (chunkFaction != this) {
            callback.Callback(new ClaimStatus(ClaimStatus.Status.NOT_PROPERTY, null));
        } else if (factionsHandler.getConfig().getBoolean("general.connectedChunks")) {
            var openList = new ArrayList<Vector2>();
            var closedList = new ArrayList<Vector2>();

            for (var neighbourChunk : ChunkUtils.GetNeighbourChunks(chunk)) {
                var neighbourChunkFaction = factionsHandler.getFaction(neighbourChunk);
                if (neighbourChunkFaction == this) {
                    openList.add(new Vector2(neighbourChunk.getX(), neighbourChunk.getZ()));
                }
            }

            while (!openList.isEmpty()) {
                var neighbours = ChunkUtils.GetNeighbourChunks(openList.get(0));
                closedList.add(openList.get(0));
                openList.remove(0);
                for (Vector2 neighbour : neighbours) {
                    boolean cont = false;
                    for (Vector2 vec : closedList) {
                        if (neighbour.getX() == vec.getX() && neighbour.getY() == vec.getY()) {
                            cont = true;
                            break;
                        }
                    }
                    if (cont)
                        continue;
                    if (openList.contains(neighbour))
                        continue;
                    var claimFaction = factionsHandler.getFaction(
                            chunk.getWorld().getChunkAt((int) neighbour.getX(), (int) neighbour.getY()));
                    if (claimFaction == this
                            && chunk != chunk.getWorld().getChunkAt((int) neighbour.getX(), (int) neighbour.getY())) {
                        // The chunk is wilderness and is not part of another faction
                        openList.add(neighbour);
                    }
                }
            }

            if (getClaimedChunks() != closedList.size() + 1) {
                callback.Callback(new ClaimStatus(ClaimStatus.Status.NEED_CONNECTION, this));
            }
        }

        container.remove(FactionsHandler.FACTION_CLAIMED_KEY);
        Bukkit.getPluginManager().callEvent(new ChunkUnclaimEvent(chunk, this));
        callback.Callback(new ClaimStatus(ClaimStatus.Status.SUCCESS, null));
    }

    private void internalClaimChunk(Chunk chunk, TCallback<ClaimStatus> callback) {
        logger.warning("internal claim chunk");
        var container = chunk.getPersistentDataContainer();

        var chunkFaction = factionsHandler.getFaction(chunk);
        if (this.claimedChunks > 0 && factionsHandler.getConfig().getBoolean("general.connectedChunks")) {
            logger.warning("needs connection");
            var connected = false;
            var neighbouringChunks = ChunkUtils.GetNeighbourChunks(chunk);
            for (var neighbourChunk : neighbouringChunks) {
                var neighbourChunkFaction = factionsHandler.getFaction(neighbourChunk);
                if (chunkFaction == neighbourChunkFaction) {
                    connected = true;
                }
            }

            if (connected == false) {
                logger.warning("not connected");
                callback.Callback(new ClaimStatus(ClaimStatus.Status.NEED_CONNECTION, this));
                return;
            }
        }

        if (!getPowerManager().canClaimChunk()) {
            logger.warning("oiwer manager thinks it cant claim");
            callback.Callback(new ClaimStatus(ClaimStatus.Status.NOT_CLAIMED, chunkFaction));
            return;
        }

        if (chunkFaction != null && chunkFaction != this) {
            logger.warning("someone owns this chunk bruh");
            if (!canOverclaim(chunk)) {
                logger.warning("cant overclaim");
                callback.Callback(new ClaimStatus(ClaimStatus.Status.ALREADY_CLAIMED, chunkFaction));
                return;
            }

            for (var player : chunkFaction.getPlayersOnline()) {
                player.sendMessage(Language.getPrefix() +
                        Language.format("&6&lWarning: &e" +
                                this.getDisplayName() + "&f claimed a chunk from your land!"));
            }
        }

        logger.warning("we claimin");
        getPowerManager().claimChunk();
        container.set(FactionsHandler.FACTION_CLAIMED_KEY, PersistentDataType.STRING, getRegistryName());
        Bukkit.getPluginManager().callEvent(new ChunkClaimEvent(chunk, this));
        callback.Callback(new ClaimStatus(ClaimStatus.Status.SUCCESS, this));
    }

    private boolean canOverclaim(Chunk chunk) {
        var chunkFaction = factionsHandler.getFaction(chunk);
        if (chunkFaction == null) {
            return true;
        }
        if (chunkFaction.getRegistryName().equals(this.getRegistryName())) {
            return false;
        }

        if (chunkFaction.getPowerManager().getPower() >= chunkFaction.getPowerManager().getPower()) {
            return false;
        }

        // if (!isCorner(chunk, wantToClaimFaction.getRegistryName()))
        // return false;
        return true;
    }

    public boolean deleteFaction() {
        if (frozen) {
            return false;
        }

        for (FactionMember member : members) {
            if (member != null) {
                var player = Bukkit.getOfflinePlayer(member.getUuid());
                // ImprovedFactionsMain.getPlugin().getPlayerMessages().SendMessage(player,Language.getPrefix()
                // + displayName + " got deleted. You left automatically");
            }
        }
        factionsHandler.removeFaction(this);

        bankManager.delete();

        for (var ally : relationManager.getAllies()) {
            factionsHandler.getFaction(ally).getRelationManager().neutral(this);
        }

        for (var enemies : relationManager.getEnemies()) {
            factionsHandler.getFaction(enemies).getRelationManager().neutral(this);
        }
        return true;
    }

    public boolean leave(Player player) {
        return leave(player.getUniqueId());
    }

    public boolean leave(OfflinePlayer player) {
        var leaveEvent = new FactionLeaveEvent(this, player);
        Bukkit.getPluginManager().callEvent(leaveEvent);
        return leave(player.getUniqueId());
    }

    public boolean leave(UUID uuid) {
        if (frozen) {
            return false;
        }

        for (int i = 0; i < members.size(); i++) {
            if (members.get(i) != null && members.get(i).getUuid().equals(uuid)) {
                members.set(i, null);
                return true;
            }
        }

        factionsHandler.getPlayerData(uuid).setPlayerFaction(null);
        powerManager.removeFactionMember();
        return false;
    }

    public String getRegistryName() {
        return registryName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = Language.format(displayName).replaceAll(" ", "");
    }

    public String getDescription() {
        return description;
    }

    public List<UUID> getBannedPlayers() {
        return bannedPlayers;
    }

    public void setBannedPlayers(List<UUID> bannedPeople) {
        this.bannedPlayers = bannedPeople;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public void setMembers(List<FactionMember> members) {
        this.members = members;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public void setRegistryName(String registryName) {
        this.registryName = registryName;
    }

    public PowerManager getPowerManager() {
        return powerManager;
    }

    public void setPowerManager(PowerManager powerManager) {
        this.powerManager = powerManager;
    }

    public int getClaimedChunks() {
        return claimedChunks;
    }

    public void setClaimedChunks(int chunks) {
        claimedChunks = chunks;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner.getUniqueId();
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        joinSilent(owner, Rank.fromString(OwnerRank.registry));
    }

    public Bank getBank() {
        return bankManager;
    }

    public void setBank(Bank bank) {
        this.bankManager = bank;
    }

    public int getClaimChunks() {
        return claimedChunks;
    }

    public void setClaimChunks(int claimChunks) {
        this.claimedChunks = claimChunks;
    }

    public RelationManager getRelationManager() {
        return relationManager;
    }

    public void setRelationManager(RelationManager relationManager) {
        this.relationManager = relationManager;
    }

    public List<FactionMember> getMembers() {
        return members;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public Rank getPlayerRank(Player player) {
        return getPlayerRank(player.getUniqueId());
    }

    public Rank getPlayerRank(UUID uuid) {
        var member = getFactionMember(uuid);
        if (member == null) {
            return null;
        }

        return member.getRank();
    }

    public boolean banPlayer(Player banner, OfflinePlayer banned) {
        if (isFrozen()) {
            return false;
        }

        if (!getBannedPlayers().contains(banned.getUniqueId())) {
            bannedPlayers.add(banned.getUniqueId());
            leave(banned);

            if (banned.isOnline()) {
                Language.sendMessage(LangMessage.BANNED_PLAYER_COMMAND_LEAVE, banned.getPlayer(),
                        new Parseable("{faction_displayName}", getDisplayName()));
            }
            return true;
        } else {
            Language.sendMessage(LangMessage.BANNED_PLAYER_COMMAND_ALREADY, banned.getPlayer());
            return false;
        }
    }

    public boolean unbanPlayer(Player unbanner, OfflinePlayer unbanned) {
        if (isBanned(unbanned.getUniqueId())) {
            bannedPlayers.remove(unbanned.getUniqueId());
            unbanner.sendMessage(Language.getPrefix() + Language.format("Unbanned &e" + unbanned.getName()));
            return true;
        } else {
            unbanner.sendMessage(Language.getPrefix() + Language.format("&cCannot unban player. Player is not banned"));
            return false;
        }
    }

    public void invitePlayer(Player player, Player playerToInvite) {
        Language.sendMessage(LangMessage.INVITE_SUCCESS_SENDER, player,
                new Parseable("{player_receive}", playerToInvite.getDisplayName()));

        var textComponent = new TextComponent(Language.getPrefix() + Language.parse(
                Language.getMessage(LangMessage.INVITE_SUCCESS_RECEIVER, playerToInvite),
                new Parseable[] {
                        new Parseable("{faction_displayname}", getDisplayName())
                }));

        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                "/f join " + getRegistryName()));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(Language.getMessage(LangMessage.INVITE_HOVER_EVENT, playerToInvite))));
        factionsHandler.getPlayerData(player).addInvitation(getRegistryName());
        playerToInvite.spigot().sendMessage(textComponent);
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public List<Report> getReports() {
        return reports;
    }

    public int addWarning(String warningMessage) {
        warnings.add(warningMessage);
        return warnings.size();
    }

    public int addReport(Report report) {
        reports.add(report);
        return reports.size();
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        this.isOpen = open;
    }

    public void save() {
        try {
            rankManager.save();
            powerManager.save();
            relationManager.save();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void load() {
        try {
            rankManager.load();
            powerManager.load();
            relationManager.load();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (InvalidConfigurationException e) {
            System.out.println(e.getMessage());
        }
    }
}
