package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * MeleeFun scenario class.
 * 
 * @author D4mnX
 */
public class MeleeFun extends Scenario implements Listener {
    private final Main plugin;

    public MeleeFun(Main plugin) {
        super("MeleeFun", "There is no delay between hits. However fast you click is how fast you hit someone.");

        this.plugin = plugin;
    }

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

        final Player player = (Player) entity;
        
        event.setDamage(event.getDamage() * 0.5);
        
        new BukkitRunnable() {
            public void run() {
                player.setNoDamageTicks(0);
            }
        }.runTaskLater(plugin, 1);
    }
}