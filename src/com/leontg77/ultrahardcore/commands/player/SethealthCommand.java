package com.leontg77.ultrahardcore.commands.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * SetHealth command class.
 * 
 * @author LeonTG77
 */
public class SethealthCommand extends UHCCommand {

	public SethealthCommand() {
		super("sethealth", "<health> [player|*]");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			return false;
		}
		
		double health = parseDouble(args[0], "health");
		
		// health can't be lower than 0
		if (health < 0) {
			health = 0;
		}
		
		if (args.length == 1) {
			if (!(sender instanceof Player)) {
				throw new CommandException("Only players can change their health.");
			}
			
			Player player = (Player) sender;

			// health can't be higher than their maximum.
			if (health > player.getMaxHealth()) {
				health = player.getMaxHealth();
			}
			
			player.sendMessage(Main.PREFIX + "You set your health to §6" + NumberUtils.makePercent(health).substring(2) + "%");
			player.setHealth(health);
			return true;
		}
		
		if (args[1].equals("*")) {
			for (Player online : Bukkit.getOnlinePlayers()) {
				double hp = health;

				// health can't be higher than their maximum.
				if (hp > online.getMaxHealth()) {
					hp = online.getMaxHealth();
				}
				
				online.setHealth(hp);
			}
			
			PlayerUtils.broadcast(Main.PREFIX + "All players health was set to §6" + NumberUtils.makePercent(health).substring(2) + "%");
			return true;
		}
		
		Player target = Bukkit.getPlayer(args[1]);
		
		if (target == null) {
			throw new CommandException("'" + args[1] + "' is not online.");
		}

		// health can't be higher than their maximum.
		if (health > target.getMaxHealth()) {
			health = target.getMaxHealth();
		}

		sender.sendMessage(Main.PREFIX + "You set §a" + target.getName() + "'s §7health to §6" + NumberUtils.makePercent(health).substring(2) + "%");
		target.sendMessage(Main.PREFIX + "Your health was set to §6" + NumberUtils.makePercent(health).substring(2) + "%");
		
		target.setHealth(health);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 2) {
    		return allPlayers();
		}

		return new ArrayList<String>();
	}
}