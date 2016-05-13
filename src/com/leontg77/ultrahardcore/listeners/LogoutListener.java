package com.leontg77.ultrahardcore.listeners;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.User;
import com.leontg77.ultrahardcore.commands.msg.MsgCommand;
import com.leontg77.ultrahardcore.events.PlayerLeaveEvent;
import com.leontg77.ultrahardcore.gui.GUIManager;
import com.leontg77.ultrahardcore.managers.PermissionsManager;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Logout listener class.
 * <p> 
 * Contains all eventhandlers for logout releated events.
 * 
 * @author LeonTG77
 */
public class LogoutListener implements Listener {
	private final Main plugin;
	private final Game game;

	private final SpecManager spec;
	private final PermissionsManager perm;
	
	/**
	 * Logout listener class constructor.
	 * 
	 * @param plugin The main class.
	 * @param game The game class.
	 * @param spec The spectator manager class.
	 * @param perm The permission manager class.
	 */
	public LogoutListener(Main plugin, Game game, GUIManager gui, SpecManager spec, PermissionsManager perm) {
		this.plugin = plugin;
		this.game = game;
		
		this.spec = spec;
		this.perm = perm;
	}
	
	@EventHandler
	public void on(PlayerLeaveEvent event) {
		Player player = event.getPlayer();
		User user = plugin.getUser(player);
		
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		Date date = new Date();
		
		user.getFile().set("lastlogout", date.getTime());
		user.saveFile();

		perm.removePermissions(player);
		user.setLastLoc(null);

		String leaveMsg = event.getQuitMessage();
		event.setQuitMessage(null);
		
		if (player.isInsideVehicle()) {
			player.leaveVehicle(); // sometimes dead players will take horses with them when they log out for some reason...
		}
		
		if (!spec.isSpectating(player) && leaveMsg != null) {
			switch (event.getLogoutReason()) {
			case CRASHED:
				PlayerUtils.broadcast("§8[§c-§8] " + user.getRankColor() + player.getName() + " §7crashed. §8(§a" + (plugin.getOnlineCount() - 1) + "§8/§a" + game.getMaxPlayers() + "§8)");
				break;
			case TIMED_OUT:
				PlayerUtils.broadcast("§8[§c-§8] " + user.getRankColor() + player.getName() + " §7timed out. §8(§a" + (plugin.getOnlineCount() - 1) + "§8/§a" + game.getMaxPlayers() + "§8)");
				break;
			default:
				PlayerUtils.broadcast("§8[§c-§8] " + user.getRankColor() + player.getName() + " §7left. §8(§a" + (plugin.getOnlineCount() - 1) + "§8/§a" + game.getMaxPlayers() + "§8)");
				break;
			}
		}

		if (MsgCommand.msg.containsKey(player.getName())) {
			MsgCommand.msg.remove(player.getName());
		}
		
		Set<String> temp = new HashSet<String>(MsgCommand.msg.keySet());
		
		for (String key : temp) {
			if (MsgCommand.msg.get(key).equals(player.getName())) {
				MsgCommand.msg.remove(key);
			}
		}
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		if (event.getReason().equals("disconnect.spam")) {
			event.setReason("Kicked for spamming");
		}
		
		if (event.getReason().startsWith("§")) {
			return;
		}

		if (event.getReason().equals("You have been idle for too long!") &&
				ImmutableList.of(
						"8b2b2e07-b694-4bd0-8f1b-ba99a267be41",
						"679021a8-67c1-4317-8323-4b2b839a01f6"
				).contains(event.getPlayer().getUniqueId().toString())) {
			event.setReason("You have been coding for too long!");
		}
		
		event.setReason("§8» §7" + event.getReason() + " §8«");
	}
}