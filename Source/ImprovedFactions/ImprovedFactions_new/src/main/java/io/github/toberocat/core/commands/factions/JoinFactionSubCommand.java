package io.github.toberocat.core.commands.factions;

import io.github.toberocat.MainIF;
import io.github.toberocat.core.factions.Faction;
import io.github.toberocat.core.factions.FactionUtility;
import io.github.toberocat.core.factions.rank.Rank;
import io.github.toberocat.core.factions.rank.members.MemberRank;
import io.github.toberocat.core.utility.Result;
import io.github.toberocat.core.utility.command.SubCommand;
import io.github.toberocat.core.utility.command.SubCommandSettings;
import io.github.toberocat.core.utility.date.DateCore;
import io.github.toberocat.core.utility.language.Language;
import io.github.toberocat.core.utility.settings.PlayerSettings;
import io.github.toberocat.core.utility.settings.type.HiddenSetting;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

public class JoinFactionSubCommand extends SubCommand {
    public JoinFactionSubCommand() {
        super("join", "", false);
    }

    @Override
    public SubCommandSettings getSettings() {
        return super.getSettings().setArgLength(1).setNeedsFaction(SubCommandSettings.NYI.No);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        Faction faction = FactionUtility.getFactionByRegistry(args[0]);
        if (faction == null) {
            Language.sendRawMessage("&cCan't find given faction", player);
            return;
        }

        if (faction.getOpenType() == Faction.OpenType.CLOSED) {
            Language.sendRawMessage("&cGiven faction is private", player);
            return;
        }

        String timeout = (String) PlayerSettings.getSettings(player.getUniqueId()).getSetting("factionJoinTimeout").getSelected();
        if (!timeout.equals("-1")) {
            DateTimeFormatter fmt = DateCore.TIME_FORMAT;
            DateTime until = fmt.parseDateTime(timeout);
            System.out.println(DateTime.now().isAfter(until));

            if (!DateTime.now().isAfter(until)) {
                Period diff = new Period(DateTime.now(), until);

                Language.sendRawMessage("Can't join. You are in timeout until " + until.toString(fmt) + "&f. Please wait &6" + diff.toString(DateCore.PERIOD_FORMAT) + "&f until you can join again", player);
                return;
            }
        }
        Result result = faction.join(player, Rank.fromString(MemberRank.registry));
        if (result.isSuccess()) {
            Language.sendRawMessage("Joined &e" + faction.getDisplayName(), player);
        } else {
            Language.sendRawMessage(result.getPlayerMessage(), player);
        }
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return FactionUtility.getAllFactions();
    }
}
