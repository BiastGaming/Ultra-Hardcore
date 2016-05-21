package com.leontg77.ultrahardcore.commands.user;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.gui.GUIManager;
import com.leontg77.ultrahardcore.gui.guis.StatsGUI;

/**
 * Stats command class.
 * 
 * @author LeonTG77
 */
public class StatsCommand extends UHCCommand {
    private final Main plugin;

    private final GUIManager gui;
    private final Game game;

    public StatsCommand(Main plugin, Game game, GUIManager gui) {
        super("stats", "[player]");

        this.plugin = plugin;

        this.game = game;
        this.gui = gui;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException("Only players can view someones stats.");
        }

        Player player = (Player) sender;

        if (game.isRecordedRound()) {
            throw new CommandException("Stats are disabled in Recorded Rounds.");
        }

        if (game.isPrivateGame()) {
            throw new CommandException("Stats are disabled in Private Games.");
        }

        Player target;

        if (args.length == 0) {
            target = player;
        } else {
            target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                throw new CommandException("'" + args[0] + "' is not online.");
            }
        }

        StatsGUI inv = gui.getGUI(StatsGUI.class);
        User user = plugin.getUser(target);

        player.openInventory(inv.get(user));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length != 1) {
            return new ArrayList<String>();
        }

        return allPlayers();
    }
}