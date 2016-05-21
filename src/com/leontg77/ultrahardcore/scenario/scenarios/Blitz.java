package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.events.MeetupEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Blitz scenario class
 * 
 * @author LeonTG77
 */
public class Blitz extends Scenario implements Listener {
    private final Main plugin;

    private final Timer timer;
    private final Game game;

    public Blitz(Main plugin, Timer timer, Game game) {
        super("Blitz", "Players start at 0.5 hearts. The game lasts a maximum of 1 hour. Players will normally be healed before meetup/sudden death.");

        this.plugin = plugin;

        this.timer = timer;
        this.game = game;
    }

    @Override
    public void onEnable() {
        game.setMeetup(60);
        
        if (!game.isState(State.INGAME)) {
            return;
        }

        on(new GameStartEvent());

        if (timer.getMeetup() > 0) {
            return;
        }

        on(new MeetupEvent());
    }

    @EventHandler
    public void on(GameStartEvent event) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.setHealth(1);
        }
    }

    @EventHandler
    public void on(MeetupEvent event) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            plugin.getUser(online).resetHealth();
        }
    }
}