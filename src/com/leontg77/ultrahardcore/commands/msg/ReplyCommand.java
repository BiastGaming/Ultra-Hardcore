package com.leontg77.ultrahardcore.commands.msg;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.DateUtils;

/**
 * Reply command class
 * 
 * @author LeonTG77
 */
public class ReplyCommand extends UHCCommand {

	public ReplyCommand() {
		super("reply", "<message>");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can ignore other players.");
			return true;
		}
		
		Player player = (Player) sender;
		
    	if (args.length == 0) {
        	return false;
        }
		
    	if (MsgCommand.isMuted(player)) {
    		User user = User.get(player);
    		
			sender.sendMessage(Main.PREFIX + "You have been muted for: �a" + user.getMutedReason());
			
			if (user.getMuteExpiration() == null) {
				sender.sendMessage(Main.PREFIX + "Your mute is permanent.");
			} else {
				sender.sendMessage(Main.PREFIX + "Your mute expires in: �a" + DateUtils.formatDateDiff(user.getMuteExpiration().getTime()));
			}
			return true;
    	}

    	if (!MsgCommand.msg.containsKey(player.getName())) {
    		throw new CommandException("You have no one to reply to.");
    	}
    	
    	Player target = Bukkit.getPlayer(MsgCommand.msg.get(player.getName()));
               
        if (target == null) {
        	throw new CommandException("You have no one to reply to.");
        }
        
		User user = User.get(target);
		
		if (user.isIgnoring(player)) {
			throw new CommandException("'" + player.getName() + "' have you ignored.");
		}
		
    	if (MsgCommand.isMuted(target)) {
    		player.sendMessage(ChatColor.RED + "'" + target.getName() + "' is muted and won't be able to respond.");
    	}
    	
        String msg = Joiner.on(' ').join(Arrays.copyOfRange(args, 0, args.length));

        sender.sendMessage("�8[�a�ome �8-> �a�o" + target.getName() + "�8] �7" + msg);
    	target.sendMessage("�8[�a�o" + sender.getName() + " �8-> �a�ome�8] �7" + msg);

    	MsgCommand.msg.put(target.getName(), player.getName());
    	MsgCommand.msg.put(player.getName(), target.getName());
		return true;
    }

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return null; // returning null actually returns a list of all online player names.
	}
}