package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.Parseable;
import io.github.toberocat.improvedfactions.ranks.NewMemberRank;
import io.github.toberocat.improvedfactions.ranks.Rank;
import org.bukkit.entity.Player;

public class JoinPrivateFactionSubCommand extends SubCommand {
    public JoinPrivateFactionSubCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "join", "");
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings()
        .setAllowAliases(false)
        .setNeedsFaction(SubCommandSettings.NYI.No);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        if (args.length != 1) {
            CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
            return;
        }
        var faction = factionsHandler.getFaction(args[0]);
        if (faction.isFrozen()) {
            CommandExecuteError(CommandExecuteError.Frozen, player);
            return;
        }
        joinPrivate(player, args[0]);
    }

    public void joinPrivate(Player player, String factionName) {
        var playerFaction = factionsHandler.getFaction(player);
        if (playerFaction == null) {
            var faction = factionsHandler.getFaction(factionName);
            if (faction == null) {
                Language.sendMessage(LangMessage.JOIN_ERROR_NO_FACTION_FOUND, player);
                return;
            }

            if (faction.isBanned(player)) {
                Language.sendMessage(LangMessage.JOIN_ERROR_FACTION_BANNED, player);
                return;
            }

            if (faction.join(player, Rank.fromString(NewMemberRank.registry))) {
                Language.sendMessage(LangMessage.JOIN_SUCCESS, player,
                        new Parseable("{faction_displayname}", faction.getDisplayName()));
            } else {
                if (faction.hasMaxMembers())
                    Language.sendMessage(LangMessage.JOIN_FULL, player);
            }
        } else {
            player.sendMessage(Language.getPrefix()
                    + "§cYou have already joined a faction. Please leave before joining another faction");
        }
    }

    @Override
    protected boolean CommandDisplayCondition(Player player, String[] args) {
        return false;
    }

    @Override
    public void CallSubCommand(Player player, String[] args) {
        CommandExecute(player, args);
    }
}
