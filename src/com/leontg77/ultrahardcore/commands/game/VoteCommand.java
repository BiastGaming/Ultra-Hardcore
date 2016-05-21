package com.leontg77.ultrahardcore.commands.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Joiner;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Vote command class
 * 
 * @author LeonTG77
 */
public class VoteCommand extends UHCCommand {
    private final Main plugin;

    public VoteCommand(Main plugin) {
        super("vote", "<message>");

        this.plugin = plugin;
    }

    private static final List<UUID> voted = new ArrayList<UUID>();
    private static BukkitRunnable run = null;

    private static int yes = 0;
    private static int no = 0;

    @Override
    public boolean execute(final CommandSender sender, final String[] args) throws CommandException {
        if (args.length == 0) {
            return false;
        }

        if (isRunning()) {
            throw new CommandException("A vote is already running");
        }
        
        String message = Joiner.on(' ').join(Arrays.copyOfRange(args, 0, args.length));
        voted.clear();
        
        yes = 0;
        no = 0;
        
        PlayerUtils.broadcast(Main.PREFIX + "A vote has started for: §6" + message + "§7.");
        PlayerUtils.broadcast(Main.ARROW + "Say §a'y'§7 or §c'n'§7 in chat to vote.");
        
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "timer 30 &7The vote ends in &8»&a");
        
        run = new BukkitRunnable() {
            public void run() {
                PlayerUtils.broadcast(Main.PREFIX + "The vote has ended, the results are: §a" + yes + " yes §7and §c" + no + " no§7.");

                run = null;
                voted.clear();

                yes = 0;
                no = 0;
            }
        };

        run.runTaskLater(plugin, 600);
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return new ArrayList<String>();
    }

    /**
     * Check if a vote is currently running.
     *
     * @return True if it is, false otherwise.
     */
    public static boolean isRunning() {
        return run != null;
    }

    /**
     * Check if the given player has already voted.
     *
     * @param player The player checking.
     * @return True if he has, false otherwise.
     */
    public static boolean hasVoted(Player player) {
        return voted.contains(player.getUniqueId());
    }

    /**
     * Add a vote for the given player.
     *
     * @param player The player voting.
     * @param votedYes True if they voted yes, false if they voted no.
     */
    public static void addVote(Player player, boolean votedYes) {
        voted.add(player.getUniqueId());

        if (votedYes) {
            yes++;
        } else {
            no++;
        }
    }
}