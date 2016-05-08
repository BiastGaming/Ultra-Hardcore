package com.leontg77.ultrahardcore.feature.potions;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.ultrahardcore.feature.Feature;

/**
 * Nerfed Strength feature class.
 * 
 * @author LeonTG77
 */
public class NerfedStrengthFeature extends Feature implements Listener {

	public NerfedStrengthFeature() {
		super("Nerfed Strength", "Make strength do the same damage as in minecraft 1.5");
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		
		if (!(damager instanceof Player)) {
			return;
		}
		
		Player player = (Player) damager;
		
		if (!player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
			return;
		}
		
		for (PotionEffect effect : player.getActivePotionEffects()) {
			if (!effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
				continue;
			}
			
			int level = effect.getAmplifier() + 1;

			double newDamage = event.getDamage() / (level * 1.3D + 1.0D) + 3 * level;
			event.setDamage(newDamage);
			break;
		}
	}
}