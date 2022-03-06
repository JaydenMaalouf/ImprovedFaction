package io.github.toberocat.core.commands.factions;

import io.github.toberocat.core.utility.command.SubCommand;
import io.github.toberocat.core.utility.command.SubCommandSettings;
import io.github.toberocat.core.utility.factions.Faction;
import io.github.toberocat.core.utility.factions.FactionUtility;
import io.github.toberocat.core.utility.factions.members.FactionMemberManager;
import io.github.toberocat.core.utility.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class InviteSubCommand extends SubCommand {
    public InviteSubCommand() {
        super("invite", "", false);
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings().setNeedsFaction(SubCommandSettings.NYI.Yes).setArgLength(1);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        Faction faction = FactionUtility.getPlayerFaction(player);
        Player invited = Bukkit.getPlayer(args[0]);

        if (invited == null) {
            sendCommandExecuteError(CommandExecuteError.PlayerNotFound, player);
            return;
        }

        faction.getFactionMemberManager().invitePlayer(invited);
        Language.sendRawMessage("You invited " + args[0] + ". Invitation will be rejected after 5 minutes", player);

    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        FactionMemberManager manager = FactionUtility.getPlayerFaction(player).getFactionMemberManager();
        return Bukkit.getOnlinePlayers().stream().filter(user -> !manager.getMembers().contains(user.getUniqueId())
                && !manager.getBanned().contains(user.getUniqueId())).map(Player::getName).toList();
    }
}
