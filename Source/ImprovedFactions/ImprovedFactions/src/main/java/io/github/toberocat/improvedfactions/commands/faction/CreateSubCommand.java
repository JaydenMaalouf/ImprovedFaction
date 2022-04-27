package io.github.toberocat.improvedfactions.commands.faction;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.event.faction.FactionCreateEvent;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.language.Parseable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CreateSubCommand extends SubCommand {

    public CreateSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "create", LangMessage.CREATE_DESCRIPTION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (factionsHandler.getFaction(player) == null) {
            if (args.length == 1) {
                var faction = factionsHandler.getFaction(args[0]);
                if (faction == null) {
                    CreateFaction(player, args[0]);
                } else {
                    Language.sendMessage(LangMessage.CREATE_ALREADY_EXISTS, player);
                }
            } else {
                Language.sendMessage(LangMessage.CREATE_NEED_NAME, player);
            }
        } else {
            Language.sendMessage(LangMessage.CREATE_ALREADY_IN_FACTION, player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        var arguments = new ArrayList<String>();
        if (args.length != 1) {
            return arguments;
        }

        if (player.hasPermission("faction.commands.create") && factionsHandler.getFaction(player) == null) {
            arguments.add("name");
            if (!player.hasPermission("faction.colors.colorInFactionName")
                    && args[0].contains("&")) {
                Language.sendMessage(LangMessage.CREATE_NO_COLOR_IN_NAME_PERM, player);
            }
        }
        return arguments;
    }

    @Override
    protected boolean CommandDisplayCondition(Player player, String[] args) {
        var result = super.CommandDisplayCondition(player, args);
        if (factionsHandler.getFaction(player) != null) {
            return false;
        }
        return result;
    }

    private void CreateFaction(Player player, final String _name) {
        var name = player.hasPermission("faction.colors.colorInFactionName")
                ? Language.format(_name)
                : _name;
        var faction = factionsHandler.createFaction(name, player);

        var createEvent = new FactionCreateEvent(faction, player);
        Bukkit.getPluginManager().callEvent(createEvent);
        if (!createEvent.isCancelled()) {
            Language.sendMessage(LangMessage.CREATE_SUCCESS, player,
                    new Parseable("{faction_displayname}", faction.getDisplayName()));
        } else {
            faction.deleteFaction();
            Language.sendMessage(LangMessage.CREATE_CANNOT_JOIN, player,
                    new Parseable("{error}", createEvent.getCancelMessage()));
        }
    }
}
