package org.rsbot.script.methods;

import org.rsbot.script.wrappers.*;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Provides access to methods that can be used by RSBot scripts.
 */
public class Methods {

	/**
	 * The logger instance
	 */
	protected Logger log = Logger.getLogger(getClass().getName());
	/**
	 * The instance of {@link java.util.Random} for random number generation.
	 */
	protected java.util.Random random = new java.util.Random();
	/**
	 * The singleton of Skills
	 */
	protected static Skills skills;
	/**
	 * The singleton of Settings
	 */
	protected static Settings settings;
	/**
	 * The singleton of Magic
	 */
	protected static Magic magic;
	/**
	 * The singleton of Bank
	 */
	protected static Bank bank;
	/**
	 * The singleton of Players
	 */
	protected static Players players;
	/**
	 * The singleton of Store
	 */
	protected static Store store;
	/**
	 * The singleton of GrandExchange
	 */
	protected static GrandExchange grandExchange;
	/**
	 * The singletion of Hiscores
	 */
	protected static Hiscores hiscores;
	/**
	 * The singleton of ClanChat
	 */
	protected static ClanChat clanChat;
	/**
	 * The singleton of Camera
	 */
	protected static Camera camera;
	/**
	 * The singleton of NPCs
	 */
	protected static NPCs npcs;
	/**
	 * The singleton of GameScreen
	 */
	protected static Game game;
	/**
	 * The singleton of Combat
	 */
	protected static Combat combat;
	/**
	 * The singleton of Interfaces
	 */
	protected static Interfaces interfaces;
	/**
	 * The singleton of Mouse
	 */
	protected static Mouse mouse;
	/**
	 * The singleton of Keyboard
	 */
	protected static Keyboard keyboard;
	/**
	 * The singleton of Menu
	 */
	protected static Menu menu;
	/**
	 * The singleton of Tiles
	 */
	protected static Tiles tiles;
	/**
	 * The singleton of Objects
	 */
	protected static Objects objects;
	/**
	 * The singleton of Walking
	 */
	protected static Walking walking;
	/**
	 * The singleton of Calculations
	 */
	protected static Calculations calc;
	/**
	 * The singleton of Inventory
	 */
	protected static Inventory inventory;
	/**
	 * The singleton of Equipment
	 */
	protected static Equipment equipment;
	/**
	 * The singleton of GroundItems
	 */
	protected static GroundItems groundItems;
	/**
	 * The singleton of Account
	 */
	protected static Account account;
	/**
	 * The singleton of Summoning
	 */
	protected static Summoning summoning;
	/**
	 * The singleton of Environment
	 */
	protected static Environment env;
	/**
	 * The singleton of Prayer
	 */
	protected static Prayer prayer;
	/**
	 * The singleton of FriendsChat
	 */
	protected static FriendChat friendChat;
	/**
	 * The singleton of Trade
	 */
	protected static Trade trade;


	/**
	 * For internal use only: initializes the method providers.
	 *
	 * @param ctx The MethodContext.
	 */
	public void init(MethodContext ctx) {
		skills = ctx.skills;
		settings = ctx.settings;
		magic = ctx.magic;
		bank = ctx.bank;
		players = ctx.players;
		store = ctx.store;
		grandExchange = ctx.grandExchange;
		hiscores = ctx.hiscores;
		clanChat = ctx.clanChat;
		camera = ctx.camera;
		npcs = ctx.npcs;
		game = ctx.game;
		combat = ctx.combat;
		interfaces = ctx.interfaces;
		mouse = ctx.mouse;
		keyboard = ctx.keyboard;
		menu = ctx.menu;
		tiles = ctx.tiles;
		objects = ctx.objects;
		walking = ctx.walking;
		calc = ctx.calc;
		inventory = ctx.inventory;
		equipment = ctx.equipment;
		groundItems = ctx.groundItems;
		account = ctx.account;
		summoning = ctx.summoning;
		env = ctx.env;
		prayer = ctx.prayer;
		friendChat = ctx.friendChat;
		trade = ctx.trade;
	}

	/**
	 * Returns the current client's local player.
	 *
	 * @return The current client's <tt>RSPlayer</tt>.
	 * @see Players#getMyPlayer()
	 */
	public RSPlayer getMyPlayer() {
		return players.getMyPlayer();
	}

	/**
	 * Returns a random integer with min as the inclusive lower bound and max as
	 * the exclusive upper bound.
	 *
	 * @param min The inclusive lower bound.
	 * @param max The exclusive upper bound.
	 * @return Random integer min <= n < max.
	 */
	public int random(int min, int max) {
		int n = Math.abs(max - min);
		return Math.min(min, max) + (n == 0 ? 0 : random.nextInt(n));
	}

	/**
	 * Checks for the existence of a NPC.
	 *
	 * @param npc The NPC to check for.
	 * @return <tt>true</tt> if found.
	 */
	public boolean verify(RSNPC npc) {
		return npc != null;
	}

	/**
	 * Checks for the existence of a RSObject.
	 *
	 * @param o The RSObject to check for.
	 * @return <tt>true</tt> if found.
	 */
	public boolean verify(RSObject o) {
		return o != null;
	}

	/**
	 * Checks for the existence of a RSTile.
	 *
	 * @param t The RSTile to check for.
	 * @return <tt>true</tt> if found.
	 */
	public boolean verify(RSTile t) {
		return t != null;
	}

	/**
	 * Checks for the existence of a RSGroundItem.
	 *
	 * @param i The RSGroundItem to check for.
	 * @return <tt>true</tt> if found.
	 */
	public boolean verify(RSGroundItem i) {
		return i != null;
	}

	/**
	 * Returns a random double with min as the inclusive lower bound and max as
	 * the exclusive upper bound.
	 *
	 * @param min The inclusive lower bound.
	 * @param max The exclusive upper bound.
	 * @return Random double min <= n < max.
	 */
	public double random(double min, double max) {
		return Math.min(min, max) + random.nextDouble() * Math.abs(max - min);
	}

	/**
	 * Pauses execution for a random amount of time between two values.
	 *
	 * @param minSleep The minimum time to sleep.
	 * @param maxSleep The maximum time to sleep.
	 * @see #sleep(int)
	 * @see #random(int, int)
	 */
	public void sleep(int minSleep, int maxSleep) {
		sleep(random(minSleep, maxSleep));
	}

	/**
	 * Pauses execution for a given number of milliseconds.
	 *
	 * @param toSleep The time to sleep in milliseconds.
	 */
	public void sleep(int toSleep) {
		try {
			long start = System.currentTimeMillis();
			Thread.sleep(toSleep);

			// Guarantee minimum sleep
			long now;
			while (start + toSleep > (now = System.currentTimeMillis())) {
				Thread.sleep(start + toSleep - now);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints to the RSBot log.
	 *
	 * @param message Object to log.
	 */
	public void log(Object message) {
		log.info(message.toString());
	}

	/**
	 * Prints to the RSBot log with a font color
	 *
	 * @param color   The color of the font
	 * @param message Object to log
	 */
	public void log(Color color, Object message) {
		Object[] parameters = {color};
		log.log(Level.INFO, message.toString(), parameters);
	}
}
