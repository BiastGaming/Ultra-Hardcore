package com.leontg77.ultrahardcore.scenario;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.leontg77.ultrahardcore.Arena;
import com.leontg77.ultrahardcore.Game;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.Timer;
import com.leontg77.ultrahardcore.feature.FeatureManager;
import com.leontg77.ultrahardcore.managers.BoardManager;
import com.leontg77.ultrahardcore.managers.ScatterManager;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.managers.TeamManager;
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
		scenarios.add(new AchievementHunters(plugin, game, timer, settings, feat));
		scenarios.add(new AchievementParanoia(game));
		scenarios.add(new Achievements(game));
		scenarios.add(new AgarIO(plugin, game));
		scenarios.add(new AloneTogether(plugin, game, teams));
		scenarios.add(new Anonymous(plugin, spec));
		scenarios.add(new Armageddon(plugin, game));
		scenarios.add(new Assassins(timer, game));
		scenarios.add(new AssaultAndBattery(game, teams));
		scenarios.add(new Astrophobia(plugin, timer, game));
		scenarios.add(new Aurophobia(game));
		scenarios.add(new Backpacks(plugin, game));
		scenarios.add(new Balance(game));
		scenarios.add(new BaldChicken());
		scenarios.add(new Barebones(game, cc));
		scenarios.add(new BedBombs(game));
		scenarios.add(new BenchBlitz(game));
		scenarios.add(new BestBTC(plugin, timer));
		scenarios.add(new BestPvE(plugin, timer));
		scenarios.add(new BetaZombies());
		scenarios.add(new BigCrack(plugin));
		scenarios.add(new BiomeParanoia(plugin, spec));
		scenarios.add(new Birds(timer));
		scenarios.add(new BlastMining(plugin, game));
		scenarios.add(new Blitz(plugin, timer, game));
		scenarios.add(new Blocked());
		scenarios.add(new BlockRush());
		scenarios.add(new BloodAnvils());
		scenarios.add(new BloodCycle(plugin));
		scenarios.add(new BloodDiamonds());
		scenarios.add(new BloodEnchants());
		scenarios.add(new BloodEnchantsPlus());
		scenarios.add(new BloodLapis());
		scenarios.add(new Bombers());
		scenarios.add(new Bow());
		scenarios.add(new BowFighters());
		scenarios.add(new Bowless());
		scenarios.add(new Captains(plugin, teams, spec));
		scenarios.add(new CarrotCombo());
		scenarios.add(new CatsEyes());
		scenarios.add(new Chicken());
		scenarios.add(new ChunkApocalypse(plugin));
		scenarios.add(new Cloud9(plugin, game));
		scenarios.add(new Cobblehaters());
		scenarios.add(new CobbleWorld(plugin));
		scenarios.add(new Coco(plugin));
		scenarios.add(new Compensation(arena, teams, feat));
		scenarios.add(new Cripple());
		scenarios.add(new Cryophobia(plugin, game));
		scenarios.add(cc);
		scenarios.add(new DamageCycle(plugin));
		scenarios.add(new DamageDodgers(plugin, game, timer));
		scenarios.add(new DeathSentence(plugin, game));
		scenarios.add(new Depths());
		scenarios.add(new Diamondless(cc));
		scenarios.add(new DoubleOrNothing(game, cc));
		scenarios.add(new DragonRush(spec));
		scenarios.add(new Eggs());
		scenarios.add(new EightLeggedFreaks(game));
		scenarios.add(new EnchantedDeath());
//		scenarios.add(new EnchantParanoia()); // TODO: Enchanted Books
		scenarios.add(new Entropy(plugin, game));
		scenarios.add(new Fallout(plugin, game));
		scenarios.add(new Fireless());
		scenarios.add(new Flooded(plugin, game));
		scenarios.add(new FlowerPower());
		scenarios.add(new Genie());
		scenarios.add(new GlassWorld(plugin, game));
		scenarios.add(new Goldless(cc));
		scenarios.add(new GoldRush());
		scenarios.add(new GoneFishing());
		scenarios.add(new GoodGame(game));
		scenarios.add(new GoodGamePlus(plugin, game));
		scenarios.add(new GoToHell(plugin, settings, feat));
		scenarios.add(new Halloween(plugin, game));
		scenarios.add(new HundredHearts(feat));
		scenarios.add(new InfiniteEnchanter());
		scenarios.add(new Inventors());
		scenarios.add(new InvertedDimensions(plugin));
		scenarios.add(new Kings(plugin, teams));
		scenarios.add(new Krenzinator());
		scenarios.add(new LAFS(game, teams));
		scenarios.add(new Landmines(plugin, game, spec));
		scenarios.add(new Lootcrates(plugin, game));
		scenarios.add(new LWCM(game, teams));
		scenarios.add(new MeleeFun(plugin));
		scenarios.add(new Moles(plugin, this, teams, spec));
		scenarios.add(new MonstersInc(scatter));
		scenarios.add(new Mountaineering());
		scenarios.add(new MysteryTeams(plugin, game));
		scenarios.add(new NightmareMode());
		scenarios.add(new NoFall());
		scenarios.add(new NoSprint());
		scenarios.add(new OneNineCooldown(plugin));
		scenarios.add(new Overcook(plugin, game));
		scenarios.add(new Paranoia(game, board));
		scenarios.add(new PeriodOfResistance(plugin));
		scenarios.add(new Permakill(game));
		scenarios.add(new PotentialHearts());
		scenarios.add(new PotentialMoles(plugin, getScenario(Moles.class)));
		scenarios.add(new PotentialPermanent(settings, feat));
		scenarios.add(new Pyrophobia(plugin));
		scenarios.add(new RewardingLongshots());
		scenarios.add(new RewardingLongshotsPlus());
		scenarios.add(new SelfDiagnosis(plugin, game, board));
		scenarios.add(new SharedHealth(plugin, game, teams));
		scenarios.add(new SkyClean());
		scenarios.add(new Skyhigh(plugin, game));
		scenarios.add(new SlaveMarket(plugin, teams));
		scenarios.add(new SlimyCrack(plugin));
		scenarios.add(new Snowday(plugin, game));
		scenarios.add(new Superheroes(plugin, teams));
		scenarios.add(new Swingers(game, teams));
		scenarios.add(new Switcheroo());
		scenarios.add(new TeamHealth(plugin, game, board, teams));
		scenarios.add(new Timber());
		scenarios.add(new Timebomb(plugin, game));
		scenarios.add(new TimeFlies(plugin, game));
		scenarios.add(new TommySX(game));
		scenarios.add(new TrainingRabbits(timer));
		scenarios.add(new TripleOres(this));
		scenarios.add(new VengefulSpirits(settings, feat));
		scenarios.add(new Voidscape(plugin));
		scenarios.add(new Webcage(game));
		scenarios.add(new WTFIPTG(plugin, spec, board));
		
		plugin.getLogger().info("All scenarios has been setup.");
	}
}