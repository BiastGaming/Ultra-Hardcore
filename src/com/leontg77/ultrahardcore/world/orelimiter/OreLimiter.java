package com.leontg77.ultrahardcore.world.orelimiter;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.events.ChunkModifiableEvent;
import com.leontg77.ultrahardcore.utils.BlockUtils;

/**
 * Ore Limiter class.
 * 
 * @author D4mnX
 */
public class OreLimiter implements Listener {
    protected static final Collection<Material> LIMITED_ORES = ImmutableList.of(
            Material.GOLD_ORE,
            Material.DIAMOND_ORE,
            Material.REDSTONE_ORE
    );

    public enum Type {
        /**
         * Impl. note:
         * Hosts shall not be able to use this type
         * from the world creator GUI / related.
         */
        NONE(0.0d, "§cDisabled.", "§7Ores are not limited."),
        /**
         * Impl. note:
         * Minor is displayed as "Disabled" as players will keep complaining.
         * Make sure to only limit within realm of "not noticable"
         */
        MINOR(0.2d, "§cDisabled.", "§7Ores are not limited."),
        LESS_VEINS(0.5d, "§aEnabled. §e(Less veins)", "§e50% §7of gold, diamond and", "§7redstone veins are §eremoved§7."),
        SMALLER_VEINS(0.0d, "§aEnabled. §e(Smaller veins)", "§7Gold, diamond and redstone", "§7spawn in veins of §e6 or less", "§7(instead of 8 or less).");

        private final double oreRemovalChance;
        private final String shortDescription;
        private final List<String> additionalLore;

        Type(double oreRemovalChance, String shortDescription, String... additionalLore) {
            this.oreRemovalChance = oreRemovalChance;
            this.shortDescription = shortDescription;
            this.additionalLore = ImmutableList.copyOf(additionalLore);
        }

        public double getOreRemovalChance() {
            return oreRemovalChance;
        }

        public String getShortDescription() {
            return shortDescription;
        }

        public List<String> getAdditionalLore() {
            return additionalLore;
        }
    }

    private final Random random = new Random();
    private final Settings settings;

    public OreLimiter(Settings settings) {
        this.settings = settings;
    }

    @EventHandler
    public void on(ChunkModifiableEvent event) {
        OreLimiter.Type oreLimiter = OreLimiter.Type.valueOf(settings.getWorlds().getString(
                event.getWorld().getName() + ".oreLimiter", OreLimiter.Type.NONE.name()));

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

                    if (!LIMITED_ORES.contains(type)) {
                        continue;
                    }

                    List<Block> vein = BlockUtils.getVein(block);
                    checked.addAll(vein);

                    if (random.nextDouble() > oreLimiter.getOreRemovalChance()) {
                        continue;
                    }

                    vein.forEach(veinBlock -> veinBlock.setType(Material.STONE));
                }
            }
        }
    }
}