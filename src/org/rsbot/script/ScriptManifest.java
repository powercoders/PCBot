package org.rsbot.script;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ScriptManifest {

	String name();

	double version() default 1.0;

	String description() default "";

	String[] authors();

	/**
	 * Use tags instead. Example: tags = {Tags.MINING, Tags.MONEY_MAKING}
	 */
	@Deprecated
	String[] keywords() default {};

	Tags[] tags() default {};

	String website() default "";

	int requiresVersion() default 200;

	/**
	 * The different tags in the script selector.
	 *
	 * @author CCSki
	 */
	public enum Tags {
		COMBAT("Combat"),
		MINING("Mining"),
		SMITHING("Smithing"),
		FISHING("Fishing"),
		COOKING("Cooking"),
		FLETCHING("Fletching"),
		CRAFTING("Crafting"),
		WOODCUTTING("Woodcutting"),
		FIREMAKING("Firemaking"),
		MAGIC("Magic"),
		AGILITY("Agility"),
		THIEVING("Thieving"),
		FARMING("Farming"),
		HERBLORE("Herblore"),
		SUMMONING("Summoning"),
		HUNTER("Hunter"),
		RUNECRAFTING("Runecrafting"),
		CONSTRUCTION("Construction"),
		DUNGEONEERING("Dungeoneering"),
		MONEY_MAKING("Money-making"),
		MINIGAMES("Minigames"),
		OTHERS("Others");

		final String tag;

		Tags(final String tag) {
			this.tag = tag;
		}
		
		public String tag() {
			return tag;
		}
		
		public String toString() {
			return tag;
		}
	}
}