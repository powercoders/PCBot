package org.rsbot.script.paint.methods;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.paint.PaintHandler;
import org.rsbot.script.paint.PaintProvider;
import org.rsbot.script.wrappers.RSModel;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;

public class FillModels extends PaintProvider implements PaintHandler {

	public FillModels(MethodContext ctx) {
		super(ctx);
	}

	public void fillModel(final RSModel model, final Color color,
			final int alpha) {
		if (model != null) {
			for (Polygon p : model.getTriangles()) {
				g().setColor(
						new Color(color.getRed(), color.getBlue(), color
								.getGreen(), alpha));
				g().fillPolygon(p);
			}
		}
	}

	public void fillObject(final RSObject object, final Color color,
			final int alpha) {
		if (object != null) {
			fillModel(object.getModel(), color, alpha);
		}
	}

	public void fillNpc(final RSNPC npc, final Color color, final int alpha) {
		if (npc != null) {
			fillModel(npc.getModel(), color, alpha);
		}
	}

	public void fillPlayer(final RSPlayer player, final Color color,
			final int alpha) {
		if (player != null) {
			fillModel(player.getModel(), color, alpha);
		}
	}

	public Graphics2D g() {
		return methods.bot.get2DGraphics();
	}
}
