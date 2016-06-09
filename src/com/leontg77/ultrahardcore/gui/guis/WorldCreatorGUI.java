package com.leontg77.ultrahardcore.gui.guis;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.gui.GUI;
import com.leontg77.ultrahardcore.utils.NameUtils;
import com.leontg77.ultrahardcore.world.WorldManager;
import com.leontg77.ultrahardcore.world.orelimiter.OreLimiter;

/**
 * World Creator inventory GUI class.
 * 
 * @author LeonTG77.
 */
public class WorldCreatorGUI extends GUI implements Listener {
    private final WorldManager manager;

    /**
     * World Creator inventory GUI class constructor.
     *
     * @param manager The world manager.
     */
    public WorldCreatorGUI(WorldManager manager) {
        super("World Creator", "A inventory with options for world generation.");

        this.manager = manager;
    }

    private final Inventory inv = Bukkit.createInventory(null, 45, "§4World Creator Options");

    @Override
    public void onSetup() {
        glassify(inv);
        update();
    }

    private String name;

    private int diameter;
    private long seed;

    private boolean newstone = false;
    private boolean antiStripmine = true;
    private OreLimiter.Type orelimiter = OreLimiter.Type.MINOR;

    private boolean moved = true;

    private boolean netherD = true;
    private boolean endD = false;

    private WorldType type = WorldType.NORMAL;

    /**
     * Get the inventory.
     *
     * @return The inventory.
     */
    public Inventory get() {
        if (inv.getViewers().isEmpty()) {
            newstone = false;
            antiStripmine = true;
            orelimiter = OreLimiter.Type.MINOR;

            moved = false;

            netherD = true;
            endD = false;

            type = WorldType.NORMAL;
            update();
        }

        return inv;
    }

    /**
     * Get the inventory with the given settings.
     *
     * @param name The world name.
     * @param diameter The world .size.
     * @param seed The world seed
     * @return The inventory.
     */
    public Inventory get(String name, int diameter, long seed) {
        this.name = name;
        this.diameter = diameter;
        this.seed = seed;

        update();
        return get();
    }

    private final Random rand = new Random();

    @EventHandler
    public void on(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked(); // safe cast

        ItemStack item = event.getCurrentItem();
        Inventory inv = event.getInventory();

        if (item == null) {
            return;
        }

        if (!this.inv.getTitle().equals(inv.getTitle())) {
            return;
        }

        event.setCancelled(true);

        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return;
        }

        // the substring is just getting the name since theres some things before and after the name
        String itemName = item.getItemMeta().getDisplayName().substring(6, item.getItemMeta().getDisplayName().length() - 4);

        switch (itemName.toLowerCase()) {
        case "1.8 stone":
            newstone = !newstone;
            update();
            break;
        case "stripmining":
            antiStripmine = !antiStripmine;
            update();
            break;
        case "ore limiter":
            switch (orelimiter) {
                case MINOR:
                    orelimiter = OreLimiter.Type.LESS_VEINS;
                    break;
                case LESS_VEINS:
                    orelimiter = OreLimiter.Type.SMALLER_VEINS;
                    break;
                case SMALLER_VEINS:
                    orelimiter = OreLimiter.Type.MINOR;
                    break;
                default:
                    return;
            }

            update();
            break;
        case "world type":
            switch (type) {
            case NORMAL:
                type = WorldType.FLAT;
                break;
            case FLAT:
                type = WorldType.LARGE_BIOMES;
                break;
            case LARGE_BIOMES:
                type = WorldType.AMPLIFIED;
                break;
            case AMPLIFIED:
                type = WorldType.NORMAL;
                break;
            default:
                return;
            }

            update();
        case "nether":
            netherD = !netherD;
            update();
            break;
        case "the end":
            endD = !endD;
            update();
            break;
        case "moved 0,0":
            moved = !moved;
            update();
            break;
        case "confirm":
            player.closeInventory();
            player.sendMessage(Main.PREFIX + "Starting creation of world '§a" + name + "§7'...");

            double x = 0.0;
            double z = 0.0; // I don't want negative coords for the Z as that messes up hostile mob spawning.

            if (moved) {
                x = rand.nextInt(7500) - 3750;
                z = rand.nextInt(3750); // I don't want negative coords for the Z as that messes up hostile mob spawning.
            }

            manager.createWorld(name, diameter, seed, Environment.NORMAL, type, antiStripmine, orelimiter, newstone, x, z);

            if (netherD) {
                manager.createWorld(name + "_nether", diameter, seed, Environment.NETHER, type, false, orelimiter, true, x, z);
            }

            if (endD) {
                manager.createWorld(name + "_end", diameter, seed, Environment.THE_END, type, false, orelimiter, true, 0.0, 0.0); // end island is at 0,0
            }

            player.sendMessage(Main.PREFIX + "World creation finished.");
            break;
        case "cancel":
            player.sendMessage(Main.PREFIX + "Cancelled world creation.");
            player.closeInventory();
            break;
        }
    }

    /**
     * Update the GUI items.
     */
    public void update() {
        List<String> lore = new ArrayList<String>();

        ItemStack newStone = new ItemStack(Material.STONE, 1, (short) 2);
        ItemMeta newStoneMeta = newStone.getItemMeta();

        newStoneMeta.setDisplayName("§8» §61.8 Stone §8«");
        lore.add(" ");
        lore.add("§8» §7Currently: " + (newstone ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        lore.add(" ");
        lore.add("§8» §cDescription:");
        lore.add("§8» §7Granite, Diorite and Andesite in caves.");
        lore.add(" ");
        newStoneMeta.setLore(lore);
        newStone.setItemMeta(newStoneMeta);

        inv.setItem(10, newStone);
        lore.clear();

        ItemStack antiSM = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta antiSMMeta = antiSM.getItemMeta();

        antiSMMeta.setDisplayName("§8» §6Stripmining §8«");
        lore.add(" ");
        lore.add("§8» §7Currently: " + (antiStripmine ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        lore.add(" ");
        lore.add("§8» §cDescription:");
        lore.add("§8» §7If enabled it removes diamonds, lapis");
        lore.add("§8» §7and gold outside of caves.");
        lore.add(" ");
        antiSMMeta.setLore(lore);
        antiSMMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        antiSM.setItemMeta(antiSMMeta);

        inv.setItem(12, antiSM);
        lore.clear();

        ItemStack oreLimiter = new ItemStack(Material.DIAMOND_ORE);
        ItemMeta oreLimiterMeta = oreLimiter.getItemMeta();
        oreLimiterMeta.setDisplayName("§8» §6Ore Limiter §8«");
        lore.add(" ");
        lore.add("§8» §7Currently: " + orelimiter.getShortDescription().replace(".", ""));
        lore.add(" ");
        lore.add("§8» §cDescription:");
        lore.addAll(Lists.transform(orelimiter.getAdditionalLore(), "§8» "::concat));
        oreLimiterMeta.setLore(lore);
        oreLimiter.setItemMeta(oreLimiterMeta);

        inv.setItem(14, oreLimiter);
        lore.clear();

        ItemStack TBA = new ItemStack(Material.EMPTY_MAP);
        ItemMeta TBAMeta = TBA.getItemMeta();

        TBAMeta.setDisplayName("§8» §6Moved 0,0 §8«");
        lore.add(" ");
        lore.add("§8» §7Currently: " + (moved ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        lore.add(" ");
        lore.add("§8» §cDescription:");
        lore.add("§8» §7Makes the center at the world not at 0,0.");
        lore.add(" ");
        TBAMeta.setLore(lore);
        TBA.setItemMeta(TBAMeta);

        inv.setItem(16, TBA);
        lore.clear();

        ItemStack worldType = new ItemStack(Material.GRASS);
        ItemMeta typeMeta = worldType.getItemMeta();

        typeMeta.setDisplayName("§8» §6World Type §8«");
        lore.add(" ");
        lore.add("§8» §7Currently: §e" + NameUtils.capitalizeString(type.name(), true));
        lore.add(" ");
        lore.add("§8» §cDescription:");
        lore.add("§8» §7The world type, duh.");
        lore.add(" ");
        typeMeta.setLore(lore);
        worldType.setItemMeta(typeMeta);

        inv.setItem(28, worldType);
        lore.clear();

        ItemStack nether = new ItemStack(Material.NETHER_STALK);
        ItemMeta netherMeta = nether.getItemMeta();

        netherMeta.setDisplayName("§8» §6Nether §8«");
        lore.add(" ");
        lore.add("§8» §7Currently: " + (netherD ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        lore.add(" ");
        lore.add("§8» §cDescription:");
        lore.add("§8» §7The nether dimention.");
        lore.add(" ");
        netherMeta.setLore(lore);
        nether.setItemMeta(netherMeta);

        inv.setItem(30, nether);
        lore.clear();

        ItemStack end = new ItemStack(Material.ENDER_PORTAL_FRAME);
        ItemMeta newStoneMeta1 = end.getItemMeta();

        newStoneMeta1.setDisplayName("§8» §6The End §8«");
        lore.add(" ");
        lore.add("§8» §7Currently: " + (endD ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));
        lore.add(" ");
        lore.add("§8» §cDescription:");
        lore.add("§8» §7The end dimention.");
        lore.add(" ");
        newStoneMeta1.setLore(lore);
        end.setItemMeta(newStoneMeta1);

        inv.setItem(32, end);
        lore.clear();

        ItemStack confirm = new ItemStack(Material.INK_SACK, 1, (short) 10);
        ItemMeta confirmMeta = confirm.getItemMeta();

        confirmMeta.setDisplayName("§8» §aConfirm §8«");
        lore.add(" ");
        lore.add("§8» §7Create the world.");
        lore.add(" ");
        confirmMeta.setLore(lore);
        confirm.setItemMeta(confirmMeta);

        inv.setItem(35, confirm);
        lore.clear();

        ItemStack cancel = new ItemStack(Material.INK_SACK, 1, (short) 1);
        ItemMeta cancelMeta = cancel.getItemMeta();

        cancelMeta.setDisplayName("§8» §cCancel §8«");
        lore.add(" ");
        lore.add("§8» §7Cancel the world creation.");
        lore.add(" ");
        cancelMeta.setLore(lore);
        cancel.setItemMeta(cancelMeta);

        inv.setItem(43, cancel);
        lore.clear();

        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();

        infoMeta.setDisplayName("§8» §6World Information §8«");
        lore.add(" ");
        lore.add("§8» §7Name: §a" + name);
        lore.add(" ");
        lore.add("§8» §7Map Size: §a" + diameter + "x" + diameter);
        lore.add("§8» §7Seed: §a" + seed);
        lore.add(" ");
        infoMeta.setLore(lore);
        info.setItemMeta(infoMeta);

        inv.setItem(44, info);
        lore.clear();
    }
}