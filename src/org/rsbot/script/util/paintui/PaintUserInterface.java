package org.rsbot.script.util.paintui;

import org.rsbot.event.listeners.PaintListener;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class PaintUserInterface implements MouseListener, MouseMotionListener, KeyListener, PaintListener {
	private Point mouseLocation = new Point(0, 0);
	private PaintCaret caret = null;
	private boolean shouldRepaint = false;

	public PaintCaret getCaret() {
		return caret;
	}

	public PaintUserInterface() {
	}

	private ArrayList<PaintComponent> children = new ArrayList<PaintComponent>();

	public void addChild(PaintComponent comp) {
		comp.setParent(null);
		comp.setInterface(this);
		children.add(comp);
		shouldRepaint = true;
	}

	public Point getMouseLocation() {
		return mouseLocation;
	}

	public PaintComponent getChild(int id) {
		return (id >= 0 && id < children.size()) ? children.get(id) : null;
	}

	public void onRepaint(Graphics render) {
		Graphics2D g = (Graphics2D) render;
		for (PaintComponent comp : children) {
			comp.onRepaint(g);
		}
		if (caret != null) {
			g.setColor(Color.BLACK);
			caret.paint(g);
		}
	}

	public void keyPressed(KeyEvent e) {
		if (caret != null) {
			caret.doKeyEvent(e);
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void mouseClicked(MouseEvent e) {
		for (PaintComponent c : children) {
			c.mouseClicked(e);
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		boolean foundCaret = false;
		for (PaintComponent c : children) {
			if (c.getAbsoluteBounds().contains(e.getPoint()) && c instanceof PaintTextField) {
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

	public void mouseReleased(MouseEvent e) {
		for (PaintComponent c : children) {
			c.mouseReleased(e);
		}
	}

	public void mouseDragged(MouseEvent e) {
		for (PaintComponent c : children) {
			c.mouseDragged(e);
		}
		if (caret != null && caret.field != null && caret.field.getAbsoluteBounds().contains(e.getPoint())) {
			caret.moveDot(caret.getPosition(e.getPoint()));
		}
	}

	public void mouseMoved(MouseEvent e) {
		for (PaintComponent c : children) {
			Rectangle rect = c.getAbsoluteBounds();
			if (rect.contains(e.getPoint()) && !rect.contains(mouseLocation)) {
				c.mouseEntered(e);
			} else if (!rect.contains(e.getPoint()) && rect.contains(mouseLocation)) {
				c.mouseExited(e);
			}
		}
		for (PaintComponent c : children) {
			c.mouseMoved(e);
		}
		mouseLocation = e.getPoint();
	}

	public boolean shouldRepaint() {
		if (shouldRepaint) {
			return true;
		}
		for (PaintComponent c : children) {
			if (c.shouldRepaint()) {
				return true;
			}
		}
		return false;
	}
}