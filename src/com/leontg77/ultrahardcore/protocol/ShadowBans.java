package com.leontg77.ultrahardcore.protocol;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.Sets;

public class ShadowBans extends PacketAdapter implements Listener {
    protected static final String BANNED_IP_ADDRESSES_KEY = "Shadowbanned IP addresses";
    protected static final String BANNED_UUIDS_KEY = "Shadowbanned UUIDs";

    protected final FileConfiguration config;
    protected final Runnable configSaver;

    protected final Object dataLock = new Object();
    protected final Set<String> shadowBannedIpAddresses = Sets.newHashSet();
    protected final Set<String> shadowBannedUuids = Sets.newHashSet();

    public ShadowBans(Plugin plugin, FileConfiguration config, Runnable configSaver) {
        super(plugin, PacketType.Status.Server.OUT_SERVER_INFO);

        this.config = config;
        this.configSaver = configSaver;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Player player = event.getPlayer();
        InetSocketAddress inetSocketAddress = player.getAddress();
        InetAddress inetAddress = inetSocketAddress.getAddress();
        String hostAddress = inetAddress.getHostAddress();

        synchronized (dataLock) {
            if (!shadowBannedIpAddresses.contains(hostAddress)) {
                return;
            }
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void on(AsyncPlayerPreLoginEvent event) {
        String uuid = event.getUniqueId().toString();
        InetAddress address = event.getAddress();
        String hostAddress = address.getHostAddress();

        boolean saveData = false;
        synchronized (dataLock) {
            boolean isShadowedBannedIp = shadowBannedIpAddresses.contains(hostAddress);
            boolean isShadowBannedUuid = shadowBannedUuids.contains(uuid);
            boolean isShadowBanned = isShadowedBannedIp || isShadowBannedUuid;

            if (!isShadowBanned) {
                return;
            }

            if (!isShadowedBannedIp) {
                shadowBannedIpAddresses.add(hostAddress);
                saveData = true;
            }

            if (!isShadowBannedUuid) {
                shadowBannedUuids.add(uuid);
                saveData = true;
            }
        }

        if (saveData) {
            saveData();
        }

        guaranteedSleep(Duration.ofMinutes(1));
    }

    public boolean isShadowBanned(UUID uuid) {
        synchronized (dataLock) {
            return shadowBannedUuids.contains(uuid.toString());
        }
    }

    public boolean isShadowBanned(InetAddress inetAddress) {
        synchronized (dataLock) {
            return shadowBannedIpAddresses.contains(inetAddress.getHostAddress());
        }
    }

    public boolean setShadowBanned(UUID uuid, boolean shadowBanned) {
        synchronized (dataLock) {
            String uuidString = uuid.toString();
            if (!shadowBanned ^ shadowBannedUuids.contains(uuidString)) {
                return false;
            }

            if (shadowBanned) {
                shadowBannedUuids.add(uuidString);
            } else {
                shadowBannedUuids.remove(uuidString);
            }

            saveData();
            return true;
        }
    }

    public boolean setShadowBanned(InetAddress address, boolean shadowBanned) {
        synchronized (dataLock) {
            String hostAddress = address.getHostAddress();
            if (!shadowBanned ^ shadowBannedIpAddresses.contains(hostAddress)) {
                return false;
            }

            if (shadowBanned) {
                shadowBannedIpAddresses.add(hostAddress);
            } else {
                shadowBannedIpAddresses.remove(hostAddress);
            }

            saveData();
            return true;
        }
    }

    public void loadData() {
        synchronized (dataLock) {
            shadowBannedIpAddresses.clear();
            shadowBannedIpAddresses.addAll(config.getStringList(BANNED_IP_ADDRESSES_KEY));

            shadowBannedUuids.clear();
            shadowBannedUuids.addAll(config.getStringList(BANNED_UUIDS_KEY));
        }
    }

    public void saveData() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            synchronized (dataLock) {
                config.set(BANNED_IP_ADDRESSES_KEY, new ArrayList<>(shadowBannedIpAddresses));
                config.set(BANNED_UUIDS_KEY, new ArrayList<>(shadowBannedUuids));
                configSaver.run();
            }
        });
    }

    protected void guaranteedSleep(Duration duration) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime until = now.plus(duration);
        boolean interrupted = false;
        while (now.isBefore(until)) {
            long millisUntil = now.until(until, ChronoUnit.MILLIS);
            try {
                Thread.sleep(millisUntil);
            } catch (InterruptedException e) {
                interrupted = true;
            }
            now = LocalDateTime.now();
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }
}