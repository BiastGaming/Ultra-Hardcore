package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * NoNameTags scenario class.
 * 
 * @author LeonTG77
 */
public class NoNameTags extends Scenario implements Listener {
    private final BoardManager board;
    private final TeamManager teams;

    public NoNameTags(BoardManager board, TeamManager teams) {
        super("NoNameTags", "Name tags are not visible.");

        this.board = board;
        this.teams = teams;
    }
    
    private Team soloTeam = null;
    
    @Override
    public void onDisable() {
        for (Team team : teams.getTeams()) {
            team.setNameTagVisibility(NameTagVisibility.ALWAYS);
        }
        
        for (OfflinePlayer offline : teams.getPlayers(soloTeam)) {
            teams.leaveTeam(offline, true);
        }
        
        soloTeam.unregister();
        soloTeam = null;
    }
    
    @Override
    public void onEnable() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.setWhitelisted(true);
        }
        
        for (Team team : teams.getTeams()) {
            team.setNameTagVisibility(NameTagVisibility.NEVER);
        }
        
        soloTeam = board.getBoard().registerNewTeam("soloNoNametag");
        
        soloTeam.setDisplayName("soloNoNametag");
        soloTeam.setPrefix("§f");
        soloTeam.setSuffix("§f");

        soloTeam.setNameTagVisibility(NameTagVisibility.NEVER);
        soloTeam.setCanSeeFriendlyInvisibles(false);
        soloTeam.setAllowFriendlyFire(true);
        
        for (OfflinePlayer offline : Bukkit.getWhitelistedPlayers()) {
            if (teams.getTeam(offline) == null) {
                teams.joinTeam(soloTeam, offline);
            }
        }
    }
    
    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        if (soloTeam == null) {
            return;
        }
        
        if (teams.getTeam(player) == null) {
            teams.joinTeam(soloTeam, player);
        }
    }
}