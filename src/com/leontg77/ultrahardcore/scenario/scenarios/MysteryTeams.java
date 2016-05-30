package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.material.Wool;

import com.google.common.collect.ImmutableSet;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.PvPEnableEvent;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NameUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * MysteryTeams scenario class
 * 
 * @author EXSolo, modified by LeonTG77
 */
public class MysteryTeams extends Scenario implements Listener, CommandExecutor, TabCompleter {
    private static final String PREFIX = "§6[§cMysteryTeams§6] §f";

    private final SpecManager spec;

    private final Map<MysteryTeam, List<UUID>> originalTeams;
    private final List<MysteryTeam> currentTeams;

    public MysteryTeams(SpecManager spec) {
        super("MysteryTeams", "Teams are unknown until meeting and comparing banners.");

        this.spec = spec;

        originalTeams = new HashMap<MysteryTeam, List<UUID>>();
        currentTeams = new ArrayList<MysteryTeam>();

        plugin.getCommand("mysteryteams").setExecutor(this);
    }

    private Material mode = Material.BANNER;

    @Override
    public void onDisable() {
        originalTeams.clear();
        currentTeams.clear();
    }

    @Override
    public void onEnable() {
        originalTeams.clear();
        currentTeams.clear();
    }

    @EventHandler
    public void on(PvPEnableEvent event) {
        for (Player online : game.getPlayers()) {
            MysteryTeam team = getTeam(online);

            if (team == null) {
                continue;
            }

            ItemStack item = getItem(team);
            PlayerUtils.giveItem(online, item);
        }

        PlayerUtils.broadcast(PREFIX + NameUtils.capitalizeString(mode.name(), true) + "s have been given to all players.");
    }

    @EventHandler
    public void on(PrepareItemCraftEvent event) {
        CraftingInventory inv = event.getInventory();
        ItemStack result = inv.getResult();

        if (result == null) {
            return;
        }

        if (result.getType() == mode) {
            inv.setResult(new ItemStack(Material.AIR));
        }
    }

    @EventHandler
    public void onPlayerShear(PlayerShearEntityEvent event) {
        if (mode != Material.WOOL) {
            return;
        }

        Entity entity = event.getEntity();

        if (entity instanceof Sheep) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (mode != Material.WOOL) {
            return;
        }

        Entity entity = event.getEntity();

        if (entity instanceof Sheep) {
            for (ItemStack drop : event.getDrops()) {
                if (drop.getType() == Material.WOOL) {
                    drop.setType(Material.AIR);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerDeathEvent event) {
        Player player = event.getEntity();
        MysteryTeam team = getTeam(player);
        
        for (ItemStack drop : event.getDrops()) {
            if (drop.getType() == mode) {
                drop.setType(Material.AIR);
            }
        }
        
        if (team == null) {
            return;
        }

        team.removePlayer(player);

        PlayerUtils.broadcast(PREFIX + team.getChatColor() + "Player from team " + team.getName() + " died.");

        if (team.getSize() > 0) {
            PlayerUtils.broadcast(PREFIX + team.getChatColor() + "Team " + team.getName() + " has §f" + team.getSize() + team.getChatColor() + (team.getSize() > 1 ? " players" : " player") + " left");
            return;
        }

        currentTeams.remove(team);

        if (currentTeams.size() > 1) {
            PlayerUtils.broadcast(PREFIX + team.getChatColor() + "Team " + team.getName() + " eliminated. There are §f" + currentTeams.size() + team.getChatColor() + " teams left.");
            return;
        }

        MysteryTeam killerTeam = null;

        for (MysteryTeam mTeam : currentTeams) {
            killerTeam = mTeam;
            break;
        }

        if (killerTeam == null) {
            return;
        }

        PlayerUtils.broadcast(PREFIX + team.getChatColor() + "Team " + team.getName() + " eliminated. " + killerTeam.getChatColor() + "The " + killerTeam.getName() + " team won!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!isEnabled()) {
            sender.sendMessage(PREFIX + "MysteryTeams is not enabled.");
            return true;
        }

        if (args.length == 0) {
            return helpMenu(sender);
        }

        if (args[0].equalsIgnoreCase("teamsize")) {
            sender.sendMessage(PREFIX + "All teamsizes:");

            for (MysteryTeam team : currentTeams) {
                sender.sendMessage(PREFIX + team.getChatColor() + "Team " + team.getName().toLowerCase() + "'s teamsize: §f" + team.getSize());
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (!sender.hasPermission("uhc.staff")) {
                return helpMenu(sender);
            }

            if (!spec.isSpectating(sender.getName())) {
                sender.sendMessage(ChatColor.RED + "You can only do this while spectating.");
                return true;
            }

            if (originalTeams.isEmpty()) {
                sender.sendMessage(PREFIX + "There are no mystery teams.");
                return true;
            }

            sender.sendMessage(PREFIX + "All Mystery Teams: §8(§f§oItalic §f= Dead§8)");

            for (Entry<MysteryTeam, List<UUID>> entry : originalTeams.entrySet()) {
                StringBuilder members = new StringBuilder();
                int i = 1;

                MysteryTeam team = entry.getKey();
                List<UUID> uuids = entry.getValue();

                for (UUID member : uuids) {
                    if (members.length() > 0) {
                        if (i == uuids.size()) {
                            members.append(" §8and §f");
                        } else {
                            members.append("§8, §f");
                        }
                    }

                    OfflinePlayer offline = Bukkit.getOfflinePlayer(member);

                    if (currentTeams.contains(team)) {
                        members.append(team.hasPlayer(offline) ? ChatColor.WHITE + offline.getName() : ChatColor.ITALIC + offline.getName());
                    } else {
                        members.append(ChatColor.RED + offline.getName());
                    }

                    i++;
                }

                sender.sendMessage(team.getChatColor() + team.getName() + ": §f" + members.toString().trim());
            }
            return true;
        }

        if (!sender.hasPermission("uhc." + getName().toLowerCase())) {
            return helpMenu(sender);
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length < 3) {
                sender.sendMessage(Main.PREFIX + "Usage: /team add <team> <player>");
                return true;
            }

            MysteryTeam team = null;

            for (MysteryTeam teams : currentTeams) {
                if (teams.getName().replaceAll(" ", "").equalsIgnoreCase(args[1])) {
                    team = teams;
                    break;
                }
            }

            if (team == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a vaild team.");
                return true;
            }

            OfflinePlayer offline = PlayerUtils.getOfflinePlayer(args[2]);

            sender.sendMessage(PREFIX + ChatColor.GREEN + offline.getName() + "§7 was added to team §6" + team.getName() + "§7.");
            team.addPlayer(offline);
            return true;
        }

        if (args[0].equalsIgnoreCase("remove")) {
            if (args.length == 1) {
                sender.sendMessage(Main.PREFIX + "Usage: /team remove <player>");
                return true;
            }

            OfflinePlayer offline = PlayerUtils.getOfflinePlayer(args[1]);
            MysteryTeam team = getTeam(offline);

            if (team == null) {
                sender.sendMessage(ChatColor.RED + "'" + offline.getName() + "' is not on a team.");
                return true;
            }

            sender.sendMessage(PREFIX + ChatColor.GREEN + offline.getName() + " §7was removed from his team.");
            team.removePlayer(offline);
            return true;
        }

        if (args[0].equalsIgnoreCase("delete")) {
            if (args.length == 1) {
                sender.sendMessage(Main.PREFIX + "Usage: /team delete <team>");
                return true;
            }

            MysteryTeam team = null;

            for (MysteryTeam teams : currentTeams) {
                if (teams.getName().replaceAll(" ", "").equalsIgnoreCase(args[1])) {
                    team = teams;
                    break;
                }
            }

            if (team == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a vaild team.");
                return true;
            }

            for (OfflinePlayer teammate : team.getPlayers()) {
                team.removePlayer(teammate);
            }

            sender.sendMessage(PREFIX + "Team §a" + team.getName() + " §7has been deleted.");
            return true;
        }

        if (args[0].equalsIgnoreCase("mode")) {
            if (args.length == 1) {
                sender.sendMessage(Main.PREFIX + "Usage: /team mode <new mode>");
                return true;
            }

            Material newMode = null;

            for (Material type : Material.values()) {
                if (type.name().startsWith(args[1].toUpperCase())) {
                    newMode = type;
                    break;
                }
            }

            if (newMode == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a vaild mode.");
                return true;
            }

            switch (newMode) {
            case FIREWORK:
            case BANNER:
            case WOOL:
                break;
            default:
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a vaild mode.");
                return true;
            }

            mode = newMode;
            PlayerUtils.broadcast(PREFIX + "Mystery Teams mode has been changed to " + mode.name().toLowerCase() + ".");
            return true;
        }

        if (args[0].equalsIgnoreCase("clear")) {
            for (MysteryTeam team : currentTeams) {
                for (OfflinePlayer teammate : team.getPlayers()) {
                    team.removePlayer(teammate);
                }
            }

            originalTeams.clear();
            currentTeams.clear();

            sender.sendMessage(PREFIX + "All teams have been cleared.");
            return true;
        }

        if (args[0].equalsIgnoreCase("randomize")) {
            if (args.length == 1) {
                sender.sendMessage(PREFIX + "Usage: /mt randomize <teamsize> <amount of teams>");
                return true;
            }

            int teamSize;

            try {
                teamSize = parseInt(args[1]);
            } catch (Exception ex) {
                sender.sendMessage(ChatColor.RED + ex.getMessage());
                return true;
            }

            int amount;

            try {
                amount = parseInt(args[2]);
            } catch (Exception ex) {
                sender.sendMessage(ChatColor.RED + ex.getMessage());
                return true;
            }

            for (int i = 0; i < amount; i++) {
                List<Player> players = new ArrayList<Player>();

                for (Player online : Bukkit.getOnlinePlayers()) {
                    if (getTeam(online) == null && game.getPlayers().contains(online)) {
                        players.add(online);
                    }
                }

                Collections.shuffle(players);
                MysteryTeam team = findAvailableTeam();

                if (team == null) {
                    sender.sendMessage(ChatColor.RED + "There are no more available teams.");
                    break;
                }

                if (!originalTeams.containsKey(team)) {
                    originalTeams.put(team, new ArrayList<UUID>());
                }

                if (!currentTeams.contains(team)) {
                    currentTeams.add(team);
                }

                for (int j = 0; j < teamSize; j++) {
                    if (players.isEmpty()) {
                        sender.sendMessage(ChatColor.RED + "No more players to add to the team.");
                        break;
                    }

                    Player player = players.remove(0);

                    originalTeams.get(team).add(player.getUniqueId());
                    team.addPlayer(player);
                }
            }

            PlayerUtils.broadcast(PREFIX + "Randomized " + amount + " teams of " + teamSize + ".");
            return true;
        }

        if (args[0].equalsIgnoreCase("give")) {
            if (args.length == 1) {
                for (Player online : Bukkit.getOnlinePlayers()) {
                    MysteryTeam team = getTeam(online);

                    if (team == null) {
                        continue;
                    }

                    ItemStack item = getItem(team);
                    PlayerUtils.giveItem(online, item);
                }

                PlayerUtils.broadcast(PREFIX + NameUtils.capitalizeString(mode.name(), true) + "s have been given to all players.");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not online.");
                return true;
            }

            MysteryTeam team = getTeam(target);

            if (team == null) {
                sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not on a team.");
                return true;
            }

            ItemStack item = getItem(team);

            sender.sendMessage(PREFIX + NameUtils.capitalizeString(mode.name(), true) + "s have been given to " + target.getName() + ".");
            PlayerUtils.giveItem(target, item);

            return true;
        }

        return helpMenu(sender);
    }

    @Override
    public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Print the help menu to the given sender.
     *
     * @param sender The sender printing too
     * @return Always true.
     */
    public boolean helpMenu(CommandSender sender) {
        sender.sendMessage(PREFIX + "MysteryTeams Help Menu:");
        sender.sendMessage("§8- §f/mt teamsize §8- §7§oDisplay all teams current teamsize.");

        if (sender.hasPermission("uhc.staff")) {
            sender.sendMessage("§8- §f/mt list §8- §7§oDisplay all teams.");
        }

        if (!sender.hasPermission("uhc." + getName().toLowerCase())) {
            return true;
        }

        sender.sendMessage("§8- §f/mt add <team> <player> §8- §7§oAdd the player to the team.");
        sender.sendMessage("§8- §f/mt remove <player> §8- §7§oRemove the player from his team.");
        sender.sendMessage("§8- §f/mt delete <team> §8- §7§oRemove all players from the team.");
        sender.sendMessage("§8- §f/mt mode <new mode> §8- §7§oChange the mystery team item to use.");
        sender.sendMessage("§8- §f/mt clear §8- §7§oClear all teams.");
        sender.sendMessage("§8- §f/mt randomize <teamsize> <amount of teams> §8- §7§oRandomize the teams.");
        sender.sendMessage("§8- §f/mt give [player] §8- §7§oGive the banners to the given player or everyone.");
        return true;
    }

    public ItemStack getItem(MysteryTeam team) {
        switch (mode) {
        case FIREWORK:
            ItemStack firework = new ItemStack(mode, 64);
            FireworkMeta fireworkMeta = (FireworkMeta) firework.getItemMeta();
            fireworkMeta.addEffect(FireworkEffect.builder().withColor(team.getDyeColor().getColor()).build());

            FireworkEffect effects = FireworkEffect.builder().withColor(team.getDyeColor().getFireworkColor()).with(FireworkEffect.Type.BALL_LARGE).build();

            fireworkMeta.addEffect(effects);
            fireworkMeta.setPower(2);

            firework.setItemMeta(fireworkMeta);
            return firework;
        case BANNER:
            ItemStack banner = new ItemStack(mode, 1);
            BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();
            bannerMeta.setBaseColor(team.getDyeColor());
            banner.setItemMeta(bannerMeta);
            return banner;
        case WOOL:
            return new Wool(team.getDyeColor()).toItemStack(1);
        default:
            return null;
        }
    }

    /**
     * Register a new mystery team.
     *
     * @return The mystery team created.
     */
    public MysteryTeam findAvailableTeam() {
        List<MysteryTeam> list = Arrays.asList(MysteryTeam.values());
        Collections.shuffle(list);

        for (MysteryTeam team : list) {
            if (team.getSize() == 0) {
                return team;
            }
        }

        return null;
    }

    /**
     * Get the mystery team of the given player.
     *
     * @param offline The player to check with.
     * @return The team if any, null otherwise.
     */
    private MysteryTeam getTeam(OfflinePlayer offline) {
        for (MysteryTeam team : currentTeams) {
            if (team.hasPlayer(offline)) {
                return team;
            }
        }

        return null;
    }

    /**
     * Get a Set of all the mystery teams.
     *
     * @return A set of mystery teams.
     */
    public Set<MysteryTeam> getTeams() {
        return ImmutableSet.copyOf(currentTeams);
    }

    /**
     * Get a Set of all the original mystery teams.
     *
     * @return A set of original mystery teams.
     */
    public Map<MysteryTeam, List<UUID>> getOriginalTeams() {
        return originalTeams;
    }

    public enum MysteryTeam {
        /**
         * Green team.
         */
        GREEN("Green", ChatColor.DARK_GREEN, DyeColor.GREEN),
        /**
         * Orange team.
         */
        ORANGE("Orange", ChatColor.GOLD, DyeColor.ORANGE),
        /**
         * Red team.
         */
        RED("Red", ChatColor.RED, DyeColor.RED),
        /**
         * Light blue team.
         */
        LIGHT_BLUE("Light blue", ChatColor.AQUA, DyeColor.LIGHT_BLUE),
        /**
         * Yellow team.
         */
        YELLOW("Yellow", ChatColor.YELLOW, DyeColor.YELLOW),
        /**
         * Light Green team.
         */
        LIME("Light Green", ChatColor.GREEN, DyeColor.LIME),
        /**
         * Pink team.
         */
        PINK("Pink", ChatColor.LIGHT_PURPLE, DyeColor.PINK),
        /**
         * Gray team.
         */
        GRAY("Gray", ChatColor.DARK_GRAY, DyeColor.GRAY),
        /**
         * Light Gray team.
         */
        SILVER("Light Gray", ChatColor.GRAY, DyeColor.SILVER),
        /**
         * Purple team.
         */
        PURPLE("Purple", ChatColor.DARK_PURPLE, DyeColor.PURPLE),
        /**
         * Blue team.
         */
        BLUE("Blue", ChatColor.BLUE, DyeColor.BLUE),
        /**
         * Cyan team.
         */
        CYAN("Cyan", ChatColor.DARK_AQUA, DyeColor.CYAN),
        /**
         * White team.
         */
        WHITE("White", ChatColor.WHITE, DyeColor.WHITE),
        /**
         * Black team.
         */
        BLACK("Black", ChatColor.BLACK, DyeColor.BLACK);

        private final String name;

        private final ChatColor chat;
        private final DyeColor dye;

        /**
         * Mystery Team class constructor.
         *
         * @param name The team name.
         * @param chat The team chat color.
         * @param dye The team dye color.
         */
        private MysteryTeam(String name, ChatColor chat, DyeColor dye) {
            this.name = name;

            this.chat = chat;
            this.dye = dye;
        }

        private final Set<UUID> players = new HashSet<UUID>();

        /**
         * Get the name of the team.
         *
         * @return The name.
         */
        public String getName() {
            return name;
        }

        /**
         * Get the chat color for the team.
         *
         * @return The chat color.
         */
        public ChatColor getChatColor() {
            return chat;
        }

        /**
         * Get the dye color for the team.
         *
         * @return The dye color.
         */
        public DyeColor getDyeColor() {
            return dye;
        }

        /**
         * Get a set of all players on this team.
         *
         * @return A set of players.
         */
        public Set<OfflinePlayer> getPlayers() {
            Set<OfflinePlayer> list = new HashSet<OfflinePlayer>();

            for (UUID uuid : players) {
                list.add(Bukkit.getOfflinePlayer(uuid));
            }

            return ImmutableSet.copyOf(list);
        }

        /**
         * Get the size of the team.
         *
         * @return The size.
         */
        public int getSize() {
            return players.size();
        }

        /**
         * Add the given player to the team.
         *
         * @param player The player adding.
         */
        public void addPlayer(OfflinePlayer player) {
            players.add(player.getUniqueId());
        }

        /**
         * Remove the given player to the team.
         *
         * @param player The player removing.
         * @return True if successful, false otherwise.
         */
        public boolean removePlayer(OfflinePlayer player) {
            return players.remove(player.getUniqueId());
        }

        /**
         * Check if the given player is on this team.
         *
         * @param player The player checking.
         * @return True if he is, false otherwise.
         */
        public boolean hasPlayer(OfflinePlayer player) {
            return players.contains(player.getUniqueId());
        }
    }
}