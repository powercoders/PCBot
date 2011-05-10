package org.rsbot.gui;

import org.rsbot.bot.Bot;
import org.rsbot.event.impl.*;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.TextPaintListener;
import org.rsbot.util.GlobalConfiguration;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class BotMenuBar extends JMenuBar {
	private static final long serialVersionUID = 971579975301998332L;
	public static final Map<String, Class<?>> DEBUG_MAP = new LinkedHashMap<String, Class<?>>();
	public static final String[] TITLES;
	public static final String[][] ELEMENTS;

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
		DEBUG_MAP.put("Web Status", TWebStatus.class);

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

		TITLES = new String[]{Messages.FILE, Messages.EDIT, Messages.VIEW, Messages.HELP};
		ELEMENTS = new String[][]{
				{Messages.NEWBOT, Messages.CLOSEBOT, Messages.MENUSEPERATOR,
						Messages.SERVICEKEY, Messages.ADDSCRIPT, Messages.RUNSCRIPT, Messages.STOPSCRIPT, Messages.PAUSESCRIPT, Messages.MENUSEPERATOR,
						Messages.SAVESCREENSHOT, Messages.MENUSEPERATOR,
						Messages.HIDEBOT, Messages.EXIT},
				{"Accounts", Messages.MENUSEPERATOR,
						"ToggleF Force Input", "ToggleF Disable Rendering", "ToggleF Disable Canvas", Messages.MENUSEPERATOR,
						"ToggleF Disable Anti-Randoms", "ToggleF Disable Auto Login", Messages.MENUSEPERATOR,
						"ToggleF Disable Advertisements", "ToggleF Disable Monitoring", "ToggleF Disable Confirmations"}, constructDebugs(),
				{"Site", "Project", "About"}};
	}

	private static String[] constructDebugs() {
		final List<String> debugItems = new ArrayList<String>();
		debugItems.add("Hide Toolbar");
		debugItems.add("Hide Log Window");
		debugItems.add("All Debugging");
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
			if (!TextPaintListener.class.isAssignableFrom(el) && !PaintListener.class.isAssignableFrom(el)) {
				debugItems.add(key);
			}
		}
		for (final ListIterator<String> it = debugItems.listIterator(); it.hasNext();) {
			final String s = it.next();
			if (!s.equals(Messages.MENUSEPERATOR)) {
				it.set("ToggleF " + s);
			}
		}
		return debugItems.toArray(new String[debugItems.size()]);
	}

	private void constructItemIcons() {
		final HashMap<String, String> map = new HashMap<String, String>(16);
		map.put(Messages.NEWBOT, GlobalConfiguration.Paths.Resources.ICON_APPADD);
		map.put(Messages.CLOSEBOT, GlobalConfiguration.Paths.Resources.ICON_APPDELETE);
		map.put(Messages.SERVICEKEY, GlobalConfiguration.Paths.Resources.ICON_KEY);
		map.put(Messages.ADDSCRIPT, GlobalConfiguration.Paths.Resources.ICON_SCRIPT_ADD);
		map.put(Messages.RUNSCRIPT, GlobalConfiguration.Paths.Resources.ICON_PLAY);
		map.put(Messages.STOPSCRIPT, GlobalConfiguration.Paths.Resources.ICON_DELETE);
		map.put(Messages.PAUSESCRIPT, GlobalConfiguration.Paths.Resources.ICON_PAUSE);
		map.put(Messages.SAVESCREENSHOT, GlobalConfiguration.Paths.Resources.ICON_PHOTO);
		map.put(Messages.HIDEBOT, GlobalConfiguration.Paths.Resources.ICON_TRAY_DOWN);
		map.put(Messages.EXIT, GlobalConfiguration.Paths.Resources.ICON_CLOSE);
		map.put("Accounts", GlobalConfiguration.Paths.Resources.ICON_REPORTKEY);
		map.put("Site", GlobalConfiguration.Paths.Resources.ICON_WEBLINK);
		map.put("Project", GlobalConfiguration.Paths.Resources.ICON_USEREDIT);
		map.put("About", GlobalConfiguration.Paths.Resources.ICON_INFO);
		for (final Entry<String, String> item : map.entrySet()) {
			final JMenuItem menu = commandMenuItem.get(item.getKey());
			menu.setIcon(new ImageIcon(GlobalConfiguration.getImage(item.getValue())));
		}
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
		commandMenuItem.get(Messages.SERVICEKEY).setVisible(false);
		commandCheckMap.get("Disable Monitoring").setVisible(false);
	}

	public void setOverrideInput(final boolean force) {
		commandCheckMap.get("Force Input").setSelected(force);
	}

	public void setPauseScript(final boolean pause) {
		commandMenuItem.get(Messages.PAUSESCRIPT).setText(pause ? "Resume Script" : Messages.PAUSESCRIPT);
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
			disable("All Debugging", "Force Input", "Disable Rendering", "Disable Canvas", "Disable Anti-Randoms", "Disable Auto Login");
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
			enable("All Debugging", selections == eventCheckMap.size());
			enable("Force Input", bot.overrideInput);
			enable("Disable Rendering", bot.disableRendering);
			enable("Disable Canvas", bot.disableCanvas);
			enable("Disable Anti-Randoms", bot.disableRandoms);
			enable("Disable Auto Login", bot.disableAutoLogin);
		}
	}

	public JCheckBoxMenuItem getCheckBox(final String key) {
		return commandCheckMap.get(key);
	}

	private void disable(final String... items) {
		for (final String item : items) {
			commandCheckMap.get(item).setSelected(false);
			commandCheckMap.get(item).setEnabled(false);
		}
	}

	public void enable(final String item, final boolean selected) {
		commandCheckMap.get(item).setSelected(selected);
		commandCheckMap.get(item).setEnabled(true);
	}

	public void doClick(final String item) {
		commandMenuItem.get(item).doClick();
	}

	public void loadPrefs() {
		final String path = GlobalConfiguration.Paths.getMenuBarPrefs();
		if (!new File(path).exists()) {
			return;
		}
		FileReader freader = null;
		BufferedReader in = null;
		try {
			freader = new FileReader(path);
			in = new BufferedReader(freader);
			String line;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (commandCheckMap.containsKey(line)) {
					commandCheckMap.get(line).doClick();
				}
			}
		} catch (final IOException ioe) {
			try {
				if (in != null) {
					in.close();
				}
				if (freader != null) {
					freader.close();
				}
			} catch (final IOException ioe1) {
			}
		}
	}

	public void savePrefs() {
		final String path = GlobalConfiguration.Paths.getMenuBarPrefs();
		FileWriter fstream = null;
		BufferedWriter out = null;
		try {
			final File f = new File(path);
			if (f.exists()) {
				f.delete();
			}
			fstream = new FileWriter(path);
			out = new BufferedWriter(fstream);
			for (final Entry<String, JCheckBoxMenuItem> item : commandCheckMap.entrySet()) {
				final boolean checked = item.getValue().isSelected();
				if (!checked) {
					continue;
				}
				out.write(item.getKey());
				out.newLine();
			}
		} catch (final IOException ioe) {
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (fstream != null) {
					fstream.close();
				}
			} catch (final IOException ioe1) {
			}
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
}