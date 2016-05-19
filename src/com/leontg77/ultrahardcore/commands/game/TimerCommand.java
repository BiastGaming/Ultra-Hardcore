package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.PacketUtils;

/**
 * Timer command class.
 * 
 * @author LeonTG77
 */
public class TimerCommand extends UHCCommand {
    private final Main plugin;

    public TimerCommand(Main plugin) {
        super("timer", "<duration|cancel> [message...]");

        this.plugin = plugin;
    }

    private static BukkitRunnable run = null;
    private boolean countdown = true;

    @Override
    public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
        if (args.length == 0) {
            return false;
        }

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("cancel")) {
                if (run == null) {
                    throw new CommandException("There are no timers running.");
                }

                sender.sendMessage(Main.PREFIX + "The timer has been stopped.");

                run.cancel();
                run = null;
                return true;
            }

            if (args.length == 1) {
                return false;
            }
        }

        if (run != null) {
            throw new CommandException("The timer is already running.");
        }

        final int time = parseInt(args[0], "time");
        countdown = time > 0;

        final String message = ChatColor.translateAlternateColorCodes('&', Joiner.on(' ').join(Arrays.copyOfRange(args, 1, args.length)));

        run = new BukkitRunnable() {
            private int ticks = time;

            public void run() {
                if (!countdown) {
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        PacketUtils.sendAction(online, message);
                    }
                    return;
                }

                for (Player online : Bukkit.getOnlinePlayers()) {
                    PacketUtils.sendAction(online, message + " " + DateUtils.ticksToString(ticks));
                }
                ticks--;

                if (ticks < 0) {
                    cancel();
                    run = null;
                }
            }
        };

        sender.sendMessage(Main.PREFIX + "The timer has been started.");
        run.runTaskTimer(plugin, 0, 20);
        return true;
    }

    @Override
    public List<String> tabComplete(final CommandSender sender, final String[] args) {
        List<String> toReturn = new ArrayList<String>();

        if (args.length == 1) {
            toReturn.add("cancel");
        }

        if (args.length == 2) {
            toReturn.add("&7Game is closing in &8»&a");
        }

        return toReturn;
    }

    /**
     * Check if the timer is currently running.
     *
     * @return True if it is, false otherwise.
     */
    public static boolean isRunning() {
        return run != null;
    }
}