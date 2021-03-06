package com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.nms.v1_8_R3.mobs.chicken;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEgg;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import net.minecraft.server.v1_8_R3.EntityEgg;

public class ThrownEggHandler implements Listener {

    @EventHandler
    public void on(ProjectileLaunchEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Egg)) return;

        Egg egg = (Egg) entity;

        // skip our own eggs
        if (((CraftEgg) entity).getHandle() instanceof CustomChickenEgg) return;

        // cancel the launch
        event.setCancelled(true);

        EntityEgg original = ((CraftEgg) egg).getHandle();

        // create our own egg from the original and spawn that
        CustomChickenEgg newEgg = new CustomChickenEgg(original.getWorld(), original.getShooter());
        original.getWorld().addEntity(newEgg, CreatureSpawnEvent.SpawnReason.EGG);
    }
}
