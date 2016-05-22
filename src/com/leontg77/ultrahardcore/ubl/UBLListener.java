package com.leontg77.ultrahardcore.ubl;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * UBL listener class.
 * 
 * @author LeonTG77
 */
public class UBLListener implements Listener {
    private final Game game;
    private final UBL ubl;

    public UBLListener(UBL ubl, Game game) {
        this.game = game;
        this.ubl = ubl;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(AsyncPlayerPreLoginEvent event) {
        if (game.isRecordedRound() || game.isPrivateGame()) {
            return;
        }
        
        UUID uuid = event.getUniqueId();

        if (!ubl.isBanned(uuid)) {
            return;
        }

        BanEntry entry = ubl.getBanEntry(uuid);
        
        if (entry == null) {
            return; // shouldn't happen but incase...
        }

        PlayerUtils.broadcast(Main.PREFIX + "§c" + event.getName() + " §7tried to join while being UBL'ed for: §c" + entry.getData("Reason"), "uhc.staff");
        event.disallow(Result.KICK_BANNED, ubl.getBanMessage(uuid));
    }
}