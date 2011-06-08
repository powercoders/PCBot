package org.rsbot.script.methods;

import org.rsbot.script.wrappers.*;

/**
 * Magic tab and spell related operations.
 *
 * @author Timer
 */
public class Magic extends MethodProvider {

	/**
	 * Provides Magic Book(s) Information.
	 */
	public static enum Book {
		MODERN(192),
		ANCIENT(193),
		LUNAR(430),
		DUNGEONEERING(950);

		private final int id;

		Book(final int id) {
			this.id = id;
		}

		public int getInterfaceID() {
			return id;
		}
	}

	/**
	 * Provides information for magic spells.
	 *
	 * @author Dunnkers
	 */
	public static enum Spell {
		/*MODERN*/
		LUMBRIDGE_HOME_TELEPORT(Book.MODERN, 24, 0, 0),
		WIND_RUSH(Book.MODERN, 98, 1, 2.7),
		WIND_STRIKE(Book.MODERN, 25, 1, 5.5),
		CONFUSE(Book.MODERN, 26, 3, 13),
		ENCHANT_CROSSBOW_BOLT_OPAL(Book.MODERN, 27, 4, 9),
		WATER_STRIKE(Book.MODERN, 28, 5, 7.5),
		ENCHANT_LEVEL_1_JEWELLERY(Book.MODERN, 29, 7, 17.5),
		ENCHANT_CROSSBOW_BOLT_SAPPHIRE(Book.MODERN, 27, 7, 17),
		EARTH_STRIKE(Book.MODERN, 30, 9, 9.5),
		MOBILISING_ARMIES_TELEPORT(Book.MODERN, 37, 10, 19),
		WEAKEN(Book.MODERN, 31, 11, 21),
		FIRE_STRIKE(Book.MODERN, 32, 13, 11.5),
		ENCHANT_CROSSBOW_BOLT_JADE(Book.MODERN, 27, 14, 19),
		BONES_TO_BANANAS(Book.MODERN, 33, 15, 25),
		WIND_BOLT(Book.MODERN, 34, 17, 13.5),
		CURSE(Book.MODERN, 35, 19, 29),
		BIND(Book.MODERN, 36, 20, 30),
		LOW_LEVEL_ALCHEMY(Book.MODERN, 38, 21, 31),
		WATER_BOLT(Book.MODERN, 39, 23, 16.5),
		ENCHANT_CROSSBOW_BOLT_PEARL(Book.MODERN, 27, 24, 29),
		VARROCK_TELEPORT(Book.MODERN, 40, 25, 35),
		ENCHANT_LEVEL_2_JEWELLERY(Book.MODERN, 41, 27, 37),
		ENCHANT_CROSSBOW_BOLT_EMERALD(Book.MODERN, 27, 27, 37),
		EARTH_BOLT(Book.MODERN, 42, 29, 19.5),
		ENCHANT_CROSSBOW_BOLT_RED_TOPAZ(Book.MODERN, 27, 29, 33),
		LUMBRIDGE_TELEPORT(Book.MODERN, 43, 31, 41),
		TELEKINETIC_GRAB(Book.MODERN, 44, 33, 43),
		FIRE_BOLT(Book.MODERN, 45, 35, 22.5),
		FALADOR_TELEPORT(Book.MODERN, 46, 37, 47),
		CRUMBLE_UNDEAD(Book.MODERN, 47, 39, 24.5),
		TELEPORT_TO_HOUSE(Book.MODERN, 48, 40, 30),
		WIND_BLAST(Book.MODERN, 49, 41, 25.5),
		SUPERHEAT_ITEM(Book.MODERN, 50, 43, 53),
		CAMELOT_TELEPORT(Book.MODERN, 51, 45, 34),
		WATER_BLAST(Book.MODERN, 52, 47, 28.5),
		ENCHANT_LEVEL_3_JEWELLERY(Book.MODERN, 53, 49, 59),
		ENCHANT_CROSSBOW_BOLT_RUBY(Book.MODERN, 27, 49, 59),
		IBAN_BLAST(Book.MODERN, 54, 50, 61),
		SNARE(Book.MODERN, 55, 50, 60),
		MAGIC_DART(Book.MODERN, 56, 50, 61),
		ARDOUGNE_TELEPORT(Book.MODERN, 57, 51, 61),
		EARTH_BLAST(Book.MODERN, 58, 53, 31.5),
		HIGH_LEVEL_ALCHEMY(Book.MODERN, 59, 55, 65),
		CHARGE_WATER_ORB(Book.MODERN, 60, 56, 56),
		ENCHANT_LEVEL_4_JEWELLERY(Book.MODERN, 61, 57, 67),
		ENCHANT_CROSSBOW_BOLT_DIAMOND(Book.MODERN, 27, 57, 67),
		WATCHTOWER_TELEPORT(Book.MODERN, 62, 58, 68),
		FIRE_BLAST(Book.MODERN, 63, 59, 34.5),
		CHARGE_EARTH_ORB(Book.MODERN, 64, 60, 70),
		BONES_TO_PEACHES(Book.MODERN, 65, 60, 65),
		SARADOMIN_STRIKE(Book.MODERN, 66, 60, 61),
		CLAWS_OF_GUTHIX(Book.MODERN, 67, 60, 61),
		FLAMES_OF_ZAMORAK(Book.MODERN, 68, 60, 61),
		TROLLHEIM_TELEPORT(Book.MODERN, 69, 61, 68),
		WIND_WAVE(Book.MODERN, 70, 62, 36),
		CHARGE_FIRE_ORB(Book.MODERN, 71, 63, 73),
		TELEPORT_TO_APE_ATOLL(Book.MODERN, 72, 64, 74),
		WATER_WAVE(Book.MODERN, 73, 65, 37.5),
		CHARGE_AIR_ORB(Book.MODERN, 74, 66, 76),
		VULNERABILITY(Book.MODERN, 75, 66, 76),
		ENCHANT_LEVEL_5_JEWELLERY(Book.MODERN, 76, 68, 78),
		ENCHANT_CROSSBOW_BOLT_DRAGONSTONE(Book.MODERN, 27, 68, 78),
		EARTH_WAVE(Book.MODERN, 77, 70, 40),
		ENFEEBLE(Book.MODERN, 78, 73, 83),
		TELEOTHER_LUMBRIDGE(Book.MODERN, 79, 74, 84),
		FIRE_WAVE(Book.MODERN, 80, 75, 42.5),
		ENTANGLE(Book.MODERN, 81, 79, 89),
		STUN(Book.MODERN, 82, 80, 90),
		CHARGE(Book.MODERN, 83, 80, 180),
		WIND_SURGE(Book.MODERN, 84, 81, 75),
		TELEOTHER_FALADOR(Book.MODERN, 85, 82, 92),
		TELEPORT_BLOCK(Book.MODERN, 86, 85, 80),
		WATER_SURGE(Book.MODERN, 87, 85, 80),
		ENCHANT_LEVEL_6_JEWELLERY(Book.MODERN, 88, 87, 97),
		ENCHANT_CROSSBOW_BOLT_ONYX(Book.MODERN, 27, 87, 97),
		EARTH_SURGE(Book.MODERN, 90, 90, 85),
		TELEOTHER_CAMELOT(Book.MODERN, 89, 90, 100),
		FIRE_SURGE(Book.MODERN, 91, 95, 90),

		/*LUNAR*/
		//TODO Add the Lunar spells

		/*ANCIENT*/
		//TODO Add the Ancient spells
		;

		private final Book book;
		private final int component;
		private final int level;
		private final double experience;

		Spell(Book book, int component, int level, double experience) {
			this.book = book;
			this.component = component;
			this.level = level;
			this.experience = experience;
		}

		/**
		 * Gets the name of this spell.
		 * <p/>
		 * <i>Example: "Lumbridge Home Teleport"</i>
		 *
		 * @return The name of this spell.
		 */
		public String getName() {
			StringBuilder name = new StringBuilder(name().toLowerCase().replaceAll("_ ", " "));
			int i = 0;
			do {
				name.replace(i, i + 1, name.substring(i, i + 1).toUpperCase());
				i = name.indexOf(" ", i) + 1;
			} while (i > 0 && i < name.length());
			return name.toString();
		}

		public Book getBook() {
			return book;
		}

		public int getComponent() {
			return component;
		}

		public int getLevel() {
			return level;
		}

		public double getExperience() {
			return experience;
		}
	}

	// Buttons
	public static final int INTERFACE_DEFENSIVE_STANCE = 2;
	public static final int INTERFACE_SHOW_COMBAT_SPELLS = 7;
	public static final int INTERFACE_SHOW_TELEPORT_SPELLS = 9;
	public static final int INTERFACE_SHOW_MISC_SPELLS = 11;
	public static final int INTERFACE_SHOW_SKILL_SPELLS = 13;
	public static final int INTERFACE_SORT_BY_LEVEL = 15;
	public static final int INTERFACE_SORT_BY_COMBAT = 16;
	public static final int INTERFACE_SORT_BY_TELEPORTS = 17;

	// Normal spells
	public static final int SPELL_HOME_TELEPORT = 24;
	public static final int SPELL_WIND_STRIKE = 25;
	public static final int SPELL_CONFUSE = 26;
	public static final int SPELL_ENCHANT_CROSSBOW_BOLT = 27;
	public static final int SPELL_WATER_STRIKE = 28;
	public static final int SPELL_LVL1_ENCHANT = 29;
	public static final int SPELL_EARTH_STRIKE = 30;
	public static final int SPELL_WEAKEN = 31;
	public static final int SPELL_FIRE_STRIKE = 32;
	public static final int SPELL_BONES_TO_BANANAS = 33;
	public static final int SPELL_WIND_BOLT = 34;
	public static final int SPELL_CURSE = 35;
	public static final int SPELL_BIND = 36;
	public static final int SPELL_MOBILISING_ARMIES_TELEPORT = 37;
	public static final int SPELL_LOW_LEVEL_ALCHEMY = 38;
	public static final int SPELL_WATER_BOLT = 39;
	public static final int SPELL_VARROCK_TELEPORT = 40;
	public static final int SPELL_LVL2_ENCHANT = 41;
	public static final int SPELL_EARTH_BOLT = 42;
	public static final int SPELL_LUMBRIDGE_TELEPORT = 43;
	public static final int SPELL_TELEKINETIC_GRAB = 44;
	public static final int SPELL_FIRE_BOLT = 45;
	public static final int SPELL_FALADOR_TELEPORT = 46;
	public static final int SPELL_CRUMBLE_UNDEAD = 47;
	public static final int SPELL_TELEPORT_TO_HOUSE = 48;
	public static final int SPELL_WIND_BLAST = 49;
	public static final int SPELL_SUPERHEAT_ITEM = 50;
	public static final int SPELL_CAMELOT_TELEPORT = 51;
	public static final int SPELL_WATER_BLAST = 52;
	public static final int SPELL_LVL3_ENCHANT = 53;
	public static final int SPELL_IBAN_BLAST = 54;
	public static final int SPELL_SNARE = 55;
	public static final int SPELL_MAGIC_DART = 56;
	public static final int SPELL_ARDOUGNE_TELEPORT = 57;
	public static final int SPELL_EARTH_BLAST = 58;
	public static final int SPELL_HIGH_LEVEL_ALCHEMY = 59;
	public static final int SPELL_CHARGE_WATER_ORB = 60;
	public static final int SPELL_LVL4_ENCHANT = 61;
	public static final int SPELL_WATCHTOWER_TELEPORT = 62;
	public static final int SPELL_FIRE_BLAST = 63;
	public static final int SPELL_CHARGE_EARTH_ORB = 64;
	public static final int SPELL_BONES_TO_PEACHES = 65;
	public static final int SPELL_SARADOMIN_STRIKE = 66;
	public static final int SPELL_CLAWS_OF_GUTHIX = 67;
	public static final int SPELL_FLAMES_OF_ZAMORAK = 68;
	public static final int SPELL_TROLLHEIM_TELEPORT = 69;
	public static final int SPELL_WIND_WAVE = 70;
	public static final int SPELL_CHARGE_FIRE_ORB = 71;
	public static final int SPELL_APE_ATOL_TELEPORT = 72;
	public static final int SPELL_WATER_WAVE = 73;
	public static final int SPELL_CHARGE_AIR_ORB = 74;
	public static final int SPELL_VULNERABILITY = 75;
	public static final int SPELL_LVL5_ENCHANT = 76;
	public static final int SPELL_EARTH_WAVE = 77;
	public static final int SPELL_ENFEEBLE = 78;
	public static final int SPELL_TELEOTHER_LUMBRIDGE = 79;
	public static final int SPELL_FIRE_WAVE = 80;
	public static final int SPELL_ENTANGLE = 81;
	public static final int SPELL_STUN = 82;
	public static final int SPELL_CHARGE = 83;
	public static final int SPELL_WIND_SURGE = 84;
	public static final int SPELL_TELEOTHER_FALADOR = 85;
	public static final int SPELL_TELEPORT_BLOCK = 86;
	public static final int SPELL_WATER_SURGE = 87;
	public static final int SPELL_LVL6_ENCHANT = 88;
	public static final int SPELL_TELEOTHER_CAMELOT = 89;
	public static final int SPELL_EARTH_SURGE = 90;
	public static final int SPELL_FIRE_SURGE = 91;

	// Ancient spells
	public static final int SPELL_ICE_RUSH = 20;
	public static final int SPELL_ICE_BLITZ = 21;
	public static final int SPELL_ICE_BURST = 22;
	public static final int SPELL_ICE_BARRAGE = 23;
	public static final int SPELL_BLOOD_RUSH = 24;
	public static final int SPELL_BLOOD_BLITZ = 25;
	public static final int SPELL_BLOOD_BURST = 26;
	public static final int SPELL_BLOOD_BARRAGE = 27;
	public static final int SPELL_SMOKE_RUSH = 28;
	public static final int SPELL_SMOKE_BLITZ = 29;
	public static final int SPELL_SMOKE_BURST = 30;
	public static final int SPELL_SMOKE_BARRAGE = 31;
	public static final int SPELL_SHADOW_RUSH = 32;
	public static final int SPELL_SHADOW_BLITZ = 33;
	public static final int SPELL_SHADOW_BURST = 34;
	public static final int SPELL_SHADOW_BARRAGE = 35;
	public static final int SPELL_MIASMIC_RUSH = 36;
	public static final int SPELL_MIASMIC_BLITZ = 37;
	public static final int SPELL_MIASMIC_BURST = 38;
	public static final int SPELL_MIASMIC_BARRAGE = 39;

	public static final int SPELL_PADDEWWA_TELEPORT = 40;
	public static final int SPELL_SENNTISTEN_TELEPORT = 41;
	public static final int SPELL_KHARYRLL_TELEPRT = 42;
	public static final int SPELL_LASSER_TELEPORT = 43;
	public static final int SPELL_DAREEYAK_TELEPORT = 44;
	public static final int SPELL_CARRALLANGER_TELEPORT = 45;
	public static final int SPELL_ANNAKARL_TELEPORT = 46;
	public static final int SPELL_GHORROCK_TELEPORT = 47;
	public static final int SPELL_ANCIENT_HOME_TELEPORT = 48;

	// Lunar spells
	public static final int SPELL_BARBARIAN_TELEPORT = 22;
	public static final int SPELL_CURE_OTHER = 23;
	public static final int SPELL_FERTILE_SOIL = 24;
	public static final int SPELL_CURE_GROUP = 25;
	public static final int SPELL_NPC_CONTACT = 26;
	public static final int SPELL_ENERGY_TRANSFER = 27;
	public static final int SPELL_MONSTERS_EXAMINE = 28;
	public static final int SPELL_HUMIDIFY = 29;
	public static final int SPELL_HUNTER_KIT = 30;
	public static final int SPELL_STAT_SPY = 31;
	public static final int SPELL_DREAM = 32;
	public static final int SPELL_PLANK_MAKE = 33;
	public static final int SPELL_SPELLBOOK_SWAP = 34;
	public static final int SPELL_MAGIC_IMBUE = 35;
	public static final int SPELL_VENGEANCE = 36;
	public static final int SPELL_BAKE_PIE = 37;
	public static final int SPELL_LUNAR_HOME_TELEPORT = 38;
	public static final int SPELL_FISHING_GUILD_TELEPORT = 39;
	public static final int SPELL_KHAZARD_TELEPORT = 40;
	public static final int SPELL_VENGEANCE_OTHER = 41;
	public static final int SPELL_MOONCLAN_TELEPORT = 42;
	public static final int SPELL_CATHERBY_TELEPORT = 43;
	public static final int SPELL_STRING_JEWELLERY = 44;
	public static final int SPELL_CURE_ME = 45;
	public static final int SPELL_WATERBIRTH_TELEPORT = 46;
	public static final int SPELL_SUPERGLASS_MAKE = 47;
	public static final int SPELL_BOOST_POTION_SHARE = 48;
	public static final int SPELL_STAT_RESTORE_POT_SHARE = 49;
	public static final int SPELL_ICE_PLATEAU_TELEPORT = 50;
	public static final int SPELL_HEAL_OTHER = 51;
	public static final int SPELL_HEAL_GROUP = 52;
	public static final int SPELL_OURANIA_TELEPORT = 53;
	public static final int SPELL_CURE_PLANT = 54;
	public static final int SPELL_TELE_GROUP_MOONCLAN = 55;
	public static final int SPELL_TELE_GROUP_WATERBIRTH = 56;
	public static final int SPELL_TELE_GROUP_BARBARIAN = 57;
	public static final int SPELL_TELE_GROUP_KHAZARD = 58;
	public static final int SPELL_TELE_GROUP_FISHING_GUILD = 59;
	public static final int SPELL_TELE_GROUP_CATHERBY = 60;
	public static final int SPELL_TELE_GROUP_ICE_PLATEAU = 61;
	public static final int SPELL_SOUTH_FALADOR_TELEPORT = 66;
	public static final int SPELL_REPAIR_RUNE_POUCH = 67;
	public static final int SPELL_NORTH_ARDOUGNE_TELEPORT = 68;
	public static final int SPELL_REMOTE_FARM = 69;
	public static final int SPELL_SPIRITUALISE_FOOD = 70;
	public static final int SPELL_MAKE_LEATHER = 71;
	public static final int SPELL_DISRUPTION_SHIELD = 72;
	public static final int SPELL_VENGEANCE_GROUP = 73;

	//Dungeoneering spells
	public static final int SPELL_HOME_TELEPORT_DUNGEONEERING = 24;
	public static final int SPELL_WIND_STRIKE_DUNGEONEERING = 25;
	public static final int SPELL_CONFUSE_DUNGEONEERING = 26;
	public static final int SPELL_WATER_STRIKE_DUNGEONEERING = 27;
	public static final int SPELL_EARTH_STRIKE_DUNGEONEERING = 28;
	public static final int SPELL_WEAKEN_DUNGEONEERING = 29;
	public static final int SPELL_FIRE_STRIKE_DUNGEONEERING = 30;
	public static final int SPELL_BONES_TO_BANANAS_DUNGEONEERING = 31;
	public static final int SPELL_WIND_BOLT_DUNGEONEERING = 32;
	public static final int SPELL_CURSE_DUNGEONEERING = 33;
	public static final int SPELL_BIND_DUNGEONEERING = 34;
	public static final int SPELL_LOW_LEVEL_ALCHEMY_DUNGEONEERING = 35;
	public static final int SPELL_WATER_BOLT_DUNGEONEERING = 36;
	public static final int SPELL_EARTH_BOLT_DUNGEONEERING = 37;
	public static final int SPELL_CREATE_GATESTONE_DUNGEONEERING = 38;
	public static final int SPELL_GATESTONE_TELEPORT_DUNGEONEERING = 39;
	public static final int SPELL_GROUP_GATESTONE_TELEPORT_DUNGEONEERING = 40;
	public static final int SPELL_FIRE_BOLT_DUNGEONEERING = 41;
	public static final int SPELL_WIND_BLAST_DUNGEONEERING = 42;
	public static final int SPELL_WATER_BLAST_DUNGEONEERING = 43;
	public static final int SPELL_SNARE_DUNGEONEERING = 44;
	public static final int SPELL_EARTH_BLAST_DUNGEONEERING = 45;
	public static final int SPELL_HIGH_LEVEL_ALCHEMY_DUNGEONEERING = 46;
	public static final int SPELL_FIRE_BLAST_DUNGEONEERING = 47;
	public static final int SPELL_WIND_WAVE_DUNGEONEERING = 48;
	public static final int SPELL_WATER_WAVE_DUNGEONEERING = 49;
	public static final int SPELL_VULNERABILITY_DUNGEONEERING = 50;
	public static final int SPELL_MONSTER_EXAMINE_DUNGEONEERING = 51;
	public static final int SPELL_CURE_OTHER_DUNGEONEERING = 52;
	public static final int SPELL_HUMIDIFY_DUNGEONEERING = 53;
	public static final int SPELL_EARTH_WAVE_DUNGEONEERING = 54;
	public static final int SPELL_CURE_ME_DUNGEONEERING = 55;
	public static final int SPELL_ENFEEBLE_DUNGEONEERING = 56;
	public static final int SPELL_CURE_GROUP_DUNGEONEERING = 57;
	public static final int SPELL_FIRE_WAVE_DUNGEONEERING = 58;
	public static final int SPELL_ENTANGLE_DUNGEONEERING = 59;
	public static final int SPELL_STUN_DUNGEONEERING = 60;
	public static final int SPELL_WINDSURGE_DUNGEONEERING = 61;
	public static final int SPELL_WATER_SURGE_DUNGEONEERING = 62;
	public static final int SPELL_EARTH_SURGE_DUNGEONEERING = 63;
	public static final int SPELL_VENGENCE_OTHER_DUNGEONEERING = 64;
	public static final int SPELL_VENGENCE_DUNGEONEERING = 65;
	public static final int SPELL_VENGENCE_GROUP_DUNGEONEERING = 66;
	public static final int SPELL_FIRE_SURGE_DUNGEONEERING = 67;

	Magic(final MethodContext ctx) {
		super(ctx);
	}

	/**
	 * Checks whether or not a spell is selected.
	 *
	 * @return <tt>true</tt> if a spell is selected; otherwise <tt>false</tt>.
	 */
	public boolean isSpellSelected() {
		return methods.client.isSpellSelected();
	}

	/**
	 * Determines whether a spell is currently set to autocast.
	 *
	 * @return <tt>true</tt> if autocasting; otherwise <tt>false</tt>.
	 */
	public boolean isAutoCasting() {
		return methods.combat.getFightMode() == 4;
	}

	/**
	 * Clicks a specified spell, opens magic tab if not open and uses interface
	 * of the spell to click it, so it works if the spells are layout in any
	 * sway.
	 *
	 * @param spell The spell to cast.
	 * @return <tt>true</tt> if the spell was clicked; otherwise <tt>false</tt>.
	 */
	public boolean castSpell(final int spell) {
		if (methods.game.openTab(Game.Tab.MAGIC)) {
			final RSInterface inter = getInterface();
			if (inter != null) {
				final RSComponent comp = inter.getComponent(spell);
				return comp != null && comp.interact("Cast");
			}
		}
		return false;
	}

	/**
	 * Hovers a specified spell, opens magic tab if not open and uses interface
	 * of the spell to hover it, so it works if the spells are layout in any
	 * sway.
	 *
	 * @param spell The spell to hover.
	 * @return <tt>true</tt> if the spell was clicked; otherwise <tt>false</tt>.
	 */
	public boolean hoverSpell(final int spell) {
		if (methods.game.openTab(Game.Tab.MAGIC)) {
			final RSInterface inter = getInterface();
			if (inter != null) {
				final RSComponent comp = inter.getComponent(spell);
				return comp != null && comp.doHover();
			}
		}
		return false;
	}

	/**
	 * Auto-casts a spell via the magic tab.
	 *
	 * @param spell The spell to auto-cast.
	 * @return <tt>true</tt> if the "Auto-cast" interface option was clicked;
	 *         otherwise <tt>false</tt>.
	 */
	public boolean autoCastSpell(final int spell) {
		if (methods.settings.getSetting(43) != 4 && methods.game.openTab(Game.Tab.MAGIC)) {
			final RSInterface inter = getInterface();
			if (inter != null) {
				final RSComponent comp = inter.getComponent(spell);
				return comp != null && comp.interact("Autocast");
			}
		}
		return false;
	}

	/**
	 * Gets the open magic book interface.
	 *
	 * @return The current magic RSInterface.
	 */
	public RSInterface getInterface() {
		methods.game.openTab(Game.Tab.MAGIC);
		for (Book book : Book.values()) {
			RSInterface inter = methods.interfaces.get(book.getInterfaceID());
			if (inter.isValid()) {
				return inter;
			}
		}
		return null;
	}

	/**
	 * Gets the current spell book.
	 *
	 * @return The Book enum of your current spell book.
	 */
	public Book getCurrentSpellBook() {
		for (Book book : Book.values()) {
			if (methods.interfaces.get(book.getInterfaceID()).isValid()) {
				return book;
			}
		}
		return null;
	}

	/**
	 * Casts a spell on a Player/NPC/Object/Ground Item.
	 *
	 * @param entity A Character or Animable.
	 * @param spell  The spell to cast.
	 * @return <tt>true</tt> if casted; otherwise <tt>false</tt>.
	 */
	public boolean castSpellOn(final Object entity, final int spell) {
		if (isSpellSelected() || entity == null) {
			return false;
		}
		if (castSpell(spell)) {
			if (entity instanceof RSCharacter) {
				return ((RSCharacter) entity).interact("Cast");
			} else if (entity instanceof RSObject) {
				return ((RSObject) entity).interact("Cast");
			} else if (entity instanceof RSGroundItem) {
				return ((RSGroundItem) entity).interact("Cast");
			}
		}
		return false;
	}

	/**
	 * Checks if we are on the correct spell book and if we have the required
	 * level.
	 *
	 * @param spell The spell to check.
	 * @return <tt>True</tt> if we can cast this spell, otherwise <tt>False</
	 */
	public boolean canCastSpell(Spell spell) {
		if (!getCurrentSpellBook().equals(spell.getBook())) {
			return false;
		}
		if (!(methods.skills.getRealLevel(Skills.MAGIC) >= spell.level)) {
			return false;
		}
		return true;
	}

	/**
	 * Clicks a specified spell, opens magic tab if not open and uses interface
	 * of the spell to click it, so it works if the spells are layout in any
	 * sway.
	 *
	 * @param spell The spell to cast.
	 * @return <tt>true</tt> if the spell was clicked; otherwise <tt>false</tt>.
	 */
	public boolean castSpell(final Spell spell) {
		return castSpell(spell.getComponent());
	}

	/**
	 * Hovers a specified spell, opens magic tab if not open and uses interface
	 * of the spell to hover it, so it works if the spells are layout in any
	 * sway.
	 *
	 * @param spell The spell to hover.
	 * @return <tt>true</tt> if the spell was clicked; otherwise <tt>false</tt>.
	 */
	public boolean hoverSpell(final Spell spell) {
		return hoverSpell(spell.getComponent());
	}

	/**
	 * Auto-casts a spell via the magic tab.
	 *
	 * @param spell The spell to auto-cast.
	 * @return <tt>true</tt> if the "Auto-cast" interface option was clicked;
	 *         otherwise <tt>false</tt>.
	 */
	public boolean autoCastSpell(final Spell spell) {
		return autoCastSpell(spell.getComponent());
	}

	/**
	 * Casts a spell on a Player/NPC/Object/Ground Item.
	 *
	 * @param entity A Character or Animable.
	 * @param spell  The spell to cast.
	 * @return <tt>true</tt> if casted; otherwise <tt>false</tt>.
	 */
	public boolean castSpellOn(final Object entity, final Spell spell) {
		return castSpellOn(entity, spell.getComponent());
	}
}
