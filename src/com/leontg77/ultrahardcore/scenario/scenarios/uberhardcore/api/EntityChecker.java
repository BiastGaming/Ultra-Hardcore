package com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.api;

import org.bukkit.entity.Entity;

@SuppressWarnings({ "rawtypes" })
public interface EntityChecker {
    /**
     * Checks if the given bukkit entity is wrapping the given class EXACTLY (==)
     *
     * @param entity the entity to check
     * @param klass the class to check for
     *
     * @return true if wrapping an entity of the exact class
     */
    boolean isEntityOfClassExact(Entity entity, Class klass);
}
