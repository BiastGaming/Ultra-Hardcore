package com.leontg77.ultrahardcore.feature.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.minigames.Arena;

/**
 * Kill board feature class.
 * 
 * @author LeonTG77
 */
public class KillBoardFeature extends Feature implements Listener {
    private final Arena arena;
    private final Game game;

    private final BoardManager board;
    private final TeamManager teams;

    public KillBoardFeature(Arena arena, Game game, BoardManager board, TeamManager teams) {
        super("Kill Board", "Adds your kill to the sidebar when you get one.");

        this.arena = arena;
        this.game = game;

        this.board = board;
        this.teams = teams;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(final PlayerDeathEvent event) {
        Player player = event.getEntity();

        // the arena has it's own way of doing deaths.
        if (arena.isEnabled() && arena.hasPlayer(player)) {
            return;
        }

        if (!game.isState(State.INGAME) || !game.getWorlds().contains(player.getWorld())) {
            return;
        }

        final Player killer = player.getKiller();

        if (killer == null) {
            if (game.isRecordedRound()) {
                return;
            }

            board.setScore("§8» §c§oPvE", board.getScore("§8» §c§oPvE") + 1);
            return;
        }

        if (killer.isDead()) {
            return;
        }

        Team pteam = teams.getTeam(player);
        Team team = teams.getTeam(killer);

        if (pteam != null && pteam.equals(team)) {
            if (!team.getName().equals("soloNoNametag") && !pteam.getName().equals("soloNoNametag")) {
                return;
            }
        }

        board.setScore(killer.getName(), board.getScore(killer.getName()) + 1);
    }
}