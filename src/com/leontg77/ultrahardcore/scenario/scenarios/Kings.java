package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Kings scenario class
 * 
 * @author LeonTG77
 */
public class Kings extends Scenario implements Listener, CommandExecutor {
	private final TeamManager teams;
	
	public Kings(Main plugin, TeamManager teams) {
		super("Kings", "Theres a king on each team, the king has 20 max hearts and resistance, if the king dies the teammates will be poisoned.");
		
		plugin.getCommand("kings").setExecutor(this);
		
		this.teams = teams;
	}
	
	private final Set<String> kings = new HashSet<String>();

	@Override
	public void onDisable() {
		for (String king : kings) {
			Player theKing = Bukkit.getPlayer(king);
        	
        	if (theKing == null) {
        		continue;
        	}
        	
        	for (PotionEffect effect : theKing.getActivePotionEffects()) {
        		theKing.removePotionEffect(effect.getType());
        	}
        	
        	theKing.setMaxHealth(20);
		}
		
		kings.clear();
	}

	@Override
	public void onEnable() {}
	
	public Set<String> getKings() {
		return kings;
	}
	
	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		if (event.getItem().getType() != Material.MILK_BUCKET) {
			return;
		}
		
		event.getPlayer().sendMessage(Main.PREFIX + "You cannot drink milk in kings.");
		event.setCancelled(true);
		event.setItem(new ItemStack (Material.AIR));
	}
	
	@EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
		if (!State.isState(State.INGAME)) {
			return;
		}
		
        Player player = event.getEntity();

        if (!kings.contains(player.getName())) {
        	return;
        }

        Team team = teams.getTeam(player);

        if (team == null) {
            return;
        }
        
        kings.remove(player.getName());
        PlayerUtils.broadcast(Main.PREFIX + "The king on team §6" + team.getName().substring(3) + " §7has died, §a" + kings.size() + " §7kings remaining.");
        
        for (String entry : team.getEntries()) {
        	Player teammate = Bukkit.getPlayer(entry);
        	
        	if (teammate == null) {
        		continue;
        	}
        	
    		teammate.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 0)); 
        	teammate.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 3600, 0)); 
        	teammate.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 260, 0));
        }
    }

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!isEnabled()) {
			sender.sendMessage(Main.PREFIX + "\"Kings\" is not enabled.");
			return true;
		}
		
		if (!sender.hasPermission("uhc.kings")) {
			sender.sendMessage(Main.NO_PERMISSION_MESSAGE);
			return true;
		}
		
		if (args.length == 0) {
			sender.sendMessage(Main.PREFIX + "Usage: /kings <add|remove|random|list> [player]");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("add")) {
			if (args.length == 1) {
				sender.sendMessage(Main.PREFIX + "Usage: /kings add <player>");
				return true;
			}
			
			Player target = Bukkit.getPlayer(args[1]);
			
			if (target == null) {
				sender.sendMessage(ChatColor.RED + args[1] + " is not online.");
				return true;
			}

			if (kings.contains(target.getName())) {
				sender.sendMessage(ChatColor.RED + target.getName() + " is already a king.");
				return true;
			}
			
			Team team = teams.getTeam(target);
			
			if (team == null) {
				sender.sendMessage(ChatColor.RED + target.getName() + " is not on a team.");
				return true;
			}
			
			for (String entry : team.getEntries()) {
				if (kings.contains(entry)) {
					sender.sendMessage(ChatColor.RED + target.getName() + "'s team already has a king.");
					return true;
				}
			}
			
			kings.add(target.getName());
			
			target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1726272000, 0)); 
			target.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1726272000, 0));
			target.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1726272000, 0));
			target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1726272000, 0));
			
			target.setMaxHealth(40);
			target.setHealth(40);

			PlayerUtils.broadcast(Main.PREFIX + "§a" + target.getName() + " §7is now a king on team §6" + team.getName().substring(3) + "§7.");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("remove")) {
			if (args.length == 1) {
				sender.sendMessage(Main.PREFIX + "Usage: /kings remove <player>");
				return true;
			}
			
			Player target = Bukkit.getPlayer(args[1]);
			
			if (target == null) {
				sender.sendMessage(ChatColor.RED + args[1] + " is not online.");
				return true;
			}

			if (!kings.contains(target.getName())) {
				sender.sendMessage(ChatColor.RED + target.getName() + " is not a king.");
				return true;
			}
			
			kings.remove(target.getName());
			
			for (PotionEffect effect : target.getActivePotionEffects()) {
				target.removePotionEffect(effect.getType());
			}

			target.setHealth(20);
			target.setMaxHealth(20);
			
			PlayerUtils.broadcast(Main.PREFIX + "§a" + target.getName() + " §7is no longer a king.");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("list")) {
			if (kings.isEmpty()) {
				sender.sendMessage(Main.PREFIX + "There are no kings.");
				return true;
			}
			
			StringBuilder list = new StringBuilder();
			int i = 1;
			
			for (String king : kings) {
				if (list.length() > 0) {
					if (i == kings.size()) {
						list.append(" §8and§a ");
					} else {
						list.append("§8, §a");
					}
				}
				
				list.append(king);
			}
			
			sender.sendMessage(Main.PREFIX + "Current kings: §8(§6" + kings.size() + "§8)");
			sender.sendMessage("§8§ §a" + list.toString().trim() + "§8.");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("random")) {
			for (Team team : teams.getTeamsWithPlayers()) {
				List<String> list = new ArrayList<String>();
				Random rand = new Random();
				
				for (String entry : team.getEntries()) {
					if (Bukkit.getPlayer(entry) != null) {
						list.add(entry);
					}
				}
				
				if (list.isEmpty()) {
					continue;
				}
				
				Player target = Bukkit.getPlayer(list.get(rand.nextInt(list.size())));
				
				kings.add(target.getName());
				
				target.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1726272000, 0)); 
				target.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1726272000, 0));
				target.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1726272000, 0));
				target.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1726272000, 0));
				
				target.setMaxHealth(40);
				target.setHealth(40);
				
				PlayerUtils.broadcast(Main.PREFIX + "§a" + target.getName() + " §7is now a king on team §6" + team.getName().substring(3) + "§7.");
			}
		}
		return true;
	}
}