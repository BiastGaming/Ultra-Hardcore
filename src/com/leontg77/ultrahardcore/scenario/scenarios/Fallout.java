package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Fallout scenario class.
 * 
 * @author LeonTG77
 */
public class Fallout extends Scenario implements Listener {
    private static final long TICKS_TO_START = (60 * 20) * 45; // 45 minutes in ticks, 20 ticks = 1 second.
    private static final long TICK_INTERVAL = 200L;
    
    public Fallout() {
        super("Fallout", "If you are not below Y=60 after 45 min, you will take 0.5 hearts of damage every 30 seconds.");
    }

    private BukkitRunnable task;

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
                for (Player online : game.getPlayers()) {
                    if (online.getLocation().getBlockY() < 60) {
                        continue;
                    }

                    PlayerUtils.damage(online, 1);
                }
            }
        };

        task.runTaskTimer(plugin, Math.max(TICK_INTERVAL, TICKS_TO_START - (timer.getTimeSinceStartInSeconds() * 20)), TICK_INTERVAL);
    }
}