package org.rsbot.script.web;

import org.rsbot.script.Random;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.MethodProvider;
import org.rsbot.script.randoms.LoginBot;
import org.rsbot.script.wrappers.RSPath;
import org.rsbot.script.wrappers.RSTile;

public class RouteStep extends MethodProvider {
	private final Type type;
	private RSTile[] path = null;
	private Teleport teleport = null;

	public static enum Type {
		PATH, TELEPORT
	}

	public RouteStep(final MethodContext ctx, final Object step) {
		super(ctx);
		if (step instanceof Teleport) {
			this.type = Type.TELEPORT;
			this.teleport = (Teleport) step;
		} else if (step instanceof RSTile[]) {
			this.type = Type.PATH;
			this.path = (RSTile[]) step;
		} else if (step instanceof RSTile) {
			this.type = Type.PATH;
			this.path = new RSTile[] { (RSTile) step };
		}else{
			throw new IllegalArgumentException("Step is of an invalid type!");
		}
	}

	public boolean execute() {
		switch (type) {
		case PATH:
			RSPath walkingPath = methods.walking.newTilePath(path);
			while (!inSomeRandom()) {
				if (!walkingPath.traverse()
						|| methods.calc.distanceTo(walkingPath.getEnd()) < 5) {
					break;
				}
				sleep(random(50, 150));
			}
			return !inSomeRandom()
					&& methods.calc.distanceTo(walkingPath.getEnd()) < 5;
		case TELEPORT:
			return teleport != null && teleport.preform();
		}
		return false;
	}

	private boolean inSomeRandom() {
		if (methods.bot.disableRandoms) {
			return false;
		}
		for (final Random random : methods.bot.getScriptHandler().getRandoms()) {
			if (random.isEnabled()
					&& !(methods.bot.disableAutoLogin && random instanceof LoginBot)) {
				if (random.activateCondition()) {
					return true;
				}
			}
		}
		return false;
	}
}
