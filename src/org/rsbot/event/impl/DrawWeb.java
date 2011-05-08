package org.rsbot.event.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.script.wrappers.Web;
import org.rsbot.script.wrappers.WebMap;
import org.rsbot.script.wrappers.WebTile;

public class DrawWeb implements PaintListener {

	private final MethodContext ctx;

	public DrawWeb(final Bot bot) {
		ctx = bot.getMethodContext();
	}

	private Point tileToMap(final RSTile tile, final RSPlayer player) {
		final double minimapAngle = -1 * Math.toRadians(ctx.camera.getAngle());
		final int x = (tile.getX() - player.getLocation().getX()) * 4 - 2;
		final int y = (player.getLocation().getY() - tile.getY()) * 4 - 2;
		return new Point((int) Math.round(x * Math.cos(minimapAngle) + y * Math.sin(minimapAngle) + 628),
				(int) Math.round(y * Math.cos(minimapAngle) - x * Math.sin(minimapAngle) + 87));
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
		render.setColor(Color.white);
		final WebMap map = new Web(ctx, null, null).map();
		final WebTile[] webTiles = map.getTiles();
		for (final WebTile webTile : webTiles) {
			if (ctx.calc.distanceTo(webTile.tile()) < 100) {
				final Point p = tileToMap(webTile, player);
				for (final int l : webTile.connectingIndex()) {
					final Point pp = tileToMap(webTiles[l], player);
					render.drawLine(pp.x, pp.y, p.x, p.y);
				}
			}
		}
		render.setColor(Color.red);
		for (final WebTile webTile : map.getTiles()) {
			if (ctx.calc.distanceTo(webTile.tile()) < 100) {
				final Point p = tileToMap(webTile, player);
				render.fillRect(p.x - 2, p.y - 2, 4, 4);
			}
		}
	}
}
