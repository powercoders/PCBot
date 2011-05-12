package org.rsbot.script.util.paintui;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

public class PaintLabel extends PaintComponent {
    private String text = "";

    public PaintLabel(String text) {
	this.text = text;
    }

    @Override
    public void onRepaint(Graphics render) {
	super.paint(render);
	Graphics g = getClippedGraphics(render);
	g.setFont(super.getCurrentStyle().font);
	g.setColor(super.getCurrentStyle().fgColor);
	FontMetrics metrics = g.getFontMetrics();
	double centerX = super.getAbsoluteBounds().getCenterX(), centerY = super
		.getAbsoluteBounds().getCenterY();
	Rectangle strBounds = metrics.getStringBounds(text, g).getBounds();
	g.drawString(text, (int) centerX - (strBounds.width / 2), (int) centerY
		+ (strBounds.height / 2));
    }
}
