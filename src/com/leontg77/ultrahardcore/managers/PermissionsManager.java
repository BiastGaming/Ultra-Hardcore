 package com.leontg77.ultrahardcore.managers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.User.Rank;

/**
 * Permissions manager class.
 * <p>
 * Basic manager for managing permissions.
 * 
 * @author LeonTG77
 */
public class PermissionsManager {
	public static Map<String, PermissionAttachment> permissions = new HashMap<String, PermissionAttachment>();
	
	/**
	 * Handle the permissions for the given player.
	 * 
	 * @param player the player.
	 */
	public static void addPermissions(Player player) {
		if (!permissions.containsKey(player.getName())) {
			permissions.put(player.getName(), player.addAttachment(Main.plugin));
		}

		PermissionAttachment perm = permissions.get(player.getName());
		
		User user = User.get(player);
		Rank rank = user.getRank();
		
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
		
		// spectator perms, they can only use them if they're spectating.
		perm.setPermission("uhc.invsee", true);
		perm.setPermission("uhc.near", true);
		perm.setPermission("uhc.specchat", true);
		perm.setPermission("uhc.speed", true);
		perm.setPermission("uhc.tp", true);
		
		if (rank == Rank.DEFAULT) {
			return;
		}
		
		perm.setPermission("uhc.spectate", true);
		perm.setPermission("uhc.prelist", true);
		
		if (rank == Rank.SPEC) {
			return;
		}
		
		// adding donation features at at later date.
		
		if (rank == Rank.DONATOR) {
			return;
		}

		perm.setPermission("uhc.cantignore", true);
		perm.setPermission("uhc.ban", true);
		perm.setPermission("uhc.broadcast", true);
		perm.setPermission("uhc.fly", true);
		perm.setPermission("uhc.info", true);
		perm.setPermission("uhc.chat", true);
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
		perm.setPermission("uhc.arena", true);
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
		perm.setPermission("uhc.spread", true);
		perm.setPermission("uhc.spectate.other", true);
		perm.setPermission("uhc.timer", true);
		perm.setPermission("uhc.vote", true);
		perm.setPermission("uhc.mysteryteams", true);
		perm.setPermission("uhc.bestbtc", true);
		perm.setPermission("uhc.bestpve", true);
		perm.setPermission("uhc.bigcrack", true);
		perm.setPermission("uhc.slimycrack", true);
		perm.setPermission("uhc.kings", true);
		
		perm.setPermission("uhc.pvp", true);
		perm.setPermission("uhc.pregen", true);
		perm.setPermission("uhc.border.set", true);
		perm.setPermission("uhc.world", true);
	}
	
	/**
	 * Handle the permissions for the given player if he leaves.
	 * 
	 * @param player the player.
	 */
	public static void removePermissions(Player player) {
		if (!permissions.containsKey(player.getName())) {
			return;
		}
		
		try {
			player.removeAttachment(permissions.get(player.getName()));
		} catch (Exception e) {
			Bukkit.getLogger().warning("Couldn't remove " + player.getName() + "'s permissions.");
		}
		
		permissions.remove(player.getName());
	}
}