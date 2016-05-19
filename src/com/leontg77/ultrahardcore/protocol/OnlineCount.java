package com.leontg77.ultrahardcore.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;

/**
 * Online count class.
 * <p> 
 * Makes the server list not count spectators.
 *
 * @author LeonTG77
 */
public class OnlineCount extends PacketAdapter {
    private final ProtocolManager manager;

    private final Main plugin;
    private final Game game;

    /**
     * Constructor for Online count.
     *
     * @param plugin The main class of the plugin.
     */
    public OnlineCount(Main plugin, Game game) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Status.Server.OUT_SERVER_INFO);

        this.plugin = plugin;
        this.game = game;

        manager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (!event.getPacketType().equals(PacketType.Status.Server.OUT_SERVER_INFO)) {
            return;
        }

        int count = plugin.getOnlineCount();
        
        if (State.isState(State.INGAME)) {
            count = game.getPlayers().size();
        }
        
        if (game.isPrivateGame() || game.isRecordedRound()) {
            count = 0;
        }
        
        WrappedServerPing ping = event.getPacket().getServerPings().read(0);
        ping.setPlayersOnline(count);
        
        event.getPacket().getServerPings().write(0, ping);
    }
    
    /**
     * Enable the online counter.
     */
    public void enable() {
        manager.addPacketListener(this);
    }    
}