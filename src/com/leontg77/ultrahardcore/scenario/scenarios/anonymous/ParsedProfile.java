package com.leontg77.ultrahardcore.scenario.scenarios.anonymous;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

/**
 * @author ghowden, converted to from kotlin to java.
 * @see https://github.com/Eluinhost/anonymous
 */
public class ParsedProfile {
    protected final String id;
    protected final String name;
    protected final List<Property> properties;

    public ParsedProfile(String id, String name, List<Property> properties) {
        this.id = id;
        this.name = name;
        this.properties = properties;
    }

    public boolean isValid() {
        return !name.isEmpty() && !id.isEmpty() && properties.stream().allMatch(Property::isValid);
    }

    public ConfigurationSection toYaml() {
        MemoryConfiguration section = new MemoryConfiguration();
 
        section.set("id", id);
        section.set("name", name);

        ConfigurationSection propertiesSection = section.createSection("properties");
        properties.forEach(it -> propertiesSection.set(it.name, it.toYaml()));

        return section;
    }

    public static ParsedProfile fromYaml(ConfigurationSection section) {
        ConfigurationSection propertiesSection = section.getConfigurationSection("properties");

        return new ParsedProfile(
            section.getString("id"),
            section.getString("name"),
            propertiesSection
                .getKeys(false)
                .stream()
                .map(propertiesSection::getConfigurationSection)
                .map(Property::fromYaml)
                .collect(Collectors.toList())
        );
    }
}