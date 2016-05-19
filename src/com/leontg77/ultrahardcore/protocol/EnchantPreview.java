package com.leontg77.ultrahardcore.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.leontg77.ultrahardcore.Main;

/**
 * Enchant Preview class.
 * <p> 
 * This disables the enchantment preview.
 *
 * @author LeonTG77
 */
public class EnchantPreview extends PacketAdapter {
    private final ProtocolManager manager;

    /**
     * Constructor for Enchant Preview.
     *
     * @param plugin The main class of the plugin.
     */
    public EnchantPreview(Main plugin) {
        super(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.CRAFT_PROGRESS_BAR);

        manager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (!event.getPacketType().equals(Play.Server.CRAFT_PROGRESS_BAR)) {
            return;
        }
        
        StructureModifier<Integer> integers = event.getPacket().getIntegers();
        int property = integers.read(1);
        
        if (property >= 4) {
            integers.write(2, -1);
        }
    }
    
    /**
     * Enable the old enchants.
     */
    public void enable() {
        manager.addPacketListener(this);
    }    
    
    /**
     * Disable the old enchants.
     */
    public void disable() {
        manager.removePacketListener(this);
    }
}