// package io.github.toberocat.improvedfactions.commands.faction;

// import io.github.toberocat.improvedfactions.FactionsHandler;
// import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
// import io.github.toberocat.improvedfactions.language.Language;
// import io.github.toberocat.improvedfactions.reports.Report;

// import org.bukkit.Bukkit;
// import org.bukkit.entity.Player;

// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;
// import java.util.stream.Collectors;

// public class ReportSubCommand extends SubCommand {
//     public ReportSubCommand(FactionsHandler factionsHandler) {
//         super(factionsHandler, "report", "");
//     }

//     @Override
//     protected void CommandExecute(Player player, String[] args) {
//         if (args.length <= 1) {
//             CommandExecuteError(CommandExecuteError.NotEnoughArgs, player);
//             return;
//         }

//         var reportedPlayer = Bukkit.getPlayer(args[0]);
//         var reason = Language.format(Arrays.stream(args).skip(1).collect(Collectors.joining(" ")));
//         .addReport(new Report(player.getUniqueId(), reason, args[0]));

//         player.sendMessage(Language.getPrefix() + "Reported " + args[0] + " because of " + reason);
//     }

//     @Override
//     protected List<String> CommandTab(Player player, String[] args) {
//         var results = new ArrayList<String>();
//         if (args.length <= 1) {
//             for (var faction : factionsHandler.getFactions()) {
//                 results.add(faction.getRegistryName());
//             }
//         } else {
//             results.add("Reason");
//         }
//         return results;
//     }
// }
