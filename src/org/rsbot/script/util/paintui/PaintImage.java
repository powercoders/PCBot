package org.rsbot.script.util.paintui;

import java.awt.Graphics;
import java.awt.Image;

public class PaintImage extends PaintContainer {
    private Image image;

    public PaintImage(Image img) {
	this.image = img;
    }

    public void setImage(Image img) {
	this.image = img;
	queuePaint();
    }

    public Image getImage() {
	return image;
    }

    @Override
    public void paint(Graphics g) {
	super.paint(g);
	Graphics clipped = getClippedGraphics(g);
	clipped.drawImage(image, 0, 0, null);
	clipped.dispose();
    }
}
