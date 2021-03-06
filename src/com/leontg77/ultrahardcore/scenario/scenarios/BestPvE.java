package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.FinalHealEvent;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.exceptions.PlayerNotFoundException;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * BestPvE scenario class.
 * 
 * @author LeonTG77
 */
public class BestPvE extends Scenario implements Listener, CommandExecutor, TabCompleter {
    private static final String PREFIX = "§aBest PvE §8» §7";
    
    private final Set<UUID> invulnerable = Sets.newHashSet();
    private final Set<UUID> pveList = Sets.newHashSet();
    
    private final Map<UUID, Integer> scheduledHealth = Maps.newHashMap();

    public BestPvE() {
        super("BestPvE", "Everyone starts on a list called BestPvE list, if you take damage you are removed from the list. The only way to get back on the list is getting a kill, All players on the BestPvE list gets 1 extra heart each 10 minutes.");

        plugin.getCommand("pve").setExecutor(this);
        plugin.getCommand("blist").setExecutor(this);
    }

    private BukkitRunnable heartTask;

    @Override
    public void onDisable() {
        if (heartTask != null) {
            heartTask.cancel();
        }
        
        pveList.clear();
        heartTask = null;
    }

    @Override
    public void onEnable() {
        if (!game.isState(State.INGAME)) {
            return;
        }

        on(new GameStartEvent());

        if (timer.getTimeSinceStartInSeconds() < 20) {
            return;
        }
 
        on(new FinalHealEvent());
    }
    
    private static final long TASK_INTERVAL = 12000L;

    @EventHandler
    public void on(GameStartEvent event) {
        heartTask = new BukkitRunnable() {
            public void run() {
                PlayerUtils.broadcast(ChatColor.GREEN + "BestPvE players gained a heart!");
                
                for (OfflinePlayer offline : game.getOfflinePlayers()) {
                    if (!pveList.contains(offline.getUniqueId())) {
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

    @EventHandler
    public void on(FinalHealEvent event) {
        for (OfflinePlayer offline : game.getOfflinePlayers()) {
            pveList.add(offline.getUniqueId());
        }
    }

    /**
     * Get the Best PvE list.
     *
     * @return The list.
     */
    public Set<UUID> getPvEList() {
        return pveList;
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

    @EventHandler(priority = EventPriority.LOW)
    public void on(PlayerDeathEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Player player = event.getEntity();
        
        if (!game.getWorlds().contains(player.getWorld())) {
            return;
        }
        
        Player killer = player.getKiller();

        if (killer == null) {
            return;
        }
        
        if (!game.getPlayers().contains(killer)) {
            return;
        }

        if (pveList.contains(killer.getUniqueId())) {
            return;
        }

        invulnerable.add(killer.getUniqueId());
        pveList.add(killer.getUniqueId());
        
        PlayerUtils.broadcast(ChatColor.GREEN + killer.getName() + " got a kill and is back on the Best PvE List!");

        new BukkitRunnable() {
            public void run() {
                invulnerable.remove(killer.getUniqueId());
            }
        }.runTaskLater(plugin, 40);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(EntityDamageEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Entity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            return;
        }
        
        Player player = (Player) entity;

        if (!pveList.contains(player.getUniqueId())) {
            return;
        }

        if (invulnerable.contains(player.getUniqueId())) {
            return;
        }

        if (timer.getTimeSinceStartInSeconds() < 20) {
            return;
        }

        PlayerUtils.broadcast(ChatColor.RED + player.getName() + " took damage!");
        pveList.remove(player.getUniqueId());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("blist")) {
            sender.sendMessage(PREFIX + "For Best PvE, use: §e'/pve list'");
            sender.sendMessage(BestBTC.PREFIX + "For Best BTC, use: §e'/btc list'");
            return true;
        }
        
        if (!isEnabled()) {
            sender.sendMessage(PREFIX + "BestPvE is currently disabled.");
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
                    
                    if (pveList.contains(target.getUniqueId())) {
                        sender.sendMessage(PREFIX + "§6" + target.getName() + " §7is already on the list.");
                        return true;
                    }
                    
                    pveList.add(target.getUniqueId());
                    sender.sendMessage(PREFIX + "§6" + target.getName() + " §7has been added to the list.");
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
                    
                    if (!pveList.contains(target.getUniqueId())) {
                        sender.sendMessage(PREFIX + "§6" + target.getName() + " §7is not on the list.");
                        return true;
                    }

                    pveList.remove(target.getUniqueId());
                    sender.sendMessage(PREFIX + "§6" + target.getName() + " §7has been removed from the list.");
                }
            }

            if (args[0].equalsIgnoreCase("list")) {
                if (pveList.isEmpty()) {
                    sender.sendMessage(PREFIX + "No one is on the Best PvE List.");
                    return true;
                }
                
                StringBuilder list = new StringBuilder();
                int i = 1;                
                
                for (UUID uuid : pveList) {
                    if (list.length() > 0) {
                        if (pveList.size() == i) {
                            list.append(" §7and §e");
                        } else {
                            list.append("§7, §e");
                        }
                    }
                    
                    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                    list.append(ChatColor.YELLOW + player.getName());
                    
                    i++;
                }
                
                sender.sendMessage(PREFIX + "Best PvE List: §8(§6" + pveList.size() + "§8)");
                sender.sendMessage(Main.ARROW + list.toString());
                return true;
            }
        }

        sender.sendMessage(PREFIX + "BestPvE Commands:");
        sender.sendMessage("§8• §e/pve list §8- §f§oView the current players on the list.");
        sender.sendMessage("§8• §e/pve add <player> §8- §f§oAdd the player to the PvE List.");
        sender.sendMessage("§8• §e/pve remove <player> §8- §f§oRemove the player from the PvE List.");
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