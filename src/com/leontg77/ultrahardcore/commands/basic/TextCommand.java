package com.leontg77.ultrahardcore.commands.basic;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;

/**
 * Text command class.
 * 
 * @author LeonTG77
 */
public class TextCommand extends UHCCommand {

    public TextCommand() {
        super("text", "<message>");
    }

    @Override
    public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException("Only players can spawn floating texts.");
        }

        if (args.length == 0) {
            return false;
        }

        final Player player = (Player) sender;

        final String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 0, args.length));
        final ArmorStand stand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);

        stand.setCustomName(ChatColor.translateAlternateColorCodes('&', message));

        stand.setCustomNameVisible(true);
        stand.setSmall(true);

        stand.setGravity(false);
        stand.setVisible(false);
        return true;
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        return allPlayers();
    }
}