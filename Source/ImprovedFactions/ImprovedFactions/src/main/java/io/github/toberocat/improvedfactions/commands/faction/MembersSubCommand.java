package io.github.toberocat.improvedfactions.commands.faction;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import org.bukkit.entity.Player;

import java.util.List;

public class MembersSubCommand extends SubCommand {
    public MembersSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "members", "");
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings().setNeedsFaction(SubCommandSettings.NYI.Yes);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        var faction = factionsHandler.getFaction(player);
        if (faction == null){
            return;
        }

        var players = faction.getAllPlayers();
        var playerNames = players.stream().map(Player::getName).toList();
        player.sendMessage("Members:\n" + String.join("\n", playerNames));
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }
}
