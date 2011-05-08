package org.rsbot.script.wrappers;

/**
 * The web map data.
 *
 * @author Timer
 * @author Aut0r
 */

public class WebMap {
	/**
	 * The tile array.
	 */
	private final WebTile[] tiles;

	public WebMap(final WebTile[] tiles) {
		this.tiles = tiles;
	}

	/**
	 * Gets a web tile by index.
	 *
	 * @param index The index of the tile in the map.
	 * @return The tile.
	 */
	public WebTile getWebTile(final int index) {
		try {
			return tiles[index];
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * Returns this maps tile array;
	 *
	 * @return The tile array.
	 */
	public WebTile[] getTiles() {
		return tiles;
	}

	/**
	 * The nearest web tile.
	 *
	 * @param tile The web tile from a tile.
	 * @return The resulting web tile.
	 */
	public WebTile getWebTile(final RSTile tile) {
		double maxDist = 999999.0;
		WebTile webTile = null;
		for (final WebTile ctrl : tiles) {
			if (maxDist == 0.0) {
				break;
			}
			final double sqrt = Math.sqrt((ctrl.getX() - tile.getX())
					* (ctrl.getX() - tile.getX())
					+ (ctrl.getY() - tile.getY())
					* (ctrl.getY() - tile.getY()));
			if (sqrt < maxDist) {
				webTile = ctrl;
				maxDist = sqrt;
			}
		}
		return webTile;
	}

	/**
	 * Finds the heuristic.
	 *
	 * @param start The starting tile.
	 * @param end   The ending tile.
	 * @return The heuristic.
	 */
	public double heuristic(final WebTile start, final WebTile end) {
		double dx = start.getX() - end.getX();
		double dy = start.getY() - end.getY();
		if (dx < 0) {
			dx = -dx;
		}
		if (dy < 0) {
			dy = -dy;
		}
		return dx < dy ? dy : dx;
	}

	/**
	 * Gets the dist.
	 *
	 * @param start Starting web tile.
	 * @param end   Ending web tile.
	 * @return The dist.
	 */
	public double dist(final WebTile start, final WebTile end) {
		if (start.getX() != end.getX() && start.getY() != end.getY()) {
			final RSTile curr = start.tile();
			final RSTile dest = end.tile();
			return Math.sqrt((curr.getX() - dest.getX())
					* (curr.getX() - dest.getX())
					+ (curr.getY() - dest.getY())
					* (curr.getY() - dest.getY()));
		} else {
			return 1.0;
		}
	}
}
