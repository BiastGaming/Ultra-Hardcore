package com.leontg77.ultrahardcore.feature.tablist;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.feature.ToggleableFeature;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * Tab health color feature.
 * 
 * @author LeonTG77
 */
public class TabHealthColorFeature extends ToggleableFeature {
    private final SpecManager spec;
    private final Main plugin;

    private BukkitRunnable task;

    public TabHealthColorFeature(Main plugin, SpecManager spec) {
        super("Tab Health Color", "Your tab name is colored after how high you are on health.");

        icon.setType(Material.INK_SACK);
        icon.setDurability((short) 10);

        slot = 6;

        this.plugin = plugin;
        this.spec = spec;
    }

    @Override
    public void onDisable() {
        if (task == null) {
            return;
        }

        task.cancel();
        task = null;

        for (Player online : Bukkit.getOnlinePlayers()) {
            online.setPlayerListName(null);
        }
    }

    @Override
    public void onEnable() {
        task = new BukkitRunnable() {
            public void run() {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    final String percentString = NumberUtils.makePercent(online.getHealth());

                    if (spec.isSpectating(online)) {
                        continue;
                    }

                    final String percentColor = percentString.substring(0, 2);
                    online.setPlayerListName(percentColor + online.getName());
                }
            }
        };

        task.runTaskTimer(plugin, 1, 1);
    }
}