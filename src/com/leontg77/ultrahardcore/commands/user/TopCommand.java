package com.leontg77.ultrahardcore.commands.user;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.gui.GUIManager;
import com.leontg77.ultrahardcore.gui.guis.TopStatsGUI;

/**
 * Top command class.
 * 
 * @author LeonTG77
 */
public class TopCommand extends UHCCommand {
    private final GUIManager gui;
    private final Game game;

    public TopCommand(Game game, GUIManager gui) {
        super("top", "");

        this.game = game;
        this.gui = gui;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException("Only players can open the top 10 inventory.");
        }

        if (game.isRecordedRound()) {
            throw new CommandException("Stats are disabled in Recorded Rounds.");
        }

        if (game.isPrivateGame()) {
            throw new CommandException("Stats are disabled in Private Games.");
        }

        Player player = (Player) sender;
        player.openInventory(gui.getGUI(TopStatsGUI.class).get());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }
}