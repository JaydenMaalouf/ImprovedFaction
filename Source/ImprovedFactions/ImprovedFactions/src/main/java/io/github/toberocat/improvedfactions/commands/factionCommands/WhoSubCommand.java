package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class WhoSubCommand extends SubCommand {

    public WhoSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "who", "");
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        Faction faction;
        if (args.length == 0) {
            faction = factionsHandler.getFaction(player);
            if (faction == null) {
                Language.sendRawMessage("&cYou are in no faction. Please select one", player);
                return;
            }
        } else {
            faction =  factionsHandler.getFaction(args[0]);
        }
        if (faction == null) {
            CommandExecuteError(CommandExecuteError.NoFaction, player);
            return;
        }

        var displayName = faction.getDisplayName();
        var topBottomMessage = "=".repeat(ChatColor.stripColor(displayName).length() + 10);
        Language.sendRawMessage(topBottomMessage, player);
        Language.sendRawMessage("&f====  &e" + displayName + "  &f====", player);
        Language.sendRawMessage(topBottomMessage, player);

        Language.sendRawMessage("Description: " + faction.getDescription(), player);

        Language.sendRawMessage("Owner: &e" + Bukkit.getOfflinePlayer(faction.getOwner()).getName(), player);

        Language.sendRawMessage("Members online: " +
                faction.getPlayersOnline().size() + "/" +
                faction.getAllPlayers().size(), player);

        Language.sendRawMessage("Power: " +
                faction.getPowerManager().getPower() + "/" +
                faction.getPowerManager().getMaxPower(), player);

        Language.sendRawMessage("Chunk claim: " +
                faction.getClaimedChunks() + "/" +
                faction.getPowerManager().getPower(), player);

        Language.sendRawMessage("Wars: " +
                String.join(", ", faction.getRelationManager().getEnemies()), player);

        Language.sendRawMessage("Ally: " +
                String.join(", ", faction.getRelationManager().getAllies()), player);

        Language.sendRawMessage("Banned players: &7" + faction.getBannedPlayers().size(), player);


        String allBanned = String.join(", ", faction.getBannedPlayers().stream().map(x -> Bukkit.getOfflinePlayer(x).getName()).toArray(String[]::new));
        if (allBanned != null && !allBanned.isEmpty()) Language.sendRawMessage(allBanned, player);

        if (faction.getBank().balance() == null) {
            Language.sendRawMessage("Balance: &eFaction economy disabled", player);
        } else {
            Language.sendRawMessage("Balance: &e" + factionsHandler.getEconomy().format(faction.getBank().balance().balance), player);
        }
        if (faction.isFrozen()) Language.sendRawMessage("&bFrozen", player);
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return factionsHandler.getFactionNames();
    }
}
