package com.leontg77.ultrahardcore.scenario.scenarios.anonymous;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import com.google.common.base.Preconditions;

/**
 * @author ghowden, converted to from kotlin to java.
 * @see https://github.com/Eluinhost/anonymous
 */
public class Property {
    protected final String name;
    protected final String value;
    protected final String signature;

    public Property(String name, String value, String signature) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(value);
        Preconditions.checkNotNull(signature);

        this.name = name;
        this.value = value;
        this.signature = signature;
    }

    public boolean isValid() {
        return !name.isEmpty() && !value.isEmpty() && !signature.isEmpty();
    }

    public ConfigurationSection toYaml() {
        MemoryConfiguration section = new MemoryConfiguration();

        section.set("name", name);
        section.set("value", value);
        section.set("signature", signature);

        return section;
    }

    public static Property fromYaml(ConfigurationSection section) {
        return new Property(section.getString("name"), section.getString("value"), section.getString("signature"));
    }
}