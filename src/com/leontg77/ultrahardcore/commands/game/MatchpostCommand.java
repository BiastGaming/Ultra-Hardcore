package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;

/**
 * Matchpost command class.
 * 
 * @author LeonTG77
 */
public class MatchpostCommand extends UHCCommand {	
	private final Game game;

	public MatchpostCommand(Game game) {
		super("matchpost", "");
		
		this.game = game;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		final String teamSize = game.getTeamSize().toLowerCase();
		
		if (teamSize.startsWith("no") || teamSize.startsWith("open")) {
			throw new CommandException("There are no matches running.");
		}
		
		sender.sendMessage(Main.PREFIX + "Match post: §a" + game.getMatchPost());
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new ArrayList<String>();
	}
}