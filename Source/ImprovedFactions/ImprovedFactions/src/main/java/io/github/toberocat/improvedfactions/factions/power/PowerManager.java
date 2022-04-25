package io.github.toberocat.improvedfactions.factions.power;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionMember;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.utility.Debugger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PowerManager {

    private int power;
    private int maxPower;

    private Faction faction;
    private FactionsHandler factionsHandler;
    private boolean isGeneratingPower;

    public PowerManager(Faction faction, FactionsHandler factionsHandler) {
        this.factionsHandler = factionsHandler;
        this.faction = faction;

        power = factionsHandler.getConfig().getInt("factions.startClaimPower");
        maxPower = power;
        isGeneratingPower = false;
    }

    public void addFactionMember() {
        maxPower += factionsHandler.getConfig().getInt("factions.powerPerPlayer");
        power += factionsHandler.getConfig().getInt("factions.powerPerPlayer");

        // ImprovedFactionsMain.getPlugin().getConfig().getInt("faction.powerPerPlayer");
    }

    public void removeFactionMember() {
        maxPower -= factionsHandler.getConfig().getInt("factions.powerPerPlayer");
        power -= Math.max(factionsHandler.getConfig().getInt("factions.minPower"),
                factionsHandler.getConfig().getInt("factions.powerPerPlayer"));
    }

    public void claimChunk() {
    }

    public void unclaimChunk() {
    }

    public boolean canClaimChunk() {
        return (power > faction.getClaimedChunks());
    }

    public void playerDeath() {
        power -= Math.max(factionsHandler.getConfig().getInt("factions.minPower"),
                factionsHandler.getConfig().getInt("factions.powerLossPerDeath"));

        for (FactionMember member : faction.getMembers()) {
            if (member == null)
                continue;
            OfflinePlayer off = Bukkit.getOfflinePlayer(member.getUuid());

            if (off.isOnline()) {
                off.getPlayer().sendMessage(Language.getPrefix() + Language.format(
                        "&eSomeone died in your faction. You current power is &b" + power));
                if (power < faction.getClaimedChunks()) {
                    off.getPlayer().sendMessage(Language.getPrefix() + Language.format(
                            "&e" + (power - faction.getClaimedChunks()) + "&e chunks are currently unprotected."));
                }
            }
        }

        startRegenerationThread();
    }

    public void startRegenerationThread() {
        if (isGeneratingPower) {
            return;
        }
        if (power == maxPower) {
            return;
        }
        
        new Thread(() -> {
            isGeneratingPower = true;
            Debugger.LogInfo("Started generation of power");
            while (power < maxPower) {
                try {
                    Thread.sleep(factionsHandler.getConfig()
                            .getInt("factions.regenerationRate"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                regenerate();
            }

            isGeneratingPower = false;
        }).start();
    }

    private void regenerate() {
        var members = faction.getPlayersOnline();
        power += members.size() * factionsHandler.getConfig().getInt("factions.regenerationPerRate");

        if (power >= maxPower) {
            power = maxPower;
        }
        for (var player : members) {
            player.sendMessage(Language.getPrefix() + Language.format(
                    "&eSome power regenerated. Current power: &b" + power + " / " + maxPower));
        }
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getMaxPower() {
        return maxPower;
    }

    public void setMaxPower(int maxPower) {
        this.maxPower = maxPower;
    }
}
