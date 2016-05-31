package com.leontg77.ultrahardcore.minigames.listeners;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Stat;
import com.leontg77.ultrahardcore.minigames.Arena;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Arena listener class.
 * <p> 
 * Contains all eventhandlers for arena releated events.
 * 
 * @author LeonTG77
 */
public class ArenaListener implements Listener {
    private final Main plugin;
    private final Arena arena;

    private static final ItemStack THREE_LAPIS = new ItemStack(Material.INK_SACK, 3, (short) 4);

    public ArenaListener(Main plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!arena.hasPlayer(player)) {
            return;
        }

        if (player.getGameMode() == GameMode.CREATIVE) {
            player.sendMessage(Arena.PREFIX + "§cNo creative mode in the Arena.");
            player.setGameMode(GameMode.SURVIVAL);
        }

        if (player.isFlying() || player.getAllowFlight()) {
            player.sendMessage(Arena.PREFIX + "§cNo fly mode in the Arena.");
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        if (!arena.hasPlayer(player)) {
            return;
        }

        if (block.getLocation().getY() > 110) {
            player.sendMessage(Arena.PREFIX + "You cannot build higher than y: 110!");
            event.setCancelled(true);
            return;
        }

        if (block == null || block.getType() != Material.COBBLESTONE) {
            return;
        }

        new BukkitRunnable() {
            public void run() {
                PlayerUtils.giveItem(player, new ItemStack(block.getType()));
            }
        }.runTask(plugin);
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (!arena.hasPlayer(player)) {
            return;
        }

        arena.removePlayer(player, false);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerDeathEvent event) {
        Player player = event.getEntity();
        User user = plugin.getUser(player);

        if (!arena.hasPlayer(player)) {
            return;
        }

        arena.removePlayer(player, true);

        event.setDeathMessage(null);
        event.setDroppedExp(0);

        user.increaseStat(Stat.ARENADEATHS);

        ItemStack skull = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta skullMeta = skull.getItemMeta();
        skullMeta.setDisplayName("§6Golden Head");
        skullMeta.setLore(Arrays.asList(ChatColor.DARK_PURPLE + "Some say consuming the head of a", ChatColor.DARK_PURPLE + "fallen foe strengthens the blood."));
        skull.setItemMeta(skullMeta);

        event.getDrops().clear();
        event.getDrops().add(new ItemStack(Material.DIAMOND, 1));
        event.getDrops().add(new ItemStack(Material.ARROW, 32));
        event.getDrops().add(skull);

        Player killer = player.getKiller();

        if (killer == null) {
            if (arena.getScore(player.getName()) > 4) {
                PlayerUtils.broadcast(Arena.PREFIX + "§6" + player.getName() + "§7's killstreak of §a" + arena.getScore(player.getName()) + " §7was shut down by PvE!");
            }

            player.sendMessage(Arena.PREFIX + "You were killed by PvE!");
            arena.resetScore(player.getName());

            ExperienceOrb exp = player.getWorld().spawn(player.getLocation(), ExperienceOrb.class);
            exp.setExperience(7);
            return;
        }

        if (player.getLastDamageCause().getCause() == DamageCause.PROJECTILE) {
            PlayerUtils.giveItem(killer, event.getDrops().toArray(new ItemStack[event.getDrops().size()]));
            event.getDrops().clear();
        }

        if (arena.getScore(player.getName()) > 4) {
            PlayerUtils.broadcast(Arena.PREFIX + "§6" + player.getName() + "§7's killstreak of §a" + arena.getScore(player.getName()) + " §7was shut down by §6" + killer.getName() + "§7!");
        }

        player.sendMessage(Arena.PREFIX + "You were killed by §a" + killer.getName() + "§7! §8(" + NumberUtils.makePercent(killer.getHealth()) + "%§8)");
        killer.sendMessage(Arena.PREFIX + "You killed §a" + player.getName() + "§7!");

        if (!killer.isDead()) {
            arena.setScore(killer.getName(), arena.getScore(killer.getName()) + 1);
        }

        arena.resetScore(player.getName());

        killer.setLevel(killer.getLevel() + 1);
        User kUser = plugin.getUser(killer);

        kUser.increaseStat(Stat.ARENAKILLS);

        if (kUser.getStat(Stat.ARENAKILLSTREAK) < arena.getScore(killer.getName())) {
            kUser.setStat(Stat.ARENAKILLSTREAK, arena.getScore(killer.getName()));
        }

        String killstreak = String.valueOf(arena.getScore(killer.getName()));

        if (killstreak.equals("0")) {
            return;
        }

        if (killstreak.endsWith("0") || killstreak.endsWith("5")) {
            PlayerUtils.broadcast(Arena.PREFIX + "§6" + killer.getName() + " §7is now on a §a" + killstreak + " §7killstreak!");
        }
    }

    @EventHandler
    public void on(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();

        if (!arena.hasPlayer(player)) {
            return;
        }

        if (!(inv instanceof EnchantingInventory)) {
            return;
        }

        inv.setItem(1, THREE_LAPIS);
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();

        if (!arena.hasPlayer(player)) {
            return;
        }

        if (!(inv instanceof EnchantingInventory)) {
            return;
        }

        inv.setItem(1, null);
    }

    @EventHandler
    public void on(EnchantItemEvent event) {
        Inventory inv = event.getInventory();
        Player player = event.getEnchanter();

        if (!arena.hasPlayer(player)) {
            return;
        }

        inv.setItem(1, THREE_LAPIS);
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked(); // safe cast

        if (!arena.hasPlayer(player)) {
            return;
        }

        Inventory inv = event.getClickedInventory();

        if (!(inv instanceof EnchantingInventory)) {
            return;
        }

        if (event.getSlot() == 1) {
            event.setCancelled(true);
        }
    }
}