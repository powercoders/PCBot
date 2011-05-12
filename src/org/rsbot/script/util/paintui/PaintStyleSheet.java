package org.rsbot.script.util.paintui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;

public class PaintStyleSheet {
    public Color bgColor = Color.WHITE;
    public Image background = null;
    public Color fgColor = Color.BLUE;
    public Font font = new Font("Arial", 1, 30);
    public Color border = null;
    public float borderSize;

    @Override
    public PaintStyleSheet clone() {
	PaintStyleSheet s = new PaintStyleSheet();
	s.bgColor = bgColor;
	s.background = background;
	s.fgColor = fgColor;
	s.font = font;
	s.border = border;
	s.borderSize = borderSize;
	return s;
    }
}
