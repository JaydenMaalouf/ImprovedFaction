// package io.github.toberocat.improvedfactions.commands.admin;

// import io.github.toberocat.improvedfactions.FactionsHandler;
// import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
// import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
// import io.github.toberocat.improvedfactions.factions.Faction;
// import io.github.toberocat.improvedfactions.factions.FactionUtils;
// import io.github.toberocat.improvedfactions.language.Language;
// import org.bukkit.entity.Player;

// import java.util.List;

// public class AdminWarningSubCommand extends SubCommand {
//     public AdminWarningSubCommand(FactionsHandler factionsHandler) {
//         super(factionsHandler, "warn", "");
//     }

//     @Override
//     protected void CommandExecute(Player player, String[] args) {
//         if (args.length != 1) {
//             return;
//         }

//         var faction = factionsHandler.getFaction(args[0]);
//         if (faction == null){
//             return;
//         }

//         var result = ImprovedFactionsMain.WARNS.addWarn(faction);
//         //TODO: add faction warning max
//         if (result >= 5) {
//             faction.deleteFaction();
//             Language.sendRawMessage("Faction got disband due to it having more than 5 warnings", player);
//         }
//     }

//     @Override
//     protected List<String> CommandTab(Player player, String[] args) {
//         if (args.length == 1){
//             return factionsHandler.getFactionNames();
//         }
//         return null;
//     }
// }
