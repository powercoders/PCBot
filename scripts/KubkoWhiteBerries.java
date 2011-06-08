import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;

import javax.imageio.ImageIO;

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Equipment;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.GrandExchange.GEItem;
import org.rsbot.script.methods.Magic;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSComponent;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Kubko" }, keywords = { "White", "Berry", "Picker" }, name = "KubkoWhiteBerries", description = "Pickin' up them berries.", version = 1.0, website = "http://www.powerbot.org/vb/showthread.php?t=722427", requiresVersion = 244)
public class KubkoWhiteBerries extends Script implements PaintListener,
		MessageListener {
	private enum STATE {
		WALK_TO_BANK, LUMB_BANK, WALK_TO_LUMB_BANK, MAKE_CANOE, WORLDHOP, DEAD, WALK_TO_TELEGRAB_TILE, TELEPORT, WALK_TO_TELEGRAB, WALK_TO_GE, GO_TO_LAIR, WALK_TO_CAVE, TELEGRAB, BACK_TO_ENTRANCE, BANK, WALK_TO_SHORTCUT, SLEEP
	}

	public static String arrayToString2(final String[] a, final String separator) {
		final StringBuffer result = new StringBuffer();
		if (a.length > 0) {
			result.append(a[0]);
			for (int i = 1; i < a.length; i++) {
				result.append(separator);
				result.append(a[i]);
			}
		}
		return result.toString();
	}

	int berryId;
	int foodId;
	double version = 1.0;
	int varrockTab = 8007;
	int whiteBerry = 239;
	int geShortcut = 9312;
	int wilderDitch = 1440;
	int closedDeepGate = 1558;
	int closedDeepGate2 = 1597;
	int openDeepGate = 1561;
	int openDeepGate2 = 1563;
	boolean playerPassedShortcut;
	String status = "";
	int berriesPicked;
	long startTime;
	GEItem berry;
	int berryPrice;
	long millis = 0;
	long hours = 0;
	long minutes = 0;
	long seconds = 0;
	long last = 0;
	int moneyPerHour;
	int mode = 2;
	int hatchet = 1359;
	int lawRune = 554;
	int fireRune = 563;
	boolean needsToBank;// / TEst
	int step = 0;; // /TEST
	boolean compassClicked;
	boolean isDead;
	int spellsCasted;
	boolean canoePaddled;
	boolean canoeChopped;
	boolean canoeFloated;
	boolean canoeShaped;
	boolean canoeSelected;
	boolean lumbBanked;
	int plane;
	int airStaffId = 1381;

	boolean worldHopped;;

	boolean firstTeleGrab = true;

	RSArea lair = new RSArea(new RSTile(2883, 4372), new RSTile(2888, 4377));

	RSTile[] toGe = { new RSTile(3209, 3427), new RSTile(3208, 3431),
			new RSTile(3206, 3436), new RSTile(3203, 3441),
			new RSTile(3199, 3444), new RSTile(3194, 3447),
			new RSTile(3189, 3448), new RSTile(3184, 3454),
			new RSTile(3181, 3455), new RSTile(3175, 3459),
			new RSTile(3166, 3461), new RSTile(3165, 3467),
			new RSTile(3163, 3472), new RSTile(3163, 3477),
			new RSTile(3161, 3482), new RSTile(3162, 3489) };

	RSArea entrance = new RSArea(new RSTile(3213, 3778), new RSTile(3215, 3785));

	public final RSTile[] backToEntrance = { new RSTile(3207, 3811),
			new RSTile(3202, 3813), new RSTile(3198, 3819),
			new RSTile(3198, 3825), new RSTile(3197, 3830),
			new RSTile(3198, 3835), new RSTile(3203, 3837),
			new RSTile(3204, 3843), new RSTile(3204, 3848),
			new RSTile(3202, 3853), };
	RSTile teleGrabTile = new RSTile(3218, 3805);

	RSArea teleGrabTileArea = new RSArea(new RSTile(3214, 3790), new RSTile(3230, 3813));

	public final RSTile[] backFromDragonIsle = { new RSTile(3200, 3814),
			new RSTile(3201, 3819), new RSTile(3200, 3824),
			new RSTile(3200, 3832), new RSTile(3200, 3837),
			new RSTile(3202, 3842), new RSTile(3201, 3847),
			new RSTile(3201, 3855) };
	public final RSTile[] walkToIsle = { new RSTile(3201, 3853),
			new RSTile(3202, 3847), new RSTile(3202, 3842),
			new RSTile(3202, 3837), new RSTile(3202, 3832),
			new RSTile(3201, 3827), new RSTile(3200, 3822),
			new RSTile(3200, 3817), new RSTile(3204, 3814),
			new RSTile(3207, 3810), new RSTile(3212, 3811),
			new RSTile(3217, 3813) };
	RSArea ge = new RSArea(new RSTile(3144, 3468), new RSTile(3185, 3516));
	RSArea redDragonIsle = new RSArea(new RSTile(3189, 3821), new RSTile(3205, 3843));
	RSArea teleGrabPlace = new RSArea(new RSTile(3211, 3790), new RSTile(3230, 3810));
	public final RSArea redDragonEntrance = new RSArea(new RSTile[] {
			new RSTile(3194, 3853), new RSTile(3208, 3853),
			new RSTile(3209, 3858), new RSTile(3209, 3864),
			new RSTile(3209, 3870), new RSTile(3207, 3875),
			new RSTile(3202, 3876), new RSTile(3197, 3875) });
	RSArea nearDitch = new RSArea(new RSTile(3127, 3498), new RSTile(3139, 3520));
	public final RSTile[] toIsle = { new RSTile(3201, 3860),
			new RSTile(3201, 3855), new RSTile(3201, 3849) };
	RSTile geShortCut[] = { new RSTile(3158, 3485), new RSTile(3153, 3492),
			new RSTile(3153, 3501), new RSTile(3148, 3507),
			new RSTile(3145, 3510), new RSTile(3145, 3512),
			new RSTile(3144, 3514) };

	RSTile ditch = new RSTile(3138, 3520);
	RSTile[] toLumb2 = { new RSTile(3219, 3219), new RSTile(3219, 3219),
			new RSTile(3215, 3215), new RSTile(3219, 3219),
			new RSTile(3215, 3215), new RSTile(3213, 3211),
			new RSTile(3219, 3219), new RSTile(3215, 3215),
			new RSTile(3213, 3211), new RSTile(3205, 3209) };
	RSTile toBerry1[] = { new RSTile(3201, 3849), new RSTile(3201, 3843),
			new RSTile(3201, 3837), new RSTile(3201, 3832),
			new RSTile(3201, 3826), new RSTile(3201, 3820),
			new RSTile(3196, 3812), new RSTile(3199, 3810),
			new RSTile(3206, 3810) };
	public final RSTile[] toFurther = { new RSTile(3200, 3856),
			new RSTile(3203, 3860), new RSTile(3208, 3861),
			new RSTile(3213, 3861), new RSTile(3218, 3860),
			new RSTile(3223, 3860), new RSTile(3228, 3860) };
	RSArea atBerries = new RSArea(new RSTile(3207, 3808), new RSTile(3221, 3820));
	RSArea afterShortcut = new RSArea(new RSTile(3135, 3516), new RSTile(3138, 3516));

	RSTile[] toCave = { new RSTile(3220, 3804), new RSTile(3219, 3800),
			new RSTile(3218, 3795), new RSTile(3217, 3792),
			new RSTile(3217, 3786), new RSTile(3214, 3783) };

	RSArea varrockArea = new RSArea(new RSTile(3160, 3420), new RSTile(3232, 3476));

	RSTile canoe = new RSTile(3133, 3510);

	public final RSTile[] toTeleGrab = { new RSTile(3147, 3800),
			new RSTile(3152, 3800), new RSTile(3157, 3800),
			new RSTile(3162, 3800), new RSTile(3167, 3798),
			new RSTile(3172, 3796), new RSTile(3177, 3794),
			new RSTile(3183, 3792), new RSTile(3188, 3794),
			new RSTile(3193, 3796), new RSTile(3198, 3797),
			new RSTile(3203, 3797), new RSTile(3208, 3797),
			new RSTile(3213, 3799), new RSTile(3218, 3800),
			new RSTile(3222, 3803) };

	RSArea lumbBank = new RSArea(new RSTile(3205, 3209), new RSTile(3212, 3225), 2);

	RSArea lumb2ndFloor = new RSArea(new RSTile(3205, 3209), new RSTile(3210, 3223), 1);

	RSArea lumbridgeArea = new RSArea(new RSTile(3203, 3202), new RSTile(3226, 3228), 0);

	// START: Code generated using Enfilade's Easel
	private final RenderingHints antialiasing = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

	private final Color color1 = new Color(0, 153, 102);

	private final Color color2 = new Color(0, 153, 153);

	private final Color color3 = new Color(0, 0, 0);

	private final Color color4 = new Color(255, 255, 0);

	private final BasicStroke stroke1 = new BasicStroke(3);

	private final Font font1 = new Font("Consolas", 2, 22);

	private final Font font2 = new Font("French Script MT", 0, 25);

	private final Image img1 = getImage("http://i51.tinypic.com/t9bd61.gif");

	// script variables needed
	public int maxPing = 150;

	public int maxPastWorlds = 25;

	public int currentWorld = 0;
	public int pastWorld[] = new int[maxPastWorlds];
	public int newWorld = 0;
	public int hopped = 0;

	public int skillTotal = -1; // the method used to count skillTotal returns

	// (totalSkills + 1)
	public boolean firstRun = true;
	public String os = System.getProperty("os.name").toLowerCase();

	public boolean atBerries() {
		return atBerries.contains(getMyPlayer().getLocation());
	}

	boolean atDitch() {
		return nearDitch.contains(getMyPlayer().getLocation());
	}

	// END: Code generated using Enfilade's Easel

	boolean atEntrance() {
		return redDragonEntrance.contains(getMyPlayer().getLocation());
	}

	boolean atGe() {
		return ge.contains(getMyPlayer().getLocation());
	}

	public boolean atIsle2() {
		return redDragonIsle.contains(getMyPlayer().getLocation());
	}

	void bank() {
	}

	void canoeChopping() {
		final RSObject station2 = objects.getNearest(12166);
		if (station2 != null && station2.isOnScreen()) {
			camera.turnTo(station2);
			station2.doAction("Chop-down");
			log("trying to chopdown");
		}
	}

	void canoeDestination() {
		final RSComponent travel = interfaces.get(53).getComponent(45);
		travel.doAction("Select");
		log("trying to press travel to deep");
		sleep(600, 1000);
	}

	void canoeFloating() {
		canoeSelected = true;
		status = "floating canoe";
		log("trying to float");
		final RSObject station2 = objects.getNearest(12166);
		if (station2 != null && station2.isOnScreen()) {
			station2.doAction("Float Canoe");
			sleep(650);
			canoeFloated = true;
			sleep(3000, 4000);
			step = 6;

		}
	}

	void canoePaddling() {
		canoeFloated = true;
		final RSObject station2 = objects.getNearest(12166);
		if (station2 != null && station2.isOnScreen()) {
			station2.doAction("Paddle Canoe");
			if (interfaces.get(53).getComponent(45).isValid()) {
				canoePaddled = true;
			}
		}
	}

	void canoeSelecting() {
		canoeShaped = true;
		if (interfaces.get(52).getComponent(24).containsText("A Waka")) {
			final RSComponent makeCanoe = interfaces.get(52).getComponent(24);
			makeCanoe.doAction("Select");
			log("WAKA WAKA");
			sleep(600, 1000);
			if (!interfaces.get(52).getComponent(24).isValid()) {
				canoeSelected = true;
			}

		}

	}

	void canoeShaping() {
		canoeChopped = true;
		final RSObject station2 = objects.getNearest(12166);
		if (station2 != null && station2.isOnScreen()) {
			station2.doAction("Shape-canoe");
			log("trying to shape");
			step = 4;
		}
	}

	void canoeToBerries() {

	}

	void canoeWalking() {
		walking.walkTo(canoe);
		firstTeleGrab = false;
		step = 2;
	}

	private Image getImage(final String url) {
		try {
			return ImageIO.read(new URL(url));
		} catch (final IOException e) {
			return null;
		}
	}

	private RSTile getNext(final RSTile[] path) {
		boolean found = false;
		for (int a = 0; a < path.length && !found; a++) {
			if (calc.tileOnMap(path[path.length - 1 - a])) {
				found = true;
				return path[path.length - 1 - a];
			}
		}
		return null;
	}

	boolean isOpen() {
		final RSObject closed = objects.getNearest(closedDeepGate);
		final RSObject open = objects.getNearest(openDeepGate);
		final RSObject closed2 = objects.getNearest(closedDeepGate);
		final RSObject open2 = objects.getNearest(openDeepGate);
		if (closed == null && closed2 == null && open2 != null || open != null) {
			return true;
		}
		return false;
	}

	private boolean isPastWorld(final int world) {
		for (final int element : pastWorld) {
			if (element == world) {
				return true;
			}
		}
		return false;
	}

	public int loop() {
		if (game.isLoggedIn()) {
			switch (states()) {

			case MAKE_CANOE:
				canoeWalking();
				sleep(3000, 4000);
				final RSObject station2 = objects.getNearest(12166);
				if (station2.isOnScreen() && station2 != null) {
					canoeChopping();
					sleep(4000, 5000);
					canoeChopped = true;
				}
				if (canoeChopped == true) {
					canoeShaping();
					sleep(4000, 6000);
					if (interfaces.get(52).getComponent(24).isValid()) {
						canoeShaped = true;
					}
				}
				if (interfaces.get(52).getComponent(24).isValid()
						&& canoeShaped == true) {
					canoeSelecting();
					sleep(7000, 8000);
				}
				if (canoeSelected == true) {
					canoeFloating();
					sleep(1500, 2000);
				}
				if (canoeFloated == true) {
					canoePaddling();
					sleep(1300, 2000);
				}
				if (interfaces.get(53).getComponent(45).isValid()) {
					canoeDestination();
					sleep(6000, 7000);
				}
				final RSComponent travel = interfaces.get(53).getComponent(45);
				if (interfaces.get(53).getComponent(45).isValid()) {
					travel.doAction("Select");
				}
				return 50;

			case WALK_TO_LUMB_BANK:
				walking.newTilePath(toLumb2);
				sleep(450, 700);
				final RSObject stairs = objects.getNearest(36773);
				if (stairs.isOnScreen() && stairs != null) {
					stairs.doAction("Climb-up");
				}
				if (game.getPlane() == 1) {
					final RSObject stairs2 = objects.getNearest(36774);
					stairs2.doAction("Climb-up");
					sleep(600, 700);
				}

			case TELEGRAB:
				if (firstTeleGrab == true) {
					walking.walkTo(teleGrabTile);
					firstTeleGrab = false;
				}
				// if (compassClicked == false){
				// mouse.click(542, 24, 5, 5, true);
				compassClicked = true;
				if (getMyPlayer().getAnimation() == -1) {
					if (!magic.isSpellSelected()) {
						final RSGroundItem berryGround = groundItems.getNearest(239);
						camera.turnTo(new RSTile(3216, 3806));
						magic.castSpell(Magic.SPELL_TELEKINETIC_GRAB);
						if (berryGround != null && berryGround.isOnScreen()) {
							berryGround.doAction("Cast");
							sleep(1300, 1600);
							spellsCasted++;
						}
					} else {
						final RSGroundItem berryGround2 = groundItems.getNearest(239);
						if (berryGround2 != null && berryGround2.isOnScreen()) {
							berryGround2.doAction("Cast");
							sleep(1000, 1300);
						}
					}
				}
				return 50;

			case WALK_TO_CAVE:
				walking.newTilePath(toCave).traverse();
				return 50;

				// case CANOE_BANK:

			case GO_TO_LAIR:
				final RSObject entrance = objects.getNearest(38815);
				if (entrance != null && entrance.isOnScreen()) {
					entrance.doAction("Go-through");
					sleep(600, 1000);
				}
				return 50;
			case TELEPORT:
				magic.castSpell(Magic.SPELL_VARROCK_TELEPORT);
				log("teleported to varrock");
				sleep(5000, 6000);
				return 50;

			case BACK_TO_ENTRANCE:
				status = "Walking to entrance.";
				walking.newTilePath(backToEntrance).traverse();
				return 50;

			case WALK_TO_BANK:
				walking.newTilePath(backToEntrance).traverse();
				return 50;

			case WORLDHOP:
				worldHop(true);
				return 50;
			case BANK:
				status = "Banking";
				walking.walkTo(new RSTile(3164, 3487));
				sleep(1000, 2000);
				bank.open();
				sleep(300, 600);
				if (bank.isOpen()) {
					bank.depositAllExcept(hatchet, 554, 553);
					berriesPicked = berriesPicked + 25;
					bank.withdraw(554, 1);
					sleep(300, 600);
					bank.withdraw(563, 31);
					if (inventory.contains(554) && inventory.contains(563)
							&& inventory.contains(hatchet)) {
						bank.close();
						needsToBank = false;
					}
				}

			case LUMB_BANK:
				canoeFloated = false;
				canoeChopped = false;
				canoeSelected = false;
				canoePaddled = false;
				canoeShaped = false;

				status = "Banking";

				if (needsToBank) {
					walking.walkTo(new RSTile(3208, 3220));
					sleep(500, 700);
					bank.open();
					sleep(300, 600);
					if (bank.isOpen()) {
						bank.depositAllExcept(hatchet, 554, 553, 1381);
						bank.withdraw(554, 1);
						sleep(300, 600);
						bank.withdraw(563, 31);
						if (inventory.contains(554) && inventory.contains(563)
								&& inventory.contains(hatchet)) {
							bank.close();
							needsToBank = false;
							isDead = false;
							final RSItem airstaff = inventory.getItem(1381);
							sleep(600, 900);
							if (lumbBanked == false && inventory.contains(1381)) {
								airstaff.doAction("Wield");
								airstaff.doAction("Wield");
								airstaff.doAction("Wield");
								final RSItem wielding = equipment.getItem(Equipment.WEAPON);
								log("we are wielding " + wielding);
								lumbBanked = true;
								game.openTab(Game.TAB_EQUIPMENT);
								if (equipment.getItem(Equipment.WEAPON) == airstaff) {
									lumbBanked = true;
								}
							} else {
								final RSItem airstaff2 = inventory.getItem(1381);
								game.openTab(Game.TAB_EQUIPMENT);
								sleep(1000, 1500);
								final RSItem wielding4 = equipment.getItem(Equipment.WEAPON);
								log("we are wielding " + wielding4);
								if (equipment.getItem(Equipment.WEAPON) == airstaff2) {
									final RSItem wielding2 = equipment.getItem(Equipment.WEAPON);
									log("we are wielding " + wielding2);
									lumbBanked = true;
								} else {
									final RSItem wielding3 = equipment.getItem(Equipment.WEAPON);
									log("we are wielding " + wielding3);
									log("no airstaff in inv or worn, shutting down, please have airstaff in inv OR woren");
									stopScript();
								}
							}
						}
					}
				}
				return 50;

			case WALK_TO_SHORTCUT:
				status = "Walking to ge shortcut";
				walking.newTilePath(geShortCut).traverse();
				sleep(700, 1200);
				final RSObject shortcut = objects.getNearest(geShortcut);
				if (shortcut != null && shortcut.isOnScreen()) {
					camera.turnTo(shortcut);
					shortcut.doAction("Climb-into");
					sleep(4500, 5000);
				}
				return 50;

			case WALK_TO_GE:
				walkPath(toGe);
				sleep(800, 900);
				return 50;

			case WALK_TO_TELEGRAB:
				compassClicked = false;
				walking.newTilePath(toTeleGrab).traverse();
				sleep(700, 900);
				return 50;

			case WALK_TO_TELEGRAB_TILE:
				compassClicked = false;
				walking.walkTo(teleGrabTile);
				sleep(700, 1200);
				sleep(1000, 1300);
				return 50;

			case SLEEP:
				status = "ERROR";
				log("Problem? Well, we do..");
			}
		}
		return 1;
	}

	public void messageReceived(final MessageEvent e) {
		final String message = e.getMessage();
		if (message.contains("You don't have enough")
				|| message.contains("You do not have enough")) {
			needsToBank = true;
			log("inv full, banking");
		}
		if (message.contains("Oh dear, you are dead")) {
			isDead = true;
			needsToBank = true;
		}
		if (message.contains("Too late")) {
			spellsCasted--;
		}
	}

	public void onRepaint(final Graphics g1) {
		final Graphics2D g = (Graphics2D) g1;
		millis = System.currentTimeMillis() - startTime;
		hours = millis / (1000 * 60 * 60);
		millis -= hours * 1000 * 60 * 60;
		minutes = millis / (1000 * 60);
		millis -= minutes * 1000 * 60;
		seconds = millis / 1000;
		moneyPerHour = (int) (berryPrice * berriesPicked * 3600000D / (System.currentTimeMillis() - startTime));
		g.setRenderingHints(antialiasing);

		g.setColor(color1);
		g.fillRoundRect(5, 344, 507, 131, 16, 16);
		g.setColor(color2);
		g.setStroke(stroke1);
		g.drawRoundRect(5, 344, 507, 131, 16, 16);
		g.setFont(font1);
		g.setColor(color3);
		g.drawString("Kubko's WhiteBerries", 5, 367);
		g.setFont(font2);
		g.setColor(color4);
		g.drawString("Time running: " + +hours + ":" + minutes + ":" + seconds
				+ ", hopped " + hopped + " times", 6, 397);
		g.drawString("Profit:" + berriesPicked * berryPrice, 5, 421);
		g.drawString("Profit per hour:" + moneyPerHour + " gp", 5, 451);
		g.drawImage(img1, 407, 356, null);
		g.drawString("State:" + status, 226, 454);
	}

	public boolean onStart() {
		log("Welcome to KubkoWhiteBerries v. " + version);
		startTime = System.currentTimeMillis();
		berry = grandExchange.lookup(whiteBerry);
		berryPrice = berry.getGuidePrice();
		log("Berry price is " + berryPrice + "gp");
		status = "Starting up";
		return true;
	}

	private int pingHost(final String host, final int timeout) {
		long start = -1;
		long end = -1;
		int total = -1;
		final int defaultPort = 80;
		final Socket theSock = new Socket();
		try {
			final InetAddress addr = InetAddress.getByName(host);
			final SocketAddress sockaddr = new InetSocketAddress(addr, defaultPort);
			start = System.currentTimeMillis();
			theSock.connect(sockaddr, timeout);
			end = System.currentTimeMillis();
		} catch (final Exception e) {
			start = -1;
			end = -1;
		} finally {
			if (theSock != null) {
				try {
					theSock.close();
				} catch (final IOException e) {
				}
				if (start != -1 && end != -1) {
					total = (int) (end - start);
					log(host + "'s ping delay is " + total + "ms.");
				} else {
					log("Connection timed out or unable to connect to host: "
							+ host);
				}
			}
		}
		return total; // returns -1 if timeout
	}

	private STATE states() {
		if (mode == 2) {
			if (game.getPlane() == 2 && lumbBanked == true
					&& needsToBank == false && worldHopped == false) {
				log("Wworldhopping. thanks to jtryba for world hop method");
				return STATE.WORLDHOP;
			}

			if (isDead == true && !ge.contains(getMyPlayer().getLocation())
					&& lumbBank.contains(getMyPlayer().getLocation())
					&& lumbBanked == false && game.getPlane() >= 2) {
				status = "banking i lumbriddge";
				return STATE.LUMB_BANK;
			}

			if (isDead == true
					&& lumbridgeArea.contains(getMyPlayer().getLocation())
					|| lumb2ndFloor.contains(getMyPlayer().getLocation())
					&& game.getPlane() <= 1) {
				plane = game.getPlane();
				status = "Walking to lumb bank";
				log("our plane is: " + plane);
				if (game.getPlane() == 2) {
					log("WTF IS THIS FAIL BOT DOING, PLANE 3 MEANS WE SHOULD BE BANKING -.-");
				}
				return STATE.WALK_TO_LUMB_BANK;
			}

			if (teleGrabTileArea.contains(getMyPlayer().getLocation())
					&& !needsToBank) {
				status = "Telegrab";
				return STATE.TELEGRAB;
			}
			if (!needsToBank
					&& !redDragonIsle.contains(getMyPlayer().getLocation())
					&& inventory.contains(hatchet)
					&& ge.contains(getMyPlayer().getLocation())
					&& !nearDitch.contains(getMyPlayer().getLocation())
					&& !atEntrance() && inventory.contains(lawRune)
					&& inventory.contains(fireRune)) {
				status = "Walking to ShortCut";
				return STATE.WALK_TO_SHORTCUT;
			}
			if (teleGrabPlace.contains(getMyPlayer().getLocation())
					&& needsToBank) {
				status = "Walking to cave";
				return STATE.WALK_TO_CAVE;

			}
			if (nearDitch.contains(getMyPlayer().getLocation()) && !needsToBank
					&& !atGe()
					&& !redDragonIsle.contains(getMyPlayer().getLocation())
					&& !atEntrance()) {
				status = "Making canoe";
				return STATE.MAKE_CANOE;
			}
			if (varrockArea.contains(getMyPlayer().getLocation())) {
				status = "Walking to ge.";
				return STATE.WALK_TO_GE;
			}

			if (lair.contains(getMyPlayer().getLocation()) && needsToBank
					|| worldHopped == true) {
				status = "teleporting to varrock";
				return STATE.TELEPORT;
			}
			if (entrance.contains(getMyPlayer().getLocation()) && needsToBank) {
				status = "going to lair.";
				return STATE.GO_TO_LAIR;
			}

			if (ge.contains(getMyPlayer().getLocation()) && needsToBank == true
					&& !lumbBank.contains(getMyPlayer().getLocation())
					&& needsToBank == true || spellsCasted == 30
					&& ge.contains(getMyPlayer().getLocation())) {
				status = "banking";
				return STATE.BANK;
			}

			if (!ge.contains(getMyPlayer().getLocation())
					&& !lair.contains(getMyPlayer().getLocation())
					&& !nearDitch.contains(getMyPlayer().getLocation())
					&& !entrance.contains(getMyPlayer().getLocation())
					&& !varrockArea.contains(getMyPlayer().getLocation())
					&& !teleGrabPlace.contains(getMyPlayer().getLocation())
					&& !needsToBank
					&& !lumbBank.contains(getMyPlayer().getLocation())
					&& !lumbridgeArea.contains(getMyPlayer().getLocation())) {
				status = "walking to telegrab place";
				return STATE.WALK_TO_TELEGRAB;
			}
		}
		return STATE.SLEEP;
	}

	void teleToBerries() {

	}

	private boolean walkPath(final RSTile path[]) {
		return walkPath(path, false);
	}

	/**
	 * Checks wether the door is open or closed.
	 * 
	 * @param tile
	 *            - any tile after the door tile, you can also use getEnd()
	 *            method.
	 * @return true if the door is open, otherwise false.
	 */

	private boolean walkPath(final RSTile[] path, final boolean reverse) {
		if (reverse) {
			for (int i = 0; i < path.length; i++) {
				path[i] = path[path.length - i - 1];
			}
		}
		if (calc.distanceTo(path[path.length - 1]) > 4) {
			final RSTile n = getNext(path);
			if (n != null) {
				walking.walkTileMM(n.randomize(2, 2));
				if (random(1, 6) != 2) {
					mouse.moveRandomly(20);
				}
			}
		}
		return false;
	}

	/**
	 * Hops to the first visible filtered world on the list by jtryba avoids the
	 * last x worlds you've been to since starting the script credits to MrByte
	 * for his help
	 * 
	 * @param members
	 *            <tt>true</tt> if player should hop to a members world.
	 * @return void
	 * 
	 */

	public void worldHop(final boolean members) {
		worldHop(members, 150, 2000);
	}

	public void worldHop(final boolean members, final int maxping) {
		worldHop(members, maxping, 2000);
	}

	public void worldHop(final boolean members, final int maxping,
			final int maxpop) {

		final int LOBBY_PARENT = 906;
		final int WORLD_SELECT_TAB_PARENT = 910;
		final int WORLD_SELECT_COM = 77;
		final int WORLD_NUMBER_COM = 69;
		final int WORLD_POPULATION_COM = 71;
		final int WORLD_TYPE_COM = 74;
		final int WORLD_SELECT_BUTTON_COM = 188;
		final int WORLD_SELECT_BUTTON_BG_COM = 12;
		final int WORLD_SELECT_TAB_ACTIVE = 4671;
		final int WORLD_FULL_BACK_BUTTON_COM = 233;
		final int CURRENT_WORLD_COM = 11;
		final int SORT_POPULATION_BUTTON_PARENT = 30;
		final int SORT_PING_BUTTON_PARENT = 45;
		final int SORT_LOOTSHARE_BUTTON_PARENT = 47;
		final int SORT_TYPE_BUTTON_PARENT = 49;
		final int SORT_ACTIVITY_BUTTON_PARENT = 52;
		final int SORT_WORLD_BUTTON_PARENT = 55;
		final int SCROLL_BAR_PARENT = 86;
		final int HIGH_RISK_WARN_PARENT = 93;
		final int PLAY_BUTTON_COM = 171;
		final int RETURN_TEXT_COM = 221;
		final int SUBSCRIBE_BACK_BUTTON_COM = 233;
		final int CONNECT_ERROR_BACK_BUTTON_COM = 42;
		final int SKILL_WORLD_BACK_BUTTON_COM = 228;
		final int SKILL_WORLD_NUMBER = 113;

		log("Hopping worlds");
		status = "Hopping worlds";

		// logout
		while (game.isLoggedIn()) {
			if (game.logout(true)) {
				while (game.getClientState() != 7) {
					sleep(random(200, 500));
				}
			}
		}

		status = "Selecting new world";
		sleep(random(1500, 2000));

		// click world select tab (if needed)
		final RSComponent worldSelectTab = interfaces.getComponent(LOBBY_PARENT, WORLD_SELECT_BUTTON_BG_COM);
		while (worldSelectTab.getBackgroundColor() != WORLD_SELECT_TAB_ACTIVE) {
			if (interfaces.getComponent(LOBBY_PARENT, WORLD_SELECT_BUTTON_COM).doClick()) {
				sleep(random(1500, 2000));
			}
		}

		// get current world
		final String cW = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, CURRENT_WORLD_COM).getText();
		final String[] cWS = cW.split(" ");
		currentWorld = Integer.parseInt(cWS[1]);
		newWorld = currentWorld;

		// randomly sort worlds
		if (random(0, 10) < 5) {
			RSComponent com = null;
			switch (random(0, 6)) {
			case 0: // population
				com = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, SORT_POPULATION_BUTTON_PARENT).getComponent(random(0, 2));
				break;
			case 1: // ping
				com = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, SORT_PING_BUTTON_PARENT).getComponent(random(0, 2));
				break;
			case 2: // loot share
				com = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, SORT_LOOTSHARE_BUTTON_PARENT).getComponent(random(0, 2));
				break;
			case 3: // type
				com = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, SORT_TYPE_BUTTON_PARENT).getComponent(random(0, 2));
				break;
			case 4: // activity
				com = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, SORT_ACTIVITY_BUTTON_PARENT).getComponent(random(0, 2));
				break;
			case 5: // world
				com = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, SORT_WORLD_BUTTON_PARENT).getComponent(random(0, 2));
				break;
			}
			if (com.doClick()) {
				sleep(random(1250, 1500));
			}
		}

		// check if we should sort by ptp/ftp
		boolean sort = false;
		final RSComponent[] wt = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, WORLD_TYPE_COM).getComponents();
		for (int i = 0; i < 10; i++) {
			if (members && !wt[i].getText().contains("Members") || !members
					&& wt[i].getText().contains("Members")) {
				sort = true;
				break;
			}
		}

		// sort by ptp/ftp (if needed)
		if (sort) {
			status = "Sorting worlds by type";
			if (interfaces.getComponent(WORLD_SELECT_TAB_PARENT, SORT_TYPE_BUTTON_PARENT).getComponent(members ? 0
					: 1).doClick()) {
				sleep(random(1000, 1500));
			}
		}

		/*-PICK-NEXT-WORLD-*/
		while (currentWorld == newWorld) {
			RSComponent worldToHop = null;
			RSComponent[] comWorldNumber = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, WORLD_NUMBER_COM).getComponents();
			int world = 0;
			int pop = 2000;
			int ping = -1;
			boolean member = false;
			while (worldToHop == null) {
				for (int i = 0; i < comWorldNumber.length - 1; i++) {
					final RSComponent[] comWorldSelect = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, WORLD_SELECT_COM).getComponents();
					final RSComponent[] comWorldPopulation = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, WORLD_POPULATION_COM).getComponents();
					final RSComponent[] comWorldType = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, WORLD_TYPE_COM).getComponents();
					comWorldNumber = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, WORLD_NUMBER_COM).getComponents();
					try {
						world = Integer.parseInt(comWorldNumber[i].getText());
					} catch (final NumberFormatException nfe) {
						world = 0;
					} catch (final ArrayIndexOutOfBoundsException aie) {
						world = 0;
					}
					if (world == SKILL_WORLD_NUMBER && skillTotal < 1000) {
						world = 0;
					}
					try {
						pop = Integer.parseInt(comWorldPopulation[i].getText());
					} catch (final NumberFormatException nfe) {
						pop = 2000;
					} catch (final ArrayIndexOutOfBoundsException aie) {
						pop = 2000;
					}
					try {
						if (comWorldType[i].getText().contains("Members")) {
							member = true;
						}
					} catch (final ArrayIndexOutOfBoundsException aie) {
					}
					if (world != 0 && !isPastWorld(world)
							&& world != currentWorld && pop < maxpop && pop > 0
							&& member == members) {
						ping = pingHost("world" + world + ".runescape.com", maxPing + 50);
					} else {
						ping = -1;
					}
					if (ping < maxping && ping > 0) {
						log("World " + world + " selected.");
						worldToHop = comWorldSelect[i];
					}
					if (worldToHop != null) {
						break;
					}
				}
				sleep(random(50, 150));
			}
			if (worldToHop != null) {
				int w = -1;
				String[] split = interfaces.getComponent(910, 8).getText().split(" ");
				try {
					w = Integer.parseInt(split[split.length - 1]);
				} catch (final Exception e) {
					w = -1;
				}
				// click new world
				while (w != world) {
					if (worldToHop.getLocation().y <= 280
							&& worldToHop.getLocation().y >= 0) {
						status = "Clicking new world";
						worldToHop.doClick();
					} else {
						final RSComponent scrollBar = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, SCROLL_BAR_PARENT);
						status = "Scrolling to new world";
						if (interfaces.scrollTo(worldToHop, scrollBar)) {
							status = "Clicking new world";
							worldToHop.doClick();
						}
					}
					split = interfaces.getComponent(910, 8).getText().split(" ");
					try {
						w = Integer.parseInt(split[split.length - 1]);
					} catch (final Exception e) {
						w = -1;
					}
				}
				// get new world
				sleep(random(1000, 1500));
				final String cW2 = interfaces.getComponent(WORLD_SELECT_TAB_PARENT, CURRENT_WORLD_COM).getText();
				final String[] cWS2 = cW2.split(" ");
				newWorld = Integer.parseInt(cWS2[1]);
			}
		}
		/*-END-PICK-NEXT-WORLD-*/

		// set last & current world/s
		if (currentWorld != newWorld) {
			for (int i = 0; i < pastWorld.length; i++) {
				if (i < pastWorld.length - 1) {
					pastWorld[i] = pastWorld[i + 1];
				} else {
					pastWorld[i] = currentWorld;
				}
			}
			currentWorld = newWorld;
		}

		while (game.getClientState() != 10) {
			status = "Logging in...";
			// click play button
			if (interfaces.getComponent(LOBBY_PARENT, PLAY_BUTTON_COM).doClick()) {
				hopped++;
				worldHopped = true;
			}

			// check for high risk world warning during login
			final long timeOut = System.currentTimeMillis() + 35000;
			while (game.getClientState() != 10
					&& System.currentTimeMillis() < timeOut) {
				final RSComponent hrParent = interfaces.getComponent(LOBBY_PARENT, HIGH_RISK_WARN_PARENT);
				if (hrParent.isValid()) {
					final RSComponent LogIn = hrParent.getComponent(random(0, hrParent.getComponents().length));
					if (LogIn != null && LogIn.isValid()) {
						if (mouse.getLocation().getX() < 386
								|| mouse.getLocation().getX() > 504
								|| mouse.getLocation().getY() < 357
								|| mouse.getLocation().getY() > 386) {
							if (LogIn.doHover()) {
								sleep(random(250, 500));
								if (menu.contains("Log In")) {
									// accept warning / click login
									mouse.click(true);
									log.warning("This is a high risk wilderness world.");
									sleep(random(250, 500));
								}
							}
						}
					}
				}
				sleep(100);
			}
			// check for login errors
			final String returnText = interfaces.getComponent(LOBBY_PARENT, RETURN_TEXT_COM).getText().toLowerCase();
			if (!game.isLoggedIn()) {
				if (returnText.contains("update")) {
					status = "Stopping script";
					log("Runescape has been updated, please reload RSBot.");
					stopScript(false);
				}
				if (returnText.contains("disable")) {
					status = "Stopping script";
					log("Your account is banned/disabled.");
					stopScript(false);
				}
				if (returnText.contains("error connecting")) {
					status = "Stopping script";
					log("Can't connect to runescape..");
					interfaces.getComponent(LOBBY_PARENT, CONNECT_ERROR_BACK_BUTTON_COM).doClick();
					stopScript(false);
				}
				if (returnText.contains("full")) {
					log("World Is Full.");
					interfaces.getComponent(LOBBY_PARENT, WORLD_FULL_BACK_BUTTON_COM).doClick();
					sleep(random(1000, 1500));
					worldHop(members, maxping, maxpop); // try again
				}
				if (returnText.contains("subscribe")) {
					interfaces.getComponent(LOBBY_PARENT, SUBSCRIBE_BACK_BUTTON_COM).doClick();
					if (members) {
						status = "Stopping script";
						log("You neen to be a member to select this world.");
						stopScript(false);
					} else {
						sleep(random(500, 1000));
						worldHop(members, maxping, maxpop); // try again
					}
				}
				if (returnText.contains("must have a total")) {
					interfaces.getComponent(LOBBY_PARENT, SKILL_WORLD_BACK_BUTTON_COM).doClick();
					sleep(random(500, 1000));
					worldHop(members, maxping, maxpop); // try again
				}
			}
		}
	}
}
