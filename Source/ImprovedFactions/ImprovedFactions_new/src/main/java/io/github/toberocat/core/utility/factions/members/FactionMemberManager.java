package io.github.toberocat.core.utility.factions.members;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.toberocat.MainIF;
import io.github.toberocat.core.listeners.PlayerJoinListener;
import io.github.toberocat.core.utility.calender.TimeCore;
import io.github.toberocat.core.utility.factions.FactionUtility;
import io.github.toberocat.core.utility.Result;
import io.github.toberocat.core.utility.data.PersistentDataUtility;
import io.github.toberocat.core.utility.factions.Faction;
import io.github.toberocat.core.utility.language.LangMessage;
import io.github.toberocat.core.utility.language.Language;
import io.github.toberocat.core.utility.messages.PlayerMessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

/**
 * This is for managing the members of a specific faction
 */
public class FactionMemberManager {

    public static final String NO_FACTION = "NONE";
    public static final String NONE_TIMEOUT = "NONE";


    private ArrayList<UUID> members;
    private ArrayList<UUID> banned;
    private ArrayList<UUID> invitations;

    private Faction faction;

    /**
     * Don't use this. It is for jackson (json).
     */
    public FactionMemberManager() {}

    public FactionMemberManager(Faction faction) {
        this.members = new ArrayList<>();
        this.banned = new ArrayList<>();
        this.invitations = new ArrayList<>();
        this.faction = faction;
    }

    /**
     * This event should get called by the @see {@link PlayerJoinListener#Join(PlayerJoinEvent)}
     * and shouldn't get called outside of it
     * @param event The event from PlayerJoin
     */
    public static void PlayerJoin(PlayerJoinEvent event) {
        updatePlayer(event.getPlayer());
    }

    private static void updatePlayer(Player player) {
        if (!PersistentDataUtility.has(PersistentDataUtility.PLAYER_FACTION_REGISTRY,
                PersistentDataType.STRING, player.getPersistentDataContainer())) return;

        String registry = PersistentDataUtility.read(PersistentDataUtility.PLAYER_FACTION_REGISTRY,
                PersistentDataType.STRING, player.getPersistentDataContainer());
        if (registry == null || registry.equals(FactionMemberManager.NO_FACTION)) return;

        PersistentDataUtility.write(PersistentDataUtility.PLAYER_FACTION_REGISTRY, PersistentDataType.STRING,
                FactionMemberManager.NO_FACTION, player.getPersistentDataContainer());

        Faction faction = FactionUtility.getFactionByRegistry(registry);
        if (faction == null) return; // Faction deleted

        if (!faction.getFactionMemberManager().members.contains(player.getUniqueId()))
            return;//Not in this faction

        PersistentDataUtility.write(PersistentDataUtility.PLAYER_FACTION_REGISTRY,
                PersistentDataType.STRING, NO_FACTION,
                player.getPersistentDataContainer());
    }

    /**
     * It adds the player to the list of invited players, and sends a message to the player
     *
     * @param player The player who was invited
     * @return A Result object.
     */
    public Result invitePlayer(Player player) {
        invitations.add(player.getUniqueId());

        new PlayerMessageBuilder("%;{CLICK(0)}%You got invited by &e" + faction.getDisplayName() +
                ". &7Click to join\n&fInvitation will run out in &e5 &fminutes",
                "/f inviteaccept " + faction.getRegistryName()).send(player);
        Bukkit.getScheduler().runTaskLater(MainIF.getIF(), () -> {
            removeInvite(player);
        }, 5 * 60 * 20);
        return Result.success();
    }

    /**
     * Remove the player from the list of invited players
     *
     * @param player The player who was invited
     * @return A Result object.
     */
    public Result removeInvite(OfflinePlayer player) {
        invitations.remove(player.getUniqueId());
        if (player.isOnline()) {
            Language.sendRawMessage("Invitation of &e" + faction.getDisplayName() +
                    "&f has now run out of time", player.getPlayer());
        }
        return Result.success();
    }

    /**
     * Let a (online) player join a faction if not in any other
     * If the result is not a success, you can get the error message by .getMessage()
     * @see {@link Result#getMachineMessage()} ()}
     *
     * Potential Errors:
     * PLAYER_OFFLINE, PLAYER_IN_FACTION, PLAYER_BANNED, PLAYER_TIMEOUT
     *
     * @param player The player that should join the faction
     */
    public Result join(Player player) {
        if (!player.isOnline()) return new Result(false).setMessages("PLAYER_OFFLINE",
                "It looks like "+player.getName()+" are offline");

        if (FactionUtility.isInFaction(player)) return new Result(false).setMessages(
                "PLAYER_IN_FACTION", player.getName() + " is already in a faction");

        if (banned.contains(player.getUniqueId())) return new Result(false).setMessages(
                "PLAYER_BANNED", "Looks like " + player.getName()
                        + " is banned from this faction");

        PersistentDataUtility.write(
                PersistentDataUtility.PLAYER_FACTION_REGISTRY,
                PersistentDataType.STRING, faction.getRegistryName(),
                player.getPersistentDataContainer());

        members.add(player.getUniqueId());
        faction.getPowerManager()
                .IncreaseMax(MainIF.getConfigManager().getValue("power.maxPowerPerPlayer"));

        return new Result(true);
    }

    /**
     * Let a (online) player leave the faction.
     * If the player could / is offline, please use @see {@link #kick(UUID)}
     *
     * If the result is not a success, you can get the error message by .getMessage()
     * @see {@link Result#getMachineMessage()} ()}
     * Potential Errors: PLAYER_OFFLINE, PLAYER_NOT_IN_FACTION
     *
     * @param player The player that should leave
     */
    public Result leave(Player player) {
        if (!player.isOnline()) return new Result(false).setMessages("PLAYER_OFFLINE",
                "It looks like "+player.getName()+" are offline");

        if (!FactionUtility.isInFaction(player)) return new Result(false).setMessages(
                "PLAYER_NOT_IN_FACTION", player.getName()
                        + " is in no faction. So nothing can be left");

        PersistentDataUtility.write(PersistentDataUtility.PLAYER_FACTION_REGISTRY, PersistentDataType.STRING,
                NO_FACTION,
                player.getPersistentDataContainer());

        updatePlayer(player);

        members.remove(player.getUniqueId());
        faction.getPowerManager()
                .IncreaseMax(MainIF.getConfigManager().getValue("power.maxPowerPerPlayer"));
        return new Result(true);
    }

    /**
     * Kick a player froma faction.
     * When you kick someone, he/she doesn't need to be online
     * @param player The uuid of the player you want to kick
     */
    public Result kick(UUID player) {
        members.remove(player);
        Player onP = Bukkit.getPlayer(player);
        if (onP != null && onP.isOnline()) {
            updatePlayer(onP);
        }
        return new Result(true);
    }

    /**
     * This will kick the player & won't let him join this faction again
     * You can ban everyone. The player doesn't need to be online
     * @param player The uuid of the player you want to ban permanently
     */
    public Result ban(UUID player) {
        banned.add(player);
        kick(player);
        return new Result(true);
    }

    /**
     * This "forgives" a player who has been banned using the function ban
     * @see #ban(UUID)
     */
    public Result pardon(UUID player) {
        banned.remove(player);
        return new Result(true);
    }

    public ArrayList<UUID> getMembers() {
        return members;
    }

    public ArrayList<UUID> getBanned() {
        return banned;
    }

    public FactionMemberManager setMembers(ArrayList<UUID> members) {
        this.members = members;
        return this;
    }

    public FactionMemberManager setBanned(ArrayList<UUID> banned) {
        this.banned = banned;
        return this;
    }

    @JsonIgnore
    public ArrayList<UUID> getInvitations() {
        return invitations;
    }

    @JsonIgnore
    public void setInvitations(ArrayList<UUID> invitations) {
        this.invitations = invitations;
    }

    @JsonIgnore
    public FactionMemberManager setFaction(Faction faction) {
        this.faction = faction;
        return this;
    }

    @JsonIgnore
    public Faction getFaction() {
        return faction;
    }

    @JsonIgnore
    public List<Player> getOnlinePlayers() {
        return members.stream().filter(uuid -> Bukkit.getOfflinePlayer(uuid).isOnline())
                .map(uuid -> Bukkit.getOfflinePlayer(uuid).getPlayer()).toList();
    }
}
