package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Board command class.
 * 
 * @author LeonTG77
 */
public class BoardCommand extends UHCCommand {	

	public BoardCommand() {
		super("board", "");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (State.isState(State.INGAME)) {
			throw new CommandException("You cannot toggle the board when the game has started.");
		}
		
		final BoardManager score = BoardManager.getInstance();
		
		if (game.pregameBoard()) {
			for (String entry : score.getBoard().getEntries()) {
				score.resetScore(entry);
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "Cleared pregame board.");
			game.setPregameBoard(false);
			return true;
		}
		
		for (String entry : score.getBoard().getEntries()) {
			score.resetScore(entry);
		}
		
		PlayerUtils.broadcast(Main.PREFIX + "Generated pregame board.");
		game.setPregameBoard(true);

		if (game.teamManagement()) {
			score.setScore("�e ", 14);
			score.setScore("�8� �cTeam:", 13);
			score.setScore("�8� �7/team", 12);
		}
		
		if (Arena.getInstance().isEnabled()) {
			score.setScore("�a ", 11);
			score.setScore("�8� �cArena:", 10);
			score.setScore("�8� �7/a ", 9);
		}

		score.setScore("�b ", 8);
		score.setScore("�8� �cTeamsize:", 7);
		score.setScore("�8� �7" + game.getAdvancedTeamSize(true, false), 6);
		
		score.setScore("�c ", 5);
		score.setScore("�8� �cScenarios:", 4);
		
		for (String scen : game.getScenarios().split(", ")) {
			score.setScore("�8� �7" + scen, 3);
		}
		
		score.setScore("�d ", 2);
		score.setScore("�8�m------------", 1);
		score.setScore("�a�o@ArcticUHC", 1);
		score.setScore("�a�o@ArcticUHC", 0);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new ArrayList<String>();
	}
}