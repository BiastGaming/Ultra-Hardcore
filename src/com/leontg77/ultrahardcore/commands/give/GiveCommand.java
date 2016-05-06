package com.leontg77.ultrahardcore.commands.give;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Give command class.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class GiveCommand extends UHCCommand {	

	public GiveCommand() {
		super("give", "<player> <item> [amount] [durability]");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (args.length < 2) {
			return false;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		if (target == null) {
			throw new CommandException("'" + args[0] + "' is not online.");
		}
		
		Material material = null;
		int amount = 1;
		short durability = 0;
		
		try {
			material = Material.getMaterial(parseInt(args[1]));
		} catch (Exception e) {
			for (Material types : Material.values()) {
				if (types.name().startsWith(args[1].toUpperCase())) {
					material = types;
					break;
				}
			}
		}
		
		if (material == null) {
			throw new CommandException("'" + args[1] + "' is not a valid item.");
		}
		
		if (args.length > 2) {
			amount = parseInt(args[2], "amount");
		}
		
		if (args.length > 3) {
			durability = (short) parseInt(args[3], "durability");
		}

		ItemStack item = new ItemStack(material, amount, durability);
		PlayerUtils.giveItem(target, item);
		
		String itemName = item.getType().name().toLowerCase().replaceAll("_", " ") + (amount > 1 && !item.getType().name().endsWith("S") ? "s" : "");
		
		sender.sendMessage(Main.PREFIX + "You gave " + target.getName() + " §a" + amount + " " + itemName + "§7.");
		target.sendMessage(Main.PREFIX + "You recieved §a" + amount + " " + itemName + "§7.");
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		
		if (args.length == 1) {
			return allPlayers();
		}
		
		if (args.length == 2) {
			for (Material type : Material.values()) {
				toReturn.add(type.name().toLowerCase());
			}
		}
		
		return toReturn;
	}
}