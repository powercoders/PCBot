package org.rsbot.event.impl;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.script.methods.Menu;
import org.rsbot.service.WebQueue;
import org.rsbot.util.CacheWriter;
import org.rsbot.util.StringUtil;

import java.awt.*;

public class TWebStatus implements TextPaintListener {

	private final Menu menu;

	public TWebStatus(Bot bot) {
		menu = bot.getMethodContext().menu;
	}

	public int drawLine(final Graphics render, int idx) {
		final String[] items = {"Web Queue", "Buffering: " + WebQueue.weAreBuffering + ", " + WebQueue.bufferingCount + " nodes.", "Speed Buffering: " + WebQueue.speedBuffer,
				"Cache Writer", "Queue Size: " + WebQueue.getCacheWriter().queueSize(0), "Remove queue size: " + WebQueue.getCacheWriter().queueSize(1), "Removing queue size: " + +WebQueue.getCacheWriter().queueSize(2), "Running: " + CacheWriter.IsRunning()};
		for (final String item : items) {
			StringUtil.drawLine(render, idx++, item);
		}
		return idx;
	}
}
