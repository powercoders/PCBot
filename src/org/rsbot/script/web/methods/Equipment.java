package org.rsbot.script.web.methods;

import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.MethodProvider;
import org.rsbot.script.wrappers.RSItem;

public class Equipment extends MethodProvider {
	public static RSItem[] equips = null;
	private static long lastSet = 0;

	public Equipment(final MethodContext ctx) {
		super(ctx);
	}

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

	public static void resetCache() {
		Equipment.lastSet = 0;
	}
}
