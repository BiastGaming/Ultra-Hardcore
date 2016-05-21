package com.leontg77.ultrahardcore.feature.world;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.scenario.scenarios.Flooded;
import com.leontg77.ultrahardcore.scenario.scenarios.Snowday;

/**
 * Weather feature class.
 * 
 * @author LeonTG77
 */
public class WeatherFeature extends Feature implements Listener {
    private final Game game;

    private final ScenarioManager scen;
    private final Timer timer;

    public WeatherFeature(Game game, Timer timer, ScenarioManager scen) {
        super("Weather", "Disable thunder storms completly and disable rain before pvp and after meetup.");

        this.game = game;

        this.timer = timer;
        this.scen = scen;
    }

    @EventHandler
    public void on(WeatherChangeEvent event) {
        if (!event.toWeatherState()) {
            if (scen.getScenario(Flooded.class).isEnabled() || scen.getScenario(Snowday.class).isEnabled()) {
                event.setCancelled(true);
            }
            return;
        }

        World world = event.getWorld();

        if (!game.getWorlds().contains(world)) {
            event.setCancelled(true);
            return;
        }

        if (!game.isState(State.INGAME)) {
            event.setCancelled(true);
            return;
        }

        if (scen.getScenario(Flooded.class).isEnabled() || scen.getScenario(Snowday.class).isEnabled()) {
            return;
        }

        if (timer.getMeetup() <= 0) {
            event.setCancelled(true);
            return;
        }

        if (timer.getPvP() > 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(ThunderChangeEvent event) {
        if (!event.toThunderState()) {
            return;
        }

        event.setCancelled(true);
    }
}