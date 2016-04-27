 package com.leontg77.ultrahardcore.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Rank;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;

/**
 * Permissions manager class.
 * <p>
 * Basic manager for managing permissions.
 * 
 * @author LeonTG77
 */
public class PermissionsManager {
	private final ScenarioManager manager;
	private final Main plugin;
	
	/**
	 * Permissions manager class constructor.
	 * 
	 * @param plugin The main class.
	 * @param manager The scenario manager class.
	 */
	public PermissionsManager(Main plugin, ScenarioManager manager) {
		this.manager = manager;
		this.plugin = plugin;
	}
	
	private final Map<UUID, PermissionAttachment> permissions = new HashMap<UUID, PermissionAttachment>();
	
	/**
	 * Get a copy of the permissions HashMap.
	 * 
	 * @return Copy of permissions HashMap.
	 */
	public Map<UUID, PermissionAttachment> getPermissions() {
		return ImmutableMap.copyOf(permissions);
	}
	
	/**
	 * A list of all the uuids of the ubl courtroom members.
	 */
	private static final Set<String> UBL_COMMITEE_UUIDS = ImmutableSet.of(
		"c83eeaab-fe2f-4c22-9e81-d0c3034fb4c4", // sperlo
		"2795d93f-788c-42ea-a2e7-d1f373e1f9ec", // joe
		"cf47d532-6346-4384-bcf9-cfd5e5a15d57", // ratchet
		"89100481-4b71-4f9e-8ed2-cbb52a2f9510", // nintendo
		"0444f904-0490-4793-a8b0-2448c0569ee2", // joris
		"01f4fabc-beeb-46ea-8858-c593711a5688" // fazed
	);
	
	/**
	 * Handle the permissions for the given player.
	 * 
	 * @param player the player.
	 */
	public void addPermissions(Player player) {
		if (!permissions.containsKey(player.getUniqueId())) {
			permissions.put(player.getUniqueId(), player.addAttachment(plugin));
		}

		PermissionAttachment perm = permissions.get(player.getUniqueId());
		Rank rank = Rank.DEFAULT;
		
		if (plugin.fileExist(player.getUniqueId())) {
			User user = plugin.getUser(player);
			rank = user.getRank();
        }
		
		if (rank == Rank.OWNER) {
			player.setOp(true);
			return;
		}
		
		player.setOp(false);
		
		perm.setPermission("uhc.border", true);
		perm.setPermission("uhc.stats", true);
		perm.setPermission("uhc.top", true);
		perm.setPermission("uhc.team", true);
		perm.setPermission("uhc.msg", true);
		perm.setPermission("uhc.reply", true);
		perm.setPermission("uhc.tl", true);
		perm.setPermission("uhc.pm", true);
		perm.setPermission("uhc.pmores", true);
		perm.setPermission("uhc.pmminedores", true);
		perm.setPermission("uhc.health", true);
		perm.setPermission("uhc.ms", true);
		perm.setPermission("uhc.tps", true);
		perm.setPermission("uhc.uhc", true);
		perm.setPermission("uhc.hof", true);
		perm.setPermission("uhc.helpop", true);
		perm.setPermission("uhc.matchpost", true);
		perm.setPermission("uhc.scenario", true);
		perm.setPermission("uhc.ignore", true);
		perm.setPermission("uhc.timeleft", true);
		perm.setPermission("uhc.list", true);
		perm.setPermission("uhc.arena", true);
		perm.setPermission("uhc.hotbar", true);
		perm.setPermission("uhc.parkour", true);
		perm.setPermission("uhc.combatlog", true);
		perm.setPermission("uhc.mucoords", true);
		perm.setPermission("uhc.viewdistance", true);
		
		// spectator perms, they can only use them if they're spectating.
		perm.setPermission("uhc.spectate", true);
		perm.setPermission("uhc.invsee", true);
		perm.setPermission("uhc.near", true);
		perm.setPermission("uhc.specchat", true);
		perm.setPermission("uhc.speed", true);
		perm.setPermission("uhc.tp", true);
		perm.setPermission("uhc.back", true);
		perm.setPermission("uhc.specandstaffchat", true);
		
		if (UBL_COMMITEE_UUIDS.contains(player.getUniqueId().toString())) {
			perm.setPermission("uhc.prelist", true);
			perm.setPermission("uhc.info.ip", true);
			perm.setPermission("uhc.info", true);
		}
		
		if (rank == Rank.DEFAULT) {
			return;
		}
		
		perm.setPermission("uhc.prelist", true);
		
		if (rank == Rank.SPEC) {
			return;
		}
		
		// adding donation features at at later date.
		
		if (rank == Rank.DONATOR) {
			return;
		}

		perm.setPermission("uhc.cantignore", true);
		perm.setPermission("uhc.ac", true);
		perm.setPermission("uhc.ban", true);
		perm.setPermission("uhc.banip", true);
		perm.setPermission("uhc.unban", true);
		perm.setPermission("uhc.dq", true);
		perm.setPermission("uhc.broadcast", true);
		perm.setPermission("uhc.fly", true);
		perm.setPermission("uhc.info", true);
		perm.setPermission("uhc.chat", true);
		perm.setPermission("uhc.respawn", true);
		perm.setPermission("uhc.kick", true);
		perm.setPermission("uhc.mute", true);
		perm.setPermission("uhc.cmdspy", true);
		perm.setPermission("uhc.staff", true);
		perm.setPermission("uhc.admin", true);
		perm.setPermission("uhc.team.admin", true);
		perm.setPermission("uhc.tempban", true);
		perm.setPermission("uhc.tp.bypass", true);
		perm.setPermission("uhc.whitelist", true);
		
		if (rank == Rank.STAFF) {
			return;
		}

		perm.setPermission("uhc.give", true);
		perm.setPermission("uhc.tp.other", true);
		perm.setPermission("uhc.arena.admin", true);
		perm.setPermission("uhc.board", true);
		perm.setPermission("uhc.butcher", true);
		perm.setPermission("uhc.clearinv", true);
		perm.setPermission("uhc.clearxp", true);
		perm.setPermission("uhc.config", true);
		perm.setPermission("uhc.end", true);
		perm.setPermission("uhc.feed", true);
		perm.setPermission("uhc.restart", true);
		perm.setPermission("uhc.giveall", true);
		perm.setPermission("uhc.heal", true);
		perm.setPermission("uhc.pregen", true);
		perm.setPermission("uhc.random", true);
		perm.setPermission("uhc.scenario.manage", true);
		perm.setPermission("uhc.sethealth", true);
		perm.setPermission("uhc.setmaxhealth", true);
		perm.setPermission("uhc.start", true);
		perm.setPermission("uhc.scatter", true);
		perm.setPermission("uhc.spectate.others", true);
		perm.setPermission("uhc.timer", true);
		perm.setPermission("uhc.vote", true);
		perm.setPermission("uhc.pvp", true);
		perm.setPermission("uhc.pregen", true);
		perm.setPermission("uhc.border.set", true);
		perm.setPermission("uhc.world", true);
		perm.setPermission("uhc.viewdistance.set", true);
		
		for (Scenario scen : manager.getScenarios()) {
			perm.setPermission("uhc." + scen.getName().toLowerCase(), true);
		}
	}
	
	/**
	 * Handle the permissions for the given player if he leaves.
	 * 
	 * @param player the player.
	 */
	public void removePermissions(Player player) {
		if (!permissions.containsKey(player.getUniqueId())) {
			return;
		}
		
		try {
			player.removeAttachment(permissions.get(player.getUniqueId()));
		} catch (Exception e) {
			Bukkit.getLogger().warning("Couldn't remove " + player.getName() + "'s permissions.");
			Bukkit.getLogger().warning(e.getClass().getName() + ": " + e.getMessage());
		}
		
		permissions.remove(player.getUniqueId());
	}
}