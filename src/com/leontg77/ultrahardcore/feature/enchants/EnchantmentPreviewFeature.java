package com.leontg77.ultrahardcore.feature.enchants;

import org.bukkit.Material;

import com.leontg77.ultrahardcore.feature.ToggleableFeature;
import com.leontg77.ultrahardcore.protocol.EnchantPreview;

/**
 * Enchant Preview feature class.
 * 
 * @author LeonTG77
 */
public class EnchantmentPreviewFeature extends ToggleableFeature {
    private final EnchantPreview preview;

    /**
     * Enchant Preview feature class constructor.
     *
     * @param preview The enchantment preview protocol class.
     */
    public EnchantmentPreviewFeature(EnchantPreview preview) {
        super("Enchant Preview", "A preview of 1 of the enchants you'll get on your enchanting weapon.");

        icon.setType(Material.ENCHANTMENT_TABLE);
        slot = 47;

        this.preview = preview;
    }

    @Override
    public void onDisable() {
        preview.enable();
    }

    @Override
    public void onEnable() {
        preview.disable();
    }
}