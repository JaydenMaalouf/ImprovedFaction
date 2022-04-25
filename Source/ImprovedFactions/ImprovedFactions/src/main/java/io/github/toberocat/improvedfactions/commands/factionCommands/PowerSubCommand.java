package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class PowerSubCommand extends SubCommand {
    public PowerSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "power", "");
    }

    public SubCommandSettings getSettings() {
        return super.getSettings().setNeedsFaction(SubCommandSettings.NYI.Yes);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        Faction faction = null;
        if (args.length == 0) {
            faction = factionsHandler.getFaction(player);
        } else {
            faction = factionsHandler.getFaction(args[0]);
        }

        if (faction == null){
            return;
        }

        player.sendMessage(Language.getPrefix() + "§e"+faction.getDisplayName()+"'s current power: " +
                faction.getPowerManager().getPower() + " / " +
                faction.getPowerManager().getMaxPower());
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
