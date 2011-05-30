package org.rsbot.script.wrappers;

import org.rsbot.script.internal.wrappers.WebAction;

public class RSWebTile extends RSTile {
	public RSWebTile(final int x, final int y) {
		super(x, y);
	}

	public RSWebTile(final int x, final int y, final int z) {
		super(x, y, z);
	}

	public RSWebTile(final RSTile tile) {
		super(tile.getX(), tile.getY(), tile.getZ());
	}

	private WebAction action = null;

	/**
	 * Sets the action class to have the tile execute.
	 *
	 * @param action The action.
	 */
	public void setAction(final WebAction action) {
		this.action = action;
	}

	/**
	 * Checks if the tile is special and needs an action.
	 *
	 * @return <tt>true</tt> if the tile is special.
	 */
	public boolean special() {
		return action != null;
	}

	/**
	 * Does the action the tile requires.
	 *
	 * @return <tt>true</tt> if the tile can be passed (or was executed correctly).
	 */
	public boolean perform() {
		return action == null || action.execute();
	}
}
