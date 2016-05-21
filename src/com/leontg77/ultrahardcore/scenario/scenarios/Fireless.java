package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Fireless scenario class.
 * 
 * @author LeonTG77
 */
public class Fireless extends Scenario implements Listener {

    /**
     * Fireless class constructor.
     */
    public Fireless() {
        super("Fireless", "You cannot take fire or lava damage.");
    }

    @EventHandler(ignoreCancelled = true)
    public void on(EntityDamageEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        switch (event.getCause()) {
        case FIRE:
        case FIRE_TICK:
        case LAVA:
            event.setCancelled(true);
            break;
        default:
            break;
        }
    }
}