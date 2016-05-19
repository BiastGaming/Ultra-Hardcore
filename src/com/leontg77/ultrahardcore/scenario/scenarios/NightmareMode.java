package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * NightmareMode scenario class.
 * 
 * @author LeonTG77
 */
public class NightmareMode extends Scenario implements Listener {
	
	public NightmareMode() {
		super(
		    "NightmareMode", 
		    "Creepers stalk at twice the normal speed. When a creeper explodes, several of its angry larvae (silverfish) will burst out of its corpse. " +
		    "Even if a creeper is murdered, there's a chance one of the larvae will survive. Skeletons tip their arrows with cave spider blood, causing weak poison on contact. " +
		    "All zombies spawn with extra strength. Their flesh has become slightly fire resistant, allowing them to play in sunlight for much longer than normal. " +
		    "Even when their flesh falls off, they'll continue to attack as a fast-maneuvering skeleton using melee. " +
		    "Spiders are unimaginably quick and have developed a thirst for human blood. When a spider dies, its silk gland ruptures, causing webs and blood to spray over the surrounding area. " +
		    "After visiting the Nether, the endermen have mutated. Any player attacked by one will be instantly ignited. When an enderman is damaged, it has a chance to summon a blaze as backup. " +
		    "Witches approach their victims silently at a horrifying velocity."
		);
	}
	
	private final Random rand = new Random();
	
	@Override
	public void onEnable() {
		for (World world : Bukkit.getWorlds()) {
			for (LivingEntity entity : world.getLivingEntities()) {
				on(new CreatureSpawnEvent(entity, SpawnReason.NATURAL)); // idk what to put for spawn reason, the method doesn't use it anyways.
			}
		}
	}
	
	@EventHandler
	public void on(CreatureSpawnEvent event) {
		LivingEntity entity = event.getEntity();
		
		switch (entity.getType()) {
		case WITCH:
			entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, NumberUtils.TICKS_IN_999_DAYS, 4));
			break;
		case SPIDER:
			entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, NumberUtils.TICKS_IN_999_DAYS, 3));
			break;
		case ZOMBIE:
			entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, NumberUtils.TICKS_IN_999_DAYS, 1));
			break;
		case CREEPER:
			entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, NumberUtils.TICKS_IN_999_DAYS, 1));
			break;
		default:
			break;
		}
	}
	
	@EventHandler
	public void on(EntityDamageEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		DamageCause cause = event.getCause();
		Entity entity = event.getEntity();
		
		switch (entity.getType()) {
		case ZOMBIE:
			if (cause != DamageCause.FIRE && cause != DamageCause.FIRE_TICK) {
				return;
			}

			event.setCancelled(rand.nextBoolean());
			break;
		case ENDERMAN:
			if (rand.nextDouble() > 0.10) {
				return;
			}
			
			entity.getWorld().spawn(entity.getLocation(), Blaze.class);
			break;
		default:
			break;
		}
	}
	
	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		Entity damager = event.getDamager();
		Entity entity = event.getEntity();
		
		if (!(entity instanceof Player)) {
			return;
		}
		
		Player player = (Player) entity;
		
		if (damager instanceof Arrow) {
			Arrow arrow = (Arrow) damager;
			
			if (arrow.getShooter() instanceof Skeleton) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 1));
			}
			return;
		}
		
		if (damager instanceof Enderman) {
			player.setFireTicks(60);
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		Entity entity = event.getEntity();
		
		if (!(entity instanceof Creeper)) {
			return;
		}
		
		for (int i = 0; i < 5; i++) {
			entity.getWorld().spawn(entity.getLocation(), Silverfish.class);
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		LivingEntity entity = event.getEntity();
		
		if (entity instanceof Creeper) {
			if (rand.nextBoolean()) {
				entity.getWorld().spawn(entity.getLocation(), Silverfish.class);
			}
			return;
		} 
		
		if (entity instanceof Zombie) {
			Skeleton skelly = entity.getWorld().spawn(entity.getLocation(), Skeleton.class);
			skelly.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1726272000, 0));
			skelly.getEquipment().setItemInHand(new ItemStack (Material.STONE_SWORD));
			return;
		} 
		
		if (entity instanceof Spider) {
			Location loc = entity.getLocation();
			Block block = loc.getBlock();

			block.getRelative(BlockFace.NORTH_EAST).setType(Material.REDSTONE_WIRE);
			block.getRelative(BlockFace.NORTH_WEST).setType(Material.REDSTONE_WIRE);
			block.getRelative(BlockFace.SOUTH_EAST).setType(Material.REDSTONE_WIRE);
			block.getRelative(BlockFace.SOUTH_WEST).setType(Material.REDSTONE_WIRE);
			block.getRelative(BlockFace.SOUTH).setType(Material.WEB);
			block.getRelative(BlockFace.NORTH).setType(Material.WEB);
			block.getRelative(BlockFace.EAST).setType(Material.WEB);
			block.getRelative(BlockFace.WEST).setType(Material.WEB);
		}
	}
}