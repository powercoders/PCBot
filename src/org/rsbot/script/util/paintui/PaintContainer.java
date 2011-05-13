package org.rsbot.script.util.paintui;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class PaintContainer extends PaintComponent {
	private final ArrayList<PaintComponent> children = new ArrayList<PaintComponent>();

	public void addChild(final PaintComponent comp) {
		comp.setParent(this);
		children.add(comp);
		queuePaint();
	}

	public PaintComponent getChild(final int id) {
		return id >= 0 && id < children.size() ? children.get(id) : null;
	}

	public void removeChild(final PaintComponent comp) {
		children.remove(comp);
		queuePaint();
	}

	@Override
	public void paint(final Graphics g) {
		super.paint(g);
		paintChildren(g);
	}

	public void paintChildren(final Graphics g) {
		final Graphics myGraphics = getClippedGraphics(g);
		for (final PaintComponent comp : children) {
			comp.onRepaint(myGraphics);
		}
		myGraphics.dispose();
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		if (getRelativeBounds().contains(e.getPoint())) {
			final MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(),
					e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y,
					e.getClickCount(), e.isPopupTrigger());
			for (final MouseListener l : getMouseListeners()) {
				l.mouseClicked(priv);
			}
			for (final PaintComponent c : children) {
				c.mouseClicked(priv);
			}
		}
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		if (getRelativeBounds().contains(e.getPoint())) {
			final MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(),
					e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y,
					e.getClickCount(), e.isPopupTrigger());
			for (final MouseListener l : getMouseListeners()) {
				l.mousePressed(priv);
			}
			for (final PaintComponent c : children) {
				c.mousePressed(priv);
			}
		}
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		if (getRelativeBounds().contains(e.getPoint())) {
			final MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(),
					e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y,
					e.getClickCount(), e.isPopupTrigger());
			for (final MouseListener l : getMouseListeners()) {
				l.mouseReleased(priv);
			}
			for (final PaintComponent c : children) {
				c.mouseReleased(priv);
			}
		}
	}

	@Override
	public void mouseDragged(final MouseEvent e) {
		if (getRelativeBounds().contains(e.getPoint())) {
			final MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(),
					e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y,
					e.getClickCount(), e.isPopupTrigger());
			for (final MouseMotionListener l : getMouseMotionListeners()) {
				l.mouseDragged(priv);
			}
			for (final PaintComponent c : children) {
				c.mouseDragged(priv);
			}
		}
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		if (getRelativeBounds().contains(e.getPoint())) {
			final MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(),
					e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y,
					e.getClickCount(), e.isPopupTrigger());
			for (final MouseMotionListener l : getMouseMotionListeners()) {
				l.mouseMoved(priv);
			}
			for (final PaintComponent c : children) {
				c.mouseMoved(priv);
			}
		}
	}

	@Override
	public boolean shouldRepaint() {
		if (super.shouldRepaint()) {
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
