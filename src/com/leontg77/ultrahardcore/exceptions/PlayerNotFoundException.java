package com.leontg77.ultrahardcore.exceptions;

import org.bukkit.ChatColor;

/**
 * Player not found exception class.
 * 
 * @author LeonTG77
 */
public class PlayerNotFoundException extends CommandException {
    private static final long serialVersionUID = 1L;

    public PlayerNotFoundException(String playerName) {
        super(ChatColor.RED + "'" + playerName + "' is not online.");
    }
}