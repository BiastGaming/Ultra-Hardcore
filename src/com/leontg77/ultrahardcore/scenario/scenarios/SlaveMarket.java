package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.exceptions.CommandException;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * SlaveMarket scenario class
 * 
 * @author LeonTG77
 */
public class SlaveMarket extends Scenario implements Listener, CommandExecutor {
    private static final String PREFIX = "§2Market §8» §7";

    private final TeamManager teams;
    private final Main plugin;

    public SlaveMarket(Main plugin, TeamManager teams) {
        super("SlaveMarket", "8 slave owners are chosen and they get 30 diamonds to bid on players as they choose. Any spare diamonds will be kept by the slaveowner for use ingame.");

        plugin.getCommand("market").setExecutor(this);
        plugin.getCommand("bid").setExecutor(this);

        this.plugin = plugin;
        this.teams = teams;
    }

    private final Set<String> owners = new HashSet<String>();
    private int bidTime = 0, biggestBid = -1;

    private String currentSlave = null;
    private String bidWinner = null;

    private boolean bidProgressing = false;
    private BukkitRunnable task = null;

    @Override
    public void onDisable() {
        bidProgressing = false;
        bidWinner = null;

        biggestBid = -1;
        bidTime = 0;

        owners.clear();
    }

    @Override
    public void onEnable() {}

    @EventHandler
    public void on(GameStartEvent event) {
           disable();
    }

    @EventHandler
    public void on(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (!owners.contains(player.getName())) {
            return;
        }

        ItemStack item = event.getItemDrop().getItemStack();

        if (item == null || item.getType() != Material.DIAMOND) {
            return;
        }

        player.sendMessage(PREFIX + "You cannot drop your diamonds.");
        event.setCancelled(true);
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!owners.contains(player.getName())) {
            return;
        }

        ItemStack item = event.getCurrentItem();

        if (item == null || item.getType() != Material.DIAMOND) {
            return;
        }

        player.sendMessage(PREFIX + "You cannot move your diamonds around.");
        event.setCancelled(true);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!isEnabled()) {
            sender.sendMessage(PREFIX + "SlaveMarket is not enabled.");
            return true;
        }

        try {
            if (cmd.getName().equalsIgnoreCase("market")) {
                return executeMarket(sender, args);
            }

            if (cmd.getName().equalsIgnoreCase("bid")) {
                return executeBid(sender, args);
            }
        } catch (CommandException ex) {
            sender.sendMessage(ChatColor.RED + ex.getMessage());
        } catch (Exception ex) {
            // send them the error message in red if anything failed.
            sender.sendMessage(ChatColor.RED + ex.getClass().getName() + ": " + ex.getMessage());
            ex.printStackTrace();
        }
        return true;
    }

    private static final String OWNER_USAGE = Main.PREFIX + "Usage: /market owner <add|remove|list> [player] [amountofdias]";
    private static final String MAIN_USAGE = Main.PREFIX + "Usage: /market <owner|start|stop|reset> [...]";

    private static final String PERMISSION = "uhc.slavemarket";

    /**
     * Execute the /market command.
     *
     * @param sender The sender of the command.
     * @param args The argurments after the /[command]
     * @return Always true.
     * @throws CommandException If any wrong args were given.
     */
    private boolean executeMarket(final CommandSender sender, final String[] args) throws CommandException {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MAIN_USAGE);
            return true;
        }

        if (args[0].equalsIgnoreCase("reset")) {
            bidProgressing = false;
            bidWinner = null;

            biggestBid = -1;
            bidTime = 0;

            owners.clear();

            for (Team team : teams.getTeamsWithPlayers()) {
                for (OfflinePlayer entry : teams.getPlayers(team)) {
                    teams.leaveTeam(entry, false);
                }
            }

            PlayerUtils.broadcast(PREFIX + "SlaveMarket has been reset.");
            return true;
        }

        if (args[0].equalsIgnoreCase("owner")) {
            if (args.length == 1) {
                sender.sendMessage(OWNER_USAGE);
                return true;
            }

            if (args[1].equalsIgnoreCase("add")) {
                if (args.length == 2) {
                    sender.sendMessage(OWNER_USAGE);
                    return true;
                }

                Player target = Bukkit.getPlayer(args[2]);

                if (target == null) {
                    throw new CommandException("'" + args[2] + "' is not online.");
                }

                Team team = teams.findAvailableTeam();
                owners.add(target.getName());

                if (team == null) {
                    throw new CommandException("There are no more available teams.");
                }

                PlayerUtils.broadcast(PREFIX + "§a" + target.getName() + " §7has been selected as the slave owner of §6Team " + team.getName().substring(3) + "§7.");
                teams.joinTeam(team, target);

                int amount = 30;

                if (args.length > 3) {
                    amount = parseInt(args[3], "amount");
                }

                PlayerUtils.giveItem(target, new ItemStack (Material.DIAMOND, amount));
                return true;
            }

            if (args[1].equalsIgnoreCase("remove")) {
                if (args.length == 2) {
                    sender.sendMessage(OWNER_USAGE);
                    return true;
                }

                Player target = Bukkit.getPlayer(args[2]);

                if (target == null) {
                    throw new CommandException("'" + args[2] + "' is not online.");
                }

                PlayerUtils.broadcast(PREFIX + "§a" + target.getName() + " §7is no longer a slave owner.");

                owners.remove(target.getName());
                teams.leaveTeam(target, true);

                target.getInventory().clear();
                return true;
            }

            if (args[1].equalsIgnoreCase("list")) {
                StringBuilder list = new StringBuilder();
                int index = 1;

                for (String owner : owners) {
                    if (list.length() > 0) {
                        if (index == owners.size()) {
                            list.append(" §8and §a");
                        } else {
                            list.append("§8, §a");
                        }
                    }

                    list.append(ChatColor.GREEN + owner);
                    index++;
                }

                sender.sendMessage(PREFIX + "Current slave owners: §8(§6" + owners.size() + "§8)");
                sender.sendMessage(Main.ARROW + list.toString());
                return true;
            }

            sender.sendMessage(OWNER_USAGE);
            return true;
        }

        if (args[0].equalsIgnoreCase("start")) {
            if (task != null && Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId())) {
                throw new CommandException("The slave market timer is already running.");
            }

            new BukkitRunnable() {
                int timeLeft = 3;

                public void run() {
                    bidTime = -1;

                    if (timeLeft == 0) {
                        task = new BukkitRunnable() {
                            public void run() {
                                if (biggestBid >= 0) {
                                    bidTime--;
                                }

                                if (bidTime == 0) {
                                    bidProgressing = false;

                                    Player winner = Bukkit.getPlayer(bidWinner);

                                    if (winner == null) {
                                        PlayerUtils.broadcast(PREFIX + "The bid winner is offline, restarting...");
                                        return;
                                    }

                                    Player slave = Bukkit.getPlayer(currentSlave);

                                    if (slave == null) {
                                        PlayerUtils.broadcast(PREFIX + "The current slave is offline, restarting...");
                                        return;
                                    }

                                    Team team = teams.getTeam(winner);

                                    if (team == null) {
                                        sender.sendMessage(ChatColor.RED + "Could not join team.");
                                        return;
                                    }

                                    PlayerUtils.broadcast(PREFIX + "§a" + slave.getName() + "§7 was sold to §a" + winner.getName() + "§7 for §a" + (biggestBid == 1 ? "1 diamond" : biggestBid + " diamonds") + "§7.");

                                    for (Player online : Bukkit.getOnlinePlayers()) {
                                        online.playSound(online.getLocation(), Sound.FIREWORK_TWINKLE, 1, 1);
                                    }

                                    for (ItemStack item : winner.getInventory().getContents()) {
                                        if (item != null && item.getType() == Material.DIAMOND) {
                                            if (item.getAmount() > biggestBid) {
                                                item.setAmount(item.getAmount() - biggestBid);
                                            } else {
                                                winner.getInventory().remove(item);
                                            }
                                            break;
                                        }
                                    }

                                    teams.joinTeam(team, slave);
                                    return;
                                }

                                if (bidTime == -1) {
                                    List<Player> list = new ArrayList<Player>();

                                    for (Player online : Bukkit.getOnlinePlayers()) {
                                        if (teams.getTeam(online) == null) {
                                            list.add(online);
                                        }
                                    }

                                    if (list.isEmpty()) {
                                        task.cancel();
                                        task = null;

                                        PlayerUtils.broadcast(PREFIX + "§oNo more players to bid on!");
                                        return;
                                    }

                                    Collections.shuffle(list);

                                    currentSlave = list.get(new Random().nextInt(list.size())).getName();
                                    bidWinner = null;

                                    bidProgressing = true;
                                    biggestBid = -1;
                                    bidTime = 5;

                                    PlayerUtils.broadcast(PREFIX + "§a" + currentSlave + "§7 is now up for auction! Use §a/bid§7!");

                                    for (Player online : Bukkit.getOnlinePlayers()) {
                                        online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 0);
                                    }
                                    return;
                                }

                                if (bidTime < 4) {
                                    PlayerUtils.broadcast(PREFIX + "Bidding ends in §a" + bidTime + "§7.");

                                    for (Player online : Bukkit.getOnlinePlayers()) {
                                        online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
                                    }
                                }
                            }
                        };

                        task.runTaskTimer(plugin, 0, 20);
                        cancel();
                        return;
                    }

                    PlayerUtils.broadcast(PREFIX + "Bidding starts in §a" + timeLeft + "§7.");

                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.playSound(online.getLocation(), Sound.NOTE_PLING, 1, 1);
                    }

                    timeLeft--;
                }
            }.runTaskTimer(plugin, 0, 20);
        }

        if (args[0].equalsIgnoreCase("stop")) {
            if (task == null || !Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId())) {
                throw new CommandException("The are no slave market timers running.");
            }

            task.cancel();
            task = null;

            PlayerUtils.broadcast(PREFIX + "The slave bidding has been stopped.");
        }
        return true;
    }

    /**
     * Execute the /bid command.
     *
     * @param sender The sender of the command.
     * @param args The argurments after the /[command]
     * @return Always true.
     * @throws CommandException If any wrong args were given.
     */
    private boolean executeBid(final CommandSender sender, final String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            throw new CommandException("Only players can bid on players.");
        }

        Player player = (Player) sender;

        if (!owners.contains(player.getName())) {
            throw new CommandException("You are not a slave owner.");
        }

        if (args.length == 0) {
            player.sendMessage(Main.PREFIX + "Usage: /bid <amount>");
            return true;
        }

        int amount = parseInt(args[0], "amount");

        if (!bidProgressing) {
            throw new CommandException("There are no slaves being bid on right now.");
        }

        if (amount < 0) {
            throw new CommandException("Bids cannot be negative.");
        }

        if (amount <= biggestBid) {
            throw new CommandException("Bids must be greater than the previous bid.");
        }

        if (!hasEnough(player, Material.DIAMOND, amount)) {
            throw new CommandException("You can't bid more diamonds than you have.");
        }

        PlayerUtils.broadcast(PREFIX + "§a" + player.getName() + "§7 has bid §a" + (amount == 1 ? "1 diamond" : amount + " diamonds") + "§7.");

        bidWinner = player.getName();
        biggestBid = amount;

        if (bidTime <= 5) {
            bidTime = bidTime + 3;
        }
        return true;
    }

    /**
     * Check if the given player has enough of the given number of the given material.
     *
     * @param player the player.
     * @param material the material.
     * @param entered the number.
     *
     * @return <code>True</code> if the player has the given number of the material, <code>false</code> otherwise
     */
    public static boolean hasEnough(Player player, Material material, int entered) {
        int total = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) {
                continue;
            }

            if (item.getType() == material) {
                total = total + item.getAmount();
            }
        }

        return total >= entered;
    }
}