package org.rsbot.script.util.paintui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

public class PaintStyleSheet {
	public static final Map<Class<?>, PaintStyleSheet> defaultPaintStyles = new HashMap<Class<?>, PaintStyleSheet>();
	public static final Map<Class<?>, PaintStyleSheet> defaultHoverStyles = new HashMap<Class<?>, PaintStyleSheet>(); static {
		PaintStyleSheet textField = new PaintStyleSheet();
		textField.font = new Font("Arial", 0, 12);
		textField.bgColor = new Color(0.41f, .55f, 0.001f, 0.1f);
		textField.border = new Color(0.41f, .55f, 0.001f);
		defaultPaintStyles.put(PaintTextField.class, textField);
		PaintStyleSheet buttonStyle = new PaintStyleSheet();
		buttonStyle.border = new Color(0.222f, 0.3f, 0.07f, 0.9f);
		buttonStyle.font = new Font("Arial", 0, 12);
		buttonStyle.bgColor = new Color(0.33f, 0.41f, 0.18f, 0.85f);
		PaintStyleSheet buttonHoverStyle = buttonStyle.clone();
		buttonHoverStyle.bkg3D = true;
		buttonHoverStyle.border3D = true;
		buttonHoverStyle.bgColor = new Color(0.333f, 0.41f, 0.18f, 0.6f);
		defaultPaintStyles.put(PaintButton.class, buttonStyle);
		defaultPaintStyles.put(PaintCheckBox.class, buttonStyle);
		defaultHoverStyles.put(PaintButton.class, buttonHoverStyle);
		defaultHoverStyles.put(PaintCheckBox.class, buttonHoverStyle);
		defaultPaintStyles.put(PaintLabel.class, new PaintStyleSheet());
	}

	public Color bgColor = null;
	public Color fgColor = Color.BLACK;
	public boolean bkg3D = false;
	public boolean bkgRaised = false;
	public Font font = new Font("Arial", 0, 12);
	public Color border = null;
	public boolean border3D = false;
	public boolean borderRaised = false;

	@Override
	public PaintStyleSheet clone() {
		PaintStyleSheet s = new PaintStyleSheet();
		s.bgColor = bgColor;
		s.fgColor = fgColor;
		s.font = font;
		s.border = border;
		s.border3D = border3D;
		s.bkg3D = bkg3D;
		s.borderRaised = borderRaised;
		s.bkgRaised = bkgRaised;
		return s;
	}
}