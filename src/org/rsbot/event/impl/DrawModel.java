package org.rsbot.event.impl;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSModel;
import org.rsbot.script.wrappers.RSObject;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

/**
 * @author Kosaki
 */
public class DrawModel implements PaintListener, MouseListener {
	private static final HashMap<RSObject.Type, Color> color_map = new HashMap<RSObject.Type, Color>();

	static {
		color_map.put(RSObject.Type.BOUNDARY, Color.BLACK);
		color_map.put(RSObject.Type.FLOOR_DECORATION, Color.YELLOW);
		color_map.put(RSObject.Type.INTERACTABLE, Color.WHITE);
		color_map.put(RSObject.Type.WALL_DECORATION, Color.GRAY);
	}

	private static final String[] OPTIONS = {"Objects", "Players", "NPCs", "Piles"};
	private static boolean[] enabled = {true, true, true, true};

	private final MethodContext ctx;

	public DrawModel(final Bot bot) {
		ctx = bot.getMethodContext();
	}

	public void onRepaint(final Graphics render) {
		drawRect(render);
		if (enabled[0]) {
			for (final org.rsbot.script.wrappers.RSObject o : ctx.objects.getAll()) {
				final RSModel model = o.getModel();
				if (model != null) {
					render.setColor(color_map.get(o.getType()));
					model.drawWireFrame(render);
					render.setColor(Color.GREEN);
					final Point p = model.getPoint();
					render.fillOval(p.x - 1, p.y - 1, 2, 2);
				}
			}
		}
		if (enabled[1]) {
			for (final org.rsbot.script.wrappers.RSCharacter c : ctx.players.getAll()) {
				final RSModel model = c.getModel();
				if (model != null) {
					render.setColor(Color.RED);
					model.drawWireFrame(render);
				}
			}
		}
		if (enabled[2]) {
			for (final org.rsbot.script.wrappers.RSCharacter c : ctx.npcs.getAll()) {
				final RSModel model = c.getModel();
				if (model != null) {
					render.setColor(Color.MAGENTA);
					model.drawWireFrame(render);
				}
			}
		}
		if (enabled[3]) {
			for (final RSGroundItem item : ctx.groundItems.getAll()) {
				final RSModel model = item.getModel();
				if (model != null) {
					render.setColor(Color.CYAN);
					model.drawWireFrame(render);
				}
			}
		}
	}

	public final void drawRect(final Graphics render) {
		final Color j = Color.BLACK;
		final Color w = Color.WHITE;
		for (int i = 0; i < OPTIONS.length; i++) {
			final int alpha = 150;
			render.setColor(new Color(j.getRed(), j.getGreen(), j.getBlue(), alpha));
			if (enabled[i]) {
				render.setColor(new Color(w.getRed(), w.getGreen(), w.getBlue(), alpha));
			}
			render.fillRect(90 + 80 * i, 3, 80, 12);
			render.setColor(Color.white);
			if (enabled[i]) {
				render.setColor(Color.BLACK);
			}
			render.drawString(OPTIONS[i], 90 + 80 * i + 10, 13);
			render.setColor(Color.black);
			render.drawRect(90 + 80 * i, 3, 80, 12);
		}
	}

	public void mouseClicked(final MouseEvent e) {
		for (int i = 0; i < OPTIONS.length; i++) {
			final Rectangle rect = new Rectangle(90 + 80 * i, 3, 80, 12);
			if (rect.contains(e.getPoint())) {
				enabled[i] = !enabled[i];
				e.consume();
				return;
			}
		}
	}

	public void mouseEntered(final MouseEvent arg0) {

	}

	public void mouseExited(final MouseEvent arg0) {

	}

	public void mousePressed(final MouseEvent arg0) {

	}

	public void mouseReleased(final MouseEvent arg0) {

	}
}
