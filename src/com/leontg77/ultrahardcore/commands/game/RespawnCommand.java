package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Stat;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Respawn command class.
 * 
 * @author LeonTG77
 */
public class RespawnCommand extends UHCCommand {
    private final Main plugin;

    public RespawnCommand(Main plugin) {
        super("respawn", "<player> [givebasicstuff]");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            throw new CommandException("'" + args[0] + "' is not online.");
        }

        boolean giveBasicStuff = false;
        User user = plugin.getUser(target);

        if (args.length > 1) {
            giveBasicStuff = parseBoolean(args[1]);
        }

        Location death = user.getDeathLoc();

        if (death == null) {
            throw new CommandException("'" + target.getName() + "' has no saved death location.");
        }

        user.setStat(Stat.DEATHS, user.getStat(Stat.DEATHS) - 1);
        target.setWhitelisted(true);

        target.teleport(death);
        user.setDeathLoc(null);

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.showPlayer(target);
        }

        sender.sendMessage(Main.PREFIX + "Sucessfully respawned §a" + target.getName() + "§7.");

        if (!giveBasicStuff) {
            return true;
        }

        PlayerUtils.giveItem(target, new ItemStack(Material.IRON_SWORD));
        PlayerUtils.giveItem(target, new ItemStack(Material.IRON_PICKAXE));
        PlayerUtils.giveItem(target, new ItemStack(Material.STONE_AXE));
        PlayerUtils.giveItem(target, new ItemStack(Material.STONE_SPADE));
        PlayerUtils.giveItem(target, new ItemStack(Material.IRON_HELMET));
        PlayerUtils.giveItem(target, new ItemStack(Material.IRON_CHESTPLATE));
        PlayerUtils.giveItem(target, new ItemStack(Material.IRON_LEGGINGS));
        PlayerUtils.giveItem(target, new ItemStack(Material.IRON_BOOTS));
        PlayerUtils.giveItem(target, new ItemStack(Material.LOG, 32));
        PlayerUtils.giveItem(target, new ItemStack(Material.COOKED_BEEF, 23));

        sender.sendMessage(Main.PREFIX + "Given iron armor, basic tools, food and wood.");
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> toReturn = new ArrayList<String>();

        if (args.length == 1) {
            return allPlayers();
        }

        if (args.length == 2) {
            toReturn.add("true");
            toReturn.add("false");
        }

        return toReturn;
    }
}