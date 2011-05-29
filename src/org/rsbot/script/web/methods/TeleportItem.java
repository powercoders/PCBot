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

	/**
	 * Checks if you're in deep wilderness.
	 * 
	 * @return <tt>true</tt> if you cannot teleport.
	 */
	private boolean deepWilderness() {
		return methods.combat.getWildernessLevel() > 20;
	}

	/**
	 * Gets the distance to your destination from the teleport.
	 * 
	 * @param destination
	 *            The destination tile.
	 * @return The distance.
	 */
	@Override
	public double getDistance(final RSTile destination) {
		return methods.calc.distanceBetween(teleportationLocation(), destination);// TODO
																					// use
																					// web
																					// distancing.
	}

	/**
	 * Checks to see if this teleport is worth using.
	 * 
	 * @return <tt>true</tt> if we gain from the teleport.
	 */
	@Override
	public boolean isApplicable(final RSTile base, final RSTile destination) {
		return methods.calc.distanceBetween(base, teleportationLocation()) > 30
				&& methods.calc.distanceBetween(teleportationLocation(), destination) < methods.calc.distanceTo(destination);
	}

	/**
	 * Checks if you can use the teleport.
	 * 
	 * @return <tt>true</tt> if you can.
	 */
	@Override
	public boolean meetsPrerequisites() {
		return !deepWilderness()
				&& (methods.inventory.containsOneOf(itemIDs) || equips.equipmentContainsOneOf(itemIDs));
	}

	/**
	 * Preforms the action on the item.
	 * 
	 * @return <tt>true</tt> if we succeeded.
	 */
	@Override
	public boolean perform() {
		RSItem item = methods.inventory.getItem(itemIDs);
		boolean equip = false;
		if (item == null) {
			for (final RSItem itm : equips.equips()) {
				for (final int id : itemIDs) {
					if (itm.getID() == id) {
						equip = true;
						item = itm;
						break;
					}
				}
			}
		}
		if (item != null
				&& methods.game.openTab(equip ? Game.Tab.EQUIPMENT
						: Game.Tab.INVENTORY)) {
			for (final String act : action) {
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
			return methods.calc.distanceBetween(methods.players.getMyPlayer().getLocation(), teleportationLocation()) < 15;
		}
		return false;
	}
}
