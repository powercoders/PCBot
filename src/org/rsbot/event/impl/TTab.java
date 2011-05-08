package org.rsbot.event.impl;

import java.awt.Graphics;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.methods.Game;
import org.rsbot.util.StringUtil;

public class TTab implements TextPaintListener {

	private final Game game;

	public TTab(final Bot bot) {
		game = bot.getMethodContext().game;
	}

	@Override
	public int drawLine(final Graphics render, int idx) {
		final int cTab = game.getCurrentTab();
		StringUtil.drawLine(render, idx++,
				"Current Tab: " + cTab + (cTab != -1 ? " (" + Game.TAB_NAMES[cTab] + ")" : ""));
		return idx;
	}

}
