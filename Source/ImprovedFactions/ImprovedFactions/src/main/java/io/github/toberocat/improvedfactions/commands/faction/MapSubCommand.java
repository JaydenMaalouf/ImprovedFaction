package io.github.toberocat.improvedfactions.commands.faction;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.listeners.OnPlayerMove;
import io.github.toberocat.improvedfactions.utility.TCallback;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MapSubCommand extends SubCommand {
    public MapSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "map", LangMessage.MAP_DESCRIPTION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (args.length == 1 && args[0].equals("auto")) {
            // if (AUTO_MAPS.contains(player.getUniqueId())) {
            // player.sendMessage(Language.getPrefix() + "§c§lDisabled§f auto map");
            // AUTO_MAPS.remove(player.getUniqueId());
            // } else {
            // AUTO_MAPS.add(player.getUniqueId());
            // player.sendMessage(Language.getPrefix() + "§a§lEnabled§f auto map");
            // new OnChunkEntered(factionsHandler).sendMap(player);
            // }
        } else {
            new OnPlayerMove(factionsHandler).sendMap(player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return new ArrayList<String>();
        // return List.of("auto");
    }

    public void getChunk(Chunk chunk, Player player, TCallback<TextComponent> callback) {
        var faction = factionsHandler.getFaction(chunk);
        var color = "";
        var hover = "";
        var symbol = "■";
        if (faction == null) { // Wildness
            color = "§2";
            hover = color + "Wildness";
        } else {
            color = faction == factionsHandler.getFaction(player) ? "§a" : "§c";
            hover = color + faction.getDisplayName();
        }

        if (player.getLocation().getChunk() == chunk) {
            hover = color + "You";
            symbol = "o";
        }

        var com = new TextComponent(color + symbol);
        com.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                hover + "\n§7" + chunk.getX() + "; " + chunk.getZ())));
        callback.Callback(com);
    }
}
