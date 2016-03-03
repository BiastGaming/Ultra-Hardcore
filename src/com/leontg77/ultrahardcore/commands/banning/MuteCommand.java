package com.leontg77.ultrahardcore.commands.banning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Mute command class
 * 
 * @author LeonTG77
 */
public class MuteCommand extends UHCCommand {

	public MuteCommand() {
		super("mute", "<player> [time] [reason]");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (args.length == 0) {
			return false;
		}

		OfflinePlayer target = PlayerUtils.getOfflinePlayer(args[0]);
		Player player = target.getPlayer();
		
		if (!User.fileExist(target.getUniqueId())) {
			throw new CommandException("'" + args[0] + "' has never joined this server.");
		}
    	
    	if (player != null && player.hasPermission("uhc.staff") && !sender.hasPermission("uhc.mute.bypass")) {
    		throw new CommandException("You cannot mute this player.");
    	}
		
		User user = User.get(target);

		if (user.isMuted()) {
	    	if (!sender.hasPermission("uhc.unmute")) {
	    		sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
	    		return true;
	    	}
	    	
			PlayerUtils.broadcast(Main.PREFIX + "�6" + target.getName() + " �7has been unmuted.");
			user.unmute();
			
			if (player != null) {
				player.sendMessage(Main.PREFIX + "You are no longer muted.");
			}
			return true;
		} 
		
		if (args.length < 3) {
			return false;
		}
    	
    	String reason = Joiner.on(' ').join(Arrays.copyOfRange(args, 2, args.length));
		long time = DateUtils.parseDateDiff(args[1], true);
		
		PlayerUtils.broadcast(Main.PREFIX + "�6" + target.getName() + " �7has been muted for �a" + reason + "�7. �8(�a" + (time <= 0 ? "permanent" : DateUtils.formatDateDiff(time)) + "�8)");
		user.mute(reason, (time <= 0 ? null : new Date(time)));
		
		if (player != null) {
			player.sendMessage(Main.PREFIX + "You have been muted for �a" + reason + "�7.");
		}
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		
		if (args.length == 1) {
			return null;
		}
		
		if (args.length == 2 && !args[1].isEmpty()) {
			toReturn.add(args[1] + "s");
			toReturn.add(args[1] + "m");
			toReturn.add(args[1] + "h");
			toReturn.add(args[1] + "d");
			toReturn.add(args[1] + "w");
			toReturn.add(args[1] + "mo");
			toReturn.add(args[1] + "y");
		}
		
		if (args.length == 3) {
			toReturn.add("Spoiling");
			toReturn.add("Spamming");
			toReturn.add("Death Wishes");
			toReturn.add("Death Treats");
		}
		
		return toReturn;
	}
}