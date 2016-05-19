package com.leontg77.ultrahardcore.utils;

import org.bukkit.potion.PotionEffectType;

/**
 * Name utilities class.
 * <p>
 * Contains methods for capitalizing strings and getting potion real names.
 * 
 * @author LeonTG77
 */
public class NameUtils {

    /**
     * Fix the given text with making the first letter captializsed and the rest not.
     *
     * @param text the text fixing.
     * @param replaceUnderscore True to replace all _ with a space, false otherwise.
     * @return The new fixed text.
     */
    public static String capitalizeString(final String text, final boolean replaceUnderscore) {
        if (text.isEmpty()) {
            return text;
        }

        if (text.length() == 1) {
            return text.toUpperCase();
        }

        String toReturn = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();

        if (replaceUnderscore) {
            toReturn = toReturn.replace("_", " ");
        }

        return toReturn;
    }

    /**
     * Get the real potion name of the given potion type.
     *
     * @param type the type.
     * @return The real potion name.
     */
    public static String getPotionName(final PotionEffectType type) {
        switch (type.getName().toLowerCase()) {
        case "speed":
            return "Speed";
        case "slow":
            return "Slowness";
        case "fast_digging":
            return "Haste";
        case "slow_digging":
            return "Mining fatigue";
        case "increase_damage":
            return "Strength";
        case "heal":
            return "Instant Health";
        case "harm":
            return "Instant Damage";
        case "jump":
            return "Jump Boost";
        case "confusion":
            return "Nausea";
        case "regeneration":
            return "Regeneration";
        case "damage_resistance":
            return "Resistance";
        case "fire_resistance":
            return "Fire Resistance";
        case "water_breathing":
            return "Water breathing";
        case "invisibility":
            return "Invisibility";
        case "blindness":
            return "Blindness";
        case "night_vision":
            return "Night Vision";
        case "hunger":
            return "Hunger";
        case "weakness":
            return "Weakness";
        case "poison":
            return "Poison";
        case "wither":
            return "Wither";
        case "health_boost":
            return "Health Boost";
        case "absorption":
            return "Absorption";
        case "saturation":
            return "Saturation";
        default:
            return "???";
        }
    }
}