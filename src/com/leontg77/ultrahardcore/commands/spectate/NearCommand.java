package com.leontg77.ultrahardcore.commands.spectate;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Near command class.
 * 
 * @author LeonTG77
 */
public class NearCommand extends UHCCommand {
	private final TeamManager teams;
	private final SpecManager spec;
	
	public NearCommand(SpecManager spec, TeamManager teams) {
		super("near", "[radius]");
		
		this.teams = teams;
		this.spec = spec;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof Player)) {
			throw new CommandException("Only players can see nearby players.");
		}

		Player player = (Player) sender;
		
		if (!spec.isSpectating(player)) {
			throw new CommandException("You can only do this while spectating.");
		}
		
		StringBuilder nearList = new StringBuilder("");
		int radius = 200;
		
		if (args.length > 0) {
			radius = parseInt(args[0], "radius");
		}
		
		for (Entity near : PlayerUtils.getNearby(player.getLocation(), radius)) {
			if (!(near instanceof Player)) {
				continue;
			}

			Player nearby = (Player) near;
			
			if (nearby == player) {
				continue;
			}
			
			if (!player.canSee(nearby)) {
				continue;
			}
			
			if (nearList.length() > 0) {
				nearList.append("§8, ");
			}
			
			nearList.append(getTeamColorAndName(nearby) + "§7(§c" + ((int) player.getLocation().distance(nearby.getLocation())) + "m§7)§a");
		}
		
		player.sendMessage(Main.PREFIX + "Nearby players: §8(§7Radius: §6" + radius + "§8)");
		player.sendMessage("§8§ §7" + (nearList.length() > 0 ? nearList.toString().trim() : "There are no players nearby."));
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new ArrayList<String>();
	}

	/**
	 * Get the team color and the name for the given player.
	 * <p>
	 * If they're not on a team, it returns their name in white.
	 * 
	 * @param player The player checking.
	 * @return The team color and the name
	 */
	private String getTeamColorAndName(Player player) {
		Team team = teams.getTeam(player);
		
		if (team == null) {
			return "§f" + player.getName();
		}
		
		return team.getPrefix() + player.getName();
	}
}