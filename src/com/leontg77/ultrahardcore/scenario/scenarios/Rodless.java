package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Entity;
import org.bukkit.entity.FishHook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Rodless scenario class.
 * 
 * @author LeonTG77
 */
public class Rodless extends Scenario implements Listener {

	/**
	 * Rodless class constructor.
	 */
	public Rodless() {
		super("Rodless", "Rods will not \"hit\" mobs/players when you it shot at them, rods can only be used for fishing.");
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		
		if (damager instanceof FishHook) {
			event.setCancelled(true);
		}
	}
}