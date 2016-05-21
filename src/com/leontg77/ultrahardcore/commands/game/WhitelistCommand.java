package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Whitelist command class.
 * 
 * @author LeonTG77
 */
public class WhitelistCommand extends UHCCommand {
    private final Game game;

    public WhitelistCommand(Game game) {
        super("whitelist", "<on|off|add|remove|all|clear|list|prewl> [player]");

        this.game = game;
    }

    @Override
    public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
        if (args.length == 0) {
            return false;
        }

        if (args.length > 1) {
            if (args[0].equalsIgnoreCase("add")) {
                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    OfflinePlayer offline = PlayerUtils.getOfflinePlayer(args[1]);

                    PlayerUtils.broadcast(Main.PREFIX + ChatColor.GREEN + offline.getName() + " §7has been whitelisted.");
                    offline.setWhitelisted(true);
                    return true;
                }

                PlayerUtils.broadcast(Main.PREFIX + ChatColor.GREEN + target.getName() + " §7has been whitelisted.");
                target.setWhitelisted(true);
                return true;
            }

            if (args[0].equalsIgnoreCase("remove")) {
                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    OfflinePlayer offline = PlayerUtils.getOfflinePlayer(args[1]);

                    PlayerUtils.broadcast(Main.PREFIX + ChatColor.GREEN + offline.getName() + " §7is no longer whitelisted.");
                    offline.setWhitelisted(false);
                    return true;
                }

                PlayerUtils.broadcast(Main.PREFIX + ChatColor.GREEN + target.getName() + " §7is no longer whitelisted.");
                target.setWhitelisted(false);
                return true;
            }
        }

        if (args[0].equalsIgnoreCase("prewl")) {
            if (game.preWhitelists()) {
                PlayerUtils.broadcast(Main.PREFIX + "Pre whitelists have been disabled.");
                game.setPreWhitelists(false);
                return true;
            }

            PlayerUtils.broadcast(Main.PREFIX + "Pre whitelists have been enabled.");
            game.setPreWhitelists(true);
            return true;
        }

        if (args[0].equalsIgnoreCase("on")) {
            if (Bukkit.hasWhitelist()) {
                throw new CommandException("The whitelist is already on.");
            }

            PlayerUtils.broadcast(Main.PREFIX + "The whitelist is now on");
            Bukkit.setWhitelist(true);

            if (game.getTeamSize().startsWith("No") || game.getTeamSize().startsWith("Open")) {
                game.setState(State.NOT_RUNNING);
            } else {
                game.setState(State.CLOSED);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("off")) {
            if (!Bukkit.hasWhitelist()) {
                throw new CommandException("The whitelist is not on.");
            }

            PlayerUtils.broadcast(Main.PREFIX + "The whitelist is now off");

            if (game.getTeamSize().startsWith("cTo")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer 600 &7Whitelist is off for &8»&a");
            }

            if (game.getTeamSize().startsWith("FFA") || game.getTeamSize().startsWith("mTo")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer 300 &7Whitelist is off for &8»&a");
            }

            Bukkit.setWhitelist(false);
            game.setState(State.OPEN);
            return true;
        }

        if (args[0].equalsIgnoreCase("all")) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.setWhitelisted(true);
            }

            PlayerUtils.broadcast(Main.PREFIX + "All players has been whitelisted.");
            return true;
        }

        if (args[0].equalsIgnoreCase("clear")) {
            if (Bukkit.getWhitelistedPlayers().size() <= 0) {
                throw new CommandException("There are no whitelisted players.");
            }

            for (OfflinePlayer whitelisted : Bukkit.getWhitelistedPlayers()) {
                whitelisted.setWhitelisted(false);
            }

            PlayerUtils.broadcast(Main.PREFIX + "The whitelist has been cleared.");
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (Bukkit.getWhitelistedPlayers().size() <= 0) {
                throw new CommandException("There are no whitelisted players.");
            }

            StringBuilder list = new StringBuilder();
            int i = 1;

            for (OfflinePlayer whitelisted : Bukkit.getWhitelistedPlayers()) {
                if (list.length() > 0) {
                    if (i == Bukkit.getWhitelistedPlayers().size()) {
                        list.append(" and ");
                    } else {
                        list.append(", ");
                    }
                }

                list.append(whitelisted.getName());
                i++;
            }

            sender.sendMessage(Main.PREFIX + "There are §6" + (i - 1) + " §7whitelisted players");
            sender.sendMessage(Main.ARROW + "Wl'd players: §f" + list.toString() + ".");
            return true;
        }

        return false;
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        List<String> toReturn = new ArrayList<String>();

        if (args.length == 1) {
            toReturn.add("on");
            toReturn.add("off");
            toReturn.add("add");
            toReturn.add("remove");
            toReturn.add("all");
            toReturn.add("clear");
            toReturn.add("list");
            toReturn.add("prewl");
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (!online.isWhitelisted()) {
                        toReturn.add(online.getName());
                    }
                }
            }

            if (args[0].equalsIgnoreCase("remove")) {
                for (OfflinePlayer offline : Bukkit.getWhitelistedPlayers()) {
                    toReturn.add(offline.getName());
                }
            }
        }

        return toReturn;
    }
}