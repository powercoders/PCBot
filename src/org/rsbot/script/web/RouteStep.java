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

	public RouteStep(final MethodContext ctx, final Type type, final Object step) {
		super(ctx);
		this.type = type;
		switch (type) {
			case PATH:
				path = (RSTile[]) step;
				break;
			case TELEPORT:
				teleport = (Teleport) step;
				break;
		}
	}

	public boolean execute() {
		switch (type) {
			case PATH:
				RSPath walkingPath = methods.walking.newTilePath(path);
				while (!inSomeRandom()) {
					if (!walkingPath.traverse() || methods.calc.distanceTo(walkingPath.getEnd()) < 5) {
						break;
					}
					sleep(random(50, 150));
				}
				return !inSomeRandom() && methods.calc.distanceTo(walkingPath.getEnd()) < 5;
			case TELEPORT:
				return teleport != null && teleport.preform();
		}
		return false;
	}

	public Teleport getTeleport() {
		return teleport;
	}

	public RSTile[] getPath() {
		return path;
	}

	private boolean inSomeRandom() {
		if (methods.bot.disableRandoms) {
			return false;
		}
		for (final Random random : methods.bot.getScriptHandler().getRandoms()) {
			if (random.isEnabled() && !(methods.bot.disableAutoLogin && random instanceof LoginBot)) {
				if (random.activateCondition()) {
					return true;
				}
			}
		}
		return false;
	}
}
