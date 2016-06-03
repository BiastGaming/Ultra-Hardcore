package com.leontg77.ultrahardcore.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.leontg77.ultrahardcore.Main;

/**
 * A class with block releated utils methods.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("deprecation")
public class BlockUtils {
    private static Main plugin;

    /**
     * Set the plugin instance the BlockUtils class will use for scheduling tasks etc.
     *
     * @param plugin The main class.
     * @throws IllegalStateException If this method was already invoked
     */
    public static void setPlugin(Main plugin) {
        Preconditions.checkArgument(plugin != null, "Plugin cannot be null");
         Preconditions.checkState(BlockUtils.plugin == null, "BlockUtils already has a plugin instance set");

         BlockUtils.plugin = plugin;
    }

    private static final Random RANDOM = new Random();

    /**
     * Display the block breaking sound and particles.
     *
     * @param player The player who mined the block.
     * @param block The block that was broken.
     */
    public static void blockBreak(Player player, Block block) {
        for (Player online : block.getWorld().getPlayers()) {
            if (player != null && online == player) {
                continue; // do not play effect for breaker if any, because they already see/hear it.
            }

            online.playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
        }
    }

    /**
     * Drop the given item to drop on the given location as if a normal block
     * break would drop it.
     *
     * @param loc The location dropping at.
     * @param toDrop The item dropping.
     */
    public static void dropItem(Location loc, ItemStack toDrop) {
        new BukkitRunnable() {
            public void run() {
                Item item = loc.getWorld().dropItem(loc, toDrop);
                item.setVelocity(randomOffset());
            }
        }.runTaskLater(plugin, 2); // wait 2 ticks before dropping the item, so the block won't push the item.
    }

    /**
     * Make the given players item in hand lose 1 durability point and break if
     * out of durability.
     *
     * @param player The item owner.
     */
    public static void degradeDurabiliy(Player player) {
        ItemStack item = player.getItemInHand();

        // max durability doesn't count for blocks, only tools (except the bow?)
        if (item.getType() == Material.AIR || item.getType() == Material.BOW || item.getType().getMaxDurability() == 0) {
            return;
        }

        short durability = item.getDurability();

        // incase of unbreaking enchantment.
        if (item.containsEnchantment(Enchantment.DURABILITY)) {
            double chance = (100 / (item.getEnchantmentLevel(Enchantment.DURABILITY) + 1));

            if (RANDOM.nextDouble() <= (chance / 100)) {
                durability++;
            }
        } else {
            durability++;
        }

        if (durability >= item.getType().getMaxDurability()) {
            player.getWorld().playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1);
            player.setItemInHand(new ItemStack(Material.AIR));
            return;
        }

        item.setDurability(durability);
        player.setItemInHand(item);
    }

    /**
     * Get a random offset for item dropping.
     *
     * @return A vector with a random offset.
     */
    private static Vector randomOffset() {
        // don't ask me for why these numbers, I was just testing different
        // onces and these seemed to work the best.
        double offsetX = RANDOM.nextDouble() / 20;
        double offsetZ = RANDOM.nextDouble() / 20;

        offsetX = offsetX - (RANDOM.nextDouble() / 20);
        offsetZ = offsetZ - (RANDOM.nextDouble() / 20);

        return new Vector(offsetX, 0.2, offsetZ);
    }

    /**
     * Get the durability of the given block.
     *
     * @param block The block checking.
     * @return The durability of the block.
     */
    public static int getDurability(Block block) {
        return block.getState().getData().toItemStack().getDurability();
    }

    private static final int DEFAULT_VEIN_LIMIT = 100;

    /**
     * Get the ore vein at the given starting block's location.
     *
     * @param start The block to start from.
     * @return A list of the vein blocks.
     */
    public static List<Block> getVein(Block start) {
        Function<Block, Material> getType = block -> {
            Material type = block.getType();
            if (type == Material.GLOWING_REDSTONE_ORE) {
                type = Material.REDSTONE_ORE;
            }
            return type;
        };

        Material startType = getType.apply(start);
        return getVein(start, relative -> startType == getType.apply(relative), DEFAULT_VEIN_LIMIT);
    }

    /**
     * Get the ore vein at the given starting block's location.
     *
     * @param start The block to start from.
     * @return A list of the vein blocks.
     */
    public static List<Block> getVein(Block start, Predicate<Block> predicate, int maxVeinSize) {
        LinkedList<Block> toCheck = Lists.newLinkedList();
        ArrayList<Block> vein = Lists.newArrayList();

        toCheck.add(start);
        vein.add(start);

        while (!toCheck.isEmpty()) {
            Block check = toCheck.poll();

            for (Block nearbyBlock : getNearby(check)) {
                if (vein.contains(nearbyBlock)) {
                    continue;
                }

                if (!predicate.test(nearbyBlock)) {
                    continue;
                }

                toCheck.add(nearbyBlock);
                vein.add(nearbyBlock);

                if (vein.size() > maxVeinSize) {
                    return vein;
                }
            }
        }

        return vein;
    }

    public static List<Block> getNearby(Block block) {
        List<Block> nearby = Lists.newArrayList();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }

                    nearby.add(block.getRelative(dx, dy, dz));
                }
            }
        }
        return nearby;
    }


}