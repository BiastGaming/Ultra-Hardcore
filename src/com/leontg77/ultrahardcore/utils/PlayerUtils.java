package com.leontg77.ultrahardcore.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.leontg77.ultrahardcore.Main;

/**
 * Player utilities class.
 * <p>
 * Contains methods for broadcasting messages, giving items, getting nearby entities and offline players.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class PlayerUtils {

    /**
     * Gets an offline player by a name.
     * <p>
     * This is just because of the deprecation on <code>Bukkit.getOfflinePlayer(String)</code>
     *
     * @param name The name.
     * @return the offline player.
     */
    public static OfflinePlayer getOfflinePlayer(String name) {
        return Bukkit.getOfflinePlayer(name);
    }

    /**
     * Broadcasts a message to everyone online.
     *
     * @param message the message.
     */
    public static void broadcast(String message) {
        broadcast(message, null);
    }

    /**
     * Broadcasts a message to everyone online with a specific permission.
     *
     * @param message the message.
     * @param permission the permission.
     */
    public static void broadcast(String message, String permission) {
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (permission != null && !online.hasPermission(permission)) {
                continue;
            }

            online.sendMessage(message);
        }

        message = message.replaceAll("§l", "");
        message = message.replaceAll("§o", "");
        message = message.replaceAll("§r", "§f");
        message = message.replaceAll("§m", "");
        message = message.replaceAll("§n", "");

        Bukkit.getLogger().info(message);
    }

    /**
     * Damage the given player by the given amount
     * <p>
     * This will also call the damage event.
     *
     * @param player The player to damage.
     * @param amount The amount of damage.
     */
    public static void damage(Player player, double amount) {
        EntityDamageEvent event = new EntityDamageEvent(player, DamageCause.CUSTOM, amount);

        Bukkit.getPluginManager().callEvent(event);
        player.damage(event.getDamage());
    }

    /**
     * Get a list of entites within a distance of a location.
     *
     * @param loc the location.
     * @param distance the distance.
     * @return A list of entites nearby.
     */
    public static List<Entity> getNearby(Location loc, double distance) {
        final List<Entity> list = new ArrayList<Entity>();

        for (Entity entity : loc.getWorld().getEntities()) {
            if (entity instanceof Player) {
                continue;
            }

            if (!entity.getType().isAlive()) {
                continue;
            }

            if (loc.distance(entity.getLocation()) > distance) {
                continue;
            }

            list.add(entity);
        }

        for (Player online : loc.getWorld().getPlayers()) {
            if (loc.distance(online.getLocation()) > distance) {
                continue;
            }

            list.add(online);
        }

        return list;
    }

    /**
     * Give the given item to the given player.
     * <p>
     * Method is made so if the inventory is full it drops the item to the ground.
     *
     * @param player the player giving to.
     * @param stack the item giving.
     */
    public static void giveItem(final Player player, final ItemStack... stacks) {
        final PlayerInventory inv = player.getInventory();
        final Map<Integer, ItemStack> leftOvers = inv.addItem(stacks);

        if (leftOvers.isEmpty()) {
            return;
        }

        player.sendMessage(Main.PREFIX + "Your inventory was full, item was dropped on the ground.");
        final Location loc = player.getLocation();

        for (ItemStack leftOver : leftOvers.values()) {
            BlockUtils.dropItem(loc, leftOver);
        }
    }
}