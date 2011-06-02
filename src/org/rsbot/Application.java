package org.rsbot;

import org.rsbot.bot.Bot;
import org.rsbot.gui.BotGUI;
import org.rsbot.gui.LoadScreen;

import java.awt.*;

public class Application {
	private static BotGUI gui;

	public static void main(final String[] args) {
		final LoadScreen loader = new LoadScreen();
		if (!loader.error) {
			gui = new BotGUI();
			loader.dispose();
			gui.setVisible(true);
		}
	}

	public static Bot getBot(final Object o) {
		return gui.getBot(o);
	}

	public static Dimension getPanelSize() {
		return gui.getPanel().getSize();
	}
}
