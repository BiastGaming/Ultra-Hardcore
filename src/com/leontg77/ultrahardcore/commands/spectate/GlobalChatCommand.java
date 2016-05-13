package com.leontg77.ultrahardcore.commands.spectate;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.SpecManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class GlobalChatCommand extends UHCCommand {

    protected final Main plugin;
    protected final Game game;
    protected final SpecManager manager;

    public GlobalChatCommand(Main plugin, Game game, SpecManager manager) {
        super("g", "<message>");
        this.plugin = plugin;
        this.game = game;
        this.manager = manager;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Main.PREFIX + "Only players can use the global chat command-");
            return true;
        }

        Player player = (Player) sender;
        if (!manager.isSpectating(player)) {
            sender.sendMessage(Main.PREFIX + "You are not spectating.");
            return true;
        }

        Set<String> specChatProtected = manager.getSpecChatProtected();

        String name = player.getName();
        if (!specChatProtected.contains(name)) {
            sender.sendMessage(Main.PREFIX + "You do not have spectator chat protection enabled.");
            return true;
        }

        if (args.length == 0) {
            return false;
        }

        String message = Joiner.on(' ').join(args);
        if (message.startsWith("/")) {
            sender.sendMessage(Main.PREFIX + "You cannot start a message with the command slash.");
            return true;
        }

        if (!message.equalsIgnoreCase("gg")
                && plugin.getUser(player).getRank() == User.Rank.DEFAULT
                && !game.isPrivateGame()
                && !game.isRecordedRound()) {
            sender.sendMessage(Main.PREFIX + "You may not talk in global chat.");
            return true;
        }

        specChatProtected.remove(name);
        player.chat(message);
        specChatProtected.add(name);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return ImmutableList.of();
    }
}
