package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Depths scenario class
 * 
 * @author LeonTG77
 */
public class Depths extends Scenario implements Listener {

    public Depths() {
        super("Depths", "The lower you go, the more mobs damage you.");
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {}

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player || (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player)) {
            return;
        }

        Entity entity = event.getEntity();

        if (entity.getLocation().getBlockY() <= 15) {
            event.setDamage(event.getDamage() * 5);
        }
        else if (entity.getLocation().getBlockY() <= 30) {
            event.setDamage(event.getDamage() * 3);
        }
        else if (entity.getLocation().getBlockY() <= 45) {
            event.setDamage(event.getDamage() * 2);
        }
        else if (entity.getLocation().getBlockY() <= 60) {
            event.setDamage(event.getDamage() * 1.5);
        }
    }
}