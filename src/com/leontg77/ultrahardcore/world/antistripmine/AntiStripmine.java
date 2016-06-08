package com.leontg77.ultrahardcore.world.antistripmine;

import java.util.List;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.events.ChunkModifiableEvent;
import com.leontg77.ultrahardcore.utils.BlockUtils;

public class AntiStripmine implements Listener {

    protected static final Set<Material> DEFAULT_ORES = ImmutableSet.of(
            Material.COAL_ORE, Material.IRON_ORE, Material.REDSTONE_ORE, Material.EMERALD_ORE,
            Material.DIAMOND_ORE, Material.GOLD_ORE, Material.LAPIS_ORE
    );

    protected static final Set<Material> EXCLUDED_ORES = ImmutableSet.of();

    private final Settings settings;

    public AntiStripmine(Settings settings) {
        this.settings = settings;
    }

    @EventHandler
    public void on(ChunkModifiableEvent event) {
        if (!settings.getWorlds().getBoolean(event.getWorld().getName() + ".antiStripmine", true)) {
            return;
        }

        Chunk chunk = event.getChunk();

        Set<Block> checked = Sets.newHashSet();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 128; y++) {
                    Block block = chunk.getBlock(x, y, z);

                    if (checked.contains(block)) {
                        continue;
                    }

                    Material type = block.getType();
                    
                    if (!DEFAULT_ORES.contains(type)) {
                        continue;
                    }

                    List<Block> anyOreVein = BlockUtils.getVein(block,
                            relative -> DEFAULT_ORES.contains(relative.getType()), Integer.MAX_VALUE);

                    checked.addAll(anyOreVein);

                    Set<Block> nearbyBlocks = Sets.newHashSet();
                    anyOreVein.stream()
                            .map(BlockUtils::getNearby)
                            .forEach(nearbyBlocks::addAll);

                    // BlockUtils#getNearby on a vein will obviously catch the other vein blocks too,
                    // so remove all vein blocks from the nearby block set.
                    nearbyBlocks.removeAll(anyOreVein);

                    boolean nearbyEmptyOrLiquid = nearbyBlocks.stream()
                            .anyMatch(near -> near.isEmpty() || near.isLiquid());

                    if (nearbyEmptyOrLiquid) {
                        continue;
                    }

                    anyOreVein.stream()
                            .filter(ore -> !EXCLUDED_ORES.contains(ore.getType()))
                            .forEach(ore -> ore.setType(Material.STONE));
                }
            }
        }
    }
}
