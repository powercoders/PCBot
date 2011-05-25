package org.rsbot.script.web.methods;

import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.wrappers.RSComponent;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSTile;

public class TeleportJewelry extends TeleportItem {
	public TeleportJewelry(MethodContext ctx, RSTile teleportationLocation, String[] action, int[] itemIDs) {
		super(ctx, teleportationLocation, action, itemIDs);
	}

	public TeleportJewelry(MethodContext ctx, RSTile teleportationLocation, String action, int[] itemIDs) {
		super(ctx, teleportationLocation, action, itemIDs);
	}

	/**
	 * Performs the usage on the jewelery.
	 *
	 * @return <tt>true</tt> if succeeded.
	 */
	public boolean perform() {
		RSItem item = methods.inventory.getItem(itemIDs);
		boolean equip = false;
		if (item == null) {
			for (RSItem itm : methods.equipment.getItems()) {
				for (int id : itemIDs) {
					if (itm.getID() == id) {
						equip = true;
						item = itm;
						break;
					}
				}
			}
		}
		if (item != null && methods.game.openTab(equip ? Game.Tab.EQUIPMENT : Game.Tab.INVENTORY)) {
			for (String s : action) {
				if (item.doAction(s)) {
					final long tO = System.currentTimeMillis();
					while (System.currentTimeMillis() - tO < 10000) {
						sleep(100);
						if (methods.calc.distanceBetween(methods.players.getMyPlayer().getLocation(), teleportationLocation()) < 15) {
							return true;
						}
					}
				}
			}

			if (item.doAction("Rub")) {
				RSComponent comp = null;
				long tO = System.currentTimeMillis();
				while (System.currentTimeMillis() - tO < 10000) {
					sleep(100);
					if ((comp = getDialogOption(action)) != null) {
						break;
					}
				}
				if (comp != null && comp.doClick()) {
					tO = System.currentTimeMillis();
					while (System.currentTimeMillis() - tO < 10000) {
						sleep(100);
						if (methods.calc.distanceBetween(methods.players.getMyPlayer().getLocation(), teleportationLocation()) < 15) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Looks for the dialog option to select.
	 *
	 * @param opts The options.
	 * @return The RSComponent matched.
	 */
	private RSComponent getDialogOption(String... opts) {
		final RSInterface[] valid = methods.interfaces.getAll();
		for (final RSInterface iface : valid) {
			if (iface.getIndex() != 137) {
				final int len = iface.getChildCount();
				for (int i = 0; i < len; i++) {
					final RSComponent child = iface.getComponent(i);
					for (String opt : opts) {
						if (child.containsText(opt) && child.isValid() && child.getAbsoluteX() > 10 && child.getAbsoluteY() > 300) {
							return child;
						}
					}
				}
			}
		}
		return null;
	}
}
