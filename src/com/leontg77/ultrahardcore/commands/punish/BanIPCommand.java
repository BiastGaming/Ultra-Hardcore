package com.leontg77.ultrahardcore.commands.punish;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;
import com.leontg77.ultrahardcore.utils.PunishUtils;

/**
 * BanIP command class
 * 
 * @author LeonTG77
 */
public class BanIPCommand extends UHCCommand {	
	private final BoardManager board;

	public BanIPCommand(BoardManager board) {
		super("banip", "<ip|name> <reason>");
		
		this.board = board;
	}
	
	private static final Type BANLIST_TYPE = Type.NAME;

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (args.length < 3) {
			return false;
		}
		
		BanList list = Bukkit.getBanList(BANLIST_TYPE);
		Player target = Bukkit.getPlayer(args[0]);
		
		String IP = args[0];
		
		if (target != null) {
			IP = target.getAddress().getAddress().getHostAddress();
		}

		String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));

		PlayerUtils.broadcast(Main.PREFIX + "An IP has been banned for §a" + message);
		list.addBan(IP, message, null, sender.getName());
		
    	for (Player online : Bukkit.getOnlinePlayers()) {
    		if (!online.getAddress().getAddress().getHostAddress().equals(IP)) {
    			continue;
    		}
    		
			board.resetScore(online.getName());
	    	
			PlayerDeathEvent event = new PlayerDeathEvent(online, new ArrayList<ItemStack>(), 0, null);
			Bukkit.getPluginManager().callEvent(event);
			
			online.kickPlayer(String.format(PunishUtils.getIPBanMessageFormat(), message, sender.getName()));
    	}
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return allPlayers();
	}
}