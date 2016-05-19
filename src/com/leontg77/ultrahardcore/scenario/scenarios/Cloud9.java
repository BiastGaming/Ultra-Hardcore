package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.PvPEnableEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Cloud9 scenario class
 * 
 * @author LeonTG77
 */
public class Cloud9 extends Scenario implements Listener {
    private final Main plugin;
    private final Game game;

    public Cloud9(Main plugin, Game game) {
        super("Cloud9", "After 45 minutes, any player below y: 200 will begin to take half a heart of damage every 30 seconds, there are no starter items.");

        this.plugin = plugin;
        this.game = game;
    }

    private BukkitRunnable task;

    @Override
    public void onDisable() {
        task.cancel();
    }

    @Override
    public void onEnable() {}

    @EventHandler
    public void on(PvPEnableEvent event) {
        task = new BukkitRunnable() {
            public void run() {
                for (Player online : game.getPlayers()) {
                    if (online.getLocation().getY() > 200) {
                        continue;
                    }

                    PlayerUtils.damage(online, 1);
                }
            }
        };

        task.runTaskTimer(plugin, 600, 600);
    }
}