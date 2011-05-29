package org.rsbot.script.web;

import java.util.Collections;

import org.rsbot.script.Random;
import org.rsbot.script.Script;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.MethodProvider;
import org.rsbot.script.randoms.LoginBot;
import org.rsbot.script.wrappers.RSPath;
import org.rsbot.script.wrappers.RSTile;

public class RouteStep extends MethodProvider {
	public static enum Type {
		PATH, TELEPORT
	}

	private final Type type;
	private RSTile[] path = null;
	private RSPath rspath = null;

	private Teleport teleport = null;

	public RouteStep(final MethodContext ctx, final Object step) {
		super(ctx);
		if (step instanceof Teleport) {
			type = Type.TELEPORT;
			teleport = (Teleport) step;
		} else if (step instanceof RSTile[]) {
			type = Type.PATH;
			path = (RSTile[]) step;
		} else if (step instanceof RSTile) {
			type = Type.PATH;
			path = new RSTile[] { (RSTile) step };
		} else {
			throw new IllegalArgumentException("Step is of an invalid type!");
		}
	}

	public boolean execute() {
		try {
			for (final Script checkScript : Collections.unmodifiableCollection(methods.bot.getScriptHandler().getRunningScripts().values())) {
				if (!checkScript.isActive() || !checkScript.isRunning()) {
					return false;
				}
				if (checkScript.isPaused()) {
					sleep(500);
					return true;
				}
			}
			if (methods.bot.getScriptHandler().getRunningScripts().size() == 0) {
				return false;
			}
			switch (type) {
			case PATH:
				if (path == null || inSomeRandom()) {// Recalculation says path
														// is a no-go (or in a
														// random).
					return false;
				}
				if (rspath == null) {
					rspath = methods.walking.newTilePath(path);
				}
				if (methods.calc.distanceTo(rspath.getEnd()) < 5) {
					rspath = null;
					path = null;
					return true;
				}
				sleep(random(50, 150));
				return !inSomeRandom() && rspath.traverse();
			case TELEPORT:
				if (inSomeRandom()) {
					return false;
				}
				if (teleport != null && teleport.perform()) {
					teleport = null;
					return true;
				}
				return false;
			}
		} catch (final Exception e) {
		}
		return false;
	}

	public boolean finished() {
		return path == null && teleport == null;
	}

	public RSTile[] getPath() {
		return path;
	}

	public Teleport getTeleport() {
		return teleport;
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

	void update() {
		if (path != null && path.length > 1) {
			final RSTile startTile = path[0];
			final RSTile endTile = path[path.length - 1];
			path = methods.web.generateTilePath(startTile, endTile);
			rspath = null;
		}
	}
}
