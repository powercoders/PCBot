package org.rsbot.script.web;

import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.MethodProvider;
import org.rsbot.script.wrappers.RSTile;

/**
 * Teleportation base information.
 *
 * @author Timer
 */
public abstract class Teleport extends MethodProvider implements Prerequisites, Transportation {
	private final RSTile teleportationLocation;
	private RSTile teleportationStart = null;

	public Teleport(final MethodContext ctx, final RSTile teleportationLocation) {
		super(ctx);
		this.teleportationLocation = teleportationLocation;
	}

	public RSTile teleportationLocation() {
		return teleportationLocation;
	}

	public void setBeginning(RSTile start) {
		teleportationStart = start;
	}

	public RSTile teleportationBeginning() {
		return teleportationStart;
	}
}