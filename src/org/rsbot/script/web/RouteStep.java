package org.rsbot.script.web;

import org.rsbot.script.Random;
import org.rsbot.script.Script;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.MethodProvider;
import org.rsbot.script.randoms.LoginBot;
import org.rsbot.script.wrappers.RSPath;
import org.rsbot.script.wrappers.RSTile;

import java.util.Collections;

public class RouteStep extends MethodProvider {
	private final Type type;
	private RSTile[] path = null;
	private RSPath rspath = null;
	private Teleport teleport = null;
	private int loops = 0;

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
			this.path = new RSTile[]{(RSTile) step};
		} else {
			throw new IllegalArgumentException("Step is of an invalid type!");
		}
	}

	public boolean execute() {
		try {
			for (final Script checkScript : methods.bot.getScriptHandler().getRunningScripts().values()) {
				if (!checkScript.isActive() || !checkScript.isRunning() || checkScript.isPaused()) {
					return false;
				}
			}
			switch (type) {
				case PATH:
					update();
					while (!inSomeRandom()) {
						loops++;
						try {
							if (!rspath.traverse() || methods.calc.distanceTo(rspath.getEnd()) < 5) {
								break;
							}
						} catch (NullPointerException npe) {
							npe.printStackTrace();
							return false;
						}
						for (final Script checkScript : methods.bot.getScriptHandler().getRunningScripts().values()) {
							if (!checkScript.isActive() || !checkScript.isRunning() || checkScript.isPaused()) {
								return false;
							}
						}
						sleep(random(50, 150));
						if (loops > (methods.bot.disableRendering ? 1 : 0)) {
							update();
							loops = 0;
						}
					}
					return !inSomeRandom() && methods.calc.distanceTo(rspath.getEnd()) < 5;
				case TELEPORT:
					return !inSomeRandom() && teleport != null && teleport.perform();
			}
		} catch (Exception e) {
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

	private void update() {
		if (type.equals(Type.TELEPORT) || path == null) {
			return;
		}
		RSTile[] nodes = methods.web.generateNodePath(path[0], path[path.length - 1]);
		if (nodes != null) {
			path = nodes;
			rspath = methods.walking.newTilePath(path);
		}
	}
}
