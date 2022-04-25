package io.github.toberocat.improvedfactions.tab;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.FactionCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FactionCommandTab implements TabCompleter {

    private FactionsHandler factionsHandler;

    public FactionCommandTab(FactionsHandler factionsHandler) {
        this.factionsHandler = factionsHandler;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if (!player.isOp() && !factionsHandler.hasWorld(player.getWorld())) {
                return null;
            }
            // Display results

            var factionCommand = new FactionCommand(factionsHandler);
            var arguments = factionCommand.CallSubCommandsTab(player, args);
            if (arguments == null) {
                return null;
            }
            var results = new ArrayList<String>();
            for (var arg : args) {
                for (var a : arguments) {
                    if (a.toLowerCase().startsWith(arg.toLowerCase())) {
                        results.add(a);
                    }
                }
            }

            return results;
        }

        return null;
    }
}
