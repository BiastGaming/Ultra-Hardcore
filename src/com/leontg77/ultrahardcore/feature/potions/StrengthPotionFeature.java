package com.leontg77.ultrahardcore.feature.potions;

import org.bukkit.Material;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;

/**
 * Strength Potions potion feature class.
 * 
 * @author LeonTG77
 */
public class StrengthPotionFeature extends ToggleableFeature {
    private static final Material FEATURE_ITEM = Material.BLAZE_POWDER;

    private final PotionFuelListener listener;

    public StrengthPotionFeature(PotionFuelListener listener) {
        super("Strength Potions", "A potion that makes your hits deal more damage.");

        this.listener = listener;

        icon.setType(FEATURE_ITEM);
        slot = 53;
    }

    @Override
    public void onDisable() {
        listener.addMaterial(FEATURE_ITEM, Main.PREFIX + "Strength potions are disabled.");
    }

    @Override
    public void onEnable() {
        listener.removeMaterial(FEATURE_ITEM);
    }
}