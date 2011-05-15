package org.rsbot.script.methods;

import org.rsbot.script.wrappers.RSLocalPath;
import org.rsbot.script.wrappers.RSPath;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.script.wrappers.RSTilePath;

import java.awt.*;

/**
 * Walking related operations.
 */
public class Walking extends MethodProvider {

	public final int INTERFACE_RUN_ORB = 750;

	Walking(final MethodContext ctx) {
		super(ctx);
	}

	private RSPath lastPath;
	private RSTile lastDestination;
	private RSTile lastStep;

	/**
	 * Creates a new path based on a provided array of tile waypoints.
	 *
	 * @param tiles The waypoint tiles.
	 * @return An RSTilePath.
	 */
	public RSTilePath newTilePath(final RSTile[] tiles) {
		if (tiles == null) {
			throw new IllegalArgumentException("null waypoint list");
		}
		return new RSTilePath(methods, tiles);
	}

	/**
	 * Generates a path from the player's current location to a destination
	 * tile.
	 *
	 * @param destination The destination tile.
	 * @return The path as an RSPath.
	 */
	public RSPath getPath(final RSTile destination) {
		return new RSLocalPath(methods, destination);
	}

	/**
	 * Determines whether or not a given tile is in the loaded map area.
	 *
	 * @param tile The tile to check.
	 * @return <tt>true</tt> if local; otherwise <tt>false</tt>.
	 */
	public boolean isLocal(final RSTile tile) {
		final int[][] flags = getCollisionFlags(methods.game.getPlane());
		final int x = tile.getX() - methods.game.getBaseX();
		final int y = tile.getY() - methods.game.getBaseY();
		return flags != null && x >= 0 && y >= 0 && x < flags.length && y < flags.length;
	}

	/**
	 * Walks one tile towards the given destination using a generated path.
	 *
	 * @param destination The destination tile.
	 * @return <tt>true</tt> if the next tile was walked to; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean walkTo(final RSTile destination) {
		if (destination.equals(lastDestination)
				&& methods.calc.distanceTo(lastStep) < 10) {
			return lastPath.traverse();
		}
		lastDestination = destination;
		lastPath = getPath(destination);
		if (!lastPath.isValid()) {
			return false;
		}
		lastStep = lastPath.getNext();
		return lastPath.traverse();
	}

	/**
	 * Walks to the given tile using the minimap with 1 tile randomness.
	 *
	 * @param t The tile to walk to.
	 * @return <tt>true</tt> if the tile was clicked; otherwise <tt>false</tt>.
	 * @see #walkTileMM(RSTile, int, int)
	 */
	public boolean walkTileMM(final RSTile t) {
		return walkTileMM(t, 0, 0);
	}

	/**
	 * Walks to the given tile using the minimap with given randomness.
	 *
	 * @param t The tile to walk to.
	 * @param x The x randomness (between 0 and x-1).
	 * @param y The y randomness (between 0 and y-1).
	 * @return <tt>true</tt> if the tile was clicked; otherwise <tt>false</tt>.
	 */
	public boolean walkTileMM(final RSTile t, final int x, final int y) {
		return walkTileMM(t, x, y, 0, 0, 0);
	}

	/**
	 * Walks to the given tile using the minimap with given randomness.
	 *
	 * @param t  The tile to walk to.
	 * @param x  The x randomness (between 0 and x-1).
	 * @param y  The y randomness (between 0 and y-1).
	 * @param rx The mouse gaussian randomness (x).
	 * @param ry The mouse gaussian randomness (y).
	 * @param rm The mouse movement distance after click.
	 * @return <tt>true</tt> if the tile was clicked; otherwise <tt>false</tt>.
	 */
	public boolean walkTileMM(final RSTile t, final int x, final int y, final int rx, final int ry, final int rm) {
		int xx = t.getX(), yy = t.getY();
		if (x > 0) {
			if (random(1, 3) == random(1, 3)) {
				xx += random(0, x);
			} else {
				xx -= random(0, x);
			}
		}
		if (y > 0) {
			if (random(1, 3) == random(1, 3)) {
				yy += random(0, y);
			} else {
				yy -= random(0, y);
			}
		}
		RSTile dest = new RSTile(xx, yy);
		if (!methods.calc.tileOnMap(dest)) {
			dest = getClosestTileOnMap(dest);
		}
		final Point p = methods.calc.tileToMinimap(dest);
		if (p.x != -1 && p.y != -1) {
			methods.mouse.move(p, rx, ry, rm);
			final Point p2 = methods.calc.tileToMinimap(dest);
			if (p2.x != -1 && p2.y != -1) {
				if (!methods.mouse.getLocation().equals(p2)) {//We must've moved while walking, move again!
					methods.mouse.move(p2);
				}
				if (!methods.mouse.getLocation().equals(p2)) {//Get exact since we're moving... should be removed?
					methods.mouse.hop(p2);
				}
				methods.mouse.click(true, rm);
				return true;
			}
		}
		return false;
	}

	/**
	 * Walks to the given tile using the minimap with given randomness.
	 *
	 * @param t The tile to walk to.
	 * @param r The maximum deviation from the tile to allow.
	 * @return <tt>true</tt> if the tile was clicked; otherwise <tt>false</tt>.
	 */
	public boolean walkTileMM(final RSTile t, final int r) {
		int x = t.getX();
		int y = t.getY();
		if (random(1, 3) == random(1, 3)) {
			x += random(0, r);
		} else {
			x -= random(0, r);
		}
		if (random(1, 3) == random(1, 3)) {
			y += random(0, r);
		} else {
			y -= random(0, r);
		}
		final RSTile dest = new RSTile(x, y);
		return !methods.players.getMyPlayer().getLocation().equals(dest) && walkTileMM(dest, 0, 0);
	}

	/**
	 * Walks to a tile using onScreen clicks and not the MiniMap. If the tile is
	 * not on the screen, it will find the closest tile that is on screen and it
	 * will walk there instead.
	 *
	 * @param tileToWalk Tile to walk.
	 * @return True if successful.
	 */
	public boolean walkTileOnScreen(final RSTile tileToWalk) {
		return methods.tiles.doAction(methods.calc.getTileOnScreen(tileToWalk), "Walk ");
	}

	/**
	 * Rests until 100% energy
	 *
	 * @return <tt>true</tt> if rest was enabled; otherwise false.
	 * @see #rest(int)
	 */
	public boolean rest() {
		return rest(100);
	}

	/**
	 * Rests until a certain amount of energy is reached.
	 *
	 * @param stopEnergy Amount of energy at which it should stop resting.
	 * @return <tt>true</tt> if rest was enabled; otherwise false.
	 */
	public boolean rest(final int stopEnergy) {
		int energy = getEnergy();
		for (int d = 0; d < 5; d++) {
			methods.interfaces.getComponent(INTERFACE_RUN_ORB, 1).doAction("Rest");
			methods.mouse.moveSlightly();
			sleep(random(400, 600));
			final int anim = methods.players.getMyPlayer().getAnimation();
			if (anim == 12108 || anim == 2033 || anim == 2716 || anim == 11786 || anim == 5713) {
				break;
			}
			if (d == 4) {
				return false;
			}
		}
		while (energy < stopEnergy) {
			sleep(random(250, 500));
			energy = getEnergy();
		}
		return true;
	}

	/**
	 * Turns run on or off using the game GUI controls.
	 *
	 * @param enable <tt>true</tt> to enable run, <tt>false</tt> to disable it.
	 */
	public void setRun(final boolean enable) {
		if (isRunEnabled() != enable) {
			methods.interfaces.getComponent(INTERFACE_RUN_ORB, 0).doClick();
		}
	}

	/**
	 * Generates a path from the player's current location to a destination
	 * tile.
	 *
	 * @param destination The destination tile.
	 * @return The path as an RSTile array.
	 */
	@Deprecated
	public RSTile[] findPath(final RSTile destination) {
		final RSLocalPath path = new RSLocalPath(methods, destination);
		if (path.isValid()) {
			final RSTilePath tp = path.getCurrentTilePath();
			if (tp != null) {
				return tp.toArray();
			}
		}
		return new RSTile[0];
	}

	/**
	 * Randomizes a single tile.
	 *
	 * @param tile          The RSTile to randomize.
	 * @param maxXDeviation Max X distance from tile.getX().
	 * @param maxYDeviation Max Y distance from tile.getY().
	 * @return The randomized tile.
	 * @deprecated Use
	 *             {@link org.rsbot.script.wrappers.RSTile#randomize(int, int)}.
	 */
	@Deprecated
	public RSTile randomize(final RSTile tile, final int maxXDeviation, final int maxYDeviation) {
		return tile.randomize(maxXDeviation, maxYDeviation);
	}

	/**
	 * Returns the closest tile on the minimap to a given tile.
	 *
	 * @param tile The destination tile.
	 * @return Returns the closest tile to the destination on the minimap.
	 */
	public RSTile getClosestTileOnMap(final RSTile tile) {
		if (!methods.calc.tileOnMap(tile) && methods.game.isLoggedIn()) {
			final RSTile loc = methods.players.getMyPlayer().getLocation();
			final RSTile walk = new RSTile((loc.getX() + tile.getX()) / 2, (loc.getY() + tile.getY()) / 2);
			return methods.calc.tileOnMap(walk) ? walk : getClosestTileOnMap(walk);
		}
		return tile;
	}

	/**
	 * Returns whether or not run is enabled.
	 *
	 * @return <tt>true</tt> if run mode is enabled; otherwise <tt>false</tt>.
	 */
	public boolean isRunEnabled() {
		return methods.settings.getSetting(173) == 1;
	}

	/**
	 * Returns the player's current run energy.
	 *
	 * @return The player's current run energy.
	 */
	public int getEnergy() {
		try {
			return Integer.parseInt(methods.interfaces.getComponent(750, 5)
					.getText());
		} catch (final NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * Gets the destination tile (where the flag is on the minimap). If there is
	 * no destination currently, null will be returned.
	 *
	 * @return The current destination tile, or null.
	 */
	public RSTile getDestination() {
		if (methods.client.getDestX() <= 0) {
			return null;
		}
		return new RSTile(
				methods.client.getDestX() + methods.client.getBaseX(),
				methods.client.getDestY() + methods.client.getBaseY());
	}

	/**
	 * Gets the collision flags for a given floor level in the loaded region.
	 *
	 * @param plane The floor level (0, 1, 2 or 3).
	 * @return the collision flags.
	 */
	public int[][] getCollisionFlags(final int plane) {
		return methods.client.getRSGroundDataArray()[plane].getBlocks();
	}

	/**
	 * Returns the collision map offset from the current region base on a given
	 * plane.
	 *
	 * @param plane The floor level.
	 * @return The offset as an RSTile.
	 */
	public RSTile getCollisionOffset(final int plane) {
		final org.rsbot.client.RSGroundData data = methods.client
				.getRSGroundDataArray()[plane];
		return new RSTile(data.getX(), data.getY());
	}

	// DEPRECATED

	/**
	 * Randomizes a single tile.
	 *
	 * @param tile          The RSTile to randomize.
	 * @param maxXDeviation Max X distance from tile.getX().
	 * @param maxYDeviation Max Y distance from tile.getY().
	 * @return The randomized tile.
	 * @deprecated Use
	 *             {@link #randomize(org.rsbot.script.wrappers.RSTile, int, int)}
	 *             .
	 */
	@Deprecated
	public RSTile randomizeTile(final RSTile tile, final int maxXDeviation, final int maxYDeviation) {
		return randomize(tile, maxXDeviation, maxYDeviation);
	}

	/**
	 * Walks towards the end of a path. This method should be looped.
	 *
	 * @param path The path to walk along.
	 * @return <tt>true</tt> if the next tile was reached; otherwise
	 *         <tt>false</tt>.
	 * @see #walkPathMM(RSTile[], int)
	 */
	@Deprecated
	public boolean walkPathMM(final RSTile[] path) {
		return walkPathMM(path, 16);
	}

	/**
	 * Walks towards the end of a path. This method should be looped.
	 *
	 * @param path    The path to walk along.
	 * @param maxDist See {@link #nextTile(RSTile[], int)}.
	 * @return <tt>true</tt> if the next tile was reached; otherwise
	 *         <tt>false</tt>.
	 * @see #walkPathMM(RSTile[], int, int)
	 */
	@Deprecated
	public boolean walkPathMM(final RSTile[] path, final int maxDist) {
		return walkPathMM(path, maxDist, 1, 1);
	}

	/**
	 * Walks towards the end of a path. This method should be looped.
	 *
	 * @param path  The path to walk along.
	 * @param randX The X value to randomize each tile in the path by.
	 * @param randY The Y value to randomize each tile in the path by.
	 * @return <tt>true</tt> if the next tile was reached; otherwise
	 *         <tt>false</tt>.
	 * @see #walkPathMM(RSTile[], int, int, int)
	 */
	@Deprecated
	public boolean walkPathMM(final RSTile[] path, final int randX, final int randY) {
		return walkPathMM(path, 16, randX, randY);
	}

	/**
	 * Walks towards the end of a path. This method should be looped.
	 *
	 * @param path    The path to walk along.
	 * @param maxDist See {@link #nextTile(RSTile[], int)}.
	 * @param randX   The X value to randomize each tile in the path by.
	 * @param randY   The Y value to randomize each tile in the path by.
	 * @return <tt>true</tt> if the next tile was reached; otherwise
	 *         <tt>false</tt>.
	 */
	@Deprecated
	public boolean walkPathMM(final RSTile[] path, final int maxDist, final int randX, final int randY) {
		try {
			final RSTile next = nextTile(path, maxDist);
			return next != null && walkTileMM(next, randX, randY);
		} catch (final Exception e) {
			return false;
		}
	}

	/**
	 * Walks to the end of a path via the screen. This method should be looped.
	 *
	 * @param path The path to walk along.
	 * @return <tt>true</tt> if the next tile was reached; otherwise
	 *         <tt>false</tt>.
	 * @see #walkPathOnScreen(RSTile[], int)
	 */
	@Deprecated
	public boolean walkPathOnScreen(final RSTile[] path) {
		return walkPathOnScreen(path, 16);
	}

	/**
	 * Walks a path using onScreen clicks and not the MiniMap. If the next tile
	 * is not on the screen, it will find the closest tile that is on screen and
	 * it will walk there instead.
	 *
	 * @param path    Path to walk.
	 * @param maxDist Max distance between tiles in the path.
	 * @return True if successful.
	 */
	@Deprecated
	public boolean walkPathOnScreen(final RSTile[] path, final int maxDist) {
		final RSTile next = nextTile(path, maxDist);
		if (next != null) {
			final RSTile os = methods.calc.getTileOnScreen(next);
			return os != null && methods.tiles.doAction(os, "Walk");
		}
		return false;
	}

	/**
	 * Reverses an array of tiles.
	 *
	 * @param other The <tt>RSTile</tt> path array to reverse.
	 * @return The reverse <tt>RSTile</tt> path for the given <tt>RSTile</tt>
	 *         path.
	 */
	@Deprecated
	public RSTile[] reversePath(final RSTile[] other) {
		final RSTile[] t = new RSTile[other.length];
		for (int i = 0; i < t.length; i++) {
			t[i] = other[other.length - i - 1];
		}
		return t;
	}

	/**
	 * Returns the next tile to walk to on a path.
	 *
	 * @param path The path.
	 * @return The next <tt>RSTile</tt> to walk to on the provided path; or
	 *         <code>null</code> if far from path or at destination.
	 * @see #nextTile(RSTile[], int)
	 */
	@Deprecated
	public RSTile nextTile(final RSTile path[]) {
		return nextTile(path, 17);
	}

	/**
	 * Returns the next tile to walk to in a path.
	 *
	 * @param path     The path.
	 * @param skipDist If the distance to the tile after the next in the path is less
	 *                 than or equal to this distance, the tile after next will be
	 *                 returned rather than the next tile, skipping one. This
	 *                 interlacing aids continuous walking.
	 * @return The next <tt>RSTile</tt> to walk to on the provided path; or
	 *         <code>null</code> if far from path or at destination.
	 */
	@Deprecated
	public RSTile nextTile(final RSTile path[], final int skipDist) {
		int dist = 99;
		int closest = -1;
		for (int i = path.length - 1; i >= 0; i--) {
			final RSTile tile = path[i];
			final int d = methods.calc.distanceTo(tile);
			if (d < dist) {
				dist = d;
				closest = i;
			}
		}

		int feasibleTileIndex = -1;

		for (int i = closest; i < path.length; i++) {

			if (methods.calc.distanceTo(path[i]) <= skipDist) {
				feasibleTileIndex = i;
			} else {
				break;
			}
		}

		if (feasibleTileIndex == -1) {
			return null;
		} else {
			return path[feasibleTileIndex];
		}
	}

	/**
	 * Randomizes a path of tiles.
	 *
	 * @param path          The RSTiles to randomize.
	 * @param maxXDeviation Max X distance from tile.getX().
	 * @param maxYDeviation Max Y distance from tile.getY().
	 * @return The new, randomized path.
	 */
	@Deprecated
	public RSTile[] randomizePath(final RSTile[] path, final int maxXDeviation,
	                              final int maxYDeviation) {
		final RSTile[] rez = new RSTile[path.length];
		for (int i = 0; i < path.length; i++) {
			rez[i] = randomize(path[i], maxXDeviation, maxYDeviation);
		}
		return rez;
	}
}
