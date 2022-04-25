// package io.github.toberocat.improvedfactions.commands.factionCommands;

// import io.github.toberocat.improvedfactions.FactionsHandler;
// import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
// import io.github.toberocat.improvedfactions.gui.FactionSettingsGui;
// import io.github.toberocat.improvedfactions.language.LangMessage;
// import io.github.toberocat.improvedfactions.language.Language;
// import org.bukkit.entity.Player;

// import java.util.List;

// public class SettingsSubCommand extends SubCommand {
//     public SettingsSubCommand(FactionsHandler factionsHandler) {
//         super(factionsHandler, "settings", LangMessage.SETTINGS_DESCRIPTION);
//     }

//     @Override
//     protected void CommandExecute(Player player, String[] args) {
//         var playerFaction = factionsHandler.getFaction(player);
//         if (playerFaction == null) {
//             player.sendMessage(Language.getPrefix() + "§cYou need to be in a faction to use this command");
//             return;
//         }

//         if (playerFaction.isFrozen()) {
//             CommandExecuteError(CommandExecuteError.Frozen, player);
//             return;
//         }

//         if (args.length == 0) {
//             if (playerFaction.getPlayerRank(player).isAdmin()) {
//                 new FactionSettingsGui(player, playerFaction);
//             } else {
//                 CommandExecuteError(CommandExecuteError.OnlyAdminCommand, player);
//             }
//         } else if (args.length == 2 && args[0].equals("motd")) {
//             playerFaction.setMotd(Language.format(args[1]));
//             player.sendMessage(Language.getPrefix() + Language.format("&a&lSuccessfully&f set motd"));
//         } else if (args.length == 2 && args[0].equals("rename")) {
//             playerFaction.setDisplayName(Language.format(args[1]));
//             player.sendMessage(Language.getPrefix() + Language.format("&a&lSuccessfully&f renamed faction"));
//         } else {
//             CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
//         }
//     }

//     @Override
//     protected List<String> CommandTab(Player player, String[] args) {
//         if (args.length == 1) {
//             return List.of("motd", "rename");
//         } else if (args.length == 2) {
//             return List.of("<new>");
//         }
//         return null;
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
//             result = false;
//         }
//         return result;
//     }
// }
