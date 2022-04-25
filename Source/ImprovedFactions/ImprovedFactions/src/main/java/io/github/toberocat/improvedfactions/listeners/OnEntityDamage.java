package io.github.toberocat.improvedfactions.listeners;

import io.github.toberocat.improvedfactions.FactionsHandler;
import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class OnEntityDamage implements Listener {
    private FactionsHandler factionsHandler;

    public OnEntityDamage(FactionsHandler factionsHandler) {
        this.factionsHandler = factionsHandler;
    }

    @EventHandler
    public void entityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player attacker) {
            if (!factionsHandler.getConfig().getBoolean("general.allowClaimProtection")) {
                return;
            }

            var attackerPlayerData = factionsHandler.getPlayerData(attacker);
            if (attackerPlayerData.getBypass()) {
                return;
            }

            if (event.getEntity() instanceof Player damagedPlayer) {
                var damagedPlayerData = factionsHandler.getPlayerData(damagedPlayer);

                if (attackerPlayerData != null && damagedPlayerData != null) {
                    if (damagedPlayerData.getPlayerFaction().getRegistryName()
                            .equals(attackerPlayerData.getPlayerFaction().getRegistryName())) {
                        event.setCancelled(true);
                        attacker.sendMessage(Language.getPrefix() + "§cCannot attack your faction member");
                    } else if (damagedPlayerData.getPlayerFaction().getRelationManager()
                            .isAllies(attackerPlayerData.getPlayerFaction())) {
                        event.setCancelled(true);
                        attacker.sendMessage(Language.getPrefix() + "§cCannot attack your faction ally member");
                    }
                }

            }
        }
    }
}
