package org.rsbot.script.web;

/**
 * Teleportation base functions.
 *
 * @author Timer
 */
public abstract class Teleportation implements Activity, Prerequisites {
	public abstract boolean teleportationLocation();
}