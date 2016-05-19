package com.leontg77.ultrahardcore.feature.health;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import com.leontg77.ultrahardcore.feature.Feature;

/**
 * Health Regen feature class.
 * 
 * @author LeonTG77
 */
public class HealthRegenFeature extends Feature implements Listener {

    public HealthRegenFeature() {
        super("Health Regen", "Disables natural regeneration.");
    }

    @EventHandler
    public void on(final EntityRegainHealthEvent event) {
        final RegainReason reason = event.getRegainReason();
        final Entity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            return;
        }
        
        if (reason != RegainReason.REGEN && reason != RegainReason.SATIATED) {
            return;
        }
        
        event.setCancelled(true);
    }
}