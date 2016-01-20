package com.leontg77.ultrahardcore.commands.banning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Ban command class
 * 
 * @author LeonTG77
 */
public class BanCommand extends UHCCommand {	

	public BanCommand() {
		super("ban", "<player> <reason>");
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
		if (args.length < 2) {
			return false;
		}
    	
    	final Player target = Bukkit.getPlayer(args[0]);
    	
    	final BoardManager board = BoardManager.getInstance();
    	final BanList list = Bukkit.getBanList(Type.NAME);
				
		final String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));

    	if (target == null) {
			PlayerUtils.broadcast(Main.PREFIX + "�6" + args[0] + " �7has been banned for �a" + message + " �8(�apermanent�8)");
			
    		list.addBan(args[0], message, null, sender.getName());
			board.resetScore(args[0]);
            return true;
		}
    	
    	if (target.hasPermission("uhc.staff") && !sender.hasPermission("uhc.ban.bypass")) {
	    	throw new CommandException("You cannot ban this player.");
    	}

    	new BukkitRunnable() {
        	int left = 3;
        	
    		public void run() {
    			if (left > 0) {
    				PlayerUtils.broadcast(Main.PREFIX + "Incoming ban in �6" + left + "�7.");
    				left--;
    				
    		    	for (Player online : PlayerUtils.getPlayers()) {
    		    		online.playSound(online.getLocation(), Sound.ANVIL_LAND, 1, 1);
    		    	}
    		    	return;
    			}
    				
    			PlayerUtils.broadcast(Main.PREFIX + "�6" + target.getName() + " �7has been banned for �a" + message + " �8(�apermanent�8)");
				
		    	for (Player online : PlayerUtils.getPlayers()) {
		    		online.playSound(online.getLocation(), Sound.EXPLODE, 1, 1);
		    	}
		    	
		    	final BanEntry ban = list.addBan(target.getName(), message, null, sender.getName());
		    	target.setWhitelisted(false);
		    	
				board.resetScore(args[0]);
		    	board.resetScore(target.getName());
		    	
		    	final PlayerDeathEvent event = new PlayerDeathEvent(target, new ArrayList<ItemStack>(), 0, null);
		    	Bukkit.getPluginManager().callEvent(event);
				
		    	target.kickPlayer(
		    	"�8� �7You have been �4banned �7from �6Arctic UHC �8�" +
		    	"\n" + 
		    	"\n�cReason �8� �7" + ban.getReason() +
		    	"\n�cBanned by �8� �7" + ban.getSource() +
	 			"\n" +
		   		"\n�8� �7If you would like to appeal, DM our twitter �a@ArcticUHC �8�"
		    	);
		    	
		    	cancel();
    		}
    	}.runTaskTimer(Main.plugin, 0, 20);
		return true;
	}

	@Override
	public List<String> tabComplete(final CommandSender sender, final String[] args) {
		List<String> toReturn = new ArrayList<String>();
		
		if (args.length == 1) {
			for (Player online : PlayerUtils.getPlayers()) {
				toReturn.add(online.getName());
			}
		}
		
		return toReturn;
	}
}