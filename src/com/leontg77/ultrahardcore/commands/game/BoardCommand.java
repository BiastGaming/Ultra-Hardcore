package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.minigames.Arena;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Board command class.
 * 
 * @author LeonTG77
 */
public class BoardCommand extends UHCCommand {
    private final BoardManager board;

    private final Arena arena;
    private final Game game;

    public BoardCommand(Arena arena, Game game, BoardManager board) {
        super("board", "");

        this.board = board;

        this.arena = arena;
        this.game = game;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (game.isState(State.INGAME)) {
            throw new CommandException("You cannot toggle the board when the game has started.");
        }

        if (game.pregameBoard()) {
            for (String entry : board.getBoard().getEntries()) {
                board.resetScore(entry);
            }

            PlayerUtils.broadcast(Main.PREFIX + "Cleared pregame board.");
            game.setPregameBoard(false);
            return true;
        }

        for (String entry : board.getBoard().getEntries()) {
            board.resetScore(entry);
        }

        PlayerUtils.broadcast(Main.PREFIX + "Generated pregame board.");
        game.setPregameBoard(true);

        if (game.teamManagement()) {
            board.setScore("§e ", 14);
            board.setScore("§8» §cTeam:", 13);
            board.setScore("§8» §7/team", 12);
        }

        if (arena.isEnabled()) {
            board.setScore("§a ", 11);
            board.setScore("§8» §cArena:", 10);
            board.setScore("§8» §7/a ", 9);
        }

        board.setScore("§b ", 8);
        board.setScore("§8» §cTeamsize:", 7);
        board.setScore("§8» §7" + game.getAdvancedTeamSize(true, false), 6);

        board.setScore("§c ", 5);
        board.setScore("§8» §cScenarios:", 4);

        for (String scen : game.getScenarios().split(", ")) {
            board.setScore("§8» §7" + scen, 3);
        }

        board.setScore("§d ", 2);
        board.setScore("§8§m------------", 1);
        board.setScore("§a§o@ArcticUHC", 1);
        board.setScore("§a§o@ArcticUHC", 0);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }
}