package com.leontg77.ultrahardcore.commands.spectate;

import java.util.List;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.managers.SpecManager;

/**
 * Spec and staff chat command class.
 * 
 * @author D4mnX
 */
public class SpecAndStaffChatCommand extends UHCCommand {
    private final SpecManager spec;
    private final CanAccessChatPredicate predicate = new CanAccessChatPredicate();

    public SpecAndStaffChatCommand(SpecManager spec) {
        super("specandstaffchat", "<message>");

        this.spec = spec;
    }

    private static final String PREFIX = "§cSpec & Staff Chat §8» §7";

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (!predicate.apply(sender)) {
            throw new CommandException("You must be either spectating or a staff member.");
        }

        if (args.length == 0) {
            return false;
        }

        String message = Joiner.on(' ').join(args);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!predicate.apply(online)) {
                continue;
            }

            online.sendMessage(PREFIX + sender.getName() + "§8: §f" + message);
        }

        message = message.replaceAll("§l", "");
        message = message.replaceAll("§o", "");
        message = message.replaceAll("§r", "§f");
        message = message.replaceAll("§m", "");
        message = message.replaceAll("§n", "");

        Bukkit.getLogger().info(PREFIX + sender.getName() + "§8: §f" + message);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return allVisiblePlayers(sender);
    }

    protected class CanAccessChatPredicate implements Predicate<CommandSender> {
        @Override
        public boolean apply(@Nullable CommandSender sender) {
            if (sender == null) return false;

            boolean isStaff = sender.hasPermission("uhc.staff");
            boolean isSpectating = false;

            if (sender instanceof Player) {
                Player player = (Player) sender;
                isSpectating = spec.isSpectating(player);
            }

            return isStaff || isSpectating;
        }
    }
}
