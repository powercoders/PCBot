package org.rsbot.script.methods;

import org.rsbot.script.Random;
import org.rsbot.script.randoms.*;
import org.rsbot.script.wrappers.RSComponent;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSTile;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * Game state and GUI operations.
 */
public class Game extends MethodProvider {
	/**
	 * Different Types of Chat Modes
	 */
	public enum ChatMode {
		VIEW, ON, FRIENDS, OFF, HIDE, ALL, FILTER
	}

	/**
	 * The chat filter buttons
	 *
	 * @author kiko
	 */
	public enum ChatButton {
		ALL(2, -1, 31),
		GAME(3, 30, 28, ChatMode.ALL, ChatMode.FILTER),
		PUBLIC(4, 27, 25, ChatMode.ON, ChatMode.FRIENDS, ChatMode.OFF, ChatMode.HIDE),
		PRIVATE(5, 24, 22, ChatMode.ON, ChatMode.FRIENDS, ChatMode.OFF),
		FRIENDS(7, 35, 33, ChatMode.ON, ChatMode.FRIENDS, ChatMode.OFF),
		CLAN(6, 21, 19, ChatMode.ON, ChatMode.FRIENDS, ChatMode.OFF),
		TRADE(8, 18, 16, ChatMode.ON, ChatMode.FRIENDS, ChatMode.OFF),
		ASSIST(9, 15, 13, ChatMode.ON, ChatMode.FRIENDS, ChatMode.OFF);

		private final int component;
		private final int textComponent;
		private final int selectComponent;
		private final ChatMode[] options;

		ChatButton(final int component, final int textComponent, final int selectComponent, final ChatMode... options) {
			this.component = component;
			this.textComponent = textComponent;
			this.selectComponent = selectComponent;
			this.options = options;
		}

		public boolean hasMode(final ChatMode mode) {
			if (mode == ChatMode.VIEW) {
				return true;
			}
			for (ChatMode option : options) {
				if (mode == option) {
					return true;
				}
			}
			return false;
		}

		public int idx() {
			return component;
		}

		public int selectIdx() {
			return selectComponent;
		}

		public int textIdx() {
			return textComponent;
		}
	}

	/**
	 * The game tabs
	 *
	 * @author kiko
	 */
	public enum Tab {
		NONE ("None", 0, -1, false),
		ATTACK ("Combat Styles", KeyEvent.VK_F5, 884, false),
		TASK ("Task System", 0, 1056, false),
		STATS ("Stats", 0, Skills.INTERFACE_TAB_STATS, false),
		QUESTS ("Quest Journal", 0, Quests.QUESTS, false),
		INVENTORY ("Inventory", KeyEvent.VK_F1, Inventory.INTERFACE_INVENTORY, true),
		EQUIPMENT ("Worn Equipment", KeyEvent.VK_F2, Equipment.INTERFACE_EQUIPMENT, true),
		PRAYER ("Prayer List", KeyEvent.VK_F3, Prayer.INTERFACE_PRAYER, false),
		MAGIC ("Magic Spellbook", KeyEvent.VK_F4, Magic.INTERFACE_DEFENSIVE_STANCE, false),
		SUMMONING ("Summoning", 0, Summoning.INTERFACE_TAB_SUMMONING, false),
		FRIENDS ("Friends List", 0, 550, false),
		FRIENDS_CHAT ("Friends Chat", 0, FriendChat.INTERFACE_FRIEND_CHAT, false),
		CLAN_CHAT ("Clan Chat", 0, ClanChat.INTERFACE_CLAN_CHAT, false),
		OPTIONS ("Options", 0, 261, false),
		EMOTES ("Emotes", 0, 464, false),
		MUSIC ("Music Player", 0, 187, false),
		LOGOUT ("Exit", 0, Game.INTERFACE_LOGOUT, false);

		final String description;
		final int functionKey;
		final int index;
		final int inter;
		final boolean refresh;

		Tab(final String description, final int functionKey, final int inter, final boolean refresh) {
			this.description = description;
			this.functionKey = functionKey;
			this.index = this.ordinal() - 1;
			this.inter = inter;
			this.refresh = refresh;
		}

		public String description() {
			return description;
		}

		public int functionKey() {
			return functionKey;
		}

		public boolean hasFunctionKey() {
			return functionKey != 0;
		}

		public int index() {
			return index;
		}

		public int inter() {
			return inter;
		}

		public boolean needsRefreshing() {
			return refresh;
		}
	}

	public static final int[] INDEX_LOGGED_IN = {10, 11};
	public static final int INDEX_LOGIN_SCREEN = 3;
	public static final int INDEX_LOBBY_SCREEN = 7;
	public static final int INDEX_FIXED = 746;

	@Deprecated
	public static final int[] TAB_FUNCTION_KEYS = {
			KeyEvent.VK_F5, // Attack
			0, // Achievements
			0, // Stats
			0, // Quests
			KeyEvent.VK_F1, // Inventory
			KeyEvent.VK_F2, // Equipment
			KeyEvent.VK_F3, // Prayer
			KeyEvent.VK_F4, // Magic
			0, // Summoning
			0, // Friends
			0, // Ignore
			0, // Clan
			0, // Options
			0, // Controls
			0, // Music
			0, // Notes
			0, // Logout
	};

	public static final int TAB_ATTACK = 0;
	public static final int TAB_TASK = 1;
	public static final int TAB_STATS = 2;
	public static final int TAB_QUESTS = 3;
	public static final int TAB_INVENTORY = 4;
	public static final int TAB_EQUIPMENT = 5;
	public static final int TAB_PRAYER = 6;
	public static final int TAB_MAGIC = 7;
	public static final int TAB_SUMMONING = 8; // Untested
	public static final int TAB_FRIENDS = 9;
	public static final int TAB_FRIENDS_CHAT = 10;
	public static final int TAB_CLAN = 11;
	public static final int TAB_CLAN_CHAT = 11;
	public static final int TAB_OPTIONS = 12;
	public static final int TAB_CONTROLS = 13;
	public static final int TAB_MUSIC = 14;
	public static final int TAB_NOTES = 15;
	public static final int TAB_LOGOUT = 16;

	public static final int CHAT_OPTION = 751;
	public static final int CHAT_OPTION_ALL = 2;
	public static final int CHAT_OPTION_GAME = 3;
	public static final int CHAT_OPTION_PUBLIC = 4;
	public static final int CHAT_OPTION_PRIVATE = 5;
	public static final int CHAT_OPTION_FRIENDS = 7;
	public static final int CHAT_OPTION_CLAN = 6;
	public static final int CHAT_OPTION_TRADE = 8;
	public static final int CHAT_OPTION_ASSIST = 9;

	public static final int INTERFACE_CHAT_BOX = 137;
	public static final int INTERFACE_GAME_SCREEN = 548;
	public static final int INTERFACE_LEVEL_UP = 740;
	public static final int INTERFACE_LOGOUT = 182;
	public static final int INTERFACE_LOGOUT_LOBBY = 1;
	public static final int INTERFACE_LOGOUT_COMPLETE = 6;
	public static final int INTERFACE_LOGOUT_BUTTON_FIXED = 181;
	public static final int INTERFACE_LOGOUT_BUTTON_RESIZED = 172;
	public static final int INTERFACE_WELCOME_SCREEN = 907;
	public static final int INTERFACE_WELCOME_SCREEN_CHILD = 150;
	public static final int INTERFACE_WELCOME_SCREEN_PLAY = 18;

	public static final int INTERFACE_HP_ORB = 748;
	public static final int INTERFACE_PRAYER_ORB = 749;

	public static final int[] INTERFACE_TALKS = {211, 241, 251, 101, 242,
			102, 161, 249, 243, 64, 65, 244, 255, 249, 230, 372, 421};
	public static final int[] INTERFACE_OPTIONS = {230, 228};

	@Deprecated
	public static final String[] TAB_NAMES = new String[]{"Combat Styles",
			"Task System", "Stats", "Quest Journals", "Inventory",
			"Worn Equipment", "Prayer List", "Magic Spellbook", "",
			"Friends List", "Friends Chat", "Clan Chat", "Options",
			"Emotes",
			"Music Player", "Notes", "Exit"};

	Game(final MethodContext ctx) {
		super(ctx);
	}

	/**
	 * Closes the currently open tab if in resizable mode.
	 */
	public void closeTab() {
		final Tab tab = getTab();
		if (isFixed() || tab == Tab.LOGOUT) {
			return;
		}
		final org.rsbot.client.RSInterface iTab = methods.gui.getTab(tab);
		if (iTab != null) {
			methods.interfaces.getComponent(iTab.getID()).doClick();
		}
	}

	/**
	 * Gets the x coordinate of the loaded map area (far west).
	 *
	 * @return The region base x.
	 */
	public int getBaseX() {
		return methods.client.getBaseX();
	}

	/**
	 * Gets the y coordinate of the loaded map area (far south).
	 *
	 * @return The region base y.
	 */
	public int getBaseY() {
		return methods.client.getBaseY();
	}

	/**
	 * Gets the game state.
	 *
	 * @return The game state.
	 */
	public int getClientState() {
		return methods.client.getLoginIndex();
	}

	/**
	 * Gets a color corresponding to x and y co ordinates from the current game screen.
	 *
	 * @param x: The x co ordinate at which to get the color.
	 * @param y: The y co ordinate at which to get the color.
	 * @return Color
	 * @see java.awt.color
	 */
	public Color getColorAtPoint(final int x, final int y) {
		final BufferedImage image = methods.env.takeScreenshot(false);
		return new Color(image.getRGB(x, y));
	}

	/**
	 * Gets the currently open tab.
	 *
	 * @return The currently open tab or the logout tab by default.
	 */
	@Deprecated
	public int getCurrentTab() {
		return getTab().index();
	}

	/**
	 * Access the last message spoken by a player.
	 *
	 * @return The last message spoken by a player or "" if none
	 */
	public String getLastMessage() {
		final RSInterface chatBox = methods.interfaces.get(INTERFACE_CHAT_BOX);
		for (int i = 279; i >= 180; i--) {// Valid text is from 180 to 279, was
			// 58-157
			final String text = chatBox.getComponent(i).getText();
			if (!text.isEmpty() && text.contains("<")) {
				return text;
			}
		}
		return "";
	}

	/**
	 * Gets the (x, y) coordinate pair of the south-western tile at the base of
	 * the loaded map area.
	 *
	 * @return The region base tile.
	 */
	public RSTile getMapBase() {
		return new RSTile(methods.client.getBaseX(), methods.client.getBaseY());
	}

	/**
	 * Gets the plane we are currently on. Typically 0 (ground level), but will
	 * increase when going up ladders. You cannot be on a negative plane. Most
	 * dungeons/basements are on plane 0 elsewhere on the world map.
	 *
	 * @return The current plane.
	 */
	public int getPlane() {
		return methods.client.getPlane();
	}

	/**
	 * Gets a random game tab (excludes the current, summoning, and logout tabs).
	 *
	 * @return Returns a random selectable game tab.
	 */
	public Tab getRandomTab() {
		LinkedList<Tab> tabs = new LinkedList<Tab>();
		Tab curr = getTab();
		for (Tab t : Tab.values()) {
			if (t != curr && t != Tab.SUMMONING & t != Tab.LOGOUT) {
				tabs.add(t);
			}
		}
		return tabs.get(random(0, tabs.size()));
	}

	/**
	 * Gets the currently open tab.
	 *
	 * @return The currently open tab.
	 */
	public Tab getTab() {
		for (Tab t : Tab.values()) {
			final org.rsbot.client.RSInterface tab = methods.gui.getTab(t);
			if (tab == null) {
				continue;
			}

			if (tab.getTextureID() != -1) {
				return t;
			}
		}
		return Tab.NONE;
	}

	/**
	 * Returns the valid chat component.
	 *
	 * @return <tt>RSInterfaceChild</tt> of the current valid talk interface;
	 *         otherwise null.
	 * @see #INTERFACE_TALKS
	 */
	public RSComponent getTalkInterface() {
		for (final int talk : INTERFACE_TALKS) {
			final RSComponent child = methods.interfaces.getComponent(talk, 0);
			if (child.isValid()) {
				return child;
			}
		}
		return null;
	}

	/**
	 * Gets the canvas height.
	 *
	 * @return The canvas' width.
	 */
	public int getWidth() {
		return methods.bot.getCanvas().getWidth();
	}

	/**
	 * Gets the canvas height.
	 *
	 * @return The canvas' height.
	 */
	public int getHeight() {
		return methods.bot.getCanvas().getHeight();
	}

	/**
	 * Excludes Loginbot, BankPin, TeleotherCloser, CloseAllInterface,
	 * ImprovedRewardsBox
	 *
	 * @return True if player is in a random
	 */
	public Boolean inRandom() {
		for (final Random random : methods.bot.getScriptHandler().getRandoms()) {
			if (random.getClass().equals(new LoginBot())
					|| random.getClass().equals(new BankPins())
					|| random.getClass().equals(new TeleotherCloser())
					|| random.getClass().equals(new CloseAllInterface())
					|| random.getClass().equals(new ImprovedRewardsBox())) {
				continue;
			} else {
				if (random.activateCondition()) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isButtonSelected(final ChatButton button) {
		return methods.interfaces.getComponent(CHAT_OPTION, button.selectIdx()).getBackgroundColor() == 1022;
	}

	/**
	 * Determines whether or not the client is currently in the fixed display
	 * mode.
	 *
	 * @return <tt>true</tt> if in fixed mode; otherwise <tt>false</tt>.
	 */
	public boolean isFixed() {
		return methods.client.getGUIRSInterfaceIndex() != INDEX_FIXED;
	}

	/**
	 * Determines whether or not the client is currently logged in to an
	 * account.
	 *
	 * @return <tt>true</tt> if logged in; otherwise <tt>false</tt>.
	 */
	public boolean isLoggedIn() {
		final org.rsbot.client.Client client = methods.client;
		final int index = client == null ? -1 : client.getLoginIndex();
		for (final int idx : INDEX_LOGGED_IN) {
			if (index == idx) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines whether or not the client is showing the login screen.
	 *
	 * @return <tt>true</tt> if the client is showing the login screen;
	 *         otherwise <tt>false</tt>.
	 */
	public boolean isLoginScreen() {
		return methods.client.getLoginIndex() == INDEX_LOGIN_SCREEN;
	}

	/**
	 * Checks whether or not the logout tab is selected.
	 *
	 * @return <tt>true</tt> if on the logout tab.
	 */
	@Deprecated
	public boolean isOnLogoutTab() {
		return getTab() == Tab.LOGOUT;
	}

	/**
	 * Determines whether or not the welcome screen is open.
	 *
	 * @return <tt>true</tt> if the client is showing the welcome screen;
	 *         otherwise <tt>false</tt>.
	 */
	public boolean isWelcomeScreen() {
		return methods.interfaces.get(INTERFACE_WELCOME_SCREEN)
				.getComponent(INTERFACE_WELCOME_SCREEN_CHILD).getAbsoluteY() > 2;
	}

	/**
	 * Runs the LoginBot random.
	 *
	 * @return <tt>true</tt> if random was run; otherwise <tt>false</tt>.
	 */
	public boolean login() {
		return new org.rsbot.script.randoms.LoginBot().activateCondition();
	}

	/**
	 * Closes the bank if it is open and logs out.
	 *
	 * @param lobby <tt>true</tt> if player should be logged out to the lobby
	 * @return <tt>true</tt> if the player was logged out.
	 */
	public boolean logout(final boolean lobby) {
		if (methods.bank.isOpen()) {
			if (methods.bank.close()) {
				sleep(random(200, 400));
			}
		}
		if (methods.bank.isOpen()) {
			return false;
		}
		if (methods.client.isSpellSelected() || methods.inventory.isItemSelected()) {
			final Tab currentTab = methods.game.getTab();
			int randomTab = random(1, 6);
			while (randomTab == currentTab.index()) {
				randomTab = random(1, 6);
			}
			if (methods.game.openTab(randomTab)) {
				sleep(random(400, 800));
			}
		}
		if (methods.client.isSpellSelected() || methods.inventory.isItemSelected()) {
			return false;
		}
		if (!isOnLogoutTab()) {
			final int idx = methods.client.getGUIRSInterfaceIndex();
			RSComponent exitComponent = methods.interfaces.getComponent(idx, isFixed() ? 181 : 173);
			if (exitComponent == null || !exitComponent.doClick()) {
				return false;
			}
			long time = System.currentTimeMillis();
			while (!isOnLogoutTab()) {
				if (System.currentTimeMillis() - time > 2000) {
					break;
				}
				sleep(random(50, 100));
			}
		}
		RSComponent exitToComponent = methods.interfaces.getComponent(INTERFACE_LOGOUT, lobby ? 5 : 10);
		if (exitToComponent.doClick()) {
			sleep(random(1500, 2000));
		}
		return !isLoggedIn();
	}

	/**
	 * Click chat button.
	 *
	 * @param button Which button? One of CHAT_OPTION
	 * @param left   Left or right button? Left = true. Right = false.
	 * @return <tt>true</tt> if it was clicked.
	 */
	@Deprecated
	public boolean mouseChatButton(final int button, final boolean left) {
		return mouseChatButton(getButton(button), left);
	}

	/**
	 * Click the specified chat button.
	 *
	 * @param button One of ChatButton
	 * @param left   true to left click, false for right click.
	 * @return <tt>true</tt> if the button was successfully clicked.
	 */
	public boolean mouseChatButton(final ChatButton button, final boolean left) {
		if (button == null || (left && isButtonSelected(button))) {
			return false;
		}
		final RSComponent chatButton = methods.interfaces.getComponent(CHAT_OPTION, button.idx());
		return chatButton.isValid() && chatButton.doClick(left);
	}

	/**
	 * Opens the specified tab at the specified index.
	 *
	 * @param tab         The tab to open, functionKey if wanting to use function keys
	 *                    to switch.
	 * @param functionKey Use a function key for fast switching?
	 * @return <tt>true</tt> if tab successfully selected; otherwise
	 *         <tt>false</tt>.
	 */
	@Deprecated
	public boolean open(final int tab, final boolean functionKey) {
		return openTab(getTab(tab), functionKey);
	}

	/**
	 * Opens the specified tab at the specified index.
	 *
	 * @param tab The tab to open.
	 * @return <tt>true</tt> if tab successfully selected; otherwise
	 *         <tt>false</tt>.
	 * @see #openTab(int tab, boolean functionKey)
	 */
	public boolean openTab(final int tab) {
		return openTab(getTab(tab));
	}

	/**
	 * Opens the specified game tab.
	 *
	 * @param tab The tab to open.
	 * @return <tt>true</tt> if tab successfully selected; otherwise
	 *         <tt>false</tt>.
	 * @see #openTab(Tab tab, boolean functionKey)
	 */
	public boolean openTab(final Tab tab) {
		return openTab(tab, false);
	}

	/**
	 * Opens the specified tab at the specified index.
	 *
	 * @param tab The tab to open, functionKey if wanting to use function keys
	 *            to switch.
	 * @return <tt>true</tt> if tab successfully selected; otherwise
	 *         <tt>false</tt>.
	 */
	@Deprecated
	public boolean openTab(final int tab, final boolean functionKey) {
		return openTab(getTab(tab), functionKey);
	}

	/**
	 * Opens the specified game tab.
	 *
	 * @param tab The tab to open, functionKey if wanting to use function keys
	 *            to switch.
	 * @return <tt>true</tt> if tab successfully selected; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean openTab(final Tab tab, final boolean functionKey) {
		if (tab == Tab.NONE) {
			return false;
		}
		if (tab == getTab()) {
			return true;
		}

		if (functionKey && tab.hasFunctionKey()) {
			methods.keyboard.pressKey((char) tab.functionKey());
			sleep(random(80, 200));
			methods.keyboard.releaseKey((char) tab.functionKey());
		} else {
			final org.rsbot.client.RSInterface iTab = methods.gui.getTab(tab);
			if (iTab == null || !methods.interfaces.getComponent(iTab.getID()).doClick()) {
				return false;
			}
		}

		boolean opened = false;
		for (int i = 0; i < 5; i++) {
			if (!opened) {
				opened = getTab() == tab;
				i--;
			} else if (methods.interfaces.get(tab.inter()).isValid()) {
				return true;
			} else if (i == 5) {
				return false;
			}
			sleep(random(100, 150));
		}
		if (tab.needsRefreshing()) {
			sleep(200);
		}
		return tab == getTab();
	}

	/**
	 * Sets the specified chat mode
	 *
	 * @param button one of ChatButton
	 * @param mode   one of ChatMode
	 * @return <tt>true</tt> if item was clicked correctly; otherwise <tt>false</tt>.
	 */
	@Deprecated
	public boolean setChatOption(final int button, final ChatMode mode) {
		return setChatOption(getButton(button), mode);
	}

	/**
	 * Sets the specified chat mode
	 *
	 * @param option one of ChatButton
	 * @param mode   one of ChatMode
	 * @return <tt>true</tt> if item was clicked correctly; otherwise <tt>false</tt>.
	 */
	public boolean setChatOption(final ChatButton option, final ChatMode mode) {
		if (option == null || !option.hasMode(mode)) {
			return false;
		}
		if (mode == ChatMode.VIEW) {
			return mouseChatButton(option, true);
		}
		final RSComponent chat = methods.interfaces.getComponent(CHAT_OPTION, option.textIdx());
		if (chat != null) {
			String setting = chat.getText();
			setting = setting.substring(setting.indexOf(">") + 1);
			if (setting.toUpperCase().equals(mode.toString())) {
				return false;
			}
		}
		mouseChatButton(option, false);
		return methods.menu.doAction(mode.toString());
	}

	/**
	 * Switches to a given world.
	 *
	 * @param world the world to switch to, must be valid.
	 */
	public boolean switchWorld(final int world) {
		methods.env.disableRandom("Login");
		if (methods.game.isLoggedIn()) {
			methods.game.logout(true);
			for (int i = 0; i < 50; i++) {
				sleep(100);
				if (methods.interfaces.get(906).isValid() && getClientState() == INDEX_LOBBY_SCREEN) {
					break;
				}
			}
		}

		if (!methods.interfaces.get(906).isValid()) {
			methods.env.enableRandom("Login");
			return false;
		}
		if (!methods.interfaces.get(910).isValid()) {
			final RSComponent worldSelect = methods.interfaces.getComponent(906, 189);
			if (worldSelect.doClick()) {
				sleep(1000);
			}
		}
		if (methods.lobby.switchWorlds(world)) {
			sleep(random(1000, 2000));
			methods.env.enableRandom("Login");
			return true;
		}
		return false;
	}

	/**
     * Fetch the chat button of the provided index for deprecated methods.
     * For internal use only.
     */
    private ChatButton getButton(final int idx) {
    	for (ChatButton b : ChatButton.values()) {
    		if (b.idx() == idx) {
    			return b;
    		}
    	}
    	return null;
    }

	/**
     * Fetch the game tab at the provided index for deprecated methods.
     * For internal use only.
     */
    private Tab getTab(final int idx) {
    	for (Tab t : Tab.values()) {
    		if (t.index() == idx) {
    			return t;
    		}
    	}
    	return null;
    }
}