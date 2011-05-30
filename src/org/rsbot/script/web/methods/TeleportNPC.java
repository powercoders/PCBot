package org.rsbot.script.web.methods;

import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.web.Teleport;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;

public class TeleportNPC extends Teleport {
	public final int npcID;
	public final String action;

	public TeleportNPC(final MethodContext ctx,
			final RSTile teleportationLocation, final String action,
			final int npcID) {
		super(ctx, teleportationLocation);
		this.npcID = npcID;
		this.action = action;
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
		return methods.npcs.getNearest(npcID) != null;
	}

	@Override
	public boolean perform() {
		final RSNPC npc = methods.npcs.getNearest(npcID);
		if (npc != null) {
			if (npc.doAction(action)) {
				final long tO = System.currentTimeMillis();
				while (System.currentTimeMillis() - tO < 10000) {
					sleep(100);
					if (!npc.isValid() || npc == null) {
						break;
					}
				}
			}
			return !npc.isValid() || npc == null;
		}
		return false;
	}
}
