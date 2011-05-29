package org.rsbot.gui;

import java.awt.Image;
import java.awt.SystemTray;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import org.rsbot.Configuration;
import org.rsbot.bot.Bot;
import org.rsbot.event.impl.DrawBoundaries;
import org.rsbot.event.impl.DrawInventory;
import org.rsbot.event.impl.DrawItems;
import org.rsbot.event.impl.DrawModel;
import org.rsbot.event.impl.DrawMouse;
import org.rsbot.event.impl.DrawNPCs;
import org.rsbot.event.impl.DrawObjects;
import org.rsbot.event.impl.DrawPlayers;
import org.rsbot.event.impl.DrawSettings;
import org.rsbot.event.impl.DrawWeb;
import org.rsbot.event.impl.MessageLogger;
import org.rsbot.event.impl.TAnimation;
import org.rsbot.event.impl.TCamera;
import org.rsbot.event.impl.TFPS;
import org.rsbot.event.impl.TFloorHeight;
import org.rsbot.event.impl.TLoginIndex;
import org.rsbot.event.impl.TMenu;
import org.rsbot.event.impl.TMenuActions;
import org.rsbot.event.impl.TMousePosition;
import org.rsbot.event.impl.TPlayerPosition;
import org.rsbot.event.impl.TTab;
import org.rsbot.event.impl.TUserInputAllowed;
import org.rsbot.event.impl.TWebStatus;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.gui.component.Messages;

/**
 * @author Paris
 * @author Timer
 */
public class BotMenuBar extends JMenuBar {
	private static final long serialVersionUID = 971579975301998332L;
	public static final Map<String, Class<?>> DEBUG_MAP = new LinkedHashMap<String, Class<?>>();
	public static final String[] TITLES;
	public static final String[][] ELEMENTS;

	private static final boolean EXTD_VIEW_INITIAL = !Configuration.RUNNING_FROM_JAR;
	private static final String[] EXTD_VIEW_ITEMS = { "Game State",
			"Current Tab", "Camera", "Floor Height", "Mouse Position",
			"User Input Allowed", "Menu", "Menu Actions", "Cache", "Models",
			"Calc Test", "Settings" };

	static {
		// Text
		DEBUG_MAP.put("Game State", TLoginIndex.class);
		DEBUG_MAP.put("Current Tab", TTab.class);
		DEBUG_MAP.put("Camera", TCamera.class);
		DEBUG_MAP.put("Animation", TAnimation.class);
		DEBUG_MAP.put("Floor Height", TFloorHeight.class);
		DEBUG_MAP.put("Player Position", TPlayerPosition.class);
		DEBUG_MAP.put("Mouse Position", TMousePosition.class);
		DEBUG_MAP.put("User Input Allowed", TUserInputAllowed.class);
		DEBUG_MAP.put("Menu Actions", TMenuActions.class);
		DEBUG_MAP.put("Menu", TMenu.class);
		DEBUG_MAP.put("FPS", TFPS.class);
		DEBUG_MAP.put("Cache", TWebStatus.class);

		// Paint
		DEBUG_MAP.put("Players", DrawPlayers.class);
		DEBUG_MAP.put("NPCs", DrawNPCs.class);
		DEBUG_MAP.put("Objects", DrawObjects.class);
		DEBUG_MAP.put("Models", DrawModel.class);
		DEBUG_MAP.put("Mouse", DrawMouse.class);
		DEBUG_MAP.put("Inventory", DrawInventory.class);
		DEBUG_MAP.put("Ground Items", DrawItems.class);
		DEBUG_MAP.put("Calc Test", DrawBoundaries.class);
		DEBUG_MAP.put("Settings", DrawSettings.class);
		DEBUG_MAP.put("Web", DrawWeb.class);

		// Other
		DEBUG_MAP.put("Log Messages", MessageLogger.class);

		TITLES = new String[] { Messages.FILE, Messages.EDIT, Messages.VIEW,
				Messages.TOOLS, Messages.HELP };
		ELEMENTS = new String[][] {
				{ Messages.NEWBOT, Messages.CLOSEBOT, Messages.MENUSEPERATOR,
						Messages.ADDSCRIPT, Messages.RUNSCRIPT,
						Messages.STOPSCRIPT, Messages.PAUSESCRIPT,
						Messages.MENUSEPERATOR, Messages.SAVESCREENSHOT,
						Messages.MENUSEPERATOR, Messages.HIDEBOT, Messages.EXIT },
				{
						Messages.ACCOUNTS,
						Messages.MENUSEPERATOR,
						Messages.TOGGLEFALSE + Messages.FORCEINPUT,
						Messages.TOGGLEFALSE + Messages.LESSCPU,
						(EXTD_VIEW_INITIAL ? Messages.TOGGLETRUE
								: Messages.TOGGLEFALSE) + Messages.EXTDVIEWS,
						Messages.MENUSEPERATOR,
						Messages.TOGGLEFALSE + Messages.DISABLEANTIRANDOMS,
						Messages.TOGGLEFALSE + Messages.DISABLEAUTOLOGIN },
				constructDebugs(), { Messages.CLEARCACHE, Messages.OPTIONS },
				{ Messages.SITE, Messages.PROJECT, Messages.ABOUT } };
	}

	private static String[] constructDebugs() {
		final List<String> debugItems = new ArrayList<String>();
		debugItems.add(Messages.HIDETOOLBAR);
		debugItems.add(Messages.HIDELOGPANE);
		debugItems.add(Messages.ALLDEBUGGING);
		debugItems.add(Messages.MENUSEPERATOR);
		for (final String key : DEBUG_MAP.keySet()) {
			final Class<?> el = DEBUG_MAP.get(key);
			if (PaintListener.class.isAssignableFrom(el)) {
				debugItems.add(key);
			}
		}
		debugItems.add(Messages.MENUSEPERATOR);
		for (final String key : DEBUG_MAP.keySet()) {
			final Class<?> el = DEBUG_MAP.get(key);
			if (TextPaintListener.class.isAssignableFrom(el)) {
				debugItems.add(key);
			}
		}
		debugItems.add(Messages.MENUSEPERATOR);
		for (final String key : DEBUG_MAP.keySet()) {
			final Class<?> el = DEBUG_MAP.get(key);
			if (!TextPaintListener.class.isAssignableFrom(el)
					&& !PaintListener.class.isAssignableFrom(el)) {
				debugItems.add(key);
			}
		}
		for (final ListIterator<String> it = debugItems.listIterator(); it.hasNext();) {
			final String s = it.next();
			if (!s.equals(Messages.MENUSEPERATOR)) {
				it.set(Messages.TOGGLEFALSE + s);
			}
		}
		return debugItems.toArray(new String[debugItems.size()]);
	}

	private final Map<String, JCheckBoxMenuItem> eventCheckMap = new HashMap<String, JCheckBoxMenuItem>();

	private final Map<String, JCheckBoxMenuItem> commandCheckMap = new HashMap<String, JCheckBoxMenuItem>();
	private final Map<String, JMenuItem> commandMenuItem = new HashMap<String, JMenuItem>();
	private final ActionListener listener;

	public BotMenuBar(final ActionListener listener) {
		this.listener = listener;
		for (int i = 0; i < TITLES.length; i++) {
			final String title = TITLES[i];
			final String[] elems = ELEMENTS[i];
			add(constructMenu(title, elems));
		}
		constructItemIcons();
		commandMenuItem.get(Messages.HIDEBOT).setVisible(SystemTray.isSupported());
		setExtendedView(EXTD_VIEW_INITIAL);
	}

	private void constructItemIcons() {
		final HashMap<String, String> map = new HashMap<String, String>(16);
		map.put(Messages.NEWBOT, Configuration.Paths.Resources.ICON_APPADD);
		map.put(Messages.CLOSEBOT, Configuration.Paths.Resources.ICON_APPDELETE);
		map.put(Messages.ADDSCRIPT, Configuration.Paths.Resources.ICON_SCRIPT_ADD);
		map.put(Messages.RUNSCRIPT, Configuration.Paths.Resources.ICON_PLAY);
		map.put(Messages.STOPSCRIPT, Configuration.Paths.Resources.ICON_DELETE);
		map.put(Messages.PAUSESCRIPT, Configuration.Paths.Resources.ICON_PAUSE);
		map.put(Messages.SAVESCREENSHOT, Configuration.Paths.Resources.ICON_PHOTO);
		map.put(Messages.HIDEBOT, Configuration.Paths.Resources.ICON_ARROWIN);
		map.put(Messages.EXIT, Configuration.Paths.Resources.ICON_CLOSE);
		map.put(Messages.ACCOUNTS, Configuration.Paths.Resources.ICON_REPORTKEY);
		map.put(Messages.CLEARCACHE, Configuration.Paths.Resources.DATABASE_ERROR);
		map.put(Messages.OPTIONS, Configuration.Paths.Resources.ICON_WRENCH);
		map.put(Messages.SITE, Configuration.Paths.Resources.ICON_WEBLINK);
		map.put(Messages.PROJECT, Configuration.Paths.Resources.ICON_GITHUB);
		map.put(Messages.ABOUT, Configuration.Paths.Resources.ICON_INFO);
		for (final Entry<String, String> item : map.entrySet()) {
			final JMenuItem menu = commandMenuItem.get(item.getKey());
			menu.setIcon(new ImageIcon(Configuration.getImage(item.getValue())));
		}
	}

	private JMenu constructMenu(final String title, final String[] elems) {
		final JMenu menu = new JMenu(title);
		for (String e : elems) {
			if (e.equals(Messages.MENUSEPERATOR)) {
				menu.add(new JSeparator());
			} else {
				JMenuItem jmi;
				if (e.startsWith(Messages.TOGGLE)) {
					e = e.substring(Messages.TOGGLE.length());
					final char state = e.charAt(0);
					e = e.substring(2);
					jmi = new JCheckBoxMenuItem(e);
					if (state == 't' || state == 'T') {
						jmi.setSelected(true);
					}
					if (DEBUG_MAP.containsKey(e)) {
						final JCheckBoxMenuItem ji = (JCheckBoxMenuItem) jmi;
						eventCheckMap.put(e, ji);
					}
					final JCheckBoxMenuItem ji = (JCheckBoxMenuItem) jmi;
					commandCheckMap.put(e, ji);
				} else {
					jmi = new JMenuItem(e);
					commandMenuItem.put(e, jmi);
				}
				jmi.addActionListener(listener);
				jmi.setActionCommand(title + "." + e);
				menu.add(jmi);
			}
		}
		return menu;
	}

	private void disable(final String... items) {
		for (final String item : items) {
			commandCheckMap.get(item).setSelected(false);
			commandCheckMap.get(item).setEnabled(false);
		}
	}

	public void doClick(final String item) {
		commandMenuItem.get(item).doClick();
	}

	public void doTick(final String item) {
		commandCheckMap.get(item).doClick();
	}

	public void enable(final String item, final boolean selected) {
		commandCheckMap.get(item).setSelected(selected);
		commandCheckMap.get(item).setEnabled(true);
	}

	public JCheckBoxMenuItem getCheckBox(final String key) {
		return commandCheckMap.get(key);
	}

	public JMenuItem getMenuItem(final String name) {
		return commandMenuItem.get(name);
	}

	public boolean isTicked(final String item) {
		return commandCheckMap.get(item).isSelected();
	}

	public void setBot(final Bot bot) {
		if (bot == null) {
			commandMenuItem.get(Messages.CLOSEBOT).setEnabled(false);
			commandMenuItem.get(Messages.RUNSCRIPT).setEnabled(false);
			commandMenuItem.get(Messages.STOPSCRIPT).setEnabled(false);
			commandMenuItem.get(Messages.PAUSESCRIPT).setEnabled(false);
			commandMenuItem.get(Messages.SAVESCREENSHOT).setEnabled(false);
			for (final JCheckBoxMenuItem item : eventCheckMap.values()) {
				item.setSelected(false);
				item.setEnabled(false);
			}
			disable(Messages.ALLDEBUGGING, Messages.FORCEINPUT, Messages.LESSCPU, Messages.DISABLEANTIRANDOMS, Messages.DISABLEAUTOLOGIN);
		} else {
			commandMenuItem.get(Messages.CLOSEBOT).setEnabled(true);
			commandMenuItem.get(Messages.RUNSCRIPT).setEnabled(true);
			commandMenuItem.get(Messages.STOPSCRIPT).setEnabled(true);
			commandMenuItem.get(Messages.PAUSESCRIPT).setEnabled(true);
			commandMenuItem.get(Messages.SAVESCREENSHOT).setEnabled(true);
			int selections = 0;
			for (final Map.Entry<String, JCheckBoxMenuItem> entry : eventCheckMap.entrySet()) {
				entry.getValue().setEnabled(true);
				final boolean selected = bot.hasListener(DEBUG_MAP.get(entry.getKey()));
				entry.getValue().setSelected(selected);
				if (selected) {
					++selections;
				}
			}
			enable(Messages.ALLDEBUGGING, selections == eventCheckMap.size());
			enable(Messages.FORCEINPUT, bot.overrideInput);
			enable(Messages.LESSCPU, bot.disableRendering);
			enable(Messages.DISABLEANTIRANDOMS, bot.disableRandoms);
			enable(Messages.DISABLEAUTOLOGIN, bot.disableAutoLogin);
		}
	}

	public void setEnabled(final String item, final boolean mode) {
		commandCheckMap.get(item).setEnabled(mode);
	}

	public void setExtendedView(final boolean show) {
		for (final String disableFeature : EXTD_VIEW_ITEMS) {
			if (commandCheckMap.containsKey(disableFeature)) {
				commandCheckMap.get(disableFeature).setVisible(show);
			}
		}
	}

	public void setOverrideInput(final boolean force) {
		commandCheckMap.get(Messages.FORCEINPUT).setSelected(force);
	}

	public void setPauseScript(final boolean pause) {
		final JMenuItem item = commandMenuItem.get(Messages.PAUSESCRIPT);
		item.setText(pause ? Messages.RESUMESCRIPT : Messages.PAUSESCRIPT);
		final Image image = Configuration.getImage(pause ? Configuration.Paths.Resources.ICON_START
				: Configuration.Paths.Resources.ICON_PAUSE);
		if (image != null) {
			item.setIcon(new ImageIcon(image));
		}
	}
}
