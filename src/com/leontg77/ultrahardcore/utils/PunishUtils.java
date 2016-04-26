package com.leontg77.ultrahardcore.utils;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
	
	public static void savePunishment(User user, PunishmentType type, Date date, String punishReason) {
		List<String> list = user.getFile().getStringList("punishmentlist");
		Format dateFormat = new SimpleDateFormat("dd/MM/yyyy '@' HH:mm", Locale.US); 
		
		String format = "§8(" + type.getColor() + NameUtils.capitalizeString(type.name(), false) + "§8) §a" + punishReason + " §8» §7" + dateFormat.format(date);
		
		if (!list.contains(format)) {
			list.add(format);
			user.getFile().set("punishmentlist", list);
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