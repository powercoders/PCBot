package org.rsbot.script.paint;

import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.paint.methods.DrawAreas;
import org.rsbot.script.paint.methods.DrawItems;
import org.rsbot.script.paint.methods.DrawModels;
import org.rsbot.script.paint.methods.DrawMouse;
import org.rsbot.script.paint.methods.DrawProgressBar;
import org.rsbot.script.paint.methods.DrawTiles;
import org.rsbot.script.paint.methods.DrawWeb;
import org.rsbot.script.paint.methods.FillModels;
import org.rsbot.script.paint.methods.Utility;

/**
 * 
 * @author Fletch To 99
 * @version 1.0
 * @since RSBot 2.48
 */
public class Paint extends PaintProvider {

	public final DrawAreas areas;
	public final DrawModels drawModels;
	public final FillModels fillModels;
	public final DrawItems items;
	public final DrawMouse mouse;
	public final Utility util;
	public final DrawProgressBar progressBar;
	public final DrawTiles tiles;
	public final DrawWeb web;
	

	public Paint(final MethodContext ctx) {
		super(ctx);
		areas = new DrawAreas(methods);
		items = new DrawItems(methods);
		drawModels = new DrawModels(methods);
		mouse = new DrawMouse(methods);
		progressBar = new DrawProgressBar(methods);
		tiles = new DrawTiles(methods);
		web = new DrawWeb(methods);
		fillModels = new FillModels(methods);
		util = new Utility(methods);
	}
}
