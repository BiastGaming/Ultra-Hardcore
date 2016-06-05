package com.leontg77.ultrahardcore.world.orelimiter;

import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.google.common.collect.Sets;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.events.ChunkModifiableEvent;
import com.leontg77.ultrahardcore.utils.BlockUtils;

import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;

/**
 * Ore Limiter class.
 * 
 * @author D4mnX
 */
public class OreLimiter implements Listener {
    protected static final TObjectDoubleMap<Material> ORE_RATES;
    
    static {
        ORE_RATES = new TObjectDoubleHashMap<>();
        ORE_RATES.put(Material.GOLD_ORE, 0.35d);
        ORE_RATES.put(Material.DIAMOND_ORE, 0.5d);
        ORE_RATES.put(Material.REDSTONE_ORE, 0.25d);
    }

    private final Random random = new Random();
    private final Settings settings;

    public OreLimiter(Settings settings) {
        this.settings = settings;
    }

    @EventHandler
    public void on(ChunkModifiableEvent event) {
        if (!settings.getWorlds().getBoolean(event.getWorld().getName() + ".oreLimiter", true)) {
            return;
        }

        Chunk chunk = event.getChunk();
        Set<Block> checked = Sets.newHashSet();

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 32; y++) {
                for (int z = 0; z < 16; z++) {
                    Block block = chunk.getBlock(x, y, z);

                    if (checked.contains(block)) {
                        continue;
                    }

                    Material type = block.getType();

                    if (!ORE_RATES.containsKey(type)) {
                        continue;
                    }

                    List<Block> vein = BlockUtils.getVein(block);
                    checked.addAll(vein);

                    if (ORE_RATES.get(type) > random.nextDouble()) {
                        continue;
                    }

                    vein.forEach(veinBlock -> veinBlock.setType(Material.STONE));
                }
            }
        }
    }
}