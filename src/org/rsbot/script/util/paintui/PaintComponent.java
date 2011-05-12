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

public class PaintComponent implements MouseListener,
	MouseMotionListener, KeyListener {
    private PaintComponent parent;
    private PaintUserInterface rootPane;
    private ArrayList<PaintComponent> children = new ArrayList<PaintComponent>();
    private List<MouseListener> mouseListeners = new ArrayList<MouseListener>();
    private List<KeyListener> keyListeners = new ArrayList<KeyListener>();
    private List<MouseMotionListener> mouseMotionListeners = new ArrayList<MouseMotionListener>();

    private int x, y, width, height;

    public PaintStyleSheet styleSheet = new PaintStyleSheet();

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

    public void addChild(PaintComponent comp) {
	comp.setParent(this);
	children.add(comp);
    }

    public PaintComponent getChild(int id) {
	return (id >= 0 && id < children.size()) ? children.get(id) : null;
    }

    public void removeChild(PaintComponent comp) {
	children.remove(comp);
    }

    public void style(PaintStyleSheet styleSheet) {
	this.styleSheet = styleSheet;
    }

    public void inheritStyle() {
	if (parent != null)
	    styleSheet = parent.styleSheet.clone();
    }

    public void revertStyle() {
	styleSheet = new PaintStyleSheet();
    }

    public PaintStyleSheet getStyle() {
	return styleSheet;
    }

    public void onRepaint(Graphics render) {
	paint(render);
	paintChildren(render);
    }

    public void paint(Graphics render){
	Graphics2D g = (Graphics2D) render;
	Rectangle bounds = getAbsoluteBounds();
	if (styleSheet.bgColor != null) {
	    g.setColor(styleSheet.bgColor);
	    g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
	}
	if (styleSheet.background != null) {
	    g.drawImage(styleSheet.background, bounds.x, bounds.y, bounds.x
		    + bounds.width, bounds.y + bounds.height, 0, 0,
		    bounds.width, bounds.height, null);
	}
	// Borders
	if (styleSheet.border != null && styleSheet.borderSize >= 0) {
	    Stroke curr = g.getStroke();
	    g.setStroke(new BasicStroke(styleSheet.borderSize));
	    g.setColor(styleSheet.border);
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

    public void paintChildren(Graphics g) {
	Graphics myGraphics = getClippedGraphics(g);
	for (PaintComponent comp : children)
	    comp.onRepaint(myGraphics);
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
	    for (PaintComponent c : children)
		c.mouseClicked(priv);
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
	    for (PaintComponent c : children)
		c.mousePressed(priv);
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
	    for (PaintComponent c : children)
		c.mouseReleased(priv);
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
	    for (PaintComponent c : children)
		c.mouseDragged(priv);
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
	    for (PaintComponent c : children)
		c.mouseMoved(priv);
	}
    }

    public int getHeight() {
	return height;
    }

    public int getWidth() {
	return width;
    }
}
