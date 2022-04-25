// package io.github.toberocat.improvedfactions.commands.factionCommands;

// import io.github.toberocat.improvedfactions.FactionsHandler;
// import io.github.toberocat.improvedfactions.commands.factionCommands.ranksCommands.PermissionsRankSubCommand;
// import io.github.toberocat.improvedfactions.commands.factionCommands.ranksCommands.SetUserRankSubCommand;
// import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
// import io.github.toberocat.improvedfactions.language.LangMessage;
// import io.github.toberocat.improvedfactions.language.Language;
// import org.bukkit.entity.Player;

// import java.util.List;

// public class RankSubCommand extends SubCommand {
//     public RankSubCommand(FactionsHandler factionsHandler) {
//         super(factionsHandler, "rank", LangMessage.RANK_DESCRIPTION);
//         subCommands.add(new SetUserRankSubCommand());
//         subCommands.add(new PermissionsRankSubCommand());
//     }

//     @Override
//     protected void CommandExecute(Player player, String[] args) {
//         var faction = factionsHandler.getFaction(player);
//         if (faction == null) {
//             CommandExecuteError(CommandExecuteError.NoFaction, player);
//             return;
//         }

//         if (faction.isFrozen()) {
//             CommandExecuteError(CommandExecuteError.Frozen, player);
//             return;
//         }

//         if (faction.getPlayerRank(player).isAdmin()) {
//             if (!this.CallSubCommands(player, args)) {
//                 player.sendMessage(Language.getPrefix() + "§cThis command doesn't exist");
//             }
//         } else {
//             CommandExecuteError(CommandExecuteError.NoFactionPermission, player);
//         }
//     }

//     @Override
//     protected List<String> CommandTab(Player player, String[] args) {
//         return this.CallSubCommandsTab(player, args);
//     }

//     @Override
//     protected boolean CommandDisplayCondition(Player player, String[] args) {
//         var result = super.CommandDisplayCondition(player, args);
//         var faction = factionsHandler.getFaction(player);
//         if (faction == null) {
//             return false;
//         }

//         var playerRank = faction.getPlayerRank(player);
//         if (playerRank != null && !playerRank.isAdmin()) {
//             return false;
//         }

//         return result;
//     }
// }
