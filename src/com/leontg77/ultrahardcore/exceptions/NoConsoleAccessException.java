package com.leontg77.ultrahardcore.exceptions;

/**
 * No console access exception class.
 * 
 * @author LeonTG77
 */
public class NoConsoleAccessException extends CommandException {
    private static final long serialVersionUID = 1L;

    public NoConsoleAccessException() {
        super("Only players can perform /%s.");
    }
}