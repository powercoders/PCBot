package org.rsbot.script.util.paintui;

import java.awt.Graphics;
import java.awt.Image;

public class PaintImage extends PaintContainer {
	private Image image;

	public PaintImage(final Image img) {
		image = img;
	}

	public void setImage(final Image img) {
		image = img;
		queuePaint();
	}

	public Image getImage() {
		return image;
	}

	@Override
	public void paint(final Graphics g) {
		super.paint(g);
		final Graphics clipped = getClippedGraphics(g);
		clipped.drawImage(image, 0, 0, null);
		clipped.dispose();
	}
}
