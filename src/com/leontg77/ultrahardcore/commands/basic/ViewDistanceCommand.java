package com.leontg77.ultrahardcore.commands.basic;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * View distance command class.
 * 
 * @author LeonTG77
 */
public class ViewDistanceCommand extends UHCCommand implements Listener {
    private int viewDistance = Bukkit.getViewDistance();

	public ViewDistanceCommand(Main plugin) {
		super("viewdistance", "<distance>");
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (args.equals(args)) {
			throw new CommandException("This feature has been disabled until future notice.");
		}
		
		if (args.length == 0 || !sender.hasPermission(getPermission() + ".set")) {
			throw new CommandException(Main.PREFIX + "Current view distance: §a" + viewDistance + " §7chunks.");
		}
		
		int distance = parseInt(args[0], "distance");
		
		if (distance < 2 || distance > 32) {
			throw new CommandException("'" + args[0] + "' is not a valid view distance, must be between 2 and 32.");
		}
		
		viewDistance = distance;

		PlayerUtils.broadcast(Main.PREFIX + "Setting server view distance to §a" + distance + " §7chunks, this might lag.");
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
		}
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			online.spigot().setViewDistance(distance);
		}
		
		PlayerUtils.broadcast(Main.PREFIX + "Server view distance successfully updated to §a" + distance + " §7chunks.");
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			online.playSound(online.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
		}
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return allPlayers();
	}
	
	@EventHandler
	public void on(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		player.spigot().setViewDistance(viewDistance);
	}
}