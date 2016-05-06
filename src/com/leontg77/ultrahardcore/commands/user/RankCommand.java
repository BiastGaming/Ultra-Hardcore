package com.leontg77.ultrahardcore.commands.user;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User.Rank;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.NameUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Rank command class.
 * 
 * @author LeonTG77
 */
public class RankCommand extends UHCCommand {	
	private final Main plugin;

	public RankCommand(Main plugin) {
		super("rank", "<player> <rank>");
		
		this.plugin = plugin;
	}

	@Override	
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (args.length < 2) {
			return false;
		}
		
		OfflinePlayer target = PlayerUtils.getOfflinePlayer(args[0]);
		
		if (!plugin.fileExist(target.getUniqueId())) {
			throw new CommandException("'" + args[0] + "' has never joined this server.");
		}
		
		Rank rank;
		
		try {
			rank = Rank.valueOf(args[1].toUpperCase());
		} catch (Exception e) {
			throw new CommandException("'" + args[1] + "' is not an valid rank.");
		}
		
		PlayerUtils.broadcast(Main.PREFIX + "§6" + target.getName() + " §7has been given §a" + NameUtils.capitalizeString(rank.name(), false) + " §7rank.");
		plugin.getUser(target).setRank(rank);
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		
		if (args.length == 1) {
    		return allPlayers();
        }
		
		if (args.length == 2) {
    		for (Rank rank : Rank.values()) {
				toReturn.add(rank.name().toLowerCase());
    		}
        }
		
		return toReturn;
	}
}