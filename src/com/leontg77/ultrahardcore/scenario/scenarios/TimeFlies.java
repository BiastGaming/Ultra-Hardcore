package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * TimeFlies scenario class.
 * 
 * @author LeonTG77
 */
public class TimeFlies extends Scenario implements Listener {
    private BukkitRunnable task = null;
    
    public TimeFlies() {
        super("TimeFlies", "The minecraft day/night cycle is twice as fast.");
    }
    
    @Override
    public void onDisable() {
        if (task != null) {
            task.cancel();
        }

        task = null;
    }

    @Override
    public void onEnable() {
        if (!game.isState(State.INGAME)) {
            return;
        }

        on(new GameStartEvent());
    }

    @EventHandler
    public void on(GameStartEvent event) {
        task = new BukkitRunnable() {
            public void run() {
                for (World world : game.getWorlds()) {
                    if (world.getGameRuleValue("doDaylightCycle").equals("false")) {
                        return;
                    }

                    world.setTime(world.getTime() + 1);
                }
            }
        };

        task.runTaskTimer(plugin, 1, 1);
    }
}