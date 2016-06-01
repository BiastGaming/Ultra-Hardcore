package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * SecretTeams scenario class.
 * 
 * @author LeonTG77
 */
public class SecretTeams extends Scenario {
    private final TeamDisablePacket packet;
    private final ProtocolManager manager;

    private final TeamManager teams;
    
    public SecretTeams(TeamManager teams) {
        super("SecretTeams", "It starts out as teams of 2 but they're mixed together and forms a to4, however no one know who is on what team.");
        
        this.manager = ProtocolLibrary.getProtocolManager();
        this.packet = new TeamDisablePacket(plugin);
        
        this.teams = teams;
    }
    
    @Override
    public void onDisable() {
        manager.removePacketListener(packet);
        
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.kickPlayer("Updating teams...");
        }
        
        teams.setup();
    }
    
    @Override
    public void onEnable() {
        for (Player online : Bukkit.getOnlinePlayers()) {
            online.kickPlayer("Updating tab team sorting and color...");
        }
        
        for (Team team : teams.getTeams()) {
            team.setPrefix("§f");
        }
        
        manager.addPacketListener(packet);
    }
    
    /**
     * Team disable packet adapter.
     * 
     * @author LeonTG77
     */
    public class TeamDisablePacket extends PacketAdapter {

        public TeamDisablePacket(Main plugin) {
            super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.SCOREBOARD_TEAM);
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            if (!event.getPacketType().equals(PacketType.Play.Server.SCOREBOARD_TEAM)) {
                return;
            }

            event.setCancelled(true);
        }
    }
}