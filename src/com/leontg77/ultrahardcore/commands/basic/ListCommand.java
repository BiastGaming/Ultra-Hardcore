package com.leontg77.ultrahardcore.commands.basic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User.Rank;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.scenario.scenarios.Anonymous;

/**
 * List command class.
 * 
 * @author LeonTG77
 */
public class ListCommand extends UHCCommand {
    private final ScenarioManager scen;

    public ListCommand(ScenarioManager scen) {
        super("list", "");

        this.scen = scen;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (scen.getScenario(Anonymous.class).isEnabled()) {
            throw new CommandException("You can't view players in anonymous.");
        }
        
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            throw new CommandException("There are no players online.");
        }

        List<Player> players = new ArrayList<Player>();
        int playersOnline = 0;

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (sender instanceof Player && !((Player) sender).canSee(online)) {
                continue;
            }

            players.add(online);
            playersOnline++;
        }

        String ownerList = getListOf(players, Rank.OWNER);
        String staffList = getListOf(players, Rank.HOST, Rank.TRIAL, Rank.STAFF);

        String donatorList = getListOf(players, Rank.DONATOR);
        String playerList = getListOf(players, Rank.SPEC, Rank.DEFAULT);

        sender.sendMessage(Main.PREFIX + "There are §6" + playersOnline + " §7out of§6 " + game.getMaxPlayers() + " §7players online.");

        if (!ownerList.isEmpty()) {
            sender.sendMessage(Main.ARROW + "Owners§8: §a" + ownerList + "§8.");
        }

        if (!staffList.isEmpty()) {
            sender.sendMessage(Main.ARROW + "Staff§8: §a" + staffList + "§8.");
        }

        if (!donatorList.isEmpty()) {
            sender.sendMessage(Main.ARROW + "Donators§8: §a" + donatorList + "§8.");
        }

        if (!playerList.isEmpty()) {
            sender.sendMessage(Main.ARROW + "Players§8: §a" + playerList + "§8.");
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
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
            if (!rank.contains(plugin.getUser(online).getRank())) {
                continue;
            }

            if (list.length() > 0) {
                if (i == players.size()) {
                    list.append(" §8and §a");
                } else {
                    list.append("§8, §a");
                }
            }

            list.append(online.getName());
            i++;
        }

        return list.toString();
    }
}