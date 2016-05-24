package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.events.PvPEnableEvent;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * Moles scenario class
 * 
 * @author Bergasms, modified by LeonTG77
 */
@SuppressWarnings("deprecation")
public class Moles extends Scenario implements Listener, CommandExecutor {
    private final ScenarioManager scen;
    
    private final TeamManager teams;
    private final SpecManager spec;

    public Moles(ScenarioManager scen, TeamManager teams, SpecManager spec) {
        super("Moles", "There are a mole on each team, moles on each team work together to take out the normal teams.");

        plugin.getCommand("molehelp").setExecutor(this);
        plugin.getCommand("mcc").setExecutor(this);
        plugin.getCommand("mcl").setExecutor(this);
        plugin.getCommand("mcp").setExecutor(this);

        this.scen = scen;

        this.teams = teams;
        this.spec = spec;
    }

    private final Random rand = new Random();

    private final List<String> hasUsedKit = new ArrayList<String>();
    private final List<String> moles = new ArrayList<String>();

    @Override
    public void onDisable() {
        moles.clear();
    }

    /**
     * Get a list of the moles.
     *
     * @return The moles.
     */
    public List<String> getMoles() {
        return moles;
    }

    @EventHandler
    public void on(PvPEnableEvent event) {
        new BukkitRunnable() {
            int i = 9;

            public void run() {
                if (i != 0) {
                    PlayerUtils.broadcast(Main.PREFIX + "Moles are being set in §a" + i + "§7 " + (i == 1 ? "second." : "seconds."));
                    PlayerUtils.broadcast(Main.PREFIX + "§4§lCLEAR YOUR TOP ROWS!");

                    for (Player online : Bukkit.getOnlinePlayers()) {
                        online.playSound(online.getLocation(), Sound.FIRE_IGNITE, 1, 0);
                    }

                    i--;
                    return;
                }

                List<String> players = new ArrayList<String>();
                cancel();

                for (Player online : Bukkit.getOnlinePlayers()) {
                    online.playSound(online.getLocation(), Sound.WOLF_HOWL, 1, 1);
                }

                for (Team team : teams.getTeamsWithPlayers()) {
                    if (scen.getScenario("PotentialMoles").isEnabled()) {
                        if (rand.nextInt(100) < 50) {
                            continue;
                        }
                    }

                    for (String entry : team.getEntries()) {
                        if (Bukkit.getPlayer(entry) != null) {
                            players.add(entry);
                        }
                    }

                    if (players.size() > 0) {
                        moles.add(players.get(rand.nextInt(players.size())));
                    }

                    players.clear();
                }

                PlayerUtils.broadcast(Main.PREFIX + "Moles has been set.");

                for (String mole : moles) {
                    Player theMole = Bukkit.getServer().getPlayer(mole);
                    theMole.sendMessage(Main.PREFIX + "You are the mole! Use §a/molehelp §7for help");

                    ItemStack wool1 = new ItemStack (Material.WOOL, 1, (short) 8);
                    ItemMeta wool1meta = wool1.getItemMeta();
                    wool1meta.setDisplayName("§aThe Mobber");
                    wool1meta.setLore(Arrays.asList("§7MONSTER_EGG x 1", "§7MONSTER_EGG x 2", "§7MONSTER_EGG x 1", "§7COBBLESTONE x 1", "§7TNT x 5", "§7ENDER_PEARL x 2"));
                    wool1.setItemMeta(wool1meta);

                    ItemStack wool2 = new ItemStack (Material.WOOL, 1, (short) 10);
                    ItemMeta wool2meta = wool2.getItemMeta();
                    wool2meta.setDisplayName("§aThe Potter");
                    wool2meta.setLore(Arrays.asList("§7POTION x 1", "§7POTION x 1", "§7POTION x 1", "§7POTION x 1", "§7ENDER_PEARL x 1", "§7COBBLESTONE x 1"));
                    wool2.setItemMeta(wool2meta);

                    ItemStack wool3 = new ItemStack (Material.WOOL, 1, (short) 14);
                    ItemMeta wool3meta = wool3.getItemMeta();
                    wool3meta.setDisplayName("§aThe Pyro");
                    wool3meta.setLore(Arrays.asList("§7LAVA_BUCKET x 1", "§7MONSTER_EGG x 5", "§7FLINT_AND_STEEL x 1", "§7POTION x 1", "§7TNT x 5", "§7COBBLESTONE x 1"));
                    wool3.setItemMeta(wool3meta);

                    ItemStack wool4 = new ItemStack (Material.WOOL, 1, (short) 12);
                    ItemMeta wool4meta = wool4.getItemMeta();
                    wool4meta.setDisplayName("§aThe Trapper");
                    wool4meta.setLore(Arrays.asList("§7TNT x 3", "§7LAVA_BUCKET x 1", "§7POTION x 1", "§7COBBLESTONE x 1", "§7COBBLESTONE x 1", "§7COBBLESTONE x 2"));
                    wool4.setItemMeta(wool4meta);

                    ItemStack wool5 = new ItemStack (Material.WOOL, 1, (short) 1);
                    ItemMeta wool5meta = wool5.getItemMeta();
                    wool5meta.setDisplayName("§aThe Troll");
                    wool5meta.setLore(Arrays.asList("§7FIREWORK x 64", "§7ENCHANTED_BOOK x 10", "§7EXPLOSIVE_MINECART x 8", "§7COBBLESTONE x 10", "§7WEB x 4", "§7ENDER_PORTAL x 1"));
                    wool5.setItemMeta(wool5meta);

                    ItemStack wool6 = new ItemStack (Material.WOOL, 1, (short) 13);
                    ItemMeta wool6meta = wool6.getItemMeta();
                    wool6meta.setDisplayName("§aThe Fighter");
                    wool6meta.setLore(Arrays.asList("§7GOLDEN_APPLE x 1", "§7DIAMOND_SWORD x 1", "§7MONSTER_EGG x 1", "§7BOW x 1", "§7ARROW x 64", "§7POTION x 1"));
                    wool6.setItemMeta(wool6meta);

                    PlayerInventory inv = theMole.getInventory();

                    inv.setItem(9, wool1);
                    inv.setItem(10, wool2);
                    inv.setItem(11, wool3);
                    inv.setItem(12, wool4);
                    inv.setItem(13, wool5);
                    inv.setItem(14, wool6);
                }
            }
        }.runTaskTimer(plugin, 20, 20);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use mole commands.");
            return true;
        }

        Player player = (Player) sender;

        if (!isEnabled()) {
            player.sendMessage(Main.PREFIX + "\"Moles\" is not enabled.");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("molehelp")) {
            if (!moles.contains(player.getName())) {
                player.sendMessage(Main.PREFIX.replaceAll("UHC", "Moles") + "You are not a mole.");
                return true;
            }

            sender.sendMessage("§9/mcc: §eChat to other moles ");
            sender.sendMessage("§9/mcl: §eSend your location to other moles");
            sender.sendMessage("§9/mcp: §eList the other moles ");
        }

        if (cmd.getName().equalsIgnoreCase("mcl")) {
            if (!moles.contains(player.getName())) {
                player.sendMessage(Main.PREFIX.replaceAll("UHC", "Moles") + "You are not a mole.");
                return true;
            }

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!moles.contains(online.getName())) {
                    continue;
                }

                online.sendMessage("§6[" + sender.getName() + "]§cLOC:§a "+ ((int) player.getLocation().getX()) + "," + ((int) player.getLocation().getY()) + "," + ((int) player.getLocation().getZ()));
            }
        }

        if (cmd.getName().equalsIgnoreCase("mcc")) {
            if (!moles.contains(player.getName())) {
                player.sendMessage(Main.PREFIX.replaceAll("UHC", "Moles") + "You are not a mole.");
                return true;
            }

            if (args.length == 0) {
                player.sendMessage(Main.PREFIX + "Usage: /mcc <message>");
                return true;
            }

            StringBuilder message = new StringBuilder();

            for (int i = 0; i < args.length; i++) {
                message.append(args[i]).append(" ");
            }

            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!spec.isSpectating(online) && !moles.contains(online.getName())) {
                    continue;
                }

                online.sendMessage("§6[" + sender.getName() + "]§c" + "MOLE PM:§a " + message.toString().trim());
            }
        }

        if (cmd.getName().equalsIgnoreCase("mcp")) {
            if (!spec.isSpectating(player) && !moles.contains(player.getName())) {
                player.sendMessage(Main.PREFIX.replaceFirst("UHC", "Moles") + "You are not a mole.");
                return true;
            }

            player.sendMessage("§cList of moles:");

            for (String mole : moles) {
                player.sendMessage("§9MOLE: §e" + mole);
            }

        }
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        if (item == null) {
            return;
        }

        if (item.getType() != Material.WOOL) {
            return;
        }

        if (!moles.contains(player.getName())) {
            return;
        }

        if (!item.hasItemMeta() && !item.getItemMeta().hasDisplayName()) {
            return;
        }

        if (hasUsedKit.contains(player.getName())) {
            return;
        }

        if (item.getItemMeta().getDisplayName().equals("§aThe Mobber")) {
            ItemStack wool1 = new ItemStack (Material.MONSTER_EGG, 1, (short) 50);

            ItemStack wool2 = new ItemStack (Material.MONSTER_EGG, 2, (short) 51);

            ItemStack wool3 = new ItemStack (Material.MONSTER_EGG, 1, (short) 57);

            ItemStack wool4 = new ItemStack (Material.COBBLESTONE, 1);
            ItemMeta wool4meta = wool4.getItemMeta();
            wool4meta.setDisplayName("§bTrap");
            wool4meta.setLore(Arrays.asList("§5§oEscape Hatch"));
            wool4.setItemMeta(wool4meta);

            ItemStack wool5 = new ItemStack (Material.TNT, 5);

            ItemStack wool6 = new ItemStack (Material.ENDER_PEARL, 2);

            player.getInventory().setItem(9, wool1);
            player.getInventory().setItem(10, wool2);
            player.getInventory().setItem(11, wool3);
            player.getInventory().setItem(12, wool4);
            player.getInventory().setItem(13, wool5);
            player.getInventory().setItem(14, wool6);
            event.setCancelled(true);
            hasUsedKit.add(player.getName());
            return;
        }

        if (item.getItemMeta().getDisplayName().equals("§aThe Potter")) {
            ItemStack wool1 = new ItemStack (Material.POTION, 1, (short) 16388);

            ItemStack wool2 = new ItemStack (Material.POTION, 1, (short) 16392);

            ItemStack wool3 = new ItemStack (Material.POTION, 1, (short) 16394);

            ItemStack wool4 = new ItemStack (Material.POTION, 1, (short) 2);

            ItemStack wool5 = new ItemStack (Material.ENDER_PEARL, 1);

            ItemStack wool6 = new ItemStack (Material.COBBLESTONE, 1);
            ItemMeta wool6meta = wool6.getItemMeta();
            wool6meta.setDisplayName("§bTrap");
            wool6meta.setLore(Arrays.asList("§5§oStaircase"));
            wool6.setItemMeta(wool6meta);

            player.getInventory().setItem(9, wool1);
            player.getInventory().setItem(10, wool2);
            player.getInventory().setItem(11, wool3);
            player.getInventory().setItem(12, wool4);
            player.getInventory().setItem(13, wool5);
            player.getInventory().setItem(14, wool6);
            event.setCancelled(true);
            hasUsedKit.add(player.getName());
            return;
        }

        if (item.getItemMeta().getDisplayName().equals("§aThe Pyro")) {
            ItemStack wool1 = new ItemStack (Material.LAVA_BUCKET, 1);

            ItemStack wool2 = new ItemStack (Material.MONSTER_EGG, 5, (short) 61);

            ItemStack wool3 = new ItemStack (Material.FLINT_AND_STEEL, 1);

            ItemStack wool4 = new ItemStack (Material.POTION, 1, (short) 3);

            ItemStack wool5 = new ItemStack (Material.TNT, 5);

            ItemStack wool6 = new ItemStack (Material.COBBLESTONE, 1);
            ItemMeta wool6meta = wool6.getItemMeta();
            wool6meta.setDisplayName("§bTrap");
            wool6meta.setLore(Arrays.asList("§5§oHole"));
            wool6.setItemMeta(wool6meta);

            player.getInventory().setItem(9, wool1);
            player.getInventory().setItem(10, wool2);
            player.getInventory().setItem(11, wool3);
            player.getInventory().setItem(12, wool4);
            player.getInventory().setItem(13, wool5);
            player.getInventory().setItem(14, wool6);
            event.setCancelled(true);
            hasUsedKit.add(player.getName());
            return;
        }

        if (item.getItemMeta().getDisplayName().equals("§aThe Trapper")) {
            ItemStack wool1 = new ItemStack (Material.TNT, 3);

            ItemStack wool2 = new ItemStack (Material.LAVA_BUCKET, 1);

            ItemStack wool3 = new ItemStack (Material.POTION, 1, (short) 16398);

            ItemStack wool4 = new ItemStack (Material.COBBLESTONE, 1);
            ItemMeta wool4meta = wool4.getItemMeta();
            wool4meta.setDisplayName("§bTrap");
            wool4meta.setLore(Arrays.asList("§5§oDrop Trap"));
            wool4.setItemMeta(wool4meta);

            ItemStack wool5 = new ItemStack (Material.COBBLESTONE, 1);
            ItemMeta wool5meta = wool5.getItemMeta();
            wool5meta.setDisplayName("§bTrap");
            wool5meta.setLore(Arrays.asList("§5§oLava Trap"));
            wool5.setItemMeta(wool5meta);

            ItemStack wool6 = new ItemStack (Material.COBBLESTONE, 2);
            ItemMeta wool6meta = wool6.getItemMeta();
            wool6meta.setDisplayName("§bTrap");
            wool6meta.setLore(Arrays.asList("§5§oTNT Trap"));
            wool6.setItemMeta(wool6meta);

            player.getInventory().setItem(9, wool1);
            player.getInventory().setItem(10, wool2);
            player.getInventory().setItem(11, wool3);
            player.getInventory().setItem(12, wool4);
            player.getInventory().setItem(13, wool5);
            player.getInventory().setItem(14, wool6);
            event.setCancelled(true);
            hasUsedKit.add(player.getName());
            return;
        }

        if (item.getItemMeta().getDisplayName().equals("§aThe Troll")) {
            ItemStack wool1 = new ItemStack (Material.FIREWORK, 64);

            ItemStack wool2 = new ItemStack (Material.ENCHANTED_BOOK, 10);

            ItemStack wool3 = new ItemStack (Material.EXPLOSIVE_MINECART, 8);

            ItemStack wool4 = new ItemStack (Material.COBBLESTONE, 10);
            ItemMeta wool4meta = wool4.getItemMeta();
            wool4meta.setDisplayName("§bTrap");
            wool4meta.setLore(Arrays.asList("§5§oHole"));
            wool4.setItemMeta(wool4meta);

            ItemStack wool5 = new ItemStack (Material.WEB, 4);

            ItemStack wool6 = new ItemStack (Material.ENDER_PORTAL, 1);

            player.getInventory().setItem(9, wool1);
            player.getInventory().setItem(10, wool2);
            player.getInventory().setItem(11, wool3);
            player.getInventory().setItem(12, wool4);
            player.getInventory().setItem(13, wool5);
            player.getInventory().setItem(14, wool6);
            event.setCancelled(true);
            hasUsedKit.add(player.getName());
            return;
        }

        if (item.getItemMeta().getDisplayName().equals("§aThe Fighter")) {
            ItemStack wool1 = new ItemStack (Material.GOLDEN_APPLE);

            ItemStack wool2 = new ItemStack (Material.DIAMOND_SWORD);

            ItemStack wool3 = new ItemStack (Material.BOW);

            ItemStack wool4 = new ItemStack (Material.ARROW, 64);

            ItemStack wool5 = new ItemStack (Material.COBBLESTONE, 1);
            ItemMeta wool4meta = wool5.getItemMeta();
            wool4meta.setDisplayName("§bTrap");
            wool4meta.setLore(Arrays.asList("§5§oStaircase"));
            wool5.setItemMeta(wool4meta);

            ItemStack wool6 = new ItemStack (Material.POTION, 1, (short) 16396);

            player.getInventory().setItem(9, wool1);
            player.getInventory().setItem(10, wool2);
            player.getInventory().setItem(11, wool3);
            player.getInventory().setItem(12, wool4);
            player.getInventory().setItem(13, wool5);
            player.getInventory().setItem(14, wool6);
            event.setCancelled(true);
            hasUsedKit.add(player.getName());
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        ItemStack item = event.getItemInHand();
        Block block = event.getBlockPlaced();

        if (!item.hasItemMeta() && !item.getItemMeta().hasLore()) {
            return;
        }

        Location loc = event.getBlock().getLocation();
        block.setType(Material.AIR);

        if (item.getItemMeta().getLore().contains("§5§oDrop Trap")) {
            createDropTrap(loc);
            return;
        }

        if (item.getItemMeta().getLore().contains("§5§oLava Trap")) {
            createLavaTrap(loc);
            return;
        }

        if (item.getItemMeta().getLore().contains("§5§oTNT Trap")) {
            createTntTrap(loc);
            return;
        }

        if (item.getItemMeta().getLore().contains("§5§oEscape Hatch")) {
            createEscapeHatch(loc);
            return;
        }

        if (item.getItemMeta().getLore().contains("§5§oHole")) {
            createHole(loc);
            return;
        }

        if (item.getItemMeta().getLore().contains("§5§oStaircase")) {
            createStaircase(loc);
        }
    }

    /**
     * Create a hole at the given location.
     *
     * @param loc The location used.
     */
    private void createHole(Location loc) {
        World world = loc.getWorld();

        for (int x = loc.getBlockX() - 4; x < loc.getBlockX() + 4; x++) {
            for (int z = loc.getBlockZ() - 4; z < loc.getBlockZ() + 4; z++) {
                for (int y = loc.getBlockY() + 2; y > loc.getBlockY() - 3; y--) {
                    double xdist = loc.getX() - x;
                    double zdist = loc.getZ() - z;

                    if (xdist * xdist + zdist * zdist < 16.0D) {
                        world.getBlockAt(x, y - 1, z).setType(Material.AIR);
                    }
                }
            }
        }

        world.getBlockAt(loc).setType(Material.STONE);
    }

    /**
     * Create a staircase at the given location.
     *
     * @param loc The location used.
     */
    private void createStaircase(Location loc) {
        World world = loc.getWorld();

        float rot = loc.getYaw() % 360.0F;
        int xoff = 0;
        int zoff = 0;

        rot = Math.abs(rot);

        if ((0.0F <= rot) && (rot < 45.0F)) {
            zoff = 1;
            xoff = 0;
        } else if ((45.0F <= rot) && (rot < 135.0F)) {
            xoff = -1;
            zoff = 0;
        } else if ((135.0F <= rot) && (rot < 225.0F)) {
            zoff = -1;
            xoff = 0;
        } else if ((225.0F <= rot) && (rot < 315.0F)) {
            xoff = 1;
            zoff = 0;
        } else {
            zoff = 1;
            xoff = 0;
        }

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 4; j++) {
                world.getBlockAt(loc.getBlockX(), loc.getBlockY() + j, loc.getBlockZ()).setType(Material.AIR);
            }

            loc.add(xoff, 1.0D, zoff);
        }
    }

    /**
     * Create the escape hatch at the given location.
     *
     * @param loc The location used.
     */
    private void createEscapeHatch(Location loc) {
        World world = loc.getWorld();

        for (int i = loc.getBlockY() - 3; i < loc.getBlockY() + 15; i++) {
            world.getBlockAt(new Location(world, loc.getX() + 2.0D, i, loc.getZ())).setType(Material.OBSIDIAN);
            world.getBlockAt(new Location(world, loc.getX(), i, loc.getZ())).setType(Material.AIR);
            world.getBlockAt(new Location(world, loc.getX() - 1.0D, i, loc.getZ())).setType(Material.OBSIDIAN);
            world.getBlockAt(new Location(world, loc.getX(), i, loc.getZ() + 1.0D)).setType(Material.OBSIDIAN);
            world.getBlockAt(new Location(world, loc.getX(), i, loc.getZ() - 1.0D)).setType(Material.OBSIDIAN);
            world.getBlockAt(new Location(world, loc.getX() + 1.0D, i, loc.getZ() + 1.0D)).setType(Material.OBSIDIAN);
            world.getBlockAt(new Location(world, loc.getX() + 1.0D, i, loc.getZ() - 1.0D)).setType(Material.OBSIDIAN);
        }
    }

    /**
     * Create the top of the drop/lava trap at the given location.
     *
     * @param loc The location of the trap.
     */
    private void createTrapTop(Location loc) {
        float rot = (loc.getYaw() - 90.0F) % 360.0F;
        int xoff = 0;
        int zoff = 0;

        World world = loc.getWorld();
        rot = Math.abs(rot);

        byte face;
        if ((0.0F <= rot) && (rot < 45.0F)) {
            zoff = -1;
            xoff = 0;
            face = 3;
        } else if ((45.0F <= rot) && (rot < 135.0F)) {
            xoff = 1;
            zoff = 0;
            face = 4;
        } else if ((135.0F <= rot) && (rot < 225.0F)) {
            zoff = 1;
            xoff = 0;
            face = 2;
        } else if ((225.0F <= rot) && (rot < 315.0F)) {
            xoff = -1;
            zoff = 0;
            face = 5;
        } else {
            zoff = -1;
            xoff = 0;
            face = 3;
        }

        world.getBlockAt(new Location(world, loc.getX() + xoff, loc.getY() - 1.0D, loc.getZ() + zoff)).setType(Material.PISTON_BASE);
        world.getBlockAt(new Location(world, loc.getX() + xoff, loc.getY() - 1.0D, loc.getZ() + zoff)).setData(face);
        world.getBlockAt(loc).setType(Material.STONE_PLATE);
        world.getBlockAt(new Location(world, loc.getX() - xoff, loc.getY() - 1.0D, loc.getZ() - zoff)).setType(Material.AIR);
    }

    /**
     * Create the drop trap at the given location.
     *
     * @param loc The location used.
     */
    private void createDropTrap(Location loc) {
        World world = loc.getWorld();
        world.getBlockAt(loc).setType(Material.STONE_PLATE);

        for (int i = loc.getBlockY(); i > (loc.getBlockY() - 25 > 3 ? loc.getBlockY() - 25 : 3); i--) {
            if (i != loc.getBlockY() - 1) {
                world.getBlockAt(loc.getBlockX(), i, loc.getBlockZ()).setType(Material.AIR);
            }
        }

        createTrapTop(loc);
    }

    /**
     * Create the lava trap at the given location.
     *
     * @param loc The location used.
     */
    private void createLavaTrap(Location loc) {
        World world = loc.getWorld();
        world.getBlockAt(loc).setType(Material.STONE_PLATE);

        for (int i = loc.getBlockY(); i > (loc.getBlockY() - 3 > 3 ? loc.getBlockY() - 3 : 3); i--) {
            if (i != loc.getBlockY() - 1) {
                world.getBlockAt(loc.getBlockX(), i, loc.getBlockZ()).setType(Material.LAVA);
            }
        }

        createTrapTop(loc);
    }

    /**
     * Create a tnt trap at the given location.
     *
     * @param loc The location used.
     */
    private void createTntTrap(Location loc) {
        World world = loc.getWorld();

        world.getBlockAt(loc).setType(Material.STONE_PLATE);
        world.getBlockAt(new Location(world, loc.getX(), loc.getY() - 2.0D, loc.getZ())).setType(Material.TNT);
    }
}