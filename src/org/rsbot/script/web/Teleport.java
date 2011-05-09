package org.rsbot.script.web;

import org.rsbot.script.wrappers.RSTile;

/**
 * Teleportation base functions.
 *
 * @author Timer
 */
public abstract class Teleport implements Transportation, Prerequisites {
	public abstract RSTile teleportationLocation();
}