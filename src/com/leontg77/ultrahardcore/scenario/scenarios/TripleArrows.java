package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;

import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * TripleArrows scenario class
 * 
 * @author LeonTG77
 */
public class TripleArrows extends Scenario implements Listener {

    public TripleArrows() {
        super("3xArrows", "When you shoot an arrow, not one but three get shot.");
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {}

    @EventHandler
    public void on(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        LivingEntity entity = event.getEntity();

        Arrow arrow = entity.launchProjectile(Arrow.class);
        Arrow arrow2 = entity.launchProjectile(Arrow.class);

        arrow.setVelocity(event.getProjectile().getVelocity());
        arrow2.setVelocity(event.getProjectile().getVelocity());
    }
}