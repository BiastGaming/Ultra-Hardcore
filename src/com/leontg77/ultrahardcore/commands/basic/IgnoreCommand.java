package com.leontg77.ultrahardcore.commands.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.FileUtils;

/**
 * Ignore command class.
 * 
 * @author LeonTG77
 */
public class IgnoreCommand extends UHCCommand {
	private final Main plugin;

	public IgnoreCommand(Main plugin) {
		super("ignore", "<player>");
		
		this.plugin = plugin;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can ignore other players.");
			return true;
		}
		
		if (sender.hasPermission("uhc.cantignore")) {
			throw new CommandException("You can't ignore people as a staff member.");
		}
		
		Player player = (Player) sender;
		User user = plugin.getUser(player);
		
		if (args.length == 0) {
			StringBuilder list = new StringBuilder();
			int i = 1;
			
			final List<String> ignoreList = user.getFile().getStringList("ignoreList");
			
			if (ignoreList.isEmpty()) {
				player.sendMessage(Main.PREFIX + "You are not ignoring anyone.");
				return true;
			}
			
			final Set<FileConfiguration> userFiles = FileUtils.getUserFiles();
			
			for (String ignored : ignoreList) {
				if (i == ignoreList.size()) {
					list.append(" §8and §7");
				} else {
					list.append("§8, §7");
				}
				
				for (FileConfiguration file : userFiles) {
					if (file.getString("uuid", "none").equals(ignored)) {
						list.append("§7" + file.getString("name", "none"));
						break;
					}
				}
			}
			
			player.sendMessage(Main.PREFIX + "Currently ignoring:");
			player.sendMessage(Main.ARROW + list.toString());
			return true;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		if (target == null) {
			throw new CommandException("'" + args[0] + "' is not online.");
		}
		
		if (target == player) {
			throw new CommandException("You can't ignore yourself.");
		}
		
		if (target.hasPermission("uhc.cantignore")) {
			throw new CommandException("You can't ignore that player.");
		}
		
		if (user.isIgnoring(target)) {
			player.sendMessage(Main.PREFIX + "You are no longer ignoring §a" + target.getName() + "§7.");
			user.unIgnore(target);
			return true;
		} 
		
		player.sendMessage(Main.PREFIX + "You are now ignoring messages from §a" + target.getName() + "§7.");
		user.ignore(target);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
			return allVisiblePlayers(sender);
		}
		
		return new ArrayList<String>();
	}
}