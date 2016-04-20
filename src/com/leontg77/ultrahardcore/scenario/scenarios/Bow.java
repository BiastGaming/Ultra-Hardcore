package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Bow scenario class.
 * 
 * @author LeonTG77
 */
public class Bow extends Scenario implements Listener {

	public Bow() {
		super("Bow", "All kinds of melee, including sword, punch, or axe, is disabled. Even iPVP is disabled. The only way to damage a player is with a bow. You are able to damage mobs with sword/melee, however.");
	}

    @EventHandler(ignoreCancelled = true)
    public void on(EntityDamageByEntityEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
        
	    Entity damager = event.getDamager();
	    Entity entity = event.getEntity();
	    
	    if (!(entity instanceof Player) || !(damager instanceof Player)) {
	    	return;
	    }
	    
	    event.setCancelled(true);
    }
}