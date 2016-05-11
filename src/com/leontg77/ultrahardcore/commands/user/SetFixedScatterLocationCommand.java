package com.leontg77.ultrahardcore.commands.user;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.Collection;
import java.util.List;

public class SetFixedScatterLocationCommand extends UHCCommand {

    protected final Main plugin;

    public SetFixedScatterLocationCommand(Main plugin) {
        super("setfixedscatterlocation", "<player> <x> <z> | <player> none");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            return false;
        }

        OfflinePlayer target = PlayerUtils.getOfflinePlayer(args[0]);
        User user = plugin.getUser(target);

        if (args[1].equalsIgnoreCase("none")) {
            user.deleteFixedScatterLocation();
            sender.sendMessage(Main.PREFIX + target.getName() + " will now be normally scattered again.");
            return true;
        }

        if (args.length < 3) {
            return false;
        }

        int x = parseInt(args[1]);
        int z = parseInt(args[2]);
        user.setFixedScatterLocation(x, z);
        sender.sendMessage(Main.PREFIX + target.getName() + " will now always be scattered near X=§a" + x + "§7,Z=§a" + z);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        Collection<String> toMatch;
        if (args.length == 1) {
            toMatch = Lists.transform(Lists.newArrayList(Bukkit.getOnlinePlayers()), Player::getName);
        } else if (args.length == 2) {
            toMatch = ImmutableList.of("none", "0 0");
        } else {
            return null;
        }

        String lastArgument = args[args.length - 1];
        return StringUtil.copyPartialMatches(lastArgument, toMatch, Lists.newArrayList());
    }
}
