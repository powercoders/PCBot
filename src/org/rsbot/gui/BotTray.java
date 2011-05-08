package org.rsbot.gui;

import org.rsbot.bot.Bot;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Gamer
 * Date: 4/27/11
 * Time: 5:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class BotTray {

    private TrayIcon tray;
    private Image trayImage;
    private BotGUI parent;

    private List<Bot> bots = new ArrayList<Bot>();

    private PopupMenu trayMenu;
    private MenuItem HIDE_SHOW = new MenuItem("Hide RSBot");

    protected BotTray(BotGUI parent) {
         this.parent = parent;
         trayImage = parent.getIconImage();

        createTray();
    }

    private void createTray() {
        tray = new TrayIcon(parent.getIconImage(), "RSBot");

        tray.setToolTip(parent.getTitle());
        tray.setImageAutoSize(true);

        try {
            SystemTray.getSystemTray().add(tray);
        } catch (AWTException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private PopupMenu generateMenu() {
        PopupMenu menu = null;
        try {

            bots = parent.getBots();
            menu = new PopupMenu();
            final MenuItem visibility = new MenuItem(parent.isVisible() ? "Hide RSBot" : "Show RSBot");

            tray.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    parent.setVisible(!parent.isVisible());
                    visibility.setLabel(parent.isVisible() ? "Hide RSBot" : "Show RSBot");
                }
            });

            visibility.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    parent.setVisible(!parent.isVisible());
                    visibility.setLabel(parent.isVisible() ? "Hide RSBot" : "Show RSBot");
                }
            });

            MenuItem exit = new MenuItem("Exit RSBot!");
            exit.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    parent.cleanExit();
                }
            });

            Menu botsMenu = new Menu("Bots...");

            menu.add(botsMenu);
            menu.add(visibility);
            menu.add(exit);

            for (int i = 0; i < bots.size(); i++) {
                final Bot bot = bots.get(i);
                if (bot == null)
                    continue;
                Menu botMenu = new Menu(bot.getAccountName() == null || bot.getAccountName().isEmpty() ? "RuneScape Bot #" + i : bot.getAccountName());
                if (bot.getScriptHandler().getRunningScripts().size() > 0) {
                    MenuItem stopScript = new MenuItem("Stop...");
                    stopScript.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            parent.setVisible(true);
                            parent.requestFocus();
                            parent.showStopScript(bot);
                        }
                    });

                    MenuItem pauseScript = new MenuItem("Pause/Resume");
                    pauseScript.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            parent.pauseScript(bot);
                        }
                    });

                    botMenu.add(stopScript);
                    botMenu.add(pauseScript);
                } else {
                    MenuItem startScript = new MenuItem("Start...");
                    startScript.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            parent.setVisible(true);
                            parent.requestFocus();
                            parent.showScriptSelector(bot);
                        }
                    });
                    botMenu.add(startScript);
                }

                 MenuItem destroyBot = new MenuItem("Destroy");
                    destroyBot.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            parent.setVisible(true);
                            parent.requestFocus();
                            parent.removeBot(bot);
                        }
                    });

                    botMenu.add(destroyBot);

                botsMenu.add(botMenu);
            }

        } catch (Exception i) {
            i.printStackTrace();
        }

        return menu;
    }

    protected void updateMenu() {
        trayMenu = generateMenu();
        tray.setPopupMenu(trayMenu);
    }

    public void displayInformation(String title, String content) {
        tray.displayMessage(title, content, TrayIcon.MessageType.INFO);
    }

    public void displayError(String title, String content) {
        tray.displayMessage(title, content, TrayIcon.MessageType.ERROR);
    }

    public void displayWarning(String title, String content) {
        tray.displayMessage(title, content, TrayIcon.MessageType.WARNING);
    }
}
