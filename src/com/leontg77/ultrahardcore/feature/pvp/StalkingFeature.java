package com.leontg77.ultrahardcore.feature.pvp;

import com.leontg77.ultrahardcore.Settings;
import com.leontg77.ultrahardcore.feature.Feature;

/**
 * Stalking feature class.
 * 
 * @author LeonTG77
 */
public class StalkingFeature extends Feature {
	private static final String PATH = "feature.stalking.rule";
	
	private final Settings settings;
	private StalkingRule rule;

	public StalkingFeature(Settings settings) {
		super("Stalking", "Change what types of stalking is allowed.");
		
		try {
			rule = StalkingRule.valueOf(settings.getConfig().getString(PATH).toUpperCase());
		} catch (Exception e) {
			rule = StalkingRule.ALLOWED_NOT_EXCESSIVE;
		}
		
		this.settings = settings;
	}
	
	/**
	 * Set the new rule for stalking.
	 * 
	 * @param rule The new rule.
	 */
	public void setStalkingRule(StalkingRule rule) {
		this.rule = rule;
		
		settings.getConfig().set(PATH, rule.name().toLowerCase());
		settings.saveConfig();
	}
	
	/**
	 * Get the current stalking rule.
	 * 
	 * @return The stalking rule.
	 */
	public StalkingRule getStalkingRule() {
		return rule;
	}
	
	/**
	 * Stalking Rule enum.
	 * <p>
	 * Contains all the possible rules for stalking.
	 * 
	 * @author LeonTG77
	 */
	public enum StalkingRule {
		ALLOWED("§aAllowed"), ALLOWED_NOT_EXCESSIVE("§aAllowed §c(Not excessive)"), UNTIL_CAUGHT("§eAllowed until you get caught"), DISALLOWED("§cDisallowed");
		
		private String message;
		
		/**
		 * Constructor for BorderShrink.
		 * 
		 * @param message The message description of the rule.
		 */
		private StalkingRule(String message) {
			this.message = message;
		}
		
		/**
		 * Get the message description of the rule.
		 * 
		 * @return The message description.
		 */
		public String getMessage() {
			return message;
		}
	}
}