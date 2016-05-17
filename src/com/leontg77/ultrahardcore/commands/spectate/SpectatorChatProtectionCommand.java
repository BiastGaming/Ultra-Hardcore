package com.leontg77.ultrahardcore.commands.spectate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.SpecManager;

/**
 * SpecChatProtection command class.
 * 
 * @author D4mnX
 */
public class SpectatorChatProtectionCommand extends UHCCommand {
    private final SpecManager manager;

    public SpectatorChatProtectionCommand(SpecManager manager) {
        super("scp", "<on|off|toggle> [player]");
        
        this.manager = manager;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
        	return false;
        }

        Player target = null;
        
        if (args.length == 1 || !sender.hasPermission(getPermission() + ".other")) {
            if (!(sender instanceof Player)) {
                throw new CommandException("Only players can protect themselves from talking in the chat.");
            }
            
            target = (Player) sender;
        } else {
        	target = Bukkit.getPlayer(args[1]);
        	
        	if (target == null) {
        		throw new CommandException("'" + args[1] + "' is not online.");
        	}
        }
        
        String name = target.getName();
        
        if (!manager.isSpectating(target)) {
            sender.sendMessage(Main.PREFIX + (target == sender ? "You are" : target.getName() + " is") + " not spectating.");
            return true;
        }

        Set<String> chatProtection = manager.getSpecChatProtected();

        boolean currentStatus = chatProtection.contains(name);
        boolean newStatus;

        if (args[0].equalsIgnoreCase("on")) {
            newStatus = true;
        } else if (args[0].equals("off")) {
            newStatus = false;
        } else if (args[0].equals("toggle")) {
            newStatus = !currentStatus;
        } else {
            return false;
        }

        if (currentStatus == newStatus) {
            sender.sendMessage(Main.PREFIX + (target == sender ? "Your" : target.getName() + "'s") + " spectator chat protection is already " + (currentStatus ? "enabled." : "disabled."));
            return true;
        }

        if (newStatus) {
            chatProtection.add(name);
            
            sender.sendMessage(Main.PREFIX + "You have enabled " + (target == sender ? "your" : target.getName() + "'s") + " spectator chat protection.");
            if (sender != target) {
            	target.sendMessage(Main.PREFIX + "Your spectator chat protection has been enabled.");
            }
        } else {
            chatProtection.remove(name);
            
            sender.sendMessage(Main.PREFIX + "You have disabled " + (target == sender ? "your" : target.getName() + "'s") + " spectator chat protection.");
            if (sender != target) {
            	target.sendMessage(Main.PREFIX + "Your spectator chat protection has been disabled.");
            }
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
    	
		if (args.length == 1) {
			toReturn.add("on");
        	toReturn.add("off");
        	toReturn.add("toggle");
        }
		
		if (args.length == 2) {
			if (!sender.hasPermission(getPermission() + ".other")) {
		        return new ArrayList<String>();
			}
			
			return allPlayers();
        }
		
		return toReturn;
    }
}