package com.leontg77.ultrahardcore.commands.team;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.managers.TeamManager;

/**
 * PMores command class.
 * 
 * @author LeonTG77
 */
public class PMOresCommand extends UHCCommand {
	private final Game game;
	
	private final TeamManager teams;
	private final SpecManager spec;

	public PMOresCommand(Game game, SpecManager spec, TeamManager teams) {
		super("pmores", "");
		
		this.game = game;
		
		this.teams = teams;
		this.spec = spec;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
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
		
		int iron = 0, gold = 0, dias = 0;
		
		for (ItemStack item : player.getInventory().getContents()) {
			if (item == null) {
				continue;
			}
			
			switch (item.getType()) {
			case IRON_ORE:
			case IRON_INGOT:
				iron += item.getAmount();
				break;
			case GOLD_ORE:
			case GOLD_INGOT:
				gold += item.getAmount();
				break;
			case DIAMOND_ORE:
			case DIAMOND:
				dias += item.getAmount();
				break;
			default:
				break;
			}
		}
		
        teams.sendMessage(team, TeamCommand.PREFIX + "§6§o" + player.getName() + "§8§o: §7Iron: §a" + iron + " §7Gold: §a" + gold + " §7Diamonds: §a" + dias);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new ArrayList<String>();
	}
}