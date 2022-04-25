package io.github.toberocat.improvedfactions.commands.factionCommands.adminSubCommands;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;

import java.util.List;

public class GPowerSubCommand extends SubCommand  {
    public GPowerSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "gpower", LangMessage.ADMIN_GPOWER_DESCRIPTION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (args.length == 2) {
            var faction = factionsHandler.getFaction(args[0]);

            if (faction == null) {
                player.sendMessage(Language.getPrefix() + "§cCan't find the faction");
                return;
            }

            int power = 0;
            try {
                power = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(Language.getPrefix() + "§cPower is no number");
                return;
            }

            faction.getPowerManager().setPower(faction.getPowerManager().getPower() + power);
            player.sendMessage(Language.getPrefix() + "§f Power has been set to §6" + faction.getPowerManager().getPower());
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        if (args.length == 1) {
            return factionsHandler.getFactionNames();
        }
        return null;
    }
}
