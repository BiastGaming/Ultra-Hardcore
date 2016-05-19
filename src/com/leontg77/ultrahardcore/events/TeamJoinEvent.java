package com.leontg77.ultrahardcore.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.scoreboard.Team;

/**
 * Team join event class.
 * 
 * @author LeonTG77
 */
public class TeamJoinEvent extends Event {
    private final OfflinePlayer player;
    private final Team team;

    /**
     * Team join event class constructor.
     *
     * @param team The team joining.
     * @param player the player joining.
     */
    public TeamJoinEvent(Team team, OfflinePlayer player) {
        this.player = player;
        this.team = team;
    }

    /**
     * Get the player that joined the team.
     * 
     * @return The player.
     */
    public OfflinePlayer getPlayer() {
        return player;
    }
    
    /**
     * Get the team the player joined.
     * 
     * @return The team.
     */
    public Team getTeam() {
        return team;
    }
    
    private static final HandlerList HANDLERS = new HandlerList();
    
    public HandlerList getHandlers() {
        return HANDLERS;
    }
     
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}