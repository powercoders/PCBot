package org.rsbot.event.impl;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.util.StringUtil;

import java.awt.*;

public class TUserInputAllowed implements TextPaintListener {

	private final Bot bot;

	public TUserInputAllowed(final Bot bot) {
		this.bot = bot;
	}

	@Override
	public int drawLine(final Graphics render, int idx) {
		final String i = bot.overrideInput || bot.inputFlags == 3 ? "[green]Enabled" :
				"[red]Disabled (" + bot.inputFlags + ")";
		StringUtil.drawLine(render, idx++, "User Input: " + i);
		return idx;
	}
}
