package com.leontg77.ultrahardcore;

import org.bukkit.Bukkit;

/**
 * The game state class.
 * 
 * @author LeonTG77
 */
public enum State {
    NOT_RUNNING, OPEN, CLOSED, SCATTER, INGAME, ENDING;

    private static Settings settings;

    /**
     * Set the instance of the timer to the givne instance.
     *
     * @param game The timer instance.
     */
    public static void setSettings(Settings setting) {
        settings = setting;
    }

    private static State currentState;

    /**
     * Sets the current state to be #.
     *
     * @param state the state setting it to.
     */
    public static void setState(State state) {
        currentState = state;

        settings.getConfig().set("state", state.name());
        settings.saveConfig();
    }

    /**
     * Checks if the state is #.
     *
     * @param state The state checking.
     * @return True if it's the given state.
     */
    public static boolean isState(State state) {
        return getState() == state;
    }

    /**
     * Gets the current state.
     *
     * @return The state
     */
    public static State getState() {
        if (currentState == null) {
            State state;

            try {
                state = State.valueOf(settings.getConfig().getString("state"));
            } catch (Exception e) {
                Bukkit.getLogger().warning("Setting the state to NOT_RUNNING as it can't find the saved one!");
                state = State.NOT_RUNNING;
            }

            currentState = state;
        }

        return currentState;
    }
}