package org.rsbot.script.wrappers;

import java.util.EnumSet;

import org.rsbot.script.methods.MethodContext;

/**
 * A path consisting of a list of tile waypoints.
 *
 * @author Timer
 */
public class RSWeb extends RSPath {
	private final RSTile[] tiles;

	public RSWeb(final MethodContext ctx, final RSTile[] tiles) {
		super(ctx);
		this.tiles = tiles;
	}

	public RSTile[] getTiles() {
		return tiles;
	}

	@Override
	public boolean traverse(final EnumSet<TraversalOption> options) {
		return false;
	}

	@Override
	public boolean isValid() {
		return false;
	}

	@Override
	public RSTile getNext() {
		return null;
	}

	@Override
	public RSTile getStart() {
		return null;
	}

	@Override
	public RSTile getEnd() {
		return null;
	}
}