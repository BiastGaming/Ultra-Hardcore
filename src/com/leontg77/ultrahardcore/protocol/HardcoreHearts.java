package com.leontg77.ultrahardcore.protocol;

import com.comphenix.protocol.PacketType.Play;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.leontg77.ultrahardcore.Main;

/**
 * Hardcore hearts class.
 * <p> 
 * This class manages the hardcore hearts feature.
 *
 * @author ghowden
 */
public class HardcoreHearts extends PacketAdapter {
	private final ProtocolManager manager = ProtocolLibrary.getProtocolManager();

	/**
	 * Constructor for HardcoreHearts.
	 * 
	 * @param plugin The main class of the plugin.
	 */
	public HardcoreHearts(Main plugin) {
		super(plugin, ListenerPriority.NORMAL, Play.Server.LOGIN);
	}

    @Override
    public void onPacketSending(PacketEvent event) {
        if (!event.getPacketType().equals(Play.Server.LOGIN)) {
        	return;
        }
        
        event.getPacket().getBooleans().write(0, true);
    }
    
    /**
     * Enable the hardcore hearts.
     */
    public void enable() {
	    manager.addPacketListener(this);
    }    
    
    /**
     * Disable the hardcore hearts.
     */
    public void disable() {
	    manager.removePacketListener(this);
    }
}	
