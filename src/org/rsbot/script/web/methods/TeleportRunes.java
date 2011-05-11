package org.rsbot.script.web.methods;

import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Magic;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.web.Teleport;
import org.rsbot.script.wrappers.RSTile;

public class TeleportRunes extends Teleport {
	public final int spell;
	public final int[] runes, count;
	public final Magic.Book book;

	public TeleportRunes(final MethodContext ctx, final int spell, final Magic.Book book, final RSTile teleportationLocation, final int[] runes, final int[] count) throws Exception {
		super(ctx, teleportationLocation);
		this.spell = spell;
		this.runes = runes;
		this.count = count;
		this.book = book;
		if (runes.length != count.length) {
			throw new Exception("Invalid array sizes.");
		}
	}

	public boolean meetsPrerequisites() {
		return !deepWilderness() && hasRunes() && methods.magic.getCurrentSpellBook() == book;
	}

	private boolean hasRunes() {
		boolean good = true;
		int i = 0;
		for (int rune : runes) {
			good = good && (methods.inventory.getCount(true, rune) >= count[i++]);
		}
		return good;
	}

	public boolean isApplicable(RSTile base, RSTile destination) {
		return methods.calc.distanceBetween(base, teleportationLocation()) > 30 && methods.calc.distanceBetween(teleportationLocation(), destination) < methods.calc.distanceTo(destination);
	}

	public boolean preform() {
		if (hasRunes()) {
			if (methods.game.getCurrentTab() != Game.TAB_MAGIC) {
				methods.game.openTab(Game.TAB_MAGIC);
				sleep(500);
			}
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

	public double getDistance(RSTile destination) {
		return methods.calc.distanceBetween(teleportationLocation(), destination);//TODO use web distancing.
	}

	private boolean deepWilderness() {
		return methods.combat.getWildernessLevel() > 20;
	}
}
