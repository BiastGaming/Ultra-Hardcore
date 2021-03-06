package com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.nms.v1_8_R3;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.EntityGhast;
import net.minecraft.server.v1_8_R3.EnumCreatureType;

public class NewSpawnsModifier implements com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.api.NewSpawnsModifier {

    protected List<BiomeBase> skipped;

    public void setup() {
        skipped = Lists.newArrayList();

        outer:
        for (BiomeBase biome : BiomeBase.getBiomes()) {
            if (biome == null) continue;

            List<BiomeBase.BiomeMeta> spawns = biome.getMobs(EnumCreatureType.MONSTER);

            // skip if it already has a ghast entry
            for (BiomeBase.BiomeMeta meta : spawns) {
                if (meta.b == EntityGhast.class) {
                    skipped.add(biome);
                    continue outer;
                }
            }

            spawns.add(new BiomeBase.BiomeMeta(EntityGhast.class, 50, 4, 4));
        }
    }

    public void desetup() {
        for (BiomeBase biome : BiomeBase.getBiomes()) {
            if (biome == null || skipped.contains(biome)) continue;

            Iterator<BiomeBase.BiomeMeta> iterator = biome.getMobs(EnumCreatureType.MONSTER).iterator();

            while(iterator.hasNext()) {
                BiomeBase.BiomeMeta meta = iterator.next();

                if (meta.b == EntityGhast.class) {
                    iterator.remove();
                }
            }
        }
    }
}
