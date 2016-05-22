package com.leontg77.ultrahardcore.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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

    private static final int VEIN_LIMIT = 100;

    /**
     * Get the ore vein at the given starting block's location.
     *
     * @param start The block to start from.
     * @return A list of the vein blocks.
     */
    public static List<Block> getVein(Block start) {
        LinkedList<Block> toCheck = Lists.newLinkedList();
        ArrayList<Block> vein = Lists.newArrayList();
        
        toCheck.add(start);
        
        Material startType = start.getType();
        
        if (startType == Material.GLOWING_REDSTONE_ORE) {
            startType = Material.REDSTONE_ORE;
        }
        
        while (!toCheck.isEmpty()) {
            Block check = toCheck.poll();
            
            for (BlockFace blockFace : BlockFace.values()) {
                Block relative = check.getRelative(blockFace);
                
                if (vein.contains(relative)) {
                    continue;
                }

                Material relativeType = relative.getType();
                
                if (relativeType == Material.GLOWING_REDSTONE_ORE) {
                    relativeType = Material.REDSTONE_ORE;
                }

                if (!relativeType.equals(startType)) {
                    continue;
                }

                vein.add(relative);
                toCheck.add(relative);

                if (vein.size() > VEIN_LIMIT) {
                    plugin.getLogger().warning("Tried to get a vein at " + start.getLocation().toString() + "(block type: " + relativeType + ") but hit the vein max size!");
                    return vein;
                }
            }
        }

        return vein;
    }
}