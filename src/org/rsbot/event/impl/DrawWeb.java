package org.rsbot.event.impl;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.Web;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

import java.awt.*;
import java.util.Iterator;
import java.util.Map;

/**
 * Draws the web.
 *
 * @author Timer
 */
public class DrawWeb implements PaintListener {
	private final MethodContext ctx;

	/**
	 * Calculates a point to the minimap.
	 *
	 * @param tile     The tile to calculate.
	 * @param baseTile Your baseTile.
	 * @return The point of the tile.
	 */
	private Point tileToMap(final RSTile tile, final RSTile baseTile) {
		final double minimapAngle = -1 * Math.toRadians(ctx.camera.getAngle());
		final int x = (tile.getX() - baseTile.getX()) * 4 - 2;
		final int y = (baseTile.getY() - tile.getY()) * 4 - 2;
		return new Point((int) Math.round(x * Math.cos(minimapAngle) + y * Math.sin(minimapAngle) + 628), (int) Math.round(y * Math.cos(minimapAngle) - x * Math.sin(minimapAngle) + 87));
	}

	public DrawWeb(final Bot bot) {
		ctx = bot.getMethodContext();
	}

	public void onRepaint(final Graphics render) {
		if (!ctx.game.isLoggedIn()) {
			return;
		}
		final RSPlayer player = ctx.players.getMyPlayer();
		if (player == null) {
			return;
		}
		final RSTile oT = player.getLocation();
		final int plane = ctx.game.getPlane();
		final Iterator<Map.Entry<RSTile, Integer>> rs = Web.rs_map.entrySet().iterator();
		while (rs.hasNext()) {
			final Map.Entry<RSTile, Integer> e = rs.next();
			final RSTile tile = e.getKey();
			final int key = e.getValue();
			if (tile.getZ() == plane && ctx.calc.distanceBetween(tile, oT) < 105) {
				render.setColor(RSTile.Questionable(key) ? Color.yellow : RSTile.Special(key) ? Color.cyan : Color.red);
				final Point p = tileToMap(tile, oT);
				render.drawLine(p.x, p.y, p.x, p.y);
			}
		}
	}
}