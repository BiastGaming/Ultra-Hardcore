package com.leontg77.ultrahardcore.commands.msg;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Rank;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.scenario.scenarios.Moles;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.NameUtils;

/**
 * Msg command class
 * 
 * @author LeonTG77
 */
public class MsgCommand extends UHCCommand {
	private final ScenarioManager scen;
	private final Main plugin;
	
	public MsgCommand(Main plugin, ScenarioManager scen) {
		super("msg", "<player> <message>");
		
		this.plugin = plugin;
		this.scen = scen;
	}
	
	public static Map<String, String> msg = new HashMap<String, String>();

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can message other players.");
			return true;
		}
		
		Player player = (Player) sender;
		User user = plugin.getUser(player);
		
    	if (args.length < 2) {
        	return false;
        }
		
    	if (user.isMuted() && !scen.getScenario(Moles.class).isEnabled()) {
    		player.sendMessage(Main.PREFIX + "You have been muted for: §a" + user.getMutedReason());
			
			if (user.getMuteExpiration() == null) {
				player.sendMessage(Main.PREFIX + "Your mute is permanent.");
			} else {
				player.sendMessage(Main.PREFIX + "Your mute expires in: §a" + DateUtils.formatDateDiff(user.getMuteExpiration().getTime()));
			}
			return true;
    	}
    	
    	Player target = Bukkit.getPlayer(args[0]);
               
        if (target == null) {
        	throw new CommandException("'" + args[0] + "' is not online.");
        }
        
		User tUser = plugin.getUser(target);
		
    	if (tUser.isMuted()) {
    		player.sendMessage(ChatColor.RED + "'" + target.getName() + "' is muted and won't be able to respond.");
    	}
        
        String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));
               
        player.sendMessage("§8[§a§ome §8-> §a§o" + name(target) + "§8] §7" + message);
		msg.put(player.getName(), target.getName());
		
		if (tUser.isIgnoring(player)) {
			return true;
		}
		
    	target.sendMessage("§8[§a§o" + name(player) + " §8-> §a§ome§8] §7" + message);
    	msg.put(target.getName(), player.getName());
		return true;
    }

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		return allVisiblePlayers(sender);
	}
	
	private String name(Player player) {
		User user = plugin.getUser(player);
		
		if (user.getRank().getLevel() >= Rank.STAFF.getLevel()) {
			return "§8(" + user.getRankColor() + NameUtils.capitalizeString(user.getRank().name(), true) + "§8) §f§o" + player.getName();
		}
		
		return player.getName();
	}
}