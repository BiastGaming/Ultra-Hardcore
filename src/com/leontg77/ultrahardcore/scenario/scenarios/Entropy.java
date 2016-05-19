package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Entropy scenario class
 * 
 * @author LeonTG77
 */
public class Entropy extends Scenario implements Listener {
    private final Main plugin;
    private final Game game;

    public Entropy(Main plugin, Game game) {
        super("Entropy", "Every 10 min 1XP level is \"drained\" from you. If you don't have any levels when the drain occurs, you die.");

        this.plugin = plugin;
        this.game = game;
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
        if (!State.isState(State.INGAME)) {
            return;
        }

        on(new GameStartEvent());
    }

    @EventHandler
    public void on(GameStartEvent event) {
        task = new BukkitRunnable() {
            public void run() {
                for (Player online : game.getPlayers()) {
                    if (online.getLevel() == 0) {
                        PlayerUtils.broadcast("§c" + online.getName() + " ran out of energy!");

                        // damage them so they hear the damage sound then kill them.
                        online.damage(0);
                        online.setHealth(0);
                        continue;
                    }

                    if (online.getLevel() > 0) {
                        online.sendMessage("§6You survived the drain! The next drain is in 10 minutes!");
                        online.setLevel(online.getLevel() - 1);
                    }
                }
            }
        };

        task.runTaskTimer(plugin, 12000, 12000);
    }
}