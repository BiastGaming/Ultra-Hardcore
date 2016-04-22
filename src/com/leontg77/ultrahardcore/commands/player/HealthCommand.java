package com.leontg77.ultrahardcore.commands.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.commands.CommandException;
import com.leontg77.ultrahardcore.commands.UHCCommand;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.scenario.scenarios.Paranoia;
import com.leontg77.ultrahardcore.scenario.scenarios.SelfDiagnosis;
import com.leontg77.ultrahardcore.scenario.scenarios.TeamHealth;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * Health command class.
 * 
 * @author LeonTG77
 */
public class HealthCommand extends UHCCommand {
	private final ScenarioManager scen;

	public HealthCommand(ScenarioManager scen) {
		super("health", "[player]");
		
		this.scen = scen;
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) throws CommandException {
		if (scen.getScenario(Paranoia.class).isEnabled() || scen.getScenario(SelfDiagnosis.class).isEnabled() || scen.getScenario(TeamHealth.class).isEnabled()) {
			throw new CommandException("/h is disabled in this gamemode.");
		}
		
		Player target;
		
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				throw new CommandException("Only players can view their own health.");
			}
			
			target = (Player) sender;
		} else {
			target = Bukkit.getPlayer(args[0]);
			
			if (target == null) {
				throw new CommandException("'" + args[0] + "' is not online.");
			}
		}

		CraftPlayer craft = (CraftPlayer) target;
		
		double health = target.getHealth();
		double maxhealth = target.getMaxHealth();
		
		double absHearts = craft.getHandle().getAbsorptionHearts();

		sender.sendMessage(Main.PREFIX + (target == sender ? "Your health:" : "§a" + target.getName() + "'s §7health:"));
		sender.sendMessage(Main.ARROW + "Health: §a" + NumberUtils.makePercent(health) + "%");
		sender.sendMessage(Main.ARROW + "Max Health: §a" + NumberUtils.makePercent(maxhealth).substring(2) + "%");
		sender.sendMessage(Main.ARROW + "Absorption hearts: §e" + NumberUtils.makePercent(absHearts).substring(2) + "%");
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		if (args.length == 1) {
    		return allPlayers();
		}

		return new ArrayList<String>();
	}
}