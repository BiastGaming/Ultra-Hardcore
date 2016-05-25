package com.leontg77.ultrahardcore.feature.entity;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

/**
 * Merge Items feature class.
 * 
 * @author D4mnX
 */
public class MergeItemsFeature extends MergeFeatureBase {

    public MergeItemsFeature() {
        super("Merge Items", "Items of the same type are merged together when close.");

        icon.setType(Material.SLIME_BALL);
        slot = 21;
    }

    @Override
    protected void updateWorld(World world) {
        ((CraftWorld) world).getHandle().spigotConfig.itemMerge = isEnabled() ? 2.5d : 0d;
    }
}