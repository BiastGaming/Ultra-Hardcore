package com.leontg77.ultrahardcore.commands.team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Random command class.
 * 
 * @author LeonTG77
 */
public class RandomCommand extends UHCCommand {
	private final TeamManager teams;

	public RandomCommand(TeamManager teams) {
		super("random", "<size> <amount>");
		
		this.teams = teams;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (args.length < 2) {
			return false;
		}
		
		int amount = parseInt(args[1], "amount");
		int size = parseInt(args[0], "teamsize");
		
		PlayerUtils.broadcast(TeamCommand.PREFIX + "Randomizing §6" + amount + "§7 teams of §6" + size + "§7.");
		
		for (int i = 0; i < amount; i++) {
			List<Player> list = new ArrayList<Player>();
			
			for (Player online : Bukkit.getOnlinePlayers()) {
				if (teams.getTeam(online) == null) {
					list.add(online);
				}
			}
			
			Collections.shuffle(list);

			Team team = teams.findAvailableTeam();
			
			if (team == null) {
				throw new CommandException("There are no more available teams.");
			}
			
			for (int j = 0; j < size; j++) {
				if (list.isEmpty()) {
					sender.sendMessage(ChatColor.RED + "Error while adding a player to team " + team.getName() + ".");
					break;
				}
				
				Player player = list.remove(0);
				teams.joinTeam(team, player);
			}
			
			if (team.getSize() > 0) {
				teams.sendMessage(team, TeamCommand.PREFIX + "You were added to team §6" + team.getName() + "§7.");
				
				for (OfflinePlayer entry : teams.getPlayers(team)) {
					Player player = entry.getPlayer();
					
					if (player == null) {
						continue;
					}
					
					if (team.getSize() == 1) {
						player.sendMessage(TeamCommand.PREFIX + "You are a solo."); 
						continue;
					}

					player.sendMessage(TeamCommand.PREFIX + "Your teammates are:"); 
					
					for (String entryTwo : team.getEntries()) {
						if (entry.getName().equals(entryTwo)) {
							continue;
						}
						
						player.sendMessage(TeamCommand.PREFIX + "§a" + entryTwo);
					}
				}
			}
		}
		
		PlayerUtils.broadcast(TeamCommand.PREFIX + "Teams has been randomized.");
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new ArrayList<String>();
	}
}