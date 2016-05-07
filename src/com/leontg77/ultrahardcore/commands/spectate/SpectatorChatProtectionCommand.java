package com.leontg77.ultrahardcore.commands.spectate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.managers.SpecManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.List;
import java.util.Set;

public class SpectatorChatProtectionCommand extends UHCCommand {

    protected final SpecManager specManager;

    public SpectatorChatProtectionCommand(SpecManager specManager) {
        super("spectatorchatprotection", "<on|off|toggle>");
        this.specManager = specManager;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException("Command intended for players only");
        }

        String name = sender.getName();
        if (!specManager.getSpectators().contains(name)) {
            sender.sendMessage(Main.PREFIX + "You are not spectating.");
            return true;
        }

        Set<String> specChatProtected = specManager.getSpecChatProtected();

        boolean currentlyProtectedStatus = specChatProtected.contains(name);
        boolean newStatus;

        if (args[0].equalsIgnoreCase("on")) {
            newStatus = true;
        } else if (args[0].equals("off")) {
            newStatus = false;
        } else if (args[0].equals("toggle")) {
            newStatus = !currentlyProtectedStatus;
        } else {
            return false;
        }

        if (currentlyProtectedStatus == newStatus) {
            sender.sendMessage(Main.PREFIX + "Your spectator chat protection is already " +
                    (currentlyProtectedStatus ? "enabled." : "disabled."));
            return true;
        }

        if (newStatus) {
            specChatProtected.add(name);
            sender.sendMessage(Main.PREFIX + "You have enabled spectator chat protection.");
        } else {
            specChatProtected.remove(name);
            sender.sendMessage(Main.PREFIX + "You have disabled spectator chat protection.");
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return StringUtil.copyPartialMatches(args[0], ImmutableList.of("on", "off", "toggle"), Lists.newArrayList());
    }
}