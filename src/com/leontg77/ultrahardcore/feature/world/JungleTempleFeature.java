package com.leontg77.ultrahardcore.feature.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import com.leontg77.ultrahardcore.events.ChunkModifiableEvent;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.utils.LocationUtils;

/**
 * Jungle Temples feature class.
 * 
 * @author LeonTG77
 */
public class JungleTempleFeature extends Feature implements Listener {
    private final Random rand = new Random();

    private final List<Location> templeBlocks = new ArrayList<Location>();

    private final Location start;
    private final Location end;

    public JungleTempleFeature() {
        super("Jungle Temples", "Generates jungle temples in the modified arctic jungles.");

        World world = Bukkit.getWorld("lobby");

        start = new Location(world, 333, 25, 3);
        end = new Location(world, 347, 38, 14);

        for (int x = start.getBlockX(); x <= end.getBlockX(); x++) {
            for (int y = start.getBlockY(); y <= end.getBlockY(); y++) {
                for (int z = start.getBlockZ(); z <= end.getBlockZ(); z++) {
                    templeBlocks.add(new Location(world, x, y, z));
                }
            }
        }
    }

    @EventHandler
    public void on(ChunkModifiableEvent event) {
        Chunk chunk = event.getChunk();

        int x = rand.nextInt(16);
        int z = rand.nextInt(16);

        Block block = chunk.getBlock(x, 0, z);
        block = LocationUtils.getHighestBlock(block.getLocation()).getBlock();

        if (block.getBiome() != Biome.JUNGLE_EDGE) {
            return;
        }

        if (rand.nextInt(300) >= 1) {
            return;
        }

        if (block.getType() != Material.GRASS) {
            block = block.getRelative(BlockFace.DOWN);

            if (block.getType() != Material.GRASS) {
                return;
            }
        }

        Location loc = block.getLocation();
        generateTemple(loc.clone().subtract(x, 3, z));
    }

    @SuppressWarnings("deprecation")
    private void generateTemple(Location loc) {
        int diffX = end.getBlockX() - start.getBlockX();
        int diffY = end.getBlockY() - start.getBlockY();
        int diffZ = end.getBlockZ() - start.getBlockZ();

        int i = 0;

        for (int x = 0; x <= diffX; x++) {
            for (int y = diffY; y >= 0; y--) {
                for (int z = 0; z <= diffZ; z++) {
                    Location current = new Location(loc.getWorld(), loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);
                    Location tLoc = templeBlocks.get(i);
                    i++;

                    if (tLoc == null) {
                        continue;
                    }

                    if (!tLoc.getChunk().isLoaded()) {
                        tLoc.getChunk().load(true);
                    }

                    Block cBlock = current.getBlock();
                    Block block = tLoc.getBlock();

                    if (block.getType() == Material.BARRIER) {
                        continue;
                    }

                    cBlock.setType(block.getType() == Material.COBBLESTONE ? (rand.nextBoolean() ? Material.COBBLESTONE : Material.MOSSY_COBBLESTONE) : block.getType());;
                    cBlock.setData(block.getData());
                    cBlock.getState().update(true);

                    if (cBlock.getState() instanceof Chest) {
                        addLoot((Chest) cBlock.getState());
                    }

                    if (cBlock.getState() instanceof Dispenser) {
                        addLoot((Dispenser) cBlock.getState());
                    }
                }
            }
        }
    }

    /**
     * Add jungle temple loot to the given chest.
     * 
     * @param chest The chest to add it to.
     */
    private void addLoot(Chest chest) {
        int rolls = rand.nextInt(5) + 2; // 2-6 rolls (aka 2-6 items)

        for (int i = 0; i < rolls; i++) {
            int slot = rand.nextInt(chest.getInventory().getSize());

            chest.getInventory().setItem(slot, randomJungleTempleLoot());
        }
    }

    /**
     * Add 2-7 arrows to the given dispenser instance.
     *
     * @param dis The dispenser to add it to.
     */
    private void addLoot(Dispenser dis) {
        int rolls = rand.nextInt(2) + 1; // 1-2 rolls (aka 1-2 items)

        for (int i = 0; i < rolls; i++) {
            int slot = rand.nextInt(dis.getInventory().getSize());

            dis.getInventory().setItem(slot, new ItemStack(Material.ARROW, rand.nextInt(6) + 2));
        }
    }

    /**
     * Get a random item representing random jungle temple loot.
     *
     * @return The random jungle temple loot.
     */
    private ItemStack randomJungleTempleLoot() { // all of these rates are taken from the minecraft wiki
        double randomPercentage = rand.nextDouble() * 100;

        // chance of 4.11% of 1-3 diamonds
        if (randomPercentage < 4.11D) {
            return new ItemStack(Material.DIAMOND, rand.nextInt(3) + 1);
        }
        randomPercentage -= 4.11D;

        // chance of 13.7% for 1-5 iron ingots
        if (randomPercentage < 13.7D) {
            return new ItemStack(Material.IRON_INGOT, rand.nextInt(5) + 1);
        }
        randomPercentage -= 13.7D;

        // chance of 20.55% for 2-7 gold ingots
        if (randomPercentage < 20.55D) {
            return new ItemStack(Material.GOLD_INGOT, rand.nextInt(6) + 2);
        }
        randomPercentage -= 20.55D;

        // chance of 2.74% for 1-3 emeralds
        if (randomPercentage < 2.74D) {
            return new ItemStack(Material.EMERALD, rand.nextInt(3) + 1);
        }
        randomPercentage -= 2.74D;

        // chance of 27.4% for 4-6 bones
        if (randomPercentage < 27.4D) {
            return new ItemStack(Material.BONE, rand.nextInt(3) + 4);
        }
        randomPercentage -= 27.4D;

        // chance of 21.92% for 3-7 rotten flesh
        if (randomPercentage < 21.92D) {
            return new ItemStack(Material.ROTTEN_FLESH, rand.nextInt(5) + 3);
        }
        randomPercentage -= 21.92D;

        // chance of 4.11% for 1 saddle
        if (randomPercentage < 4.11D) {
            return new ItemStack(Material.SADDLE, 1);
        }
        randomPercentage -= 4.11D;

        // chance of 1.37% for 1 iron horse armor
        if (randomPercentage < 1.37D) {
            return new ItemStack(Material.IRON_BARDING, 1);
        }
        randomPercentage -= 1.37D;

        // chance of 1.37% for 1 gold horse armor
        if (randomPercentage < 1.37D) {
            return new ItemStack(Material.GOLD_BARDING, 1);
        }
        randomPercentage -= 1.37D;

        // chance of 1.37% for 1 diamond horse armor
        if (randomPercentage < 1.37D) {
            return new ItemStack(Material.DIAMOND_BARDING, 1);
        }
        randomPercentage -= 1.37D;

        // chance of 1.37& (all left over) for a level 30 enchanted book
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK, 1);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();

        Enchantment ench = Enchantment.values()[rand.nextInt(Enchantment.values().length)];
        int level = rand.nextInt(ench.getMaxLevel()) + 1;

        meta.addStoredEnchant(ench, level, true);
        item.setItemMeta(meta);
        return item; 
    }
}