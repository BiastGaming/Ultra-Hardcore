package com.leontg77.ultrahardcore.listeners;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.leontg77.ultrahardcore.events.PlayerLeaveEvent;

/**
 * Quit message listener class.
 * 
 * @author D4mnX
 */
public class QuitMessageListener extends AbstractAppender implements Listener {
	private static final Pattern LOG_PATTERM = Pattern.compile("([a-zA-Z0-9_]{1,16}) lost connection: (.*)");
	
	private static final Map<String, LogoutReason> STATIC_QUIT_REASONS = ImmutableMap.of(
            "Disconnected", LogoutReason.LEFT,
            "Timed out", LogoutReason.TIMED_OUT
    );

    private final Map<String, LogoutReason> storedReasons = Maps.newHashMap();	

    public QuitMessageListener() {
        super(QuitMessageListener.class.getName(), null, null);
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        LogoutReason reason = storedReasons.remove(player.getName());
        
        if (reason == null) {
            reason = LogoutReason.UNKNOWN;
        }

        PlayerLeaveEvent leave = new PlayerLeaveEvent(player, event.getQuitMessage(), reason);
        Bukkit.getPluginManager().callEvent(leave);
        
        event.setQuitMessage(leave.getQuitMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void on(PlayerKickEvent event) {
    	Player player = event.getPlayer();
    	
        storedReasons.put(player.getName(), LogoutReason.KICKED);
    }

    @Override
    public void append(LogEvent logEvent) {
        if (logEvent.getLevel() != Level.INFO) {
            return;
        }

        String message = logEvent.getMessage().getFormattedMessage();
        Matcher matcher = LOG_PATTERM.matcher(message);
        
        if (!matcher.find()) {
            return;
        }

        String playerName = matcher.group(1);
        String quitReason = matcher.group(2);

        if (storedReasons.get(playerName) == LogoutReason.KICKED) {
            return;
        }

        LogoutReason quitType = STATIC_QUIT_REASONS.getOrDefault(quitReason, LogoutReason.CRASHED);
        storedReasons.put(playerName, quitType);
    }
    
    /**
     * Logout reason enum types.
     * 
     * @author D4mnX
     */
    public enum LogoutReason {
        LEFT,
        KICKED,
        CRASHED,
        TIMED_OUT,
        UNKNOWN;
    }
}