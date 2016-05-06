package com.leontg77.ultrahardcore.commands.spectate;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.SpecManager;

/**
 * SpecChat command class
 * 
 * @author LeonTG77
 */
public class SpecChatCommand extends UHCCommand {
	private final SpecManager spec;

	public SpecChatCommand(SpecManager spec) {
		super("specchat", "<message>");
		
		this.spec = spec;
	}
	
	private static final String PREFIX = "§5Spec Chat §8§ §d";

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (sender instanceof Player && !spec.isSpectating((Player) sender)) {
			throw new CommandException("You can only do this while spectating.");
		}
		
		if (args.length == 0) {
        	return false;
        } 
        
        String message = Joiner.on(' ').join(args);
        
        for (Player online : Bukkit.getOnlinePlayers()) {
        	if (!spec.isSpectating(online)) {
        		continue;
        	}
        	
        	online.sendMessage(PREFIX + sender.getName() + "§8: §f" + message);
        }

		message = message.replaceAll("§l", "");
		message = message.replaceAll("§o", "");
		message = message.replaceAll("§r", "§f");
		message = message.replaceAll("§m", "");
		message = message.replaceAll("§n", "");
		
		Bukkit.getLogger().info(PREFIX + sender.getName() + "§8: §f" + message);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return allVisiblePlayers(sender);
	}
}