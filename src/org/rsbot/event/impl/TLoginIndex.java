package org.rsbot.event.impl;

import java.awt.Graphics;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.methods.Game;
import org.rsbot.util.StringUtil;

public class TLoginIndex implements TextPaintListener {

	private final Game game;

	public TLoginIndex(final Bot bot) {
		game = bot.getMethodContext().game;
	}

	@Override
	public int drawLine(final Graphics render, int idx) {
		StringUtil.drawLine(render, idx++, "Client State: "
				+ game.getClientState());
		return idx;
	}

}
