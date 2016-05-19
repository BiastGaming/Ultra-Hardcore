package com.leontg77.ultrahardcore.commands.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * SetMaxhealth command class.
 * 
 * @author LeonTG77
 */
public class SetmaxhealthCommand extends UHCCommand {

    public SetmaxhealthCommand() {
        super("setmaxhealth", "<health> [player|*]");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            return false;
        }

        double health = parseDouble(args[0], "max health");

        // I don't want them to not have any health...
        if (health < 1) {
            health = 1;
        }

        // uh, I are limits...
        if (health > 2000) {
            health = 2000;
        }

        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                throw new CommandException("Only players can change their max health.");
            }

            Player player = (Player) sender;

            player.sendMessage(Main.PREFIX + "You set your max health to §6" + NumberUtils.makePercent(health).substring(2) + "%");
            player.setMaxHealth(health);
            return true;
        }

        if (args[1].equals("*")) {
            PlayerUtils.broadcast(Main.PREFIX + "All players max health was set to §6" + NumberUtils.makePercent(health).substring(2) + "%");

            for (Player online : Bukkit.getOnlinePlayers()) {
                online.setMaxHealth(health);
            }
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            throw new CommandException("'" + args[1] + "' is not online.");
        }

        sender.sendMessage(Main.PREFIX + "You set §a" + target.getName() + "'s §7max health to §6" + NumberUtils.makePercent(health).substring(2) + "%");
        target.sendMessage(Main.PREFIX + "Your max health was set to §6" + NumberUtils.makePercent(health).substring(2) + "%");

        target.setMaxHealth(health);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return allPlayers();
        }

        return new ArrayList<String>();
    }
}