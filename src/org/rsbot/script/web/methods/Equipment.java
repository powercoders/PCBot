package org.rsbot.script.web.methods;

import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.MethodProvider;
import org.rsbot.script.wrappers.RSItem;

/**
 * A equipment class to prevent bans.
 */
public class Equipment extends MethodProvider {
	public static RSItem[] equips = null;
	private static long lastSet = 0;

	public Equipment(final MethodContext ctx) {
		super(ctx);
	}

	/**
	 * Checks if the cache has an item.
	 *
	 * @param itemIDs The item ID.
	 * @return <tt>true</tt> if true, otherwise <tt>false</tt>.
	 */
	public boolean equipmentContainsOneOf(final int[] itemIDs) {
		for (final RSItem item : equips()) {
			for (final int id : itemIDs) {
				if (item != null && item.getID() == id) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns the cache of items.
	 *
	 * @return The array of RSItems.
	 */
	public RSItem[] equips() {
		if (equips == null) {
			equips = methods.equipment.getItems();
			Equipment.lastSet = System.currentTimeMillis();
		} else {
			if (System.currentTimeMillis() - lastSet > 600000) {
				equips = methods.equipment.getItems();
				Equipment.lastSet = System.currentTimeMillis();
			}
		}
		return equips;
	}

	/**
	 * Resets the cache to grab again next web-gen.
	 */
	public static void resetCache() {
		Equipment.lastSet = 0;
	}
}
