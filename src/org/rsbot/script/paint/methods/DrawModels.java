package org.rsbot.script.paint.methods;

import java.awt.Color;
import java.awt.Graphics2D;

import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.paint.PaintHandler;
import org.rsbot.script.paint.PaintProvider;
import org.rsbot.script.wrappers.RSModel;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;

public class DrawModels extends PaintProvider implements PaintHandler {

	public DrawModels(MethodContext ctx) {
		super(ctx);
	}

	public void drawModel(final RSModel model, final Color color,
			final int alpha) {
		if (model != null) {
			g().setColor(new Color(color.getRed(), color.getBlue(), color
					.getGreen(), alpha));
			model.drawWireFrame(g());
		}
	}

	public void drawObject(final RSObject object, final Color color,
			final int alpha, final boolean drawModel) {
		if (object != null) {
			if (drawModel) {
				drawModel(object.getModel(), color, alpha);
			} else {
				methods.paint.tiles.drawTileOnScreen(object.getLocation(),
						color, alpha);
			}
			methods.paint.tiles.drawTileMM(object.getLocation(), color, alpha);
		}
	}

	public void drawNpc(final RSNPC npc, final Color color, final int alpha,
			final boolean drawModel) {
		if (npc != null) {
			if (drawModel) {
				drawModel(npc.getModel(), color, alpha);
			} else {
				methods.paint.tiles.drawTileOnScreen(npc.getLocation(), color,
						alpha);
			}
			methods.paint.tiles.drawTileMM(npc.getLocation(), color, alpha);
		}
	}

	public void drawPlayer(final RSPlayer player, final Color color,
			final int alpha, final boolean drawModel) {
		if (player != null) {
			if (drawModel) {
				drawModel(player.getModel(), color, alpha);
			} else {
				methods.paint.tiles.drawTileOnScreen(player.getLocation(),
						color, alpha);
			}
			methods.paint.tiles.drawTileMM(player.getLocation(), color, alpha);
		}
	}

	public Graphics2D g() {
		return methods.bot.get2DGraphics();
	}
}
