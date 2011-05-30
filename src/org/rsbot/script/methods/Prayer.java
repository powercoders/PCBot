package org.rsbot.script.methods;

import java.util.LinkedList;
import java.util.List;

import org.rsbot.script.wrappers.RSComponent;

/**
 * Prayer related operations.
 * 
 * @author Debauchery
 */
public class Prayer extends MethodProvider {
	public interface Book {
		/**
		 * @return The component of the selected prayer
		 */
		public int getComponentIndex();

		/**
		 * @return The required level of the selected prayer
		 */
		public int getRequiredLevel();

		/**
		 * @return The settings value of the selected prayer
		 */
		public int getSettings();

	}

	public enum Curses implements Book {
		PROTECT_ITEM_CURSE(0, 0x1, 50), SAP_WARRIOR(1, 0x2, 50), SAP_RANGER(2,
				0x4, 52), SAP_MAGE(3, 0x8, 54), SAP_SPIRIT(4, 0x10, 56), BERSERKER(
				5, 0x20, 59), DEFLECT_SUMMONING(6, 0x40, 62), DEFLECT_MAGIC(7,
				0x80, 65), DEFLECT_MISSILE(8, 0x100, 68), DEFLECT_MELEE(9,
				0x200, 71), LEECH_ATTACK(10, 0x400, 74), LEECH_RANGE(11, 0x800,
				76), LEECH_MAGIC(12, 0x1000, 78), LEECH_DEFENCE(13, 0x2000, 80), LEECH_STRENGTH(
				14, 0x4000, 82), LEECH_ENERGY(15, 0x8000, 84), LEECH_SPECIAL_ATTACK(
				16, 0x10000, 86), WRATH(17, 0x20000, 89), SOUL_SPLIT(18,
				0x40000, 92), TURMOIL(19, 0x80000, 95);
		int comp, setting, level;

		private Curses(final int comp, final int setting, final int level) {
			this.comp = comp;
			this.setting = setting;
			this.level = level;
		}

		@Override
		public int getComponentIndex() {
			return comp;
		}

		@Override
		public int getRequiredLevel() {
			return level;
		}

		@Override
		public int getSettings() {
			return setting;
		}
	}

	public enum Normal implements Book {
		THICK_SKIN(0, 0x1, 1), BURST_OF_STRENGTH(1, 0x2, 4), CLARITY_OF_THOUGHT(
				2, 0x4, 7), SHARP_EYE(3, 0x40000, 8), MYSTIC_WILL(4, 0x80000, 9), ROCK_SKIN(
				5, 0x8, 10), SUPERHUMAN_STRENGTH(6, 0x10, 13), IMPROVED_REFLEXES(
				7, 0x20, 16), RAPID_RESTORE(8, 0x40, 19), RAPID_HEAL(9, 0x80,
				22), PROTECT_ITEM_REGULAR(10, 0x100, 25), HAWK_EYE(11,
				0x100000, 26), MYSTIC_LORE(12, 0x200000, 27), STEEL_SKIN(13,
				0x200, 28), ULTIMATE_STRENGTH(14, 0x400, 31), INCREDIBLE_REFLEXES(
				15, 0x800, 34), PROTECT_FROM_SUMMONING(16, 0x1000000, 35), PROTECT_FROM_MAGIC(
				17, 0x1000, 37), PROTECT_FROM_MISSILES(18, 0x2000, 40), PROTECT_FROM_MELEE(
				19, 0x4000, 43), EAGLE_EYE(20, 0x400000, 44), MYSTIC_MIGHT(21,
				0x800000, 45), RETRIBUTION(22, 0x8000, 46), REDEMPTION(23,
				0x10000, 49), SMITE(24, 0x20000, 52), CHIVALRY(25, 0x2000000,
				60), RAPID_RENEWAL(26, 0x8000000, 65), PIETY(27, 0x4000000, 70), RIGOUR(
				28, 0x10000000, 74), AUGURY(29, 0x20000000, 77);

		int comp, setting, level;

		private Normal(final int comp, final int setting, final int level) {
			this.comp = comp;
			this.setting = setting;
			this.level = level;
		}

		@Override
		public int getComponentIndex() {
			return comp;
		}

		@Override
		public int getRequiredLevel() {
			return level;
		}

		@Override
		public int getSettings() {
			return setting;
		}
	}

	public enum ProtectPrayer {
		MEELE(Prayer.Normal.PROTECT_FROM_MELEE, Prayer.Curses.DEFLECT_MELEE), MAGE(
				Prayer.Normal.PROTECT_FROM_MAGIC, Prayer.Curses.DEFLECT_MAGIC), RANGE(
				Prayer.Normal.PROTECT_FROM_MISSILES,
				Prayer.Curses.DEFLECT_MISSILE);
		private Prayer.Book normal;
		private Prayer.Book curses;

		ProtectPrayer(final Prayer.Book normal, final Prayer.Book curses) {
			this.normal = normal;
			this.curses = curses;
		}
	}

	public static final int PRAYER_INTERFACE = 271;

	public static final int INTERFACE_PRAYER_ORB = 749;

	public static final int INTERFACE_PRAYER = 271;

	Prayer(final MethodContext ctx) {
		super(ctx);
	}

	public boolean deactivateAll() {
		for (final Book pray : getSelectedPrayers()) {
			setPrayer(pray, false);
		}
		return getSelectedPrayers().length < 1;
	}

	/**
	 * Gets the remaining prayer points.
	 * 
	 * @return The number of prayer points left.
	 */
	public int getPrayerLeft() {
		return Integer.parseInt(methods.interfaces.getComponent(Game.INTERFACE_PRAYER_ORB, 4).getText());
	}

	/**
	 * Gets the percentage of prayer points left based on the players current
	 * prayer level.
	 * 
	 * @return The percentage of prayer points left.
	 */
	public int getPrayerPercentLeft() {
		return 100 * getPrayerLeft()
				/ methods.skills.getCurrentLevel(Skills.PRAYER);
	}

	/**
	 * Returns an array of all current active prayers.
	 * 
	 * @return an array of all current active prayers.
	 */
	public Book[] getSelectedPrayers() {
		final int bookSetting = isCursing() ? 1582 : 1395;
		final List<Book> activePrayers = new LinkedList<Book>();
		for (final Book prayer : isCursing() ? Curses.values()
				: Normal.values()) {
			if ((methods.settings.getSetting(bookSetting) & prayer.getSettings()) == prayer.getSettings()) {
				activePrayers.add(prayer);
			}
		}
		return activePrayers.toArray(new Book[activePrayers.size()]);
	}

	/**
	 * Checks if the player's prayer book is set to Curses.
	 * 
	 * @return <tt>true</tt> if Curses are enabled; otherwise <tt>false</tt>.
	 */
	public boolean isCursing() {
		return methods.settings.getSetting(1584) % 2 != 0;
	}

	/**
	 * Returns true if designated prayer is turned on.
	 * 
	 * @param prayer
	 *            The prayer to check.
	 * @return <tt>true</tt> if enabled; otherwise <tt>false</tt>.
	 */
	public boolean isPrayerOn(final Book prayer) {
		for (final Book pray : getSelectedPrayers()) {
			if (pray == prayer) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the quick prayer interface has been used to activate
	 * prayers.
	 * 
	 * @return <tt>true</tt> if quick prayer is on; otherwise <tt>false</tt>.
	 */
	public boolean isQuickPrayerOn() {
		return methods.interfaces.getComponent(Game.INTERFACE_PRAYER_ORB, 2).getBackgroundColor() == 782;
	}

	private boolean isQuickPrayerSet(final Book thePrayer) {
		return methods.interfaces.getComponent(INTERFACE_PRAYER, 42).getComponent(thePrayer.getComponentIndex()).getBackgroundColor() == 181;
	}

	private boolean isQuickPrayerSet(final Book... prayers) {
		for (final Book effect : prayers) {
			if (!isQuickPrayerSet(effect)) {
				return false;
			}
		}
		return true;
	}

	public boolean protectFrom(final ProtectPrayer ppray) {
		final Book pray = isCursing() ? ppray.curses : ppray.normal;
		if (methods.skills.getRealLevel(Skills.PRAYER) >= pray.getRequiredLevel()
				&& !isPrayerOn(pray)) {
			setPrayer(pray, true);
		}
		return isPrayerOn(pray);
	}

	/**
	 * Activates/deactivates a prayer via interfaces.
	 * 
	 * @param pray
	 *            The prayer to activate.
	 * @param active
	 *            <tt>true</tt> to activate; <tt>false</tt> to deactivate.
	 * @return <tt>true</tt> if the interface was clicked; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean setPrayer(final Book pray, final boolean active) {
		if (isPrayerOn(pray) == active) {
			return true;
		} else {
			if (methods.skills.getRealLevel(Skills.PRAYER) < pray.getRequiredLevel()) {
				return false;
			}
			methods.game.openTab(Game.Tab.PRAYER);
			if (methods.game.getTab() == Game.Tab.PRAYER) {
				final RSComponent component = methods.interfaces.getComponent(PRAYER_INTERFACE, 7).getComponent(pray.getComponentIndex());
				if (component.isValid()) {
					component.doAction(active ? "Activate" : "Deactivate");
				}
			}
		}
		return isPrayerOn(pray) == active;
	}

	public boolean setQuickPrayer(final boolean activate) {
		if (isQuickPrayerOn() != activate) {
			if (getPrayerLeft() > 1) {
				methods.interfaces.getComponent(749, 2).doClick();
			}
		}
		return isQuickPrayerOn() == activate;
	}

	/**
	 * Sets the character's quick prayers to the given prayers.
	 * 
	 * @param prayers
	 *            The prayers to set the quick prayers to.
	 * @return <tt>true</tt> if the quick prayers were set; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean setQuickPrayers(final Book... prayers) {
		if (!isQuickPrayerOn()) {
			methods.interfaces.getComponent(INTERFACE_PRAYER_ORB, 1).doAction("Select quick prayers");
		}
		for (final Book effect : prayers) {
			if (isQuickPrayerSet(effect)) {
				continue;
			}
			methods.interfaces.getComponent(INTERFACE_PRAYER, 42).getComponent(effect.getComponentIndex()).doAction("Select");
			sleep(random(750, 1100));
		}
		return isQuickPrayerSet(prayers)
				&& methods.interfaces.getComponent(INTERFACE_PRAYER, 42).getComponent(43).doAction("Confirm Selection");
	}
}
