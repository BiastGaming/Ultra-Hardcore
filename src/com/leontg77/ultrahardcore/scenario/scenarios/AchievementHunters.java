package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.events.GameStartEvent;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.feature.health.GoldenHeadsFeature;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.utils.NameUtils;
import com.leontg77.ultrahardcore.utils.PlayerUtils;

/**
 * AchievementHunters scenario class.
 * 
 * @author dans1988, modified by LeonTG77
 */
public class AchievementHunters extends Scenario implements CommandExecutor, Listener {
    private static final String PREFIX = "§cAch. Hunters §8» §7";

    private final Main plugin;

    private final Timer timer;
    private final Game game;

    private final FeatureManager feat;
    private final Settings settings;

    public AchievementHunters(Main plugin, Game game, Timer timer, Settings settings, FeatureManager feat) {
        super("AchievementHunters", "If you gain achievements you get certain awards, read: http://pastebin.com/1KJ2y5c9");

        this.plugin = plugin;

        this.timer = timer;
        this.game = game;

        this.settings = settings;
        this.feat = feat;

        plugin.getCommand("alist").setExecutor(this);
    }

    private static final int IRON_MAN_CHECK_TIME = 20 * 60 * 60;  //ticks per second * seconds per minutes * 60 minutes

    private final Set<Achievement> awarded = new HashSet<Achievement>();
    private final Set<UUID> damaged = new HashSet<UUID>();
    
    private BukkitRunnable task;
    
    @Override
    public void onDisable() {
        if (task != null) {
            task.cancel();
        }

        task = null;

        awarded.clear();
        damaged.clear();
    }
    
    @Override
    public void onEnable() {
        awarded.clear();
        damaged.clear();

        // I don't want the code to place the normal skulls so then
        // I disable them instead since I have a custom dropping
        feat.getFeature(GoldenHeadsFeature.class).disable(settings);

        if (!game.isState(State.INGAME)) {
            return;
        }

        on(new GameStartEvent());
    }
    
    @EventHandler
    public void on(GameStartEvent event) {
        task = new BukkitRunnable() {
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!damaged.contains(player.getUniqueId())) {
                        awardPlayer(player, Achievement.IRONMAN);
                    }
                }
            }
        };
        
        task.runTaskLater(plugin, IRON_MAN_CHECK_TIME);
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerInteractEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Player player = event.getPlayer();
        
        if (!game.getPlayers().contains(player)) {
            return;
        }
        
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        ItemStack item = event.getItem();
        Block block = event.getClickedBlock();
        
        if (item == null || block == null) {
            return;
        }
        
        if (item.getType() == Material.CACTUS && block.getType() == Material.FLOWER_POT) {
            awardPlayer(player, Achievement.FIRST_CACTUS_IN_A_FLOWER_POT);
        }
        
        if (item.getType().isRecord() && block.getType() == Material.JUKEBOX) {
            awardPlayer(player, Achievement.FIRST_RECORD_IN_JUKEBOX);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void on(PlayerItemConsumeEvent event)  {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (!game.getPlayers().contains(player)) {
            return;
        }
        
        if (item.getType() == Material.GOLDEN_APPLE) {
            awardPlayer(player, Achievement.FIRST_GOLDEN_APPLE);
            awardPlayer(player, Achievement.GOLDEN_APPLES);
        }
    }


    @EventHandler(ignoreCancelled = true)
    public void on(CraftItemEvent event)  {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        
        if (!game.getPlayers().contains(player)) {
            return;
        }
        
        if (item == null) {
            return;
        }
        
        switch (item.getType()) {
        case ENCHANTMENT_TABLE:
            awardPlayer(player, Achievement.FIRST_ENCHANTMENT_TABLE);
            break;
        case BREWING_STAND_ITEM:
            awardPlayer(player, Achievement.FIRST_BREWING_STAND);
            break;
        case BOW:
            awardPlayer(player, Achievement.FIRST_BOW);
            break;
        case FISHING_ROD:
            awardPlayer(player, Achievement.FIRST_FISHING_ROD);
            break;
        case EMERALD_BLOCK:
            awardPlayer(player, Achievement.FIRST_EMERALD_BLOCK);
            break;
        case REDSTONE_BLOCK:
            awardPlayer(player, Achievement.FIRST_REDSTONE_BLOCK);
            break;
        case ANVIL:
            awardPlayer(player, Achievement.FIRST_ANVIL);
            break;
        case MINECART:
            awardPlayer(player, Achievement.FIRST_MINECART);
            break;
        case DIAMOND_SPADE:
            awardPlayer(player, Achievement.FIRST_DIAMOND_SHOVEL);
            break;
        case CHEST:
            awardPlayer(player, Achievement.FIRST_CHEST);
            break;
        case GOLDEN_APPLE:
            awardPlayer(player, Achievement.FIRST_GOLDEN_APPLE);
            break;
        case WORKBENCH:
            awardPlayer(player, Achievement.FIRST_CRAFTING_TABLE);
            break;
        default:
            break;
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(EntityDamageEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        if (timer.getTimeSinceStartInSeconds() < 20) {
            return;
        }
        
        Entity entity = event.getEntity();
        
        if (!(entity instanceof Player)) {
            return;
        }
        
        Player player = (Player) entity;
        
        if (!game.getPlayers().contains(player)) {
            return;
        }
        
        awardPlayer(player, Achievement.FIRST_DAMAGE);
        
        if (!damaged.contains(player.getUniqueId())) {
            PlayerUtils.broadcast(PREFIX + "§c" + player.getName() + " §7lost the iron man achievement!");
            damaged.add(player.getUniqueId());
        }
        
        if (event.getCause() == DamageCause.FALL) {
            awardPlayer(player, Achievement.FALL_DAMAGE);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void on(EntityDamageByEntityEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        
        if (!(entity instanceof Player)) {
            return;
        }
        
        Player player = (Player) entity;
        
        if (!(damager instanceof Projectile)) {
            return;
        }
        
        Projectile proj = (Projectile) damager;
        
        if (!(proj.getShooter() instanceof Player)) {
            return;
        }
        
        Player killer = (Player) proj.getShooter();
        
        if (!game.getPlayers().contains(player) || !game.getPlayers().contains(killer)) {
            return;
        }
        
        double distance = killer.getLocation().distance(player.getLocation());
        
        if (distance >= 100.0) {
            awardPlayer(killer, Achievement.LONGSHOT_100);
            return;
        }
        
        if (distance >= 60.0) {
            awardPlayer(killer, Achievement.LONGSHOT_60);
        }
    }

    
    @EventHandler(ignoreCancelled = true)
    public void on(PlayerPickupItemEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        ItemStack item = event.getItem().getItemStack();
        Player player = event.getPlayer();
        
        if (!game.getPlayers().contains(player)) {
            return;
        }
        
        if (item == null) {
            return;
        }
        
        if (item.getType() == Material.SADDLE) {
            awardPlayer(player, Achievement.FIRST_SADDLE);
        }

        if (item.getType() == Material.ENDER_PEARL) {
            awardPlayer(player, Achievement.FIRST_ENDERPEARL);
        }

        if (item.getType() == Material.BOW) {
            awardPlayer(player, Achievement.FIRST_BOW);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(InventoryClickEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }

        Player player = (Player) event.getWhoClicked(); // safe cast
        
        if (!game.getPlayers().contains(player)) {
            return;
        }
        
        InventoryView view = event.getView();
        ItemStack item = event.getCurrentItem();
        
        if (item == null) {
            return;
        }
        
        if (view.getType() != InventoryType.CHEST) {
            return;
        }
        
        if (item.getType() == Material.SADDLE) {
            awardPlayer(player, Achievement.FIRST_SADDLE);
        }

        if (item.getType() == Material.ENDER_PEARL) {
            awardPlayer(player, Achievement.FIRST_ENDERPEARL);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerDeathEvent event) {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Player player = event.getEntity();
        Player killer = player.getKiller();
        
        if (killer == null) {
            return;
        }
        
        if (!game.getPlayers().contains(player) || !game.getPlayers().contains(killer)) {
            return;
        }
        
        if (!killer.getUniqueId().equals(player.getUniqueId())) {
            awardPlayer(killer, Achievement.FIRST_BLOOD);
        }
    }
    
    @EventHandler(ignoreCancelled = true)
    public void on(PlayerPortalEvent event)  {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        Player player = event.getPlayer();
        
        if (!game.getPlayers().contains(player)) {
            return;
        }
        
        awardPlayer(player, Achievement.FIRST_NETHER_PORTAL);
    }
    
    @EventHandler(ignoreCancelled = true)
    public void on(EntityDeathEvent event)  {
        if (!game.isState(State.INGAME)) {
            return;
        }
        
        LivingEntity entity = event.getEntity();
        
        if (!(entity instanceof Witch)) {
            return;
        }
        
        Player killer = entity.getKiller();
        
        if (killer == null) {
            return;
        }
        
        if (!game.getPlayers().contains(killer)) {
            return;
        }
        
        awardPlayer(killer, Achievement.FIRST_KILLED_WITCH);
    }
    
    /**
     * Award the given achievement to the given player as well as broadcasting if needed.
     * 
     * @param player The player to give it to.
     * @param ach The achievement to give.
     */
    public void awardPlayer(Player player, Achievement ach) {
        String name = player.getName();

        switch (ach) {
        case FIRST_DAMAGE:
            if (awarded.contains(ach)) {
                return;
            }

            broadcast(name, ach);
            awarded.add(ach);

            player.setMaxHealth(18);
            break;
        case FIRST_BLOOD:
            if (awarded.contains(ach)) {
                return;
            }

            broadcast(name, ach);
            awarded.add(ach);

            PlayerUtils.giveItem(player, new ItemStack(Material.GOLD_INGOT, 7));
            PlayerUtils.giveItem(player, new ItemStack(Material.APPLE, 1));
            break;
        case FIRST_ENCHANTMENT_TABLE:
            if (awarded.contains(ach)) {
                return;
            }

            broadcast(name, ach);
            awarded.add(ach);

            PlayerUtils.giveItem(player, new ItemStack(Material.BOOK, 2));
            PlayerUtils.giveItem(player, new ItemStack(Material.EXP_BOTTLE, 10));
            break;
        case FIRST_BREWING_STAND:
            if (awarded.contains(ach)) {
                return;
            }

            broadcast(name, ach);
            awarded.add(ach);

            PlayerUtils.giveItem(player, new ItemStack(Material.SPECKLED_MELON, 1));
            break;
        case FIRST_BOW:
            if (awarded.contains(ach)) {
                return;
            }

            broadcast(name, ach);
            awarded.add(ach);

            PlayerUtils.giveItem(player, new ItemStack(Material.ARROW, 32));
            break;
        case FIRST_FISHING_ROD:
            if (awarded.contains(ach)) {
                return;
            }

            broadcast(name, ach);
            awarded.add(ach);

            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) book.getItemMeta();
            bookMeta.addStoredEnchant(Enchantment.LUCK, 1, true);
            book.setItemMeta(bookMeta);

            PlayerUtils.giveItem(player, new ItemStack(Material.COOKED_FISH, 16));
            PlayerUtils.giveItem(player, book);
            break;
        case FIRST_EMERALD_BLOCK:
            if (awarded.contains(ach)) {
                return;
            }

            broadcast(name, ach);
            awarded.add(ach);

            PlayerUtils.giveItem(player, new ItemStack(Material.MONSTER_EGG, 6, (short) 120));
            break;
        case FIRST_REDSTONE_BLOCK:
            if (awarded.contains(ach)) {
                return;
            }

            broadcast(name, ach);
            awarded.add(ach);

            PlayerUtils.giveItem(player, new ItemStack(Material.DIAMOND, 1));
            break;
        case FIRST_SADDLE:
            if (awarded.contains(ach)) {
                return;
            }

            broadcast(name, ach);
            awarded.add(ach);

            PlayerUtils.giveItem(player, new ItemStack(Material.MONSTER_EGG, 1, (short) 100));
            break;
        case FIRST_ENDERPEARL:
            if (awarded.contains(ach)) {
                return;
            }

            broadcast(name, ach);
            awarded.add(ach);

            PlayerUtils.giveItem(player, new ItemStack(Material.ENDER_PEARL, 2));
            break;
        case FIRST_CACTUS_IN_A_FLOWER_POT:
            if (awarded.contains(ach)) {
                return;
            }

            broadcast(name, ach);
            awarded.add(ach);

            ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
            skullMeta.setOwner("NTBama");
            skull.setItemMeta(skullMeta);
            
            PlayerUtils.giveItem(player, skull);
            break;
        case FIRST_ANVIL:
            if (awarded.contains(ach)) {
                return;
            }

            broadcast(name, ach);
            awarded.add(ach);

            PlayerUtils.giveItem(player, new ItemStack(Material.BOOK, 8));
            break;
        case FIRST_MINECART:
            if (awarded.contains(ach)) {
                return;
            }

            broadcast(name, ach);
            awarded.add(ach);

            PlayerUtils.giveItem(player, new ItemStack(Material.RAILS, 64));
            PlayerUtils.giveItem(player, new ItemStack(Material.POWERED_RAIL, 8));
            break;
        case FIRST_KILLED_WITCH:
            if (awarded.contains(ach)) {
                return;
            }

            broadcast(name, ach);
            awarded.add(ach);

            PlayerUtils.giveItem(player, new ItemStack(Material.POTION, 2, (short) 16460));
            break;
        case FIRST_RECORD_IN_JUKEBOX:
            if (awarded.contains(ach)) {
                return;
            }

            broadcast(name, ach);
            awarded.add(ach);

            PlayerUtils.giveItem(player, new ItemStack(Material.WOOD, 8));
            PlayerUtils.giveItem(player, new ItemStack(Material.DIAMOND, 1));
            break;
        case FIRST_CRAFTING_TABLE:
            if (awarded.contains(ach)) {
                return;
            }

            broadcast(name, ach);
            awarded.add(ach);

            PlayerUtils.giveItem(player, new ItemStack(Material.STICK, 1));
            break;
        case FIRST_CHEST:
            if (awarded.contains(ach)) {
                return;
            }

            broadcast(name, ach);
            awarded.add(ach);

            PlayerUtils.giveItem(player, new ItemStack(Material.CHEST, 64));
            break;
        case FIRST_GOLDEN_APPLE:
            if (awarded.contains(ach)) {
                return;
            }

            broadcast(name, ach);
            awarded.add(ach);

            PlayerUtils.giveItem(player, new ItemStack(Material.GOLDEN_APPLE, 1));
            break;
        case FIRST_NETHER_PORTAL:
            if (awarded.contains(ach)) {
                return;
            }

            broadcast(name, ach);
            awarded.add(ach);

            PlayerUtils.giveItem(player, new ItemStack(Material.GOLD_INGOT, 4));
        case FIRST_DIAMOND_SHOVEL:
            if (awarded.contains(ach)) {
                return;
            }

            broadcast(name, ach);
            awarded.add(ach);

            ItemStack skull2 = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            SkullMeta skullMeta2 = (SkullMeta) skull2.getItemMeta();
            skullMeta2.setOwner("TommySX");
            skull2.setItemMeta(skullMeta2);
            
            PlayerUtils.giveItem(player, skull2);
            PlayerUtils.giveItem(player, new ItemStack(Material.MONSTER_EGG, 1, (short) 50));
            break;
        case FALL_DAMAGE:
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 1));
            break;
        case GOLDEN_APPLES:
            player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1));
            break;
        case LONGSHOT_100:
            double maxHealth = player.getMaxHealth();
            double health = player.getHealth();
            
            if ((health + 4) > maxHealth) {
                health = maxHealth;
            } else {
                health = health + 4;
            }
            
            if (player.getHealth() > 0) {
                player.setHealth(health);
            }
            break;
        case LONGSHOT_60:
            double maxHealth2 = player.getMaxHealth();
            double health2 = player.getHealth();
            
            if ((health2 + 2) > maxHealth2) {
                health2 = maxHealth2;
            } else {
                health2 = health2 + 2;
            }
            
            if (player.getHealth() > 0) {
                player.setHealth(health2);
            }
            break;
        case IRONMAN:
            broadcast(name, ach);
            player.setMaxHealth(24);
            break;
        default:
            break;
        }
    }

    /**
     * Broadcast that the given name has gotten the given achievement.
     * 
     * @param ach The achievement gotten.
     * @param name The name of the getter.
     */
    private void broadcast(String name, Achievement ach) {
        PlayerUtils.broadcast(PREFIX + "§a" + NameUtils.capitalizeString(ach.name(), true) + " §7was awarded to §e" + name + "§7.");
    }

    /**
     * Get a list of all damaged players' UUID.
     * 
     * @return A list of damaged players.
     */
    public Set<UUID> getDamagedPlayers() {
        return damaged;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!isEnabled()) {
            sender.sendMessage(PREFIX + "AchievementHunters is not enabled.");
            return true;
        }

        StringBuilder list = new StringBuilder();
        int i = 1;
        
        for (Achievement ach : Achievement.values()) {
            if (!ach.isOneTime()) {
                continue;
            }

            if (list.length() > 0) {
                if (i == Achievement.values().length) {
                    list.append(" §7and §a");
                } else {
                    list.append("§7, §a");
                }
            }
            
            if (awarded.contains(ach)) {
                list.append(ChatColor.RED + NameUtils.capitalizeString(ach.name(), true));
            } else {
                list.append(ChatColor.GREEN + NameUtils.capitalizeString(ach.name(), true));
            }
        }
        
        sender.sendMessage(PREFIX + "Achievements: §8(§aGreen§7 = Available§8, §cRed §7= Taken§8)");
        sender.sendMessage(Main.ARROW + list.toString());
        return true;
    }

    /**
     * Achievements for the scenario.
     * 
     * @author dans1988
     */
    public enum Achievement {
        FIRST_DAMAGE(true),
        FIRST_BLOOD(true),
        FIRST_ENCHANTMENT_TABLE(true),
        FIRST_BREWING_STAND(true),
        FIRST_BOW(true),
        FIRST_FISHING_ROD(true),
        FIRST_EMERALD_BLOCK(true),
        FIRST_REDSTONE_BLOCK(true),
        FIRST_NETHER_PORTAL(true),
        FIRST_SADDLE(true),
        FIRST_ENDERPEARL(true),
        FIRST_CACTUS_IN_A_FLOWER_POT(true),
        FIRST_KILLED_WITCH(true),
        FIRST_ANVIL(true),
        FIRST_MINECART(true),
        FIRST_RECORD_IN_JUKEBOX(true),
        FIRST_CRAFTING_TABLE(true),
        FIRST_CHEST(true),
        FALL_DAMAGE(false),
        FIRST_GOLDEN_APPLE(true),
        FIRST_DIAMOND_SHOVEL(true),
        GOLDEN_APPLES(false),
        LONGSHOT_100(false),
        LONGSHOT_60(false),
        IRONMAN(false);

        private final boolean oneTime;

        /**
         * Achievement class constructor.
         *
         * @param oneTime Wether or not this is a one time achievement.
         */
        private Achievement(boolean oneTime) {
            this.oneTime = oneTime;
        }

        /**
         * Check if this achievement is a one time achievement.
         *
         * @return True if it is, false otherwise.
         */
        public boolean isOneTime() {
            return oneTime;
        }
    }
}