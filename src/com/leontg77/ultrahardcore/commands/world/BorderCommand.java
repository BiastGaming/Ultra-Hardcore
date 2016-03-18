package com.leontg77.ultrahardcore.commands.world;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Border command class.
 * 
 * @author LeonTG77
 */
public class BorderCommand extends UHCCommand {

	public BorderCommand() {
		super("border", "<world> <size>");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		World world;

		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				return false;
			}

			Player player = (Player) sender;
			world = player.getWorld();
		} else {
			world = Bukkit.getWorld(args[0]);
			
			if (world == null) {
				throw new CommandException("The world '" + args[0] + "' does not exist.");
			}
		}
		
		if (args.length < 2) {
			WorldBorder border = world.getWorldBorder();
			
			int diameter = (int) border.getSize();
			int radius = diameter / 2;
			
			sender.sendMessage(Main.PREFIX + "The border is currently: �a" + diameter + "x" + diameter + " �8(�a+" + radius + " -" + radius + "�8)");
			return true;
		}
		
		if (!sender.hasPermission(getPermission() + ".set")) {
			sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
			return true;
		}

		WorldBorder border = world.getWorldBorder();
		
		int diameter = parseInt(args[1], "radius");
		border.setSize(diameter);
		
		PlayerUtils.broadcast(Main.PREFIX + "Borders in world '�6" + world.getName() + "�7' has been setup with size �a" + diameter + "x" + diameter + "�7.");
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
    	
		if (args.length == 1) {
        	for (World world : Bukkit.getWorlds()) {
        		toReturn.add(world.getName());
        	}
        }
		
		if (args.length == 2) {
        	toReturn.add("3000");
        	toReturn.add("2000");
        }
		
		return toReturn;
	}
}