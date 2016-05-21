package com.leontg77.ultrahardcore.commands.basic;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;

/**
 * Edit command class.
 * 
 * @author LeonTG77
 */
public class EditCommand extends UHCCommand {
    private static final int BLOCK_REACH = 5;

    public EditCommand() {
        super("edit", "<line> <message>");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException("Only players can change sign lines.");
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            return false;
        }

        Block block = player.getTargetBlock((Set<Material>) null, BLOCK_REACH);

        if (block == null) {
            throw new CommandException("You are not looking at a block.");
        }

        BlockState state = block.getState();

        if (!(state instanceof Sign)) {
            throw new CommandException("You are not looking at a sign.");
        }

        Sign sign = (Sign) state;
        int line = parseInt(args[0], "line number");

        if (line < 1) {
            line = 1;
        }

        if (line > 4) {
            line = 4;
        }

        String message = ChatColor.translateAlternateColorCodes('&', Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length)));

        player.sendMessage(Main.PREFIX + "You set the sign's §a" + line + " §7line to: §6" + message);
        line--;

        sign.setLine(line, message);
        sign.update();
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return allPlayers();
    }
}