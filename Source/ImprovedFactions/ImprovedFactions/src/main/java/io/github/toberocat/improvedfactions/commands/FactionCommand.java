package io.github.toberocat.improvedfactions.commands;

import io.github.toberocat.improvedfactions.commands.admin.*;
import io.github.toberocat.improvedfactions.commands.faction.owner.BanSubCommand;
import io.github.toberocat.improvedfactions.commands.faction.owner.DeleteSubCommand;
import io.github.toberocat.improvedfactions.commands.faction.owner.InviteAcceptSubCommand;
import io.github.toberocat.improvedfactions.commands.faction.owner.InviteSubCommand;
import io.github.toberocat.improvedfactions.commands.faction.owner.KickSubCommand;
import io.github.toberocat.improvedfactions.commands.faction.owner.SetRulesSubCommand;
import io.github.toberocat.improvedfactions.commands.faction.owner.UnbanSubCommand;
import io.github.toberocat.improvedfactions.commands.faction.power.LeftPowerSubCommand;
import io.github.toberocat.improvedfactions.commands.faction.power.PowerSubCommand;
import io.github.toberocat.improvedfactions.commands.faction.*;
import io.github.toberocat.improvedfactions.commands.faction.claim.ClaimSubCommand;
import io.github.toberocat.improvedfactions.commands.faction.claim.UnclaimSubCommand;
import io.github.toberocat.improvedfactions.commands.faction.relations.*;
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
        addSubCommand(new HelpSubCommand(factionsHandler));
        addSubCommand(new CreateSubCommand(factionsHandler));
        addSubCommand(new LeaveSubCommand(factionsHandler));
        addSubCommand(new JoinSubCommand(factionsHandler));
        addSubCommand(new SaveSubCommand(factionsHandler));
        addSubCommand(new DeleteSubCommand(factionsHandler));
        addSubCommand(new ClaimSubCommand(factionsHandler));
        addSubCommand(new UnclaimSubCommand(factionsHandler));
        addSubCommand(new VersionSubCommand(factionsHandler));
        //addSubCommand(new SettingsSubCommand(factionsHandler));
        addSubCommand(new JoinPrivateFactionSubCommand(factionsHandler));
        addSubCommand(new InviteSubCommand(factionsHandler));
        //addSubCommand(new RankSubCommand(factionsHandler));
        addSubCommand(new KickSubCommand(factionsHandler));
        addSubCommand(new MapSubCommand(factionsHandler));
        addSubCommand(new DescriptionSubCommand(factionsHandler));
        addSubCommand(new BanSubCommand(factionsHandler));
        addSubCommand(new UnbanSubCommand(factionsHandler));
        //addSubCommand(new ReloadConfigSubCommand(factionsHandler));
        addSubCommand(new ListBannedSubCommand(factionsHandler));
        addSubCommand(new RulesSubCommand(factionsHandler));
        addSubCommand(new SetRulesSubCommand(factionsHandler));
        addSubCommand(new AdminSubCommand(factionsHandler));
        addSubCommand(new WhoSubCommand(factionsHandler));
        //addSubCommand(new AdminWarningSubCommand(factionsHandler));

        addSubCommand(new AllySubCommand(factionsHandler));
        addSubCommand(new AllyAcceptSubCommand(factionsHandler));
        addSubCommand(new AllyRejectSubCommand(factionsHandler));
        addSubCommand(new WarSubCommand(factionsHandler));
        addSubCommand(new NeutralSubCommand(factionsHandler));
        addSubCommand(new PowerSubCommand(factionsHandler));
        addSubCommand(new LeftPowerSubCommand(factionsHandler));
        addSubCommand(new TutorialCommand(factionsHandler));
        addSubCommand(new InviteAcceptSubCommand(factionsHandler));
        addSubCommand(new MembersSubCommand(factionsHandler));
        //addSubCommand(new ReportSubCommand(factionsHandler));

        this.factionsHandler = factionsHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (!player.isOp() && !factionsHandler.hasWorld(player.getWorld())) {
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
