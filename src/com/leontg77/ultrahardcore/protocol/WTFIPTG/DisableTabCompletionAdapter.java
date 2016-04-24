package com.leontg77.ultrahardcore.protocol.WTFIPTG;

import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.leontg77.ultrahardcore.managers.SpecManager;

/**
 * Disable tab complete packet class.
 * 
 * @author D4mnX
 */
public class DisableTabCompletionAdapter extends DisablePacketAdapter {

    public DisableTabCompletionAdapter(Plugin plugin, SpecManager spec) {
        super(plugin, spec, PacketType.Play.Client.TAB_COMPLETE, PacketType.Play.Server.TAB_COMPLETE);
    }
}