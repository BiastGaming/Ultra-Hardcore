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
import org.bukkit.entity.Player;

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
				muteMessage = "�aTrue�7, Reason: �6" + user.getMutedReason() + " �8(�apermanent�8)";
			} else {
				muteMessage = "�aTrue�7, Reason: �6" + user.getMutedReason() + " �8(�a" + DateUtils.formatDateDiff(user.getMuteExpiration().getTime()) + "�8)";
			}
		} else {
			muteMessage = "�cFalse";
		}
		
		if (list.isBanned(target.getName())) {
			if (entry.getExpiration() == null) {
				banMessage = "�aTrue�7, Reason: �6" + entry.getReason() + " �8(�apermanent�8)";
			} else {
				banMessage = "�aTrue�7, Reason: �6" + entry.getReason() + " �8(�a" + DateUtils.formatDateDiff(entry.getExpiration().getTime()) + "�8)";
			}
		} else {
			banMessage = "�cFalse";
		}
		
		StringBuilder ips = new StringBuilder();
		
		for (String IP : user.getFile().getStringList("ips")) {
			if (ips.length() > 0) {
				ips.append("�8, �7");
			}
			
			Player player = target.getPlayer();
			String currentIP = "";
			
			if (player != null) {
				currentIP = player.getAddress().getAddress().getHostAddress();
			}
			
			if (currentIP.equals(IP)) {
				ips.append(sender.hasPermission("uhc.info.ip") ? "�a" + IP : "�a�m" + IP.replaceAll("[0-9]", "#"));
			} else {
				ips.append(sender.hasPermission("uhc.info.ip") ? "�6" + IP : "�6�m" + IP.replaceAll("[0-9]", "#"));
			}
		}
		
		Format date = new SimpleDateFormat("E, MMM. dd, yyyy 'at' HH:mm 'UTC'", Locale.US); 

		sender.sendMessage(Main.PREFIX + "Info about �6" + target.getName() + "�8: (�7Currently: " + (target.getPlayer() == null ? "�cOffline" : "�aOnline") + "�8)");
		sender.sendMessage("�8� �m--------------------------------------�8 �");
		sender.sendMessage("�8� �7UUID: �a" + user.getFile().getString("uuid"));
		sender.sendMessage("�8� �7First Joined: �6" + date.format(new Date(user.getFile().getLong("firstjoined"))));
		sender.sendMessage("�8� �7Last login: �6" + DateUtils.formatDateDiff(user.getFile().getLong("lastlogin")));
		sender.sendMessage("�8� �7Last logout: �6" + (lastlogout == -1l ? "�cHasn't logged out" : DateUtils.formatDateDiff(lastlogout)));
		sender.sendMessage("�8� �m--------------------------------------�8 �");
		sender.sendMessage("�8� �7IPS: �8(�aGreen �7= Current IP�8)");
		sender.sendMessage("�8� �7" + ips.toString().trim());
		sender.sendMessage("�8� �m--------------------------------------�8 �");
		if (user.getAlts().isEmpty()) {
			sender.sendMessage("�8� �7Possible Alts: �cNone");
		} else {
			String alts = user.getAlts().toString();
			sender.sendMessage("�8� �7Possible Alts: " + alts.substring(1, alts.length() - 1));
		}
		sender.sendMessage("�8� �m--------------------------------------�8 �");
		sender.sendMessage("�8� �7Banned: �6" + banMessage);
		sender.sendMessage("�8� �7Muted: �6" + muteMessage);
		sender.sendMessage("�8� �m--------------------------------------�8 �");
		if (user.getFile().getStringList("punishmentlist").isEmpty()) {
			sender.sendMessage("�8� �7Punishments: �cNone");
		} else {
			sender.sendMessage("�8� �7Punishments:");
			for (String punish : user.getFile().getStringList("punishmentlist")) {
				sender.sendMessage("�8� �7" + punish);
			}
		}
		sender.sendMessage("�8� �m--------------------------------------�8 �");
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