package com.leontg77.ultrahardcore.feature.entity;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public class MergeExpFeature extends MergeFeatureBase {

    public MergeExpFeature() {
        super("Merge EXP", "Nearby EXP orbs are merged together when close.");

        icon.setType(Material.EXP_BOTTLE);
        slot = 22;
    }

    @Override
    protected void updateWorld(World world) {
        ((CraftWorld) world).getHandle().spigotConfig.expMerge = isEnabled() ? 3.0d : 0d;
    }
}
