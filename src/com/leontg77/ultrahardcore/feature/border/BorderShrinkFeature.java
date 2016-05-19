package com.leontg77.ultrahardcore.feature.border;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.events.GameEndEvent;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.events.MeetupEvent;
import com.leontg77.ultrahardcore.events.PvPEnableEvent;
import com.leontg77.ultrahardcore.feature.Feature;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Border shrink feature class.
 * 
 * @author LeonTG77
 */
public class BorderShrinkFeature extends Feature implements Listener {
    private static final String PATH = "feature.border.shrinkAt";

    private final Settings settings;
    private final Main plugin;

    private final Timer timer;
    private final Game game;

    private BorderShrink shrink;

    /**
     * Border shrink feature class constructor.
     *
     * @param plugin The main class.
     * @param settings The settings class.
     * @param timer The timer class.
     * @param game The game class.
     */
    public BorderShrinkFeature(Main plugin, Settings settings, Timer timer, Game game) {
        super("Border Shrink", "A shrinking border once the set event happends.");

        try {
            shrink = BorderShrink.valueOf(settings.getConfig().getString(PATH).toUpperCase());
        } catch (Exception e) {
            shrink = BorderShrink.MEETUP;
        }

        this.settings = settings;
        this.plugin = plugin;

        this.timer = timer;
        this.game = game;
    }

    private BukkitRunnable currentTask = null;

    /**
     * Set the time the border will shrink at.
     *
     * @param border The new border shrink time.
     */
    public void setBorderShrink(BorderShrink border) {
        this.shrink = border;

        settings.getConfig().set(PATH, border.name().toLowerCase());
        settings.saveConfig();
    }

    /**
     * Get the time the border will shrink at.
     *
     * @return The shrink time.
     */
    public BorderShrink getBorderShrink() {
        return shrink;
    }

    @EventHandler
    public void on(GameStartEvent event) {
        if (!shrink.equals(BorderShrink.START)) {
            return;
        }

        PlayerUtils.broadcast(Main.BORDER_PREFIX + "Border will now shrink to §6300x300 §7over §a" + timer.getMeetup() + " §7minutes.");

        for (World world : game.getWorlds()) {
            world.getWorldBorder().setSize(300, timer.getMeetupInSeconds());
        }
    }

    @EventHandler
    public void on(PvPEnableEvent event) {
        if (!shrink.equals(BorderShrink.PVP)) {
            return;
        }

        PlayerUtils.broadcast(Main.BORDER_PREFIX + "Border will now shrink to §6300x300 §7over §a" + timer.getMeetup() + " §7minutes.");

        for (World world : game.getWorlds()) {
            world.getWorldBorder().setSize(300, timer.getMeetupInSeconds());
        }
    }

    @EventHandler
    public void on(MeetupEvent event) {
        if (shrink.equals(BorderShrink.NEVER)) {
            return;
        }

        int timeToNextShrink;

        if (shrink.equals(BorderShrink.MEETUP)) {
            timeToNextShrink = 120;
        } else {
            timeToNextShrink = 1320;

            PlayerUtils.broadcast(Main.BORDER_PREFIX + "Border has stopped shrinking.");
            PlayerUtils.broadcast(Main.BORDER_PREFIX + "Next shrink is in §a22§7 minutes.");
        }

        if (currentTask != null) {
            return;
        }

        PlayerUtils.broadcast(Main.BORDER_PREFIX + "The border starts shrinking in §a" + DateUtils.advancedTicksToString(timeToNextShrink) + "§7.");

        currentTask = new BorderRunnable(game, timeToNextShrink);
        currentTask.runTaskTimer(plugin, 20, 20);
    }

    @EventHandler
    public void on(GameEndEvent event) {
        if (currentTask == null) {
            return;
        }

        currentTask.cancel();
        currentTask = null;
    }

    /**
     * Border Shrink enum.
     * <p>
     * Contains all the possible times when the border can shrink.
     *
     * @author LeonTG77
     */
    public enum BorderShrink {
        /**
         * Represents a border that never shrinks.
         */
        NEVER(""),
        /**
         * Represents a border starts shrinking at the start.
         */
        START("from "),
        /**
         * Represents a border starts shrinking when pvp enables.
         */
        PVP("at "),
        /**
         * Represents a border starts shrinking at meetup.
         */
        MEETUP("at ");

        private String preText;

        /**
         * Constructor for BorderShrink.
         *
         * @param preText The text that fits before the shink name.
         */
        private BorderShrink(String preText) {
            this.preText = preText;
        }

        /**
         * Get the border pre text.
         *
         * @return Pre text.
         */
        public String getPreText() {
            return preText;
        }
    }
}