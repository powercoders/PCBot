package org.rsbot.script.paint.methods;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;

import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.Skills;
import org.rsbot.script.paint.PaintHandler;
import org.rsbot.script.paint.PaintProvider;

public class DrawProgressBar extends PaintProvider implements PaintHandler {

	public DrawProgressBar(MethodContext ctx) {
		super(ctx);
	}

	public void drawProgressBar(final int skill, final int x, final int y,
			final int width, final int height, final Color colorBase,
			final Color colorOver, final Color textColor, final int alpha) {
		final GradientPaint base = new GradientPaint(x, y, new Color(200, 200,
				200, alpha), x, y + height + 3, colorBase);
		final GradientPaint overlay = new GradientPaint(x, y, new Color(200,
				200, 200, alpha), x, y + height + 3, colorOver);
		g().setPaint(base);
		g().fillRect(x, y, width, height);
		g().setPaint(overlay);
		g().fillRect(
				x,
				y,
				(int) (width * methods.skills.getPercentToNextLevel(skill) / 100.0),
				height);
		g().setColor(Color.BLACK);
		g().drawRect(x, y, width, height);
		g().setFont(new Font("Arial", 1, (width / 35 + height / 5)));
		final String progress = methods.skills
				.getPercentToNextLevel(skill)
				+ "% to "
				+ (methods.skills.getCurrentLevel(skill) + 1)
				+ " "
				+ Skills.SKILL_NAMES[skill]
				+ " | "
				+ methods.skills.getExpToNextLevel(skill)
				+ " XP Until level";
		g().setColor(new Color(textColor.getRed(), textColor.getGreen(),
				textColor.getBlue(), 150));
		g().drawString(
				progress,
				x + (width - g().getFontMetrics().stringWidth(progress)) / 2,
				(int) (y + (g().getFontMetrics().getHeight() / 2 + height / 4 * 1.65)));
	}

	public void draw3DProgressBar(final int skill, final int x, final int y,
			final int width, final int height, final Color color,
			final Color textColor, final int alpha) {
		g().setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(),
				alpha));
		g().fillRect(x - width / 100, y - width / 100, width + width / 40, height
				+ width / 40);
		g().fill3DRect(
				x,
				y,
				(int) (width * methods.skills.getPercentToNextLevel(skill) / 100.0),
				height, true);
		g().setColor(Color.BLACK);
		g().drawRect(x - width / 100, y - width / 100, width + width / 40, height
				+ width / 40);
		g().setFont(new Font("Arial", 1, (width / 35 + height / 5)));
		final String progress = methods.skills
				.getPercentToNextLevel(skill)
				+ "% to "
				+ (methods.skills.getCurrentLevel(skill) + 1)
				+ " "
				+ Skills.SKILL_NAMES[skill]
				+ " | "
				+ methods.skills.getExpToNextLevel(skill)
				+ " XP Until level";
		g().setColor(new Color(textColor.getRed(), textColor.getGreen(),
				textColor.getBlue(), 150));
		g().drawString(
				progress,
				x + (width - g().getFontMetrics().stringWidth(progress)) / 2,
				(int) (y + (g().getFontMetrics().getHeight() / 2 + height / 4 * 1.65)));
	}

	public Graphics2D g() {
		return methods.bot.get2DGraphics();
	}

}
