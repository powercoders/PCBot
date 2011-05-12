package org.rsbot.script.web.methods;

import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.web.Teleport;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSTile;

public class TeleportItem extends Teleport {
	public final int[] itemIDs;
	public final String[] action;

	public TeleportItem(final MethodContext ctx,
			final RSTile teleportationLocation, final String action,
			final int... itemIDs) {
		super(ctx, teleportationLocation);
		this.itemIDs = itemIDs;
		this.action = new String[] { action };
	}

	public TeleportItem(final MethodContext ctx,
			final RSTile teleportationLocation, final String[] action,
			final int... itemIDs) {
		super(ctx, teleportationLocation);
		this.itemIDs = itemIDs;
		this.action = action;
	}

	public boolean meetsPrerequisites() {
		return !deepWilderness() && methods.inventory.containsOneOf(itemIDs);
	}

	public boolean isApplicable(RSTile base, RSTile destination) {
		return methods.calc.distanceBetween(base, teleportationLocation()) > 30
				&& methods.calc.distanceBetween(teleportationLocation(),
						destination) < methods.calc.distanceTo(destination);
	}

	public boolean preform() {
		if (methods.inventory.containsOneOf(itemIDs)) {
			if (methods.game.getCurrentTab() != Game.TAB_INVENTORY) {
				methods.game.openTab(Game.TAB_INVENTORY);
				sleep(500);
			}
			RSItem item = methods.inventory.getItem(itemIDs);
			if (item != null) {
				for (String act : action) {
					if (item.doAction(act)) {
						final long tO = System.currentTimeMillis();
						while (System.currentTimeMillis() - tO < 10000) {
							sleep(100);
							if (methods.calc.distanceBetween(methods.players
									.getMyPlayer().getLocation(),
									teleportationLocation()) < 15) {
								break;
							}
						}
					}
				}
			}
			return methods.calc.distanceBetween(methods.players.getMyPlayer()
					.getLocation(), teleportationLocation()) < 15;
		}
		return false;
	}

	public double getDistance(RSTile destination) {
		return methods.calc.distanceBetween(teleportationLocation(),
				destination);// TODO use web distancing.
	}

	private boolean deepWilderness() {
		return methods.combat.getWildernessLevel() > 20;
	}
}
