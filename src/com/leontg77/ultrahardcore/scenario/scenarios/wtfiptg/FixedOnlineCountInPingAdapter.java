package com.leontg77.ultrahardcore.scenario.scenarios.wtfiptg;

import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedServerPing;

public class FixedOnlineCountInPingAdapter extends PacketAdapter {

    protected static final PacketType PACKET_TYPE = PacketType.Status.Server.OUT_SERVER_INFO;

    protected final int onlineCount;

    public FixedOnlineCountInPingAdapter(Plugin plugin, int onlineCount) {
        super(plugin, PACKET_TYPE);
        this.onlineCount = onlineCount;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        // Apparantly you need to check if it's OUT_SERVER_INFO, because it'll try sending OUT_PING too...
        if (packet.getType() != PACKET_TYPE) return;

        StructureModifier<WrappedServerPing> pingModifier = packet.getServerPings();
        WrappedServerPing ping = pingModifier.read(0);
        ping.setPlayersOnline(onlineCount);
        pingModifier.write(0, ping);
    }
}