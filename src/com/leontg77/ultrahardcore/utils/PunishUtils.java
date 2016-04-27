package com.leontg77.ultrahardcore.utils;

import java.util.Date;

import org.bukkit.ChatColor;

import com.leontg77.ultrahardcore.User;

/**
 * Punishment releated util methods class.
 * 
 * @author LeonTG77
 */
public class PunishUtils {

	public static String getBanReasonFormat() {
		return
		"§8» §7You have been §4banned §7from §6Arctic UHC §8«" +
		"\n" + 
		"\n§cReason §8» §7%s" + 
		"\n§cBanned by §8» §7%s" + 
		"\n" +
		"\n§8» §7If you would like to appeal, DM our twitter §a@ArcticUHC §8«"
		;
	}

	public static String getIPBanReasonFormat() {
		return
		"§8» §7You have been §4IP banned §7from §6Arctic UHC §8«" +
		"\n" + 
		"\n§cReason §8» §7%s" + 
		"\n§cBanned by §8» §7%s" + 
		"\n" +
		"\n§8» §7If you would like to appeal, DM our twitter §a@ArcticUHC §8«"
		;
	}

	public static String getTempbanReasonFormat() {
		return 
		"§8» §7You have been §4temp-banned §7from §6Arctic UHC §8«" +
		"\n" + 
		"\n§cReason §8» §7%s" + 
		"\n§cBanned by §8» §7%s" + 
		"\n§cExpires in §8» §7%s" + 
		"\n" +
		"\n§8» §7If you would like to appeal, DM our twitter §a@ArcticUHC §8«"
		;
	}

	public static String getDQReasonFormat() {
		return 
		"§8» §7You have been §cdisqualified §7from this game §8«" +
		"\n" + 
		"\n§cReason §8» §7%s" + 
		"\n§cDQ'ed by §8» §7%s" + 
		"\n" + 
		"\n§8» §7Don't worry, this is not a perma ban. §8«"
		;
	}
	
	public static void savePunishment(User user, PunishmentType type, String punishReason, Date to) {
		int count = 1;
		
		if (user.getFile().contains("punishments")) {
			count = user.getFile().getConfigurationSection("punishments").getKeys(false).size() + 1;
		}

		user.getFile().set("punishments." + count + ".type", type.name());
		user.getFile().set("punishments." + count + ".reason", punishReason);
		user.getFile().set("punishments." + count + ".created", new Date().getTime());
		user.getFile().set("punishments." + count + ".expires", to == null ? -1l : to.getTime());
		user.saveFile();
	}
	
	public static void setPunishmentExpireToNow(User user, PunishmentType type, long oldExpire) {
		if (!user.getFile().contains("punishments")) {
			return;
		}

		for (String punish : user.getFile().getConfigurationSection("punishments").getKeys(false)) {
			if (!user.getFile().getString("punishments." + punish + ".type", "none").equals(type.name())) {
				continue;
			}
			
			if (user.getFile().getLong("punishments." + punish + ".expires", -2l) == oldExpire) {
				user.getFile().set("punishments." + punish + ".expires", new Date().getTime());
				user.saveFile();
				break;
			}
		}
	}
	
	public enum PunishmentType {
		BAN(ChatColor.DARK_RED),
		KICK(ChatColor.YELLOW),
		MUTE(ChatColor.BLUE), 
		DISQUALIFY(ChatColor.GOLD), 
		TEMPBAN(ChatColor.RED);
		
		private final ChatColor punishColor;
		
		private PunishmentType(ChatColor punishColor) {
			this.punishColor = punishColor;
		}
		
		public ChatColor getColor() {
			return punishColor;
		}
	}
}