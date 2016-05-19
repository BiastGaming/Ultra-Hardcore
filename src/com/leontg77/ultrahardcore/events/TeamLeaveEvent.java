package com.leontg77.ultrahardcore.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.scoreboard.Team;

/**
 * Team leave event class.
 * 
 * @author LeonTG77
 */
public class TeamLeaveEvent extends Event {
    private final OfflinePlayer player;
    private final Team team;

    /**
     * Team leave event class constructor.
     *
     * @param team The team leaving.
     * @param player the player leaving.
     */
    public TeamLeaveEvent(Team team, OfflinePlayer player) {
        this.player = player;
        this.team = team;
    }

    /**
     * Get the player that left the team.
     * 
     * @return The player.
     */
    public OfflinePlayer getPlayer() {
        return player;
    }
    
    /**
     * Get the team the player left.
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