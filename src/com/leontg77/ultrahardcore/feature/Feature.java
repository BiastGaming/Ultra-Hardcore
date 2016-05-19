package com.leontg77.ultrahardcore.feature;

/**
 * A feature of the plugin.
 * 
 * @author LeonTG77
 */
public abstract class Feature {
    private String description;
    private String name;

    /**
     * Feature class constructor
     *
     * @param name The name of the feature.
     * @param description A short description of the feature.
     */
    public Feature(String name, String description) {
        this.description = description;
        this.name = name;
    }

    /**
     * Get the name of the feature
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get a description of the feature.
     *
     * @return The description.
     */
    public String getDescription() {
        return description;
    }
}