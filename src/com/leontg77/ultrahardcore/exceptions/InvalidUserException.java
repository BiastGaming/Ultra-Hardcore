package com.leontg77.ultrahardcore.exceptions;

/**
 * Invalid user exception class.
 * 
 * @author LeonTG77
 */
public class InvalidUserException extends CommandException {
    private static final long serialVersionUID = 1L;

    public InvalidUserException(String name) {
        super("'" + name + "' has never joined this server.");
    }
}