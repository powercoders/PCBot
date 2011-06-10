package org.rsbot.event.impl;


import org.rsbot.bot.Bot;
import org.rsbot.client.Client;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.util.StringUtil;

import java.awt.*;

public class TLogin implements TextPaintListener {
	private final Client client;

	public TLogin(final Bot bot) {
		client = bot.getClient();
	}

	public int drawLine(final Graphics render, int idx) {
		StringUtil.drawLine(render, idx++, "Username: " + client.getCurrentUsername());
		StringUtil.drawLine(render, idx++, "Password: " + client.getCurrentPassword());
		return idx;
	}
}