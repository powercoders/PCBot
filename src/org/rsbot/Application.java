package org.rsbot;

import org.rsbot.bot.Bot;
import org.rsbot.gui.BotGUI;
import org.rsbot.gui.LoadScreen;

import javax.swing.*;
import java.awt.*;

public class Application {
	private static BotGUI gui;

	public static void main(final String[] args) {
		for (String arg : args) {
			if (arg == null) {
				continue;
			}
			if (arg.equals("-betabuild")) {
				// Allows us to run a as if it was a beta build in an IDE environment
				Configuration.betaBuild = true;
			}
		}
		if (Configuration.isBetaBuild()) {
			JOptionPane.showMessageDialog(
					null,
					"You are running a beta build. Be aware that there might be some bugs.",
					"Beta build",
					JOptionPane.INFORMATION_MESSAGE);
		}
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
