package com.leontg77.ultrahardcore.protocol.WTFIPTG;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.base.Predicate;
import com.leontg77.ultrahardcore.managers.SpecManager;

/**
 * Hide players in spawn packet class.
 * 
 * @author D4mnX
 */
public class HidePlayersInSpecificWorldsAdapter extends PacketAdapter {
    private final Predicate<World> worldPredicate;
    private final SpecManager spec;

    public HidePlayersInSpecificWorldsAdapter(Plugin plugin, SpecManager spec, Predicate<World> worldPredicate) {
        super(plugin, PacketType.Play.Server.NAMED_ENTITY_SPAWN);

        this.worldPredicate = worldPredicate;
        this.spec = spec;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Player player = event.getPlayer();
        
        boolean isExempted = spec.isSpectating(player);
        boolean isInAffectedWorld = worldPredicate.apply(player.getWorld());

        if (!isExempted && isInAffectedWorld) {
            event.setCancelled(true);
        }
    }
}