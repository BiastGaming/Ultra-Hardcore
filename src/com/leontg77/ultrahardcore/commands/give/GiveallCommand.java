package com.leontg77.ultrahardcore.commands.give;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * GiveAll command class.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class GiveallCommand extends UHCCommand {

    public GiveallCommand() {
        super("giveall", "<item> [amount] [durability]");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            return false;
        }

        Material material = null;
        int amount = 1;
        short durability = 0;

        try {
            material = Material.getMaterial(parseInt(args[0]));
        } catch (Exception e) {
            for (Material types : Material.values()) {
                if (types.name().startsWith(args[0].toUpperCase())) {
                    material = types;
                    break;
                }
            }
        }

        if (material == null) {
            throw new CommandException("'" + args[0] + "' is not a valid item.");
        }

        if (args.length > 1) {
            amount = parseInt(args[1], "amount");
        }

        if (args.length > 2) {
            durability = (short) parseInt(args[2], "durability");
        }

        ItemStack item = new ItemStack(material, amount, durability);
        String itemName = item.getType().name().toLowerCase().replaceAll("_", " ") + (amount > 1 && !item.getType().name().endsWith("s") ? "s" : "");

        PlayerUtils.broadcast(Main.PREFIX + "All players recieved §a" + amount + " " + itemName + "§7.");

        for (Player online : Bukkit.getOnlinePlayers()) {
            PlayerUtils.giveItem(online, item.clone());
        }
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> toReturn = new ArrayList<String>();

        if (args.length == 1) {
            for (Material type : Material.values()) {
                toReturn.add(type.name().toLowerCase());
            }
        }

        return toReturn;
    }
}