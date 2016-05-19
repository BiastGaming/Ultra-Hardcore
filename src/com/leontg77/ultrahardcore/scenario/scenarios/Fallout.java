package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Fallout scenario class
 * 
 * @author LeonTG77
 */
public class Fallout extends Scenario implements Listener {
    private final Main plugin;
    private final Game game;

    public Fallout(Main plugin, Game game) {
        super("Fallout", "After a certain amount of time, any player above y: 60 will begin to take half a heart of damage every 30 seconds.");

        this.plugin = plugin;
        this.game = game;
    }

    private BukkitRunnable task;

    @Override
    public void onDisable() {
        task.cancel();
        task = null;
    }

    @Override
    public void onEnable() {}

    @EventHandler
    public void on(GameStartEvent event) {
        task = new BukkitRunnable() {
            public void run() {
                for (Player online : game.getPlayers()) {
                    if (online.getLocation().getBlockY() <= 60) {
                        continue;
                    }

                    PlayerUtils.damage(online, 1);
                }
            }
        };

        task.runTaskTimer(plugin, 600, 600);
    }
}