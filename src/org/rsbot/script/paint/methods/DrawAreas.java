package org.rsbot.script.paint.methods;

import java.awt.Color;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.paint.PaintProvider;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSTile;

public class DrawAreas extends PaintProvider {


	public DrawAreas(MethodContext ctx) {
		super(ctx);
	}

	public void drawRSAreaOnScreen(final RSArea area, final Color color,
			final int alpha) {
		final RSTile[] array = area.getTileArray();
		for (final RSTile tile : array) {
			if (tile == null) {
				continue;
			}
			if (!methods.calc.tileOnScreen(tile)) {
				continue;
			}
			methods.paint.tiles.drawTileOnScreen(tile, color, alpha);
		}
	}

	public void drawRSAreaMM(final RSArea area, final Color color,
			final int alpha) {
		final RSTile[] array = area.getTileArray();
		for (final RSTile tile : array) {
			if (tile == null) {
				continue;
			}
			methods.paint.tiles.drawTileMM(tile, color, alpha);
		}
	}
}
