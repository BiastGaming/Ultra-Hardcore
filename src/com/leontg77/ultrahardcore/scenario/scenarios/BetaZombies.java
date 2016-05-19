package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * BetaZombies scenario class
 * 
 * @author LeonTG77
 */
public class BetaZombies extends Scenario implements Listener {

    public BetaZombies() {
        super("BetaZombies", "Zombies drop feathers");
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {}

    @EventHandler
    public void on(EntityDeathEvent event) {
        final Entity entity = event.getEntity();

        if (!(entity instanceof Zombie)) {
            return;
        }

        for (ItemStack drops : event.getDrops()) {
            if (drops.getType() != Material.ROTTEN_FLESH) {
                continue;
            }

            drops.setType(Material.FEATHER);
        }
    }
}