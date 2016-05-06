package com.leontg77.ultrahardcore.commands.punish;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Rank;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;
import com.leontg77.ultrahardcore.utils.PunishUtils;
import com.leontg77.ultrahardcore.utils.PunishUtils.PunishmentType;

/**
 * Ban command class
 * 
 * @author LeonTG77
 */
public class BanCommand extends UHCCommand {	
	private final BoardManager board;
	private final Main plugin;

	public BanCommand(Main plugin, BoardManager board) {
		super("ban", "<player> <reason>");

		this.plugin = plugin;
		this.board = board;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (args.length < 2) {
			return false;
		}
		
		String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));
    	BanList banList = Bukkit.getBanList(Type.NAME);
    	
		if (banList.isBanned(args[0])) {
			throw new CommandException("'" + args[0] + "' is already banned.");
		}
		
    	User user = plugin.getUser(PlayerUtils.getOfflinePlayer(args[0]));
    	
    	if (user.getRank().getLevel() >= Rank.STAFF.getLevel()) {
	    	throw new CommandException("'" + args[0] + "' is a staff member and can't be banned.");
    	}
    	
    	Player target = Bukkit.getPlayer(args[0]);

    	if (target == null) {
	    	PunishUtils.savePunishment(user, PunishmentType.BAN, message, null);
	    	
			PlayerUtils.broadcast(Main.PREFIX + "§6" + args[0] + " §7has been banned for §a" + message + "§7.");
			
    		banList.addBan(args[0], message, null, sender.getName());
			board.resetScore(args[0]);
            return true;
		}

    	PlayerUtils.broadcast(Main.PREFIX + "§6" + target.getName() + " §7has been banned for §a" + message + "§7.");
    	
    	banList.addBan(target.getName(), message, null, sender.getName());
    	
    	PlayerDeathEvent deathEvent = new PlayerDeathEvent(target, new ArrayList<ItemStack>(), 0, null);
    	Bukkit.getPluginManager().callEvent(deathEvent);
		
    	new BukkitRunnable() {
			public void run() {
		    	Block block = target.getLocation().getBlock();
		    	
				block.setType(Material.AIR);
				block = block.getRelative(BlockFace.UP);
				block.setType(Material.AIR);
			}
		}.runTaskLater(plugin, 2);
		
    	target.kickPlayer(String.format(PunishUtils.getBanMessageFormat(), message, sender.getName()));
    	PunishUtils.savePunishment(plugin.getUser(target), PunishmentType.BAN, message, null);
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> toReturn = new ArrayList<String>();
		
		if (args.length == 1) {
			return allPlayers();
		}
		
		if (args.length > 1) {
			toReturn.add("Hacked Client");
			toReturn.add("Xray");
		}
		
		return toReturn;
	}
}