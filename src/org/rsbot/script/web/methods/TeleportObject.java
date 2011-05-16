package org.rsbot.script.web.methods;

import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.web.Teleport;
import org.rsbot.script.wrappers.RSComponent;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.script.wrappers.RSWeb;

public class TeleportObject extends Teleport {
	public final int objectID;
	public final String action;
	public final RSComponent comp;

	public TeleportObject(final MethodContext ctx, final RSTile teleportationLocation, final String action, final int objectID) {
		super(ctx, teleportationLocation);
		this.objectID = objectID;
		this.action = action;
		comp = null;
	}

	public TeleportObject(final MethodContext ctx, final RSTile teleportationLocation, final String action, final RSComponent comp, final int objectID) {
		super(ctx, teleportationLocation);
		this.objectID = objectID;
		this.action = action;
		this.comp = comp;
	}

	public boolean meetsPrerequisites() {
		return methods.objects.getNearest(objectID) != null;
	}

	public boolean isApplicable(RSTile base, RSTile destination) {
		return methods.calc.distanceBetween(base, teleportationLocation()) > 30 && methods.calc.distanceBetween(teleportationLocation(), destination) < methods.calc.distanceTo(destination);
	}

	public boolean perform() {
		RSObject obj = methods.objects.getNearest(objectID);
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

	public double getDistance(RSTile destination) {
		RSWeb tempWeb = methods.web.getWeb(teleportationLocation(), destination);
		double d = 0.0D;
		if (tempWeb != null) {
			d = tempWeb.getDistance();
			tempWeb = null;
		}
		return d;
	}
}
