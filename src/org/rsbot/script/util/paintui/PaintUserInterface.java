package org.rsbot.script.util.paintui;

import java.awt.Graphics;
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
    }

    public Point getMouseLocation() {
	return mouseLocation;
    }

    public PaintComponent getChild(int id) {
	return (id >= 0 && id < children.size()) ? children.get(id) : null;
    }

    @Override
    public void onRepaint(Graphics render) {
	Graphics g = render.create();
	for (PaintComponent comp : children)
	    comp.onRepaint(g);
    }

    @Override
    public void keyPressed(KeyEvent e) {
	if (caret != null)
	    caret.doKeyEvent(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
	for (PaintComponent c : children) {
	    c.mouseClicked(e);
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
	for (PaintComponent c : children) {
	    if (c.getAbsoluteBounds().contains(e.getPoint())
		    && c instanceof PaintTextField) {
		if (caret == null || caret.field != c)
		    caret = new PaintCaret((PaintTextField) c);
		else if (e.isShiftDown())
		    caret.moveDot(caret.getPosition(e.getPoint()));
		else
		    caret.setDot(caret.getPosition(e.getPoint()));
	    }
	    c.mousePressed(e);
	}
    }

    @Override
    public void mouseReleased(MouseEvent e) {
	for (PaintComponent c : children)
	    c.mouseReleased(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
	for (PaintComponent c : children)
	    c.mouseDragged(e);
	if (caret != null)
	    caret.moveDot(caret.getPosition(e.getPoint()));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
	mouseLocation = e.getPoint();
	for (PaintComponent c : children)
	    c.mouseMoved(e);
    }
}
