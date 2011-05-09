package org.rsbot.event.impl;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.internal.wrappers.TileFlags;
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
	 * @param tile   The tile to calculate.
	 * @param player Your player.
	 * @return The point of the tile.
	 */
	private Point tileToMap(final RSTile tile, final RSPlayer player) {
		final double minimapAngle = -1 * Math.toRadians(ctx.camera.getAngle());
		final int x = (tile.getX() - player.getLocation().getX()) * 4 - 2;
		final int y = (player.getLocation().getY() - tile.getY()) * 4 - 2;
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
		final int plane = ctx.game.getPlane();
		final Iterator<Map.Entry<RSTile, TileFlags>> rs = Web.map.entrySet().iterator();
		while (rs.hasNext()) {
			final TileFlags t = rs.next().getValue();
			if (t.getZ() == plane) {
				render.setColor(t.isQuestionable() ? Color.yellow : t.isWater() ? Color.cyan : Color.red);
				final Point p = tileToMap(t, player);
				render.drawLine(p.x, p.y, p.x, p.y);
			}
		}
	}
}