package com.leontg77.ultrahardcore.commands.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * ClearXp command class.
 * 
 * @author LeonTG77
 */
public class ClearXPCommand extends UHCCommand {
	private final Main plugin;

	public ClearXPCommand(Main plugin) {
		super("clearxp", "[player|*]");
		
		this.plugin = plugin;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				throw new CommandException("Only players can clear their own xp level.");
			}
			
			Player player = (Player) sender;
			User user = plugin.getUser(player);
			
			player.sendMessage(Main.PREFIX + "You cleared your own xp.");
			user.resetExp();
			return true;
		}
		
		if (!sender.hasPermission(getPermission() + ".other")) {
			throw new CommandException("You cannot clear other players xp levels.");
		}
		
		if (args[0].equals("*")) {
			for (Player online : Bukkit.getOnlinePlayers()) {
				User user = plugin.getUser(online);
				user.resetExp();
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "All players xp levels has been cleared.");
			return true;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		if (target == null) {
			throw new CommandException("'"  + args[0] + "' is not online.");
		}
		
		User user = plugin.getUser(target);
		user.resetExp();

		sender.sendMessage(Main.PREFIX + "You cleared §a" + target.getName() + "'s §7xp level.");
		target.sendMessage(Main.PREFIX + "Your xp level was cleared.");
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
    		return allPlayers();
		}
		
		return new ArrayList<String>();
	}
}