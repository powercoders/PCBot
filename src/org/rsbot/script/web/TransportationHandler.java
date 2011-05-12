package org.rsbot.script.web;

import org.rsbot.script.methods.Magic;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.MethodProvider;
import org.rsbot.script.web.methods.TeleportItem;
import org.rsbot.script.web.methods.TeleportRunes;
import org.rsbot.script.web.methods.TeleportRunes.Rune;
import org.rsbot.script.wrappers.RSTile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The class that handles all activities.
 *
 * @author Timer
 * @author Equinox-
 */
public class TransportationHandler extends MethodProvider {
	private List<Teleport> teleports = new ArrayList<Teleport>();

	public TransportationHandler(final MethodContext ctx) {
		super(ctx);
		Items items = new Items();
		Runes runes = new Runes();
		teleports.add(items.ARDOUGENE_TAB);
		teleports.add(items.BRIMHAVEN_TAB);
		teleports.add(items.CAMELOT_TAB);
		teleports.add(items.ECTOPHIAL);
		teleports.add(items.LLETYA_CRYSTAL);
		teleports.add(items.LUMBER_YARD_SCROLL);
		teleports.add(items.LUMBRIDGE_TAB);
		teleports.add(items.MISCELLANIA_SCROLL);
		teleports.add(items.MISCELLANIA_SCROLL);
		teleports.add(items.NARDAH_SCROLL);
		teleports.add(items.RELLEKA_TAB);
		teleports.add(items.RIMMINGTON_TAB);
		teleports.add(items.TAVERLEY_TAB);
		teleports.add(items.VARROCK_TAB);
		teleports.add(runes.CAMELOT);
		teleports.add(runes.LUMBRIDGE_HOME);
		teleports.add(runes.VARROCK);
	}

	public boolean canTeleport(final RSTile destination) {
		Iterator<Teleport> teleportIterator = teleports.listIterator();
		while (teleportIterator.hasNext()) {
			Teleport teleport = teleportIterator.next();
			if (teleport.meetsPrerequisites() && teleport.isApplicable(methods.players.getMyPlayer().getLocation(), destination)) {
				return true;
			}
		}
		return false;
	}

	public Teleport getTeleport(final RSTile destination) {
		Teleport bestTeleport = null;
		double dist = 0.0D;
		Iterator<Teleport> teleportIterator = teleports.listIterator();
		while (teleportIterator.hasNext()) {
			Teleport teleport = teleportIterator.next();
			if (teleport.meetsPrerequisites() && teleport.isApplicable(methods.players.getMyPlayer().getLocation(), destination)) {
				if (dist == 0.0D || dist > teleport.getDistance(destination)) {
					dist = teleport.getDistance(destination);
					bestTeleport = teleport;
				}
			}
		}
		return bestTeleport;
	}

	private class Items {
		public final TeleportItem ECTOPHIAL = new TeleportItem(methods, new RSTile(3658, 3522), "Empty", 4251);
		public final TeleportItem LUMBRIDGE_TAB = new TeleportItem(methods, new RSTile(3221, 3220), "Break", 8008);
		public final TeleportItem ARDOUGENE_TAB = new TeleportItem(methods, new RSTile(2661, 3303), "Break", 8011);
		public final TeleportItem CAMELOT_TAB = new TeleportItem(methods, new RSTile(2757, 3478), "Break", 8010);
		public final TeleportItem VARROCK_TAB = new TeleportItem(methods, new RSTile(3212, 3429), "Break", 8007);
		public final TeleportItem TAVERLEY_TAB = new TeleportItem(methods, new RSTile(2893, 3465), "Break", 18110);
		public final TeleportItem RIMMINGTON_TAB = new TeleportItem(methods, new RSTile(2956, 3224), "Break", 18809);
		public final TeleportItem RELLEKA_TAB = new TeleportItem(methods, new RSTile(2670, 3652), "Break", 18812);
		public final TeleportItem BRIMHAVEN_TAB = new TeleportItem(methods, new RSTile(2759, 3179), "Break", 18813);
		public final TeleportItem NARDAH_SCROLL = new TeleportItem(methods, new RSTile(3421, 2918), "Read", 19475);
		public final TeleportItem LUMBER_YARD_SCROLL = new TeleportItem(methods, new RSTile(3308, 3492), "Read", 19480);
		public final TeleportItem MISCELLANIA_SCROLL = new TeleportItem(methods, new RSTile(2513, 3858), "Read", 19477);
		public final TeleportItem LLETYA_CRYSTAL = new TeleportItem(methods, new RSTile(2328, 3172), new String[]{"Activate", "Lletya"}, 6102, 6100, 6101);
	}

	private class Runes {
		public TeleportRunes VARROCK, CAMELOT, LUMBRIDGE_HOME;

		public Runes() {
			VARROCK = new TeleportRunes(methods, Magic.SPELL_VARROCK_TELEPORT, Magic.Book.MODERN, new RSTile(3212, 3428, 0), new Rune[]{Rune.LAW, Rune.FIRE, Rune.AIR}, new int[]{1, 1, 3});
			CAMELOT = new TeleportRunes(methods, Magic.SPELL_CAMELOT_TELEPORT, Magic.Book.MODERN, new RSTile(2757, 3478, 0), new Rune[]{Rune.LAW, Rune.AIR}, new int[]{1, 5});
			LUMBRIDGE_HOME = new TeleportRunes(methods, Magic.SPELL_HOME_TELEPORT, Magic.Book.MODERN, new RSTile(3221, 3220), new Rune[]{}, new int[]{});
		}
	}
}