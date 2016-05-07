package com.leontg77.ultrahardcore.commands.basic;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * StaffChat command class
 * 
 * @author LeonTG77
 */
public class StaffChatCommand extends UHCCommand {
	private static final String PREFIX = "§4Staff Chat §8» §c";

	public StaffChatCommand() {
		super("ac", "<message>");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
        	return false;
        } 
        
    	String message = Joiner.on(' ').join(args);
		PlayerUtils.broadcast(PREFIX + sender.getName() + "§8: §f" + message, "uhc.staff");
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return allPlayers();
	}
}