package com.leontg77.ultrahardcore.commands.basic;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Skull command class.
 * 
 * @author LeonTG77
 */
public class SkullCommand extends UHCCommand {
	
	public SkullCommand() {
		super("skull", "<name> [player]");
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
		if (args.length == 0) {
			return false;
		}
		
		Player target;
		
		if (args.length == 1) {
			if (!(sender instanceof Player)) {
				throw new CommandException("Only players can spawn player heads.");
			}
			
			target = (Player) sender;
		} else {
			target = Bukkit.getPlayer(args[1]);
			
			if (target == null) {
				throw new CommandException("'" + args[1] + "' is not online.");
			}
		}
		
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwner(args[0]);
		item.setItemMeta(meta);
		
		PlayerUtils.giveItem(target, item);
		target.sendMessage(Main.PREFIX + "You've been given the head of '§a" + args[0] + "§7'.");
		
		if (target != sender) {
			sender.sendMessage(Main.PREFIX + "You gave " + target.getName() + " the head of '§a" + args[0] + "§7'.");
		}
		return true;
	}

	@Override
	public List<String> tabComplete(final CommandSender sender, final String[] args) {
		if (args.length < 3) {
			return allPlayers();
		}
		
		return new ArrayList<String>();
	}
}