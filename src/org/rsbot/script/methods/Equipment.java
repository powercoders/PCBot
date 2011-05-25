package org.rsbot.script.methods;

import org.rsbot.script.wrappers.RSComponent;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSItem;

/**
 * Equipment related operations.
 */
public class Equipment extends MethodProvider {

	public static final int ITEM_SLOTS = 11;
	public static final int INTERFACE_EQUIPMENT = 387;
	public static final int HELMET = 8;
	public static final int CAPE = 11;
	public static final int NECK = 14;
	public static final int WEAPON = 17;
	public static final int BODY = 20;
	public static final int SHIELD = 23;
	public static final int LEGS = 26;
	public static final int HANDS = 29;
	public static final int FEET = 32;
	public static final int RING = 35;
	public static final int AMMO = 38;

	Equipment(final MethodContext ctx) {
		super(ctx);
	}

	/**
	 * Gets the equipment interface.
	 *
	 * @return the equipment interface
	 */
	public RSInterface getInterface() {
		if (methods.game.getTab() != Game.Tab.EQUIPMENT) {
			if (methods.bank.isOpen()) {
				methods.bank.close();
			}
			methods.game.openTab(Game.Tab.EQUIPMENT);
			sleep(random(900, 1500));
		}
		return methods.interfaces.get(INTERFACE_EQUIPMENT);
	}

	/**
	 * Gets the equipment array.
	 *
	 * @return An array containing all equipped items
	 */
	public RSItem[] getItems() {
		final RSComponent[] equip = getInterface().getComponents();
		final RSItem[] items = new RSItem[ITEM_SLOTS];
		for (int i = 0; i < items.length; i++) {
			items[i] = new RSItem(methods, equip[i * 3 + 8]);
		}
		return items;
	}

	/**
	 * Gets the cached equipment array (i.e. does not open the interface).
	 *
	 * @return The items equipped as seen when the equipment tab was last
	 *         opened.
	 */
	public RSItem[] getCachedItems() {
		final RSInterface equipment = methods.interfaces.get(INTERFACE_EQUIPMENT);
		final RSComponent[] components = equipment.getComponents();
		final RSItem[] items = new RSItem[ITEM_SLOTS];
		for (int i = 0; i < items.length; i++) {
			items[i] = new RSItem(methods, components[i * 3 + 8]);
		}
		return items;
	}

	/**
	 * Gets the equipment item at a given index.
	 *
	 * @param index The item index.
	 * @return The equipped item.
	 */
	public RSItem getItem(final int index) {
		return new RSItem(methods, getInterface().getComponents()[index]);
	}

	/**
	 * Returns the number of items equipped excluding stack sizes.
	 *
	 * @return Amount of items currently equipped.
	 */
	public int getCount() {
		return ITEM_SLOTS - getCount(-1);
	}

	/**
	 * Returns the number of items matching a given ID equipped excluding stack
	 * sizes.
	 *
	 * @param itemID The item ID to count. Same as the equipment/item id in the
	 *               inventory.
	 * @return Amount of specified item currently equipped.
	 * @see #getItems()
	 */
	public int getCount(final int itemID) {
		int count = 0;
		for (final RSItem item : getItems()) {
			if (item.getID() == itemID) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Checks whether the player has all of the given items equipped.
	 *
	 * @param items The item ID to check for. Same as the equipment/item id in the
	 *              inventory.
	 * @return <tt>true</tt> if specified item is currently equipped; otherwise
	 *         <tt>false</tt>.
	 * @see #getItems()
	 */
	public boolean containsAll(final int... items) {
		final RSItem[] equips = getItems();
		int count = 0;
		for (final int item : items) {
			for (final RSItem equip : equips) {
				if (equip.getID() == item) {
					count++;
					break;
				}
			}
		}
		return count == items.length;
	}

	/**
	 * Checks if the player has one (or more) of the given items equipped.
	 *
	 * @param items The IDs of items to check for.
	 * @return <tt>true</tt> if the player has one (or more) of the given items
	 *         equipped; otherwise <tt>false</tt>.
	 */
	public boolean containsOneOf(final int... items) {
		for (final RSItem item : getItems()) {
			for (final int id : items) {
				if (item.getID() == id) {
					return true;
				}
			}
		}
		return false;
	}

}
