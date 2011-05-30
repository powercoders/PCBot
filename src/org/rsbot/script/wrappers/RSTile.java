package org.rsbot.script.wrappers;

/**
 * A tile at an absolute location in the game world.
 */
public class RSTile {
	private final int x;
	private final int y;
	private final int z;

	public static interface Flag {
		public static final int W_NW = 0x1;
		public static final int W_N = 0x2;
		public static final int W_NE = 0x4;
		public static final int W_E = 0x8;
		public static final int W_SE = 0x10;
		public static final int W_S = 0x20;
		public static final int W_SW = 0x40;
		public static final int W_W = 0x80;
		public static final int BLOCKED = 0x100;
		public static final int WATER = 0x1280100;
	}

	/**
	 * @param x the x axel of the Tile
	 * @param y the y axel of the Tile
	 */
	public RSTile(final int x, final int y) {
		this.x = x;
		this.y = y;
		z = 0;
	}

	/**
	 * @param x the x axel of the Tile
	 * @param y the y axel of the Tile
	 * @param z the z axel of the Tile( the floor)
	 */
	public RSTile(final int x, final int y, final int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	/**
	 * Randomizes this tile.
	 *
	 * @param maxXDeviation Max X distance from tile x.
	 * @param maxYDeviation Max Y distance from tile y.
	 * @return The randomized tile
	 */
	public RSTile randomize(final int maxXDeviation, final int maxYDeviation) {
		int x = getX();
		int y = getY();
		if (maxXDeviation > 0) {
			double d = Math.random() * 2 - 1.0;
			d *= maxXDeviation;
			x += (int) d;
		}
		if (maxYDeviation > 0) {
			double d = Math.random() * 2 - 1.0;
			d *= maxYDeviation;
			y += (int) d;
		}
		return new RSTile(x, y, getZ());
	}

	public static boolean Questionable(final int FLAG) {
		return (FLAG & (Flag.W_NW | Flag.W_N | Flag.W_NE | Flag.W_E | Flag.W_SE | Flag.W_S | Flag.W_SW | Flag.W_W)) != 0;
	}

	public static boolean Walkable(final int FLAG) {
		return (FLAG & (Flag.BLOCKED | Flag.WATER)) == 0;
	}

	public static boolean Special(final int FLAG) {
		return (FLAG & Flag.BLOCKED) == 0 && (FLAG & Flag.WATER) != 0;
	}

	@Override
	public int hashCode() {
		return x * 31 + y;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof RSTile) {
			final RSTile tile = (RSTile) obj;
			return tile.x == x && tile.y == y && tile.z == z;
		}
		return false;
	}

	@Override
	public String toString() {
		return "(X: " + x + ", Y:" + y + ", Z:" + z + ")";
	}
}
