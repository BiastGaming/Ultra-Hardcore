package com.leontg77.ultrahardcore.commands.team;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.SpecInfo;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.managers.TeamManager;

/**
 * PMores command class.
 * 
 * @author LeonTG77
 */
public class PMMinedOresCommand extends UHCCommand {
	private final Game game;
	
	private final TeamManager teams;
	private final SpecManager spec;

	public PMMinedOresCommand(Game game, SpecManager spec, TeamManager teams) {
		super("pmminedores", "");
		
		this.game = game;
		
		this.teams = teams;
		this.spec = spec;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (args.equals(args)) {
			throw new CommandException("This feature has been disabled until future notice.");
		}
		
		if (!(sender instanceof Player)) {
			throw new CommandException("Only players can send their ore count to their team.");
		}
		
		Player player = (Player) sender;

		if (!game.getPlayers().contains(player)) {
			throw new CommandException("You are not playing a match.");
		}
		
		Team team = teams.getTeam(player);
		
		if (team == null || spec.isSpectating(player)) { 
			throw new CommandException("You are not on a team.");
		}
		
		SpecInfo info = spec.getSpecInfo();
		
		int iron = info.getTotal(player).get(Material.IRON_ORE);
		int gold = info.getTotal(player).get(Material.GOLD_ORE);
		int dias = info.getTotal(player).get(Material.DIAMOND_ORE);
		
        teams.sendMessage(team, TeamCommand.PREFIX + "§6§o" + player.getName() + "§8§o: §7Iron: §a" + iron + " §7Gold: §a" + gold + " §7Diamonds: §a" + dias);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new ArrayList<String>();
	}
}