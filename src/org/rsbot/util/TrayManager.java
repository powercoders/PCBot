package org.rsbot.util;

import org.rsbot.gui.BotGUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Timer
 */
public class TrayManager {
	private static final TrayIcon trayIcon;

	static {
		trayIcon = new TrayIcon(GlobalConfiguration.getImage(GlobalConfiguration.Paths.Resources.ICON).getScaledInstance(
				SystemTray.getSystemTray().getTrayIconSize().width, SystemTray.getSystemTray().getTrayIconSize().height, 0),
				"RSBot", generateMenu());
		trayIcon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SystemTray.getSystemTray().remove(trayIcon);
				BotGUI.showGUI();
			}
		});
	}

	public static void Hide() throws AWTException {
		SystemTray.getSystemTray().add(trayIcon);
		BotGUI.hideGUI();
	}

	private static PopupMenu generateMenu() {
		PopupMenu menu = new PopupMenu();
		MenuItem release = new MenuItem("Restore");
		release.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SystemTray.getSystemTray().remove(trayIcon);
				BotGUI.showGUI();
			}
		});
		menu.add(release);
		return menu;
	}
}

