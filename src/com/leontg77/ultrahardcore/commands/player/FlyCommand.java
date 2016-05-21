package com.leontg77.ultrahardcore.commands.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;

/**
 * Fly command class.
 * 
 * @author LeonTG77
 */
public class FlyCommand extends UHCCommand {

    public FlyCommand() {
        super("fly", "[player]");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                throw new CommandException("Only players can can toggle their fly mode.");
            }

            Player player = (Player) sender;

            if (game.isState(State.INGAME) && !player.getWorld().getName().equals("lobby")) {
                throw new CommandException("You can only fly in the spawn.");
            }

            if (player.getAllowFlight()) {
                player.sendMessage(Main.PREFIX + "Your fly mode is now disabled.");

                player.setAllowFlight(false);
                player.setFlying(false);
            } else {
                player.sendMessage(Main.PREFIX + "Your fly mode is now enabled.");

                player.setAllowFlight(true);
            }
            return true;
        }

        if (!sender.hasPermission(getPermission() + ".other")) {
            throw new CommandException("You cannot toggle other fly mode.");
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            throw new CommandException("'" + args[0] + "' is not online.");
        }

        if (game.isState(State.INGAME) && !target.getWorld().getName().equals("lobby")) {
            throw new CommandException("'" + target.getName() + "' can only fly in the spawn.");
        }

        if (target.getAllowFlight()) {
            sender.sendMessage(Main.PREFIX + "You disabled §a" + target.getName() + "'s §7fly mode.");
            target.sendMessage(Main.PREFIX + "Your fly mode is now disabled.");

            target.setAllowFlight(false);
            target.setFlying(false);
        } else {
            sender.sendMessage(Main.PREFIX + "You enabled §a" + target.getName() + "'s §7fly mode.");
            target.sendMessage(Main.PREFIX + "Your fly mode is now enabled.");

            target.setAllowFlight(true);
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return allPlayers();
        }

        return new ArrayList<String>();
    }
}