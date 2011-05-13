package org.rsbot.script.util.paintui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class PaintComponent implements MouseListener, MouseMotionListener, KeyListener {
	private PaintComponent parent;
	private PaintUserInterface rootPane;
	private List<MouseListener> mouseListeners = new ArrayList<MouseListener>();
	private List<KeyListener> keyListeners = new ArrayList<KeyListener>();
	private List<MouseMotionListener> mouseMotionListeners = new ArrayList<MouseMotionListener>();
	private boolean repaint = false;
	protected int x = 0, y = 0, width = 0, height = 0;
	public PaintStyleSheet styleSheet = new PaintStyleSheet();
	public PaintStyleSheet mouseOverSheet = new PaintStyleSheet();

	public PaintComponent(PaintComponent parent) {
		this.parent = parent;
	}

	public void queuePaint() {
		repaint = true;
	}

	public boolean shouldRepaint() {
		return repaint;
	}

	public void setStyle(Class<?> clazz) {
		styleSheet = PaintStyleSheet.defaultPaintStyles.get(clazz);
		mouseOverSheet = PaintStyleSheet.defaultHoverStyles.get(clazz);
		if (styleSheet == null) {
			styleSheet = new PaintStyleSheet();
		} else {
			styleSheet = styleSheet.clone();
		}
		if (mouseOverSheet == null) {
			mouseOverSheet = new PaintStyleSheet();
		} else {
			mouseOverSheet = mouseOverSheet.clone();
		}
		queuePaint();
	}

	public PaintComponent() {
		this.parent = null;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		queuePaint();
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
		queuePaint();
	}

	public PaintComponent getParent() {
		return parent;
	}

	public void setParent(PaintComponent parent) {
		this.parent = parent;
		queuePaint();
	}

	public PaintUserInterface getInterface() {
		return rootPane != null ? rootPane : parent != null ? parent.getInterface() : null;
	}

	public void setInterface(PaintUserInterface rtPane) {
		this.rootPane = rtPane;
		queuePaint();
	}

	public int getAbsoluteX() {
		int absX = this.x;
		if (parent != null) {
			absX += parent.getAbsoluteX();
			parent = parent.getParent();
		}
		return absX;
	}

	public int getAbsoluteY() {
		int absY = this.y;
		if (parent != null) {
			absY += parent.getAbsoluteY();
		}
		return absY;
	}

	public int getRelativeX() {
		return x;
	}

	public int getRelativeY() {
		return y;
	}

	public boolean hasParent() {
		return (parent != null);
	}

	public Rectangle getRelativeBounds() {
		return new Rectangle(x, y, width, height);
	}

	public Rectangle getAbsoluteBounds() {
		return new Rectangle(getAbsoluteX(), getAbsoluteY(), width, height);
	}

	public void setStyle(PaintStyleSheet styleSheet) {
		this.styleSheet = styleSheet;
		queuePaint();
	}

	public void setHoverStyle(PaintStyleSheet styleSheet) {
		this.mouseOverSheet = styleSheet;
		queuePaint();
	}

	public void inheritStyle() {
		if (parent != null) {
			styleSheet = parent.styleSheet;
			mouseOverSheet = parent.mouseOverSheet;
			queuePaint();
		}
	}

	public void revertStyle() {
		styleSheet = new PaintStyleSheet();
		mouseOverSheet = new PaintStyleSheet();
		queuePaint();
	}

	public PaintStyleSheet getStyle() {
		return styleSheet;
	}

	public PaintStyleSheet getHoverStyle() {
		return mouseOverSheet;
	}

	public Point getMouseLocation() {
		return getInterface() != null ? getInterface().getMouseLocation() : new Point(0, 0);
	}

	protected PaintStyleSheet getCurrentStyle() {
		if (getAbsoluteBounds().contains(getMouseLocation()) && mouseOverSheet != null) {
			return mouseOverSheet;
		} else {
			return styleSheet;
		}
	}

	public final void onRepaint(Graphics render) {
		paint(render);
		repaint = false;
	}

	public void paint(Graphics render) {
		Graphics2D g = (Graphics2D) render;
		Rectangle bounds = getAbsoluteBounds();
		if (getCurrentStyle() != null) {
			if (getCurrentStyle().bgColor != null) {
				g.setColor(getCurrentStyle().bgColor);
				if (getCurrentStyle().bkg3D) {
					g.fill3DRect(bounds.x, bounds.y, bounds.width, bounds.height, getCurrentStyle().bkgRaised);
				} else {
					g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
				}
			}
			if (getCurrentStyle().border != null) {
				g.setColor(getCurrentStyle().border);
				if (getCurrentStyle().border3D) {
					g.draw3DRect(bounds.x, bounds.y, bounds.width, bounds.height, getCurrentStyle().borderRaised);
				} else {
					g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
				}
			}
		}
	}

	public Graphics getClippedGraphics(Graphics render) {
		Graphics g = render.create();
		g.clipRect(x, y, width, height);
		return g;
	}

	public void addMouseListener(final MouseListener m) {
		mouseListeners.add(m);
	}

	public void addMouseMotionListener(final MouseMotionListener m) {
		mouseMotionListeners.add(m);
	}

	public void addKeyListener(final KeyListener l) {
		keyListeners.add(l);
	}

	public MouseListener[] getMouseListeners() {
		return mouseListeners.toArray(new MouseListener[mouseListeners.size()]);
	}

	public MouseMotionListener[] getMouseMotionListeners() {
		return mouseMotionListeners.toArray(new MouseMotionListener[mouseMotionListeners.size()]);
	}

	public KeyListener[] getKeyListeners() {
		return keyListeners.toArray(new KeyListener[keyListeners.size()]);
	}

	public void keyPressed(KeyEvent e) {
		for (KeyListener l : getKeyListeners()) {
			l.keyPressed(e);
		}
	}

	public void keyReleased(KeyEvent e) {
		for (KeyListener l : getKeyListeners()) {
			l.keyReleased(e);
		}
	}

	public void keyTyped(KeyEvent e) {
		for (KeyListener l : getKeyListeners()) {
			l.keyTyped(e);
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (getRelativeBounds().contains(e.getPoint())) {
			MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y, e.getClickCount(), e.isPopupTrigger());
			for (MouseListener l : getMouseListeners()) {
				l.mouseClicked(priv);
			}
		}
	}

	public void mouseEntered(MouseEvent e) {
		MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y, e.getClickCount(), e.isPopupTrigger());
		for (MouseListener l : getMouseListeners()) {
			l.mouseEntered(priv);
		}
		queuePaint();
	}

	public void mouseExited(MouseEvent e) {
		MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y, e.getClickCount(), e.isPopupTrigger());
		for (MouseListener l : getMouseListeners()) {
			l.mouseExited(priv);
		}
		queuePaint();
	}

	public void mousePressed(MouseEvent e) {
		if (getRelativeBounds().contains(e.getPoint())) {
			MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y, e.getClickCount(), e.isPopupTrigger());
			for (MouseListener l : getMouseListeners()) {
				l.mousePressed(priv);
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (getRelativeBounds().contains(e.getPoint())) {
			MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y, e.getClickCount(), e.isPopupTrigger());
			for (MouseListener l : getMouseListeners()) {
				l.mouseReleased(priv);
			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		if (getRelativeBounds().contains(e.getPoint())) {
			MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y, e.getClickCount(), e.isPopupTrigger());
			for (MouseMotionListener l : getMouseMotionListeners()) {
				l.mouseDragged(priv);
			}
		}
	}

	public void mouseMoved(MouseEvent e) {
		if (getRelativeBounds().contains(e.getPoint())) {
			MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y, e.getClickCount(), e.isPopupTrigger());
			for (MouseMotionListener l : getMouseMotionListeners()) {
				l.mouseMoved(priv);
			}
		}
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}
}