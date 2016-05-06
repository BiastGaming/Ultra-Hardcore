package com.leontg77.ultrahardcore.commands.punish;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;
import com.leontg77.ultrahardcore.utils.PunishUtils;
import com.leontg77.ultrahardcore.utils.PunishUtils.PunishmentType;

/**
 * Unban command class
 * 
 * @author LeonTG77
 */
public class UnbanCommand extends UHCCommand {	
	private static final Type BANLIST_TYPE = Type.NAME;
	
	private final Main plugin;

	public UnbanCommand(Main plugin) {
		super("unban", "<player>");
		
		this.plugin = plugin;
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
		if (args.length == 0) {
			return false;
		}

		BanList list = Bukkit.getBanList(BANLIST_TYPE);
		String target = args[0];
    	
		if (!list.isBanned(target)) {
			throw new CommandException("'" + target + "' is not banned.");
		}

		User user = plugin.getUser(PlayerUtils.getOfflinePlayer(target));
		BanEntry ban = list.getBanEntry(target);

		if (ban.getExpiration() == null) {
	    	PunishUtils.setPunishmentExpireToNow(user, PunishmentType.BAN, -1l);
		} else {
	    	PunishUtils.setPunishmentExpireToNow(user, PunishmentType.TEMPBAN, ban.getExpiration().getTime());
		}

		
		PlayerUtils.broadcast(Main.PREFIX + "§6" + target + " §7has been unbanned.");
		list.pardon(target);
		return true;
	}
	
	@Override
	public List<String> tabComplete(final CommandSender sender, final String[] args) {
		List<String> toReturn = new ArrayList<String>();
    	
		if (args.length == 1) {
        	BanList list = Bukkit.getBanList(BANLIST_TYPE);
        	
    		for (BanEntry entry : list.getBanEntries()) {
    			String ip = entry.getTarget();
    			
    			toReturn.add(ip);
    		}
        }
		
		return toReturn;
	}
}