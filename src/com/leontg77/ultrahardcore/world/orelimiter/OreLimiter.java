package com.leontg77.ultrahardcore.world.orelimiter;

import com.google.common.collect.Sets;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.utils.BlockUtils;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class OreLimiter implements Listener {

    protected static final TObjectDoubleMap<Material> ORE_RATES;
    static {
        ORE_RATES = new TObjectDoubleHashMap<>();
        ORE_RATES.put(Material.GOLD_ORE, 0.5d);
        ORE_RATES.put(Material.DIAMOND_ORE, 0.65d);
    }

    protected final Random random = new Random();
    protected final Settings settings;

    public OreLimiter(Settings settings) {
        this.settings = settings;
    }

    @EventHandler
    protected void on(ChunkPopulateEvent event) {
        World world = event.getWorld();
        if (!settings.getWorlds().getBoolean(world.getName() + ".oreLimiter", true)) {
            return;
        }

        Chunk chunk = event.getChunk();
        Set<Block> checked = Sets.newHashSet();

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 256; y++) {
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

                    if (random.nextDouble() > ORE_RATES.get(type)) {
                        continue;
                    }

                    vein.forEach(veinBlock -> veinBlock.setType(Material.STONE));
                }
            }
        }
    }
}
