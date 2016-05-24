package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.List;
import java.util.logging.Level;

import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.EntityKiller;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.MobRegistry;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.api.EntityChecker;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.api.EntityClassReplacer;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.api.MobOverride;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.api.NMSHandler;
import com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.api.NewSpawnsModifier;

/**
 * UberHardcore scenario class.
 * 
 * @author ghowden
 * @see https://github.com/LeonTG77/Ultra-Hardcore/blob/master/licenses/UberHardcore
 * @see https://github.com/Eluinhost/UberHardcore
 */
public class UberHardcore extends Scenario {

    public UberHardcore() {
        super("UberHardcore", "Variety of changes to mobs to make them more difficult. https://github.com/Eluinhost/UberHardcore/blob/master/README.md");
    }

    protected MobRegistry registry;

    @Override
    public void onDisable() {
        if (registry != null) {
            registry.deregisterEntities();
        }
    }
    
    @Override
    public void onEnable() {
        NMSHandler handler = new com.leontg77.ultrahardcore.scenario.scenarios.uberhardcore.nms.v1_8_R3.NMSHandler(plugin);

        EntityClassReplacer replacer = handler.getEntityClassReplacer();
        EntityChecker checker = handler.getEntityChecker();
        NewSpawnsModifier newSpawnsModifier = handler.getNewSpawnsModifier();
        EntityKiller killer = new EntityKiller(checker);
        List<MobOverride> overrides = handler.getMobOverrides();

        try {
            replacer.initialize();
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Error while enabling UberHardcore", e);
            this.disable();
            return;
        }

        registry = new MobRegistry(
                plugin,
                this,
                replacer,
                checker,
                newSpawnsModifier,
                killer,
                overrides
        );

        registry.registerEntities();
    }
}
