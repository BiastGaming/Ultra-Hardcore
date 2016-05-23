package com.leontg77.ultrahardcore.feature.scoreboard;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.minigames.Arena;

/**
 * Sidebar reset feature class.
 * 
 * @author LeonTG77
 */
public class SidebarResetFeature extends ToggleableFeature implements Listener {
    private final Arena arena;
    private final Game game;

    private final BoardManager board;
    private final TeamManager teams;

    public SidebarResetFeature(Arena arena, Game game, BoardManager board, TeamManager teams) {
        super("Sidebar Reset", "Make dead players sidebar score get reset when they die.");

        icon.setType(Material.SIGN);
        slot = 5;

        this.arena = arena;
        this.game = game;

        this.board = board;
        this.teams = teams;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(final PlayerDeathEvent event) {
        if (!isEnabled() || game.isRecordedRound()) {
            return;
        }

        Player player = event.getEntity();

        // the arena has it's own way of doing deaths.
        if (arena.isEnabled() && arena.hasPlayer(player)) {
            return;
        }

        if (!game.isState(State.INGAME) || !game.getWorlds().contains(player.getWorld())) {
            return;
        }

        Player killer = player.getKiller();

        if (killer == null) {
            board.resetScore(player.getName());
            return;
        }

        Team pteam = teams.getTeam(player);
        Team team = teams.getTeam(killer);

        if (pteam != null && pteam.equals(team)) {
            if (!team.getName().equals("soloNoNametag") && !pteam.getName().equals("soloNoNametag")) {
                return;
            }
        }

        board.resetScore(player.getName());
    }
}