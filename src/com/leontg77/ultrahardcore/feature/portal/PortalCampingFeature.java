package com.leontg77.ultrahardcore.feature.portal;

import org.bukkit.Material;

import com.leontg77.ultrahardcore.feature.ToggleableFeature;

/**
 * Portal camping feature class.
 * 
 * @author LeonTG77
 */
public class PortalCampingFeature extends ToggleableFeature {

    public PortalCampingFeature() {
        super("Portal Camping", "Players that wait at the portal for others to go thru.");

        icon.setType(Material.OBSIDIAN);
        slot = 44;
    }
}