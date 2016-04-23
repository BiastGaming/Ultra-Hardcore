package com.leontg77.ultrahardcore.protocol;

import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

/**
 * Fake Health protocol class.
 * 
 * @author ghowden
 * @see https://github.com/UltraSoftcore/hiddenhealth/blob/1.1/src/main/java/gg/uhc/hiddenhealth/HealthUpdatePacketAdapter.java
 * @see licenses/hiddenhealth
 */
public class HealthUpdatePacketAdapter extends PacketAdapter {
	// the packet identifier for 0x06 Update Health, also contains hunger + saturation
    protected static final PacketType UPDATE_HEALTH_PACKET = PacketType.Play.Server.UPDATE_HEALTH;

    // the packet identifier for 0x1C entity metadata, client will update it's visual health
    // when any metadata sent to itself if sent with the health flag on it
    protected static final PacketType ENTITY_METADATA = PacketType.Play.Server.ENTITY_METADATA;

	private final ProtocolManager manager;

    public HealthUpdatePacketAdapter(Plugin plugin) {
        super(plugin, ListenerPriority.HIGHEST, UPDATE_HEALTH_PACKET, ENTITY_METADATA);
		
		this.manager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (event.getPacketType() == UPDATE_HEALTH_PACKET) {
            // check if they're not dead, stops VERY buggy behaviour on death
            if (event.getPacket().getFloat().read(0) > 0) {
                event.getPacket().getFloat().write(0, 20f);
            }
            return;
        }

        if (event.getPacketType() == ENTITY_METADATA) {
            // only process if it's metadata for the player it's being sent to
            if (event.getPlayer().getEntityId() != event.getPacket().getIntegers().read(0)) {
            	return;
            }

            // get the data watcher for the entity metadata
            WrappedDataWatcher dataWatcher = new WrappedDataWatcher(event.getPacket().getWatchableCollectionModifier().read(0));

            // get the health flag from index 6 of the metadata
            Float health = dataWatcher.getFloat(6);

            // if this metadata contained a health flag (!= null) and not dead (to avoid buggy client behaviour)
            if (health != null && health > 0) {
                // set the health flag to the value
                dataWatcher.setObject(6, 20f);
            }
        }
    }
    
    /**
     * Enable the fake hearts.
     */
    public void enable() {
	    manager.addPacketListener(this);
    }    
    
    /**
     * Disable the fake hearts.
     */
    public void disable() {
	    manager.removePacketListener(this);
    }
}