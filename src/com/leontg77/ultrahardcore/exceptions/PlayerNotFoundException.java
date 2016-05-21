package com.leontg77.ultrahardcore.exceptions;

/**
 * Player not found exception class.
 * 
 * @author LeonTG77
 */
public class PlayerNotFoundException extends CommandException {
    private static final long serialVersionUID = 1L;

    public PlayerNotFoundException(String playerName) {
        super("'" + playerName + "' is not online.");
    }
}