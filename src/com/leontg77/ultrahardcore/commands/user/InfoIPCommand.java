package com.leontg77.ultrahardcore.commands.user;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.FileUtils;

/**
 * InfoIP command class.
 * 
 * @author LeonTG77
 */
public class InfoIPCommand extends UHCCommand {	

	public InfoIPCommand() {
		super("infoip", "<ip>");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			return false;
		}
		
		String adress = args[0];
		sender.sendMessage(Main.PREFIX + "Players with the IP: §a" + adress);
        
		for (FileConfiguration file : FileUtils.getUserFiles()) {
			if (!file.getStringList("ips").contains(adress)) {
				continue;
			}
			
			sender.sendMessage("§8» §7" + file.getString("username", "Unknown"));
		}
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return new ArrayList<String>();
	}
}