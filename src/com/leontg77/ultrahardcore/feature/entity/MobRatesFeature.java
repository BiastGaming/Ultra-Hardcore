package com.leontg77.ultrahardcore.feature.entity;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.events.MeetupEvent;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.utils.EntityUtils;

/**
 * Mob Rates feature class.
 *  
 * @author LeonTG77
 */
public class MobRatesFeature extends Feature implements Listener {
	private final Timer timer;
	private final Game game;

	public MobRatesFeature(Timer timer, Game game) {
		super("Mob Rates", "Lower mob rates by how many people are online, lower mob rates in jungles and fix meetup mobs.");
		
		this.timer = timer;
		this.game = game;
	}

	@EventHandler
	public void on(final MeetupEvent event) {
		for (World world : game.getWorlds()) {
    		for (Entity mob : world.getEntities()) {
    			final EntityType type = mob.getType();
    			
    			if (type == EntityType.DROPPED_ITEM || type == EntityType.EXPERIENCE_ORB) {
    				continue;
    			}
    			
				if (!EntityUtils.isButcherable(type)) {
					continue;
				}
				
				mob.remove();
    		}
       	}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void on(final CreatureSpawnEvent event) {
		final Entity entity = event.getEntity();
		final EntityType type = entity.getType();
		
		final Location loc = event.getLocation();
		final World world = loc.getWorld();
		
		final SpawnReason reason = event.getSpawnReason();
		final Biome biome = loc.getBlock().getBiome();
		
		if (world.getName().equals("lobby") || world.getName().equals("arena")) {
			switch (reason) {
			case BUILD_SNOWMAN:
			case CUSTOM:
			case SPAWNER:
			case SPAWNER_EGG:
				return;
			default:
				event.setCancelled(true);
				break;
			}
			return;
		}
		
		final Random rand = new Random();
		
		if (State.isState(State.INGAME) || State.isState(State.ENDING)) {
			if (timer.getMeetup() <= 0) {
				switch (reason) { 
				case CHUNK_GEN:
				case DEFAULT:
				case DISPENSE_EGG:
				case EGG:
				case INFECTION:
				case NATURAL:
				case NETHER_PORTAL:
				case OCELOT_BABY:
				case REINFORCEMENTS:
				case SILVERFISH_BLOCK:
				case SLIME_SPLIT:
				case SPAWNER:
				case VILLAGE_DEFENSE:
				case VILLAGE_INVASION:
					event.setCancelled(true);
					return;
				default:
					break;
				}
				return;
			}
		}
		
		switch (reason) {
		case BREEDING:
		case BUILD_IRONGOLEM:
		case BUILD_SNOWMAN:
		case BUILD_WITHER:
		case CUSTOM:
		case INFECTION:
		case JOCKEY:
		case LIGHTNING:
		case MOUNT:
		case SLIME_SPLIT:
		case SPAWNER:
		case SPAWNER_EGG:
			return;
		default:
			break;
		}
		
		// since all jungles are replaced by jungle edge it spawns A LOT more mobs...
		if (biome.equals(Biome.JUNGLE_EDGE) && rand.nextBoolean()) {
			event.setCancelled(true);
			return;
		}

		// limit mob spawning by how many are online.
		final int count = Bukkit.getOnlinePlayers().size();
		
		switch (type) {
		case SQUID:
		case BAT:
			if (count > 60) {
				event.setCancelled(true);
				return;
			}
			
			if (count > 80) {
				event.setCancelled(true);
				return;
			}
			break;
		case BLAZE:
			if (count > 90) {
				event.setCancelled(true);
				return;
			}
			
			if (count > 110) {
				event.setCancelled(true);
				return;
			}
			break;
		case ENDERMITE:
		case HORSE:
		case PIG:
			if (count > 100) {
				event.setCancelled(true);
				return;
			}
			
			if (count > 120) {
				event.setCancelled(true);
				return;
			}
			break;
		case RABBIT:
		case SHEEP:
			if (count > 80) {
				event.setCancelled(true);
				return;
			}
			
			if (count > 100) {
				event.setCancelled(true);
				return;
			}
			break;
		case MAGMA_CUBE:
		case SLIME:
			if (count > 80) {
				event.setCancelled(true);
				return;
			}
			
			if (count > 100) {
				event.setCancelled(true);
				return;
			}
			break;
		case CREEPER:
		case ENDERMAN:
		case PIG_ZOMBIE:
		case SKELETON:
		case GUARDIAN:
		case GHAST:
		case SPIDER:
		case WITCH:
		case CAVE_SPIDER:
		case ZOMBIE:
			if (count > 80) {
				event.setCancelled(true);
				return;
			}
			
			if (count > 110) {
				event.setCancelled(true);
				return;
			}
			break;
		default:
			event.setCancelled(false);
			return;
		}
		
	}
}