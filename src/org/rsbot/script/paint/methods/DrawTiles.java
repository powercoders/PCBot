package org.rsbot.script.paint.methods;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.paint.PaintHandler;
import org.rsbot.script.paint.PaintProvider;
import org.rsbot.script.wrappers.RSTile;

public class DrawTiles extends PaintProvider implements PaintHandler {

	public DrawTiles(MethodContext ctx) {
		super(ctx);
	}

	/**
	 * Draws the RSTiles on tiles on the minimap.
	 * 
	 * @param tiles
	 *            The array of tiles to color.
	 * @param color
	 *            Color to color the tile.
	 * @author Fletch To 99
	 */
	public void drawTilesMM(final RSTile[] tiles, final Color color,
			final int alpha, final Graphics2D g) {
		for (final RSTile tile : tiles) {
			if (tile != null) {
				final int tX = tile.getX(), tY = tile.getY();
				final Point p1 = methods.calc
						.worldToMinimap(tX - 0.4, tY - 0.4);
				final Point p2 = methods.calc
						.worldToMinimap(tX - 0.4, tY + 0.4);
				final Point p3 = methods.calc
						.worldToMinimap(tX + 0.4, tY + 0.4);
				final Point p4 = methods.calc
						.worldToMinimap(tX + 0.4, tY - 0.4);
				if (p1.x != -1 && p2.x != -1 && p3.x != -1 && p4.x != -1) {
					final int[] allX = new int[] { p1.x, p2.x, p3.x, p4.x };
					final int[] allY = new int[] { p1.y, p2.y, p3.y, p4.y };
					g().setColor(
							new Color(color.getRed(), color.getGreen(), color
									.getBlue(), alpha));
					g().fillPolygon(allX, allY, 4);
				}
			}
		}
	}

	/**
	 * Draws an RSTile on the minimap.
	 * 
	 * @param tile
	 *            The tile to color.
	 * @param color
	 *            Color to color the model.
	 * @author Fletch To 99
	 */
	public void drawTileMM(final RSTile tile, final Color color, final int alpha) {
		if (tile != null) {
			final int tX = tile.getX(), tY = tile.getY();
			final Point p1 = methods.calc.worldToMinimap(tX - 0.4, tY - 0.4);
			final Point p2 = methods.calc.worldToMinimap(tX - 0.4, tY + 0.4);
			final Point p3 = methods.calc.worldToMinimap(tX + 0.4, tY + 0.4);
			final Point p4 = methods.calc.worldToMinimap(tX + 0.4, tY - 0.4);
			if (p1.x != -1 && p2.x != -1 && p3.x != -1 && p4.x != -1) {
				final int[] allX = new int[] { p1.x, p2.x, p3.x, p4.x };
				final int[] allY = new int[] { p1.y, p2.y, p3.y, p4.y };
				g().setColor(
						new Color(color.getRed(), color.getGreen(), color
								.getBlue(), alpha));
				g().fillPolygon(allX, allY, 4);
			}
		}
	}

	/**
	 * Draws an RSTile on the minimap
	 * 
	 * @param tiles
	 *            Tiles to draw
	 * @param color
	 *            Color to draw the tiles
	 * @author jtryba
	 */
	public void drawTilesOnScreen(final RSTile[] tiles, final Color color,
			final int alpha, final Graphics2D g) {
		if (tiles != null) {
			for (final RSTile tile : tiles) {
				drawTileOnScreen(tile, color, alpha);
			}
		}
	}

	/**
	 * Draws an RSTile on the screen
	 * 
	 * @param tile
	 *            Tile to draw
	 * @param color
	 *            Color to draw the tile
	 * @author Fletch to 99, jtryba
	 */
	public void drawTileOnScreen(final RSTile tile, final Color color,
			final int alpha) {
		final Point southwest = methods.calc.tileToScreen(tile, 0, 0, 0);
		final Point southeast = methods.calc.tileToScreen(tile, 1, 0, 0);
		final Point northwest = methods.calc.tileToScreen(tile, 0, 1, 0);
		final Point northeast = methods.calc.tileToScreen(tile, 1, 1, 0);
		if (methods.calc.pointOnScreen(southwest)
				&& methods.calc.pointOnScreen(southeast)
				&& methods.calc.pointOnScreen(northwest)
				&& methods.calc.pointOnScreen(northeast)) {
			g().setColor(Color.BLACK);
			g()
					.drawPolygon(
							new int[] { (int) northwest.getX(),
									(int) northeast.getX(),
									(int) southeast.getX(),
									(int) southwest.getX() },
							new int[] { (int) northwest.getY(),
									(int) northeast.getY(),
									(int) southeast.getY(),
									(int) southwest.getY() }, 4);
			g().setColor(
					new Color(color.getRed(), color.getGreen(),
							color.getBlue(), alpha));
			g()
					.fillPolygon(
							new int[] { (int) northwest.getX(),
									(int) northeast.getX(),
									(int) southeast.getX(),
									(int) southwest.getX() },
							new int[] { (int) northwest.getY(),
									(int) northeast.getY(),
									(int) southeast.getY(),
									(int) southwest.getY() }, 4);
		}
	}

	public Graphics2D g() {
		return methods.bot.get2DGraphics();
	}

}
