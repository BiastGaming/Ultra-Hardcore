package com.leontg77.ultrahardcore.commands.inventory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.User.Rank;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.gui.GUIManager;
import com.leontg77.ultrahardcore.gui.guis.HallOfFameGUI;

/**
 * Hall of fame command class.
 * 
 * @author LeonTG77
 */
public class HOFCommand extends UHCCommand {
    private final GUIManager gui;
    private final Main plugin;

    private final Settings settings;
    private final Game game;

    public HOFCommand(Main plugin, Game game, Settings settings, GUIManager gui) {
        super("hof", "[host]");

        this.plugin = plugin;
        this.gui = gui;

        this.settings = settings;
        this.game = game;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        String host = game.getHostHOFName(game.getHost());

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("global")) {
                int matchCount = 0;

                for (String hostName : settings.getHOF().getKeys(false)) {
                    matchCount += settings.getHOF().getConfigurationSection(hostName + ".games").getKeys(false).size();
                }

                sender.sendMessage(Main.PREFIX + "There's been a total of §a" + matchCount + " §7games hosted here.");
                return true;
            }

            host = game.getHostHOFName(args[0]);
        }

        if (!(sender instanceof Player)) {
            throw new CommandException("Only players can view the hall of fame.");
        }

        Player player = (Player) sender;

        if (settings.getHOF().getConfigurationSection(host) == null) {
            throw new CommandException("'" + host + "' has never hosted any games here.");
        }

        HallOfFameGUI hof = gui.getGUI(HallOfFameGUI.class);
        Inventory inv = hof.get(host);

        hof.currentHost.put(player.getName(), host);
        hof.currentPage.put(player.getName(), 1);

        player.openInventory(inv);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> toReturn = new ArrayList<String>();

        if (args.length == 1) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                Rank rank = plugin.getUser(online).getRank();

                if (rank.getLevel() > Rank.STAFF.getLevel()) {
                    toReturn.add(online.getName());
                }
            }

            for (String host : settings.getHOF().getKeys(false)) {
                toReturn.add(host);
            }

            toReturn.add("global");
        }

        return toReturn;
    }
}