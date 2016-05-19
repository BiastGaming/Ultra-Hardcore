package com.leontg77.ultrahardcore.commands.spectate;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.SpecManager;

/**
 * Back command class.
 * 
 * @author LeonTG77
 */
public class BackCommand extends UHCCommand {
    private final SpecManager spec;
    private final Main plugin;

    public BackCommand(Main plugin, SpecManager spec) {
        super("back", "");

        this.plugin = plugin;
        this.spec = spec;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException("Only players can teleport to their last location.");
        }

        Player player = (Player) sender;
        User user = plugin.getUser(player);

        if (player.getGameMode() != GameMode.CREATIVE && !spec.isSpectating(player)) {
            throw new CommandException("You can only do this while spectating.");
        }

        Location last = user.getLastLoc();

        if (last == null) {
            throw new CommandException("You haven't teleported anywhere yet.");
        }

        player.sendMessage(Main.PREFIX + "Returning to your last location.");
        player.teleport(last);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }
}