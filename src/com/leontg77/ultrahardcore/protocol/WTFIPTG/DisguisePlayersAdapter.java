package com.leontg77.ultrahardcore.protocol.WTFIPTG;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.Lists;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.scenario.scenarios.WTFIPTG.FakePlayer;

/**
 * Disguise player packet class.
 * 
 * @author D4mnX
 */
public class DisguisePlayersAdapter extends PacketAdapter {
    private final FakePlayer fakePlayer;
    private final SpecManager spec;
    private final PlayerInfoData infoData;

    public DisguisePlayersAdapter(Plugin plugin, FakePlayer fakePlayer, SpecManager spec) {
        super(plugin, PacketType.Play.Server.NAMED_ENTITY_SPAWN, PacketType.Play.Server.PLAYER_INFO);

        this.fakePlayer = fakePlayer;
        this.spec = spec;
        this.infoData = new PlayerInfoData(new WrappedGameProfile(fakePlayer.getUUID(), fakePlayer.getName()), 0, EnumWrappers.NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(fakePlayer.getName()));
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Player reciever = event.getPlayer();
        
        if (spec.isSpectating(reciever)) {
        	return;
        }

        PacketContainer packet = event.getPacket();
        PacketType type = packet.getType();
        
        if (type == PacketType.Play.Server.NAMED_ENTITY_SPAWN) {
            packet.getModifier().write(1, fakePlayer.getUUID());
        } else if (type == PacketType.Play.Server.PLAYER_INFO) {
            packet.getPlayerInfoDataLists().write(0, Lists.newArrayList(infoData));
        }
    }
}