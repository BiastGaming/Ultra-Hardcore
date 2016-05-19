package com.leontg77.ultrahardcore.protocol;

import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

/**
 * Saturation concealer packet class.
 * 
 * @author D4mnX
 * @see https://github.com/D4mnX/LMVB/blob/1.0.0-RC1/src/main/java/de/web/paulschwandes/lmvb/SaturationConcealer.java
 * @see licenses/LMVB
 */
public class SaturationConcealer extends PacketAdapter {

    /**
     * Saturation concealer class constructor.
     *
     * @param plugin The plugin class.
     */
    public SaturationConcealer(Plugin plugin) {
        super(plugin, PacketType.Play.Server.UPDATE_HEALTH);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        packet.getFloat().write(1, 0.0f);
    }
}