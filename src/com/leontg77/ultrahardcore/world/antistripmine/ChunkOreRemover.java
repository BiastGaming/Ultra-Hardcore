package com.leontg77.ultrahardcore.world.antistripmine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Chunk ore remover class.
 * 
 * @author XHawk87, modified by LeonTG77.
 */
public class ChunkOreRemover implements Runnable {
    private final AntiStripmine antiSM;
    private final WorldData data;

    private final int chunkX;
    private final int chunkZ;

    /**
     * Chunk ore remover class constructor.
     *
     * @param antiSM The anti stripmine class.
     * @param data The world data class for this chunk.
     * @param chunk The chunk.
     */
    public ChunkOreRemover(AntiStripmine antiSM, WorldData data, Chunk chunk) {
        this.antiSM = antiSM;
        this.data = data;

        this.chunkX = chunk.getX();
        this.chunkZ = chunk.getZ();
    }

    /**
     * Get the block in the given chunk on the given coords.
     *
     * @param chunk The chunk to check in.
     * @param dx The X level of the block.
     * @param y The Y level of the block.
     * @param dz The Z level of the block.
     *
     * @return The block if found, null otherwise.
     */
    private Block getBlock(Chunk chunk, int dx, int y, int dz) {
        if (y < 0 || y > 255) {
            return null;
        }

        if (dx >= 0 && dx <= 15 && dz >= 0 && dz <= 15) {
            return chunk.getBlock(dx, y, dz);
        }

        int x = chunk.getX() * 16 + dx;
        int z = chunk.getZ() * 16 + dz;

        if (chunk.getWorld().isChunkLoaded(x >> 4, z >> 4)) {
            return chunk.getWorld().getBlockAt(x, y, z);
        }

        return null;
    }

    /**
     * Allow all linked ores to exist as one of them is near air.
     *
     * @param block The block to check for linked ores.
     * @param toRemove All blocks that were supposed to be removed.
     * @param allowed A list of all blocks that are allowed to be there.
     */
    private void allowLinked(Block block, Map<Block, Set<Block>> toRemove, Set<Block> allowed) {
        if (!toRemove.containsKey(block)) {
            return;
        }

        Set<Block> linked = toRemove.get(block);
        toRemove.remove(block);
        allowed.add(block);

        for (Block link : linked) {
            allowLinked(link, toRemove, allowed); // check for linked ores to the linked ores.
        }
    }

    /**
     * Add all linked ores to the given vein list.
     *
     * @param block The block to start checking from.
     * @param toRemove The currently getting removed blocks.
     * @param vein The vein list to use.
     */
    private void addLinked(Block block, Map<Block, Set<Block>> toRemove, Set<Block> vein) {
        if (!toRemove.containsKey(block) || vein.contains(block)) {
            return;
        }

        vein.add(block);
        Set<Block> linked = toRemove.get(block);

        for (Block link : linked) { // loop for ores connected to the linked ores.
            if (link.getType().equals(block.getType())) {
                addLinked(link, toRemove, vein);
            }
        }
    }

    /**
     * Add the given type and size to the given remaining map.
     *
     * @param remaining The map to add the types to.
     * @param type The type of the block.
     * @param size The size of the vein.
     */
    private void addRemainingOres(Map<Material, Integer> remaining, Material type, int size) {
        if (remaining.containsKey(type)) {
            remaining.put(type, remaining.get(type) + size);
        } else {
            remaining.put(type, size);
        }
    }

    @Override
    public void run() {
        long started = System.nanoTime();
        boolean hasNoOres = true;

        Set<Material> ores = antiSM.getDefaultOres();

        Map<Block, Set<Block>> toRemove = new HashMap<Block, Set<Block>>();
        Set<Block> allowed = new HashSet<Block>();

        Chunk chunk = data.getWorld().getChunkAt(chunkX, chunkZ);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y <= antiSM.getMaxHeight(); y++) {
                    Block block = getBlock(chunk, x, y, z);

                    if (block == null) {
                        continue;
                    }

                    if (!ores.contains(block.getType())) {
                        continue;
                    }

                    hasNoOres = false;
                    Set<Block> nearOres = new HashSet<Block>();

                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                if (dx == 0 && dy == 0 && dz == 0) {
                                    continue; // I don't want it to use the block it started looping from.
                                }

                                Block near = getBlock(chunk, x + dx, y + dy, z + dz);

                                if (near == null) {
                                    continue;
                                }

                                // if there is a ore nearby, make sure to check if that one is also not near a cave.
                                if (ores.contains(near.getType())) {
                                    nearOres.add(near);
                                }

                                if (allowed.contains(near) || near.isEmpty() || near.isLiquid()) {
                                    allowed.add(block); // allow all ores near air or liquids
                                }
                            }
                        }
                    }

                    if (allowed.contains(block)) {
                        for (Block near : nearOres) {
                            allowLinked(near, toRemove, allowed);
                        }

                        toRemove.remove(block);
                    } else {
                        toRemove.put(block, nearOres);
                    }
                }
            }
        }

        Map<Material, Integer> remaining = new HashMap<Material, Integer>();

        while (!toRemove.isEmpty()) {
            Set<Block> vein = new HashSet<Block>();

            Block next = toRemove.keySet().iterator().next();
            addLinked(next, toRemove, vein);

            Material type = vein.iterator().next().getType();

            if (antiSM.getExcludedOres().contains(type)) {
                addRemainingOres(remaining, type, vein.size()); // log all excluded ores that were ignored.
            } else {
                for (Block block : vein) {
                    block.setType(antiSM.getOreReplacer());
                }
            }

            toRemove.keySet().removeAll(vein);
        }

        long duration = System.nanoTime() - started;

        if (hasNoOres && !data.hasNoOres(this)) {
            data.doASecondCheck(this);
        } else {
            data.logCompleted(this, duration, remaining);
        }
    }

    /**
     * Get the chunk used for this class.
     *
     * @return The chunk.
     */
    public Chunk getChunk() {
        return data.getWorld().getChunkAt(chunkX, chunkZ);
    }

    /**
     * Get the used chunk's X coordinate.
     *
     * @return The chunk X.
     */
    public int getChunkX() {
        return chunkX;
    }

    /**
     * Get the used chunk's Z coordinate.
     *
     * @return The chunk Z.
     */
    public int getChunkZ() {
        return chunkZ;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChunkOreRemover)) {
            return false;
        }

        ChunkOreRemover other = (ChunkOreRemover) obj;

        return (other.getChunkX() == getChunkX()) && (other.getChunkZ() == getChunkZ()) && (other.data.equals(data));
    }

    @Override
    public int hashCode() {
        int hash = 5;

        hash = 13 * hash + Objects.hashCode(data);
        hash = 13 * hash + getChunkX();
        hash = 13 * hash + getChunkZ();

        return hash;
    }

    @Override
    public String toString() {
        return getChunkX() + "," + getChunkZ();
    }
}