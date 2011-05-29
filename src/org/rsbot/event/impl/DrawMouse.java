package org.rsbot.event.impl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.rsbot.bot.Bot;
import org.rsbot.client.Client;
import org.rsbot.client.input.Mouse;
import org.rsbot.event.listeners.PaintListener;

public class DrawMouse implements PaintListener {
	private class Cross {
		private final long time, st;
		private final Point location;
		private final double rot;

		public Cross(final long lifetime, final long st, final Point loc,
				final double rot) {
			time = System.currentTimeMillis() + lifetime;
			location = loc;
			this.rot = rot;
			this.st = st;
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof Cross) {
				final Cross oo = (Cross) o;
				return oo.location.equals(location);
			}
			return false;
		}

		public long getAge() {
			return time - System.currentTimeMillis();
		}

		public int getAlpha() {
			return Math.min(255, Math.max(0, (int) (256.0D * getAge() / 1500.0D)));
		}

		public Point getLocation() {
			return location;
		}

		public double getRot() {
			return rot;
		}

		public long getStart() {
			return st;
		}

		public boolean handle() {
			return System.currentTimeMillis() <= time;
		}
	}

	private final Client client;
	private final List<Cross> clicks = new LinkedList<Cross>();

	private final Object lock = new Object();

	public DrawMouse(final Bot bot) {
		client = bot.getClient();
	}

	private void drawPoint(final Point location, final double rot,
			final Graphics2D g, final int al) {
		final Graphics2D g1 = (Graphics2D) g.create();
		g1.setColor(new Color(255, 0, 0, al));
		g1.rotate(rot, location.x, location.y);
		g1.drawLine(location.x, location.y - 5, location.x, location.y + 5);
		g1.drawLine(location.x - 5, location.y, location.x + 5, location.y);
	}

	private double getRot() {
		return System.currentTimeMillis() % 3600 / 10.0D;
	}

	@Override
	public void onRepaint(final Graphics render) {
		final Mouse mouse = client.getMouse();
		if (mouse != null) {
			final Point location = new Point(mouse.getX(), mouse.getY());
			final Graphics2D g = (Graphics2D) render.create();
			final Graphics2D gg = (Graphics2D) render.create();
			g.setColor(Color.GREEN);
			g.rotate(Math.toRadians(getRot()), location.x, location.y);
			g.drawLine(location.x, location.y - 5, location.x, location.y + 5);
			g.drawLine(location.x - 5, location.y, location.x + 5, location.y);
			if (mouse.isPressed()
					&& (clicks.size() > 0
							&& clicks.get(clicks.size() - 1).getAge() > 100
							&& clicks.get(clicks.size() - 1).getStart() != mouse.getPressTime() || clicks.size() == 0)) {
				final Cross newCross = new Cross(1500, mouse.getPressTime(), location, getRot());
				if (!clicks.contains(newCross)) {
					clicks.add(newCross);
				}
			}
			synchronized (lock) {
				final Iterator<Cross> clickIterator = clicks.listIterator();
				while (clickIterator.hasNext()) {
					final Cross toDraw = clickIterator.next();
					if (toDraw.handle()) {
						drawPoint(toDraw.getLocation(), toDraw.getRot(), gg, toDraw.getAlpha());
					} else {
						clicks.remove(toDraw);
					}
				}
			}
		}
	}
}
