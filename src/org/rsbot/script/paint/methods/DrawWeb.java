package org.rsbot.script.paint.methods;

import java.awt.Color;
import java.util.Iterator;
import java.util.Map;

import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.Web;
import org.rsbot.script.paint.PaintProvider;
import org.rsbot.script.wrappers.RSTile;

public class DrawWeb extends PaintProvider {

	public DrawWeb(MethodContext ctx) {
		super(ctx);
	}

	public void drawWebOnScreen(final int alpha) {
		final Iterator<Map.Entry<RSTile, Integer>> rs = Web.rs_map.entrySet()
				.iterator();
		while (rs.hasNext()) {
			final Map.Entry<RSTile, Integer> e = rs.next();
			final RSTile tile = e.getKey();
			final int key = e.getValue();
			methods.paint.tiles.drawTileOnScreen(
					tile,
					RSTile.Questionable(key) ? Color.yellow : RSTile
							.Special(key) ? Color.cyan : Color.red, alpha);
		}
	}

	public void drawWebMM(final int alpha) {
		final Iterator<Map.Entry<RSTile, Integer>> rs = Web.rs_map.entrySet()
				.iterator();
		while (rs.hasNext()) {
			final Map.Entry<RSTile, Integer> e = rs.next();
			final RSTile tile = e.getKey();
			final int key = e.getValue();
			methods.paint.tiles.drawTileMM(
					tile,
					RSTile.Questionable(key) ? Color.yellow : RSTile
							.Special(key) ? Color.cyan : Color.red, alpha);
		}
	}

}
