package io.github.toberocat.improvedfactions.factions.relation;

import io.github.toberocat.improvedfactions.BaseManager;
import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RelationManager extends BaseManager {

    private List<String> allies;
    private List<String> enemies;
    private List<String> invites;

    public RelationManager(Faction faction, FactionsHandler factionsHandler) {
        super("relations", faction, factionsHandler);
        this.faction = faction;
        this.allies = new ArrayList<>();
        this.enemies = new ArrayList<>();
        this.invites = new ArrayList<>();

        this.factionsHandler = factionsHandler;
    }

    public void sendInvite(Faction faction) {
        faction.getRelationManager().addInvite(this.faction);
        new Thread(() -> {
            for (var member : faction.getOnlineMembers()) {
                if (member.getRank().isAdmin()) {
                    Bukkit.getPlayer(member.getUuid()).sendMessage(Language.getPrefix() +
                            Language.format("&e" + faction.getDisplayName()
                                    + " &f wants to be ally. Use &7/f allyaccept&f to accept, &7/f allycancel&f to deny"));
                }
            }
        }).start();
    }

    public void addInvite(Faction faction) {
        invites.add(faction.getRegistryName());
    }

    public void removeInvite(Faction faction) {
        invites.remove(faction.getRegistryName());
        new Thread(() -> {
            for (var member : this.faction.getOnlineMembers()) {
                if (member.getRank().isAdmin()) {
                    Bukkit.getPlayer(member.getUuid()).sendMessage(Language.getPrefix() +
                            Language.format("&e" + faction.getDisplayName() + " &frejected your invite to be allies"));
                }
            }
        }).start();
    }

    public void neutral(Faction faction) {
        allies.remove(faction.getRegistryName());
        enemies.remove(faction.getRegistryName());
        new Thread(() -> {
            for (var member : this.faction.getPlayersOnline()) {
                member.sendMessage(Language.getPrefix() + Language.format("&e" +
                        faction.getDisplayName() + " &f is now neutral towards your faction"));
            }
        }).start();
    }

    public void beginWar(Faction faction) {
        enemies.add(faction.getRegistryName());
        new Thread(() -> {
            for (var member : this.faction.getPlayersOnline()) {
                member.sendMessage(Language.getPrefix() + Language.format("&e" +
                        faction.getDisplayName() + " &f began a war with your faction"));
            }
        }).start();
    }

    public void makeAllies(Faction faction) {
        allies.add(faction.getRegistryName());
        new Thread(() -> {
            for (var member : this.faction.getPlayersOnline()) {
                member.sendMessage(Language.getPrefix() + Language.format("&e" +
                        faction.getDisplayName() + " &f is now your ally"));
            }
        }).start();
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    public List<String> getAllies() {
        return allies;
    }

    public boolean isAllies(Faction faction) {
        return allies.contains(faction.getRegistryName());
    }

    public void setAllies(ArrayList<String> allies) {
        this.allies = allies;
    }

    public List<String> getEnemies() {
        return enemies;
    }

    public boolean isEnemies(Faction faction) {
        return enemies.contains(faction.getRegistryName());
    }

    public void setEnemies(ArrayList<String> enemies) {
        this.enemies = enemies;
    }

    public List<String> getInvites() {
        return invites;
    }

    public void setInvites(ArrayList<String> invites) {
        this.invites = invites;
    }

    public boolean removeFactionInvite(Faction faction) {
        return invites.remove(faction.getRegistryName());
    }

    public boolean hasInvite(Faction faction) {
        return invites.contains(faction.getRegistryName());
    }

    public boolean acceptInvite(Faction inviteeFaction) {
        inviteeFaction.getRelationManager().makeAllies(this.faction);
        makeAllies(inviteeFaction);
        invites.remove(inviteeFaction.getRegistryName());
        return true;
    }

    @Override
    public void save() throws IOException {
        var config = new YamlConfiguration();
        config.set("allies", allies);
        config.set("enemies", enemies);
        config.set("invites", invites);
        super.internalSave(config);
    }

    @Override
    public void load() throws FileNotFoundException, IOException, InvalidConfigurationException {
        var config = super.internalLoad();
        allies = config.getStringList("allies");
        enemies = config.getStringList("enemies");
        invites = config.getStringList("invites");
    }
}
