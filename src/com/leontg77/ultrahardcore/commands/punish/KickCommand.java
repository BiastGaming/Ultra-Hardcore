package com.leontg77.ultrahardcore.commands.punish;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Rank;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.utils.PlayerUtils;
import com.leontg77.ultrahardcore.utils.PunishUtils;
import com.leontg77.ultrahardcore.utils.PunishUtils.PunishmentType;

/**
 * Kick command class.
 * 
 * @author LeonTG77
 */
public class KickCommand extends UHCCommand {
    private final Main plugin;

    public KickCommand(Main plugin) {
        super("kick", "<player> <reason>");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
        if (args.length < 2) {
            return false;
        }

        final String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length));

        if (args[0].equals("*")) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.hasPermission("uhc.prelist")) {
                    continue;
                }

                online.kickPlayer(message);
            }

            PlayerUtils.broadcast(Main.PREFIX + "All normal players has been kicked for §6" + message);
            return true;
        }

        if (args[0].equals("**")) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (online.isOp()) {
                    continue;
                }

                online.kickPlayer(message);
            }

            PlayerUtils.broadcast(Main.PREFIX + "All players has been kicked for §6" + message);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            throw new CommandException("'" + args[0] + "' is not online.");
        }

        User user = plugin.getUser(target);

        if (user.getRank().getLevel() >= Rank.STAFF.getLevel()) {
            throw new CommandException("'" + args[0] + "' is a staff member and can't be kicked.");
        }

        PlayerUtils.broadcast(Main.PREFIX + "§6" + target.getName() + " §7has been kicked for §a" + message, "uhc.kick");
        target.kickPlayer(message);
        PunishUtils.savePunishment(user, PunishmentType.KICK, message, null);
        return true;
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        List<String> toReturn = new ArrayList<String>();

        if (args.length == 1) {
            toReturn.add("*");
            toReturn.add("**");

            for (String name : allPlayers()) {
                toReturn.add(name);
            }
        }

        return toReturn;
    }
}