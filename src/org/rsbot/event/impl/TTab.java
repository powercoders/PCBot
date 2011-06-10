package org.rsbot.event.impl;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Game.Tab;
import org.rsbot.util.StringUtil;

import java.awt.*;

public class TTab implements TextPaintListener {
	private final Game game;

	public TTab(final Bot bot) {
		game = bot.getMethodContext().game;
	}

	public int drawLine(final Graphics render, int idx) {
		final Tab cTab = game.getTab();
		StringUtil.drawLine(render, idx++, "Current Tab: " + cTab.description() + " (" + cTab.index() + ")");
		return idx;
	}

}
