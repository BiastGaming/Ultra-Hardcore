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

    /**
     * Get the ban message format to use on the ban screen.
     * <p>
     * First format string is the reason, second is the ban source.
     *
     * @return The ban message format.
     */
    public static String getBanMessageFormat() {
        return
        "§8» §7You have been §4banned §7from §6Arctic UHC §8«" +
        "\n" +
        "\n§cReason §8» §7%s" +
        "\n§cBanned by §8» §7%s" +
        "\n" +
        "\n§8» §7If you would like to appeal, DM our twitter §a@ArcticUHC §8«"
        ;
    }

    /**
     * Get the ip ban message format to use on the ip ban screen.
     * <p>
     * First format string is the reason, second is the ip ban source.
     *
     * @return The ip ban message format.
     */
    public static String getIPBanMessageFormat() {
        return
        "§8» §7You have been §4IP banned §7from §6Arctic UHC §8«" +
        "\n" +
        "\n§cReason §8» §7%s" +
        "\n§cBanned by §8» §7%s" +
        "\n" +
        "\n§8» §7If you would like to appeal, DM our twitter §a@ArcticUHC §8«"
        ;
    }

    /**
     * Get the tempban message format to use on the tempban screen.
     * <p>
     * First format string is the reason, second is the ban source, third is how long until it expires.
     *
     * @return The tempban message format.
     */
    public static String getTempbanMessageFormat() {
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

    /**
     * Get the dq message format to use on the dq screen.
     * <p>
     * First format string is the reason, second is the dq source.
     *
     * @return The dq message format.
     */
    public static String getDQMessageFormat() {
        return
        "§8» §7You have been §cdisqualified §7from this game §8«" +
        "\n" +
        "\n§cReason §8» §7%s" +
        "\n§cDQ'ed by §8» §7%s" +
        "\n" +
        "\n§8» §7Don't worry, this is not a perma ban. §8«"
        ;
    }

    /**
     * Save the given punishment to the given user.
     *
     * @param user The user to punish.
     * @param type The type of punishment.
     * @param reason The reason of the punishment.
     * @param to When the punishment expires, null if forever.
     */
    public static void savePunishment(User user, PunishmentType type, String reason, Date to) {
        int count = 1;

        if (user.getFile().contains("punishments")) {
            count = user.getFile().getConfigurationSection("punishments").getKeys(false).size() + 1;
        }

        user.getFile().set("punishments." + count + ".type", type.name());
        user.getFile().set("punishments." + count + ".reason", reason);
        user.getFile().set("punishments." + count + ".created", new Date().getTime());
        user.getFile().set("punishments." + count + ".expires", to == null ? -1l : to.getTime());
        user.saveFile();
    }

    /**
     * Set the given users given punishment type's punishment to expire now.
     *
     * @param user The user that is expiring.
     * @param type The punishment type to expire.
     * @param oldExpire The old expire time.
     */
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

        for (String punish : user.getFile().getConfigurationSection("punishments").getKeys(false)) {
            long created = user.getFile().getLong("punishments." + punish + ".created", -2l);
            long expires = user.getFile().getLong("punishments." + punish + ".expires", -2l);

            if ((expires - created) < 300000) {
                user.getFile().set("punishments." + punish, null);
                user.saveFile();
                break;
            }
        }
    }

    /**
     * Punishment types enum class.
     *
     * @author LeonTG77
     */
    public enum PunishmentType {
        /**
         * Represents a kick punishment
         */
        KICK(ChatColor.YELLOW),
        /**
         * Represents a mute punishment
         */
        MUTE(ChatColor.BLUE),
        /**
         * Represents a dq punishment
         */
        DISQUALIFY(ChatColor.GOLD),
        /**
         * Represents a tempban punishment
         */
        TEMPBAN(ChatColor.RED),
        /**
         * Represents a ban punishment
         */
        BAN(ChatColor.DARK_RED);

        private final ChatColor punishColor;

        /**
         * Punishment typ class constructor.
         *
         * @param punishColor The color of the punishment.
         */
        private PunishmentType(ChatColor punishColor) {
            this.punishColor = punishColor;
        }

        /**
         * Get the color of the punishment.
         *
         * @return The color.
         */
        public ChatColor getColor() {
            return punishColor;
        }
    }
}