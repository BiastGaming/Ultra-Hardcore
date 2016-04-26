package com.leontg77.ultrahardcore.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Ping concealer packet class.
 * 
 * @author D4mnX
 * @see https://github.com/D4mnX/LMVB/blob/1.0.0-RC1/src/main/java/de/web/paulschwandes/lmvb/PingConcealer.java
 * @see licenses/LMVB
 */
public class PingConcealer extends PacketAdapter {

	/**
	 * Ping concealer class constructor.
	 * 
	 * @param plugin The plugin class.
	 */
    public PingConcealer(Plugin plugin) {
        super(plugin, PacketType.Play.Server.PLAYER_INFO);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        StructureModifier<List<PlayerInfoData>> playerInfoDataLists = packet.getPlayerInfoDataLists();
        
        List<PlayerInfoData> oldList = ImmutableList.copyOf(playerInfoDataLists.read(0));
        List<PlayerInfoData> newList = Lists.transform(oldList, new Function<PlayerInfoData, PlayerInfoData>() {
            @Override
            public PlayerInfoData apply(PlayerInfoData playerInfoData) {
                return transform(playerInfoData);
            }
        });
        
        playerInfoDataLists.write(0, ImmutableList.copyOf(newList));
    }

    /**
     * Transform the given info data into the new one.
     * 
     * @param data The old data.
     * @return The new data.
     */
    protected PlayerInfoData transform(PlayerInfoData data) {
        WrappedChatComponent displayName = data.getDisplayName();
        EnumWrappers.NativeGameMode gameMode = data.getGameMode();
        WrappedGameProfile profile = data.getProfile();
        
        int actualPing = data.getPing();
        int concealedPing = getConcealedPing(actualPing);

        return new PlayerInfoData(profile, concealedPing, gameMode, displayName);
    }

    /**
     * Get the concealed ping from the given ping.
     * 
     * @param actualPing The actual ping to use.
     * @return The random ping
     */
    protected int getConcealedPing(int actualPing) {
        if (actualPing < 150) {
        	return randomNumber(1, 149);
        }
        
        if (actualPing < 300) {
        	return randomNumber(151, 299);
        }
        
        if (actualPing < 600) {
        	return randomNumber(301, 599);
        }
        
        if (actualPing < 1000) {
        	return randomNumber(601, 999);
        }
        
        return randomNumber(1001, actualPing);
    }

    /**
     * Get a random integer between the 2 given ints
     * 
     * @param min The minimum int.
     * @param max The maximum int.
     * @return The random integer.
     */
    protected int randomNumber(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max+1);
    }
}