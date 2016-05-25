package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.PvPEnableEvent;
import com.leontg77.ultrahardcore.exceptions.PlayerNotFoundException;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * BestBTC scenario class.
 * 
 * @author LeonTG77
 */
public class BestBTC extends Scenario implements Listener, CommandExecutor, TabCompleter {
    public static final String PREFIX = "§6Best BTC §8» §7";
    
    private final Map<UUID, Integer> scheduledHealth = Maps.newHashMap();
    private final Set<UUID> btcList = Sets.newHashSet();

    public BestBTC() {
        super("BestBTC", "After PvP enables, for every 10 minutes you are under Y=50, you gain a heart. Going above Y=50 will take you off the list. To get back on, you must mine a diamond.");

        plugin.getCommand("btc").setExecutor(this);
    }

    private BukkitRunnable heartTask;

    @Override
    public void onDisable() {
        if (heartTask != null) {
            heartTask.cancel();
        }
        
        btcList.clear();
        heartTask = null;
    }

    @Override
    public void onEnable() {
        if (!game.isState(State.INGAME)) {
            return;
        }
       
        if (timer.getPvP() > 0) {
            return;
        }
 
        on(new PvPEnableEvent());
    }
    
    private static final long TASK_INTERVAL = 12000L;

    @EventHandler
    public void on(PvPEnableEvent event) {
        for (OfflinePlayer offline : game.getOfflinePlayers()) {
            btcList.add(offline.getUniqueId());
        }
        
        heartTask = new BukkitRunnable() {
            public void run() {
                PlayerUtils.broadcast(ChatColor.GREEN + "BestBTC players gained a heart!");
                
                for (OfflinePlayer offline : game.getOfflinePlayers()) {
                    if (!btcList.contains(offline.getUniqueId())) {
                        continue;
                    }

                    Player online = offline.getPlayer();
                    
                    if (online == null) {
                        if (scheduledHealth.containsKey(offline.getUniqueId())) {
                            scheduledHealth.put(offline.getUniqueId(), scheduledHealth.get(offline.getUniqueId()) + 2);
                        } else {
                            scheduledHealth.put(offline.getUniqueId(), 2);
                        }
                        continue;
                    }
                    
                    online.setMaxHealth(online.getMaxHealth() + 2);
                    online.setHealth(online.getHealth() + 2);
                }
            }
        };

        heartTask.runTaskTimer(plugin, TASK_INTERVAL, TASK_INTERVAL);
    }

    /**
     * Get the Best PvE list.
     *
     * @return The list.
     */
    public Set<UUID> getBTCList() {
        return btcList;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(PlayerJoinEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Player player = event.getPlayer();
        
        if (!game.getPlayers().contains(player)) {
            return;
        }
        
        if (!scheduledHealth.containsKey(player.getUniqueId())) {
            return;
        }

        int healthToAdd = scheduledHealth.get(player.getUniqueId());
        
        player.setMaxHealth(player.getMaxHealth() + healthToAdd);
        player.setHealth(player.getHealth() + healthToAdd);
        
        scheduledHealth.remove(player.getUniqueId());
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        if (!game.getPlayers().contains(player)) {
            return;
        }

        if (btcList.contains(player.getUniqueId())) {
            return;
        }

        if (block.getType() != Material.DIAMOND_ORE) {
            return;
        }

        btcList.add(player.getUniqueId());
        PlayerUtils.broadcast(ChatColor.GREEN + player.getName() + " mined a diamond! He is back on the Best BTC List!");
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerMoveEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Player player = event.getPlayer();
        Location to = event.getTo();
        
        if (!game.getPlayers().contains(player)) {
            return;
        }

        if (to.getBlockY() <= 50) {
            return;
        }

        if (!btcList.contains(player.getUniqueId())) {
            return;
        }

        btcList.remove(player.getUniqueId());
        PlayerUtils.broadcast(ChatColor.RED + player.getName() + " moved above y:50!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!isEnabled()) {
            sender.sendMessage(PREFIX + "BestBTC is currently disabled.");
            return true;
        }

        if (args.length > 0) {
            if (args.length > 1) {
                if (args[0].equalsIgnoreCase("add")) {
                    if (!sender.hasPermission("uhc." + getName().toLowerCase())) {
                        sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
                        return true;
                    }
                    
                    Player target = Bukkit.getPlayer(args[1]);
                    
                    if (target == null) {
                        sender.sendMessage(new PlayerNotFoundException(args[1]).getMessage());
                        return true;
                    }
                    
                    if (btcList.contains(target.getUniqueId())) {
                        sender.sendMessage(PREFIX + "§a" + target.getName() + " §7is already on the list.");
                        return true;
                    }
                    
                    btcList.add(target.getUniqueId());
                    sender.sendMessage(PREFIX + "§a" + target.getName() + " §7has been added to the list.");
                    return true;
                }

                if (args[0].equalsIgnoreCase("remove")) {
                    if (!sender.hasPermission("uhc." + getName().toLowerCase())) {
                        sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
                        return true;
                    }
                    
                    Player target = Bukkit.getPlayer(args[1]);
                    
                    if (target == null) {
                        sender.sendMessage(new PlayerNotFoundException(args[1]).getMessage());
                        return true;
                    }
                    
                    if (!btcList.contains(target.getUniqueId())) {
                        sender.sendMessage(PREFIX + "§a" + target.getName() + " §7is not on the list.");
                        return true;
                    }

                    btcList.remove(target.getUniqueId());
                    sender.sendMessage(PREFIX + "§a" + target.getName() + " §7has been removed from the list.");
                }
            }

            if (args[0].equalsIgnoreCase("list")) {
                if (btcList.isEmpty()) {
                    sender.sendMessage(PREFIX + "No one is on the Best PvE List.");
                    return true;
                }
                
                StringBuilder list = new StringBuilder();
                int i = 1;                
                
                for (UUID uuid : btcList) {
                    if (list.length() > 0) {
                        if (btcList.size() == i) {
                            list.append(" §7and §e");
                        } else {
                            list.append("§7, §e");
                        }
                    }
                    
                    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                    list.append(ChatColor.YELLOW + player.getName());
                    
                    i++;
                }
                
                sender.sendMessage(PREFIX + "Best BTC List: §8(§6" + btcList.size() + "§8)");
                sender.sendMessage(Main.ARROW + list.toString());
                return true;
            }
        }

        sender.sendMessage(PREFIX + "BestBTC Commands:");
        sender.sendMessage("§8• §e/btc list §8- §f§oView the current players on the list.");
        sender.sendMessage("§8• §e/btc add <player> §8- §f§oAdd the player to the PvE List.");
        sender.sendMessage("§8• §e/btc remove <player> §8- §f§oRemove the player from the PvE List.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> toReturn = Lists.newArrayList();
        List<String> list = Lists.newArrayList();
        
        if (!isEnabled()) {
            return toReturn;
        }
        
        if (args.length == 1) {
            list.add("list");
            
            if (sender.hasPermission("uhc." + getName().toLowerCase())) {
                list.add("add");
                list.add("remove");
            }
        }
        
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
                list.addAll(allPlayers());
            }
        }
        
        for (String str : list) {
            if (args[args.length - 1].isEmpty() || str.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                toReturn.add(str);
            }
        }
        
        return toReturn;
    }
}
