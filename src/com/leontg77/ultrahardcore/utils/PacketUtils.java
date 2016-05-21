package com.leontg77.ultrahardcore.utils;

import java.lang.reflect.Field;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

/**
 * Packet utilities class.
 * <p>
 * Contains methods for sending action titles, normal titles and setting the tab list for players.
 * 
 * @author LeonTG77
 */
public class PacketUtils {

    /**
     * Sets a tablist for the given player.
     *
     * @param player the player.
     */
    public static void setTabList(Player player, Main plugin, Game game) {
        if (game.isRecordedRound()) {
            return;
        }
        
        Format date = new SimpleDateFormat("HH:mm:ss 'UTC'", Locale.US);
        String dateStr = date.format(new Date());

        double tps = plugin.getTps();
        ChatColor color;

        if (tps >= 21) {
            color = ChatColor.GRAY;
        } else if (tps == 20.0) {
            color = ChatColor.GREEN;
        } else if (tps >= 17) {
            color = ChatColor.GREEN;
        } else if (tps >= 14) {
            color = ChatColor.GOLD;
        } else {
            color = ChatColor.RED;
        }

        int ping = plugin.getUser(player).getPing();
        ChatColor pingColor;

        if (ping < 0) {
            pingColor = ChatColor.RED;
        } else if (ping < 75) {
            pingColor = ChatColor.GREEN;
        } else if (ping < 150) {
            pingColor = ChatColor.DARK_GREEN;
        } else if (ping < 250) {
            pingColor = ChatColor.GOLD;
        } else if (ping < 400) {
            pingColor = ChatColor.RED;
        } else {
            pingColor = ChatColor.DARK_RED;
        }

        IChatBaseComponent headerJSON = ChatSerializer.a(
              "{text:'§4§lArctic UHC§r §8- §a§o@ArcticUHC§r\n" +
            "§7Follow us for games and updates!\n" +
            "\n§7TPS: " + color + (tps >= 21 ? "§cCatching up after lag" : tps) + " §8- §7Your ping: §a" + pingColor + (ping == 0 ? "§cNot calculated" : ping) + " §8- §7Time: §a" + dateStr + "\n'}"
        );

        String gamemode = game.getAdvancedTeamSize(false, true).replaceAll("-", "§8-§7") + game.getScenarios().replaceAll(",", "§8,§7");
        String teamsize = game.getTeamSize().toLowerCase();

        IChatBaseComponent footerJSON = ChatSerializer.a(
            "{text:'\n§7" + gamemode + (!teamsize.startsWith("no") && !teamsize.startsWith("open") ?
            "\n§4Host §8» §a" + game.getHost() : "") + "'}"
        );

        PacketPlayOutPlayerListHeaderFooter headerPacket = new PacketPlayOutPlayerListHeaderFooter(headerJSON);

        try {
            Field field = headerPacket.getClass().getDeclaredField("b");
            field.setAccessible(true);
            field.set(headerPacket, footerJSON);
        } catch (Exception e) {
            Bukkit.getLogger().severe("§cCould not send tab list packets to " + player.getName());
            return;
        }

        sendPacket(player, headerPacket);
    }

    /**
     * Sends a message in action bar to the given player.
     *
     * @param player the player.
     * @param msg the message.
     */
    public static void sendAction(Player player, String msg) {
        IChatBaseComponent actionJSON = ChatSerializer.a("{text:\"" + msg + "\"}");
        PacketPlayOutChat actionPacket = new PacketPlayOutChat(actionJSON, (byte) 2);
        
        sendPacket(player, actionPacket);
    }

    /**
     * Sends a title message to the given player.
     *
     * @param player the player displaying to.
     * @param title the title.
     * @param subtitle the subtitle.
     *
     * @param in how long it uses to fade in.
     * @param out how long it uses to fade out.
     * @param stay how long it stays.
     */
    public static void sendTitle(Player player, String title, String subtitle, int in, int stay, int out) {
        IChatBaseComponent titleJSON = ChatSerializer.a("{'text': '" + title + "'}");
        IChatBaseComponent subtitleJSON = ChatSerializer.a("{'text': '" + subtitle + "'}");

        PacketPlayOutTitle timesPacket = new PacketPlayOutTitle(EnumTitleAction.TIMES, (IChatBaseComponent) null, in, stay, out);
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleJSON);
        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, subtitleJSON);

        sendPacket(player, timesPacket);
        sendPacket(player, titlePacket);
        sendPacket(player, subtitlePacket);
    }

    /**
     * Send the given packet to the given player.
     *
     * @param player The player to send it to.
     * @param packet The packet to send.
     */
    private static void sendPacket(Player player, Packet<?> packet) {
        CraftPlayer craft = (CraftPlayer) player;

        craft.getHandle().playerConnection.sendPacket(packet);
    }
}