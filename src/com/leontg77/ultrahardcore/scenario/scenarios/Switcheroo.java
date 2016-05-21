package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Switcheroo scenario class
 * 
 * @author LeonTG77
 */
public class Switcheroo extends Scenario implements Listener {

    public Switcheroo() {
        super("Switcheroo", "When you shoot someone, you trade places with them");
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {}

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;

        if (!(damager instanceof Arrow)) {
            return;
        }

        Arrow proj = (Arrow) damager;

        if (!(proj.getShooter() instanceof Player)) {
            return;
        }

        Player shooter = (Player) proj.getShooter();

        Location loc1 = player.getLocation();
        Location loc2 = shooter.getLocation();

        shooter.teleport(loc1);
        player.teleport(loc2);
    }
}