package org.rsbot.script.web;

import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.MethodProvider;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSTile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The class that handles all activities.
 *
 * @author Timer
 */
public class TransportationHandler extends MethodProvider {
	private List<Teleport> teleports = new ArrayList<Teleport>();

	public TransportationHandler(final MethodContext ctx) {
		super(ctx);
	}

	public boolean canPreform(final RSTile destination) {
		Iterator<Teleport> teleportIterator = teleports.listIterator();
		while (teleportIterator.hasNext()) {
			Teleport teleport = teleportIterator.next();
			if (teleport.meetsPrerequisites() && teleport.isApplicable(methods.players.getMyPlayer().getLocation(), destination)) {
				return true;
			}
		}
		return false;
	}

	public boolean preform(final RSTile destination) {
		Teleport bestTeleport = null;
		double dist = 0.0D;
		Iterator<Teleport> teleportIterator = teleports.listIterator();
		while (teleportIterator.hasNext()) {
			Teleport teleport = teleportIterator.next();
			if (teleport.meetsPrerequisites() && teleport.isApplicable(methods.players.getMyPlayer().getLocation(), destination)) {
				if (dist == 0.0D || dist < teleport.getDistance(destination)) {
					dist = teleport.getDistance(destination);
					bestTeleport = teleport;
				}
			}
		}
		if (bestTeleport != null) {
			return bestTeleport.preform();
		}
		return false;
	}

	private boolean deepWilderness() {
		return methods.combat.getWildernessLevel() > 20;
	}

	private class Tablets {
		final Teleport VARROCK = new Teleport() {
			final int itemID = 8007;

			@Override
			public RSTile teleportationLocation() {
				return new RSTile(3212, 3428, 0);
			}

			public boolean meetsPrerequisites() {
				return !deepWilderness() && methods.inventory.contains(itemID);
			}

			public boolean isApplicable(RSTile base, RSTile destination) {
				return methods.calc.distanceBetween(base, teleportationLocation()) > 30 && methods.calc.distanceBetween(teleportationLocation(), destination) < methods.calc.distanceTo(destination);
			}

			public boolean preform() {
				if (methods.inventory.contains(itemID)) {
					if (methods.game.getCurrentTab() != Game.TAB_INVENTORY) {
						methods.game.openTab(Game.TAB_INVENTORY);
						sleep(500);
					}
					RSItem item = methods.inventory.getItem(itemID);
					if (item != null) {
						if (item.doAction("Break")) {
							final long tO = System.currentTimeMillis();
							while (System.currentTimeMillis() - tO < 10000) {
								sleep(100);
								if (methods.calc.distanceBetween(methods.players.getMyPlayer().getLocation(), teleportationLocation()) < 15) {
									break;
								}
							}
						}
					}
					return methods.calc.distanceBetween(methods.players.getMyPlayer().getLocation(), teleportationLocation()) < 15;
				}
				return false;
			}

			public double getDistance(RSTile destination) {
				return methods.calc.distanceBetween(teleportationLocation(), destination);//TODO use web distancing.
			}
		};
		final Teleport CAMELOT = new Teleport() {
			final int itemID = 8010;

			@Override
			public RSTile teleportationLocation() {
				return new RSTile(2757, 3478, 0);
			}

			public boolean meetsPrerequisites() {
				return !deepWilderness() && methods.inventory.contains(itemID);
			}

			public boolean isApplicable(RSTile base, RSTile destination) {
				return methods.calc.distanceBetween(base, teleportationLocation()) > 30 && methods.calc.distanceBetween(teleportationLocation(), destination) < methods.calc.distanceTo(destination);
			}

			public boolean preform() {
				if (methods.inventory.contains(itemID)) {
					if (methods.game.getCurrentTab() != Game.TAB_INVENTORY) {
						methods.game.openTab(Game.TAB_INVENTORY);
						sleep(500);
					}
					RSItem item = methods.inventory.getItem(itemID);
					if (item != null) {
						if (item.doAction("Break")) {
							final long tO = System.currentTimeMillis();
							while (System.currentTimeMillis() - tO < 10000) {
								sleep(100);
								if (methods.calc.distanceBetween(methods.players.getMyPlayer().getLocation(), teleportationLocation()) < 15) {
									break;
								}
							}
						}
					}
					return methods.calc.distanceBetween(methods.players.getMyPlayer().getLocation(), teleportationLocation()) < 15;
				}
				return false;
			}

			public double getDistance(RSTile destination) {
				return methods.calc.distanceBetween(teleportationLocation(), destination);//TODO use web distancing.
			}
		};
	}
}