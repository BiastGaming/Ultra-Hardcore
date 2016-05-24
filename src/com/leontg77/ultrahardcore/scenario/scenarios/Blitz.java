package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.events.MeetupEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Blitz scenario class.
 * 
 * @author LeonTG77
 */
public class Blitz extends Scenario implements Listener {

    public Blitz() {
        super("Blitz", "Players start at 0.5 hearts. The game lasts a maximum of 1 hour. Players will normally be healed before meetup/sudden death.");
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
        for (Player online : game.getPlayers()) {
            online.setHealth(1);
        }
    }

    @EventHandler
    public void on(MeetupEvent event) {
        for (Player online : game.getPlayers()) {
            User user = plugin.getUser(online);
            user.resetHealth();
        }
    }
}