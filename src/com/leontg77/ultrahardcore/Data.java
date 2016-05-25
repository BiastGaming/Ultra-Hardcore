package com.leontg77.ultrahardcore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.leontg77.ultrahardcore.managers.TeamManager;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.ScenarioManager;
import com.leontg77.ultrahardcore.scenario.scenarios.BestBTC;
import com.leontg77.ultrahardcore.scenario.scenarios.BestPvE;

/**
 * The data storage system class.
 * 
 * @author LeonTG77
 */
@SuppressWarnings("unused")
public class Data {
    private final Settings settings;
    private final Main plugin;

    /**
     * Data class constructor.
     *
     * @param plugin The main class.
     * @param settings The settings class.
     */
    public Data(Main plugin, Settings settings) {
        this.settings = settings;
        this.plugin = plugin;
    }

    /**
     * Store all data to the data file.
     *
     * @param teams The team manager for team saving.
     * @param scens The scenario manager for team saving.
     */
    public void store(TeamManager teams, ScenarioManager scens) {
        clearData();

        saveUUIDSet("bestbtc", scens.getScenario(BestBTC.class).getBTCList());
        saveUUIDSet("bestpve", scens.getScenario(BestPvE.class).getPvEList());

        List<String> scenarios = new ArrayList<String>();

        for (Scenario scen : scens.getEnabledScenarios()) {
            scenarios.add(scen.getName());
        }

        settings.getData().set("scenarios", scenarios);

        for (Entry<String, Set<String>> entry : teams.getSavedTeams().entrySet()) {
            settings.getData().set("teams.data." + entry.getKey(), new ArrayList<String>(entry.getValue()));
        }

        settings.saveData();
    }

    /**
     * Restore all data from the data file to where they are supposed to be.
     *
     * @param teams The team manager for team saving.
     * @param scens The scenario manager for team saving.
     */
    public void restore(TeamManager teams, ScenarioManager scens) {
        restoreUUIDSet("bestbtc", scens.getScenario(BestBTC.class).getBTCList());
        restoreUUIDSet("bestpve", scens.getScenario(BestPvE.class).getPvEList());

        try {
            for (String name : settings.getData().getConfigurationSection("teams.data").getKeys(false)) {
                teams.getSavedTeams().put(name, new HashSet<String>(settings.getData().getStringList("teams.data." + name)));
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Could not recover team data.");
            plugin.getLogger().warning(e.getClass().getName() + ": " + e.getMessage());
        }

        try {
            for (String scen : settings.getData().getStringList("scenarios")) {
                scens.getScenario(scen).enable(plugin);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Could not recover scenario data.");
            plugin.getLogger().warning(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    /**
     * Clear all data in the data file.
     */
    public void clearData() {
        for (String path : settings.getData().getKeys(false)) {
            settings.getData().set(path, null);
        }

        settings.saveData();
    }

    /**
     * Save the given list into the data file.
     *
     * @param name The name for the path.
     * @param list The list to save.
     */
    private void saveList(String name, List<String> list) {
        settings.getData().set("list." + name, list);
        settings.saveData();
    }

    /**
     * Restore the given path name into the given list.
     *
     * @param name The name for the path.
     * @param list The list to restore to.
     */
    private void restoreList(String name, List<String> list) {
        list.addAll(settings.getData().getStringList("list." + name));
    }

    /**
     * Save the given set into the data file.
     *
     * @param name The name for the path.
     * @param list The set to save.
     */
    private void saveSet(String name, Set<String> list) {
        saveList(name, new ArrayList<String>(list));
    }

    /**
     * Restore the given path name into the given set.
     *
     * @param name The name for the path.
     * @param list The set to restore to.
     */
    private void restoreSet(String name, Set<String> list) {
        restoreList(name, new ArrayList<String>(list));
    }

    /**
     * Save the given set into the data file.
     *
     * @param name The name for the path.
     * @param list The set to save.
     */
    private void saveUUIDSet(String name, Set<UUID> list) {
        Set<String> uuidToString = Sets.newHashSet();
        
        for (UUID uuid : list) {
            uuidToString.add(uuid.toString());
        }
        
        saveSet(name, uuidToString);
    }

    /**
     * Restore the given path name into the given set.
     *
     * @param name The name for the path.
     * @param list The set to restore to.
     */
    private void restoreUUIDSet(String name, Set<UUID> list) {
        Set<String> stringToUUID = Sets.newHashSet();
        restoreSet(name, stringToUUID);
        
        for (String stringUUID : stringToUUID) {
            list.add(UUID.fromString(stringUUID));
        }
    }
}