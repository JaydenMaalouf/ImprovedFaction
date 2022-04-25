package io.github.toberocat.improvedfactions.commands.factionCommands.adminSubCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class SafezoneSubCommand extends SubCommand {
    public SafezoneSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "safezone", LangMessage.ADMIN_SAFEZONE_DESCRIPTION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        Faction faction = factionsHandler.getFaction("safezone");
        if (faction == null) {
            faction = factionsHandler.createFaction(Language.format(
                factionsHandler.getConfig().getString("general.safezoneText")), player);
            faction.setRegistryName("safezone");
            faction.getPowerManager().setMaxPower(9999);
            faction.getPowerManager().setPower(9999);
        }

        faction.claimChunk(player.getLocation().getChunk(), null);
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
