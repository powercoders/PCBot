package org.rsbot.script.util.paintui;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import org.rsbot.event.listeners.PaintListener;

public class PaintComponent implements MouseListener, MouseMotionListener,
	KeyListener {
    private PaintComponent parent;
    private PaintUserInterface rootPane;
    private List<MouseListener> mouseListeners = new ArrayList<MouseListener>();
    private List<KeyListener> keyListeners = new ArrayList<KeyListener>();
    private List<MouseMotionListener> mouseMotionListeners = new ArrayList<MouseMotionListener>();

    protected int x, y, width, height;

    public PaintStyleSheet styleSheet = new PaintStyleSheet();
    public PaintStyleSheet mouseOverSheet = new PaintStyleSheet();

    public PaintComponent(PaintComponent parent) {
	this.parent = parent;
    }

    public PaintComponent() {
	this.parent = null;
    }

    public void setSize(int width, int height) {
	this.width = width;
	this.height = height;
    }

    public void setLocation(int x, int y) {
	this.x = x;
	this.y = y;
    }

    public PaintComponent getParent() {
	return parent;
    }

    public void setParent(PaintComponent parent) {
	this.parent = parent;
    }

    public PaintUserInterface getInterface() {
	return rootPane != null ? rootPane : parent != null ? parent
		.getInterface() : null;
    }

    public void setInterface(PaintUserInterface rtPane) {
	this.rootPane = rtPane;
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
    }

    public void setHoverStyle(PaintStyleSheet styleSheet) {
	this.mouseOverSheet = styleSheet;
    }

    public void inheritStyle() {
	if (parent != null) {
	    styleSheet = parent.styleSheet.clone();
	    mouseOverSheet = parent.mouseOverSheet.clone();
	}
    }

    public void revertStyle() {
	styleSheet = new PaintStyleSheet();
	mouseOverSheet = new PaintStyleSheet();
    }

    public PaintStyleSheet getStyle() {
	return styleSheet;
    }

    public PaintStyleSheet getHoverStyle() {
	return mouseOverSheet;
    }

    protected PaintStyleSheet getCurrentStyle() {
	if (getAbsoluteBounds().contains(getInterface().getMouseLocation())
		&& mouseOverSheet != null)
	    return mouseOverSheet;
	else
	    return styleSheet;
    }

    public void onRepaint(Graphics render) {
	paint(render);
    }

    public void paint(Graphics render) {
	Graphics2D g = (Graphics2D) render;
	Rectangle bounds = getAbsoluteBounds();
	if (getCurrentStyle().bgColor != null) {
	    g.setColor(getCurrentStyle().bgColor);
	    g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
	}
	if (getCurrentStyle().background != null) {
	    g.drawImage(getCurrentStyle().background, bounds.x, bounds.y, bounds.x
		    + bounds.width, bounds.y + bounds.height, 0, 0,
		    bounds.width, bounds.height, null);
	}
	// Borders
	if (getCurrentStyle().border != null && getCurrentStyle().borderSize >= 0) {
	    Stroke curr = g.getStroke();
	    g.setStroke(new BasicStroke(getCurrentStyle().borderSize));
	    g.setColor(getCurrentStyle().border);
	    g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
	    g.setStroke(curr);
	}
    }

    public Graphics getClippedGraphics(Graphics g) {
	Rectangle currClip = g.getClipBounds();
	return g.create(Math.max(0, x), Math.max(0, y),
		Math.min(width, currClip.width),
		Math.min(currClip.height, height));
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
    public void keyPressed(KeyEvent e) {
	for (KeyListener l : getKeyListeners()) {
	    l.keyPressed(e);
	}
    }

    @Override
    public void keyReleased(KeyEvent e) {
	for (KeyListener l : getKeyListeners()) {
	    l.keyReleased(e);
	}
    }

    @Override
    public void keyTyped(KeyEvent e) {
	for (KeyListener l : getKeyListeners()) {
	    l.keyTyped(e);
	}
    }

    @Override
    public void mouseClicked(MouseEvent e) {
	if (getRelativeBounds().contains(e.getPoint())) {
	    MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(),
		    e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y,
		    e.getClickCount(), e.isPopupTrigger());
	    for (MouseListener l : getMouseListeners())
		l.mouseClicked(priv);
	}
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
	if (getRelativeBounds().contains(e.getPoint())) {
	    MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(),
		    e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y,
		    e.getClickCount(), e.isPopupTrigger());
	    for (MouseListener l : getMouseListeners())
		l.mousePressed(priv);
	}
    }

    @Override
    public void mouseReleased(MouseEvent e) {
	if (getRelativeBounds().contains(e.getPoint())) {
	    MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(),
		    e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y,
		    e.getClickCount(), e.isPopupTrigger());
	    for (MouseListener l : getMouseListeners())
		l.mouseReleased(priv);
	}
    }

    @Override
    public void mouseDragged(MouseEvent e) {
	if (getRelativeBounds().contains(e.getPoint())) {
	    MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(),
		    e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y,
		    e.getClickCount(), e.isPopupTrigger());
	    for (MouseMotionListener l : getMouseMotionListeners())
		l.mouseDragged(priv);
	}
    }

    @Override
    public void mouseMoved(MouseEvent e) {
	if (getRelativeBounds().contains(e.getPoint())) {
	    MouseEvent priv = new MouseEvent(e.getComponent(), e.getID(),
		    e.getWhen(), e.getModifiers(), e.getX() - x, e.getY() - y,
		    e.getClickCount(), e.isPopupTrigger());
	    for (MouseMotionListener l : getMouseMotionListeners())
		l.mouseMoved(priv);
	}
    }

    public int getHeight() {
	return height;
    }

    public int getWidth() {
	return width;
    }
}
