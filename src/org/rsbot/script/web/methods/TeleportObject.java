package org.rsbot.script.web.methods;

import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.web.Teleport;
import org.rsbot.script.wrappers.RSComponent;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

public class TeleportObject extends Teleport {
	public final int objectID;
	public final String action;
	public final RSComponent comp;

	public TeleportObject(final MethodContext ctx,
			final RSTile teleportationLocation, final String action,
			final int objectID) {
		super(ctx, teleportationLocation);
		this.objectID = objectID;
		this.action = action;
		comp = null;
	}

	public TeleportObject(final MethodContext ctx,
			final RSTile teleportationLocation, final String action,
			final RSComponent comp, final int objectID) {
		super(ctx, teleportationLocation);
		this.objectID = objectID;
		this.action = action;
		this.comp = comp;
	}

	@Override
	public double getDistance(final RSTile destination) {
		return methods.calc.distanceBetween(teleportationLocation(), destination);// TODO
																					// use
																					// web
																					// distancing.
	}

	@Override
	public boolean isApplicable(final RSTile base, final RSTile destination) {
		return methods.calc.distanceBetween(base, teleportationLocation()) > 30
				&& methods.calc.distanceBetween(teleportationLocation(), destination) < methods.calc.distanceTo(destination);
	}

	@Override
	public boolean meetsPrerequisites() {
		return methods.objects.getNearest(objectID) != null;
	}

	@Override
	public boolean perform() {
		final RSObject obj = methods.objects.getNearest(objectID);
		if (obj != null) {
			if (obj.doAction(action)) {
				final long tO = System.currentTimeMillis();
				while (System.currentTimeMillis() - tO < 10000) {
					if (methods.interfaces.canContinue()) {
						methods.interfaces.clickContinue();
						sleep(100);
					}
					if (comp != null) {
						comp.doClick();
					}
					sleep(100);
					if (methods.calc.distanceBetween(methods.players.getMyPlayer().getLocation(), teleportationLocation()) < 15) {
						break;
					}
				}
			}
			return methods.calc.distanceBetween(methods.players.getMyPlayer().getLocation(), teleportationLocation()) < 15;
		}
		return false;
	}
}
