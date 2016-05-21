package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * BladChicken scenario class
 * 
 * @author LeonTG77
 */
public class BaldChicken extends Scenario implements Listener {

    public BaldChicken() {
        super("BaldChicken", "Chicken's drop no feathers. Skeletons drop 3-5 arrows.");
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Entity entity = event.getEntity();

        switch (entity.getType()) {
        case CHICKEN:
            for (ItemStack drop : event.getDrops()) {
                if (drop.getType() == Material.FEATHER) {
                    drop.setType(Material.AIR);
                }
            }
            break;
        case SKELETON:
            for (ItemStack drop : event.getDrops()) {
                if (drop.getType() == Material.ARROW) {
                    drop.setType(Material.AIR);
                }
            }

            event.getDrops().add(new ItemStack(Material.ARROW, NumberUtils.randomIntBetween(3, 5)));
            break;
        default:
            break;
        }
    }
}