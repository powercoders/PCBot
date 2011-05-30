package org.rsbot.script.web.methods;

import org.rsbot.script.methods.Equipment;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Magic;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.web.Teleport;
import org.rsbot.script.wrappers.RSTile;

public class TeleportRunes extends Teleport {
	public enum Rune {
		AIR(556), EARTH, WATER, FIRE(554), BODY, MIND, CHAOS, DEATH, COSMIC, LAW(
				563), NATURE, ASTRAL, BLOOD, SOUL;
		private final int[] ids;

		Rune(final int... ids) {
			this.ids = ids;
		}

		public int[] getItemIDs() {
			return ids;
		}

		public boolean isElemental() {
			return this == AIR || this == WATER || this == FIRE
					|| this == EARTH;
		}
	}

	public final int spell;
	public final Rune[] runes;
	public final int[] count;

	public final Magic.Book book;

	public TeleportRunes(final MethodContext ctx, final int spell,
			final Magic.Book book, final RSTile teleportationLocation,
			final Rune[] runes, final int[] count) {
		super(ctx, teleportationLocation);
		this.spell = spell;
		this.runes = runes;
		this.count = count;
		this.book = book;
		if (runes.length != count.length) {
			throw new IllegalArgumentException("Invalid array sizes.");
		}
	}

	private boolean deepWilderness() {
		return methods.combat.getWildernessLevel() > 20;
	}

	@Override
	public double getDistance(final RSTile destination) {
		return methods.calc.distanceBetween(teleportationLocation(), destination);// TODO
																					// use
																					// web
																					// distancing.
	}

	private int getRuneCount(final Rune rune) {
		if (rune.isElemental()) {
			final String wepName = methods.equipment.getItem(Equipment.WEAPON) != null ? methods.equipment.getItem(Equipment.WEAPON).getName()
					: "";
			if (rune == Rune.WATER) {
				final String shieldName = methods.equipment.getItem(Equipment.SHIELD) != null ? methods.equipment.getItem(Equipment.SHIELD).getName()
						: "";
				if (shieldName != null
						&& shieldName.trim().equalsIgnoreCase("tome of frost")) {
					return 999999;
				}
			}
			if (wepName != null && wepName.toLowerCase().contains("staff")) {
				if (wepName.toLowerCase().contains(rune.name().toLowerCase())) {
					return 999999;
				}
				if (wepName.toLowerCase().contains("dust")
						&& (rune == Rune.AIR || rune == Rune.EARTH)) {
					return 999999;
				}
				if (wepName.toLowerCase().contains("lava")
						&& (rune == Rune.EARTH || rune == Rune.FIRE)) {
					return 999999;
				}
				if (wepName.toLowerCase().contains("steam")
						&& (rune == Rune.WATER || rune == Rune.FIRE)) {
					return 999999;
				}
			}
		}
		return methods.inventory.getCount(true, rune.getItemIDs());
	}

	private boolean hasRunes() {
		int i = 0;
		for (final Rune rune : runes) {
			if (getRuneCount(rune) < count[i++]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isApplicable(final RSTile base, final RSTile destination) {
		return methods.calc.distanceBetween(base, teleportationLocation()) > 30
				&& methods.calc.distanceBetween(teleportationLocation(), destination) < methods.calc.distanceTo(destination);
	}

	@Override
	public boolean meetsPrerequisites() {
		return !deepWilderness() && hasRunes()
				&& methods.magic.getCurrentSpellBook() == book;
	}

	@Override
	public boolean perform() {
		if (hasRunes() && methods.game.openTab(Game.Tab.MAGIC)) {
			if (methods.magic.castSpell(spell)) {
				final long t = System.currentTimeMillis();
				while (System.currentTimeMillis() - t < 10000) {
					if (methods.calc.distanceTo(teleportationLocation()) < 15) {
						break;
					}
					sleep(50);
				}
			}
			return methods.calc.distanceTo(teleportationLocation()) < 15;
		}
		return false;
	}
}
