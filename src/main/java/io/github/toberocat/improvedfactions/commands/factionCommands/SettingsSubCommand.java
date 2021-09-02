package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.gui.FactionSettingsGui;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class SettingsSubCommand extends SubCommand {
    public SettingsSubCommand() {
        super("settings", "Opens the settings gui");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (FactionUtils.getFaction(player) != null) {
            Faction faction = FactionUtils.getFaction(player);
            if (FactionUtils.getPlayerRank(faction, player).isAdmin()) {
                new FactionSettingsGui(player, ImprovedFactionsMain.playerData.get(player.getUniqueId()).playerFaction);
            } else {
                CommandExecuteError(CommandExecuteError.OnlyAdminCommand, player);
            }
        } else {
            player.sendMessage(Language.getPrefix() + "§cYou need to be in a faction to use this command");
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }

    @Override
    protected boolean CommandDisplayCondition(Player player, String[] args) {
        boolean result = super.CommandDisplayCondition(player, args);
        Faction faction = FactionUtils.getFaction(player);
        if (faction == null) {
            result = false;
        } else if (FactionUtils.getPlayerRank(faction, player) != null && !FactionUtils.getPlayerRank(faction, player).isAdmin())
            result = false;
        return result;
    }
}
