package org.rsbot.script.web.methods;

import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.web.Teleport;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSTile;

public class TeleportTab extends Teleport {
	public final int tabletID;

	public TeleportTab(final MethodContext ctx, final int tabletID, final RSTile teleportationLocation) {
		super(ctx, teleportationLocation);
		this.tabletID = tabletID;
	}

	public boolean meetsPrerequisites() {
		return !deepWilderness() && methods.inventory.contains(tabletID);
	}

	public boolean isApplicable(RSTile base, RSTile destination) {
		return methods.calc.distanceBetween(base, teleportationLocation()) > 30 && methods.calc.distanceBetween(teleportationLocation(), destination) < methods.calc.distanceTo(destination);
	}

	public boolean preform() {
		if (methods.inventory.contains(tabletID)) {
			if (methods.game.getCurrentTab() != Game.TAB_INVENTORY) {
				methods.game.openTab(Game.TAB_INVENTORY);
				sleep(500);
			}
			RSItem item = methods.inventory.getItem(tabletID);
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

	private boolean deepWilderness() {
		return methods.combat.getWildernessLevel() > 20;
	}
}
