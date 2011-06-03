package org.rsbot.script.paint.methods;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.LinkedList;

import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.paint.PaintHandler;
import org.rsbot.script.paint.PaintProvider;

public class DrawMouse extends PaintProvider implements PaintHandler {

	public DrawMouse(MethodContext ctx) {
		super(ctx);
	}

	private final LinkedList<MouseSquarePathPoint> mouseSquarePath = new LinkedList<MouseSquarePathPoint>();
	private final LinkedList<MouseCirclePathPoint> mouseCirclePath = new LinkedList<MouseCirclePathPoint>();
	private final LinkedList<MousePathPoint> mousePath = new LinkedList<MousePathPoint>();
	private final LinkedList<MouseClick> mouseClick = new LinkedList<MouseClick>();
	private final LinkedList<MousePicPoint> mousePic = new LinkedList<MousePicPoint>();

	private class MousePathPoint extends Point {
		private static final long serialVersionUID = 1L;

		private final int toColor(final double d) {
			return Math.min(255, Math.max(0, (int) d));
		}

		private final long finishTime;
		private final double lastingTime;

		public MousePathPoint(final int x, final int y, final int lastingTime) {
			super(x, y);
			this.lastingTime = lastingTime;
			finishTime = System.currentTimeMillis() + lastingTime;
		}

		public boolean isUp() {
			return System.currentTimeMillis() > finishTime;
		}

		public double toTime(final double d) {
			return d * (finishTime - System.currentTimeMillis()) / lastingTime;
		}
	}

	private class MousePicPoint extends Point {
		private static final long serialVersionUID = 1L;
		private final long finishTime;

		public MousePicPoint(final int x, final int y, final int lastingTime) {
			super(x, y);
			finishTime = System.currentTimeMillis() + lastingTime;
		}

		public boolean isUp() {
			return System.currentTimeMillis() > finishTime;
		}
	}

	private class MouseCirclePathPoint extends Point {
		private static final long serialVersionUID = 1L;

		private int toColor(final double d) {
			return Math.min(255, Math.max(0, (int) d));
		}

		private final long finishTime;
		private final double lastingTime;

		public MouseCirclePathPoint(final int x, final int y,
				final int lastingTime) {
			super(x, y);
			this.lastingTime = lastingTime;
			finishTime = System.currentTimeMillis() + lastingTime;
		}

		public boolean isUp() {
			return System.currentTimeMillis() > finishTime;
		}

		public double toTime(final double d) {
			return d * (finishTime - System.currentTimeMillis()) / lastingTime;
		}
	}

	private class MouseSquarePathPoint extends Point {
		private static final long serialVersionUID = 1L;

		private int toColor(final double d) {
			return Math.min(255, Math.max(0, (int) d));
		}

		private final long finishTime;
		private final double lastingTime;

		public MouseSquarePathPoint(final int x, final int y,
				final int lastingTime) {
			super(x, y);
			this.lastingTime = lastingTime;
			finishTime = System.currentTimeMillis() + lastingTime;
		}

		public boolean isUp() {
			return System.currentTimeMillis() > finishTime;
		}

		public double toTime(final double d) {
			return d * (finishTime - System.currentTimeMillis()) / lastingTime;
		}
	}

	private class MouseClick extends Point {
		private static final long serialVersionUID = 1L;

		private int toColor(final double d) {
			return Math.min(255, Math.max(0, (int) d));
		}

		private final long finishTime;
		private double lastingTime;

		public MouseClick(final int x, final int y, final int lastingTime) {
			super(x, y);
			finishTime = System.currentTimeMillis() + lastingTime;
		}

		public boolean isUp() {
			return System.currentTimeMillis() > finishTime;
		}

		public double toTime() {
			return 256 * (finishTime - System.currentTimeMillis())
					/ lastingTime;
		}
	}

	public void drawMouse(final Color color, final int diameter,
			final Boolean click, final int lastingTime) {
		final Point m = methods.mouse.getLocation();
		final Point p = methods.mouse.getPressLocation();
		g().setColor(color);
		g().drawOval(m.x - 1, m.y - 1, 2, 2);
		g().drawOval(m.x - diameter / 2,
				m.y - diameter / 2, diameter, diameter);
		if (click) {
			while (!mouseClick.isEmpty() && mouseClick.peek().isUp()) {
				mouseClick.remove();
			}
			final MouseClick click1 = new MouseClick(p.x, p.y, lastingTime);
			if (mouseClick.isEmpty() || !mouseClick.getLast().equals(click1)) {
				mouseClick.add(click1);
			}
			MouseClick lastPoint = null;
			for (final MouseClick a : mouseClick) {
				if (lastPoint != null) {
					g().setFont(
							new Font("Airal", 0, 15));
					g().setColor(
							new Color(color.getRed(), color.getGreen(), color
									.getBlue(), a.toColor(a.toTime())));
					g().drawString("click", a.x - 13,
							a.y - diameter / 2 - 3);
				}
				lastPoint = a;
			}
		}
	}

	public void drawMouseCrosshair(final Color color) {
		final int gW = methods.game.getWidth();
		final int gH = methods.game.getHeight();
		final Point localPoint = methods.mouse.getLocation();
		g().setColor(color);
		g().drawLine(0, localPoint.y, gW, localPoint.y);
		g().drawLine(localPoint.x, 0, localPoint.x, gH);
	}

	public void drawMouseLine(final Color color, final int lastingTime) {
		final Point m = methods.mouse.getLocation();
		while (!mousePath.isEmpty() && mousePath.peek().isUp()) {
			mousePath.remove();
		}
		final MousePathPoint mp = new MousePathPoint(m.x, m.y, lastingTime);
		if (mousePath.isEmpty() || !mousePath.getLast().equals(mp)) {
			mousePath.add(mp);
		}
		MousePathPoint lastPoint = null;
		for (final MousePathPoint a : mousePath) {
			if (lastPoint != null) {
				g().setColor(
						new Color(color.getRed(), color.getGreen(), color
								.getBlue(), a.toColor(a.toTime(256))));
				g().drawLine(a.x, a.y, lastPoint.x,
						lastPoint.y);
			}
			lastPoint = a;
		}
	}

	public void drawCircleMouseLine(final Color color, final int lastingTime,
			final int diameter) {
		final Point m = methods.mouse.getLocation();
		while (!mouseCirclePath.isEmpty() && mouseCirclePath.peek().isUp()) {
			mouseCirclePath.remove();
		}
		final MouseCirclePathPoint mp = new MouseCirclePathPoint(m.x, m.y,
				lastingTime);
		if (mouseCirclePath.isEmpty() || !mouseCirclePath.getLast().equals(mp)) {
			mouseCirclePath.add(mp);
		}
		MouseCirclePathPoint lastPoint = null;
		for (final MouseCirclePathPoint a : mouseCirclePath) {
			if (lastPoint != null) {
				g().setColor(
						new Color(color.getRed(), color.getGreen(), color
								.getBlue(), a.toColor(a.toTime(256))));
				g().fillOval(
						a.x - a.toColor(a.toTime(diameter)) / 2,
						a.y - a.toColor(a.toTime(diameter)) / 2,
						a.toColor(a.toTime(diameter)),
						a.toColor(a.toTime(diameter)));
				g().setColor(
						new Color(0, 0, 0, a.toColor(a.toTime(256))));
				g().drawOval(
						a.x - a.toColor(a.toTime(diameter)) / 2,
						a.y - a.toColor(a.toTime(diameter)) / 2,
						a.toColor(a.toTime(diameter)),
						a.toColor(a.toTime(diameter)));
			}
			lastPoint = a;
		}
	}

	public void drawPicMouseLine(final Image image, final int lastingTime) {
		final int h = image.getHeight(null);
		final int w = image.getWidth(null);
		final Point m = methods.mouse.getLocation();
		while (!mousePic.isEmpty() && mousePic.peek().isUp()) {
			mousePic.remove();
		}
		final MousePicPoint mp = new MousePicPoint(m.x, m.y, lastingTime);
		if (mousePic.isEmpty() || !mousePic.getLast().equals(mp)) {
			mousePic.add(mp);
		}
		MousePicPoint lastPoint = null;
		for (final MousePicPoint a : mousePic) {
			if (lastPoint != null) {
				g().drawImage(image, a.x - w / 2,
						a.y - h / 2, null);
			}
			lastPoint = a;
		}
	}

	public void drawPicMouseLine(final Image image, final int lastingTime,
			final int offsetX, final int offsetY) {
		final Point m = methods.mouse.getLocation();
		while (!mousePic.isEmpty() && mousePic.peek().isUp()) {
			mousePic.remove();
		}
		final MousePicPoint mp = new MousePicPoint(m.x, m.y, lastingTime);
		if (mousePic.isEmpty() || !mousePic.getLast().equals(mp)) {
			mousePic.add(mp);
		}
		MousePicPoint lastPoint = null;
		for (final MousePicPoint a : mousePic) {
			if (lastPoint != null) {
				g().drawImage(image, a.x - offsetX,
						a.y - offsetY, null);
			}
			lastPoint = a;
		}
	}

	public void drawSquareMouseLine(final Color color, final int lastingTime,
			final int sideLength) {
		final Point m = methods.mouse.getLocation();
		while (!mouseSquarePath.isEmpty() && mouseSquarePath.peek().isUp()) {
			mouseSquarePath.remove();
		}
		final MouseSquarePathPoint mp = new MouseSquarePathPoint(m.x, m.y,
				lastingTime);
		if (mouseSquarePath.isEmpty() || !mouseSquarePath.getLast().equals(mp)) {
			mouseSquarePath.add(mp);
		}
		MouseSquarePathPoint lastPoint = null;
		for (final MouseSquarePathPoint a : mouseSquarePath) {
			if (lastPoint != null) {
				g().setColor(
						new Color(color.getRed(), color.getGreen(), color
								.getBlue(), a.toColor(a.toTime(256))));
				g().fillRect(
						a.x - a.toColor(a.toTime(sideLength)) / 2,
						a.y - a.toColor(a.toTime(sideLength)) / 2,
						a.toColor(a.toTime(sideLength)),
						a.toColor(a.toTime(sideLength)));
				g().setColor(
						new Color(0, 0, 0, a.toColor(a.toTime(256))));
				g().drawRect(
						a.x - a.toColor(a.toTime(sideLength)) / 2,
						a.y - a.toColor(a.toTime(sideLength)) / 2,
						a.toColor(a.toTime(sideLength)),
						a.toColor(a.toTime(sideLength)));
			}
			lastPoint = a;
		}
	}

	public Graphics2D g() {
		return methods.bot.get2DGraphics();
	}
}
