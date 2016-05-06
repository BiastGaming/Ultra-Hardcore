package com.leontg77.ultrahardcore.commands.basic;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Broadcast command class.
 * 
 * @author LeonTG77
 */
public class BroadcastCommand extends UHCCommand {

	public BroadcastCommand() {
		super("broadcast", "<message>");
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) {
		if (args.length == 0) {
			return false;
		}
		
		String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 0, args.length));
		PlayerUtils.broadcast(Main.ALERT_PREFIX + "§a" + ChatColor.translateAlternateColorCodes('&', message));
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
		}
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return allPlayers();
	}
}