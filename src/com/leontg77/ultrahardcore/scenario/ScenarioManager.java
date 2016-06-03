package com.leontg77.ultrahardcore.scenario;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.ScatterManager;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.minigames.Arena;
import com.leontg77.ultrahardcore.scenario.scenarios.*;

/**
 * Scenario management class.
 * 
 * @author LeonTG77
 */
public class ScenarioManager {
    private final Main plugin;

    public ScenarioManager(Main plugin) {
        this.plugin = plugin;
    }

    private final List<Scenario> scenarios = new ArrayList<Scenario>();

    /**
     * Get a scenario by a name.
     *
     * @param name the name.
     * @return The scenario, null if not found.
     */
    public Scenario getScenario(String name) {
        for (Scenario scen : scenarios) {
            if (scen.getName().equalsIgnoreCase(name)) {
                return scen;
            }
        }

        return null;
    }

    /**
     * Get a scenario by the class.
     *
     * @param scenarioClass The class.
     * @return The scenario, null if not found.
     */
    @SuppressWarnings("unchecked")
    public <T> T getScenario(Class<T> scenarioClass) {
        for (Scenario scen : scenarios) {
            if (scen.getClass().equals(scenarioClass)) {
                return (T) scen;
            }
        }

        return null;
    }

    /**
     * Get a list of all scenarios.
     *
     * @return the list of scenarios.
     */
    public List<Scenario> getScenarios() {
        return ImmutableList.copyOf(scenarios);
    }

    /**
     * Get a list of all enabled scenarios.
     *
     * @return the list of enabled scenarios.
     */
    public List<Scenario> getEnabledScenarios() {
        List<Scenario> list = new ArrayList<Scenario>();

        for (Scenario scen : scenarios) {
            if (scen.isEnabled()) {
                list.add(scen);
            }
        }

        return list;
    }

    /**
     * Get a list of all enabled scenarios.
     *
     * @return the list of enabled scenarios.
     */
    public List<Scenario> getDisabledScenarios() {
        List<Scenario> list = new ArrayList<Scenario>();

        for (Scenario scen : scenarios) {
            if (!scen.isEnabled()) {
                list.add(scen);
            }
        }

        return list;
    }

    /**
     * Setup all the scenario classes.
     */
    public void registerScenarios(Arena arena, Game game, Timer timer, TeamManager teams, SpecManager spec, Settings settings, FeatureManager feat, ScatterManager scatter, BoardManager board) {
        CutClean cc = new CutClean(this);

        scenarios.add(new HalfOres());
        scenarios.add(new TripleArrows());
        scenarios.add(new AchievementHunters(settings, feat));
        scenarios.add(new AchievementParanoia());
        scenarios.add(new Achievements());
        scenarios.add(new AgarIO());
        scenarios.add(new AloneTogether(teams));
//        scenarios.add(new Anonymous(spec));
        scenarios.add(new AppleFamine());
        scenarios.add(new Armageddon());
        scenarios.add(new Assassins());
        scenarios.add(new AssaultAndBattery(teams));
        scenarios.add(new Astrophobia());
        scenarios.add(new Aurophobia());
        scenarios.add(new Backpacks());
        scenarios.add(new Balance());
        scenarios.add(new BaldChicken());
        scenarios.add(new Barebones(cc));
        scenarios.add(new BarrierWorld());
        scenarios.add(new BedBombs());
        scenarios.add(new BenchBlitz());
        scenarios.add(new BestBTC());
        scenarios.add(new BestPvE());
        scenarios.add(new BetaZombies());
        scenarios.add(new BigCrack());
        scenarios.add(new BiomeParanoia(spec));
        scenarios.add(new Birds());
        scenarios.add(new BlastMining());
        scenarios.add(new Blitz());
        scenarios.add(new Blocked());
        scenarios.add(new BlockRush());
        scenarios.add(new BloodAnvils());
        scenarios.add(new BloodCycle());
        scenarios.add(new BloodDiamonds());
        scenarios.add(new BloodEnchants());
        scenarios.add(new BloodEnchantsPlus());
        scenarios.add(new BloodLapis());
        scenarios.add(new Bombers());
        scenarios.add(new Bow());
        scenarios.add(new BowFighters());
        scenarios.add(new Bowless());
        scenarios.add(new Captains(teams, spec));
        scenarios.add(new CarrotCombo());
        scenarios.add(new CatsEyes());
        scenarios.add(new Chicken());
        scenarios.add(new ChildrenLeftUnattended(teams));
        scenarios.add(new ChunkApocalypse());
        scenarios.add(new CityWorld());
        scenarios.add(new Cloud9());
        scenarios.add(new Cobblehaters());
        scenarios.add(new CobbleWorld());
        scenarios.add(new Coco());
        scenarios.add(new Compensation(arena, teams, feat));
        scenarios.add(new CraftableTP());
        scenarios.add(new Cripple());
        scenarios.add(new Cryophobia());
        scenarios.add(cc);
        scenarios.add(new DamageCycle());
        scenarios.add(new DamageDodgers());
        scenarios.add(new DeathSentence());
        scenarios.add(new Depths());
        scenarios.add(new DetailedWorld());
        scenarios.add(new Diamondless(cc));
        scenarios.add(new DoubleDates(teams));
        scenarios.add(new DoubleOrNothing(cc));
        scenarios.add(new DragonRush(spec));
        scenarios.add(new Eggs());
        scenarios.add(new EightLeggedFreaks());
        scenarios.add(new EnchantedDeath());
//      scenarios.add(new EnchantParanoia()); // TODO: Enchanted Books
        scenarios.add(new EndMeetup(scatter));
        scenarios.add(new Entropy());
        scenarios.add(new ExplosiveArrows());
        scenarios.add(new Fallout());
        scenarios.add(new Fireless());
        scenarios.add(new FlatWorld());
        scenarios.add(new Flooded());
        scenarios.add(new FlowerPower());
        scenarios.add(new FrozenInTime());
        scenarios.add(new Genie());
        scenarios.add(new GlassWorld());
        scenarios.add(new Goldless(cc));
        scenarios.add(new GoldRush());
        scenarios.add(new GoneFishing());
        scenarios.add(new GoodGame());
        scenarios.add(new GoodGamePlus());
        scenarios.add(new GoToHell(settings, feat));
        scenarios.add(new Grandpas());
        scenarios.add(new Halloween());
        scenarios.add(new HeadHunters());
        scenarios.add(new HundredHearts(feat));
        scenarios.add(new InfiniteEnchanter());
        scenarios.add(new Inventors());
        scenarios.add(new InvertedDimensions());
        scenarios.add(new InvertedParallel());
        scenarios.add(new Kings(teams));
        scenarios.add(new Krenzinator());
        scenarios.add(new LAFS(teams));
        scenarios.add(new Landmines(spec));
        scenarios.add(new Lootcrates());
        scenarios.add(new LWCM(teams));
        scenarios.add(new MeleeFun());
        scenarios.add(new Moles(this, teams, spec));
        scenarios.add(new MonstersInc(scatter));
        scenarios.add(new Mountaineering());
        scenarios.add(new MysteryTeams(spec));
        scenarios.add(new NightmareMode());
        scenarios.add(new NoFall());
        scenarios.add(new NoNameTags(board, teams));
        scenarios.add(new NoSprint());
        scenarios.add(new OneNineCooldown());
        scenarios.add(new Overcook());
        scenarios.add(new Paranoia(board, feat));
        scenarios.add(new PeriodOfResistance());
        scenarios.add(new Permakill());
        scenarios.add(new Popcorn());
        scenarios.add(new PopcornPlus());
        scenarios.add(new PotentialHearts());
        scenarios.add(new PotentialMoles(plugin, getScenario(Moles.class)));
        scenarios.add(new PotentialPermanent(settings, feat));
        scenarios.add(new PotionHealing(feat));
        scenarios.add(new Pyrophobia());
        scenarios.add(new RewardingLongshots());
        scenarios.add(new RewardingLongshotsPlus());
        scenarios.add(new Rodless());
        scenarios.add(new SecretTeams(teams));
        scenarios.add(new SelectFire());
        scenarios.add(new SelfDiagnosis(board));
        scenarios.add(new SharedHealth(teams));
        scenarios.add(new SkyClean());
        scenarios.add(new Skyhigh());
        scenarios.add(new SkyOres());
        scenarios.add(new SlaveMarket(teams));
        scenarios.add(new SlimyCrack());
        scenarios.add(new Snowday());
        scenarios.add(new SoulBrothers(teams, scatter));
        scenarios.add(new StockUp());
        scenarios.add(new Superheroes(teams));
        scenarios.add(new Swingers(teams));
        scenarios.add(new Switcheroo());
        scenarios.add(new TeamHealth(board, teams));
        scenarios.add(new Timber());
        scenarios.add(new Timebomb());
        scenarios.add(new TimeFlies());
        scenarios.add(new TommySX());
        scenarios.add(new TrainingRabbits());
        scenarios.add(new TripleOres(this));
        scenarios.add(new UberHardcore());
        scenarios.add(new UndergroundParallel());
        scenarios.add(new VeinMiner());
        scenarios.add(new VengefulSpirits(settings, feat));
        scenarios.add(new Voidscape());
        scenarios.add(new WeakestLink(getScenario(Paranoia.class)));       
        scenarios.add(new Webcage());           
//        scenarios.add(new WTFIPTG(spec, board));

        for (Scenario scen : scenarios) {
            scen.onSetup(plugin, game, timer);
        }
        
        plugin.getLogger().info("All scenarios has been setup.");
    }
}