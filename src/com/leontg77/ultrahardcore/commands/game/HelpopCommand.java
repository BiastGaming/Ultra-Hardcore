package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.SpecManager;

/**
 * Helpop command class.
 * 
 * @author LeonTG77
 */
public class HelpopCommand extends UHCCommand {
	private static final String PREFIX = "§4Help§8-§4Op §8§ §7";
	
	private final SpecManager spec;
	private final Main plugin;
	
	public HelpopCommand(Main plugin, SpecManager spec) {
		super("helpop", "<message>");
		
		this.plugin = plugin;
		this.spec = spec;
	}
	
	private final List<CommandSender> cooldown = new ArrayList<CommandSender>();

	@Override
	public boolean execute(final CommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
           	return false;
		}
		
		if (cooldown.contains(sender)) {
			throw new CommandException(PREFIX + "Please do not spam helpops.");
		}
		
		String msg = Joiner.on(' ').join(Arrays.copyOfRange(args, 0, args.length));

		for (Player online : Bukkit.getOnlinePlayers()) {
			if (!online.hasPermission("uhc.staff") && !spec.isSpectating(online)) {
				continue;
			}
			
			if (online == sender) {
				continue;
			}
			
			online.sendMessage(PREFIX + sender.getName() + "§7: §6" + msg);
			online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
		}
		
		Bukkit.getLogger().info(PREFIX + sender.getName() + "§7: §6" + msg);
		cooldown.add(sender);

		sender.sendMessage(PREFIX + sender.getName() + "§7: §6" + msg);
		sender.sendMessage(PREFIX + "§7Helpop has been sent, please don't spam this :)");
		sender.sendMessage(PREFIX + "§7Remember to check §a/uhc §7for other game info.");
		
		new BukkitRunnable() {
			public void run() {
				cooldown.remove(sender);
			}
		}.runTaskLater(plugin, 100);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return allVisiblePlayers(sender);
	}
}