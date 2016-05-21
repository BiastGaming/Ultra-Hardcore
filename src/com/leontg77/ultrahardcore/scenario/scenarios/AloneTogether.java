package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.events.TeamJoinEvent;
import com.leontg77.ultrahardcore.events.TeamLeaveEvent;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * AloneTogether scenario class.
 * 
 * @author LeonTG77
 */
public class AloneTogether extends Scenario implements Listener {
    private final Main plugin;
    private final Game game;

    private final TeamManager teams;

    /**
     * AloneTogether class constructor.
     *
     * @param game The game class.
     * @param teams The team manager.
     */
    public AloneTogether(Main plugin, Game game, TeamManager teams) {
        super("AloneTogether", "Your teammates are vanished.");

        this.plugin = plugin;
        this.game = game;

        this.teams = teams;
    }

    @Override
    public void onDisable() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            update(online);
        }
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
        for (Player online : game.getPlayers()) {
            update(online);
        }
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        final Player player = event.getPlayer();

        if (!game.getPlayers().contains(player)) {
            return;
        }

        new BukkitRunnable() {
            public void run() {
                update(player);
            }
        }.runTaskLater(plugin, 5);

    }

    @EventHandler
    public void on(TeamJoinEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        for (Player online : game.getPlayers()) {
            update(online);
        }
    }

    @EventHandler
    public void on(TeamLeaveEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        for (Player online : game.getPlayers()) {
            update(online);
        }
    }

    /**
     * Update the given player and hide his teammates if any.
     *
     * @param player The player to update for.
     */
    @SuppressWarnings("deprecation")
    private void update(Player player) {
        Team team = teams.getTeam(player);

        if (team == null) {
            for (Player online : game.getPlayers()) {
                player.showPlayer(online);
                online.showPlayer(player);
            }
            return;
        }

        for (Player online : game.getPlayers()) {
            if (team.hasPlayer(online)) {
                player.hidePlayer(online);
                online.hidePlayer(player);
            } else {
                player.showPlayer(online);
                online.showPlayer(player);
            }
        }
    }
}