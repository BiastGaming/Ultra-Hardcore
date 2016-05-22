package com.leontg77.ultrahardcore.protocol.wtfiptg;

import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.leontg77.ultrahardcore.managers.SpecManager;

/**
 * Disable Packet packet class.
 * 
 * @author D4mnX
 */
public class DisablePacketAdapter extends PacketAdapter {
    private final SpecManager spec;

    public DisablePacketAdapter(Plugin plugin, SpecManager spec, PacketType... types) {
        super(plugin, types);
        
        this.spec = spec;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        handlePacket(event);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        handlePacket(event);
    }

    protected void handlePacket(PacketEvent event) {
        if (spec.isSpectating(event.getPlayer())) {
            return;
        }
        
        event.setCancelled(true);
    }
}