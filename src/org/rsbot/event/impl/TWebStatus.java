package org.rsbot.event.impl;

import java.awt.Graphics;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.service.WebQueue;
import org.rsbot.util.CacheWriter;
import org.rsbot.util.StringUtil;

/**
 * Draws the web cache and cache writer information.
 *
 * @author Timer
 */
public class TWebStatus implements TextPaintListener {
	public TWebStatus(final Bot bot) {
	}

	@Override
	public int drawLine(final Graphics render, int idx) {
		final String[] items = {"Web Queue", "Buffering: " + WebQueue.weAreBuffering + ", " + WebQueue.bufferingCount + " nodes.", "Speed Buffering: " + WebQueue.speedBuffer,
				"Cache Writer", "Queue Size: " + WebQueue.getCacheWriter().queueSize(0), "Remove queue size: " + WebQueue.getCacheWriter().queueSize(1), "Removing queue size: " + +WebQueue.getCacheWriter().queueSize(2), "Running: " + CacheWriter.IsRunning()};
		for (final String item : items) {
			StringUtil.drawLine(render, idx++, item);
		}
		return idx;
	}
}
