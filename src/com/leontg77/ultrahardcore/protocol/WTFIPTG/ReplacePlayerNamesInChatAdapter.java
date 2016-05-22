package com.leontg77.ultrahardcore.protocol.wtfiptg;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.collect.Lists;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.scenario.scenarios.WTFIPTG.FakePlayer;

/**
 * ReplacePlayerNamesInChatAdapter packet class.
 * 
 * @author D4mnX
 */
public class ReplacePlayerNamesInChatAdapter extends PacketAdapter {
    private final SpecManager spec;
    private final FakePlayer fake;

    public ReplacePlayerNamesInChatAdapter(Main plugin, FakePlayer fake, SpecManager spec) {
        super(plugin, PacketType.Play.Server.CHAT);
        
        this.fake = fake;
        this.spec = spec;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Player reciever = event.getPlayer();
        
        if (spec.isSpectating(reciever)) {
            return;
        }

        PacketContainer packet = event.getPacket();
        List<WrappedChatComponent> messages = getAllChatComponents(packet);
        
        if (messages.isEmpty()) {
            return;
        }

        for (WrappedChatComponent message : messages) {
            String json = message.getJson();
            
            for (Player notExempted : Bukkit.getOnlinePlayers()) {
                if (spec.isSpectating(notExempted)) {
                    continue;
                }

                json = json.replace(notExempted.getName(), fake.getName());
            }
            
            message.setJson(json);
        }
    }

    /**
     * Get all the chat components from the given packet.
     * 
     * @param packet The packet to use.
     * @return The chat components.
     */
    private List<WrappedChatComponent> getAllChatComponents(PacketContainer packet) {
        List<WrappedChatComponent> messages = Lists.newArrayList(packet.getChatComponents().getValues());
        
        for (WrappedChatComponent[] wrappedChatComponents : packet.getChatComponentArrays().getValues()) {
            Collections.addAll(messages, wrappedChatComponents);
        }
        
        return messages;
    }
}