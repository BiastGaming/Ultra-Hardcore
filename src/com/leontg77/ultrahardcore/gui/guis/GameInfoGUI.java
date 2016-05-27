package com.leontg77.ultrahardcore.gui.guis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Game.State;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.User.Rank;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.feature.border.BorderShrinkFeature;
import com.leontg77.ultrahardcore.feature.border.BorderShrinkFeature.BorderShrink;
import com.leontg77.ultrahardcore.feature.death.DeathLightningFeature;
import com.leontg77.ultrahardcore.feature.enchants.AnvilsFeature;
import com.leontg77.ultrahardcore.feature.enchants.BookshelfFeature;
import com.leontg77.ultrahardcore.feature.enchants.EnchantmentPreviewFeature;
import com.leontg77.ultrahardcore.feature.entity.WitchHealthPotionFeature;
import com.leontg77.ultrahardcore.feature.health.AbsorptionFeature;
import com.leontg77.ultrahardcore.feature.health.GoldenHeadsFeature;
import com.leontg77.ultrahardcore.feature.horses.HorseArmorFeature;
import com.leontg77.ultrahardcore.feature.horses.HorseFeature;
import com.leontg77.ultrahardcore.feature.horses.HorseHealingFeature;
import com.leontg77.ultrahardcore.feature.pearl.PearlDamageFeature;
import com.leontg77.ultrahardcore.feature.portal.EndFeature;
import com.leontg77.ultrahardcore.feature.portal.NetherFeature;
import com.leontg77.ultrahardcore.feature.portal.PortalCampingFeature;
import com.leontg77.ultrahardcore.feature.portal.PortalTrappingFeature;
import com.leontg77.ultrahardcore.feature.potions.SplashPotionFeature;
import com.leontg77.ultrahardcore.feature.potions.StrengthPotionFeature;
import com.leontg77.ultrahardcore.feature.potions.Tier2PotionFeature;
import com.leontg77.ultrahardcore.feature.pvp.StalkingFeature;
import com.leontg77.ultrahardcore.feature.rates.AppleRatesFeature;
import com.leontg77.ultrahardcore.feature.rates.FlintRatesFeature;
import com.leontg77.ultrahardcore.feature.rates.ShearsFeature;
import com.leontg77.ultrahardcore.feature.recipes.NotchApplesFeature;
import com.leontg77.ultrahardcore.feature.xp.NerfedQuartzXPFeature;
import com.leontg77.ultrahardcore.feature.xp.NerfedXPFeature;
import com.leontg77.ultrahardcore.gui.GUI;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.scenario.scenarios.AchievementHunters;
import com.leontg77.ultrahardcore.scenario.scenarios.Moles;
import com.leontg77.ultrahardcore.scenario.scenarios.PotentialMoles;
import com.leontg77.ultrahardcore.scenario.scenarios.PotentialPermanent;
import com.leontg77.ultrahardcore.scenario.scenarios.VengefulSpirits;
import com.leontg77.ultrahardcore.utils.DateUtils;
import com.leontg77.ultrahardcore.utils.FileUtils;
import com.leontg77.ultrahardcore.utils.NameUtils;
import com.leontg77.ultrahardcore.utils.NumberUtils;

/**
 * GameInfo inventory GUI class.
 * 
 * @author LeonTG77.
 */
public class GameInfoGUI extends GUI implements Listener {
    private final Settings settings;
    private final Main plugin;

    private final Timer timer;
    private final Game game;

    private final FeatureManager feat;
    private final ScenarioManager scen;

    /**
     * GameInfo inventory GUI class constructor.
     *
     * @param plugin The main class.
     * @param settings The settings class.
     * @param game The game class.
     * @param timer The timer class.
     * @param feat The feature manager class.
     * @param scen The scenario manager class.
     */
    public GameInfoGUI(Main plugin, Settings settings, Game game, Timer timer, FeatureManager feat, ScenarioManager scen) {
        super("GameInfo", "A inventory with informative items.");

        this.settings = settings;
        this.plugin = plugin;

        this.timer = timer;
        this.game = game;

        this.feat = feat;
        this.scen = scen;
    }

    private final Inventory inv = Bukkit.createInventory(null, 45, "§4UHC Game Information");

    @Override
    public void onSetup() {
        glassify(inv);
        updateStaff();
        update();

        new BukkitRunnable() {
            public void run() {
                updateTimer();
            }
        }.runTaskTimer(plugin, 15, 15);
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) {
            return;
        }
        
        HumanEntity player = event.getWhoClicked();
        
        ItemStack item = event.getCurrentItem();
        Inventory inv = event.getInventory();

        if (!this.inv.getTitle().equals(inv.getTitle())) {
            return;
        }

        event.setCancelled(true);

        if (item == null) {
            return;
        }

        if (!item.hasItemMeta() || !item.getItemMeta().hasLore() || !item.getItemMeta().hasDisplayName()) {
            return;
        }

        player.sendMessage("§8» §m--------" + item.getItemMeta().getDisplayName() + "§8§m--------§8 «");

        for (String lore : item.getItemMeta().getLore()) {
            player.sendMessage(lore);
        }

        player.sendMessage("§8» §m----------------------------§8 «");
    }

    /**
     * Get the GameInfo inventory.
     *
     * @return The inventory.
     */
    public Inventory get() {
        return inv;
    }

    /**
     * Update the item lores in the inventory.
     */
    public void update() {
        List<String> lore = new ArrayList<String>();

        // general info item
        ItemStack general = new ItemStack(Material.SIGN);
        ItemMeta generalMeta = general.getItemMeta();
        generalMeta.setDisplayName("§8» §6General Info §8«");
        lore.add(" ");
        lore.add("§8» §7Server Made on: §626th of May, 2015");
        lore.add(" ");
        lore.add("§8» §fHello, we're ArcticUHC and we love to host");
        lore.add("§8» §fUHC's for you to enjoy! Our server runs on a");
        lore.add("§8» §fcustom plugin and no skripts. If you got any");
        lore.add("§8» §fcomplains/ideas, just tell us!");
        lore.add(" ");
        lore.add("§8» §6General Rules §8«");
        lore.add(" ");
        lore.add("§8» §7Misuse of Helpop: §9Disqualify");
        lore.add("§8» §7Benefitting: §9Disqualify");
        lore.add("§8» §7Lag Machine: §9Disqualify");
        lore.add(" ");
        lore.add("§8» §7Bug Abuse: §4Temp-Ban");
        lore.add(" ");
        lore.add("§8» §7Usage of a Hacked Client: §4Ban");
        lore.add("§8» §7Xray Client/Pack: §4Ban");
        lore.add("§8» §7Inappropriate Name: §4Ban");
        lore.add(" ");
        generalMeta.setLore(lore);
        general.setItemMeta(generalMeta);
        lore.clear();

        ItemStack chat = new ItemStack(Material.PAPER);
        ItemMeta chatMeta = chat.getItemMeta();
        chatMeta.setDisplayName("§8» §6Chat Rules §8«");;
        lore.add(" ");
        lore.add("§8» §7Spoiling after death: §cNot Allowed");
        lore.add("§8» §7Spoiling while alive: §aAllowed");
        lore.add(" ");
        lore.add("§8» §7Bad sportsmanship: §cMute");
        lore.add("§8» §7Spamming: §cMute");
        lore.add(" ");
        lore.add("§8» §7Disrespect: §cMute§7/§4Ban §8(§7Depends§8)");
        lore.add("§8» §7Harrasment: §cMute§7/§4Ban §8(§7Depends§8)");
        lore.add("§8» §7Racism: §cMute§7/§4Ban §8(§7Depends§8)");
        lore.add(" ");
        lore.add("§8» §7Death Threats: §cMute§7/§4Ban §8(§7Depends§8)");
        lore.add("§8» §7Death Wishes: §cMute§7/§4Ban §8(§7Depends§8)");
        lore.add("§8» §7Death Promises: §cMute§7/§4Ban §8(§7Depends§8)");
        lore.add("§8» §7DDoS Threats: §4Ban");
        lore.add(" ");
        lore.add("§8» §7Talking other languages in chat: §cMute");
        lore.add("§8» §7Reasoning: §eWe want everyone to be able");
        lore.add("§8» §eto understand whats said in the chat, as well");
        lore.add("§8» §ewe want to be able to moderate if anything");
        lore.add("§8» §emean/bad is said. Keep in mind, this only");
        lore.add("§8» §eapplies for the main chat, not team chat (etc)");
        lore.add(" ");
        chatMeta.setLore(lore);
        chat.setItemMeta(chatMeta);
        lore.clear();

        String teamKilling;

        if (game.getTeamSize().startsWith("rTo")) {
            if (scen.getScenario(Moles.class).isEnabled() || scen.getScenario(PotentialMoles.class).isEnabled()) {
                teamKilling = "§aAllowed";
            } else {
                teamKilling = "§cNot Allowed";
            }
        }
        else if (game.getTeamSize().equals("FFA")) {
            teamKilling = "§cNo teaming at all";
        }
        else if (game.getTeamSize().startsWith("cTo")) {
            teamKilling = "§aAllowed";
        }
        else {
            teamKilling = "§cNot Allowed";
        }

        ItemStack pvp = new ItemStack (Material.DIAMOND_SWORD);
        ItemMeta pvpMeta = pvp.getItemMeta();
        pvpMeta.setDisplayName("§8» §6PvP Rules §8«");
        lore.add(" ");
        lore.add("§8» §7iPvP: §cNot Allowed before PvP.");
        lore.add(" ");
        lore.add("§8» §7Stalking: " + feat.getFeature(StalkingFeature.class).getStalkingRule().getMessage());
        lore.add("§8» §7Stealing: §aAllowed.");
        lore.add(" ");
        lore.add("§8» §7Crossteaming/Allying: §cNot Allowed.");
        lore.add("§8» §7Team Killing: " + teamKilling);
        lore.add(" ");
        lore.add("§8» §7Teaming in the arena: §cNot Allowed.");
        lore.add(" ");
        pvpMeta.setLore(lore);
        pvpMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        pvp.setItemMeta(pvpMeta);
        lore.clear();

        ItemStack mining = new ItemStack (Material.IRON_PICKAXE);
        ItemMeta miningMeta = mining.getItemMeta();
        miningMeta.setDisplayName("§8» §6Mining Rules §8«");
        lore.add(" ");
        lore.add("§8» §4Information:");
        if (game.getWorld() == null) {
            lore.add("§8» §7No game world has been set yet");
            lore.add("§8» §7for the rule to come up.");
        } else if (settings.getWorlds().getBoolean(game.getWorld().getName() + ".antiStripmine", true)) {
            lore.add("§8» §7Gold, diamonds and lapis only spawns near caves.");
            lore.add(" ");
            lore.add("§8» §7Stripmining: §aAllowed.");
            lore.add("§8» §7Branchmining: §aAllowed.");
            lore.add("§8» §7Pokeholing: §aAllowed.");
            lore.add(" ");
            lore.add("§8» §7Blastmining: §aAllowed.");
            lore.add("§8» §7Staircasing: §aAllowed.");
            lore.add("§8» §7Rollercoastering: §aAllowed.");
            lore.add(" ");
            lore.add("§8» §7Digging to sounds: §aAllowed.");
            lore.add("§8» §7Digging to entities: §aAllowed.");
            lore.add("§8» §7Digging to players: §aAllowed.");
            lore.add(" ");
            lore.add("§8» §7Digging straight down: §aAllowed.");
            lore.add("§8» §7Digging around lava: §aAllowed.");
        } else {
            lore.add("§8» §7The \"ores in caves\" feature is disabled.");
            lore.add(" ");
            lore.add("§8» §7Stripmining: §cNot Allowed.");
            lore.add("§8» §7Branchmining: §cNot Allowed.");
            lore.add("§8» §7Pokeholing: §cOnly to caves.");
            lore.add(" ");
            lore.add("§8» §7Blastmining: §aAllowed.");
            lore.add("§8» §7Staircasing: §aAllowed, §ey:32➷.");
            lore.add("§8» §7Rollercoastering: §aAllowed, §ey:2➹y:32➷y:2➹y:32.");
            lore.add(" ");
            lore.add("§8» §7Digging to sounds: §aAllowed.");
            lore.add("§8» §7Digging to entities: §aAllowed.");
            lore.add("§8» §7Digging to players: §cOnly if you see them.");
            lore.add(" ");
            lore.add("§8» §7Digging straight down: §aAllowed.");
            lore.add("§8» §7Digging around lava: §aAllowed.");
        }
        lore.add(" ");
        miningMeta.setLore(lore);
        miningMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        mining.setItemMeta(miningMeta);
        lore.clear();

        BorderShrink border = feat.getFeature(BorderShrinkFeature.class).getBorderShrink();

        ItemStack meetup = new ItemStack (Material.EYE_OF_ENDER);
        ItemMeta miscMeta = meetup.getItemMeta();
        miscMeta.setDisplayName("§8» §6Meetup Rules §8«");
        lore.add(" ");
        if (border == BorderShrink.NEVER) {
            lore.add("§8» §7Strict Meetup: §aYes.");
            lore.add(" ");
            lore.add("§8» §7Towering: " + (game.isRecordedRound() ? "§cDisallowed" : "§eOnly allowed before meetup."));
            lore.add("§8» §7Forting: §eOnly allowed before meetup.");
            lore.add("§8» §7Digging down: §eOnly allowed before meetup.");
            lore.add(" ");
            lore.add("§8» §7At meetup you have to go to 0,0 and fight, running");
            lore.add("§8» §7and stalling will be disallowed and you may only");
            lore.add("§8» §7fight and pick up loot, no farming apples etc.");
            lore.add(" ");
            lore.add("§8» §7Border shrinks: §6Never.");
        } else {
            lore.add("§8» §7Strict Meetup: §cNo.");
            lore.add(" ");
            lore.add("§8» §7Towering: " + (game.isRecordedRound() ? "§cDisallowed" : "§eOnly allowed before meetup."));
            lore.add("§8» §7Forting: §eOnly allowed before meetup.");
            lore.add("§8» §7Digging down: §eOnly allowed before meetup.");
            lore.add(" ");
            lore.add("§8» §7At meetup you are allowed to do anything you may");
            lore.add("§8» §7want to, but you have to be inside of the MC border");
            lore.add("§8» §7and on the surface, rules above also apply.");
            lore.add(" ");
            lore.add("§8» §7Border shrinks: §6" + NameUtils.capitalizeString(border.getPreText(), false) + border.name().toLowerCase() + ".");
            lore.add("§8» §7The border will damage you if you go");
            lore.add("§8» §7outside, until it eventually kills you");
        }

        lore.add(" ");
        miscMeta.setLore(lore);
        meetup.setItemMeta(miscMeta);
        lore.clear();

        ItemStack commands = new ItemStack (Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta commandsMeta = (SkullMeta) commands.getItemMeta();
        commandsMeta.setOwner("MHF_Exclamation");
        commandsMeta.setDisplayName("§8» §6Useful commands §8«");
        lore.add(" ");
        lore.add("§a/uhc §8» §7§oView this menu, omg!");
        lore.add("§a/helpop §8» §7§oAsk for help by the staff.");
        lore.add("§a/list §8» §7§oView online players.");
        lore.add(" ");
        lore.add("§a/post §8» §7§oGet a link to the matchpost.");
        lore.add("§a/scen §8» §7§oView the enabled scenarios.");
        lore.add(" ");
        lore.add("§a/timeleft §8» §7§oView the timer.");
        lore.add("§a/border §8» §7§oView the current border size.");
        lore.add(" ");
        lore.add("§a/team §8» §7§oView the team help menu.");
        lore.add("§a/hof §8» §7§oView the hall of fame.");
        lore.add(" ");
        lore.add("§a/lag §8» §7§oView the server performance.");
        lore.add("§a/ms §8» §7§oView your or someones ping.");
        lore.add(" ");
        lore.add("§a/pm §8» §7§oTalk in team chat.");
        lore.add("§a/tl §8» §7§oTell your team your coords.");
        lore.add(" ");
        lore.add("§a/ct §8» §7§oCheck if you are in combat.");
        lore.add("§a/hotbar §8» §7§oChange your arena hotbar setup.");
        lore.add(" ");
        commandsMeta.setLore(lore);
        commands.setItemMeta(commandsMeta);
        lore.clear();

        ItemStack scenario = new ItemStack (Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta scenarioMeta = (SkullMeta) commands.getItemMeta();
        scenarioMeta.setOwner("MHF_Question");
        scenarioMeta.setDisplayName("§8» §6Game Information §8«");
        if (game.getAdvancedTeamSize(false, false).startsWith("No")) {
            lore.add(" ");
            lore.add("§8» §7There are no games running.");
            lore.add(" ");
        } else if (game.getAdvancedTeamSize(false, false).startsWith("Open")) {
            lore.add(" ");
            lore.add("§8» §7Currently having Open " + game.getScenarios());
            lore.add(" ");
        } else {
            lore.add(" ");
            lore.add("§8» §7Host: §a" + game.getHost());
            lore.add(" ");
            lore.add("§8» §7Max player slots: §a" + game.getMaxPlayers());
            lore.add("§8» §7Match post: §a" + game.getMatchPost());
            lore.add(" ");
            lore.add("§8» §7PvP enabled after: §a" + game.getPvP() + " minutes.");
            lore.add("§8» §7Meetup is after: §a" + game.getMeetup() + " minutes.");
            lore.add(" ");
            lore.add("§8» §cTeamsize:");
            lore.add("§8» §7" + game.getAdvancedTeamSize(true, false));
            lore.add(" ");
            lore.add("§8» §cScenarios:");
            for (String scen : game.getScenarios().split(", ")) {
                lore.add("§8» §7" + scen);
            }
            lore.add(" ");
        }
        scenarioMeta.setLore(lore);
        scenario.setItemMeta(scenarioMeta);
        lore.clear();

        ItemStack nether = new ItemStack (Material.NETHER_STALK);
        ItemMeta netherMeta = nether.getItemMeta();
        netherMeta.setDisplayName("§8» §6Nether Info §8«");
        lore.add(" ");
        lore.add("§8» §7Nether: " + (feat.getFeature(NetherFeature.class).isEnabled() ? "§aEnabled." : "§cDisabled."));
        lore.add("§8» §7The End: " + (feat.getFeature(EndFeature.class).isEnabled() ? "§aEnabled." : "§cDisabled."));
        lore.add(" ");
        if (feat.getFeature(NetherFeature.class).isEnabled()) {
            lore.add("§8» §7Trapping: " + (feat.getFeature(PortalTrappingFeature.class).isEnabled() ? "§aAllowed." : "§cNot allowed."));
            lore.add("§8» §7Camping: " + (feat.getFeature(PortalCampingFeature.class).isEnabled() ? "§aAllowed." : "§cNot allowed."));
            lore.add(" ");
            lore.add("§8» §7Strength: " + (feat.getFeature(StrengthPotionFeature.class).isEnabled() ? "§cNerfed to MC 1.5 values" : "§cDisabled"));
            lore.add("§8» §7Tier 2: " + (feat.getFeature(Tier2PotionFeature.class).isEnabled() ? "§aEnabled." : "§cDisabled."));
            lore.add("§8» §7Splash: " + (feat.getFeature(SplashPotionFeature.class).isEnabled() ? "§aEnabled." : "§cDisabled."));
            lore.add(" ");
            lore.add("§8» §7Quartz XP: " + (feat.getFeature(NerfedQuartzXPFeature.class).isEnabled() ? "§c50% reduced." : "§aVanilla."));
            lore.add(" ");
            lore.add("§8» §7Golden Carrot: §6Gold Ingots.");
            lore.add("§8» §7Golden Melon: §6Gold Ingots.");
            lore.add("§8» §7Ghast Drop: §6Gold Ingot.");
            lore.add(" ");
        }
        netherMeta.setLore(lore);
        nether.setItemMeta(netherMeta);
        lore.clear();

        String heal = NumberUtils.formatDouble(feat.getFeature(GoldenHeadsFeature.class).getHealAmount());

        ItemStack healing = new ItemStack (Material.GOLDEN_APPLE);
        ItemMeta healingMeta = healing.getItemMeta();
        healingMeta.setDisplayName("§8» §6Healing Info §8«");
        lore.add(" ");
        if (scen.getScenario(PotentialPermanent.class).isEnabled()) {
            lore.add("§8» §7Absorption: §cDisabled.");
        }
        else if (feat.getFeature(AbsorptionFeature.class).isEnabled()) {
            lore.add("§8» §7Absorption: §aEnabled.");
        }
        else {
            lore.add("§8» §7Absorption: §cDisabled.");
        }
        lore.add("§8» §7Notch Apples: " + (feat.getFeature(NotchApplesFeature.class).isEnabled() ? "§aEnabled." : "§cDisabled."));
        lore.add(" ");
        if (feat.getFeature(GoldenHeadsFeature.class).isEnabled()) {
            lore.add("§8» §7Golden Heads: §aEnabled. §8(§7On a head post§8)");
            lore.add("§8» §7Heads Heal: §6" + heal + " hearts.");
        }
        else if (scen.getScenario(VengefulSpirits.class).isEnabled()) {
            lore.add("§8» §7Golden Heads: §aEnabled. §8(§7From VengefulSpirits§8)");
            lore.add("§8» §7Heads Heal: §6" + heal + " hearts.");
        }
        else if (scen.getScenario(AchievementHunters.class).isEnabled()) {
            lore.add("§8» §7Golden Heads: §aEnabled. §8(§7Only from achievements awards§8)");
            lore.add("§8» §7Heads Heal: §6" + heal + " hearts.");
        }
        else {
            lore.add("§8» §7Golden Heads: §cDisabled");
        }
        lore.add(" ");
        healingMeta.setLore(lore);
        healing.setItemMeta(healingMeta);
        lore.clear();

        String shear = NumberUtils.formatDouble(feat.getFeature(ShearsFeature.class).getShearRates() * 100);

        ItemStack rates = new ItemStack (Material.FLINT);
        ItemMeta ratesMeta = rates.getItemMeta();
        ratesMeta.setDisplayName("§8» §6Rates Info §8«");
        lore.add(" ");
        lore.add("§8» §7Apple Rates: §6" + NumberUtils.formatDouble(feat.getFeature(AppleRatesFeature.class).getAppleRates() * 100) + "%");
        lore.add("§8» §7Flint Rates: §6" + NumberUtils.formatDouble(feat.getFeature(FlintRatesFeature.class).getFlintRate() * 100) + "%");
        lore.add(" ");
        lore.add("§8» §7Witch Pot. Rate: " + (feat.getFeature(WitchHealthPotionFeature.class).isEnabled() ? "§630% (100% when poisoned)." : "§aVanilla."));
        lore.add("§8» §7Mob Rates: §6Vanilla, Difficulty Hard.");
        lore.add(" ");
        lore.add("§8» §7Shears for apples: " + (feat.getFeature(ShearsFeature.class).isEnabled() ? "§aWork, " + shear + "%" : "§cDoes not work."));
        lore.add("§8» §7Shears for leaves: §aWork.");
        lore.add(" ");
        ratesMeta.setLore(lore);
        rates.setItemMeta(ratesMeta);
        lore.clear();

        ItemStack horse = new ItemStack (Material.SADDLE);
        ItemMeta horseMeta = horse.getItemMeta();
        horseMeta.setDisplayName("§8» §6Horse Info §8«");
        lore.add(" ");
        lore.add("§8» §7Horses: " + (feat.getFeature(HorseFeature.class).isEnabled() ? "§aEnabled." : "§cDisabled."));
        if (feat.getFeature(HorseFeature.class).isEnabled()) {
            lore.add(" ");
            lore.add("§8» §7Horse Armor: " + (feat.getFeature(HorseArmorFeature.class).isEnabled() ? "§aEnabled." : "§cDisabled."));
            lore.add("§8» §7Horse Healing: " + (feat.getFeature(HorseHealingFeature.class).isEnabled() ? "§aEnabled." : "§cDisabled."));
        }
        lore.add(" ");
        horseMeta.setLore(lore);
        horse.setItemMeta(horseMeta);
        lore.clear();

        double pDamage = feat.getFeature(PearlDamageFeature.class).getPearlDamage();

        ItemStack misc = new ItemStack (Material.ENDER_PEARL);
        ItemMeta miscIMeta = misc.getItemMeta();
        miscIMeta.setDisplayName("§8» §6Misc. Info §8«");
        lore.add(" ");
        lore.add("§8» §7Saturation Fix: §aEnabled.");
        lore.add("§8» §7Starter food: §cNone.");
        lore.add(" ");
        lore.add("§8» §7Enderpearl Damage: " + (pDamage != 0 ? "§aDeals " + (pDamage == 1 ? "1 heart." : pDamage + " hearts."): "§cDisabled."));
        lore.add("§8» §7Death Lightning: " + (feat.getFeature(DeathLightningFeature.class).isEnabled() ? "§aEnabled." : "§cDisabled."));
        lore.add(" ");
        lore.add("§8» §7Enchantment Preview: " + (feat.getFeature(EnchantmentPreviewFeature.class).isEnabled() ? "§aEnabled." : "§cDisabled."));
        lore.add("§8» §7Bookshelves: " + (feat.getFeature(BookshelfFeature.class).isEnabled() ? "§aEnabled." : "§cDisabled."));
        lore.add("§8» §7Anvils: " + (feat.getFeature(AnvilsFeature.class).isEnabled() ? "§aEnabled." : "§cDisabled."));
        lore.add(" ");
        lore.add("§8» §7Nerfed XP: " + (feat.getFeature(NerfedXPFeature.class).isEnabled() ? "§aEnabled." : "§cDisabled."));
        lore.add(" ");
        lore.add("§8» §7Ore Limiter: §6" + (game.getWorld() == null ? "§cN/A" : settings.getWorlds().getBoolean(game.getWorld().getName() + ".oreLimiter", false) ? "§aEnabled." : "§cDisabled."));
        lore.add("§8» §71.8 Stone: §6" + (game.getWorld() == null ? "§cN/A" : settings.getWorlds().getBoolean(game.getWorld().getName() + ".newStone", false) ? "§aEnabled." : "§cDisabled."));
        lore.add(" ");
        miscIMeta.setLore(lore);
        misc.setItemMeta(miscIMeta);
        lore.clear();

        inv.setItem(0, general);
        inv.setItem(2, chat);
        inv.setItem(4, pvp);
        inv.setItem(6, mining);
        inv.setItem(8, meetup);

        inv.setItem(21, commands);
        inv.setItem(23, scenario);

        inv.setItem(36, healing);
        inv.setItem(38, nether);
        inv.setItem(40, rates);
        inv.setItem(42, horse);
        inv.setItem(44, misc);
    }

    /**
     * Update the timer item in the inventory.
     */
    public void updateTimer() {
        ItemStack timerItem = new ItemStack (Material.WATCH);
        ItemMeta timerMeta = timerItem.getItemMeta();
        timerMeta.setDisplayName("§8» §6Timers §8«");

        List<String> lore = new ArrayList<String>();
        lore.add(" ");

        int timePassed = timer.getTimeSinceStartInSeconds();
        int meetup = timer.getMeetupInSeconds();
        int pvp = timer.getPvPInSeconds();

        int finalheal = 20 - timePassed;

        if (game.isRecordedRound()) {
            lore.add("§8» §7Current Episode: §a" + timer.getMeetup() + ".");
            lore.add("§8» §7Time to next episode: §a" + timer.getTimeSinceStart() + " minute(s).");
        } else if (game.getAdvancedTeamSize(false, false).startsWith("No") || game.getAdvancedTeamSize(false, false).startsWith("Open")) {
            lore.add("§8» §7There are no matches running.");
        } else if (!game.isState(State.INGAME)) {
            lore.add("§8» §7The game has not started yet.");
        } else {
            lore.add("§8» §7Time since start: §a" + DateUtils.secondsToString(timePassed));
            lore.add("§8» " + (finalheal <= 0 ? "§eFinal heal has passed!" : "§7Final heal is given in: §a" + DateUtils.secondsToString(finalheal)));
            lore.add("§8» " + (pvp <= 0 ? "§aPvP is enabled!" : "§7PvP enables in: §a" + DateUtils.secondsToString(pvp)));
            lore.add("§8» " + (meetup <= 0 ? "§cMeetup is NOW!" : "§7Meetup in: §a" + DateUtils.secondsToString(meetup)));
        }

        lore.add(" ");
        timerMeta.setLore(lore);
        timerItem.setItemMeta(timerMeta);
        inv.setItem(25, timerItem);
        lore.clear();
    }

    /**
     * Update the staff item in the inventory.
     */
    public void updateStaff() {
        List<String> lore = new ArrayList<String>();

        ItemStack staff = new ItemStack (Material.BOOK, 1);
        ItemMeta staffMeta = staff.getItemMeta();
        staffMeta.setDisplayName("§8» §6Staff §8«");

        lore.add(" ");
        lore.add("§8» §4Owners:");

        for (String split : getRankList(Rank.OWNER).split("-")) {
            lore.add("§8» §7" + split);
        }

        lore.add(" ");
        lore.add("§8» §cHosts:");

        for (String split : getRankList(Rank.TRIAL, Rank.HOST).split("-")) {
            lore.add("§8» §7" + split);
        }

        lore.add(" ");
        lore.add("§8» §6Staff:");

        for (String split : getRankList(Rank.STAFF).split("-")) {
            lore.add("§8» §7" + split);
        }

        lore.add(" ");
        lore.add("§8» §9Specs:");

        for (String split : getRankList(Rank.SPEC).split("-")) {
            lore.add("§8» §7" + split);
        }

        lore.add(" ");
        staffMeta.setLore(lore);
        staff.setItemMeta(staffMeta);
        inv.setItem(19, staff);
        lore.clear();
    }

    /**
     * Make a list of people having the given rank.
     *
     * @param rank The rank to get a list for.
     * @return A list of people with the rank.
     */
    private String getRankList(Rank... ranks) {
        StringBuilder list = new StringBuilder();
        List<String> peopleWithRank = new ArrayList<String>();

        int i = 1;
        int j = -1;

        for (FileConfiguration config : FileUtils.getUserFiles()) {
            for (Rank rank : ranks) {
                if (config.getString("rank", "USER").equals(rank.name())) {
                    peopleWithRank.add(config.getString("username", "null"));
                }
            }
        }
        
        Collections.sort(peopleWithRank);

        for (String loopRanks : peopleWithRank) {
            if (list.length() > 0) {
                if (peopleWithRank.size() == i) {
                    list.append(" and ");
                } else {
                    list.append(", ");
                }
            }

            if (j == 2) {
                list.append("-");
                j = 0;
            } else {
                j++;
            }

            list.append(loopRanks);
            i++;
        }
        
        return list.toString();
    }
}