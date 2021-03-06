package com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.nms.v1_8_R3.mobs.rabbit;

import java.util.Random;

import org.bukkit.entity.Rabbit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class RabbitSpawnHandler implements Listener {

    protected static final Random random = new Random();

    @EventHandler
    public void on(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Rabbit)) return;

        Rabbit rabbit  = (Rabbit) event.getEntity();

        // switch to a killer rabbit
        if (random.nextDouble() < .01D) rabbit.setRabbitType(Rabbit.Type.THE_KILLER_BUNNY);
    }
}
