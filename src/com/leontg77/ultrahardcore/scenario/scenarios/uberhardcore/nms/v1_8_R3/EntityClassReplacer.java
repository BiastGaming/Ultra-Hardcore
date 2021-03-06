package com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.nms.v1_8_R3;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.common.collect.Lists;

import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityTypes;
import net.minecraft.server.v1_8_R3.EnumCreatureType;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class EntityClassReplacer implements com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.api.EntityClassReplacer {

    protected Map<Class<? extends Entity>, Integer> classToIdMapping;
    protected Map<Class<? extends Entity>, String> classToStringMapping;
    protected Map<Integer, Class<? extends Entity>> idToClassMapping;
    protected Map<String, Class<? extends Entity>> stringToClassMapping;
    protected List<BiomeBase.BiomeMeta> spawnLists;

    protected final Logger logger;

    public EntityClassReplacer(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void replaceClasses(Class current, Class replacement) {
        // doesn't currently exist
        if (!classToIdMapping.containsKey(current)) {
            logger.severe("Failed to replace " + current.getName() + " as it doesn't exist in NMS maps");
            return;
        }

        // get original ID and name
        int id = classToIdMapping.get(current);
        String name = classToStringMapping.get(current);

        // modify names <-> class
        stringToClassMapping.put(name, replacement); // overwrites original mapping
        classToStringMapping.remove(current); // remove original class
        classToStringMapping.put(replacement, name); // add our one in

        // modify id <-> class
        idToClassMapping.put(id, replacement); // overwrite original mapping
        classToIdMapping.remove(current); // remove original class
        classToIdMapping.put(replacement, id); // add our one in

        // string <-> id mapping requires no changes
        logger.info(String.format("Replaced %s with %s. ID: `%d` Name: `%s`", current.getName(), replacement.getName(), id, name));

        // replace spawns
        int count = 0;
        for (BiomeBase.BiomeMeta spawn : spawnLists) {
            if (spawn.b == current) {
                spawn.b = replacement;
                count++;
            }
        }

        logger.info(String.format("Replaced %d spawn entries for %s", count, current.getName()));
    }

    public void initialize() throws Exception {
        Field classToIdMappingField = EntityTypes.class.getDeclaredField("f");
        Field classToStringMappingField = EntityTypes.class.getDeclaredField("d");
        Field idToClassMappingField = EntityTypes.class.getDeclaredField("e");
        Field stringToClassMappingField = EntityTypes.class.getDeclaredField("c");

        classToIdMappingField.setAccessible(true);
        classToStringMappingField.setAccessible(true);
        idToClassMappingField.setAccessible(true);
        stringToClassMappingField.setAccessible(true);

        classToIdMapping = (Map<Class<? extends Entity>, Integer>) classToIdMappingField.get(null);
        classToStringMapping = (Map<Class<? extends Entity>, String>) classToStringMappingField.get(null);
        idToClassMapping = (Map<Integer, Class<? extends Entity>>) idToClassMappingField.get(null);
        stringToClassMapping = (Map<String, Class<? extends Entity>>) stringToClassMappingField.get(null);

        BiomeBase[] biomes = BiomeBase.getBiomes();

        // create one big list for easy replacements
        spawnLists = Lists.newArrayList();
        for (BiomeBase biome : biomes) {
            if (biome == null) continue;

            spawnLists.addAll(biome.getMobs(EnumCreatureType.CREATURE));
            spawnLists.addAll(biome.getMobs(EnumCreatureType.AMBIENT));
            spawnLists.addAll(biome.getMobs(EnumCreatureType.WATER_CREATURE));
            spawnLists.addAll(biome.getMobs(EnumCreatureType.MONSTER));
        }
    }
}
