package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Ordering;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

public class BlockBattle extends Scenario {
    public static final String PREFIX = "§6Block Battle §8» §f";

    public BlockBattle() {
        super("BlockBattle", "Every 10 minutes the person with the most kinds of blocks gets rewarded with 4 diamonds. The blocks will get removed from the inventory.");
    }

    protected final Ordering<Player> MOST_BLOCKS = new Ordering<Player>() {
        @Override
        public int compare(Player p1, Player p2) {
            return Integer.compare(getUniqueBlockCount(p1), getUniqueBlockCount(p2));
        }
    };

    protected Optional<BukkitTask> task = Optional.empty();
    protected int seconds;

    @Override
    public void onDisable() {
        task.ifPresent(BukkitTask::cancel);
        task = Optional.empty();
    }

    @Override
    public void onEnable() {
        if (!game.isState(Game.State.INGAME)) {
            return;
        }

        on(new GameStartEvent());
    }

    @EventHandler
    protected void on(GameStartEvent event) {
        seconds = 600;

        task = Optional.of(Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            seconds--;

            switch (seconds) {
                case 300:
                    PlayerUtils.broadcast(PREFIX + "Counting unique blocks in 5 minutes!");
                    break;
                case 60:
                    PlayerUtils.broadcast(PREFIX + "Counting unique blocks in 1 minute!");
                    break;
                case 30:
                case 10:
                case 5:
                case 4:
                case 3:
                case 2:
                    PlayerUtils.broadcast(PREFIX + "Counting unique blocks in " + seconds + " seconds!");
                    break;
                case 1:
                    PlayerUtils.broadcast(PREFIX + "Counting unique blocks in 1 second!");
                    break;
                case 0:
                    List<Player> gamePlayers = game.getPlayers();
                    if (gamePlayers.isEmpty()) {
                        PlayerUtils.broadcast(PREFIX + "There were no game players.");
                        return;
                    }

                    List<Player> sorted = MOST_BLOCKS.sortedCopy(gamePlayers);
                    int max = getUniqueBlockCount(MOST_BLOCKS.max(sorted));
                    List<Player> playersWithMaxBlocks = sorted.stream()
                            .filter(player -> getUniqueBlockCount(player) == max)
                            .collect(Collectors.toList());

                    if (playersWithMaxBlocks.isEmpty()) {
                        // WAT?
                        throw new AssertionError();
                    }

                    if (playersWithMaxBlocks.size() > 1) {
                        PlayerUtils.broadcast(PREFIX + "There were multiple players with the most amount of blocks!");
                        PlayerUtils.broadcast(PREFIX + "Nobody has received any diamonds.");
                    } else {
                        Player winner = playersWithMaxBlocks.get(0);
                        PlayerUtils.giveItem(winner, new ItemStack(Material.DIAMOND, 4));
                        PlayerUtils.broadcast(PREFIX + winner.getName() + " had the most unique blocks in his inventory and won 4 diamonds!");
                    }

                    seconds = 600;
                    break;
            }
        }, 0L, 20L));
    }

    protected int getUniqueBlockCount(Player player) {
        return Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .map(ItemStack::getType)
                .filter(Material::isBlock)
                .collect(Collectors.toSet())
                .size();
    }
}