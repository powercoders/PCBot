/**
 * @author Aaimister
 * @version 1.15 ©2010-2011 Aaimister, No one except Aaimister has the right to
 *          modify and/or spread this script without the permission of Aaimister.
 *          I'm not held responsible for any damage that may occur to your
 *          property.
 */

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.gui.AccountManager;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Scanner;

@ScriptManifest(authors = {"Aaimister"}, name = "Aaimister's Roach Killer v1.15", keywords = "Combat", version = 1.15, description = ("Kills roaches in Edgville."))
public class AaimistersRoaches extends Script implements PaintListener, MouseListener, MessageListener {

	private static interface AM {

		//Paths
		final RSTile toBank[] = {new RSTile(3080, 3471), new RSTile(3080, 3480), new RSTile(3086, 3486),
				new RSTile(3093, 3490)};
		final RSTile toCave[] = {new RSTile(3093, 3490), new RSTile(3084, 3484), new RSTile(3080, 3479),
				new RSTile(3080, 3473), new RSTile(3078, 3464)};

		//Areas
		final RSArea bankArea = new RSArea(new RSTile(3090, 3488), new RSTile(3098, 3499));
		//final RSArea dropArea = new RSArea(new RSTile(3074, 3461), new RSTile(3080, 3466));
		final RSArea rArea1 = new RSArea(new RSTile(3146, 4274), new RSTile(3160, 4281));
		final RSArea rArea2 = new RSArea(new RSTile(3170, 4229), new RSTile(3196, 4273));

		//Tiles
		final RSTile bankTile = new RSTile(3093, 3490);
		final RSTile dropTile = new RSTile(3078, 3462);
	}

	private RSArea rArea;

	private long nextBreak = System.currentTimeMillis();
	private long nextLength = 60000;
	private long antiBanRandom = random(15000, 90000);
	private long antiBanTime = System.currentTimeMillis() + antiBanRandom;
	private long totalBreakTime;
	private long lastBreakTime;
	private long nextBreakT;
	private long startTime;
	private long runTime;
	private long now;

	private final String[] colorstring = {"Black", "Blue", "Brown", "Cyan", "Green", "Lime", "Orange", "Pink", "Purple", "Red", "White", "Yellow"};
	private String[] lootString;
	private ArrayList<String> doLoot = new ArrayList<String>(50);

	AaimistersGUI g = new AaimistersGUI();
	public final File settingsFile = new File(getCacheDirectory(), "AaimistersRKillerSettings.txt");
	public final File glootFile = new File(getCacheDirectory(), "GoodLootList.txt");
	public final File blootFile = new File(getCacheDirectory(), "BadLootList.txt");
	public final File flootFile = new File(getCacheDirectory(), "RealLootList.txt");

	NumberFormat formatter = new DecimalFormat("#,###,###");

	Font Cam10 = new Font("Cambria Math", Font.BOLD, 10);
	Font Cam = new Font("Cambria Math", Font.BOLD, 12);

	Color PercentGreen = new Color(0, 163, 4, 150);
	Color PercentRed = new Color(163, 4, 0, 150);
	Color White150 = new Color(255, 255, 255, 150);
	Color White90 = new Color(255, 255, 255, 90);
	Color White = new Color(255, 255, 255);
	Color Background = new Color(219, 200, 167);
	Color UpGreen = new Color(0, 169, 0);
	//Color LineColor = new Color(0, 0, 0);
	Color ClickC = new Color(187, 0, 0);
	Color UpRed = new Color(169, 0, 0);
	Color Black = new Color(0, 0, 0);
	Color MainColor = Black;
	Color ThinColor = new Color(0, 0, 0, 70);
	Color BoxColor = Black;
	Color LineColor = White;

	final NumberFormat nf = NumberFormat.getInstance();

	String formatTime(final int milliseconds) {
		final long t_seconds = milliseconds / 1000;
		final long t_minutes = t_seconds / 60;
		final long t_hours = t_minutes / 60;
		final int seconds = (int) (t_seconds % 60);
		final int minutes = (int) (t_minutes % 60);
		final int hours = (int) (t_hours % 60);
		return (nf.format(hours) + ":" + nf.format(minutes) + ":" + nf.format(seconds));
	}

	private String currentNPC = "Roach";
	private String currentStat;
	private String status = "";
	private String url = "http://3ff8d067.any.gs";

	//All Arrows
	int Aitems[] = {890, 882, 11212, 19157, 884, 888, 2866, 892, 19152,
			886, 19162};
	//Coins, Mushroom, Clue Scroll (m), Nature Tail, Shield Left Half, Loop Half Key, Tooth half Key 
	//Ancient Effigy, Chaos Tail, Water Tail, Fire Tail, Earth Tail
	int Oitems[] = {995, 6004, 0, 1462, 2366, 987, 985, 18778, 1454, 1444, 1443, 1441};
	//JangerBerry, Wildblood, Limpwurt, Avantoe, Belladonna, Cactus, Cadantine, Irit, Kwuarm
	//Marrentill, Mushroom, Poison ivy, Strawberry, Tarromin, Toadflax, Whiteberry, Watermelon, Harralander
	//Spirit weed, Dwarf weed, Lantadyme, Snapdragon, Torstol, Ranarr
	int Sitems[] = {5104, 5311, 5100, 5298, 5281, 5280, 5301, 5297, 5299,
			5292, 5282, 5106, 5323, 5293, 5296, 5105, 5321, 5294,
			12176, 5303, 5302, 5300, 5304, 5295};
	//Crimson, Blue, Gold, Green, All
	int Citems[] = {12160, 12163, 12158, 12159, 12161, 12162, 12164, 12165,
			12166, 12167};
	//Sapphire, Emerald, Ruby, Diamond, Dragonstone
	int Gitems[] = {1623, 1621, 1619, 1617, 1631};
	//Mithril Ore, Law, Death, Adamantite Ore, Fire, Blood, Chaos, Silver Ore, Nature, Soul
	int Ritems[] = {448, 563, 560, 450, 554, 565, 562, 442, 562, 566};
	//Black helm, Mithril med, Rune sq, Rune scimi, Rune javelin, Dragon spear, Rune spear, Addy Javelin,
	//Rune Kite, D Med Helm
	int Witems[] = {1165, 1143, 1185, 1333, 830, 1249, 1247, 829, 1201, 1149};
	//-=- Rare Table -=-//
	//D Dagger, Onyx Bolt, Vecna Skull, Sara Brew, Rune Bar, Addy Bolts, Rune Arrow, Big Bones, Battlestaff,
	//Air Orb, Fire Orb, C Toadflax, C Ranarr, C Snapdragon, C Torstol, Coal Ore, Rune Ore, Addy Bar, Pure Ess,
	//Raw Swordfish, Raw Shark, Yew Seed, Magic Seed, Palm Seed, Yew Logs, Water Tail, Fire Tail, Earth Tail
	int tableItems[] = {1216, 9342, 20667, 6686, 2364, 9143, 892, 533, 1392, 574, 570, 2999,
			258, 3001, 270, 454, 452, 2362, 7937, 372, 384, 5315, 5316, 5289, 1516,
			1445, 1443, 1441};

	int noCheckItems[] = {6004, 1462, 2366, 987, 985, 1623, 1621, 1619, 1617, 1631, 1165, 1143, 1185,
			1333, 830, 1249, 1247, 829, 1201, 1149, 20667, 890, 882, 11212, 19157, 884,
			888, 2866, 892, 19152, 886, 19162};

	int notedItems[] = {562, 1216, 533, 1392, 574, 570, 2999, 258, 3001, 270, 454, 452, 2362, 7937,
			372, 384, 1443, 1441, 1516, 448, 450};

	int incave = 29728;
	//"Enter"
	int outcave = 29729;
	//"Climb"
	int roach = 7160;
	//"Attack"
	int upstairs = 29672;
	//"Climb-up"
	int dwstairs = 29671;
	//"Climb-down"
	int rCount;
	int rHour;
	int boo = 26972;
	int banker = 2759;
	int idle;
	int food = 379;
	int minHealth = 200;
	int noFood;
	int X = 20;
	int v, z, x;
	int id;
	int maxBetween;
	int minBetween;
	int maxLength;
	int minLength;
	//Other
	int charmsHour;
	int totalCharms;
	int totalItems;
	int itemsHour;
	int GPHour;
	int totalPrice;
	int xpGained;
	int xpHour;
	int priceO;
	int priceS;
	int priceG;
	int priceR;
	int priceW;
	//Defense
	int dfxpHour;
	int dfxpToLvl;
	int dfcurrentXP;
	int dfgainedLvl;
	int dfxpGained;
	int dfstartEXP;
	int dftimeToLvl;
	//Strength
	int stxpHour;
	int stxpToLvl;
	int stcurrentXP;
	int stgainedLvl;
	int stxpGained;
	int ststartEXP;
	int sttimeToLvl;
	//Attack
	int atxpHour;
	int atxpToLvl;
	int atcurrentXP;
	int atgainedLvl;
	int atxpGained;
	int atstartEXP;
	int attimeToLvl;
	//Range
	int rgxpHour;
	int rgxpToLvl;
	int rgcurrentXP;
	int rggainedLvl;
	int rgxpGained;
	int rgstartEXP;
	int rgtimeToLvl;
	//Cons.
	int coxpHour;
	int coxpToLvl;
	int cocurrentXP;
	int cogainedLvl;
	int coxpGained;
	int costartEXP;
	int cotimeToLvl;

	boolean checkMem = true;
	boolean member;
	boolean currentlyBreaking;
	boolean randomBreaks;
	boolean painting;
	boolean antiBanOn;
	boolean free;
	boolean equip;
	boolean room2;
	boolean noted;
	boolean notChosen = true;
	boolean doBreak;
	boolean bankedOpen;
	boolean useBanker;
	boolean attacked;
	boolean useBooth;
	boolean clicked;
	boolean checked;
	boolean logTime;
	boolean checkIn;
	boolean opened;
	boolean closed;
	boolean wLoot;
	//Paint Buttons
	boolean xButton;
	boolean StatAT;
	;
	boolean StatCO;
	boolean StatDF;
	boolean StatST;
	boolean StatRG;
	boolean Main = true;

	private enum State {TOROACH, TOBANK, ATTACK, EAT, BANK, LOOT, ERROR}

	;

	private State getState() {
		try {
			if (Integer.parseInt(interfaces.getComponent(748, 8).getText()) < (minHealth + random(-10, 10))) {
				return State.EAT;
			}
		} catch (Exception e) {

		}
		if (inventory.contains(food)) {
			noFood = 0;
			if (!room2) {
				if (game.getPlane() == 3) {
					if (loot()) {
						return State.LOOT;
					} else {
						return State.ATTACK;
					}
				} else {
					return State.TOROACH;
				}
			} else {
				if (game.getPlane() == 2) {
					if (loot()) {
						return State.LOOT;
					} else {
						return State.ATTACK;
					}
				} else {
					return State.TOROACH;
				}
			}
		} else {
			if (AM.bankArea.contains(getMyPlayer().getLocation())) {
				return State.BANK;
			} else {
				return State.TOBANK;
			}
		}
	}

	public double getVersion() {
		return 1.15;
	}

	public boolean onStart() {
		status = "Starting up...";

		URLConnection url = null;
		BufferedReader in = null;

		//Check right away...
		try {
			//Open the version text file
			url = new URL("http://aaimister.webs.com/scripts/AaimistersRoachVersion.txt").openConnection();
			//Create an input stream for it
			in = new BufferedReader(new InputStreamReader(url.getInputStream()));
			//Check if the current version is outdated
			if (Double.parseDouble(in.readLine()) > getVersion()) {
				if (JOptionPane.showConfirmDialog(null, "Please visit the thread: " +
						"http://www.powerbot.org/vb/showthread.php?t=769805") == 0) {
					//If so, tell to go to the thread.
					openThread();
					if (in != null) {
						in.close();
					}
					return false;
				}
			} else {
				JOptionPane.showMessageDialog(null, "You have the latest version.");
				//User has the latest version. Tell them!
				if (in != null) {
					in.close();
				}
			}
		} catch (IOException e) {
			log("Problem getting version. Please visit the forums.");
			return false; //Return false if there was a problem
		}

		try {
			settingsFile.createNewFile();
			glootFile.createNewFile();
			blootFile.createNewFile();
			flootFile.createNewFile();
		} catch (final IOException ignored) {

		}

		createAndWaitforGUI();
		if (closed) {
			log.severe("The GUI window was closed!");
			return false;
		}

		dfstartEXP = skills.getCurrentExp(1);
		dfcurrentXP = skills.getExpToNextLevel(1);
		ststartEXP = skills.getCurrentExp(2);
		stcurrentXP = skills.getExpToNextLevel(2);
		atstartEXP = skills.getCurrentExp(0);
		atcurrentXP = skills.getExpToNextLevel(0);
		costartEXP = skills.getCurrentExp(3);
		cocurrentXP = skills.getExpToNextLevel(3);
		rgstartEXP = skills.getCurrentExp(4);
		rgcurrentXP = skills.getExpToNextLevel(4);
		if (doBreak) {
			breakingNew();
		}

		return true;
	}

	private void createAndWaitforGUI() {
		if (SwingUtilities.isEventDispatchThread()) {
			g.AaimistersGUI.setVisible(true);
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						g.AaimistersGUI.setVisible(true);
					}
				});
			} catch (InvocationTargetException ite) {
			} catch (InterruptedException ie) {
			}
		}
		sleep(100);
		while (g.AaimistersGUI.isVisible()) {
			sleep(100);
		}
	}

	public void openThread() {
		if (java.awt.Desktop.isDesktopSupported()) {
			java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

			if (!desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
				log("Can't open thread. Something is conflicting.");
				return;
			}

			try {

				java.net.URI uri = new java.net.URI(url);
				desktop.browse(uri);
			} catch (Exception e) {

			}
		}
	}

	private void breakingNew() {
		if (randomBreaks) {
			long varTime = random(3660000, 10800000);
			nextBreak = System.currentTimeMillis() + varTime;
			nextBreakT = varTime;
			long varLength = random(900000, 3600000);
			nextLength = nextBreakT;
		} else {
			int diff = random(0, 5) * 1000 * 60;
			long varTime = random((minBetween * 1000 * 60) + diff, (maxBetween * 1000 * 60) - diff);
			nextBreak = System.currentTimeMillis() + varTime;
			nextBreakT = varTime;
			int diff2 = random(0, 5) * 1000 * 60;
			long varLength = random((minLength * 1000 * 60) + diff2, (maxLength * 1000 * 60) - diff2);
			nextLength = varLength;
		}
		logTime = true;
	}

	private boolean breakingCheck() {
		if (nextBreak <= System.currentTimeMillis()) {
			return true;
		}
		return false;
	}

	private String Location() {
		if (AM.bankArea.contains(getMyPlayer().getLocation())) {
			return "Bank";
		} else if (game.getPlane() == 3 || game.getPlane() == 2) {
			return "Cave";
		} else if (calc.distanceTo(AM.bankTile) < 300) {
			return "Edgeville";
		} else if (!game.isLoggedIn()) {
			return "Login Screen";
		} else {
			return "Unknown";
		}
	}

	private boolean loot() {
		RSGroundItem[] all = groundItems.getAll(20);
		if (all != null) {
			if (getMyPlayer().getInteracting() == null) {
				for (int i = 0; i < all.length; i++) {
					if (rArea.contains(all[i].getLocation())) {
						if (doLoot.contains(Integer.toString(all[i].getItem().getID())) || all[i].getItem().getName().contains("scroll")) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private void checkPrice(int x, int y) {
		if (x == 995) {
			if (inventory.getCount(true, x) > y) {
				totalPrice += (inventory.getCount(true, x) - y);
				y = inventory.getCount(true, x);
			}
		} else if (noted) {
			totalPrice += ((inventory.getCount(true, x) - y) * getGuidePrice(x - 1));
			y = inventory.getCount(true, x);
		} else {
			totalPrice += ((inventory.getCount(true, x) - y) * getGuidePrice(x));
			y = inventory.getCount(true, x);
		}
	}

	private void setRun() {
		if (!walking.isRunEnabled()) {
			if (walking.getEnergy() >= random(45, 100)) {
				walking.setRun(true);
				sleep(1000, 1600);
			}
		}
	}

	private void setCamera() {
		if (camera.getPitch() < 2) {
			camera.setPitch(true);
			sleep(1000, 1600);
		}
	}

	private boolean walkPath(RSTile[] tiles) {
		RSPath walkPath = walking.newTilePath(tiles).randomize(1, 1);
		try {
			if (walkPath != null) {
				if (!getMyPlayer().isMoving() || calc.distanceTo(walking.getDestination()) < 4) {
					return walkPath.traverse();
				}
			}
		} catch (Exception e) {

		}
		return false;
	}

	private void clickNPC(RSNPC x, String y) {
		try {
			if (x.getModel().getPointOnScreen() != null) {
				x.getModel().hover();
				sleep(150, 300);
				x.doAction(y);
			}
		} catch (Exception e) {

		}
	}

	private void clickObj(RSObject x, String y) {
		try {
			if (x.getModel().getPointOnScreen() != null) {
				x.getModel().hover();
				sleep(150, 300);
				x.doAction(y);
			}
		} catch (Exception e) {

		}
	}


	private void lootItem(RSGroundItem x, String y) {
		try {
			if (x.isOnScreen()) {
				mouse.move(calc.tileToScreen(x.getLocation()));
				sleep(60);
				x.doAction("Take " + y);
			}
		} catch (Exception e) {

		}
	}

	public int loop() {
		if (breakingCheck() && doBreak) {
			status = "Breaking...";
			long endTime = System.currentTimeMillis() + nextLength;
			totalBreakTime += (nextLength + 5000);
			lastBreakTime = (totalBreakTime - (nextLength + 5000));
			currentlyBreaking = true;
			while (game.isLoggedIn()) {
				game.logout(false);
				sleep(50);
			}
			log("Taking a break for " + formatTime((int) nextLength));
			while (System.currentTimeMillis() < endTime && currentlyBreaking == true) {
				sleep(1000);
			}
			currentlyBreaking = false;
			while (!game.isLoggedIn()) {
				try {
					breakingNew();
					game.login();
				} catch (Exception e) {
					return 10;
				}
				sleep(50);
			}
			return 10;
		}

		if (!game.isLoggedIn()) {
			status = "Breaking...";
			return 3000;
		}

		if (startTime == 0 && skills.getCurrentLevel(8) != 0) {
			startTime = System.currentTimeMillis();
			dfstartEXP = skills.getCurrentExp(1);
			dfcurrentXP = skills.getExpToNextLevel(1);
			ststartEXP = skills.getCurrentExp(2);
			stcurrentXP = skills.getExpToNextLevel(2);
			atstartEXP = skills.getCurrentExp(0);
			atcurrentXP = skills.getExpToNextLevel(0);
			costartEXP = skills.getCurrentExp(3);
			cocurrentXP = skills.getExpToNextLevel(3);
			rgstartEXP = skills.getCurrentExp(4);
			rgcurrentXP = skills.getExpToNextLevel(4);
		}

		currentlyBreaking = false;

		if (logTime) {
			log("Next Break In: " + formatTime((int) nextBreakT) + " For: " + formatTime((int) nextLength) + ".");
			logTime = false;
		}

		mouse.setSpeed(random(4, 8));
		setCamera();
		setRun();

		if (checkMem) {
			if (AccountManager.isMember(account.getName())) {
				member = true;
			}
			if (doBreak) {
				if (AccountManager.isTakingBreaks(account.getName())) {
					log.severe("Turn Off Bot Breaks!");
					log.severe("Turning off custom breaker...");
					doBreak = false;
				}
			}
			checkMem = false;
		}

		if (equip) {
			if (inventory.containsOneOf(Aitems)) {
				RSItem i = inventory.getItem(Aitems);
				i.doAction("Wield");
				equip = false;
				return random(500, 1000);
			} else {
				log.severe("Out of Ammo. =O");
				game.logout(false);
				sleep(500);
				stopScript();
			}
		}

		if (checkIn) {
			if (inventory.getCount(true, v) > z) {
				checkPrice(v, z);
				checkIn = false;
				noted = false;
			} else {
				checkIn = true;
			}
		}

		switch (getState()) {
			case EAT:
				if (inventory.contains(food)) {
					RSItem foo = inventory.getItem(food);
					foo.doAction("Eat");
					return random(1200, 2000);
				}

				break;
			case TOROACH:
				status = "Walking to roaches...";
				if (idle > 5) {
					clicked = false;
					idle = 0;
				}
				try {
					if (game.getPlane() != 3) {
						if (calc.distanceTo(AM.dropTile) > 3) {
							walkPath(AM.toCave);
							return 50;
						} else {
							RSObject rope = objects.getNearest(incave);
							RSTile loc = rope.getArea().getNearestTile(getMyPlayer().getLocation());
							if (calc.distanceTo(loc) > 3) {
								if (!getMyPlayer().isMoving() || calc.distanceTo(walking.getDestination()) < 4) {
									walking.walkTileMM(walking.getClosestTileOnMap(loc.randomize(2, 2)));
									return random(150, 300);
								}
							} else {
								if (rope.isOnScreen()) {
									idle++;
									if (!clicked) {
										clickObj(rope, "Enter");
										clicked = true;
										return 10;
									}
								} else {
									camera.turnTo(rope);
									return random(200, 400);
								}
							}
						}
					} else if (room2) {
						if (game.getPlane() == 3) {
							RSObject stair = objects.getNearest(dwstairs);
							RSTile loc = stair.getArea().getNearestTile(getMyPlayer().getLocation());
							if (calc.distanceTo(loc) > 3) {
								if (!getMyPlayer().isMoving() || calc.distanceTo(walking.getDestination()) < 4) {
									walking.walkTileMM(walking.getClosestTileOnMap(loc.randomize(1, 1)));
									return random(150, 300);
								}
							} else {
								if (stair.isOnScreen()) {
									clickObj(stair, "Climb-down");
									return random(1000, 1500);
								} else {
									camera.turnTo(stair);
									return random(300, 500);
								}
							}
						}
					}
				} catch (Exception e) {

				}
				break;
			case TOBANK:
				notChosen = true;
				if (idle > 8) {
					clicked = false;
					idle = 0;
				}
				status = "Walking to bank...";
				try {
					if (game.getPlane() == 3) {
						RSObject rope = objects.getNearest(outcave);
						RSTile loc = rope.getArea().getNearestTile(getMyPlayer().getLocation());
						if (calc.distanceTo(loc) > 3) {
							if (getMyPlayer().isMoving() && !getMyPlayer().isInCombat()) {
								return 10;
							}
							if (!getMyPlayer().isMoving() || calc.distanceTo(walking.getDestination()) < 4) {
								walking.walkTileMM(walking.getClosestTileOnMap(loc.randomize(1, 1)));
								return random(150, 300);
							}
						} else {
							if (rope.isOnScreen()) {
								idle++;
								if (!clicked) {
									clickObj(rope, "Climb");
									clicked = true;
									idle = 0;
									return 10;
								}
							} else {
								camera.turnTo(rope);
								return random(200, 400);
							}
						}
					} else if (game.getPlane() == 2) {
						RSObject stair = objects.getNearest(upstairs);
						RSTile loc = stair.getArea().getNearestTile(getMyPlayer().getLocation());
						if (calc.distanceTo(loc) > 3) {
							if (!getMyPlayer().isMoving() || calc.distanceTo(walking.getDestination()) < 4) {
								walking.walkTileMM(walking.getClosestTileOnMap(loc.randomize(1, 1)));
								return random(150, 300);
							}
						} else {
							if (stair.isOnScreen()) {
								clickObj(stair, "Climb-up");
								return random(1000, 1500);
							} else {
								camera.turnTo(stair);
								return random(300, 500);
							}
						}
					} else {
						walkPath(AM.toBank);
						return 50;
					}
				} catch (Exception e) {

				}

				break;
			case ATTACK:
				status = "Attacking roaches...";
				clicked = false;
				if (idle > 8) {
					attacked = false;
					idle = 0;
				}
				if (wLoot) {
					wLoot = false;
					return random(1800, 2500);
				}
				if (!getMyPlayer().isInCombat() && getMyPlayer().getInteracting() == null) {
					if (roach() != null && !loot()) {
						if (roach().isOnScreen()) {
							idle++;
							if (!attacked) {
								clickNPC(roach(), "Attack");
								rCount++;
								attacked = true;
								wLoot = true;
								idle = 0;
								return random(500, 1000);
							}
						} else {
							if (!getMyPlayer().isMoving() || calc.distanceTo(walking.getDestination()) < 4) {
								walking.walkTileMM(walking.getClosestTileOnMap(roach().getLocation().randomize(1, 1)));
								return random(1000, 1300);
							}
						}
					} else {
						attacked = false;
						return 10;
					}
				} else {
					idle = 0;
					if (antiBanTime <= System.currentTimeMillis()) {
						doAntiBan();
					}
					return 10;
				}


				break;
			case BANK:
				status = "Banking...";
				clicked = false;
				if (idle > 3) {
					opened = false;
					bankedOpen = false;
					idle = 0;
				}
				if (!inventory.contains(food) && noFood >= 2) {
					log.severe("Out of Food!");
					game.logout(false);
					sleep(200, 500);
					stopScript();
				}
				if (notChosen) {
					if (random(0, 5) == 0 || random(0, 5) == 2) {
						useBanker = true;
					} else {
						useBooth = true;
					}
					notChosen = false;
				}
				RSObject booth = objects.getNearest(boo);
				RSNPC bankP = banker();
				if (AM.bankArea.contains(getMyPlayer().getLocation()) && booth.isOnScreen()) {
					if (!bank.isOpen()) {
						idle++;
						if (!opened) {
							if (useBooth) {
								booth.doAction("Use-quickly");
							} else {
								bankP.doAction("Bank Banker");
							}
							opened = true;
							return random(200, 500);
						}
					} else {
						opened = false;
						idle++;
						if (!bankedOpen && bank.isOpen()) {
							if (inventory.getCount() > 0) {
								bank.depositAll();
								sleep(350, 500);
							}
							if (bank.getItem(food) != null) {
								bank.withdraw(food, X);
								idle = 0;
								sleep(350, 500);
							} else if (bank.isOpen()) {
								noFood++;
							}
							bankedOpen = true;
							return random(100, 150);
						}
					}
				} else {
					if (!getMyPlayer().isMoving() || calc.distanceTo(walking.getDestination()) < 4) {
						if (useBooth) {
							walking.walkTileMM(walking.getClosestTileOnMap(booth.getLocation().randomize(1, 1)));
						} else {
							walking.walkTileMM(walking.getClosestTileOnMap(banker().getLocation().randomize(1, 1)));
						}
						return random(1200, 1500);
					}
				}

				break;
			case LOOT:
				attacked = false;
				status = "Picking up loot...";
				wLoot = false;
				if (idle > 3) {
					clicked = false;
					idle = 0;
				}
				if (inventory.isFull()) {
					RSItem foo = inventory.getItem(food);
					foo.doAction("Eat");
					return random(1000, 1300);
				}
				try {
					if (getMyPlayer().isMoving() || calc.distanceTo(walking.getDestination()) > 3) {
						random(150, 300);
					}
				} catch (Exception e) {

				}
				RSGroundItem[] all = groundItems.getAll(50);
				if (all != null) {
					if (getMyPlayer().getInteracting() == null) {
						for (int i = 0; i < all.length; i++) {
							if (rArea.contains(all[i].getLocation())) {
								if (doLoot.contains(Integer.toString(all[i].getItem().getID()))) {
									if (all[i].isOnScreen()) {
										idle++;
										if (!clicked) {
											if (!getMyPlayer().isMoving()) {
												lootItem(all[i], all[i].getItem().getName());
											} else {
												return 50;
											}
											clicked = true;
											idle = 0;
											for (int a = 0; a < Aitems.length; a++) {
												if (all[i].getItem().getID() == Aitems[a]) {
													equip = true;
												}
											}
											for (int c = 0; c < Citems.length; c++) {
												if (all[i].getItem().getID() == Citems[c]) {
													totalCharms++;
												}
											}
											for (int o = 0; o < noCheckItems.length; o++) {
												if (all[i].getItem().getID() == noCheckItems[o]) {
													int p = getGuidePrice(all[i].getItem().getID());
													if (p > 1) {
														totalPrice += p;
														idle = 0;
														totalItems++;
													}
													return (calc.distanceTo(all[i].getLocation()) * 1000);
												}
											}
											for (int n = 0; n < notedItems.length; n++) {
												if (all[i].getItem().getID() == notedItems[n]) {
													noted = true;
												}
											}
											v = all[i].getItem().getID();
											z = inventory.getCount(true, v);
											checkIn = true;
											idle = 0;
											totalItems++;
											return (calc.distanceTo(all[i].getLocation()) * 1000);
										}
									} else {
										if (!getMyPlayer().isMoving() || calc.distanceTo(walking.getDestination()) < 4) {
											walking.walkTileMM(all[i].getLocation().randomize(1, 1));
											return random(150, 300);
										}
									}
								}
							}
						}
					}
				}

				break;
			case ERROR:

				break;
		}
		return random(300, 600);
	}

	public boolean dyingRo() {
		for (RSNPC i : npcs.getAll()) {
			if (i.getAnimation() == 8789 && calc.distanceTo(i.getLocation()) < 3 && getMyPlayer().isInCombat()) {
				return true;
			}
		}
		return false;
	}

	private RSNPC roach() {
		return npcs.getNearest(new Filter<RSNPC>() {
			public boolean accept(RSNPC r) {
				if (!free) {
					return r.getID() == roach && r.getHPPercent() > 0;
				} else {
					return r.getID() == roach && r.getHPPercent() > 0 && !r.isInCombat();
				}
			}
		});
	}

	private RSNPC banker() {
		return npcs.getNearest(new Filter<RSNPC>() {
			public boolean accept(RSNPC n) {
				return n.getID() == banker;
			}
		});
	}

	private RSPlayer playerNear() {
		RSPlayer me = myPlayer();
		return me != null ? me : players.getNearest(new Filter<RSPlayer>() {
			public boolean accept(RSPlayer p) {
				return !p.isMoving() && p.isOnScreen();
			}
		});
	}

	private RSPlayer myPlayer() {
		final String myName = players.getMyPlayer().getName();
		return players.getNearest(new Filter<RSPlayer>() {
			public boolean accept(RSPlayer p) {
				return p.getName() == myName;
			}
		});
	}

	public void doAntiBan() {

		if (!antiBanOn) {
			return;
		}

		antiBanRandom = random(15000, 90000);
		antiBanTime = System.currentTimeMillis() + antiBanRandom;

		int action = random(0, 4);

		switch (action) {
			case 0:
				rotateCamera();
				sleep(200, 400);
				break;
			case 1:
				mouse.moveRandomly(100, 900);
				sleep(200, 400);
				break;
			case 2:
				checkXP();
				sleep(200, 400);
				break;
			case 3:
				mouse.moveOffScreen();
				sleep(200, 400);
				break;
			case 4:
				checkPlayer();
				sleep(200, 400);
				break;
		}
	}

	public void checkPlayer() {
		RSPlayer near = playerNear();
		if (near != null) {
			if (!getMyPlayer().isMoving()) {
				if (near.getScreenLocation() != null) {
					if (mouse.getLocation() != near.getScreenLocation()) {
						mouse.move(near.getScreenLocation());
						sleep(300, 550);
					}
					mouse.click(false);
					sleep(300, 500);
					if (menu.contains("Follow")) {
						Point menuu = menu.getLocation();
						int Mx = menuu.x;
						int My = menuu.y;
						int x = Mx + random(3, 120);
						int y = My + random(3, 98);
						mouse.move(x, y);
						sleep(2320, 3520);
						mouse.moveRandomly(100, 900);
						sleep(50);
						if (menu.isOpen()) {
							mouse.moveRandomly(100, 900);
							sleep(50);
						}
						if (menu.isOpen()) {
							mouse.moveRandomly(100, 900);
							sleep(50);
						}
					} else {
						mouse.moveRandomly(100, 900);
					}
				}
			} else {
				return;
			}
		} else {
			mouse.moveRandomly(100, 900);
		}
	}

	public void checkXP() {
		if (game.getCurrentTab() != 2) {
			game.openTab(2);
			sleep(500, 900);
		}

		int action = random(0, 3);

		if (action == 1 && atxpGained == 0) {
			action = 0;
		} else if (action == 2 && stxpGained == 0) {
			action = 0;
		} else if (action == 3 && dfxpGained == 0) {
			action = 0;
		}

		switch (action) {
			case 0:
				//Cons.
				mouse.move(random(617, 667), random(214, 232));
				break;
			case 1:
				//Att.
				mouse.move(random(555, 605), random(213, 232));
				break;
			case 2:
				//Str.
				mouse.move(random(555, 604), random(241, 260));
				break;
			case 3:
				//Def.
				mouse.move(random(555, 606), random(271, 288));
				break;
		}
		sleep(2800, 5500);
		game.openTab(4);
		sleep(50, 100);
		mouse.moveRandomly(50, 900);
	}

	public void rotateCamera() {
		if (!antiBanOn) {
			return;
		}
		final char[] LR = new char[]{KeyEvent.VK_LEFT,
				KeyEvent.VK_RIGHT};
		final char[] UD = new char[]{KeyEvent.VK_DOWN,
				KeyEvent.VK_UP};
		final char[] LRUD = new char[]{KeyEvent.VK_LEFT,
				KeyEvent.VK_RIGHT, KeyEvent.VK_UP,
				KeyEvent.VK_UP};
		final int randomLR = random(0, 2);
		final int randomUD = random(0, 2);
		final int randomAll = random(0, 4);
		if (random(0, 3) == 0) {
			keyboard.pressKey(LR[randomLR]);
			sleepCR(random(2, 9));
			keyboard.pressKey(UD[randomUD]);
			sleepCR(random(6, 10));
			keyboard.releaseKey(UD[randomUD]);
			sleepCR(random(2, 7));
			keyboard.releaseKey(LR[randomLR]);
		} else {
			keyboard.pressKey(LRUD[randomAll]);
			if (randomAll > 1) {
				sleepCR(random(6, 11));
			} else {
				sleepCR(random(9, 12));
			}
			keyboard.releaseKey(LRUD[randomAll]);
		}
	}

	private boolean sleepCR(int amtOfHalfSecs) {
		for (int x = 0; x < (amtOfHalfSecs + 1); x++) {
			sleep(random(48, 53));
		}
		return true;
	}

	//Credits Aion
	private String stripFormatting(String str) {
		if (str != null && !str.isEmpty()) {
			return str.replaceAll("(^[^<]+>|<[^>]+>|<[^>]+$)", "");
		}
		return "";
	}

	// Credits Aion
	private int getGuidePrice(int itemID) {
		try {
			URL url = new URL(
					"http://services.runescape.com/m=itemdb_rs/viewitem.ws?obj="
							+ itemID);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String line = null;

			while ((line = br.readLine()) != null) {
				if (line.contains("<b>Current guide price:</b>")) {
					line = line.replace("<b>Current guide price:</b>", "");
					return (int) parse(line);
				}
			}
		} catch (IOException e) {
		}
		return -1;
	}


	//Credits Aion
	private double parse(String str) {
		if (str != null && !str.isEmpty()) {
			str = stripFormatting(str);
			str = str.substring(str.indexOf(58) + 2, str.length());
			str = str.replace(",", "");
			if (!str.endsWith("%")) {
				if (!str.endsWith("k") && !str.endsWith("m")) {
					return Double.parseDouble(str);
				}
				return Double.parseDouble(str.substring(0, str.length() - 1))
						* (str.endsWith("m") ? 1000000 : 1000);
			}
			int k = str.startsWith("+") ? 1 : -1;
			str = str.substring(1);
			return Double.parseDouble(str.substring(0, str.length() - 1)) * k;
		}
		return -1D;
	}

	public void main(final Graphics g) {
		long totalTime = System.currentTimeMillis() - startTime;
		final String formattedTime = formatTime((int) totalTime);
		g.setColor(LineColor);
		g.drawString("Time running: " + formattedTime, 63, 390);
		g.drawString("Location: " + Location(), 63, 404);
		g.drawString("Status: " + status, 63, 418);
		g.drawString("Current NPC: " + currentNPC, 63, 433);
		g.drawString("Total XP: " + formatter.format((long) xpGained), 63, 447);
		g.drawString("Total XP/h: " + formatter.format((long) xpHour), 63, 463);
	}

	public void drawMouse(final Graphics g) {
		final Point loc = mouse.getLocation();
		final long mpt = System.currentTimeMillis() - mouse.getPressTime();
		if (mouse.getPressTime() == -1 || mpt >= 1000) {
			g.setColor(ThinColor);
			g.drawLine(0, loc.y, 766, loc.y);
			g.drawLine(loc.x, 0, loc.x, 505);
			g.setColor(MainColor);
			g.drawLine(0, loc.y + 1, 766, loc.y + 1);
			g.drawLine(0, loc.y - 1, 766, loc.y - 1);
			g.drawLine(loc.x + 1, 0, loc.x + 1, 505);
			g.drawLine(loc.x - 1, 0, loc.x - 1, 505);
		}
		if (mpt < 1000) {
			g.setColor(ClickC);
			g.drawLine(0, loc.y, 766, loc.y);
			g.drawLine(loc.x, 0, loc.x, 505);
			g.setColor(MainColor);
			g.drawLine(0, loc.y + 1, 766, loc.y + 1);
			g.drawLine(0, loc.y - 1, 766, loc.y - 1);
			g.drawLine(loc.x + 1, 0, loc.x + 1, 505);
			g.drawLine(loc.x - 1, 0, loc.x - 1, 505);
		}
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		//X Button
		if (e.getX() >= 497 && e.getX() < 497 + 16 && e.getY() >= 344 && e.getY() < 344 + 16) {
			if (!xButton) {
				xButton = true;
			} else {
				xButton = false;
			}
		}
		//Next Button
		if (e.getX() >= 478 && e.getX() < 478 + 16 && e.getY() >= 413 && e.getY() < 413 + 14) {
			if (Main) {
				Main = false;
				if (atxpGained != 0) {
					StatAT = true;
				} else if (stxpGained != 0) {
					StatST = true;
				} else if (dfxpGained != 0) {
					StatDF = true;
				} else if (rgxpGained != 0) {
					StatRG = true;
				} else {
					StatCO = true;
				}
			} else if (StatAT) {
				StatAT = false;
				if (stxpGained != 0) {
					StatST = true;
				} else if (dfxpGained != 0) {
					StatDF = true;
				} else if (rgxpGained != 0) {
					StatRG = true;
				} else {
					StatCO = true;
				}
			} else if (StatST) {
				StatST = false;
				if (dfxpGained != 0) {
					StatDF = true;
				} else if (rgxpGained != 0) {
					StatRG = true;
				} else {
					StatCO = true;
				}
			} else if (StatDF) {
				StatDF = false;
				if (rgxpGained != 0) {
					StatRG = true;
				} else {
					StatCO = true;
				}
			} else if (StatRG) {
				StatRG = false;
				StatCO = true;
			} else if (StatCO) {
				StatCO = false;
				Main = true;
			}
		}
		//Prev Button
		if (e.getX() >= 25 && e.getX() < 25 + 16 && e.getY() >= 413 && e.getY() < 413 + 14) {
			if (Main) {
				Main = false;
				StatCO = true;
			} else if (StatCO) {
				StatCO = false;
				if (dfxpGained != 0) {
					StatDF = true;
				} else if (stxpGained != 0) {
					StatST = true;
				} else if (atxpGained != 0) {
					StatAT = true;
				} else if (rgxpGained != 0) {
					StatRG = true;
				} else {
					Main = true;
				}
			} else if (StatDF) {
				StatDF = false;
				if (stxpGained != 0) {
					StatST = true;
				} else if (atxpGained != 0) {
					StatAT = true;
				} else if (rgxpGained != 0) {
					StatRG = true;
				} else {
					Main = true;
				}
			} else if (StatST) {
				StatST = false;
				if (atxpGained != 0) {
					StatAT = true;
				} else if (rgxpGained != 0) {
					StatRG = true;
				} else {
					Main = true;
				}
			} else if (StatAT) {
				StatAT = false;
				if (rgxpGained != 0) {
					StatRG = true;
				} else {
					Main = true;
				}
			} else if (StatRG) {
				StatRG = false;
				Main = true;
			}
		}

	}

	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void messageReceived(MessageEvent e) {
		if (e.getMessage().contains("You've just advanced an At")) {
			atgainedLvl++;
		}
		if (e.getMessage().contains("You've just advanced a Str")) {
			stgainedLvl++;
		}
		if (e.getMessage().contains("You've just advanced a Con")) {
			cogainedLvl++;
		}
		if (e.getMessage().contains("You've just advanced a Ran")) {
			rggainedLvl++;
		}
		if (e.getMessage().contains("You've just advanced a Def")) {
			dfgainedLvl++;
		}
		if (e.getMessage().contains("There is no ammo")) {
			equip = true;
		}
	}

	private int getStat() {
		if (StatDF) {
			currentStat = " Defense";
			return 1;
		} else if (StatST) {
			currentStat = " Strength";
			return 2;
		} else if (StatAT) {
			currentStat = " Attack";
			return 0;
		} else if (StatCO) {
			currentStat = " Cons.";
			return 3;
		} else if (StatRG) {
			currentStat = " Range";
			return 4;
		} else {
			currentStat = " Cons.";
			return 3;
		}
	}

	private Image getImage(String url) {
		try {
			return ImageIO.read(new URL(url));
		} catch (IOException e) {
			return null;
		}
	}

	private Image logo = getImage("http://i88.photobucket.com/albums/k170/aaimister/AaimistersRoaches.gif");
	private Image atom = getImage("http://i88.photobucket.com/albums/k170/aaimister/Atomm.png");

	public void onRepaint(Graphics g) {
		long totalTime = System.currentTimeMillis() - startTime;
		if (!currentlyBreaking) {
			runTime = (System.currentTimeMillis() - startTime) - totalBreakTime;
			now = (totalTime);
			checked = false;
		} else {
			if (!game.isLoggedIn()) {
				if (!checked) {
					runTime = (now - lastBreakTime);
					checked = true;
				}
			}
		}

		if (startTime != 0) {
			//AT
			atcurrentXP = skills.getExpToNextLevel(0);
			atxpGained = skills.getCurrentExp(0) - atstartEXP;
			atxpToLvl = skills.getExpToNextLevel(0);
			atxpHour = ((int) ((3600000.0 / (double) runTime) * atxpGained));
			if (atxpHour != 0) {
				attimeToLvl = (int) (((double) atcurrentXP / (double) atxpHour) * 3600000.0);
			}
			//ST
			stcurrentXP = skills.getExpToNextLevel(2);
			stxpGained = skills.getCurrentExp(2) - ststartEXP;
			stxpToLvl = skills.getExpToNextLevel(2);
			stxpHour = ((int) ((3600000.0 / (double) runTime) * stxpGained));
			if (stxpHour != 0) {
				sttimeToLvl = (int) (((double) stcurrentXP / (double) stxpHour) * 3600000.0);
			}
			//DF
			dfcurrentXP = skills.getExpToNextLevel(1);
			dfxpGained = skills.getCurrentExp(1) - dfstartEXP;
			dfxpToLvl = skills.getExpToNextLevel(1);
			dfxpHour = ((int) ((3600000.0 / (double) runTime) * dfxpGained));
			if (dfxpHour != 0) {
				dftimeToLvl = (int) (((double) dfcurrentXP / (double) dfxpHour) * 3600000.0);
			}
			//RG
			rgcurrentXP = skills.getExpToNextLevel(4);
			rgxpGained = skills.getCurrentExp(4) - rgstartEXP;
			rgxpToLvl = skills.getExpToNextLevel(4);
			rgxpHour = ((int) ((3600000.0 / (double) runTime) * rgxpGained));
			if (rgxpHour != 0) {
				rgtimeToLvl = (int) (((double) rgcurrentXP / (double) rgxpHour) * 3600000.0);
			}
			//CO
			cocurrentXP = skills.getExpToNextLevel(3);
			coxpGained = skills.getCurrentExp(3) - costartEXP;
			coxpToLvl = skills.getExpToNextLevel(3);
			coxpHour = ((int) ((3600000.0 / (double) runTime) * coxpGained));
			if (coxpHour != 0) {
				cotimeToLvl = (int) (((double) cocurrentXP / (double) coxpHour) * 3600000.0);
			}
			xpGained = dfxpGained + stxpGained + atxpGained + coxpGained;
			xpHour = ((int) ((3600000.0 / (double) runTime) * xpGained));
			charmsHour = (int) ((3600000.0 / (double) runTime) * totalCharms);
			itemsHour = (int) ((3600000.0 / (double) runTime) * totalItems);
			GPHour = (int) ((3600000.0 / (double) runTime) * totalPrice);
			rHour = (int) ((3600000.0 / (double) runTime) * rCount);
		}

		if (painting) {
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		}

		//Objects
		//drawObjects(g);

		if (!xButton) {
			//Background
			g.setColor(MainColor);
			g.fillRect(6, 344, 507, 129);
			g.setColor(LineColor);
			g.drawRect(6, 344, 507, 129);
			//Logo
			g.drawImage(logo, 6, 348, null);
			g.drawImage(atom, 40, 358, null);
			g.setColor(LineColor);
			g.setFont(Cam10);
			g.drawString("By Aaimister © " + getVersion(), 379, 369);
			//Next Button
			g.setColor(BoxColor);
			g.fillRect(478, 413, 16, 14);
			g.setColor(LineColor);
			g.setFont(Cam);
			g.drawString(">", 481, 424);
			g.drawRect(478, 413, 16, 14);
			//Shadow
			g.setColor(White90);
			g.fillRect(478, 413, 16, 7);
			//Prev Button
			g.setColor(BoxColor);
			g.fillRect(25, 413, 16, 14);
			g.setColor(LineColor);
			g.setFont(Cam);
			g.drawString("<", 28, 424);
			g.drawRect(25, 413, 16, 14);
			//Shadow
			g.setColor(White90);
			g.fillRect(25, 413, 16, 7);
			//Main Box
			g.setColor(BoxColor);
			g.fillRect(59, 374, 401, 95);
			g.setColor(White90);
			g.fillRect(59, 374, 401, 46);
			//Text
			if (Main) {
				//Column 1
				main(g);
				//Column 2
				g.drawString("Total Money: $" + formatter.format((long) totalPrice), 264, 390);
				g.drawString("Money / Hour: $" + formatter.format((long) GPHour), 264, 404);
				g.drawString("Total Item(s): " + formatter.format((long) totalItems), 264, 418);
				g.drawString("Item(s) / Hour: " + formatter.format((long) itemsHour), 264, 433);
				if (member) {
					g.drawString("Total Charm(s): " + formatter.format((long) totalCharms), 264, 447);
					g.drawString("Charm(s) / Hour: " + formatter.format((long) charmsHour), 264, 463);
				} else {
					g.drawString("Roaches Attacked: " + formatter.format((long) rCount), 264, 447);
					g.drawString("Roaches / Hour: " + formatter.format((long) rHour), 264, 463);
				}
			}
			if (StatAT) {
				//Column 1
				main(g);
				//Column 2
				g.drawString("Total Attack XP: " + formatter.format((long) atxpGained), 264, 390);
				g.drawString("Attack XP/h: " + formatter.format((long) atxpHour), 264, 404);
				g.drawString("Level In: " + formatTime(attimeToLvl), 264, 418);
				g.drawString("Attack XP to Lvl: " + formatter.format((long) atxpToLvl), 264, 433);
				g.drawString("Current Lvl: " + (skills.getCurrentLevel(0)), 264, 447);
				g.drawString("Gained Lvl(s): " + formatter.format((long) atgainedLvl), 264, 463);
			}
			if (StatST) {
				//Column 1
				main(g);
				//Column 2
				g.drawString("Total Strength XP: " + formatter.format((long) stxpGained), 264, 390);
				g.drawString("Strength XP/h: " + formatter.format((long) stxpHour), 264, 404);
				g.drawString("Level In: " + formatTime(sttimeToLvl), 264, 418);
				g.drawString("Strength XP to Lvl: " + formatter.format((long) stxpToLvl), 264, 433);
				g.drawString("Current Lvl: " + (skills.getCurrentLevel(2)), 264, 447);
				g.drawString("Gained Lvl(s): " + formatter.format((long) stgainedLvl), 264, 463);
			}
			if (StatDF) {
				//Column 1
				main(g);
				//Column 2
				g.drawString("Total Defence XP: " + formatter.format((long) dfxpGained), 264, 390);
				g.drawString("Defence XP/h: " + formatter.format((long) dfxpHour), 264, 404);
				g.drawString("Level In: " + formatTime(dftimeToLvl), 264, 418);
				g.drawString("Defence XP to Lvl: " + formatter.format((long) dfxpToLvl), 264, 433);
				g.drawString("Current Lvl: " + (skills.getCurrentLevel(1)), 264, 447);
				g.drawString("Gained Lvl(s): " + formatter.format((long) dfgainedLvl), 264, 463);
			}
			if (StatRG) {
				//Column 1
				main(g);
				//Column 2
				g.drawString("Total Range XP: " + formatter.format((long) rgxpGained), 264, 390);
				g.drawString("Range XP/h: " + formatter.format((long) rgxpHour), 264, 404);
				g.drawString("Level In: " + formatTime(rgtimeToLvl), 264, 418);
				g.drawString("Range XP to Lvl: " + formatter.format((long) rgxpToLvl), 264, 433);
				g.drawString("Current Lvl: " + (skills.getCurrentLevel(4)), 264, 447);
				g.drawString("Gained Lvl(s): " + formatter.format((long) rggainedLvl), 264, 463);
			}
			if (StatCO) {
				//Column 1
				main(g);
				//Column 2
				g.drawString("Total Cons. XP: " + formatter.format((long) coxpGained), 264, 390);
				g.drawString("Cons. XP/h: " + formatter.format((long) coxpHour), 264, 404);
				g.drawString("Level In: " + formatTime(cotimeToLvl), 264, 418);
				g.drawString("Cons. XP to Lvl: " + formatter.format((long) coxpToLvl), 264, 433);
				g.drawString("Current Lvl: " + (skills.getCurrentLevel(3)), 264, 447);
				g.drawString("Gained Lvl(s): " + formatter.format((long) cogainedLvl), 264, 463);
			}
			//% Bar
			g.setColor(MainColor);
			g.fillRect(4, 318, 512, 20);
			g.setColor(Black);
			g.fillRect(6, 320, 508, 16);
			g.setColor(PercentRed);
			g.fillRect(6, 320, 508, 16);
			g.setColor(PercentGreen);
			g.fillRect(6, 320, skills.getPercentToNextLevel(getStat()) * (508 / 100), 16);
			g.setColor(White);
			g.setFont(Cam);
			g.drawString("" + skills.getPercentToNextLevel(getStat()) + "% to lvl " + (skills.getCurrentLevel(getStat()) + 1) + currentStat, 194, 332);
			//Shadow
			g.setColor(White90);
			g.fillRect(4, 318, 512, 10);
			//X
			g.setColor(LineColor);
			g.setFont(Cam);
			g.drawString("X", 501, 357);
			//Main Box Shadow
			g.setColor(LineColor);
			g.drawRect(59, 374, 401, 95);
			g.drawLine(260, 380, 260, 465);
		} else {
			//X Button
			g.setColor(MainColor);
			g.fillRect(497, 344, 16, 16);
			g.setColor(LineColor);
			g.drawRect(497, 344, 16, 16);
			//X
			g.setColor(LineColor);
			g.setFont(Cam);
			g.drawString("O", 501, 357);
			//Shadow
			g.setColor(White90);
			g.fillRect(497, 344, 17, 8);
		}

		//Mouse
		drawMouse(g);
	}

	private String cleaned(String s, String char1, String char2) {
		ArrayList<Integer> start = new ArrayList<Integer>(50);
		ArrayList<Integer> end = new ArrayList<Integer>(50);
		ArrayList<String> fin = new ArrayList<String>(50);
		for (int i = 0; i < s.lastIndexOf(char1); i++) {
			if (s.indexOf(char1, i) > 0) {
				if (!start.contains(s.indexOf(char1, i))) {
					start.add(s.indexOf(char1, i));
				}
			}
		}
		for (int e = 0; e < s.lastIndexOf(char2); e++) {
			if (s.indexOf(char2, e) > 0) {
				if (!end.contains(s.indexOf(char2, e))) {
					end.add(s.indexOf(char2, e));
				}
			}
		}
		for (int f = 0; f < start.size(); f++) {
			fin.add(s.substring(start.get(f) + 1, end.get(f)));
		}
		return fin.toString();
	}

	private boolean readLoot(String url, DefaultListModel mod) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
			String line = null;
			String[] opts = {};
			while ((line = in.readLine()) != null) {
				if (line.contains(";")) {
					opts = line.split(";");
				}
			}
			in.close();
			lootString = opts;
			for (int i = 0; i < lootString.length; i++) {
				doLoot.add(lootString[i].toString());
				mod.addElement(lootString[i]);
			}
		} catch (IOException e) {
			log("Problem getting loot.");
		}
		return true;
	}

	public class AaimistersGUI {

		private void list1ValueChanged(ListSelectionEvent e) {
			String text = (String) noList.getSelectedValue();
			if ((text == null) || text.isEmpty()) {
				return;
			}
			lootTable.addElement(text);
			noTable.remove(noList.getSelectedIndex());
		}

		private void list2ValueChanged(ListSelectionEvent e) {
			String text = (String) doList.getSelectedValue();
			if ((text == null) || text.isEmpty()) {
				return;
			}
			noTable.addElement(text);
			lootTable.remove(doList.getSelectedIndex());
		}

		public void submitActionPerformed(ActionEvent e) {
			String color = (String) colorBox.getSelectedItem();
			if (color.contains("Blue")) {
				MainColor = new Color(0, 0, 100);
				ThinColor = new Color(0, 0, 100, 70);
				LineColor = new Color(255, 255, 255);
				BoxColor = MainColor;
			} else if (color.contains("Black")) {
				MainColor = new Color(0, 0, 0);
				ThinColor = new Color(0, 0, 0, 70);
				LineColor = new Color(255, 255, 255);
				BoxColor = MainColor;
			} else if (color.contains("Brown")) {
				MainColor = new Color(92, 51, 23);
				ThinColor = new Color(92, 51, 23, 70);
				BoxColor = MainColor;
			} else if (color.contains("Cyan")) {
				MainColor = new Color(0, 255, 255);
				ThinColor = new Color(0, 255, 255, 70);
				BoxColor = MainColor;
				LineColor = new Color(0, 0, 0);
			} else if (color.contains("Green")) {
				MainColor = new Color(0, 100, 0);
				ThinColor = new Color(0, 100, 0, 70);
				BoxColor = MainColor;
			} else if (color.contains("Lime")) {
				MainColor = new Color(0, 220, 0);
				ThinColor = new Color(0, 220, 0, 70);
				BoxColor = MainColor;
				LineColor = new Color(0, 0, 0);
			} else if (color.contains("Orange")) {
				MainColor = new Color(255, 127, 0);
				ThinColor = new Color(255, 127, 0, 70);
				BoxColor = MainColor;
				LineColor = new Color(0, 0, 0);
			} else if (color.contains("Pink")) {
				MainColor = new Color(238, 18, 137);
				ThinColor = new Color(238, 18, 137, 70);
				BoxColor = MainColor;
				LineColor = new Color(0, 0, 0);
			} else if (color.contains("Purple")) {
				MainColor = new Color(104, 34, 139);
				ThinColor = new Color(104, 34, 139, 70);
				BoxColor = MainColor;
			} else if (color.contains("Red")) {
				MainColor = new Color(100, 0, 0);
				ThinColor = new Color(100, 0, 0, 70);
				ClickC = Black;
				BoxColor = MainColor;
			} else if (color.contains("White")) {
				MainColor = new Color(255, 255, 255);
				ThinColor = new Color(255, 255, 255, 70);
				LineColor = new Color(0, 0, 0);
				BoxColor = new Color(140, 140, 140);
				LineColor = new Color(0, 0, 0);
			} else if (color.contains("Yellow")) {
				MainColor = new Color(238, 201, 0);
				ThinColor = new Color(238, 201, 0, 70);
				BoxColor = MainColor;
				LineColor = new Color(0, 0, 0);
			}
			if (paintBox.isSelected()) {
				painting = true;
			}
			if (antibanBox.isSelected()) {
				antiBanOn = true;
			}
			if (room2Box.isSelected()) {
				room2 = true;
				rArea = AM.rArea2;
			} else {
				room2 = false;
				rArea = AM.rArea1;
			}
			if (freeBox.isSelected()) {
				free = true;
			}
			X = Integer.parseInt(withText.getValue().toString());
			food = Integer.parseInt(foodText.getValue().toString());
			minHealth = Integer.parseInt(healText.getValue().toString());
			if (breakBox.isSelected()) {
				doBreak = true;
				if (randomBox.isSelected()) {
					randomBreaks = true;
				} else {
					maxBetween = Integer.parseInt(maxTimeBeBox.getValue().toString());
					minBetween = Integer.parseInt(minTimeBeBox.getValue().toString());
					maxLength = Integer.parseInt(maxBreakBox.getValue().toString());
					minLength = Integer.parseInt(minBreakBox.getValue().toString());
					if (minBetween < 1) {
						minBetween = 1;
					}
					if (minLength < 1) {
						minLength = 1;
					}
					if (maxBetween > 5000) {
						maxBetween = 5000;
					} else if (maxBetween < 6) {
						maxBetween = 6;
					}
					if (maxLength > 5000) {
						maxLength = 5000;
					} else if (maxLength < 5) {
						maxLength = 5;
					}
				}
			}
			if (lootTable.getSize() > 1) {
				doLoot.clear();
				String fin;
				for (int i = 0; i < lootTable.getSize(); i++) {
					String add = cleaned(lootTable.get(i).toString(), "(", ")");
					fin = add.toString();
					if (add.contains("[")) {
						fin = add.replace("[", "");
					}
					if (add.contains("]")) {
						fin = add.replace("]", "");
					}
					if (add.contains("[]")) {
						fin = add.replace("[]", "");
					}
					doLoot.add(fin.substring(1).trim());
				}
				if (doLoot.contains("1111")) {
					for (int i = 0; i < Citems.length; i++) {
						doLoot.add(Integer.toString(Citems[i]));
					}
					doLoot.remove("1111");
				}
				if (doLoot.contains("0000")) {
					for (int i = 0; i < tableItems.length; i++) {
						doLoot.add(Integer.toString(tableItems[i]));
					}
					doLoot.remove("0000");
				}
			}

			// Write settings
			try {
				final BufferedWriter out = new BufferedWriter(new FileWriter(glootFile));
				final BufferedWriter out2 = new BufferedWriter(new FileWriter(blootFile));
				final BufferedWriter out3 = new BufferedWriter(new FileWriter(flootFile));
				String good = lootTable.toString();
				String bad = noTable.toString();
				String fin = cleaned(good, "(", ")");
				out.write(good);
				out.close();
				out2.write(bad);
				out2.close();
				out3.write(fin);
				out3.close();
			} catch (final Exception e1) {
				log.warning("Error saving loot.");
			}

			try {
				final BufferedWriter out = new BufferedWriter(new FileWriter(settingsFile));
				out.write((room2Box.isSelected() ? true : false)
						+ ":" // 0
						+ (freeBox.isSelected() ? true : false)
						+ ":" // 1
						+ (colorBox.getSelectedIndex())
						+ ":" // 2
						+ (antibanBox.isSelected() ? true : false)
						+ ":" // 3
						+ (paintBox.isSelected() ? true : false)
						+ ":" // 4
						+ (breakBox.isSelected() ? true : false)
						+ ":" // 5
						+ (randomBox.isSelected() ? true : false)
						+ ":" // 6
						+ (maxTimeBeBox.getValue().toString())
						+ ":" // 7
						+ (minTimeBeBox.getValue().toString())
						+ ":" // 8
						+ (maxBreakBox.getValue().toString())
						+ ":" // 9
						+ (minBreakBox.getValue().toString())
						+ ":" // 10
						+ (healText.getValue().toString())
						+ ":" // 11
						+ (foodText.getValue().toString())
						+ ":" // 12
						+ (withText.getValue().toString())
						// 13
				);
				out.close();
			} catch (final Exception e1) {
				log.warning("Error saving setting.");
			}
			// End write settings

			AaimistersGUI.dispose();
		}

		private AaimistersGUI() {
			initComponents();
		}

		public void initComponents() {
			AaimistersGUI = new JFrame();
			contentPane = new JPanel();
			colorBox = new JComboBox();
			antibanBox = new JCheckBox();
			paintBox = new JCheckBox();
			breakBox = new JCheckBox();
			room2Box = new JCheckBox();
			freeBox = new JCheckBox();
			randomBox = new JCheckBox();
			healText = new JSpinner();
			foodText = new JSpinner();
			withText = new JSpinner();
			maxTimeBeBox = new JSpinner();
			minTimeBeBox = new JSpinner();
			maxBreakBox = new JSpinner();
			minBreakBox = new JSpinner();
			lootTable = new DefaultListModel();
			lootScroll = new JScrollPane();
			noTable = new DefaultListModel();
			noScroll = new JScrollPane();
			submit = new JButton();

			AaimistersGUI.setResizable(false);
			AaimistersGUI.setTitle("Aaimister's Roach Killer");
			AaimistersGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			AaimistersGUI.setBounds(100, 100, 450, 344);
			contentPane = new JPanel();
			contentPane.setBackground(UIManager.getColor("Button.background"));
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			AaimistersGUI.setContentPane(contentPane);
			// Listeners
			AaimistersGUI.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					closed = true;
				}
			});

			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

			JLabel lblAaimistersRoachKiller = new JLabel("<html><img src=http://i88.photobucket.com/albums/k170/aaimister/Untitled-2-2.png /></html>");
			lblAaimistersRoachKiller.setHorizontalAlignment(SwingConstants.CENTER);
			lblAaimistersRoachKiller.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));

			submit.setText("Start");
			submit.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
			submit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					submitActionPerformed(e);
				}
			});

			JPanel panel = new JPanel();
			tabbedPane.addTab("General", null, panel, null);

			room2Box.setText("Room Two");

			antibanBox.setText("Anti Ban");
			antibanBox.setSelected(true);

			freeBox.setText("Only Attack Free Roaches");

			paintBox.setText("Anti Aliasing");
			paintBox.setSelected(true);

			JLabel lblFoodId = new JLabel("Food ID:");
			lblFoodId.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));

			JLabel lblEatWhenBelow = new JLabel("Eat When Below:");
			lblEatWhenBelow.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));

			JLabel lblPaintColor = new JLabel("Paint Color:");
			lblPaintColor.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));

			healText.setModel(new SpinnerNumberModel(new Integer(300), null, null, new Integer(1)));

			colorBox.setModel(new DefaultComboBoxModel(colorstring));

			foodText.setModel(new SpinnerNumberModel(new Integer(379), null, null, new Integer(1)));

			JLabel lblWitdraw = new JLabel("Witdraw:");
			lblWitdraw.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));

			withText.setModel(new SpinnerNumberModel(new Integer(20), null, null, new Integer(1)));
			GroupLayout gl_panel = new GroupLayout(panel);
			gl_panel.setHorizontalGroup(
					gl_panel.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_panel.createSequentialGroup()
									.addContainerGap()
									.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
											.addGroup(gl_panel.createSequentialGroup()
													.addComponent(freeBox)
													.addGap(18)
													.addComponent(lblWitdraw))
											.addGroup(gl_panel.createSequentialGroup()
													.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
															.addComponent(room2Box)
															.addComponent(antibanBox)
															.addComponent(paintBox))
													.addGap(85)
													.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
															.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
																	.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
																			.addGroup(Alignment.LEADING, gl_panel.createSequentialGroup()
																					.addComponent(lblEatWhenBelow)
																					.addPreferredGap(ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
																					.addComponent(healText, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE))
																			.addGroup(Alignment.LEADING, gl_panel.createSequentialGroup()
																					.addComponent(lblPaintColor)
																					.addPreferredGap(ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
																					.addComponent(colorBox, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)))
																	.addGap(36))
															.addGroup(gl_panel.createSequentialGroup()
																	.addComponent(lblFoodId)
																	.addGap(46)
																	.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
																			.addComponent(withText, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
																			.addComponent(foodText, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE))))))
									.addContainerGap())
			);
			gl_panel.setVerticalGroup(
					gl_panel.createParallelGroup(Alignment.TRAILING)
							.addGroup(gl_panel.createSequentialGroup()
									.addGap(21)
									.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
											.addGroup(gl_panel.createSequentialGroup()
													.addComponent(room2Box)
													.addGap(18)
													.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
															.addComponent(antibanBox)
															.addComponent(lblPaintColor)
															.addComponent(colorBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
													.addGap(18)
													.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
															.addComponent(paintBox)
															.addComponent(lblFoodId)
															.addComponent(foodText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
											.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
													.addComponent(lblEatWhenBelow)
													.addComponent(healText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
									.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
											.addGroup(gl_panel.createSequentialGroup()
													.addGap(18)
													.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
															.addComponent(freeBox)
															.addComponent(lblWitdraw)))
											.addGroup(gl_panel.createSequentialGroup()
													.addGap(18)
													.addComponent(withText, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
									.addGap(22))
			);
			panel.setLayout(gl_panel);

			Panel panel_3 = new Panel();
			tabbedPane.addTab("Loot", null, panel_3, null);
			panel_3.setLayout(null);

			JLabel lblWhatToLoot = new JLabel("What to Loot");
			lblWhatToLoot.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
			lblWhatToLoot.setBounds(43, 21, 93, 14);
			panel_3.add(lblWhatToLoot);

			JLabel lblDoNotLoot = new JLabel("Do not Loot");
			lblDoNotLoot.setFont(new Font("Comic Sans MS", Font.PLAIN, 13));
			lblDoNotLoot.setBounds(300, 21, 93, 14);
			panel_3.add(lblDoNotLoot);

			doList = new JList(lootTable);
			readLoot("http://aaimister.webs.com/scripts/rLoot.txt", lootTable);
			lootScroll.setBounds(14, 49, 155, 127);
			lootScroll.getViewport().setView(doList);
			panel_3.add(lootScroll);
			doList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					list2ValueChanged(e);
				}
			});

			noList = new JList(noTable);
			noScroll.setBounds(268, 49, 155, 127);
			noScroll.getViewport().setView(noList);
			panel_3.add(noScroll);
			noList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					list1ValueChanged(e);
				}
			});

			JPanel panel_1 = new JPanel();
			tabbedPane.addTab("Breaks", null, panel_1, null);

			breakBox.setText("Custom Breaks");

			randomBox.setText("Random Breaks");

			JLabel lblTimeBetweenBreaks = new JLabel("Time Between Breaks:");
			lblTimeBetweenBreaks.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));

			minTimeBeBox.setModel(new SpinnerNumberModel(new Integer(120), null, null, new Integer(1)));

			JLabel lblMins = new JLabel("mins");
			lblMins.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));

			JLabel lblTo = new JLabel("to");
			lblTo.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));

			maxTimeBeBox.setModel(new SpinnerNumberModel(new Integer(220), null, null, new Integer(1)));

			JLabel label = new JLabel("mins");
			label.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));

			JLabel lblBreakLengths = new JLabel("Break Lengths:");
			lblBreakLengths.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));

			minBreakBox.setModel(new SpinnerNumberModel(new Integer(15), null, null, new Integer(1)));

			JLabel label_1 = new JLabel("mins");
			label_1.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));

			JLabel label_2 = new JLabel("to");
			label_2.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));

			maxBreakBox.setModel(new SpinnerNumberModel(new Integer(60), null, null, new Integer(1)));

			JLabel label_3 = new JLabel("mins");
			label_3.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
			GroupLayout gl_panel_1 = new GroupLayout(panel_1);
			gl_panel_1.setHorizontalGroup(
					gl_panel_1.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_panel_1.createSequentialGroup()
									.addGap(42)
									.addComponent(breakBox)
									.addPreferredGap(ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
									.addComponent(randomBox)
									.addGap(62))
							.addGroup(gl_panel_1.createSequentialGroup()
									.addContainerGap()
									.addComponent(lblTimeBetweenBreaks)
									.addContainerGap(269, Short.MAX_VALUE))
							.addGroup(gl_panel_1.createSequentialGroup()
									.addContainerGap()
									.addComponent(lblBreakLengths)
									.addContainerGap(371, Short.MAX_VALUE))
							.addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
									.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
											.addGroup(gl_panel_1.createSequentialGroup()
													.addContainerGap()
													.addComponent(minBreakBox, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
													.addGap(5)
													.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
													.addGap(60)
													.addComponent(label_2, GroupLayout.PREFERRED_SIZE, 12, GroupLayout.PREFERRED_SIZE)
													.addGap(108)
													.addComponent(maxBreakBox, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
													.addGap(5)
													.addComponent(label_3, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
											.addGroup(gl_panel_1.createSequentialGroup()
													.addGap(41)
													.addGroup(gl_panel_1.createSequentialGroup()
															.addComponent(minTimeBeBox, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
															.addPreferredGap(ComponentPlacement.RELATED)
															.addComponent(lblMins)
															.addGap(60)
															.addComponent(lblTo))
													.addPreferredGap(ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
													.addComponent(maxTimeBeBox, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
													.addPreferredGap(ComponentPlacement.RELATED)
													.addComponent(label, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)))
									.addGap(40))
			);
			gl_panel_1.setVerticalGroup(
					gl_panel_1.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_panel_1.createSequentialGroup()
									.addContainerGap()
									.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
											.addComponent(breakBox)
											.addComponent(randomBox))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lblTimeBetweenBreaks)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
											.addComponent(label, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
											.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
													.addComponent(lblTo)
													.addComponent(lblMins))
											.addComponent(minTimeBeBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(maxTimeBeBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addGap(18)
									.addComponent(lblBreakLengths)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
											.addComponent(minBreakBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addGroup(gl_panel_1.createSequentialGroup()
													.addGap(2)
													.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))
											.addGroup(gl_panel_1.createSequentialGroup()
													.addGap(2)
													.addComponent(label_2, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE))
											.addComponent(maxBreakBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addGroup(gl_panel_1.createSequentialGroup()
													.addGap(2)
													.addComponent(label_3, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)))
									.addContainerGap(95, Short.MAX_VALUE))
			);
			panel_1.setLayout(gl_panel_1);
			GroupLayout gl_contentPane = new GroupLayout(contentPane);
			gl_contentPane.setHorizontalGroup(
					gl_contentPane.createParallelGroup(Alignment.LEADING)
							.addComponent(lblAaimistersRoachKiller, GroupLayout.PREFERRED_SIZE, 434, GroupLayout.PREFERRED_SIZE)
							.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 434, GroupLayout.PREFERRED_SIZE)
							.addGroup(gl_contentPane.createSequentialGroup()
									.addGap(169)
									.addComponent(submit, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE))
			);
			gl_contentPane.setVerticalGroup(
					gl_contentPane.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(lblAaimistersRoachKiller, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
									.addGap(6)
									.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 214, GroupLayout.PREFERRED_SIZE)
									.addGap(11)
									.addComponent(submit))
			);
			contentPane.setLayout(gl_contentPane);
			// LOAD SAVED SELECTION INFO
			try {
				String filename = getCacheDirectory() + "\\GoodLootList.txt";
				BufferedReader in = new BufferedReader(new FileReader(filename));
				String line;
				lootTable.clear();
				while ((line = in.readLine()) != null) {
					String[] add = line.split(",");
					String fin;
					for (int i = 0; i < add.length; i++) {
						if (!lootTable.contains(add[i].toString())) {
							fin = add[i].toString();
							if (add[i].contains("[")) {
								fin = add[i].replace("[", "");
							}
							if (add[i].contains("]")) {
								fin = add[i].replace("]", "");
							}
							if (add[i].contains("[]")) {
								fin = add[i].replace("[]", "");
							}
							lootTable.addElement(fin.trim());
						}
					}
				}
				in.close();
			} catch (final Exception e) {
				//e2.printStackTrace();
				log.warning("Failed to load your good loot. If this is first time running script, ignore.");
			}

			try {
				String filename = getCacheDirectory() + "\\BadLootList.txt";
				BufferedReader in = new BufferedReader(new FileReader(filename));
				String line;
				noTable.clear();
				while ((line = in.readLine()) != null) {
					String[] add = line.split(",");
					String fin;
					for (int i = 0; i < add.length; i++) {
						if (!lootTable.contains(add[i].toString())) {
							fin = add[i].toString();
							if (add[i].contains("[")) {
								fin = add[i].replace("[", "");
							}
							if (add[i].contains("]")) {
								fin = add[i].replace("]", "");
							}
							if (add[i].contains("[]")) {
								fin = add[i].replace("[]", "");
							}
							noTable.addElement(fin.trim());
						}
					}
				}
				in.close();
			} catch (final Exception e) {
				//e2.printStackTrace();
				log.warning("Failed to load your bad loot. If this is first time running script, ignore.");
			}

			try {
				String filename = getCacheDirectory() + "\\RealLootList.txt";
				BufferedReader in = new BufferedReader(new FileReader(filename));
				String line;
				doLoot.clear();
				while ((line = in.readLine()) != null) {
					String[] add = line.split(",");
					String fin;
					for (int i = 0; i < add.length; i++) {
						if (!lootTable.contains(add[i].toString())) {
							fin = add[i].toString();
							if (add[i].contains("[")) {
								fin = add[i].replace("[", "");
							}
							if (add[i].contains("]")) {
								fin = add[i].replace("]", "");
							}
							if (add[i].contains("[]")) {
								fin = add[i].replace("[]", "");
							}
							doLoot.add(fin.trim());
						}
					}
				}
				in.close();
			} catch (final Exception e) {
				//e2.printStackTrace();
				log.warning("Failed to load your loot. If this is first time running script, ignore.");
			}

			try {
				String filename = getCacheDirectory() + "\\AaimistersRKillerSettings.txt";
				Scanner in = new Scanner(new BufferedReader(new FileReader(filename)));
				String line;
				String[] opts = {};
				while (in.hasNext()) {
					line = in.next();
					if (line.contains(":")) {
						opts = line.split(":");
					}
				}
				in.close();
				if (opts.length > 1) {
					if (opts[5].equals("true")) {
						breakBox.setSelected(true);
						if (opts[6].equals("false")) {
							randomBox.setSelected(false);
							maxTimeBeBox.setValue(Integer.parseInt(opts[7]));
							minTimeBeBox.setValue(Integer.parseInt(opts[8]));
							maxBreakBox.setValue(Integer.parseInt(opts[9]));
							minBreakBox.setValue(Integer.parseInt(opts[10]));
						} else {
							randomBox.setSelected(true);
						}
					} else {
						breakBox.setSelected(false);
					}
					if (opts[0].equals("true")) {
						room2Box.setSelected(true);
					} else {
						room2Box.setSelected(false);
					}
					if (opts[1].equals("true")) {
						freeBox.setSelected(true);
					} else {
						freeBox.setSelected(false);
					}
					colorBox.setSelectedIndex(Integer.parseInt(opts[2]));
					if (opts[3].equals("true")) {
						antibanBox.setSelected(true);
					} else {
						antibanBox.setSelected(false);
					}
					if (opts[4].equals("true")) {
						paintBox.setSelected(true);
					} else {
						paintBox.setSelected(false);
					}
					healText.setValue(Integer.parseInt(opts[11]));
					foodText.setValue(Integer.parseInt(opts[12]));
					withText.setValue(Integer.parseInt(opts[13]));
				}
			} catch (final Exception e2) {
				//e2.printStackTrace();
				log.warning("Error loading settings.  If this is first time running script, ignore.");
			}
			// END LOAD SAVED SELECTION INFO
		}

		private JFrame AaimistersGUI;
		private JPanel contentPane;
		private JComboBox colorBox;
		private JCheckBox antibanBox;
		private JCheckBox paintBox;
		private JCheckBox breakBox;
		private JCheckBox randomBox;
		private JCheckBox freeBox;
		private JCheckBox room2Box;
		private JSpinner healText;
		private JSpinner foodText;
		private JSpinner withText;
		private JSpinner maxTimeBeBox;
		private JSpinner minTimeBeBox;
		private JSpinner maxBreakBox;
		private JSpinner minBreakBox;
		private JList doList;
		private JList noList;
		private DefaultListModel lootTable;
		private JScrollPane lootScroll;
		private DefaultListModel noTable;
		private JScrollPane noScroll;
		private JButton submit;
	}
}
