package org.rsbot.script.util.paintui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

public class PaintComponent implements MouseListener, MouseMotionListener,
KeyListener {
	private PaintComponent parent;
	private PaintUserInterface rootPane;
	private final List<MouseListener> mouseListeners = new ArrayList<MouseListener>();
	private final List<KeyListener> keyListeners = new ArrayList<KeyListener>();
	private final List<MouseMotionListener> mouseMotionListeners = new ArrayList<MouseMotionListener>();
	private boolean repaint = false;
	protected int x = 0, y = 0, width = 0, height = 0;

	public PaintStyleSheet styleSheet = new PaintStyleSheet();
	public PaintStyleSheet mouseOverSheet = new PaintStyleSheet();

	public PaintComponent(final PaintComponent parent) {
		this.parent = parent;
	}

	public void queuePaint() {
		repaint = true;
	}

	public boolean shouldRepaint() {
		return repaint;
	}

	public void setStyle(final Class<?> clazz) {
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
		parent = null;
	}

	public void setSize(final int width, final int height) {
		this.width = width;
		this.height = height;
		queuePaint();
	}

	public void setLocation(final int x, final int y) {
		this.x = x;
		this.y = y;
		queuePaint();
	}

	public PaintComponent getParent() {
		return parent;
	}

	public void setParent(final PaintComponent parent) {
		this.parent = parent;
		queuePaint();
	}

	public PaintUserInterface getInterface() {
		return rootPane != null ? rootPane : parent != null ? parent
				.getInterface() : null;
	}

	public void setInterface(final PaintUserInterface rtPane) {
		rootPane = rtPane;
		queuePaint();
	}

	public int getAbsoluteX() {
		int absX = x;
		if (parent != null) {
			absX += parent.getAbsoluteX();
			parent = parent.getParent();
		}
		return absX;
	}

	public int getAbsoluteY() {
		int absY = y;
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
		return parent != null;
	}

	public Rectangle getRelativeBounds() {
		return new Rectangle(x, y, width, height);
	}

	public Rectangle getAbsoluteBounds() {
		return new Rectangle(getAbsoluteX(), getAbsoluteY(), width, height);
	}

	public void setStyle(final PaintStyleSheet styleSheet) {
		this.styleSheet = styleSheet;
		queuePaint();
	}

	public void setHoverStyle(final PaintStyleSheet styleSheet) {
		mouseOverSheet = styleSheet;
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

	protected PaintStyleSheet getCurrentStyle() {
		if (getAbsoluteBounds().contains(getInterface().getMouseLocation())
				&& mouseOverSheet != null) {
			return mouseOverSheet;
		} else {
			return styleSheet;
		}
	}

	public final void onRepaint(final Graphics render) {
		paint(render);
		repaint = false;
	}

	public void paint(final Graphics render) {
		final Graphics2D g = (Graphics2D) render;
		final Rectangle bounds = getAbsoluteBounds();
		if (getCurrentStyle() != null) {
			if (getCurrentStyle().bgColor != null) {
				g.setColor(getCurrentStyle().bgColor);
				if (getCurrentStyle().bkg3D) {
					g.fill3DRect(bounds.x, bounds.y, bounds.width,
							bounds.height, getCurrentStyle().bkgRaised);
				} else {
					g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
				}
			}
			// Borders
			if (getCurrentStyle().border != null) {
				g.setColor(getCurrentStyle().border);
				if (getCurrentStyle().border3D) {
					g.draw3DRect(bounds.x, bounds.y, bounds.width,
							bounds.height, getCurrentStyle().borderRaised);
				} else {
					g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
				}
			}
		}
	}

	public Graphics getClippedGraphics(final Graphics render) {
		final Graphics g = render.create();
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
		return mouseMotionListeners
		.toArray(new MouseMotionListener[mouseMotionListeners.size()]);
	}

	public KeyListener[] getKeyListeners() {
		return keyListeners.toArray(new KeyListener[keyListeners.size()]);
	}

	@Override
	public void keyPressed(final KeyEvent e) {
		for (final KeyListener l : getKeyListeners()) {
			l.keyPressed(e);
		}
	}

	@Override
	public void keyReleased(final KeyEvent e) {
		for (final KeyListener l : getKeyListeners()) {
			l.keyReleased(e);
		}
	}

	@Override
	public void keyTyped(final KeyEvent e) {
		for (final KeyListener l : getKeyListeners()) {
			l.keyTyped(e);
		}
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
		if (getRelativeBounds().contains(e.getPoint())) {
			final MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(),
					e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y,
					e.getClickCount(), e.isPopupTrigger());
			for (final MouseListener l : getMouseListeners()) {
				l.mousePressed(priv);
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
		}
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}
}
