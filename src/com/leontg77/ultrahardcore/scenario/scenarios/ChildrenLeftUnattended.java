package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * ChildrenLeftUnattended scenario class.
 * 
 * @author LeonTG77
 */
public class ChildrenLeftUnattended extends Scenario implements Listener {
    private final TeamManager teams;
    private final ItemStack potion;

    public ChildrenLeftUnattended(TeamManager teams) {
        super("ChildrenLeftUnattended", "When one teammate dies the other(s) will get a speed potion (espresso) and a tamed wolf (free puppy).");
        
        this.teams = teams;
        
        Potion pot = new Potion(PotionType.SPEED);
        pot.setLevel(1);
        
        this.potion = pot.toItemStack(1);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerDeathEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Player player = event.getEntity();
        Team team = teams.getTeam(player);
        
        if (team == null) {
            return;
        }
        
        teams.leaveTeam(player, false);
        
        for (OfflinePlayer teammate : teams.getPlayers(team)) {
            Player online = teammate.getPlayer();
            
            if (online != null) {
                PlayerUtils.giveItem(online, potion);
                
                Wolf wolf = online.getWorld().spawn(online.getLocation(), Wolf.class);
                
                wolf.setTamed(true);
                wolf.setOwner(online);
            }
        }
    }
}