package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Eggs scenario class.
 * 
 * @author LeonTG77
 */
public class Eggs extends Scenario implements Listener {

    public Eggs() {
        super("Eggs", "When you throw an egg, a random mob will spawn where it lands. This includes enderdragons and withers. When you kill a chicken, there is a 5% chance of it dropping an egg.");
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Entity entity = event.getEntity();

        // check if the entity was a chicken, if not return.
        if (!(entity instanceof Chicken)) {
            return;
        }

        Random rand = new Random();
        double chance = 0.05;

        // check if the random value is more than the chance, if so return.
        if (rand.nextDouble() > chance) {
            return;
        }

        // add an egg to the drops of the chicken.
        event.getDrops().add(new ItemStack(Material.EGG));
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Projectile proj = event.getEntity();

        // check if the projectile is an egg, if not return.
        if (!(proj instanceof Egg)) {
            return;
        }

        Random rand = new Random();

        // make a list of all the entitytypes
        ArrayList<EntityType> types = new ArrayList<EntityType>();

        // Loop over all entity types.
        for (EntityType type : EntityType.values()) {
            // if the current looped type isnt alive AND isn't spawnable, hop over this loop.
            if (!type.isAlive() || !type.isSpawnable()) {
                continue;
            }

            // add the loop value to the list.
            types.add(type);
        }

        // get a random type out of the list we just created.
        EntityType type = types.get(rand.nextInt(types.size()));

        // get the location and world the projectile hit in.
        Location loc = proj.getLocation();
        World world = proj.getWorld();

        // spawn the entity in the world and location we got.
        world.spawnEntity(loc, type);
    }
}