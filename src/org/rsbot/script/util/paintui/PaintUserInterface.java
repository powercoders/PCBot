package org.rsbot.script.util.paintui;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import org.rsbot.event.listeners.PaintListener;

public class PaintUserInterface implements MouseListener, MouseMotionListener,
KeyListener, PaintListener {
	private Point mouseLocation = new Point(0, 0);
	private PaintCaret caret = null;
	private boolean shouldRepaint = false;

	public PaintCaret getCaret() {
		return caret;
	}

	public static void main(final String[] args) {
		final PaintUserInterface p = new PaintUserInterface();
		final PaintCheckBox chk = new PaintCheckBox("Checkbox");
		chk.setLocation(100, 100);
		final PaintLabel lbl = new PaintLabel("Label");
		lbl.setLocation(100, 200);
		// lbl.setSize(200, 25);
		final PaintTextField field = new PaintTextField("Username");
		field.setLocation(100, 300);
		p.addChild(chk);
		p.addChild(lbl);
		p.addChild(field);
		final Frame fram = new Frame("Frame");
		fram.setSize(500, 500);
		fram.setLocation(0, 0);
		fram.setVisible(true);
		fram.addMouseListener(p);
		fram.addKeyListener(p);
		fram.addMouseMotionListener(p);
		fram.getGraphics();
		while (true) {
			p.onRepaint(fram.getGraphics());
		}
	}

	public PaintUserInterface() {
	}

	private final ArrayList<PaintComponent> children = new ArrayList<PaintComponent>();

	public void addChild(final PaintComponent comp) {
		comp.setParent(null);
		comp.setInterface(this);
		children.add(comp);
		shouldRepaint = true;
	}

	public Point getMouseLocation() {
		return mouseLocation;
	}

	public PaintComponent getChild(final int id) {
		return id >= 0 && id < children.size() ? children.get(id) : null;
	}

	@Override
	public void onRepaint(final Graphics render) {
		final Graphics2D g = (Graphics2D) render;
		if (shouldRepaint()) {
			for (final PaintComponent comp : children) {
				comp.onRepaint(g);
			}
		}
		if (caret != null) {
			g.setColor(Color.BLACK);
			caret.paint(g);
		}
	}

	@Override
	public void keyPressed(final KeyEvent e) {
		if (caret != null) {
			caret.doKeyEvent(e);
		}
	}

	@Override
	public void keyReleased(final KeyEvent e) {
	}

	@Override
	public void keyTyped(final KeyEvent e) {
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		for (final PaintComponent c : children) {
			c.mouseClicked(e);
		}
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
	}

	@Override
	public void mouseExited(final MouseEvent e) {
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		boolean foundCaret = false;
		for (final PaintComponent c : children) {
			if (c.getAbsoluteBounds().contains(e.getPoint())
					&& c instanceof PaintTextField) {
				if (caret == null || caret.field != c) {
					caret = new PaintCaret((PaintTextField) c);
					shouldRepaint = true;
				}
				if (e.isShiftDown()) {
					caret.moveDot(caret.getPosition(e.getPoint()));
				} else {
					caret.setDot(caret.getPosition(e.getPoint()));
				}
				foundCaret = true;
			}
			c.mousePressed(e);
		}
		if (!foundCaret) {
			caret = null;
		}
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		for (final PaintComponent c : children) {
			c.mouseReleased(e);
		}
	}

	@Override
	public void mouseDragged(final MouseEvent e) {
		for (final PaintComponent c : children) {
			c.mouseDragged(e);
		}
		if (caret != null && caret.field != null
				&& caret.field.getAbsoluteBounds().contains(e.getPoint())) {
			caret.moveDot(caret.getPosition(e.getPoint()));
		}
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		mouseLocation = e.getPoint();
		for (final PaintComponent c : children) {
			c.mouseMoved(e);
		}
	}

	public boolean shouldRepaint() {
		if (shouldRepaint) {
			return true;
		}
		for (final PaintComponent c : children) {
			if (c.shouldRepaint()) {
				return true;
			}
		}
		return false;
	}
}
