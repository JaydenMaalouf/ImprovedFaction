// package io.github.toberocat.improvedfactions.commands.admin;

// import io.github.toberocat.improvedfactions.FactionsHandler;
// import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
// import io.github.toberocat.improvedfactions.language.Language;
// import io.github.toberocat.improvedfactions.utility.async.AsyncCore;
// import org.bukkit.entity.Player;

// import java.util.List;

// public class AdminClearReportsSubCommand extends SubCommand {
//     public AdminClearReportsSubCommand(FactionsHandler factionsHandler) {
//         super(factionsHandler, "clearreports", "");
//     }

//     @Override
//     protected void CommandExecute(Player player, String[] args) {
//         AsyncCore.Run(() -> {
//             factionsHandler.getReports().clear();
//             player.sendMessage(Language.getPrefix() + "All reports removed");
//         });
//     }

//     @Override
//     protected List<String> CommandTab(Player player, String[] args) {
//         return null;
//     }
// }
