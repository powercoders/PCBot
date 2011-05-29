package org.rsbot.script.internal.wrappers;

/**
 * A class that handles flags of tiles.
 * 
 * @author Timer
 */
public class TileData {
	public static interface Key {
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

	public static boolean Questionable(final int FLAG) {
		return (FLAG & (Key.W_NW | Key.W_N | Key.W_NE | Key.W_E | Key.W_SE
				| Key.W_S | Key.W_SW | Key.W_W)) != 0;
	}

	public static boolean Special(final int FLAG) {
		return (FLAG & Key.BLOCKED) == 0 && (FLAG & Key.WATER) != 0;
	}

	public static boolean Walkable(final int FLAG) {
		return (FLAG & (Key.BLOCKED | Key.WATER)) == 0;
	}
}
