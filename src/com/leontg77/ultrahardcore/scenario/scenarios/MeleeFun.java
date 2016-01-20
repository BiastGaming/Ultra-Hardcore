package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * MeleeFun scenario class.
 * 
 * @author D4mnX
 */
public class MeleeFun extends Scenario implements Listener {
	
	public MeleeFun() {
		super("MeleeFun", "There is no delay between hits. However fast you click is how fast you hit someone.");
	}

	@Override
	public void onDisable() {}

	@Override
	public void onEnable() {}
	
	@EventHandler
    public void on(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity entity = event.getEntity();
		
		if (!(entity instanceof Player) && !(damager instanceof Player)) {
            return;
        }

        if (event.getCause() != DamageCause.ENTITY_ATTACK) {
            return;
        }

        final Player player = (Player) event.getEntity();
        event.setDamage(event.getDamage() * 0.5);
        
        Bukkit.getScheduler().runTaskLater(Main.plugin, new Runnable() {
            public void run() {
                player.setNoDamageTicks(0);
            }
        }, 1L);
    }
}