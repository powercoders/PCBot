package org.rsbot.script.web;

/**
 * Teleportation base functions.
 */
public abstract class Teleportation implements Activity, Prerequisites {
	public abstract boolean teleportationLocation();
}