package com.leontg77.ultrahardcore.commands.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Rank;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * List command class.
 * 
 * @author LeonTG77
 */
public class ListCommand extends UHCCommand {

	public ListCommand() {
		super("list", "");
	}

	@Override
	public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
		if (PlayerUtils.getPlayers().isEmpty()) {
	    	throw new CommandException("There are no players online.");
		}

		final List<Player> players = new ArrayList<Player>();
    	int playersOnline = 0;
    		
    	for (Player online : PlayerUtils.getPlayers()) {
    		if (sender instanceof Player && !((Player) sender).canSee(online)) {
    			continue;
    		}
    		
    		players.add(online);
			playersOnline++;
		}

		final Game game = Game.getInstance();
		
		final String ownerList = getListOf(players, Rank.OWNER);
		final String staffList = getListOf(players, Rank.HOST, Rank.TRIAL, Rank.STAFF);
		
		final String donatorList = getListOf(players, Rank.DONATOR);
		final String playerList = getListOf(players, Rank.SPEC, Rank.DEFAULT);
    			
    	sender.sendMessage(Main.PREFIX + "There are �6" + playersOnline + " �7out of�6 " + game.getMaxPlayers() + " �7players online.");
    	
    	if (!ownerList.isEmpty()) {
        	sender.sendMessage("�8� �7Owners�8: �a" + ownerList + "�8.");
    	}
    	
    	if (!staffList.isEmpty()) {
        	sender.sendMessage("�8� �7Staff�8: �a" + staffList + "�8.");
    	}
    	
    	if (!donatorList.isEmpty()) {
        	sender.sendMessage("�8� �7Donators�8: �a" + donatorList + "�8.");
    	}
    	
    	if (!playerList.isEmpty()) {
        	sender.sendMessage("�8� �7Players�8: �a" + playerList + "�8.");
    	}
		return true;
	}

	@Override
	public List<String> tabComplete(final CommandSender sender, final String[] args) {
		return new ArrayList<String>();
	}
	
	/**
	 * Make a list of all visible players for the given list.
	 * 
	 * @param players The players visible.
	 * @param ranks The ranks to use.
	 * @return A string list.
	 */
	private String getListOf(List<Player> players, Rank... ranks) {
		final StringBuilder list = new StringBuilder();
    	int i = 1;
    	
    	List<Rank> rank = Arrays.asList(ranks);
    		
    	for (Player online : players) {
    		if (!rank.contains(User.get(online).getRank())) {
    			continue;
    		}
    		
    		if (list.length() > 0) {
				if (i == players.size()) {
					list.append(" �8and �a");
				} else {
					list.append("�8, �a");
				}
			}
			
			list.append(online.getName());
			i++;
		}
		
		return list.toString();
	}
}