package org.rsbot.script.util.paintui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class PaintCaret {
    int blinkRate = 500;
    int dot = 0, mark = 0;
    long lastBlink = 0;
    boolean blinkState = true;
    final PaintTextField field;

    public PaintCaret(PaintTextField f) {
	this.field = f;
    }

    public int getBlinkRate() {
	return blinkRate;
    }

    public int getDot() {
	return dot;
    }

    public Line2D getCaretPosition(int dotPos) {
	String off;
	if (dotPos >= 0)
	    off = field.getDisplay().substring(0, dotPos);
	else
	    off = field.getDisplay();
	Rectangle2D strBounds = field.getCurrentStyle().font.getStringBounds(
		off, field.frc);
	return new Line2D.Double(field.getAbsoluteX() + 3
		+ (dotPos >= 0 ? (int) strBounds.getWidth() : 0),
		field.getAbsoluteY() + field.getHeight() - 2,
		field.getAbsoluteX() + 3
			+ (dotPos >= 0 ? (int) strBounds.getWidth() : 0),
		field.getAbsoluteY() + 2);
    }

    public int getMark() {
	return mark;
    }

    public void moveDot(int arg0) {
	dot = arg0;
    }

    public void paint(Graphics2D g) {
	if (System.currentTimeMillis() > lastBlink + blinkRate) {
	    lastBlink = System.currentTimeMillis();
	    blinkState = !blinkState;
	}
	Line2D dotP = getCaretPosition(dot);
	if (blinkState)
	    g.draw(dotP);
	if (getDot() != getMark()) {
	    Line2D markP = getCaretPosition(mark);
	    g.draw(markP);
	    Polygon poly = new Polygon();
	    poly.addPoint((int) markP.getX1(), (int) markP.getY1());
	    poly.addPoint((int) markP.getX2(), (int) markP.getY2());
	    poly.addPoint((int) dotP.getX2(), (int) dotP.getY2());
	    poly.addPoint((int) dotP.getX1(), (int) dotP.getY1());
	    g.setColor(new Color(0f, 0f, 0f, 0.05f));
	    g.fill(poly);
	}
    }

    public void setBlinkRate(int arg0) {
	blinkRate = arg0;
    }

    public void setDot(int arg0) {
	dot = arg0;
	mark = arg0;
    }

    public int getPosition(Point p) {
	int best = field.getText().length() - 1;
	double bestDist = Double.MAX_VALUE;
	for (int i = -1; i <= field.getText().length(); i++) {
	    Point2D saidPos = getCaretPosition(i).getP1();
	    double dist = Math.abs(saidPos.getX() - p.x);
	    if (dist < bestDist) {
		bestDist = dist;
		best = i;
	    }
	}
	return best;
    }

    public void doKeyEvent(KeyEvent e) {
	if (e.getKeyCode() == KeyEvent.VK_LEFT && dot >= 0)
	    if (e.isShiftDown())
		moveDot(dot - 1);
	    else
		setDot(dot - 1);
	else if (e.getKeyCode() == KeyEvent.VK_RIGHT
		&& dot < field.getText().length())
	    if (e.isShiftDown())
		moveDot(dot + 1);
	    else
		setDot(dot + 1);
	else if (e.getKeyCode() == KeyEvent.VK_UP
		|| e.getKeyCode() == KeyEvent.VK_END
		|| e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
	    if (e.isShiftDown())
		moveDot(field.getText().length());
	    else
		setDot(field.getText().length());
	else if (e.getKeyCode() == KeyEvent.VK_DOWN
		|| e.getKeyCode() == KeyEvent.VK_HOME
		|| e.getKeyCode() == KeyEvent.VK_PAGE_UP)
	    if (e.isShiftDown())
		moveDot(-1);
	    else
		setDot(-1);
	else if (e.getKeyChar() == '\b'
		&& (Math.min(dot, mark) > 0 || dot != mark)) {
	    if (dot == mark) {
		String b4 = field.getText().substring(0,
			Math.min(dot, mark) - 1);
		String after = "";
		if (Math.max(dot, mark) >= 0)
		    after = field.getText().substring(Math.max(dot, mark));
		field.setText(b4 + after);
		setDot(b4.length());
	    } else {
		String b4 = Math.min(dot, mark) > 0 ? field.getText()
			.substring(0, Math.min(dot, mark)) : "";
		String after = "";
		if (Math.max(dot, mark) >= 0)
		    after = field.getText().substring(Math.max(dot, mark));
		field.setText(b4 + after);
		setDot(b4.length());
	    }
	} else if (e.getKeyCode() == KeyEvent.VK_DELETE
		&& ((dot < field.getText().length() && dot >= 0) || dot != mark)) {
	    if (dot == mark) {
		String b4 = field.getText().substring(0, dot);
		String after = "";
		if (dot >= 0)
		    after = field.getText().substring(Math.max(dot, mark) + 1);
		field.setText(b4 + after);
		setDot(b4.length());
	    } else {
		String b4 = Math.min(dot, mark) > 0 ? field.getText()
			.substring(0, Math.min(dot, mark)) : "";
		String after = "";
		if (Math.max(dot, mark) >= 0)
		    after = field.getText().substring(Math.max(dot, mark));
		field.setText(b4 + after);
		setDot(b4.length());
	    }
	} else if (field.getStyle().font.canDisplay(e.getKeyChar())) {
	    String b4 = Math.min(dot, mark) > 0 ? field.getText().substring(0,
		    Math.min(dot, mark)) : "";
	    String after = "";
	    if (Math.max(dot, mark) >= 0)
		after = field.getText().substring(Math.max(dot, mark));
	    String newText = b4 + e.getKeyChar() + after;
	    Rectangle2D strBounds = field.getCurrentStyle().font
		    .getStringBounds(newText, field.frc);
	    int width = (int) (strBounds.getWidth() + 6);
	    if (width <= field.getWidth()) {
		field.setText(newText);
		setDot(b4.length() + 1);
	    }
	}
    }
}
