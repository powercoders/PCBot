package org.rsbot.event.impl;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.wrappers.RSModel;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class ModelTest implements PaintListener, MouseMotionListener {
	private Point mousePoint = new Point(-1, -1);
	private final MethodContext ctx;

	public ModelTest(final Bot bot) {
		ctx = bot.getMethodContext();
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		mousePoint = e.getPoint();
		e.consume();
	}

	public void onRepaint(Graphics render) {
		render.setColor(Color.green);
		for (final org.rsbot.script.wrappers.RSObject o : ctx.objects.getAll()) {
			final RSModel model = o.getModel();
			if (model != null && model.contains(mousePoint)) {
				model.drawWireFrame(render);
				render.setColor(Color.black);
				final Point p = model.getPoint();
				render.fillOval(p.x - 1, p.y - 1, 2, 2);
			}
		}
	}
}
