package org.rsbot.event.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Iterator;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.internal.wrappers.TileData;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.Web;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

/**
 * Draws the web.
 * 
 * @author Timer
 */
public class DrawWeb implements PaintListener {
	private final MethodContext ctx;

	public DrawWeb(final Bot bot) {
		ctx = bot.getMethodContext();
	}

	@Override
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
		final Iterator<Map.Entry<Short[], Integer>> rs = Web.rs_map.entrySet().iterator();
		while (rs.hasNext()) {
			final Map.Entry<Short[], Integer> e = rs.next();
			final Short[] tile = e.getKey();
			final RSTile t = new RSTile(tile[0], tile[1], tile[2]);
			final int key = e.getValue();
			if (t.getZ() == plane && ctx.calc.distanceBetween(t, oT) < 105) {
				render.setColor(TileData.Questionable(key) ? Color.yellow
						: TileData.Special(key) ? Color.cyan : Color.red);
				final Point p = tileToMap(t, oT);
				render.drawLine(p.x, p.y, p.x, p.y);
			}
		}
	}

	/**
	 * Calculates a point to the minimap.
	 * 
	 * @param tile
	 *            The tile to calculate.
	 * @param player
	 *            Your player.
	 * @return The point of the tile.
	 */
	private Point tileToMap(final RSTile tile, final RSTile player) {
		final double minimapAngle = -1 * Math.toRadians(ctx.camera.getAngle());
		final int x = (tile.getX() - player.getX()) * 4 - 2;
		final int y = (player.getY() - tile.getY()) * 4 - 2;
		return new Point((int) Math.round(x * Math.cos(minimapAngle) + y
				* Math.sin(minimapAngle) + 628), (int) Math.round(y
				* Math.cos(minimapAngle) - x * Math.sin(minimapAngle) + 87));
	}
}
