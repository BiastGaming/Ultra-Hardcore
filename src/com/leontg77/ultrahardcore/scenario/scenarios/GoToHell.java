package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.events.PvPEnableEvent;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.feature.portal.NetherFeature;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * GoToHell scenario class
 * 
 * @author LeonTG77
 */
public class GoToHell extends Scenario implements Listener {
    private final Main plugin;
    private final Game game;

    private final FeatureManager feat;
    private final Settings settings;

    public GoToHell(Main plugin, Game game, Settings settings, FeatureManager feat) {
        super("GoToHell", "After 45 minutes you have to be in the nether or else you take 0.5 hearts of damage every 30 seconds");

        this.plugin = plugin;
        this.game = game;

        this.settings = settings;
        this.feat = feat;
    }

    private BukkitRunnable task = null;

    @Override
    public void onDisable() {
        if (task != null && Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId())) {
            task.cancel();
        }

        task = null;
    }

    @Override
    public void onEnable() {
        feat.getFeature(NetherFeature.class).enable(settings);
    }

    @EventHandler
    public void on(PvPEnableEvent event) {
        task = new BukkitRunnable() {
            public void run() {
                for (Player online : game.getPlayers()) {
                    Environment env = online.getWorld().getEnvironment();

                    if (env == Environment.NETHER) {
                        continue;
                    }

                    PlayerUtils.damage(online, 1);
                }
            }
        };

        task.runTaskTimer(plugin, 600, 600);
    }
}