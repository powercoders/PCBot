package org.rsbot.script.util.paintui;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

public class PaintTextField extends PaintComponent {
    private static final long serialVersionUID = 1L;
    Character mask = null;
    String text = "";
    final String name;
    FontRenderContext frc = new FontRenderContext(new AffineTransform(), false,
	    false);
    public String getText(){
	return text;
    }
    public void setText(String s){
	this.text = s;
    }
    public PaintTextField(Rectangle rect, String name) {
	super.setLocation(rect.x, rect.y);
	super.setSize(rect.width, rect.height);
	this.name = name;
    }

    public PaintTextField(Rectangle rect, String name, char mask) {
	this(rect, name);
	this.mask = mask;
    }

    @Override
    public void onRepaint(Graphics render) {
	paint(render);
	Graphics g = super.getClippedGraphics(render);
	g.setColor(styleSheet.fgColor);
	g.setFont(styleSheet.font);
	if (text.isEmpty() && !selected()) {
	    g.drawString(name, getAbsoluteX() + 3, getAbsoluteY() + getHeight()
		    - 3);
	} else {
	    g.drawString(getDisplay(), getAbsoluteX() + 3, getAbsoluteY()
		    + getHeight() - 3);
	}
	super.paintChildren(render);
    }

    public boolean selected() {
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
