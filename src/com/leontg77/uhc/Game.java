package com.leontg77.uhc;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.leontg77.uhc.Main.BorderShrink;
import com.leontg77.uhc.utils.PacketUtils;
import com.leontg77.uhc.utils.PlayerUtils;

public class Game {
	private Settings settings = Settings.getInstance();
	private static Game instance = new Game();
	
	/**
	 * Get the instance of the class.
	 * 
	 * @return The instance.
	 */
	public static Game getInstance() {
		return instance;
	}
	
	/**
	 * Change the FFA mode.
	 * 
	 * @param ffa True to enable, false to disable.
	 */
	public void setFFA(boolean ffa) {
		settings.getConfig().set("game.ffa", ffa);
		settings.saveConfig();
	}
	
	/**
	 * Check if the game is in FFA mode
	 * 
	 * @return True if it is, false otherwise.
	 */
	public boolean isFFA() {
		return settings.getConfig().getBoolean("game.ffa", true);
	}
	
	/**
	 * Set the team size of the game.
	 * 
	 * @param teamSize the new teamsize.
	 */
	public void setTeamSize(int teamSize) {
		settings.getConfig().set("game.teamsize", teamSize);
		settings.saveConfig();
	}
	
	/**
	 * Get the game teamsize.
	 * 
	 * @return The teamsize.
	 */
	public int getTeamSize() {
		return settings.getConfig().getInt("game.teamsize", 0);
	}
	
	public void setBorderShrink(BorderShrink border) {
		settings.getConfig().set("feature.border.shrinkAt", border.name());
		settings.saveConfig();
	}
	
	public BorderShrink getBorderShrink() {
		return BorderShrink.valueOf(settings.getConfig().getString("feature.border.shrinkAt", BorderShrink.MEETUP.name()));
	}

	public boolean absorption() {
		return settings.getConfig().getBoolean("feature.absorption.enabled", false);
	}
	
	public void setAbsorption(boolean enable) {
		settings.getConfig().set("feature.absorption.enabled", enable);
		settings.saveConfig();
	}

	public boolean goldenHeads() {
		return settings.getConfig().getBoolean("feature.goldenheads.enabled", true);
	}
	
	public void setGoldenHeads(boolean enable) {
		settings.getConfig().set("feature.goldenheads.enabled", enable);
		settings.saveConfig();
	}
	
	public int goldenHeadsHeal() {
		return settings.getConfig().getInt("feature.goldenheads.heal", 4);
	}
	
	public void setGoldenHeadsHeal(int heal) {
		settings.getConfig().set("feature.goldenheads.heal", heal);
		settings.saveConfig();
	}

	public boolean pearlDamage() {
		return settings.getConfig().getBoolean("feature.pearldamage.enabled", false);
	}
	
	public void setPearlDamage(boolean enable) {
		settings.getConfig().set("feature.pearldamage.enabled", enable);
		settings.saveConfig();
	}

	public boolean notchApples() {
		return settings.getConfig().getBoolean("feature.notchapples.enabled", true);
	}
	
	public void setNotchApples(boolean enable) {
		settings.getConfig().set("feature.notchapples.enabled", enable);
		settings.saveConfig();
	}

	public String getScenarios() {
		return settings.getConfig().getString("game.scenarios", "games scheduled");
	}
	
	public void setScenarios(String sceanrios) {
		settings.getConfig().set("game.scenarios", sceanrios);
		settings.saveConfig();
	}

	public boolean isMuted() {
		return settings.getConfig().getBoolean("muted", false);
	}

	public void setMuted(boolean mute) {
		settings.getConfig().set("muted", mute);
		settings.saveConfig();
	}

	public boolean deathLightning() {
		return settings.getConfig().getBoolean("feature.deathlightning.enabled", true);
	}
	
	public void setDeathLightning(boolean enable) {
		settings.getConfig().set("feature.deathlightning.enabled", enable);
		settings.saveConfig();
	}

	public boolean nether() {
		return settings.getConfig().getBoolean("feature.nether.enabled", false);
	}
	
	public void setNether(boolean enable) {
		settings.getConfig().set("feature.nether.enabled", enable);
		settings.saveConfig();
	}

	public boolean theEnd() {
		return settings.getConfig().getBoolean("feature.theend.enabled", false);
	}
	
	public void setTheEnd(boolean enable) {
		settings.getConfig().set("feature.theend.enabled", enable);
		settings.saveConfig();
	}

	public boolean ghastDropGold() {
		return settings.getConfig().getBoolean("feature.ghastdrops.enabled", true);
	}
	
	public void setGhastDropGold(boolean enable) {
		settings.getConfig().set("feature.ghastdrops.enabled", enable);
		settings.saveConfig();
	}

	public boolean nerfedStrength() {
		return settings.getConfig().getBoolean("feature.nerfedStrength.enabled", true);
	}
	
	public void setNerfedStrength(boolean enable) {
		settings.getConfig().set("feature.nerfedStrength.enabled", enable);
		settings.saveConfig();
	}

	public boolean tabShowsHealthColor() {
		return settings.getConfig().getBoolean("feature.tabcolors.enabled", false);
	}
	
	public void setTabShowsHealthColor(boolean enable) {
		settings.getConfig().set("feature.tabcolors.enabled", enable);
		settings.saveConfig();
	}

	public boolean goldenMelonNeedsIngots() {
		return settings.getConfig().getBoolean("feature.harderCrafting.enabled", true);
	}
	
	public void setGoldenMelonNeedsIngots(boolean enable) {
		settings.getConfig().set("feature.harderCrafting.enabled", enable);
		settings.saveConfig();
	}

	public boolean shears() {
		return settings.getConfig().getBoolean("rates.shears.enabled", true);
	}
	
	public void setShears(boolean enable) {
		settings.getConfig().set("rates.shears.enabled", enable);
		settings.saveConfig();
	}
	
	public int getShearRates() {
		return settings.getConfig().getInt("rates.shears.rate", 5);
	}
	
	public void setShearRates(int shearRate) {
		settings.getConfig().set("rates.shears.rate", shearRate);
		settings.saveConfig();
	}
	
	public int getFlintRates() {
		return settings.getConfig().getInt("rates.flint.rate", 35);
	}
	
	public void setFlintRates(int flintRate) {
		settings.getConfig().set("rates.flint.rate", flintRate);
		settings.saveConfig();
	}

	public boolean pregameBoard() {
		return settings.getData().getBoolean("boards.pregame", false);
	}
	
	public void setPregameBoard(boolean enable) {
		settings.getData().set("boards.pregame", enable);
		settings.saveConfig();
	}

	public boolean arenaBoard() {
		return settings.getData().getBoolean("boards.arena", false);
	}
	
	public void setArenaBoard(boolean enable) {
		settings.getData().set("boards.arena", enable);
		settings.saveData();
	}

	public boolean teamManagement() {
		return settings.getData().getBoolean("teams.management", false);
	}
	
	public void setTeamManagement(boolean enable) {
		settings.getData().set("teams.management", enable);
		settings.saveData();
	}

	public boolean isRecordedRound() {
		return settings.getConfig().getBoolean("rr.state", false);
	}
	
	public void setRR(boolean enable) {
		settings.getConfig().set("rr.state", enable);
		settings.saveData();
		
		for (Player online : PlayerUtils.getPlayers()) {
			PacketUtils.setTabList(online);
		}
	}

	public String getRRName() {
		return settings.getConfig().getString("rr.name", "ANAMEHEREPLEASE");
	}
	
	public void setRRName(String name) {
		settings.getConfig().set("rr.name", name);
		settings.saveConfig();
	}

	public String getHost() {
		return settings.getConfig().getString("game.host", "None");
	}

	public boolean tier2() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean splash() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getAppleRates() {
		// TODO Auto-generated method stub
		return 1;
	}

	public int getPvP() {
		// TODO Auto-generated method stub
		return 3;
	}

	public int getMeetup() {
		// TODO Auto-generated method stub
		return 4;
	}

	public boolean antiStripmine() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean horses() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean horseHealing() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean horseArmor() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getMaxPlayers() {
		return settings.getConfig().getInt("maxplayers", 150);
	}

	public String getMatchPost() {
		return settings.getConfig().getString("matchpost", "redd.it");
	}

	public World getWorld() {
		return Bukkit.getWorld(settings.getConfig().getString("game.world", "leon"));
	}
}