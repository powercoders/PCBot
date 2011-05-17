package org.rsbot.script.internal.wrappers;

/**
 * A class that handles flags of tiles.
 *
 * @author Timer
 */
public class TileData {
	public static interface Flags {
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

	public static boolean Questionable(final int key) {
		return (key & Flags.WATER) == 0 && (key & Flags.BLOCKED) == 0;
	}

	public static boolean Walkable(final int key) {
		return (key & (Flags.W_NW | Flags.W_N | Flags.W_NE | Flags.W_E | Flags.W_SE | Flags.W_S | Flags.W_SW | Flags.W_W |
				Flags.BLOCKED | Flags.WATER)) == 0;
	}

	public static boolean Special(final int key) {
		return (key & Flags.BLOCKED) == 0 && (key & Flags.WATER) != 0;
	}
}