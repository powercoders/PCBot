package org.rsbot.script.paint.methods;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.paint.PaintHandler;
import org.rsbot.script.paint.PaintProvider;
import org.rsbot.script.util.Timer;

public class DrawPaint extends PaintProvider implements PaintHandler {
	private BufferedImage img = null;

	public DrawPaint(MethodContext ctx) {
		super(ctx);
	}

	public void drawPaint(final int skill, final int startXP, final int amount,
			final long startTime, final Color textColor, final String status) {
		drawPaint(skill, startXP, amount, startTime, textColor, status, false,
				false);
	}

	public void drawPaint(final int skill, final int startXP, final int amount,
			final long startTime, final Color textColor, final String status,
			final boolean drawHideShowButton) {
		drawPaint(skill, startXP, amount, startTime, textColor, status,
				drawHideShowButton, false);
	}

	public void drawPaint(final int skill, final int startXP, final int amount,
			final long startTime, final Color textColor, final String status,
			final boolean drawHideShowButton, final boolean isHidden) {
		if (!isHidden) {
			g().setFont(new Font("Arial", 1, 15));
			g().setColor(new Color(220, 202, 169));
			g().fillRect(6, 344, 507, 129);
			g().setColor(textColor);
			g()
					.drawString(
							"Time Running: "
									+ methods.paint.util.getRuntime(startTime),
							60, 372);
			g().drawString(
					"Exp Gained: "
							+ (methods.skills.getCurrentExp(skill) - startXP),
					60, 391);
			g().drawString(
					"Exp/H: "
							+ methods.paint.util.getHourly(
									methods.skills.getCurrentExp(skill)
											- startXP, startTime), 60, 409);
			g().drawString("Done:  " + amount, 60, 426);
			g().drawString(
					"Items/Hr:  "
							+ methods.paint.util.getHourly(amount, startTime),
					60, 444);
			final long ttl = methods.skills.getTimeTillNextLevel(skill,
					startXP, System.currentTimeMillis() - startTime);
			if (ttl != -1) {
				g().drawString(
						"Estimated TTL: " + Timer.format(ttl), 10, 40);
			} else {
				g().drawString(
						"Estimated TTL: 00:00:00", 10, 40);
			}
			g().drawString("Status:  " + status, 60,
					466);
			if (img == null) {
				img = (BufferedImage) methods.paint.util.getImage(
						String.valueOf(skill) + ".png", true,
						"http://dl.dropbox.com/u/23938245/Scripts/Paint%20Class/capes/"
								+ skill + ".png");
			} else {
				g().drawImage(img, 440, 330, null);
			}
			methods.paint.progressBar.drawProgressBar(skill, 4, 3, 512, 18,
					Color.RED, Color.GREEN, textColor, 127);
			methods.paint.mouse.drawMouse(textColor, 10, true, 1500);
		}
		if (drawHideShowButton) {
			g().setColor(
					methods.paint.util.getInverseColor(textColor));
			if (isHidden) {
				g().fillRect(22, 344, 90, 14);
			}
			final Rectangle HIDE = new Rectangle(6, 344, 14, 14);
			g().fillRect(HIDE.x, HIDE.y, HIDE.width,
					HIDE.height);
			g().setColor(Color.black);
			g().drawRect(HIDE.x, HIDE.y, HIDE.width,
					HIDE.height);
			g().setColor(textColor);
			g().setFont(new Font("Arial", 1, 15));
			g().drawString("Show/Hide", 23, 356);
		}
	}

	public Graphics2D g() {
		return methods.bot.get2DGraphics();
	}

}
