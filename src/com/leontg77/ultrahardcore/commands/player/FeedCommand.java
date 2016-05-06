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
 * Feed command class.
 * 
 * @author LeonTG77
 */
public class FeedCommand extends UHCCommand {
	private final Main plugin;

	public FeedCommand(Main plugin) {
		super("feed", "[player]");
		
		this.plugin = plugin;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				throw new CommandException("Only players can feed themselves.");
			}
			
			Player player = (Player) sender;
			User user = plugin.getUser(player);
			
			player.sendMessage(Main.PREFIX + "You have been fed.");
			user.resetFood();
			return true;
		}
		
		if (args[0].equals("*")) {
			for (Player online : Bukkit.getOnlinePlayers()) {
				User user = plugin.getUser(online);
				user.resetFood();
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "All players have been fed.");
			return true;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		if (target == null) {
			throw new CommandException("'" + args[0] + "' is not online.");
		}
		
		User user = plugin.getUser(target);
		user.resetFood();

		sender.sendMessage(Main.PREFIX + "You fed §a" + target.getName() + "§7.");
		target.sendMessage(Main.PREFIX + "You have been fed by §a" + sender.getName() + "§7.");
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