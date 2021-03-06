package com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore;

import org.bukkit.World;
import org.bukkit.entity.Entity;

import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.api.EntityChecker;

public class EntityKiller {

    protected final EntityChecker entityChecker;

    public EntityKiller(EntityChecker entityChecker) {
        this.entityChecker = entityChecker;
    }

    public void killEntitiesInWorld(Class<?> klass, World world) {
        for (Entity entity : world.getEntities()) {
            if (entityChecker.isEntityOfClassExact(entity, klass)) {
                entity.remove();
            }
        }
    }
}
