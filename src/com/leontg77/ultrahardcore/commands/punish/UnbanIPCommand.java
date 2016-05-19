package com.leontg77.ultrahardcore.commands.punish;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * UnbanIP command class
 * 
 * @author LeonTG77
 */
public class UnbanIPCommand extends UHCCommand {
    private static final Type BANLIST_TYPE = Type.IP;

    public UnbanIPCommand() {
        super("unbanip", "<ip>");
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            return false;
        }

        BanList list = Bukkit.getBanList(BANLIST_TYPE);
        String IP = args[0];

        if (!list.isBanned(IP)) {
            throw new CommandException("That IP is not banned.");
        }

        PlayerUtils.broadcast(Main.PREFIX + "An IP has been unbanned.");
        list.pardon(IP);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        List<String> toReturn = new ArrayList<String>();

        BanList list = Bukkit.getBanList(BANLIST_TYPE);

        if (args.length == 1) {
            for (BanEntry entry : list.getBanEntries()) {
                String IP = entry.getTarget();

                toReturn.add(IP);
            }
        }

        return toReturn;
    }
}