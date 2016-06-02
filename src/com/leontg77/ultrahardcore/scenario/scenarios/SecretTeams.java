package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
    protected final TeamDisablePacket packet;
    protected final ProtocolManager manager;

    protected final TeamManager teams;
    
    public SecretTeams(TeamManager teams) {
        super("SecretTeams", "It starts out as teams of 2 but they're mixed together and forms a to4, however no one know who is on what team.");
        
        this.manager = ProtocolLibrary.getProtocolManager();
        this.packet = new TeamDisablePacket(plugin);
        
        this.teams = teams;
    }

    protected static final Consumer<Player> KICK_PLAYER = online -> online.kickPlayer("Kicked to update tab colors and sorting...");
    
    @Override
    public void onDisable() {
        manager.removePacketListener(packet);
        
        Bukkit.getOnlinePlayers().forEach(KICK_PLAYER);
        teams.setup();
    }
    
    @Override
    public void onEnable() {
        manager.addPacketListener(packet);
        
        Bukkit.getOnlinePlayers().forEach(KICK_PLAYER);
        teams.getTeams().forEach(team -> team.setPrefix("§f"));
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