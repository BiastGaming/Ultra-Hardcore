package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.scenario.Scenario;

/**
 * Popcorn scenario base class.
 * 
 * @author D4mnX
 */
class PopcornBase extends Scenario {
    protected static final List<PushingDirection> DIRECTIONS = ImmutableList.copyOf(PushingDirection.values());

    protected final Main plugin;
    protected final Game game;
    
    protected final double pushingSpeed;
    protected final long intervalInTicks;
    
    protected final Random random = new Random();
    protected Optional<BukkitTask> currentTask = Optional.empty();

    PopcornBase(String name, Main plugin, Game game, double pushingSpeed, long intervalInTicks) {
        super(name, "You get constantly nudged in random directions. Fall damage is off.");
        this.plugin = plugin;
        this.game = game;
        this.pushingSpeed = pushingSpeed;
        this.intervalInTicks = intervalInTicks;
    }

    @Override
    public void onDisable() {
        if (!currentTask.isPresent()) {
            return;
        }

        currentTask.get().cancel();
        currentTask = Optional.empty();
    }

    @Override
    public void onEnable() {
        if (State.isState(State.INGAME)) {
            on(new GameStartEvent());
        }
    }

    @EventHandler
    protected void on(GameStartEvent event) {
        if (currentTask.isPresent()) {
            return;
        }

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin,
                () -> game.getPlayers().stream().forEach(this::pushPlayer),
                intervalInTicks, intervalInTicks
        );
        currentTask = Optional.of(task);
    }

    protected void pushPlayer(Player player) {
        PushingDirection randomDirection = DIRECTIONS.get(random.nextInt(DIRECTIONS.size()));
        randomDirection.apply(player, pushingSpeed);
    }

    public enum PushingDirection {
        NORTH(0,0,-1),
        SOUTH(0,0,1),
        WEST(-1,0,0),
        EAST(1,0,0),
        UPWARDS(0, 1, 0);

        private final double dx;
        private final double dy;
        private final double dz;

        PushingDirection(double dx, double dy, double dz) {
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
        }

        public void apply(Player player, double pushingSpeed) {
            double pushX = this.dx * pushingSpeed;
            double pushY = this.dy * pushingSpeed;
            double pushZ = this.dz * pushingSpeed;

            Vector oldVelocity = player.getVelocity();
            Vector newVelocity = oldVelocity.clone().add(new Vector(pushX, pushY, pushZ));
            player.setVelocity(newVelocity);
        }
    }
}
