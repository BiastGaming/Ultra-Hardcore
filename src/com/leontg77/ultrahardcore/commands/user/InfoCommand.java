package com.leontg77.ultrahardcore.commands.user;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Info command class.
 * 
 * @author LeonTG77
 */
public class InfoCommand extends UHCCommand {	
	private final Main plugin;

	public InfoCommand(Main plugin) {
		super("info", "<player>");
		
		this.plugin = plugin;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			return false;
		}
        
		OfflinePlayer target = PlayerUtils.getOfflinePlayer(args[0]);
		
		if (!plugin.fileExist(target.getUniqueId())) {
			throw new CommandException("'" + args[0] + "' has never joined this server.");
		}
		
		User user = plugin.getUser(target);
		
		BanList list = Bukkit.getBanList(Type.NAME);
		BanEntry entry = list.getBanEntry(target.getName());
		
		long lastlogout = user.getFile().getLong("lastlogout", -1l);
		
		String muteMessage;
		String banMessage;
		
		if (user.isMuted()) {
			if (user.getMuteExpiration() == null) {
				muteMessage = "§aTrue§7, Reason: §6" + user.getMutedReason() + " §8(§apermanent§8)";
			} else {
				muteMessage = "§aTrue§7, Reason: §6" + user.getMutedReason() + " §8(§a" + DateUtils.formatDateDiff(user.getMuteExpiration().getTime()) + "§8)";
			}
		} else {
			muteMessage = "§cFalse";
		}
		
		if (list.isBanned(target.getName())) {
			if (entry.getExpiration() == null) {
				banMessage = "§aTrue§7, Reason: §6" + entry.getReason() + " §8(§apermanent§8)";
			} else {
				banMessage = "§aTrue§7, Reason: §6" + entry.getReason() + " §8(§a" + DateUtils.formatDateDiff(entry.getExpiration().getTime()) + "§8)";
			}
		} else {
			banMessage = "§cFalse";
		}
		
		Format date = new SimpleDateFormat("E, MMM. dd, yyyy 'at' HH:mm 'UTC'", Locale.US); 

		sender.sendMessage(Main.PREFIX + "Info about §6" + target.getName() + "§8:");
		sender.sendMessage("§8» §m--------------------------------------§8 «");
		sender.sendMessage("§8» §7Status: §6" + (target.getPlayer() == null ? "§cOffline" : "§aOnline"));
		sender.sendMessage("§8» §7UUID: §6" + user.getFile().getString("uuid"));
		sender.sendMessage("§8» §7IP: §6" + (sender.hasPermission("uhc.info.ip") ? user.getFile().getString("ip") : "§m" + user.getFile().getString("ip", "Unknown").replaceAll("[0-9]", "#")));
		sender.sendMessage("§8» §m--------------------------------------§8 «");
		sender.sendMessage("§8» §7First Joined: §6" + date.format(new Date(user.getFile().getLong("firstjoined"))));
		sender.sendMessage("§8» §7Last login: §6" + DateUtils.formatDateDiff(user.getFile().getLong("lastlogin")));
		sender.sendMessage("§8» §7Last logout: §6" + (lastlogout == -1l ? "§cHasn't logged out" : DateUtils.formatDateDiff(lastlogout)));
		sender.sendMessage("§8» §m--------------------------------------§8 «");
		sender.sendMessage("§8» §7Possible alt accounts:");
        
		if (user.getAlts().isEmpty()) {
			sender.sendMessage("§8» §6This player has no known alts.");
		}
		
		for (String alt : user.getAlts()) {
			sender.sendMessage("§8» §c" + alt);
		}
		
		sender.sendMessage("§8» §m--------------------------------------§8 «");
		sender.sendMessage("§8» §7Banned: §6" + banMessage);
		sender.sendMessage("§8» §7Muted: §6" + muteMessage);
		sender.sendMessage("§8» §m--------------------------------------§8 «");
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