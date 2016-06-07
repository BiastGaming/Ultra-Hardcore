package com.leontg77.ultrahardcore.scenario.scenarios.anonymous;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;

import com.comphenix.executors.BukkitExecutors;
import com.comphenix.executors.BukkitScheduledExecutorService;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.collect.Lists;

/**
 * @author ghowden, converted to from kotlin to java.
 * @see https://github.com/Eluinhost/anonymous
 */
public class DisguiseController implements Listener {
    protected final Plugin plugin;

    protected final UUID skinUUID;
    protected final String name;

    protected final ProfileParser profiles;
    protected final ProtocolManager manager;

    protected final BukkitScheduledExecutorService asyncExecutor;

    protected final WrappedChatComponent wrappedName;

    protected WrappedSignedProperty texture = null;
    
    /**
     * A listener that calls [onTabListPacket] when receiving a [tab list packet][PacketType.Play.Server.PLAYER_INFO]
     * and the player does not have the [bypass permission][SKIN_BYPASS_PERMISSION]. This means anyone with the
     * permission will *see* valid skins, not that people will see the player.
     */
    protected PacketAdapter tabListPlayersListener;

    /**
     * Handles disguising players on join by changing their skin/tab name/display name
     *
     * @param skinUUID uuid of the skin to display
     * @param name name of the player to show in tab/as display name
     * @param profiles used to fetch skin data to show to clients
     * @param plugin used to register events/packet listeners and for logging
     * @param refreshTime amount of time between trying to refresh the skin data in minutes
     * @param manager used to register packet listeners for modifying skins
     */
    public DisguiseController(UUID skinUUID, String name, ProfileParser profiles, Plugin plugin, long refreshTime, ProtocolManager manager) {
        this.skinUUID = skinUUID;
        this.name = name;

        this.profiles = profiles;
        this.manager = manager;

        this.plugin = plugin;

        this.asyncExecutor = BukkitExecutors.newAsynchronous(plugin);
        this.wrappedName = WrappedChatComponent.fromText(name);

        this.tabListPlayersListener = new PacketAdapter(plugin, PacketType.Play.Server.PLAYER_INFO) {
            public void onPacketSending(PacketEvent event) {
                onTabListPacket(event.getPacket());
            }
        };

        // add listener for rewriting tab packets
        manager.addPacketListener(tabListPlayersListener);

        // add listener for log in events
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        // force 'login event' for each player that is already online (/reload)
        // we don't rebuild the tab list so these players will need to reload
        // to see the new skin/names
        plugin.getServer().getOnlinePlayers().forEach(this::onPlayerLogin);

        // start updating the stored texture on a schedule, we run async because network could be involved
        asyncExecutor.scheduleAtFixedRate(this::updateSkin, 0, refreshTime, TimeUnit.MINUTES);
    }

    public void disable() {
        manager.removePacketListener(tabListPlayersListener);
        HandlerList.unregisterAll(this);
        
        Bukkit.getOnlinePlayers().forEach(online -> online.setDisplayName(null));
    }
    
    /**
     * Changes the textures and name for the player/s on the tab list for the receiver
     *
     * @param packet raw packet to be modified
     */
    public void onTabListPacket(PacketContainer packet) {
        packet.getPlayerInfoDataLists()
                .write(
                        0,
                        packet.getPlayerInfoDataLists().read(0).stream().map(it -> {
                            WrappedGameProfile profile = new WrappedGameProfile(it.getProfile().getUUID(), name);

                            profile.getProperties().putAll(it.getProfile().getProperties());
                            profile.getProperties().replaceValues("textures", texture == null ? Lists.newArrayList() : Lists.newArrayList(texture));

                            return new PlayerInfoData(profile, it.getPing(), it.getGameMode(), wrappedName);
                        }).collect(Collectors.toList())
                );
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(PlayerLoginEvent event) {
        onPlayerLogin(event.getPlayer());
    }

    /**
     * Called whenever a player logs in, simply modifies their display name so that other plugins can use it
     *
     * @param player the player that has just logged in
     */
    protected void onPlayerLogin(Player player) {
        player.setDisplayName(name);
    }

    /**
     * Attempts to update the [stored skin textures][texture] from the [provided profile fetcher][profiles]. Logs
     * information via [plugin] on success/failure.
     *
     * *NOTE: do not call this on the main thread as [profiles] can be working over the network*
     */
    protected void updateSkin() {
        plugin.getLogger().info("Starting update of skin texture");

        try {
            ParsedProfile profile = profiles.getForUuid(skinUUID);
            Optional<Property> x = profile.properties.stream().filter(it -> it.name.equals("textures")).findFirst();

            if (!x.isPresent()) {
                throw new IllegalArgumentException("Skin data missing textures property");
            }

            Property p = x.get();

            WrappedSignedProperty newTexture = WrappedSignedProperty.fromValues(p.name, p.value, p.signature);

            if (!newTexture.equals(texture)) {
                texture = newTexture;
                plugin.getLogger().info("Updated skin texture");
            } else {
                plugin.getLogger().info("Skin texture already up to date");
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
            plugin.getLogger().warning("Failed to fetch the skin data for the uuid $skinUUID");
        }
    }
}