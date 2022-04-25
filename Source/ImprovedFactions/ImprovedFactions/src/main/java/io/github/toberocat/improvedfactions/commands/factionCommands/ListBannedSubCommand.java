package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.data.Permissions;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ListBannedSubCommand extends SubCommand {
    public ListBannedSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "banList", LangMessage.BANNED_LIST_DESCRIPTION);
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings().setNeedsFaction(SubCommandSettings.NYI.Yes).setFactionPermission(Permissions.LIST_BANNED_PERMISSION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        var faction = factionsHandler.getFaction(player);
        if (faction == null){
            return;
        }

        for (var banned : faction.getBannedPlayers()) {
            var bannedPlayer = Bukkit.getOfflinePlayer(banned);
            player.sendMessage(Language.getPrefix() + Language.format("&c" + bannedPlayer.getName()));
        }
        player.sendMessage(Language.getPrefix() + Language.format("&fAll banned people got displayed"));
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
