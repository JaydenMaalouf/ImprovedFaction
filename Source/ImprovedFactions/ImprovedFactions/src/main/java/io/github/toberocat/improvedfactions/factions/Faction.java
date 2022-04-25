package io.github.toberocat.improvedfactions.factions;

import io.github.toberocat.improvedfactions.event.faction.FactionJoinEvent;
import io.github.toberocat.improvedfactions.event.faction.FactionLeaveEvent;
import io.github.toberocat.improvedfactions.factions.economy.Bank;
import io.github.toberocat.improvedfactions.factions.power.PowerManager;
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

import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;

public class Faction {
    public enum OpenType {
        Public("&aPublic"),
        Private("&cPrivate");

        private final String display;

        OpenType(String display) {
            this.display = Language.format(display);
        }

        @Override
        public String toString() {
            return display;
        }
    }

    public static String CLAIM_CHUNK_PERMISSION = "claim_chunk";
    public static String UNCLAIM_CHUNK_PERMISSION = "unclaim_chunk";
    public static String INVITE_PERMISSION = "invite";
    public static String BUILD_PERMISSION = "build";
    public static String BREAK_PERMISSION = "break";
    public static String LIST_BANNED_PERMISSION = "listBanned";
    public static String OPENTYPE_FLAG = "openType";
    public static String RENAME_FLAG = "rename";
    public static String MOTD = "motd";

    private String rules;

    private String displayName;
    private String registryName;
    private List<FactionMember> members;

    private String description = "A improved faction faction";
    private String motd = "";

    private PowerManager powerManager;
    private RelationManager relationManager;
    private Bank bankManager;

    private boolean permanent;
    private boolean frozen;

    private int claimedChunks;

    private FactionSettings settings;

    private UUID owner;
    private List<UUID> bannedPlayers;
    private List<String> warnings;
    private List<Report> reports;
    private boolean isOpen;

    private FactionsHandler factionsHandler;

    public Faction(FactionsHandler factionsHandler, String name) {
        this.factionsHandler = factionsHandler;

        displayName = name;
        this.registryName = ChatColor.stripColor(displayName);
        this.claimedChunks = 0;

        // ImprovedFactionsMain.getPlugin().getConfig().getInt("factions.maxMembers")
        // members = new
        // FactionMember[_factionsHandler.getConfig().getInt("factions.maxMembers")];

        bannedPlayers = new ArrayList<>();
        settings = new FactionSettings(factionsHandler);

        relationManager = new RelationManager(this);
        powerManager = new PowerManager(this, factionsHandler);
        bankManager = new Bank(this, factionsHandler);

        permanent = factionsHandler.getConfig().getBoolean("faction.permanent");
        frozen = false;
    }

    public String getDisplayName() {
        return displayName;
    }

    public FactionRankPermission getPermission(String perm) {
        return settings.getRanks().get(perm);
    }

    public FactionSettings getSettings() {
        return settings;
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

        var perms = settings.getRanks().get(permission);
        if (perms == null) {
            factionsHandler.getConsoleSender().sendMessage("Couldn't get permissions for "
                    + permission
                    + ". This should not happen. If it does (It did if you are reading this), please report it to me (The developer). Send me a message over discord or spigotmc. Go on spigotmc to get a link to discord");
            return false;
        }
        return perms.getRanks().contains(member.getRank());
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
        var members = new ArrayList<FactionMember>();
        for (var member : getMembers()) {
            if (member == null) {
                continue;
            }

            var offlinePlayer = Bukkit.getOfflinePlayer(member.getUuid());
            if (offlinePlayer.isOnline()) {
                members.add(member);
            }
        }

        return members;
    }

    public List<Player> getAllPlayers() {
        var members = new ArrayList<Player>();
        for (var member : getMembers()) {
            if (member == null) {
                continue;
            }

            var offlinePlayer = Bukkit.getOfflinePlayer(member.getUuid());
            members.add(offlinePlayer.getPlayer());
        }
        return members;
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
        for (FactionMember member : members) {
            if (member == null)
                return false;
        }
        return true;
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
            return false;
        }
        if (bannedPlayers.contains(uuid)) {
            return false;
        }

        var result = false;
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i) == null) {
                members.set(i, new FactionMember(uuid, rank));
                result = true;
                break;
            }
        }
        if (!result) {
            return false;
        }

        factionsHandler.getPlayerData(uuid).setPlayerFaction(this);
        powerManager.addFactionMember();

        return true;
    }

    public void claimChunk(Chunk chunk, TCallback<ClaimStatus> callback) {
        if (!powerManager.canClaimChunk()) {
            callback.Callback(null);
            return;
        }

        ChunkUtils.ClaimChunk(chunk, this, result -> {
            if (result.getClaimStatus() == ClaimStatus.Status.SUCCESS) {
                powerManager.claimChunk();
                claimedChunks++;
            }
            callback.Callback(result);
        });
    }

    public void unclaimChunk(Chunk chunk, TCallback<ClaimStatus> callback) {
        ChunkUtils.UnClaimChunk(chunk, this, result -> {
            if (result.getClaimStatus() == ClaimStatus.Status.SUCCESS) {
                powerManager.unclaimChunk();
                claimedChunks--;
            }
            callback.Callback(result);
        });
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

    public boolean isOpen(){
        return isOpen;
    }

    public void setOpen(boolean open){
        this.isOpen = open;
    }
}
