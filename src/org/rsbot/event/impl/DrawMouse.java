package org.rsbot.event.impl;

import org.rsbot.bot.Bot;
import org.rsbot.client.Client;
import org.rsbot.client.input.Mouse;
import org.rsbot.event.listeners.PaintListener;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DrawMouse implements PaintListener {
	private final Client client;
	private final List<Cross> clicks = new LinkedList<Cross>();
	private final Object lock = new Object();
	private final MouseTrail trail = new MouseTrail();
	private final RenderingHints antialiasing = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	public DrawMouse(final Bot bot) {
		client = bot.getClient();
	}

	@Override
	public void onRepaint(final Graphics render) {
		final Mouse mouse = client.getMouse();
		if (mouse != null) {
			final Point location = new Point(mouse.getX(), mouse.getY());
			final Graphics2D g = (Graphics2D) render.create();
			final Graphics2D gg = (Graphics2D) render.create();
			trail.draw((Graphics2D) render, Color.red);
			g.setColor(System.currentTimeMillis() - mouse.getPressTime() > 400 ? Color.green.brighter() : Color.red);
			g.rotate(Math.toRadians(getRot()), location.x, location.y);
			g.drawLine(location.x, location.y - 6, location.x, location.y + 6);
			g.drawLine(location.x - 6, location.y, location.x + 6, location.y);
			if (mouse.isPressed() && (clicks.size() > 0 && clicks.get(clicks.size() - 1).getAge() > 100 && clicks.get(clicks.size() - 1).getStart() != mouse.getPressTime() || clicks.size() == 0)) {
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

	private void drawPoint(final Point location, final double rot, final Graphics2D g, final int al) {
		final Graphics2D g1 = (Graphics2D) g.create();
		g1.setRenderingHints(antialiasing);
		g1.setColor(new Color(255, 0, 0, al));
		g1.rotate(rot, location.x, location.y);
		g1.drawLine(location.x, location.y - 5, location.x, location.y + 5);
		g1.drawLine(location.x - 5, location.y, location.x + 5, location.y);
	}

	private double getRot() {
		 return (System.currentTimeMillis() % (360 * 10)) / 10;
	}

	/**
	 * @author Baheer (Doout).
	 */
	public class MouseTrail {

		private int lifeTime = 1500;
		private LinkedList<Particle> trail = new LinkedList<Particle>();
		private Point prev = new Point(-1, -1);

		private void getLocation() {
			Point m = new Point(client.getMouse().getX(), client.getMouse().getY());
			if (accept(m)) {
				prev = m;
				trail.add(new Particle(System.currentTimeMillis(), m));
			}
		}

		private boolean accept(Point p) {
			if (p.equals(prev)) {
				return false;
			}
			for (Particle t : trail) {
				if (t.getLocation().equals(p)) {
					return false;
				}
			}
			return true;
		}

		public void draw(Graphics2D g, Color c) {
			getLocation();
			g.setRenderingHints(antialiasing);
			if (!trail.isEmpty())
				for (Particle particle : trail) {
					int i = (int) (lifeTime - particle.getAge()) / 100;
					if (i >= 2) {
						Rectangle rect = new Rectangle(particle.getLocation().x - i / 2,
								particle.getLocation().y - i / 2, i, i);
						Paint prevPaint = g.getPaint();
						RadialGradientPaint gradPaint = new RadialGradientPaint(
								new Point2D.Double(rect.x + rect.width / 2.0D,
										rect.y + rect.height / 2.0D),
								(float) (rect.getWidth() / 2.0D),
								new float[]{0.0F, 1.0F}, new Color[]{
										new Color(c.getRed(), c.getGreen(), c.getBlue(), 40),
										new Color(0.0F, 0.0F, 0.0F, 0.4F)});
						g.setPaint(gradPaint);
						g.fillRoundRect(rect.x, rect.y,
								rect.width, rect.height,
								rect.width, rect.height);
						g.setPaint(prevPaint);
					}
				}
			if (!trail.isEmpty()) {
				if (trail.getFirst().getAge() > 1000) {
					trail.removeFirst();
				}
			}
		}

	}

	private class Cross extends Particle {
		private final long time, st;
		private final Point location;
		private final double rot;

		public Cross(final long lifetime, final long st, final Point loc, final double rot) {
			super(System.currentTimeMillis(), loc);
			time = System.currentTimeMillis() + lifetime;
			location = loc;
			this.rot = rot;
			this.st = st;
		}

		public long getStart() {
			return st;
		}

		@Override
		public long getAge() {
			return time - System.currentTimeMillis();
		}

		public int getAlpha() {
			return Math.min(255, Math.max(0, (int) (256.0D * getAge() / 1500.0D)));
		}

		public boolean handle() {
			return System.currentTimeMillis() <= time;
		}

		public double getRot() {
			return rot;
		}

		@Override
		public Point getLocation() {
			return location;
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof Cross) {
				final Cross oo = (Cross) o;
				return oo.location.equals(location);
			}
			return false;
		}
	}

	private class Particle {

		private long time;
		private Point location;

		public Particle(long time, Point loc) {
			this.time = time;
			location = loc;
		}

		public long getAge() {
			return System.currentTimeMillis() - time;
		}

		public Point getLocation() {
			return location;
		}

	}
}