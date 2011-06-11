package org.rsbot.script.wrappers;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jeroen
 * Date: 11-6-11
 * Time: 14:26
 * To change this template use File | Settings | File Templates.
 */
public interface RSTarget {

	/**
	 * Returns the screen location from this target
	 *
	 * @return
	 */
	public Point getPoint();

	/**
	 * Checks if the given x and y are inside of this target
	 *
	 * @param x The x position
	 * @param y The y position
	 */
	public boolean contains(int x, int y);
}
