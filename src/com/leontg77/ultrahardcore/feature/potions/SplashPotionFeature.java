package com.leontg77.ultrahardcore.feature.potions;

import org.bukkit.Material;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;

/**
 * Splash Potions potion feature class.
 * 
 * @author LeonTG77
 */
public class SplashPotionFeature extends ToggleableFeature {
    private static final Material FEATURE_ITEM = Material.SULPHUR;

    private final PotionFuelListener listener;

    public SplashPotionFeature(PotionFuelListener listener) {
        super("Splash Potions", "Make potions throwable with gunpowder.");

        this.listener = listener;

        icon.setType(FEATURE_ITEM);
        slot = 52;
    }

    @Override
    public void onDisable() {
        listener.addMaterial(FEATURE_ITEM, Main.PREFIX + "Splash potions are disabled.");
    }

    @Override
    public void onEnable() {
        listener.removeMaterial(FEATURE_ITEM);
    }
}