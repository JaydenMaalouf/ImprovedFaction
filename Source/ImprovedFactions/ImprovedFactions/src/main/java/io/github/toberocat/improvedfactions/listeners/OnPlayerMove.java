package io.github.toberocat.improvedfactions.listeners;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.factionCommands.MapSubCommand;
import io.github.toberocat.improvedfactions.language.Language;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Objects;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class OnPlayerMove implements Listener {
    private FactionsHandler factionsHandler;

    public OnPlayerMove(FactionsHandler factionsHandler) {
        this.factionsHandler = factionsHandler;
    }

    @EventHandler
    public void PlayerMove(PlayerMoveEvent event) {
        if (!event.getFrom().getChunk().equals(event.getTo().getChunk())) {
            OnChunkEntered(event.getPlayer(), event.getTo().getChunk());
        }
    }

    private void OnChunkEntered(Player player, Chunk chunk) {
        if (chunk == null) {
            return;
        }

        var worldName = chunk.getWorld().getName();
        if (!factionsHandler.getWorlds().contains(worldName)) {
            return;
        }

        var playerData = factionsHandler.getPlayerData(player.getUniqueId());
        if (playerData == null) {
            return;
        }

        var newChunk = playerData.setChunk(chunk);

        var playerFaction = playerData.getPlayerFaction();
        if (playerData.getAutoClaim() && playerFaction != null) {
            playerFaction.claimChunk(chunk, null);
        }

        // TODO: implement automap
        // if (MapSubCommand.AUTO_MAPS.contains(player.getUniqueId())) {
        // sendMap(player);
        // }

        // Check if is in wildness

        var chunkFaction = factionsHandler.getFaction(chunk);
        if (newChunk) {
            if (chunkFaction == null) {
                if (Objects.equals(factionsHandler.getConfig().getString("general.messageType"), "TITLE")) {
                    player.sendTitle(
                            Language.format(factionsHandler.getConfig().getString("general.wildnessText")), "", 10, 20,
                            10);
                } else if (Objects.equals(factionsHandler.getConfig().getString("general.messageType"), "ACTIONBAR")) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent
                            .fromLegacyText(factionsHandler.getConfig().getString("general.wildnessText")));
                } else {
                    player
                            .sendMessage("Couldn't find the type "
                                    + factionsHandler.getConfig().getString("general.messageType") + "\n"
                                    + "Valid types = { TITLE, ACTIONBAR }");
                }

            } else {
                if (Objects.equals(factionsHandler.getConfig().getString("general.messageType"), "TITLE")) {
                    player.sendTitle((chunkFaction == playerFaction ? "§a" : "§c")
                            + chunkFaction.getDisplayName(),
                            "§f" + (chunkFaction.getMotd() == null ? "" : chunkFaction.getMotd()), 10,
                            20, 10);
                } else if (Objects.equals(factionsHandler.getConfig().getString("general.messageType"), "ACTIONBAR")) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            TextComponent.fromLegacyText(
                                    (chunkFaction == playerFaction ? "§a" : "§c")
                                            + chunkFaction.getDisplayName()));
                } else {
                    player
                            .sendMessage("Couldn't find the type "
                                    + factionsHandler.getConfig().getString("general.messageType") + "\n"
                                    + "Valid types = { TITLE, ACTIONBAR }");
                }
            }
        }
    }

    public void sendMap(Player player) {
        var center = player.getLocation().getChunk();
        int dstH = factionsHandler.getConfig().getInt("general.mapViewDistanceW");
        int dstW = factionsHandler.getConfig().getInt("general.mapViewDistanceH");

        int leftTopX = center.getX() - dstW / 2;
        int leftTopZ = center.getZ() - dstH / 2;

        int rightDownX = center.getX() + dstW / 2;
        int rightDownZ = center.getZ() + dstH / 2;

        var chunks = new Chunk[dstW][dstH];

        for (int x = leftTopX; x <= rightDownX; x++) {
            for (int z = leftTopZ; z <= rightDownZ; z++) {
                chunks[x - leftTopX][z - leftTopZ] = player.getLocation().getWorld().getChunkAt(x, z);
            }
        }

        var map = new TextComponent(
                Language.getPrefix() + "§fMap for §7" + center.getX() + "; " + center.getZ() + "\n");
        for (int i = 0; i < dstW; i++) {
            map.addExtra(Language.getPrefix());
            for (int j = 0; j < dstH; j++) {
                new MapSubCommand(factionsHandler).getChunk(chunks[i][j], player, map::addExtra);
            }
            if (i < (dstW - 1)) {
                map.addExtra("\n");
            }
        }
        player.spigot().sendMessage(map);
    }
}
