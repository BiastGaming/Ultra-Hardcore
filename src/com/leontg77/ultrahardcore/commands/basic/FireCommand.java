package com.leontg77.ultrahardcore.commands.basic;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.FireworkManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Fire command class.
 * 
 * @author LeonTG77
 */
public class FireCommand extends UHCCommand {
    private final FireworkManager firework;

    public FireCommand(FireworkManager firework) {
        super("fire", "");

        this.firework = firework;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        PlayerUtils.broadcast(Main.PREFIX + "The firework show has started by §6" + sender.getName() + "§7!");

        if (!(sender instanceof Player)) {
            firework.startFireworkShow();
            return true;
        }

        Player player = (Player) sender;
        firework.startFireworkShow(player.getLocation());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }
}