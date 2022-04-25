package io.github.toberocat.improvedfactions.commands;

import io.github.toberocat.improvedfactions.commands.admin.*;
import io.github.toberocat.improvedfactions.commands.factionCommands.*;
import io.github.toberocat.improvedfactions.commands.factionCommands.relations.*;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FactionCommand extends SubCommand implements CommandExecutor {

    private FactionsHandler factionsHandler;

    public FactionCommand(FactionsHandler factionsHandler) {
        super(factionsHandler, "", "");
        AddSubCommand(new HelpSubCommand(factionsHandler));
        AddSubCommand(new CreateSubCommand(factionsHandler));
        AddSubCommand(new LeaveSubCommand(factionsHandler));
        AddSubCommand(new JoinSubCommand(factionsHandler));
        AddSubCommand(new SaveSubCommand(factionsHandler));
        AddSubCommand(new DeleteSubCommand(factionsHandler));
        AddSubCommand(new ClaimChunkSubCommand(factionsHandler));
        AddSubCommand(new UnClaimChunkCommands(factionsHandler));
        AddSubCommand(new VersionSubCommand(factionsHandler));
        //AddSubCommand(new SettingsSubCommand(factionsHandler));
        AddSubCommand(new JoinPrivateFactionSubCommand(factionsHandler));
        AddSubCommand(new InviteSubCommand(factionsHandler));
        //AddSubCommand(new RankSubCommand(factionsHandler));
        AddSubCommand(new KickSubCommand(factionsHandler));
        AddSubCommand(new MapSubCommand(factionsHandler));
        AddSubCommand(new DescriptionSubCommand(factionsHandler));
        AddSubCommand(new BanSubCommand(factionsHandler));
        AddSubCommand(new UnbanSubCommand(factionsHandler));
        //AddSubCommand(new ReloadConfigSubCommand(factionsHandler));
        AddSubCommand(new ListBannedSubCommand(factionsHandler));
        AddSubCommand(new RulesSubCommand(factionsHandler));
        AddSubCommand(new SetRulesSubCommand(factionsHandler));
        AddSubCommand(new AdminSubCommand(factionsHandler));
        AddSubCommand(new WhoSubCommand(factionsHandler));
        //AddSubCommand(new AdminWarningSubCommand(factionsHandler));

        AddSubCommand(new AllySubCommand(factionsHandler));
        AddSubCommand(new AllyAcceptSubCommand(factionsHandler));
        AddSubCommand(new AllyRejectSubCommand(factionsHandler));
        AddSubCommand(new WarSubCommand(factionsHandler));
        AddSubCommand(new NeutralSubCommand(factionsHandler));
        AddSubCommand(new PowerSubCommand(factionsHandler));
        AddSubCommand(new ClaimPower(factionsHandler));
        AddSubCommand(new TutorialCommand(factionsHandler));
        AddSubCommand(new InviteAccept(factionsHandler));
        //AddSubCommand(new ReportSubCommand(factionsHandler));

        this.factionsHandler = factionsHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!player.isOp() && !factionsHandler.getWorlds().contains(player.getWorld().getName())) {
                Language.sendRawMessage("This world is disabled", player);
                return false;
            }
            if (args.length == 0) {
                Language.sendRawMessage("Can't use without parameters", player);
                return false;
            }

            if (!this.CallSubCommands(player, args)) {
                Language.sendMessage(LangMessage.THIS_COMMAND_DOES_NOT_EXIST, player);
            }
        } else {
            factionsHandler.getConsoleSender()
                    .sendMessage(Language.getPrefix() + "§cYou cannot use this command in the console");
        }
        return false;
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {

    }
}
