package com.leontg77.ultrahardcore.feature.entity;

import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.ultrahardcore.feature.ToggleableFeature;

/**
 * Witch Health Potion feature class.
 *  
 * @author LeonTG77
 */
public class WitchHealthPotionFeature extends ToggleableFeature implements Listener {
    private static final short POTION_DATA = 8261;

    public WitchHealthPotionFeature() {
        super("Witch Health Potion", "Make witches drop health pots 30% of the time but 100% when you are poisoned.");

        icon.setType(Material.POTION);
        icon.setDurability(POTION_DATA);

        slot = 36;
    }

    @EventHandler
    public void on(EntityDeathEvent event) {
        if (!isEnabled()) {
            return;
        }

        final LivingEntity entity = event.getEntity();
        final Player killer = entity.getKiller();

        if (!(entity instanceof Witch)) {
            return;
        }

        final ItemStack potion = new ItemStack (Material.POTION, 1, POTION_DATA);
        final List<ItemStack> drops = event.getDrops();

        if (killer == null) {
            return;
        }

        for (ItemStack drop : drops) {
            if (drop.getType() == Material.POTION && drop.getDurability() == POTION_DATA) {
                drop.setType(Material.AIR);
            }
        }

        drops.remove(potion);

        if (killer.hasPotionEffect(PotionEffectType.POISON)) {
            drops.add(potion);
            return;
        }

        final Random rand = new Random();

        if (rand.nextDouble() >= 0.3) {
            return;
        }

        drops.add(potion);
    }
}