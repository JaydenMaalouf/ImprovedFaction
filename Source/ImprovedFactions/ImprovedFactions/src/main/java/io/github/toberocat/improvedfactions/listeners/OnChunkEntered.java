package io.github.toberocat.improvedfactions.listeners;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.factionCommands.MapSubCommand;
import io.github.toberocat.improvedfactions.commands.factionCommands.claimCommands.ClaimAutoChunkSubCommand;
import io.github.toberocat.improvedfactions.commands.factionCommands.claimCommands.UnclaimAutoChunkSubCommand;
import io.github.toberocat.improvedfactions.data.PlayerData;
import io.github.toberocat.improvedfactions.event.chunk.OnChunkEnterEvent;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.utility.ChunkUtils;
import io.github.toberocat.improvedfactions.utility.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class OnChunkEntered implements Listener {
    private FactionsHandler _factionsHandler;
    public OnChunkEntered(FactionsHandler factionsHandler){
        _factionsHandler = factionsHandler;
    }


    @EventHandler
    public void ChunkEnter(OnChunkEnterEvent event) {
        var currentChunk = event.getChunk();
        if (currentChunk == null){
            return;
        }

        var worldName = currentChunk.getWorld().getName();
        if (!_factionsHandler.getWorlds().contains(worldName)) {
            return;
        }

        var player = event.getPlayer();
        if (player == null){
            return;
        }

        var playerData = _factionsHandler.getPlayerData(player.getUniqueId());
        if (UnclaimAutoChunkSubCommand.autoUnclaim.contains(player.getUniqueId()) && playerData.playerFaction != null) {
            Utils.UnClaimChunk(player);
        }

        if (ClaimAutoChunkSubCommand.autoClaim.contains(player.getUniqueId()) && playerData.playerFaction != null) {
            Utils.ClaimChunk(player);
        }

        if (MapSubCommand.AUTO_MAPS.contains(player.getUniqueId())) {
            sendMap(player);
        }

        boolean oldClaimedchunk = playerData.chunkData.isInClaimedChunk;
        String oldFactionName = playerData.chunkData.factionRegistry;
        //Check if is in wildness

        var key = _factionsHandler.createNamespacedKey("faction-claimed");
        var chunkContainer = event.getChunk().getPersistentDataContainer();
        playerData.chunkData.isInClaimedChunk = chunkContainer.has(key, PersistentDataType.STRING);
        playerData.chunkData.factionRegistry = chunkContainer.has(key, PersistentDataType.STRING) ?
                chunkContainer.get(key, PersistentDataType.STRING) : playerData.chunkData.factionRegistry;
        if (playerData.chunkData.isInClaimedChunk != oldClaimedchunk) {
            playerData.display.alreadyDisplayedRegion = false;
        }
        if (!playerData.chunkData.factionRegistry.equals(oldFactionName)) {
            playerData.display.alreadyDisplayedRegion = false;
        }
        if (!playerData.chunkData.isInClaimedChunk) {
            if (!playerData.display.alreadyDisplayedRegion) {
                if  (Objects.equals(_factionsHandler.getConfig().getString("general.messageType"), "TITLE")) {
                    event.getPlayer().sendTitle(Language.format(_factionsHandler.getConfig().getString("general.wildnessText")), "", 10, 20, 10);
                } else if (Objects.equals(_factionsHandler.getConfig().getString("general.messageType"), "ACTIONBAR")) {
                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(_factionsHandler.getConfig().getString("general.wildnessText")));
                } else {
                    event.getPlayer().sendMessage("Couldn't find the type " + _factionsHandler.getConfig().getString("general.messageType") + "\n"
                            + "Valid types = { TITLE, ACTIONBAR }");
                }
                playerData.display.alreadyDisplayedRegion = true;
            }
        }else {
            if (!playerData.display.alreadyDisplayedRegion) {
                String factionRegistry = chunkContainer.get(key, PersistentDataType.STRING);
                Faction faction = FactionUtils.getFaction(factionRegistry);

                    if (faction == null) {
                        chunkContainer.remove(ChunkUtils.FACTION_CLAIMED_KEY);
                        return;
                    }

                    if  (Objects.equals(_factionsHandler.getConfig().getString("general.messageType"), "TITLE")) {
                        event.getPlayer().sendTitle((faction == FactionUtils.getFaction(event.getPlayer()) ? "§a" : "§c")
                                + faction.getDisplayName(), "§f"+(faction.getMotd() == null ? "" : faction.getMotd()), 10, 20, 10);
                    } else if (Objects.equals(_factionsHandler.getConfig().getString("general.messageType"), "ACTIONBAR")) {
                        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText((faction == FactionUtils.getFaction(event.getPlayer()) ? "§a" : "§c")
                                + faction.getDisplayName()));
                    } else {
                        event.getPlayer().sendMessage("Couldn't find the type " + _factionsHandler.getConfig().getString("general.messageType") + "\n"
                                + "Valid types = { TITLE, ACTIONBAR }");
                    }
                    playerData.display.alreadyDisplayedRegion = true;
            }
        }
    }

    public void sendMap(Player player) {
        Chunk center = player.getLocation().getChunk();
        int dstH = _factionsHandler.getConfig().getInt("general.mapViewDistanceW");
        int dstW = _factionsHandler.getConfig().getInt("general.mapViewDistanceH");

        int leftTopX = center.getX() - dstW/2;
        int leftTopZ = center.getZ() - dstH/2;

        int rightDownX = center.getX() + dstW/2;
        int rightDownZ = center.getZ() + dstH/2;

        Chunk[][] chunks = new Chunk[dstW][dstH];

        for (int x = leftTopX; x <= rightDownX; x++) {
            for (int z = leftTopZ; z <= rightDownZ; z++) {
                chunks[x-leftTopX][z-leftTopZ] = player.getLocation().getWorld().getChunkAt(x, z);
            }
        }

        TextComponent map = new TextComponent(Language.getPrefix() + "§fMap for §7"+ center.getX() + "; " + center.getZ() +"\n");
        for (int i = 0; i < dstW; i++) {
            map.addExtra(Language.getPrefix());
            for (int j = 0; j < dstH; j++) {
                MapSubCommand.getChunk(chunks[i][j],player, map::addExtra);
            }
            if (i < (dstW-1)) {
                map.addExtra("\n");
            }
        }
        player.spigot().sendMessage(map);
    }
}
