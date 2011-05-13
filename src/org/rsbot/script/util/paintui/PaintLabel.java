package org.rsbot.script.util.paintui;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

public class PaintLabel extends PaintComponent {
	private String text = "";

	public PaintLabel(String text) {
		super();
		this.text = text;
		setStyle(getClass());
	}

	public void setLabel(String lbl) {
		this.text = lbl;
		queuePaint();
	}

	public String getLabel() {
		return text;
	}

	@Override
	public void paint(Graphics render) {
		super.paint(render);
		Graphics g = getClippedGraphics(render);
		g.setFont(super.getCurrentStyle().font);
		if (isItalic() && isBold()) {
			g.setFont(g.getFont().deriveFont(Font.BOLD | Font.ITALIC));
		} else if (isItalic()) {
			g.setFont(g.getFont().deriveFont(Font.ITALIC));
		} else if (isBold()) {
			g.setFont(g.getFont().deriveFont(Font.BOLD));
		}
		g.setColor(super.getCurrentStyle().fgColor);
		FontMetrics metrics = g.getFontMetrics();
		double centerX = super.getAbsoluteBounds().getCenterX(), centerY = super.getAbsoluteBounds().getCenterY();
		Rectangle strBounds = metrics.getStringBounds(getDisplay(), g).getBounds();
		g.drawString(getDisplay(), (int) centerX - (strBounds.width / 2), (int) centerY + (strBounds.height / 2));
		g.dispose();
	}

	private String getDisplay() {
		return text.replace("[B]", "").replace("[I]", "").replace("[i]", "").replace("[b]", "");
	}

	private boolean isBold() {
		return text.toLowerCase().contains("[b]");
	}

	private boolean isItalic() {
		return text.toLowerCase().contains("[i]");
	}
}