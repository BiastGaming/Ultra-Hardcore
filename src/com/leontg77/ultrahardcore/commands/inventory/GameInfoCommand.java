package com.leontg77.ultrahardcore.commands.inventory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.gui.GUIManager;
import com.leontg77.ultrahardcore.gui.guis.GameInfoGUI;

/**
 * UHC command class.
 * 
 * @author LeonTG77
 */
public class GameInfoCommand extends UHCCommand {
    private final GUIManager gui;

    public GameInfoCommand(GUIManager gui) {
        super("uhc", "");

        this.gui = gui;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException("Only players can view the game info inventory.");
        }

        Inventory inv = gui.getGUI(GameInfoGUI.class).get();
        Player player = (Player) sender;

        if (args.length == 0) {
            player.openInventory(inv);
            return true;
        }

        String info = Joiner.on(' ').join(args);

        for (ItemStack item : inv.getContents()) {
            if (item == null) {
                continue;
            }

            if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) {
                continue;
            }

            for (String lore : item.getItemMeta().getLore()) {
                if (lore.length() < 6) {
                    continue;
                }

                if (!lore.contains(":")) {
                    continue;
                }

                String name = lore.substring(6, lore.indexOf(':'));

                if (info.equalsIgnoreCase(name)) {
                    sender.sendMessage("§4§lUHC " + lore);
                    return true;
                }
            }
        }

        throw new CommandException("Could not find any information about '" + info + "'.");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        Inventory inv = gui.getGUI(GameInfoGUI.class).get();
        List<String> toReturn = new ArrayList<String>();

        if (args.length == 1) {
            for (ItemStack item : inv.getContents()) {
                if (item == null) {
                    continue;
                }

                if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) {
                    continue;
                }

                for (String lore : item.getItemMeta().getLore()) {
                    if (lore.length() < 6) {
                        continue;
                    }

                    if (!lore.contains(":")) {
                        continue;
                    }

                    String name = lore.substring(6, lore.indexOf(':'));
                    toReturn.add(name.toLowerCase());
                }
            }
        }

        return toReturn;
    }
}