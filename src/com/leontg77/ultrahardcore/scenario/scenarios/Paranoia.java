package com.leontg77.ultrahardcore.scenario.scenarios;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.feature.health.GoldenHeadsFeature;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Paranoia scenario class.
 * 
 * @author LeonTG77
 */
public class Paranoia extends Scenario implements Listener {
    public static final String PREFIX = "§cParanoia §8» §a";

    private final Game game;

    private final FeatureManager feat;
    private final BoardManager board;

    /**
     * Paranoia class constructor.
     *
     * @param game The game class.
     * @param board The board manager class.
     */
    public Paranoia(Game game, BoardManager board, FeatureManager feat) {
        super("Paranoia", "Your coordinates are broadcasted when you mine diamonds/gold, craft or eat an golden apple, you craft an anvil or enchantment table or you die");

        this.game = game;

        this.board = board;
        this.feat = feat;
    }

    @Override
    public void onDisable() {
        board.setup(game);
    }

    @Override
    public void onEnable() {
        board.getBoard().clearSlot(DisplaySlot.PLAYER_LIST);
        board.getBoard().clearSlot(DisplaySlot.BELOW_NAME);
    }

    @EventHandler
    public void on(PlayerItemConsumeEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Player player = event.getPlayer();

        if (!game.getPlayers().contains(player)) {
            return;
        }

        GoldenHeadsFeature ghead = feat.getFeature(GoldenHeadsFeature.class);

        ItemStack item = event.getItem();
        Location loc = player.getLocation();

        if (item.getType() == Material.GOLDEN_APPLE) {
            if (ghead.isGoldenHead(item)) {
                PlayerUtils.broadcast(PREFIX + player.getName() + "§f ate a §5Golden Head §fat " + location(loc));
                return;
            }

            if (item.getDurability() == 1) {
                PlayerUtils.broadcast(PREFIX + player.getName() + "§f ate a §dNotch Apple §fat " + location(loc));
                return;
            }

            PlayerUtils.broadcast(PREFIX + player.getName() + "§f ate a §eGolden Apple §fat " + location(loc));
        }
    }

    @EventHandler
    public void on(CraftItemEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        HumanEntity player = event.getWhoClicked();
        Location loc = player.getLocation();

        if (!game.getPlayers().contains(player)) {
            return;
        }

        GoldenHeadsFeature ghead = feat.getFeature(GoldenHeadsFeature.class);
        ItemStack result = event.getRecipe().getResult();

        if (result.getType() == Material.GOLDEN_APPLE) {
            if (ghead.isGoldenHead(result)) {
                PlayerUtils.broadcast(PREFIX + player.getName() + "§f crafted a §5Golden Head §fat " + location(loc));
                return;
            }

            if (result.getDurability() == 1) {
                PlayerUtils.broadcast(PREFIX + player.getName() + "§f crafted a §dNotch Apple §fat " + location(loc));
                return;
            }

            PlayerUtils.broadcast(PREFIX + player.getName() + "§f crafted a §eGolden Apple §fat " + location(loc));
            return;
        }

        if (result.getType() == Material.ANVIL) {
            PlayerUtils.broadcast(PREFIX + player.getName() + "§f crafted an §dAnvil §fat " + location(loc));
            return;
        }

        if (result.getType() == Material.ENCHANTMENT_TABLE) {
            PlayerUtils.broadcast(PREFIX + player.getName() + "§f crafted an §5Enchantment Table §fat " + location(loc) + ".");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerDeathEvent event) {
        if (!State.isState(State.INGAME)) {
            return;
        }

        Player player = event.getEntity();
        Location loc = player.getLocation();

        if (!game.getWorlds().contains(player.getWorld())) {
            return;
        }

        PlayerUtils.broadcast(PREFIX + player.getName() + "§f died at " + location(loc));
    }

    /**
     * Get the given location in string form.
     *
     * @param loc the given location.
     * @return Location in String form.
     */
    public String location(Location loc) {
        return "X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ();
    }
}