package io.github.toberocat.improvedfactions.listeners;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.data.PlayerData;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.utility.ChunkUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class OnEntityDamage implements Listener {

    @EventHandler
    public void entityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            PlayerData attackerData = ImprovedFactionsMain.playerData.get(attacker.getUniqueId());
            if (event.getEntity() instanceof Player) {
                Player target = (Player) event.getEntity();
                PlayerData targetData = ImprovedFactionsMain.playerData.get(target.getUniqueId());
                if (targetData.playerFaction == attackerData.playerFaction) {
                    event.setCancelled(true);
                    attacker.sendMessage(Language.getPrefix() + "§cCannot attack your faction member");
                }
            } else {
                Faction claimFaction = ChunkUtils.GetFactionClaimedChunk(event.getEntity().getLocation().getChunk());

                if (claimFaction == null)
                    return;

                if (FactionUtils.getFaction(attacker) == null) {
                    event.setCancelled(true);
                    return;
                }

                if (!claimFaction.getRegistryName()
                        .equals(attackerData.playerFaction.getRegistryName())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
