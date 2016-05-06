package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.Lists;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.events.FinalHealEvent;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NumberUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Superheroes scenario class
 * 
 * @author LeonTG77
 */
public class Superheroes extends Scenario implements Listener, CommandExecutor {
	private final TeamManager teams;
	
	public Superheroes(Main plugin, TeamManager teams) {
		super("Superheroes", "Each player on the team receives a special \"super\" power such as jump boost, health boost, strength, speed, invis, or resistance.");
		
		plugin.getCommand("super").setExecutor(this);
		
		this.teams = teams;
	}
	
	private final Map<String, HeroType> types = new HashMap<String, HeroType>();

	@Override
	public void onDisable() {
		for (Player online : Bukkit.getOnlinePlayers()) {
			removeEffects(online);
		}
		
		types.clear();
	}

	@Override
	public void onEnable() {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
		on(new FinalHealEvent());
	}
	
	@EventHandler
	public void on(FinalHealEvent event) {
		PlayerUtils.broadcast(Main.PREFIX + "Setting superhero types...");
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			HeroType type = getRandomType(online);
			
			if (type == null) {
				continue;
			}
			
			addEffects(online, type);
			
			online.sendMessage(Main.PREFIX + "You are the §a" + type.name().toLowerCase() + " §7hero type.");

		}
	}
	
	@EventHandler
	public void on(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		
		if (item.getType() != Material.MILK_BUCKET) {
			return;
		}
		
		player.sendMessage(Main.PREFIX + "You can't drink milk in this gamemode.");
		event.setItem(new ItemStack(Material.AIR));
		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getCause() != DamageCause.FALL) {
			return;
		}
		
		event.setCancelled(true);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!isEnabled()) {
			sender.sendMessage(Main.PREFIX + "Superheroes is not enabled.");
			return true;
		}
		
		if (!sender.hasPermission("uhc.superheroes")) {
			sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
			return true;
		}
		
		if (args.length == 0) {
			sender.sendMessage(Main.PREFIX + "Help for Superheroes:");
			sender.sendMessage("§8§ §7/super list §8- §f§oList all classes and who has them.");
			sender.sendMessage("§8§ §7/super apply §8- §f§oReapply the effects.");
			sender.sendMessage("§8§ §7/super set <player> §8- §f§oAdd a random effect to a player.");
			sender.sendMessage("§8§ §7/super clear <player> §8- §f§oClears the players effects.");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("apply")) {
			for (Player online : Bukkit.getOnlinePlayers()) {
				addEffects(online, types.get(online.getName()));
			}
			
			sender.sendMessage(Main.PREFIX + "Superhero effects reapplied.");
			return true;
		} 

		if (args[0].equalsIgnoreCase("set")) {
			if (args.length == 1) {
				sender.sendMessage(Main.PREFIX + "Usage: /super set <player>");
				return true;
			}
			
			Player target = Bukkit.getPlayer(args[1]);
			
			if (target == null) {
				sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is not online.");
				return true;
			}
			
			HeroType type = getRandomType(target);
			
			if (type == null) {
				sender.sendMessage(ChatColor.RED + "No available effects found for '" + target.getName() + "'.");
				return true;
			}
			
			addEffects(target, type);
			
			sender.sendMessage(Main.PREFIX + "Given §a" + target.getName() + " §7an random effect.");
			target.sendMessage(Main.PREFIX + "You are now the §a" + type.name().toLowerCase() + " §7type.");
			return true;
		}

		if (args[0].equalsIgnoreCase("clear")) {
			if (args.length == 1) {
				sender.sendMessage(Main.PREFIX + "Usage: /super clear <player>");
				return true;
			}
			
			Player target = Bukkit.getPlayer(args[1]);
			
			if (target == null) {
				sender.sendMessage(ChatColor.RED + "'" + args[0] + "' is not online.");
				return true;
			}
			
			removeEffects(target);
			
			sender.sendMessage(Main.PREFIX + "Effects of §a" + target.getName() + " §7has been removed.");
			target.sendMessage(Main.PREFIX + "Your effects has been removed.");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("list")) {
			StringBuilder health = new StringBuilder("");
			StringBuilder invis = new StringBuilder("");
			StringBuilder jump = new StringBuilder("");
			StringBuilder resistance = new StringBuilder("");
			StringBuilder speed = new StringBuilder("");
			StringBuilder strength = new StringBuilder("");
			
			for (String key : types.keySet()) {
				if (types.get(key) == HeroType.HEALTH) {
					if (health.length() > 0) {
						health.append("§7, §a");
					}
					
					health.append(ChatColor.GREEN + key);
				} 
				else if (types.get(key) == HeroType.INVIS) {
					if (invis.length() > 0) {
						invis.append("§7, §a");
					}
					
					invis.append(ChatColor.GREEN + key);
				}
				else if (types.get(key) == HeroType.JUMP) {
					if (jump.length() > 0) {
						jump.append("§7, §a");
					}
					
					jump.append(ChatColor.GREEN + key);
				}
				else if (types.get(key) == HeroType.RESISTANCE) {
					if (resistance.length() > 0) {
						resistance.append("§7, §a");
					}
					
					resistance.append(ChatColor.GREEN + key);
				}
				else if (types.get(key) == HeroType.SPEED) {
					if (speed.length() > 0) {
						speed.append("§7, §a");
					}
					
					speed.append(ChatColor.GREEN + key);
				}
				else if (types.get(key) == HeroType.STRENGTH) {
					if (strength.length() > 0) {
						strength.append("§7, §a");
					}
					
					strength.append(ChatColor.GREEN + key);
				}
			}
			
			sender.sendMessage(Main.PREFIX + "List of types:");
			sender.sendMessage("§8§ §7Health: " + health.toString().trim());
			sender.sendMessage("§8§ §7Invis: " + invis.toString().trim());
			sender.sendMessage("§8§ §7Jump: " + jump.toString().trim());
			sender.sendMessage("§8§ §7Resistance: " + resistance.toString().trim());
			sender.sendMessage("§8§ §7Speed: " + speed.toString().trim());
			sender.sendMessage("§8§ §7Strength: " + strength.toString().trim());
		}
		
		sender.sendMessage(Main.PREFIX + "Help for Superheroes:");
		sender.sendMessage("§8§ §7/super list §8- §f§oList all classes and who has them.");
		sender.sendMessage("§8§ §7/super apply §8- §f§oReapply the effects.");
		sender.sendMessage("§8§ §7/super set <player> §8- §f§oAdd a random effect to a player.");
		sender.sendMessage("§8§ §7/super clear <player> §8- §f§oClears the players effects.");
		return true;
	}
	
	/**
	 * Add the potion effects (Extra hearts if health type) to the given player.
	 * 
	 * @param player The player giving.
	 * @param type The type of the player.
	 */
	private void addEffects(Player player, HeroType type) {
		// health doesn't have any pot effects, only a max health modification.
		if (type == HeroType.HEALTH) {
			player.setMaxHealth(player.getMaxHealth() + 20);
			player.setHealth(player.getMaxHealth()); 
			return;
		}
		
		for (PotionEffect effect : type.getEffects()) {
			if (player.hasPotionEffect(effect.getType())) {
				player.removePotionEffect(effect.getType());
			}
			
			player.addPotionEffect(effect);
		}
	}
	
	/**
	 * Remove the effects and reset the max health of the given player.
	 * 
	 * @param player The player resetting.
	 */
	private void removeEffects(Player player) {
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}

		types.remove(player.getName());
		player.setMaxHealth(20.0);
	}
	
	private static final Random RANDOM = new Random();
	
	/**
	 * Get a random HeroType for the given player.
	 * 
	 * @param player The player getting for.
	 * @return A random hero type (not invis if hes on a team).
	 */
    private HeroType getRandomType(Player player) {
        List<HeroType> available = Lists.newArrayList(HeroType.values());
        Team team = teams.getTeam(player);

        if (team != null) {
            available.remove(HeroType.INVIS);
            
            // Teammates shouldn't have the same effect...
            for (String entry : team.getEntries()) {
                available.remove(types.get(entry));
            }
        }

        if (available.size() == 0) return null;

        HeroType type = available.get(RANDOM.nextInt(available.size()));
        types.put(player.getName(), type);

        return type;
    }
	
	private static final int EFFECT_TICKS = NumberUtils.TICKS_IN_999_DAYS;
	
	/**
	 * HeroType enum class.
	 * 
	 * @author LeonTG77
	 */
	private enum HeroType {
		JUMP(new PotionEffect(PotionEffectType.JUMP, EFFECT_TICKS, 3), new PotionEffect(PotionEffectType.FAST_DIGGING, EFFECT_TICKS, 1), new PotionEffect(PotionEffectType.SATURATION, EFFECT_TICKS, 9)), 
		RESISTANCE(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, EFFECT_TICKS, 1), new PotionEffect(PotionEffectType.FIRE_RESISTANCE, EFFECT_TICKS, 0)),
		INVIS(new PotionEffect(PotionEffectType.INVISIBILITY, EFFECT_TICKS, 1), new PotionEffect(PotionEffectType.WATER_BREATHING, EFFECT_TICKS, 0)), 
		SPEED(new PotionEffect(PotionEffectType.SPEED, EFFECT_TICKS, 1), new PotionEffect(PotionEffectType.FAST_DIGGING, EFFECT_TICKS, 1)), 
		STRENGTH(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, EFFECT_TICKS, 0)), 
		HEALTH();

		private PotionEffect[] effects;
		
		/**
		 * Constructor for HeroType.
		 *  
		 * @param effects The effects for the type.
		 */
		private HeroType(PotionEffect... effects) {
			this.effects = effects;
		}
		
		/**
		 * Get all the effects for the type.
		 * 
		 * @return An array of potion effects.
		 */
		public PotionEffect[] getEffects() {
			return effects;
		}
	}
}