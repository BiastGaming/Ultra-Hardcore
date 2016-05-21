package com.leontg77.ultrahardcore.commands.spectate;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.managers.SpecManager;

/**
 * Speed command class.
 * 
 * @author LeonTG77
 */
public class SpeedCommand extends UHCCommand {
    private final SpecManager spec;

    public SpeedCommand(SpecManager spec) {
        super("speed", "<speed> [player]");

        this.spec = spec;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (!spec.isSpectating(sender.getName())) {
            throw new CommandException("You can only do this while spectating.");
        }

        if (args.length == 0) {
            return false;
        }

        float speed = parseFloat(args[0], "speed");

        if (speed > 10f) {
            speed = 10f;
        } else if (speed < 0.0001f) {
            speed = 0.0001f;
        }

        float orgSpeed = speed;
        Player target;

        if (args.length == 1 || !sender.hasPermission(getPermission() + ".others")) {
            if (!(sender instanceof Player)) {
                throw new CommandException("Only players can change their walk/fly speed.");
            }

            target = (Player) sender;
        } else {
            target = Bukkit.getPlayer(args[1]);
        }

        if (target == null) {
            throw new CommandException("'" + args[1] + "' is not online.");
        }

        float defaultSpeed = target.isFlying() ? 0.1f : 0.2f;
        float maxSpeed = 1f;

        if (speed < maxSpeed) {
            speed = defaultSpeed * speed;
        } else {
            float ratio = ((speed - 1) / 9) * (maxSpeed - defaultSpeed);

            speed = ratio + defaultSpeed;
        }

        if (target.isFlying()) {
            target.setFlySpeed(speed);

            if (target == sender) {
                sender.sendMessage(Main.PREFIX + "You set your flying speed to §6" + orgSpeed + "§7.");
            } else {
                sender.sendMessage(Main.PREFIX + "You set " + target.getName() + "'s flying speed to §6" + orgSpeed + "§7.");
                target.sendMessage(Main.PREFIX + "Your flying speed was set to §6" + orgSpeed + "§7.");
            }
        } else {
            target.setWalkSpeed(speed);

            if (target == sender) {
                sender.sendMessage(Main.PREFIX + "You set your walking speed to §6" + orgSpeed + "§7.");
            } else {
                sender.sendMessage(Main.PREFIX + "You set " + target.getName() + "'s walking speed to §6" + orgSpeed + "§7.");
                target.sendMessage(Main.PREFIX + "Your walking speed was set to §6" + orgSpeed + "§7.");
            }
        }
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