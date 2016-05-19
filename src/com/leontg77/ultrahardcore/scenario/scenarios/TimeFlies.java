package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * TimeFlies scenario class.
 * 
 * @author LeonTG77
 */
public class TimeFlies extends Scenario implements Listener {
    private final Main plugin;
    private final Game game;

    /**
     * TimeFlies class constructor.
     *
     * @param plugin The main class.
     * @param game The game class.
     */
    public TimeFlies(Main plugin, Game game) {
        super("TimeFlies", "The minecraft day/night cycle is twice as fast.");

        this.plugin = plugin;
        this.game = game;
    }

    private BukkitRunnable task = null;

    @Override
    public void onDisable() {
        if (task != null) {
            task.cancel();
        }

        task = null;
    }

    @Override
    public void onEnable() {
        if (!State.isState(State.INGAME)) {
            return;
        }

        on(new GameStartEvent());
    }

    @EventHandler
    public void on(GameStartEvent event) {
        task = new BukkitRunnable() {
            public void run() {
                World world = game.getWorld();

                if (world == null) {
                    return;
                }

                if (world.getGameRuleValue("doDaylightCycle").equals("false")) {
                    return;
                }

                world.setTime(world.getTime() + 1);
            }
        };

        task.runTaskTimer(plugin, 1, 1);
    }
}