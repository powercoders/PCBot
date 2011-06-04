package org.rsbot.script.paint.methods;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.rsbot.script.methods.Game.Tab;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.paint.PaintHandler;
import org.rsbot.script.paint.PaintProvider;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSItem;

public class DrawItems extends PaintProvider implements PaintHandler {

	public DrawItems(MethodContext ctx) {
		super(ctx);
	}

	public void drawItems(final Color color) {
		if (methods.game.getTab() == Tab.INVENTORY) {
			g().setFont(new Font(null, Font.BOLD, 8));
			g().setColor(color);
			final RSItem[] curItem = methods.inventory.getItems();
			for (RSItem i : curItem) {
				if (i != null && i.getID() != -1) {
					final Rectangle bounds = i.getComponent().getArea();
					final String name = i.getName().replaceAll(" ", "\n");
					g().draw(bounds);
					g().drawString(name, i.getComponent().getAbsoluteX(), i
							.getComponent().getCenter().y);
				}
			}
		}
	}

	public void drawItems(final int[] items, final Color color) {
		if (methods.game.getTab() == Tab.INVENTORY) {
			g().setFont(new Font(null, Font.BOLD, 8));
			g().setColor(color);
			for (final RSItem i : methods.inventory.getItems(items)) {
				if (i.getID() != -1) {
					final Rectangle bounds = i.getComponent().getArea();
					final String name = i.getName();
					g().draw(bounds);
					g().drawString(name, i.getComponent().getAbsoluteX(), i
							.getComponent().getCenter().y);
				}
			}
		}
	}

	public void drawNearestGroundItem(final int id, final Color color,
			final int alpha) {
		final RSGroundItem item = methods.groundItems.getNearest(id);
		if (item != null) {
			methods.paint.tiles.drawTileOnScreen(item.getLocation(), color,
					alpha);
			methods.paint.tiles.drawTileMM(item.getLocation(), color, alpha);
		}
	}

	public void drawGroundItem(final RSGroundItem groundItem,
			final Color color, final int alpha) {
		if (groundItem != null) {
			methods.paint.tiles.drawTileOnScreen(groundItem.getLocation(),
					color, alpha);
			methods.paint.tiles.drawTileMM(groundItem.getLocation(), color,
					alpha);
		}
	}

	public void drawGroundItems(final int[] ids, final Color color,
			final int alpha) {
		final Filter<RSGroundItem> filter = new Filter<RSGroundItem>() {
			public boolean accept(final RSGroundItem gi) {
				return gi != null && gi.getItem() != null
						&& idMatch(gi.getItem().getID());
			}

			public boolean idMatch(final int id) {
				for (final int id2 : ids) {
					if (id2 == id) {
						return true;
					}
				}
				return false;
			}
		};
		final RSGroundItem[] items = methods.groundItems.getAll(filter);
		for (final RSGroundItem item : items) {
			methods.paint.tiles.drawTileOnScreen(item.getLocation(), color,
					alpha);
			methods.paint.tiles.drawTileMM(item.getLocation(), color, alpha);
		}
	}

	public Graphics2D g() {
		return methods.bot.get2DGraphics();
	}
}
