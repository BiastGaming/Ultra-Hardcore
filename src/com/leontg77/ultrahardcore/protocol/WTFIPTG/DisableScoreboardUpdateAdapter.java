package com.leontg77.ultrahardcore.protocol.WTFIPTG;

import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.leontg77.ultrahardcore.managers.SpecManager;

/**
 * Disable scoreboard updater packet class.
 * 
 * @author D4mnX
 */
public class DisableScoreboardUpdateAdapter extends DisablePacketAdapter {

    public DisableScoreboardUpdateAdapter(Plugin plugin, SpecManager spec) {
        super(plugin, spec, PacketType.Play.Server.SCOREBOARD_SCORE);
    }
}