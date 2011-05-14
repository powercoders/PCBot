package org.rsbot.script.web.methods;

import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.web.Teleport;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSTile;

public class TeleportItem extends Teleport {
	public final int[] itemIDs;
	public final String[] action;
	public final Equipment equips = new Equipment(methods);

	public TeleportItem(final MethodContext ctx, final RSTile teleportationLocation, final String action, final int... itemIDs) {
		super(ctx, teleportationLocation);
		this.itemIDs = itemIDs;
		this.action = new String[]{action};
	}

	public TeleportItem(final MethodContext ctx, final RSTile teleportationLocation, final String[] action, final int... itemIDs) {
		super(ctx, teleportationLocation);
		this.itemIDs = itemIDs;
		this.action = action;
	}

	/**
	 * Checks if you can use the teleport.
	 *
	 * @return <tt>true</tt> if you can.
	 */
	public boolean meetsPrerequisites() {
		return !deepWilderness() && (methods.inventory.containsOneOf(itemIDs) || equips.equipmentContainsOneOf(itemIDs));
	}

	/**
	 * Checks to see if this teleport is worth using.
	 *
	 * @return <tt>true</tt> if we gain from the teleport.
	 */
	public boolean isApplicable(RSTile base, RSTile destination) {
		return methods.calc.distanceBetween(base, teleportationLocation()) > 30 && methods.calc.distanceBetween(teleportationLocation(), destination) < methods.calc.distanceTo(destination);
	}

	/**
	 * Preforms the action on the item.
	 *
	 * @return <tt>true</tt> if we succeeded.
	 */
	public boolean perform() {
		RSItem item = methods.inventory.getItem(itemIDs);
		boolean equip = false;
		if (item == null) {
			for (RSItem itm : equips.equips()) {
				for (int id : itemIDs) {
					if (itm.getID() == id) {
						equip = true;
						item = itm;
						break;
					}
				}
			}
		}
		if (item != null) {
			if (methods.game.getCurrentTab() != (equip ? Game.TAB_EQUIPMENT : Game.TAB_INVENTORY)) {
				methods.game.openTab(equip ? Game.TAB_EQUIPMENT : Game.TAB_INVENTORY);
				sleep(500);
			}
			if (item != null) {
				for (String act : action) {
					if (item.doAction(act)) {
						final long tO = System.currentTimeMillis();
						while (System.currentTimeMillis() - tO < 10000) {
							sleep(100);
							if (methods.calc.distanceBetween(methods.players.getMyPlayer().getLocation(), teleportationLocation()) < 15) {
								break;
							}
						}
					}
				}
			}
			return methods.calc.distanceBetween(methods.players.getMyPlayer().getLocation(), teleportationLocation()) < 15;
		}
		return false;
	}

	/**
	 * Gets the distance to your destination from the teleport.
	 *
	 * @param destination The destination tile.
	 * @return The distance.
	 */
	public double getDistance(RSTile destination) {
		return methods.calc.distanceBetween(teleportationLocation(), destination);// TODO use web distancing.
	}

	/**
	 * Checks if you're in deep wilderness.
	 *
	 * @return <tt>true</tt> if you cannot teleport.
	 */
	private boolean deepWilderness() {
		return methods.combat.getWildernessLevel() > 20;
	}
}
