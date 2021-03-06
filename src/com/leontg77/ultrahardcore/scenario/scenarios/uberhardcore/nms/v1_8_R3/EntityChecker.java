package com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.nms.v1_8_R3;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

@SuppressWarnings({ "rawtypes" })
public class EntityChecker implements com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.api.EntityChecker {
    @Override
    public boolean isEntityOfClassExact(Entity entity, Class klass) {
        return ((CraftEntity) entity).getHandle().getClass() == klass;
    }
}
