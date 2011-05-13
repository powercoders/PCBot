package org.rsbot.script.util.paintui;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class PaintContainer extends PaintComponent {
	private ArrayList<PaintComponent> children = new ArrayList<PaintComponent>();

	public void addChild(PaintComponent comp) {
		comp.setParent(this);
		children.add(comp);
		queuePaint();
	}

	public PaintComponent getChild(int id) {
		return (id >= 0 && id < children.size()) ? children.get(id) : null;
	}

	public void removeChild(PaintComponent comp) {
		children.remove(comp);
		queuePaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		paintChildren(g);
	}

	public void paintChildren(Graphics g) {
		Graphics myGraphics = getClippedGraphics(g);
		for (PaintComponent comp : children) {
			comp.onRepaint(myGraphics);
		}
		myGraphics.dispose();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (getRelativeBounds().contains(e.getPoint())) {
			MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y, e.getClickCount(), e.isPopupTrigger());
			for (MouseListener l : getMouseListeners()) {
				l.mouseClicked(priv);
			}
			for (PaintComponent c : children) {
				c.mouseClicked(priv);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (getRelativeBounds().contains(e.getPoint())) {
			MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y, e.getClickCount(), e.isPopupTrigger());
			for (MouseListener l : getMouseListeners()) {
				l.mousePressed(priv);
			}
			for (PaintComponent c : children) {
				c.mousePressed(priv);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (getRelativeBounds().contains(e.getPoint())) {
			MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y, e.getClickCount(), e.isPopupTrigger());
			for (MouseListener l : getMouseListeners()) {
				l.mouseReleased(priv);
			}
			for (PaintComponent c : children) {
				c.mouseReleased(priv);
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (getRelativeBounds().contains(e.getPoint())) {
			MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y, e.getClickCount(), e.isPopupTrigger());
			for (MouseMotionListener l : getMouseMotionListeners()) {
				l.mouseDragged(priv);
			}
			for (PaintComponent c : children) {
				c.mouseDragged(priv);
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (getRelativeBounds().contains(e.getPoint())) {
			MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y, e.getClickCount(), e.isPopupTrigger());
			for (MouseMotionListener l : getMouseMotionListeners()) {
				l.mouseMoved(priv);
			}
			for (PaintComponent c : children) {
				c.mouseMoved(priv);
			}
		}
	}

	@Override
	public boolean shouldRepaint() {
		if (super.shouldRepaint()) {
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