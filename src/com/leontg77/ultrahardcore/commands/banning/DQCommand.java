package com.leontg77.ultrahardcore.commands.banning;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
 * DQ command class.
 * 
 * @author LeonTG77
 */
public class DQCommand extends UHCCommand {	
	private final BoardManager board;
	private final Main plugin;

	/**
	 * DQ command class constructor.
	 * 
	 * @param board The board manager class.
	 */
	public DQCommand(Main plugin, BoardManager board) {
		super("dq", "<player> <reason>");

		this.plugin = plugin;
		this.board = board;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (args.length < 2) {
			return false;
		}
		
		if (!Bukkit.hasWhitelist()) {
			throw new CommandException("You cannot DQ when the whitelist is off.");
		}
		
		Player target = Bukkit.getPlayer(args[0]);

    	if (target == null) {
	    	throw new CommandException("'" + args[0] + "' is not online.");
		}
    	
    	User user = plugin.getUser(target);

    	if (user.getRank().getLevel() >= Rank.STAFF.getLevel()) {
	    	throw new CommandException("'" + args[0] + "' is a staff member and can't be dq'ed.");
    	}
    	
    	String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));
    	
    	PlayerUtils.broadcast(Main.PREFIX + "§6" + target.getName() + " §7has been disqualified for §a" + message + "§7.");

    	board.resetScore(target.getName());
		board.resetScore(args[0]);
    	
    	target.setWhitelisted(false);
    	target.setHealth(0);

    	target.kickPlayer(String.format(PunishUtils.getDQReasonFormat(), message, sender.getName()));
    	PunishUtils.savePunishment(user, PunishmentType.DISQUALIFY, message, new Date());
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return allPlayers();
	}
}