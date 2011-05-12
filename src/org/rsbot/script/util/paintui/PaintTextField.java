package org.rsbot.script.util.paintui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

public class PaintTextField extends PaintComponent {
    private static final long serialVersionUID = 1L;
    Character mask = null;
    String text = "";
    final String name;
    FontRenderContext frc = new FontRenderContext(new AffineTransform(), false,
	    false);

    public String getText() {
	return text;
    }

    public void setText(String s) {
	this.text = s;
	queuePaint();
    }

    public PaintTextField(String empty) {
	this(empty, null);
    }

    public PaintTextField(String empty, Character mask) {
	this.name = empty;
	this.mask = mask;
	setStyle(getClass());
	setSize(100,16);
    }

    public void setMask(Character mask) {
	this.mask = mask;
	queuePaint();
    }

    @Override
    public void paint(Graphics render) {
	super.paint(render);
	Graphics g = super.getClippedGraphics(render);
	g.setColor(getCurrentStyle().fgColor);
	g.setFont(getCurrentStyle().font);
	frc = ((Graphics2D) g).getFontRenderContext();
	if (text.isEmpty() && !selected()) {
	    g.drawString(name, getAbsoluteX() + 3, getAbsoluteY() + getHeight()
		    - 3);
	} else {
	    g.drawString(getDisplay(), getAbsoluteX() + 3, getAbsoluteY()
		    + getHeight() - 3);
	}
	g.dispose();
    }

    private boolean selected() {
	return getInterface().getCaret() != null
		&& getInterface().getCaret().field == this;
    }

    public String getDisplay() {
	String disp = "";
	if (mask != null) {
	    for (int i = 0; i < text.length(); i++)
		disp += mask;
	} else
	    disp = text;
	return disp;
    }
}
