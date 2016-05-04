package com.leontg77.ultrahardcore.scenario.scenarios;

import java.util.UUID;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.leontg77.ultrahardcore.Main;
import com.leontg77.ultrahardcore.managers.SpecManager;
import com.leontg77.ultrahardcore.protocol.Anonymous.DisguisePlayersAdapter;
import com.leontg77.ultrahardcore.scenario.Scenario;
import com.leontg77.ultrahardcore.scenario.scenarios.WTFIPTG.FakePlayer;

/**
 * Anonymous scenario class.
 * 
 * @author LeonTG77
 */
public class Anonymous extends Scenario {
	private final DisguisePlayersAdapter disguise;
    private final ProtocolManager manager;

	public Anonymous(Main plugin, SpecManager spec) {
		super("Anonymous", "Everyone is disguised as the same player.");
		
		this.disguise = new DisguisePlayersAdapter(plugin, new FakePlayer("Anonymous", UUID.fromString("640a5372-780b-4c2a-b7e7-8359d2f9a6a8")), spec);
		this.manager = ProtocolLibrary.getProtocolManager();
	}
	
	@Override
	public void onDisable() {
		manager.removePacketListener(disguise);
	}
	
	@Override
	public void onEnable() {
		manager.addPacketListener(disguise);
	}
}