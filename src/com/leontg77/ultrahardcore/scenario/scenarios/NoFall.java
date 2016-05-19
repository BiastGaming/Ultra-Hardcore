package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * No fall scenario class
 * 
 * @author LeonTG77
 */
public class NoFall extends Scenario implements Listener {

    public NoFall() {
        super("NoFall", "You cannot take fall damage.");
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {}

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (event.getCause() != DamageCause.FALL) {
            return;
        }

        event.setCancelled(true);
    }
}