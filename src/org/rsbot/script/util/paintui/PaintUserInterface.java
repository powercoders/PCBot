package org.rsbot.script.util.paintui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
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


    @Override
    public void onRepaint(Graphics render) {
	Graphics2D g = (Graphics2D) render;
	//if (shouldRepaint())
	    for (PaintComponent comp : children) {
		comp.onRepaint(g);
	    }
	if (caret != null) {
	    g.setColor(Color.BLACK);
	    caret.paint(g);
	}
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
	boolean foundCaret = false;
	for (PaintComponent c : children) {
	    if (c.getAbsoluteBounds().contains(e.getPoint())
		    && c instanceof PaintTextField) {
		if (caret == null || caret.field != c) {
		    caret = new PaintCaret((PaintTextField) c);
		    shouldRepaint = true;
		}
		if (e.isShiftDown())
		    caret.moveDot(caret.getPosition(e.getPoint()));
		else
		    caret.setDot(caret.getPosition(e.getPoint()));
		foundCaret = true;
	    }
	    c.mousePressed(e);
	}
	if (!foundCaret)
	    caret = null;
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
	if (caret != null && caret.field != null
		&& caret.field.getAbsoluteBounds().contains(e.getPoint()))
	    caret.moveDot(caret.getPosition(e.getPoint()));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
	for (PaintComponent c : children) {
	    Rectangle rect = c.getAbsoluteBounds();
	    if (rect.contains(e.getPoint()) && !rect.contains(mouseLocation))
		c.mouseEntered(e);
	    else if (!rect.contains(e.getPoint())
		    && rect.contains(mouseLocation))
		c.mouseExited(e);
	}
	for (PaintComponent c : children)
	    c.mouseMoved(e);
	mouseLocation = e.getPoint();
    }

    public boolean shouldRepaint() {
	if (shouldRepaint)
	    return true;
	for (PaintComponent c : children)
	    if (c.shouldRepaint())
		return true;
	return false;
    }
}
