package com.leontg77.ultrahardcore.managers;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.utils.LocationUtils;

/**
 * Firework randomizer class.
 * <p>
 * This class contains methods for launching random fireworks, getting a random type and a random color.
 * 
 * @author LeonTG77
 */
public class FireworkManager {
	private final Main plugin;
	
	/**
	 * Firework manager class constructor.
	 * 
	 * @param plugin The main class.
	 */
	public FireworkManager(Main plugin) {
		this.plugin = plugin;
	}
	
	private final Random rand = new Random();

	/**
	 * Launch an random firework at the given location.
	 * 
	 * @param loc the location launching at.
	 */
	public void launchRandomFirework(Location loc) {
		Firework item = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta meta = item.getFireworkMeta();

		Stream.generate(() -> {
			Builder builder = FireworkEffect.builder();

			builder.flicker(rand.nextBoolean());
			builder.trail(rand.nextBoolean());
			builder.withColor(Stream.generate(this::randomColor).limit(rand.nextInt(5) + 1).collect(Collectors.toList()));
			builder.with(randomType());

			if (rand.nextBoolean()) {
				builder.withFade(Stream.generate(this::randomColor).limit(rand.nextInt(5) + 1).collect(Collectors.toList()));
			}

			return builder.build();
		}).limit(rand.nextInt(2) + 1).forEach(meta::addEffect);

		meta.setPower(rand.nextInt(2) + 1);
		item.setFireworkMeta(meta);
	}
	
	/**
	 * Launch fireworks around the spawn.
	 */
	public void startFireworkShow() {
		startFireworkShow(plugin.getSpawn());
	}
	
	/**
	 * Launch fireworks around the given location.
	 * 
	 * @param locToUse The location to use.
	 */
	public void startFireworkShow(final Location locToUse) {
		new BukkitRunnable() {
			int i = 0;
			
			public void run() {
				int x = rand.nextInt(50 * 2) - 50;
				int z = rand.nextInt(50 * 2) - 50;

				Location loc = locToUse.clone().add(x, 0, z);
				loc.setY(LocationUtils.getHighestBlock(loc).getY());
				
				launchRandomFirework(loc.add(0, 1, 0));
				
				i++;
				
				if (i == 200) {
					cancel();
				}
			}
		}.runTaskTimer(plugin, 20, 5);
	}
	
	/**
	 * Gets an random color.
	 * 
	 * @return A random color.
	 */
	private Color randomColor() {
		return Color.fromBGR(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
	}

	/**
	 * Gets an random firework type.
	 * 
	 * @return A random firework type.
	 */
	private Type randomType() {
		return Type.values()[rand.nextInt(Type.values().length)];
	}
}