package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.protocol.WTFIPTG.DisableScoreboardUpdateAdapter;
import com.leontg77.ultrahardcore.protocol.WTFIPTG.DisableTabCompletionAdapter;
import com.leontg77.ultrahardcore.protocol.WTFIPTG.DisguisePlayersAdapter;
import com.leontg77.ultrahardcore.protocol.WTFIPTG.FixedOnlineCountInPingAdapter;
import com.leontg77.ultrahardcore.protocol.WTFIPTG.HidePlayersInSpecificWorldsAdapter;
import com.leontg77.ultrahardcore.protocol.WTFIPTG.ReplacePlayerNamesInChatAdapter;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * WTFIPTG scenario class.
 * 
 * @author D4mnX & LeonTG77
 */
public class WTFIPTG extends Scenario implements Listener {
    private final List<PacketAdapter> adapters;
    private final Set<String> allowedCommands;

    private final ProtocolManager manager;

    private final BoardManager board;
    private final SpecManager spec;

    private final FakePlayer fake;

    public WTFIPTG(Main plugin, SpecManager spec, BoardManager board) {
        super("WTFIPTG", "Chat is disabled, Join messages are disabled, Player name tags are disabled, All players are disguised so you cannot recognise them by their skin, Death messages are disabled and the kill scoreboard is off.");

        this.board = board;
        this.spec = spec;

        this.fake = new FakePlayer("Anonymous", UUID.fromString("640a5372-780b-4c2a-b7e7-8359d2f9a6a8"));
        this.manager = ProtocolLibrary.getProtocolManager();

        this.adapters = ImmutableList.of(
            new DisableScoreboardUpdateAdapter(plugin, spec),
            new DisableTabCompletionAdapter(plugin, spec),
            new DisguisePlayersAdapter(plugin, fake, spec),
            new FixedOnlineCountInPingAdapter(plugin, 0),
            new HidePlayersInSpecificWorldsAdapter(plugin, spec, Predicates.equalTo(plugin.getSpawn().getWorld())),
            new ReplacePlayerNamesInChatAdapter(plugin, fake, spec)
        );

        this.allowedCommands = ImmutableSet.of(
            "/parkour", "/ac", "/ssc", "/scen", "/uhc", "/matchpost", "/post", "/spec", "/r",
            "/mucoords", "/helpop", "/timeleft", "/hof", "/tps", "/top", "/border", "/a"
        );
    }

    private Team team;

    @Override
    public void onDisable() {
        for (PacketAdapter adapter : adapters) {
            manager.removePacketListener(adapter);
        }
        
        if (team == null) {
            return;
        }
        
        try {
            team.unregister();
        } catch (IllegalStateException ignored) {} // Team was manually removed, ignore
    }
    
    @Override
    public void onEnable() {
        team = board.getBoard().getTeam(fake.getName());
        
        if (team == null) {
            team = board.getBoard().registerNewTeam(fake.getName());
            team.setNameTagVisibility(NameTagVisibility.NEVER);
            team.addEntry(fake.getName());
        }

        for (PacketAdapter adapter : adapters) {
            manager.addPacketListener(adapter);
        }
    }

    @EventHandler
    public void on(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (spec.isSpectating(player)) {
            return;
        }

        player.sendMessage(ChatColor.RED + "You can't chat in this scenario.");
        event.setCancelled(true);
    }

    @EventHandler
    public void on(PlayerCommandPreprocessEvent event) {
        final String message = event.getMessage();
        Player player = event.getPlayer();

        if (spec.isSpectating(player)) {
            return;
        }
        
        if (allowedCommands.contains(message.split(" ")[0])) {
            return;
        }

        player.sendMessage(ChatColor.RED + "You can only send these commands: " + Joiner.on(" | ").join(allowedCommands));
        event.setCancelled(true);
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void on(PlayerJoinEvent event) {
        String message = event.getJoinMessage();
        Player player = event.getPlayer();

        if (handleEvent(player, message)) {
            event.setJoinMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(PlayerQuitEvent event) {
        String message = event.getQuitMessage();
        Player player = event.getPlayer();

        if (handleEvent(player, message)) {
            event.setQuitMessage(null);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(PlayerDeathEvent event) {
        String message = event.getDeathMessage();
        Player player = event.getEntity();

        if (handleEvent(player, message)) {
            event.setDeathMessage(null);
        }
    }
    
    /**
     * Handle the event for the given player and the message.
     * 
     * @param player The player of the event.
     * @param message The message to the event.
     * @return The success of the handling.
     */
    private boolean handleEvent(Player player, String message) {
        if (spec.isSpectating(player)) {
            return false;
        }
        
        if (message == null) {
            return false;
        }

        for (Player exempted : Bukkit.getOnlinePlayers()) {
            if (!spec.isSpectating(exempted)) {
                continue;
            }

            exempted.sendMessage(message);
        }

        return true;
    }
    
    /**
     * Fake player class.
     * 
     * @author D4mnX
     */
    public static class FakePlayer {
        private final String name;
        private final UUID uuid;

        /**
         * Fake player class constructor.
         * 
         * @param name The name of the fake player.
         * @param uuid The UUID of the fake player.
         */
        public FakePlayer(String name, UUID uuid) {
            this.name = name;
            this.uuid = uuid;
        }

        /**
         * Get the name of this fake player.
         * 
         * @return The name.
         */
        public String getName() {
            return name;
        }

        /**
         * Get the UUID of this fake player.
         * 
         * @return The UUID.
         */
        public UUID getUUID() {
            return uuid;
        }
    }
}