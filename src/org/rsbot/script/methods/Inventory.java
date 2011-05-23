package org.rsbot.script.methods;

import org.rsbot.script.wrappers.RSComponent;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

import java.awt.*;
import java.util.LinkedList;

/**
 * Inventory related operations.
 *
 * @author Jacmob
 * @author kiko
 */
public class Inventory extends MethodProvider {
	public static final int INTERFACE_INVENTORY = 149;
	public static final int INTERFACE_INVENTORY_PRICE_CHECK = 204;
	public static final int INTERFACE_INVENTORY_SHOP = 621;
	public static final int INTERFACE_INVENTORY_EQUIPMENT_BONUSES = 670;
	public static final int INTERFACE_INVENTORY_BANK = 763;
	public static final int INTERFACE_INVENTORY_DUNGEONEERING_SHOP = 957;

	Inventory(final MethodContext ctx) {
		super(ctx);
	}

	/**
	 * Left-clicks on the selected item.
	 *
	 * @return <tt>true</tt> if item was selected, </tt>false</tt> if not.
	 * @see #clickSelectedItem(boolean)
	 */
	public boolean clickSelectedItem() {
		return clickSelectedItem(true);
	}

	/**
	 * Clicks selected inventory item, if it's selected.
	 *
	 * @param leftClick <tt>true</tt> for left button click, <tt>false</tt> for right
	 *                  button.
	 * @return <tt>true</tt> if item was selected, <tt>false</tt> if not.
	 */
	public boolean clickSelectedItem(final boolean leftClick) {
		final RSItem item = getSelectedItem();
		return item != null && item.doClick(true);
	}

	/**
	 * Checks whether or not your inventory contains the provided item ID.
	 *
	 * @param itemID The item(s) you wish to evaluate.
	 * @return <tt>true</tt> if your inventory contains an item with the ID
	 *         provided; otherwise <tt>false</tt>.
	 * @see #containsOneOf(int...)
	 * @see #containsAll(int...)
	 */
	public boolean contains(final int itemID) {
		return getItem(itemID) != null;
	}

	/**
	 * Checks whether or not your inventory contains the provided item name.
	 *
	 * @param name The item(s) you wish to evaluate.
	 * @return <tt>true</tt> if your inventory contains an item with the name
	 *         provided; otherwise <tt>false</tt>.
	 */
	public boolean contains(final String name) {
		return getItem(name) != null;
	}

	/**
	 * Checks whether or not your inventory contains all of the provided item
	 * IDs.
	 *
	 * @param itemID The item(s) you wish to evaluate.
	 * @return <tt>true</tt> if your inventory contains at least one of all of
	 *         the item IDs provided; otherwise <tt>false</tt>.
	 * @see #containsOneOf(int...)
	 */
	public boolean containsAll(final int... itemID) {
		for (final int i : itemID) {
			if (getItem(i) == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether or not your inventory contains at least one of the
	 * provided item IDs.
	 *
	 * @param itemID The item ID to check for.
	 * @return <tt>true</tt> if inventory contains one of the specified items;
	 *         otherwise <tt>false</tt>.
	 * @see #containsAll(int...)
	 */
	public boolean containsOneOf(final int... itemID) {
		for (final RSItem item : getItems()) {
			for (final int i : itemID) {
				if (item.getID() == i) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Destroys any inventory items with the given ID.
	 *
	 * @param itemID The ID of items to destroy.
	 * @return <tt>true</tt> if the items were destroyed; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean destroyItem(final int itemID) {
		RSItem item = getItem(itemID);
		if (item == null || !item.hasAction("Destroy")) {
			return false;
		}
		while ((item = getItem(itemID)) != null) {
			if (methods.interfaces.get(94).isValid()) {
				methods.interfaces.getComponent(94, 3).doClick();
			} else {
				item.doAction("Destroy");
			}
			sleep(random(700, 1100));
		}
		return true;
	}

	/**
	 * Drops all items except those with one of the provided IDs.
	 * This method drops items vertically going down the inventory.
	 *
	 * @param ids The item IDs to drop.
	 * @return <tt>true</tt> if items were dropped from the inventory;
	 *         otherwise <tt>false</tt>.
	 * @see #dropAllExcept(boolean, int...)
	 */
	public boolean dropAllExcept(final int... ids) {
		return dropAllExcept(false, ids);
	}

	/**
	 * Drops all items except those with one of the provided IDs.
	 *
	 * @param leftToRight <tt>true</tt> to drop items from left to right.
	 * @param ids         The item IDs to drop
	 * @return <tt>true</tt> if items were dropped from the inventory;
	 *         otherwise <tt>false</tt>.
	 */
	public boolean dropAllExcept(final boolean leftToRight, final int... ids) {
		final int startCount = getCount();
		final RSTile startLocation = methods.players.getMyPlayer().getLocation();
		boolean found_droppable = true;
		while (found_droppable && getCountExcept(ids) != 0) {
			if (methods.calc.distanceTo(startLocation) > 100) {
				break;
			}
			found_droppable = false;

			for (int j = 0; j < 28; j++) {
				final int c = leftToRight ? j % 4 : j / 7;
				final int r = leftToRight ? j / 4 : j % 7;
				final RSItem curItem = getItems()[c + r * 4];
				if (curItem != null) {
					int id = curItem.getID();
					if (id != -1) {
						boolean isInItems = false;
						for (final int i : ids) {
							if (i == id) {
								isInItems = true;
								break;
							}
						}
						if (!isInItems) {
							for (int d = 0; d < 3; d++) {
								if (dropItem(c, r)) {
									found_droppable = true;
									break;
								}
								sleep(random(100, 400));
							}
						}
					}
				}
			}
			sleep(random(400, 800));
		}
		return getCount() < startCount;
	}

	/**
	 * Drops the item in the specified column and row.
	 *
	 * @param col The column the item is in.
	 * @param row The row the item is in.
	 * @return <tt>true</tt> if we tried to drop the item,
	 *         <tt>false</tt> if not (e.g., if item is undroppable)
	 */
	public boolean dropItem(final int col, final int row) {
		if (methods.interfaces.canContinue()) {
			methods.interfaces.clickContinue();
			sleep(random(800, 1300));
		}
		if (methods.game.getTab() != Game.Tab.INVENTORY
				&& !methods.interfaces.get(Bank.INTERFACE_BANK).isValid()
				&& !methods.interfaces.get(Store.INTERFACE_STORE).isValid()) {
			methods.game.openTab(Game.Tab.INVENTORY);
		}
		if (col < 0 || col > 3 || row < 0 || row > 6) {
			return false;
		}
		final RSItem item = getItems()[col + row * 4];
		return item != null && item.getID() != -1 && item.doAction("Drop");
	}

	/**
	 * Gets the count of all items in your inventory, ignoring stack sizes.
	 *
	 * @return The count.
	 */
	public int getCount() {
		return getCount(false);
	}

	/**
	 * Gets the count of all items in your inventory.
	 *
	 * @param includeStacks <tt>false</tt> if stacked items should be counted as a
	 *                      single item; otherwise <tt>true</tt>.
	 * @return The count.
	 */
	public int getCount(final boolean includeStacks) {
		int count = 0;
		for (final RSItem item : getItems()) {
			final int iid = item.getID();
			if (iid != -1) {
				if (includeStacks) {
					count += item.getStackSize();
				} else {
					++count;
				}
			}
		}
		return count;
	}

	/**
	 * Gets the count of all the items in the inventory with the any of the
	 * specified IDs. This ignores stack sizes.
	 *
	 * @param itemIDs the item IDs to include
	 * @return The count.
	 */
	public int getCount(final int... itemIDs) {
		return getCount(false, itemIDs);
	}

	/**
	 * Gets the count of all the items in the inventory with the any of the
	 * specified IDs.
	 *
	 * @param includeStacks <tt>true</tt> to count the stack sizes of each item;
	 *                      <tt>false</tt> to count a stack as a single item.
	 * @param itemIDs       the item IDs to include
	 * @return The count.
	 */
	public int getCount(final boolean includeStacks, final int... itemIDs) {
		int total = 0;

		for (final RSItem item : getItems()) {
			if (item == null) {
				continue;
			}

			for (final int ID : itemIDs) {
				if (item.getID() == ID) {
					total += includeStacks ? item.getStackSize() : 1;
				}
			}
		}

		return total;
	}

	/**
	 * Gets the count of all the items in the inventory without any of the
	 * provided IDs, ignoring stack sizes.
	 *
	 * @param ids The item IDs to exclude.
	 * @return The count.
	 */
	public int getCountExcept(final int... ids) {
		return getCountExcept(false, ids);
	}

	/**
	 * Gets the count of all the items in the inventory without any of the
	 * provided IDs.
	 *
	 * @param includeStacks <tt>true</tt> to count the stack sizes of each item;
	 *                      <tt>false</tt> to count a stack as a single item.
	 * @param ids           The item IDs to exclude.
	 * @return The count.
	 */
	public int getCountExcept(final boolean includeStacks, final int... ids) {
		int count = 0;
		for (final RSItem i : getItems()) {
			if (i.getID() != -1) {
				boolean skip = false;
				for (final int id : ids) {
					if (i.getID() == id) {
						skip = true;
						break;
					}
				}
				if (!skip) {
					count += includeStacks ? i.getStackSize() : 1;
				}
			}
		}
		return count;
	}

	/**
	 * Gets the first item in the inventory with any of the provided IDs.
	 *
	 * @param ids The IDs of the item to find.
	 * @return The first <tt>RSItem</tt> for the given IDs; otherwise null.
	 */
	public RSItem getItem(final int... ids) {
		for (final RSItem item : getItems()) {
			for (final int id : ids) {
				if (item.getID() == id) {
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the first item in the inventory containing any of the provided names.
	 *
	 * @param names The names of the item to find.
	 * @return The first <tt>RSItem</tt> for the given name(s); otherwise null.
	 */
	public RSItem getItem(final String... names) {
		for (final RSItem item : getItems()) {
			String name = item.getName();
			if (name != null) {
				name = name.toLowerCase();
				for (final String n : names) {
					if (n != null && name.contains(n.toLowerCase())) {
						return item;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Gets inventory item at specified index.
	 *
	 * @param index The index of inventory item.
	 * @return The item, or <tt>null</tt> if not found.
	 */
	public RSItem getItemAt(final int index) {
		final RSComponent comp = getInterface().getComponent(index);
		return 0 <= index && index < 28 && comp != null ? new RSItem(methods,
				comp) : null;
	}

	/**
	 * Gets the ID of an item in the inventory with a given name.
	 *
	 * @param name The name of the item you wish to find.
	 * @return The ID of the item or -1 if not in inventory.
	 */
	public int getItemID(final String name) {
		final RSItem item = getItem(name);
		return item != null ? item.getID() : -1;
	}


	/**
	 * Gets all the items in the inventory.
	 *
	 * @return <tt>RSItem</tt> array of the current inventory items or an
	 *         empty <tt>RSItem[]</tt> if unavailable.
	 */
	public RSItem[] getItems() {
		return getItems(false);
	}

	/**
	 * Gets all the items in the inventory.
	 *
	 * @param cached If the inventory interface should be updated before
	 *               returning the items.
	 * @return <tt>RSItem</tt> array of the current inventory items or an
	 *         empty <tt>RSItem[]</tt> if unavailable.
	 */
	public RSItem[] getItems(final boolean cached) {
		final RSComponent invIface = getInterface(cached);
		if (invIface != null) {
			final RSComponent[] comps = invIface.getComponents();
			if (comps.length > 0) {
				int len = 0;
				for (final RSComponent com : comps) {
					if (com.getType() == 5) {
						++len;
					}
				}

				final RSItem[] inv = new RSItem[len];
				for (int i = 0; i < len; ++i) {
					final RSComponent item = comps[i];
					final int idx = item.getComponentIndex();
					if (idx >= 0) {
						inv[idx] = new RSItem(methods, item);
					} else {
						return new RSItem[0];
					}
				}
				return inv;
			}
		}

		return new RSItem[0];
	}

	/**
	 * Gets all the items in the inventory matching any of the provided IDs.
	 *
	 * @param ids Valid IDs.
	 * @return <tt>RSItem</tt> array of the matching inventory items.
	 */
	public RSItem[] getItems(final int... ids) {
		final LinkedList<RSItem> items = new LinkedList<RSItem>();
		for (final RSItem item : getItems()) {
			for (final int i : ids) {
				if (item.getID() == i) {
					items.add(item);
					break;
				}
			}
		}
		return items.toArray(new RSItem[items.size()]);
	}

	/**
	 * Gets the inventory interface.
	 *
	 * @return the current inventory interface if available; otherwise null.
	 */
	public RSComponent getInterface() {
		return getInterface(false);
	}

	/**
	 * Gets the inventory interface.
	 *
	 * @param cached <tt>true</tt> to skip updating the inventory interface.
	 * @return the current inventory interface if available; otherwise null.
	 */
	public RSComponent getInterface(final boolean cached) {
		if (methods.interfaces.get(INTERFACE_INVENTORY_BANK).isValid()) {
			final RSComponent bankInv = methods.interfaces.getComponent(
					INTERFACE_INVENTORY_BANK, 0);
			if (bankInv != null && bankInv.getAbsoluteX() > 50) {
				return bankInv;
			}
		}
		if (methods.interfaces.get(INTERFACE_INVENTORY_SHOP).isValid()) {
			final RSComponent shopInv = methods.interfaces.getComponent(
					INTERFACE_INVENTORY_SHOP, 0);
			if (shopInv != null && shopInv.getAbsoluteX() > 50) {
				return shopInv;
			}
		}
		if (methods.interfaces.get(INTERFACE_INVENTORY_PRICE_CHECK).isValid()) {
			final RSComponent priceInv = methods.interfaces.getComponent(INTERFACE_INVENTORY_PRICE_CHECK, 0);
			if (priceInv != null && priceInv.getAbsoluteX() > 50) {
				return priceInv;
			}
		}
		if (methods.interfaces.get(INTERFACE_INVENTORY_EQUIPMENT_BONUSES).isValid()) {
			final RSComponent equipInv = methods.interfaces.getComponent(INTERFACE_INVENTORY_EQUIPMENT_BONUSES, 0);
			if (equipInv != null && equipInv.getAbsoluteX() > 50) {
				return equipInv;
			}
		}
		if (methods.interfaces.get(INTERFACE_INVENTORY_DUNGEONEERING_SHOP).isValid()) {
			final RSComponent dungInv = methods.interfaces.getComponent(INTERFACE_INVENTORY_DUNGEONEERING_SHOP, 0);
			if (dungInv != null && dungInv.getAbsoluteX() > 50) {
				return dungInv;
			}
		}

		if (!cached) {
			methods.game.openTab(Game.Tab.INVENTORY);
		}

		return methods.interfaces.getComponent(INTERFACE_INVENTORY, 0);
	}

	/**
	 * Gets the selected inventory item.
	 *
	 * @return The current selected item, or <tt>null</tt> if none is selected.
	 */
	public RSItem getSelectedItem() {
		final int index = getSelectedItemIndex();
		return index == -1 ? null : getItemAt(index);
	}

	/**
	 * Gets the selected item index.
	 *
	 * @return The index of current selected item, or -1 if none is selected.
	 */
	public int getSelectedItemIndex() {
		final RSComponent[] comps = getInterface().getComponents();
		for (int i = 0; i < Math.min(28, comps.length); ++i) {
			if (comps[i].getBorderThickness() == 2) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Gets the selected item name.
	 *
	 * @return The name of the current selected item, or null if none is
	 *         selected.
	 */
	public String getSelectedItemName() {
		final String name = methods.client.getSelectedItemName();
		return name == null ? null : name.replaceAll("<[\\w\\d]+=[\\w\\d]+>", "");
	}

	/**
	 * Checks whether or not your inventory is full.
	 *
	 * @return <tt>true</tt> if your inventory contains 28 items; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean isFull() {
		return getCount(false) == 28;
	}

	/**
	 * Checks whether or not an inventory item is selected.
	 *
	 * @return <tt>true</tt> if an item in your inventory is selected; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean isItemSelected() {
		return getSelectedItemIndex() != -1;
	}

	/**
	 * Randomizes a point.
	 *
	 * @param inventoryPoint The inventory point to be randomized.
	 * @return A randomized <tt>Point</tt> from the center of the given
	 *         <tt>Point</tt>.
	 */
	public Point randomizeItemPoint(final Point inventoryPoint) {
		return new Point(inventoryPoint.x + random(-10, 10), inventoryPoint.y + random(-10, 10));
	}

	/**
	 * Selects the first item in the inventory with the provided ID.
	 *
	 * @param itemID The ID of the item to select.
	 * @return <tt>true</tt> if the item was selected; otherwise <tt>false</tt>.
	 */
	public boolean selectItem(final int itemID) {
		final RSItem item = getItem(itemID);
		return item != null && selectItem(item);
	}

	/**
	 * Selects the specified item in the inventory
	 *
	 * @param item The item to select.
	 * @return <tt>true</tt> if the item was selected; otherwise <tt>false</tt>.
	 */
	public boolean selectItem(final RSItem item) {
		final int itemID = item.getID();
		RSItem selItem = getSelectedItem();
		if (selItem != null && selItem.getID() == itemID) {
			return true;
		}
		if (!item.doAction("Use")) {
			return false;
		}
		for (int c = 0; c < 5 && (selItem = getSelectedItem()) == null; c++) {
			sleep(random(200, 300));
		}
		return selItem != null && selItem.getID() == itemID;
	}

	/**
	 * Uses two items together.
	 *
	 * @param itemID   The first item ID to use.
	 * @param targetID The item ID you want the first parameter to be used on.
	 * @return <tt>true</tt> if the first item has been "used" on the other;
	 *         otherwise <tt>false</tt>.
	 */
	public boolean useItem(final int itemID, final int targetID) {
		final RSItem item = getItem(itemID);
		final RSItem target = getItem(targetID);
		return item != null && target != null && useItem(item, target);
	}

	/**
	 * Uses an item on an object.
	 *
	 * @param itemID The item ID to use on the object.
	 * @param object The RSObject you want the item to be used on.
	 * @return <tt>true</tt> if the "use" action had been used on both the
	 *         RSItem and RSObject; otherwise <tt>false</tt>.
	 */
	public boolean useItem(final int itemID, final RSObject object) {
		final RSItem item = getItem(itemID);
		return item != null && useItem(item, object);
	}

	/**
	 * Uses two items together.
	 *
	 * @param item       The item to use on another item.
	 * @param targetItem The item you want the first parameter to be used on.
	 * @return <tt>true</tt> if the "use" action had been used on both items;
	 *         otherwise <tt>false</tt>.
	 */
	public boolean useItem(final RSItem item, final RSItem targetItem) {
		methods.game.openTab(Game.Tab.INVENTORY);
		return selectItem(item) && targetItem.doAction("Use");
	}

	/**
	 * Uses an item on an object.
	 *
	 * @param item         The item to use on another item.
	 * @param targetObject The RSObject you want the first parameter to be used on.
	 * @return <tt>true</tt> if the "use" action had been used on both the
	 *         RSItem and RSObject; otherwise <tt>false</tt>.
	 */
	public boolean useItem(final RSItem item, final RSObject targetObject) {
		methods.game.openTab(Game.Tab.INVENTORY);
		return selectItem(item) && targetObject.doAction("Use", targetObject.getName());
	}
}
