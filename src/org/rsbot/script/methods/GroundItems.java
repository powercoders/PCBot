package org.rsbot.script.methods;

import org.rsbot.client.HashTable;
import org.rsbot.script.internal.wrappers.Deque;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSTile;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides access to ground items.
 */
public class GroundItems extends MethodProvider {
	public static final Filter<RSGroundItem> ALL_FILTER = new Filter<RSGroundItem>() {
		public boolean accept(final RSGroundItem item) {
			return true;
		}
	};

	GroundItems(final MethodContext ctx) {
		super(ctx);
	}

	/**
	 * Returns all ground items in the loaded area.
	 *
	 * @return All ground items in the loaded area.
	 */
	public RSGroundItem[] getAll() {
		return getAll(52, ALL_FILTER);
	}

	/**
	 * Returns all matching ground items in the loaded area.
	 *
	 * @param filter Filters out unwanted matches.
	 * @return All ground items
	 */
	public RSGroundItem[] getAll(final Filter<RSGroundItem> filter) {
		return getAll(52, filter);
	}

	/**
	 * Returns all ground items within the provided range.
	 *
	 * @param range The range (max distance in all directions) in which to check
	 *              items for.
	 * @return <tt>RSGroundItem</tt> array containing all of the items in range.
	 */
	public RSGroundItem[] getAll(final int range) {
		return getAll(range, ALL_FILTER);
	}

	/**
	 * Returns all matching ground items within the provided range.
	 *
	 * @param range  The range (max distance in all directions) in which to check items for.
	 * @param filter Filters out unwanted matches.
	 * @return <tt>RSGroundItem</tt> array containing all of the items in range.
	 */
	public RSGroundItem[] getAll(final int range, final Filter<RSGroundItem> filter) {
		final ArrayList<RSGroundItem> temp = new ArrayList<RSGroundItem>();
		final int pX = methods.players.getMyPlayer().getLocation().getX();
		final int pY = methods.players.getMyPlayer().getLocation().getY();
		final int minX = pX - range, minY = pY - range;
		final int maxX = pX + range, maxY = pY + range;
		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				final RSGroundItem[] items = getAllAt(x, y);
				for (final RSGroundItem item : items) {
					if (item != null && filter.accept(item)) {
						temp.add(item);
					}
				}
			}
		}
		return temp.toArray(new RSGroundItem[temp.size()]);
	}

	/**
	 * Returns the nearest ground item that is accepted by the provided Filter.
	 *
	 * @param filter Filters out unwanted matches.
	 * @return The nearest item that is accepted by the provided Filter; or null.
	 */
	public RSGroundItem getNearest(final Filter<RSGroundItem> filter) {
		int dist = 9999999;
		final int pX = methods.players.getMyPlayer().getLocation().getX();
		final int pY = methods.players.getMyPlayer().getLocation().getY();
		final int minX = pX - 52, minY = pY - 52;
		final int maxX = pX + 52, maxY = pY + 52;
		RSGroundItem itm = null;
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				final RSGroundItem[] items = getAllAt(x, y);
				for (final RSGroundItem item : items) {
					if (item != null && filter.accept(item) && methods.calc.distanceTo(item.getLocation()) < dist) {
						dist = methods.calc.distanceTo(item.getLocation());
						itm = item;
					}
				}
			}
		}
		return itm;
	}

	/**
	 * Returns the nearest item on the ground with an ID that matches any of the
	 * IDs provided.
	 *
	 * @param ids The IDs to look for.
	 * @return The nearest RSGroundItem with an ID that matches any in the array of
	 *         IDs provided; or null if no matching ground items were found.
	 */
	public RSGroundItem getNearest(final int... ids) {
		return getNearest(new Filter<RSGroundItem>() {
			public boolean accept(final RSGroundItem item) {
				if (item != null) {
					final int iid = item.getItem().getID();
					for (final int id : ids) {
						if (id == iid) {
							return true;
						}
					}
				}
				return false;
			}
		});
	}

	/**
	 * Returns the nearest item on the ground with one of the provided names.
	 *
	 * @param names The names to look for.
	 * @return The nearest RSGroundItem with a name that matches any in the array provided; or null if
	 *         no matching ground items were found.
	 */
	public RSGroundItem getNearest(final String... names) {
		return getNearest(new Filter<RSGroundItem>() {
			public boolean accept(final RSGroundItem item) {
				final String name = item != null ? item.getItem().getName() : null;
				if (name != null) {
					for (final String n : names) {
						if (n.equalsIgnoreCase(name)) {
							return true;
						}
					}
				}
				return false;
			}
		});
	}

	/**
	 * Returns all the ground items at a tile on the current plane.
	 *
	 * @param x The x position of the tile in the world.
	 * @param y The y position of the tile in the world.
	 * @return An array of the ground items on the specified tile.
	 */
	public RSGroundItem[] getAllAt(final int x, final int y) {
		if (!methods.game.isLoggedIn()) {
			return new RSGroundItem[0];
		}
		final List<RSGroundItem> list = new ArrayList<RSGroundItem>();

		final HashTable itemNC = methods.client.getRSItemHashTable();
		final int id = x | y << 14 | methods.client.getPlane() << 28;

		final org.rsbot.client.NodeListCache itemNLC = (org.rsbot.client.NodeListCache) methods.nodes.lookup(itemNC, id);

		if (itemNLC == null) {
			return new RSGroundItem[0];
		}

		final Deque<org.rsbot.client.RSItem> itemNL = new Deque<org.rsbot.client.RSItem>(itemNLC.getNodeList());
		for (org.rsbot.client.RSItem item = itemNL.getHead(); item != null; item = itemNL.getNext()) {
			list.add(new RSGroundItem(methods, new RSTile(x, y), new RSItem(methods, item)));
		}

		return list.toArray(new RSGroundItem[list.size()]);
	}

	/**
	 * Returns all the ground items at a tile on the current plane.
	 *
	 * @param t The tile.
	 * @return An array of the ground items on the specified tile.
	 */
	public RSGroundItem[] getAllAt(final RSTile t) {
		return getAllAt(t.getX(), t.getY());
	}
}
