package com.leontg77.uhc.utils;

import net.minecraft.server.v1_8_R3.MinecraftServer;

import org.bukkit.Bukkit;

import com.leontg77.uhc.Game;
import com.leontg77.uhc.Settings;
import com.leontg77.uhc.State;

/**
 * Game utilities class.
 * <p>
 * Contains game related methods.
 * 
 * @author LeonTG77
 */
public class GameUtils {
	
	/**
	 * Gets the servers tps.
	 * 
	 * @return The servers tps.
	 */
	public static double getTps() {
		return MinecraftServer.getServer().recentTps[0];
	}
	
	/**
	 * Gets a string version of the current state.
	 * 
	 * @return The string version.
	 */
	public static String getState() {
		State current = State.getState();
		
		switch (current) {
		case INGAME:
			if (getTeamSize().startsWith("No") || Game.getInstance().isRR()) {
				return "No games running.";
			} 
			else if (getTeamSize().startsWith("Open")) {
				return "Open for visitors.";
			} 
			else {
				return "Started.";
			}
		case LOBBY:
			if (Bukkit.getServer().hasWhitelist()) {
				if (getTeamSize().startsWith("No") || Game.getInstance().isRR()) {
					return "No games running.";
				} 
				else if (getTeamSize().startsWith("Open")) {
					return "Open for visitors.";
				} 
				else {
					return "Not open yet.";
				}
			} 
			else {
				return "Waiting for players...";
			}
		case SCATTER:
			if (getTeamSize().startsWith("No") || Game.getInstance().isRR()) {
				return "No games running.";
			} 
			else if (getTeamSize().startsWith("Open")) {
				return "Open for visitors.";
			} 
			else {
				return "Scattering...";
			}
		default:
			return "No games running.";
		}
	}
	
	/**
	 * Get the teamsize in a string format.
	 * 
	 * @return The string format.
	 */
	public static String getTeamSize() {
		Game game = Game.getInstance();
		
		if (game.isFFA()) {
			if (game.getTeamSize() == 1) {
				return "FFA ";
			} 
			else if (game.getTeamSize() == 0) {
				return "No ";
			}
			else if (game.getTeamSize() == -1) {
				return "Open ";
			} 
			else if (game.getTeamSize() == -2) {
				return "";
			}
			else {
				return "rTo" + (game.getTeamSize() > 0 ? game.getTeamSize() : "X") + " ";
			}
		} 
		else {
			return "cTo" + (game.getTeamSize() > 0 ? game.getTeamSize() : "X") + " ";
		}
	}
	
	/**
	 * Get the current host hof name.
	 * 
	 * @return The hof name.
	 */
	public static String getCurrentHost() {
		String host = Settings.getInstance().getConfig().getString("game.host");
		
		if (host.equalsIgnoreCase("LeonTG77")) {
			return "Leon";
		} 
		else if (host.equalsIgnoreCase("PolarBlunk")) {
			return "Polar";
		} 
		else if (host.equalsIgnoreCase("Itz_Isaac")) {
			return "Isaac";
		}
		return host;
	}

	/**
	 * Get the hof name for the given host.
	 * 
	 * @param host The host.
	 * @return The hof name.
	 */
	public static String getHost(String host) {
		if (host.equalsIgnoreCase("LeonTG77") || host.equalsIgnoreCase("Leon")) {
			return "Leon";
		} 
		else if (host.equalsIgnoreCase("Polar") || host.equalsIgnoreCase("PolarBlunk")) {
			return "Polar";
		} 
		else if (host.equalsIgnoreCase("Itz_Isaac") || host.equalsIgnoreCase("Isaac")) {
			return "Isaac";
		}
		return host;
	}

	/**
	 * Get the host name for the given alt name.
	 * 
	 * @param host The host.
	 * @return The hof name.
	 */
	public static String getHostName(String host) {
		if (host.equalsIgnoreCase("Leon")) {
			return "LeonTG77";
		} 
		else if (host.equalsIgnoreCase("Polar")) {
			return "PolarBlunk";
		} 
		else if (host.equalsIgnoreCase("Isaac")) {
			return "Itz_Isaac";
		}
		else if (host.equalsIgnoreCase("Axlur")) {
			return "AxlurUHC";
		}
		return host;
	}
}