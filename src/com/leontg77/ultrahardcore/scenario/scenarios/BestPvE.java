package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.events.FinalHealEvent;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * BestPvE scenario class
 * 
 * @author LeonTG77
 */
public class BestPvE extends Scenario implements Listener, CommandExecutor {
    private final Timer timer;
    private final Main plugin;

    public BestPvE(Main plugin, Timer timer) {
        super("BestPvE", "Everyone starts on a list called bestpve list, if you take damage you are removed from the list. The only way to get back on the list is getting a kill, All players on the bestpve list gets 1 extra heart each 10 minutes.");

        plugin.getCommand("pve").setExecutor(this);
        plugin.getCommand("pvelist").setExecutor(this);

        this.plugin = plugin;
        this.timer = timer;
    }

    private final Set<String> list = new HashSet<String>();
    private BukkitRunnable task;

    @Override
    public void onDisable() {
        list.clear();
        task.cancel();
        task = null;
    }

    @Override
    public void onEnable() {
        if (!State.isState(State.INGAME)) {
            return;
        }

        on(new GameStartEvent());

        if (timer.getTimeSinceStartInSeconds() < 20) {
            return;
        }

        on(new FinalHealEvent());
    }

    @EventHandler
    public void on(GameStartEvent event) {
        task = new BukkitRunnable() {
            public void run() {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (!list.contains(online.getName())) {
                        online.sendMessage(ChatColor.GREEN + "BestPvE players gained a heart!");
                        continue;
                    }

                    online.sendMessage(ChatColor.GREEN + "You were rewarded for your PvE skills!");

                    online.setMaxHealth(online.getMaxHealth() + 2);
                    online.setHealth(online.getHealth() + 2);
                }
            }
        };

        task.runTaskTimer(plugin, 12000, 12000);
    }

    @EventHandler
    public void on(FinalHealEvent event) {
        for (OfflinePlayer whitelisted : Bukkit.getWhitelistedPlayers()) {
            list.add(whitelisted.getName());
        }
    }

    /**
     * Get the Best PvE list.
     *
     * @return The list.
     */
    public Set<String> getList() {
        return list;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(PlayerDeathEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        final Player killer = event.getEntity().getKiller();

        if (killer == null) {
            return;
        }

        final Player player = killer;

        if (list.contains(player.getName())) {
            return;
        }

        PlayerUtils.broadcast(ChatColor.GREEN + player.getName() + " got a kill! He is back on the Best PvE List!");

        new BukkitRunnable() {
            public void run() {
                list.add(player.getName());
            }
        }.runTaskLater(plugin, 40);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(EntityDamageEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (event.isCancelled()) {
            return;
        }

        final Player player = (Player) event.getEntity();

        if (!list.contains(player.getName())) {
            return;
        }

        if (timer.getTimeSinceStartInSeconds() < 20) {
            return;
        }

        PlayerUtils.broadcast(ChatColor.RED + player.getName() + " took damage!");
        list.remove(player.getName());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!isEnabled()) {
            sender.sendMessage(ChatColor.RED + "Best PvE game is not in progress!");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("pvelist")) {
            sender.sendMessage(ChatColor.GREEN + "Best PvE players:");

            for (String player : list) {
                sender.sendMessage(ChatColor.YELLOW + player);
            }
        }

        if (cmd.getName().equalsIgnoreCase("pve")) {
            if (!sender.hasPermission("uhc.bestpve")) {
                sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
                return true;
            }

            if (args.length < 2) {
                sender.sendMessage(ChatColor.GREEN + "Help for BestPvE:");
                sender.sendMessage("§7- §f/pve add <player> - §oAdd a player to the list.");
                sender.sendMessage("§7- §f/pve remove <player> - §oRemove a player from the list.");
                return true;
            }

            String player = args[1];

            if (args[0].equalsIgnoreCase("add")) {
                if (list.contains(player)) {
                    sender.sendMessage(ChatColor.RED + player + " is already on the list!");
                    return true;
                }

                sender.sendMessage(ChatColor.GOLD + player + " was added to the list!");
                list.add(player);
                return true;
            }

            if (args[0].equalsIgnoreCase("remove")) {
                if (!list.contains(player)) {
                    sender.sendMessage(ChatColor.RED + player + " is not present on the list!");
                    return true;
                }

                sender.sendMessage(ChatColor.GOLD + player + " was removed from the list!");
                list.remove(player);
                return true;
            }

            sender.sendMessage(ChatColor.GREEN + "Help for BestPvE:");
            sender.sendMessage("§7- §f/pve add <player> - §oAdd a player to the list.");
            sender.sendMessage("§7- §f/pve remove <player> - §oRemove a player from the list.");
        }
        return true;
    }
}