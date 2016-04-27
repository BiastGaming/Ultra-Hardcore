package com.leontg77.ultrahardcore.utils;

import java.util.Set;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Zombie;

import com.google.common.collect.ImmutableSet;

/**
 * Entity utils.
 * <p>
 * This class contains a method for getting the name of an 
 * entity type and checking if an entity type is clearable
 * 
 * @author LeonTG77
 */
public class EntityUtils {
	
	/**
	 * A HashSet of all entity types that should be removed at the start.
	 */
	protected static final Set<EntityType> clearable = ImmutableSet.of(
		EntityType.BAT, EntityType.BLAZE, EntityType.CAVE_SPIDER, EntityType.CREEPER, EntityType.DROPPED_ITEM,
		EntityType.EGG, EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.ENDER_PEARL, EntityType.EXPERIENCE_ORB, 
		EntityType.FIREBALL, EntityType.GHAST, EntityType.GIANT, EntityType.GUARDIAN, EntityType.IRON_GOLEM, 
		EntityType.MAGMA_CUBE, EntityType.MUSHROOM_COW, EntityType.PIG_ZOMBIE, EntityType.PRIMED_TNT,
		EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.SMALL_FIREBALL, EntityType.SPIDER,
		EntityType.WITCH, EntityType.WITHER, EntityType.WITHER_SKULL, EntityType.ZOMBIE
	);
	
	/**
	 * Check if the given entity type can be butchered.
	 * 
	 * @param type The type checking.
	 * @return True if it is butcherable, false otherwise.
	 */
	public static boolean isButcherable(final EntityType type) {
		return clearable.contains(type);
	}
	
	/**
	 * Get the MC name for the given entity type.
	 * 
	 * @param entity The entity wanting to check.
	 * @return The MC name of the type.
	 */
	public static String getMobName(Entity entity) {
		switch (entity.getType()) {
		case ARMOR_STAND:
			return "Armor Stand";
		case ARROW:
			return "Arrow";
		case BAT:
			return "Bat";
		case BLAZE:
			return "Blaze";
		case BOAT:
			return "Boat";
		case CAVE_SPIDER:
			return "Cave Spider";
		case CHICKEN:
			return "Chicken";
		case COW:
			return "Cow";
		case CREEPER:
			return "Creeper";
		case DROPPED_ITEM:
			return "Dropped Item";
		case EGG:
			return "Egg";
		case ENDERMAN:
			return "Enderman";
		case ENDERMITE:
			return "Endermite";
		case ENDER_CRYSTAL:
			return "Ender Crystal";
		case ENDER_DRAGON:
			return "Ender Dragon";
		case ENDER_PEARL:
			return "Ender Pearl";
		case ENDER_SIGNAL:
			return "Ender Signal";
		case EXPERIENCE_ORB:
			return "Exp. Orb";
		case FALLING_BLOCK:
			return "Falling Block";
		case FIREBALL:
			return "Fireball";
		case FIREWORK:
			return "Firework";
		case FISHING_HOOK:
			return "Fish. Hook";
		case GHAST:
			return "Ghast";
		case GIANT:
			return "Giant";
		case GUARDIAN:
			return "Guardian";
		case HORSE:
			return "Horse";
		case IRON_GOLEM:
			return "Iron Golem";
		case ITEM_FRAME:
			return "Item Frame";
		case LEASH_HITCH:
			return "Leash";
		case LIGHTNING:
			return "Lightning";
		case MAGMA_CUBE:
			return "Magma Cube";
		case MINECART:
			return "Minecart";
		case MINECART_CHEST:
			return "Minecart Chest";
		case MINECART_COMMAND:
			return "Minecart CommandBlock";
		case MINECART_FURNACE:
			return "Minecart Furnace";
		case MINECART_HOPPER:
			return "Minecart Hopper";
		case MINECART_MOB_SPAWNER:
			return "Minecart Mob Spawner";
		case MINECART_TNT:
			return "Minecart";
		case MUSHROOM_COW:
			return "Mushroom Cow";
		case OCELOT:
			return "Ocelot";
		case PAINTING:
			return "Painting";
		case PIG:
			return "Pig";
		case PIG_ZOMBIE:
			return "Zombie Pigman";
		case PLAYER:
			return "Player";
		case PRIMED_TNT:
			return "TNT";
		case RABBIT:
			return "Rabbit";
		case SHEEP:
			return "Sheep";
		case SILVERFISH:
			return "Silverfish";
		case SKELETON:
			if (((Skeleton) entity).getSkeletonType() == SkeletonType.WITHER) {
				return "Wither Skeleton";
			} else {
				return "Skeleton";
			}
		case SLIME:
			return "Slime";
		case SMALL_FIREBALL:
			return "Fireball";
		case SNOWBALL:
			return "Snowball";
		case SNOWMAN:
			return "Snow Golem";
		case SPIDER:
			return "Spider";
		case SPLASH_POTION:
			return "Potion";
		case SQUID:
			return "Squid";
		case THROWN_EXP_BOTTLE:
			return "Thrown Exp. Bottle";
		case VILLAGER:
			return "Villager";
		case WITCH:
			return "Witch";
		case WITHER:
			return "Wither";
		case WITHER_SKULL:
			return "Wither Skull";
		case WOLF:
			return "Wolf";
		case ZOMBIE:
			if (((Zombie) entity).isBaby()) {
				return "Baby Zombie";
			} else {
				return "Zombie";
			}
		default:
			return "???" + NameUtils.capitalizeString(entity.getType().name(), true);
		}
	}
}