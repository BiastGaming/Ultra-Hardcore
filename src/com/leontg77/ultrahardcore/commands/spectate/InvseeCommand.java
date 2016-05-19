package com.leontg77.ultrahardcore.commands.spectate;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.gui.GUIManager;
import com.leontg77.ultrahardcore.gui.guis.InvseeGUI;
import com.leontg77.ultrahardcore.managers.SpecManager;

/**
 * Invsee command class
 * 
 * @author LeonTG77
 */
public class InvseeCommand extends UHCCommand {
    private final SpecManager spec;
    private final GUIManager gui;

    public InvseeCommand(GUIManager gui, SpecManager spec) {
        super("invsee", "<player>");

        this.spec = spec;
        this.gui = gui;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException("Only players can open player invs.");
        }

        Player player = (Player) sender;

        if (!spec.isSpectating(player)) {
            throw new CommandException("You can only do this while spectating.");
        }

        if (args.length == 0) {
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            throw new CommandException("'" + args[0] + "' is not online.");
        }

        InvseeGUI inv = gui.getGUI(InvseeGUI.class);
        player.openInventory(inv.get(target));
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