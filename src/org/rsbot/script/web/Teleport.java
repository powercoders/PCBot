package org.rsbot.script.web;

/**
 * Teleportation base functions.
 *
 * @author Timer
 */
public abstract class Teleport implements Transportation, Prerequisites {
	public abstract boolean teleportationLocation();
}