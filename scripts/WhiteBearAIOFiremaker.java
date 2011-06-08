/**
 * @author White Bear
 * @copyright (C)2010-2011 White Bear
 * 			No one except White Bear has the right to modify this script!
 */

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.rsbot.Configuration;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Bank;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.GrandExchange.GEItem;
import org.rsbot.script.methods.Players;
import org.rsbot.script.methods.Skills;
import org.rsbot.script.methods.Walking;
import org.rsbot.script.wrappers.RSComponent;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "WhiteBear" }, keywords = "Burner Firemaker Logs Global Universal", name = "White Bear AIO Firemaker", version = 1.13, description = "Fast and Flawless All-in-One Firemaker", website = "http://whitebearrs.orgfree.com")
public class WhiteBearAIOFiremaker extends Script implements PaintListener,
		MessageListener, MouseListener, MouseMotionListener {

	// ---------------ANTIBAN--------------\\
	private class Antiban {
		int moveMouseB = 75;
		int allRand = 21, cam = 27, skill = 45, player = 18, friend = 61;
		boolean checkFriend = false, checkExperience = true,
				screenLookaway = false;

		// Antiban timeouts and next times
		long timeOutB1 = 50000, timeOutB2 = 100000;
		long timeFriend = System.currentTimeMillis(),
				timeExp = System.currentTimeMillis();
		long timeLook = System.currentTimeMillis();
		long timeOutFriend = 20000, timeOutExp = 20000;

		private boolean breakingCheck() {
			if (nextBreak <= System.currentTimeMillis()) {
				return true;
			}
			return false;
		}

		private void breakingNew() {
			if (randomBreaking) {
				final long varTime = random(7200000, 18000000);
				nextBreak = System.currentTimeMillis() + varTime;
				final long varLength = random(120000, 600000);
				nextLength = varLength;
			} else {
				final int diff = randTime * 1000 * 60;
				final long varTime = random(midTime * 1000 * 60 - diff, midTime
						* 1000 * 60 + diff);
				nextBreak = System.currentTimeMillis() + varTime;
				final int diff2 = randLength * 1000 * 60;
				final long varLength = random(midLength * 1000 * 60 - diff2, midLength
						* 1000 * 60 + diff2);
				nextLength = varLength;
			}
		}

		private RSPlayer getNearbyMod() {
			final RSPlayer[] modCheck = players.getAll();
			int Dist = 18;
			RSPlayer closest = null;
			int element = 0;
			final int size = modCheck.length;
			while (element < size) {
				if (modCheck[element] != null) {
					try {
						if (modCheck[element].getName().startsWith("Mod")) {
							final int distance = calc.distanceTo(modCheck[element]);
							if (distance < Dist) {
								Dist = distance;
								closest = modCheck[element];
							}
						}
					} catch (final Exception ignored) {
					}
				}
				element += 1;
			}
			return closest;
		}

		private boolean load() {
			try {
				WBini.load(new FileInputStream(new File(Configuration.Paths.getScriptCacheDirectory(), "WhiteBearAIOFiremaker.ini")));
			} catch (final java.lang.Exception e) {
				log.severe("[ERROR] Could not load settings file!");
				return false;
			}
			if (WBini.getProperty("ABallRand") != null) {
				allRand = Integer.parseInt(WBini.getProperty("ABallRand"));
			}
			if (WBini.getProperty("ABcam") != null) {
				cam = Integer.parseInt(WBini.getProperty("ABcam"));
			}
			if (WBini.getProperty("ABskill") != null) {
				skill = Integer.parseInt(WBini.getProperty("ABskill"));
			}
			if (WBini.getProperty("ABplayer") != null) {
				player = Integer.parseInt(WBini.getProperty("ABplayer"));
			}
			if (WBini.getProperty("ABfriend") != null) {
				friend = Integer.parseInt(WBini.getProperty("ABfriend"));
			}

			if (WBini.getProperty("ABtimeOutFriend") != null) {
				timeOutFriend = Integer.parseInt(WBini.getProperty("ABtimeOutFriend"));
			}
			if (WBini.getProperty("ABtimeOutExp") != null) {
				timeOutExp = Integer.parseInt(WBini.getProperty("ABtimeOutExp"));
			}
			if (WBini.getProperty("ABmoveMouseB") != null) {
				moveMouseB = Integer.parseInt(WBini.getProperty("ABmoveMouseB"));
			}
			return true;
		}

		private boolean lookAway() {
			if (!chatRes.typing && screenLookaway
					&& timeLook < System.currentTimeMillis()
					&& random(0, 111) == 0) {
				chatRes.pause = true;
				status = "Look Away";
				if (random(0, moveMouseB) <= 50) {
					mouse.setSpeed(random(3, 5));
					mouse.move(random(40, game.getWidth() - 50), game.getHeight());
					mouse.setSpeed(random(minMS, maxMS));
				}
				final int r1 = random(0, 101);
				if (getMyPlayer().isMoving()) {
					int m = 0;
					while (valid() && m < 31) {
						m++;
						sleep(50);
						if (r1 < 41 && m > 13) {
							break;
						}
						if (!getMyPlayer().isMoving()) {
							break;
						}
					}
					timeLook = (long) (System.currentTimeMillis() + random(timeOutB1, timeOutB2));
				}
				chatRes.pause = false;
				return true;
			}
			return false;
		}

		private void main(final boolean extras) {
			mouse.setSpeed(random(minMS + 1, maxMS + 1));
			if (!chatRes.typing) {
				if (nextRun < System.currentTimeMillis()
						&& walking.getEnergy() >= random(79, 90)) {
					nextRun = System.currentTimeMillis() + 7000;
					walking.setRun(true);
					sleep(100);
				}
				final int random = random(1, allRand);
				if (random == 1) {
					if (random(1, 3) == 1) {
						chatRes.wait = true;
						mouse.move(random(5, game.getWidth()), random(5, game.getHeight()));
						chatRes.wait = false;
					}
				}
				if (random == 2) {
					final int randCamera = random(1, cam);
					if (randCamera <= 4) {
						camTurned += 1;
						chatRes.wait = true;
						turnCamera();
						chatRes.wait = false;
					}
				}
				if (checkExperience && random == 6) {
					if (System.currentTimeMillis() > timeExp
							&& random(1, skill) == 1
							&& getMyPlayer().getAnimation() != -1) {
						if (game.getCurrentTab() != 1) {
							chatRes.wait = true;
							game.openTab(1);
							final Point stats = new Point(interfaces.get(320).getComponent(3).getAbsoluteX() + 20, interfaces.get(320).getComponent(3).getAbsoluteY() + 10);
							mouse.move(stats, 5, 5);
							sleepCR(random(28, 31));
							timeExp = System.currentTimeMillis()
									+ (long) random(timeOutExp - 1500, timeOutExp + 1500);
							chatRes.wait = false;
						}
					}
				}
				if (random == 7) {
					if (random(0, 2) == 0) {
						if (checkFriend
								&& System.currentTimeMillis() > timeFriend
								&& random(1, friend) == 1) {
							if (getMyPlayer().getAnimation() != -1
									|| getMyPlayer().isMoving()
									&& calc.distanceTo(walking.getDestination()) > 5) {
								chatRes.wait = true;
								game.openTab(9);
								sleepCR(random(18, 25));
								timeFriend = System.currentTimeMillis()
										+ (long) random(timeOutFriend - 1500, timeOutFriend + 1500);
								chatRes.wait = false;
							}
						}
					}
				}
				if (random == 8) {
					if (extras == true) {
						final int chance2 = random(1, player);
						if (chance2 == 1) {
							final RSPlayer player = players.getNearest(Players.ALL_FILTER);
							if (player != null && calc.distanceTo(player) != 0) {
								chatRes.wait = true;
								mouse.move(player.getScreenLocation(), 5, 5);
								sleepCR(random(6, 9));
								mouse.click(false);
								sleepCR(random(15, 17));
								mouse.move(random(10, 450), random(10, 495));
								chatRes.wait = false;
							}
						}
					}
				}
			}
			mouse.setSpeed(random(minMS, maxMS));
		}

		private boolean personalize() {
			try {
				WBini.load(new FileInputStream(new File(Configuration.Paths.getScriptCacheDirectory(), "WhiteBearAIOFiremaker.ini")));
			} catch (final java.lang.Exception e) {
			}
			if (WBini.getProperty("ABallRand") == null) {
				WBini.setProperty("ABallRand", Integer.toString(random(20, 23)));
			}
			if (WBini.getProperty("ABcam") == null) {
				WBini.setProperty("ABcam", Integer.toString(random(26, 29)));
			}
			if (WBini.getProperty("ABskill") == null) {
				WBini.setProperty("ABskill", Integer.toString(random(44, 49)));
			}
			if (WBini.getProperty("ABplayer") == null) {
				WBini.setProperty("ABplayer", Integer.toString(random(19, 23)));
			}
			if (WBini.getProperty("ABfriend") == null) {
				WBini.setProperty("ABfriend", Integer.toString(random(59, 69)));
			}

			if (WBini.getProperty("ABtimeOutFriend") == null) {
				WBini.setProperty("ABtimeOutFriend", Integer.toString(random(33000, 60000)));
			}
			if (WBini.getProperty("ABtimeOutExp") == null) {
				WBini.setProperty("ABtimeOutExp", Integer.toString(random(25000, 50000)));
			}

			if (WBini.getProperty("ABmoveMouseB") == null) {
				WBini.setProperty("ABmoveMouseB", Integer.toString(random(55, 105)));
			}
			try {
				WBini.store(new FileWriter(new File(Configuration.Paths.getScriptCacheDirectory(), "WhiteBearAIOFiremaker.ini")), "The GUI Settings for White Bear AIO Firemaker (Version: "
						+ Double.toString(properties.version()) + ")");
			} catch (final java.lang.Exception e) {
				log.severe("[ERROR] Could not save settings file!");
				return true;
			}
			boolean load = antiban.load();
			while (!load) {
				load = antiban.load();
			}
			return true;
		}

		private boolean sleepCR(final int amtOfHalfSecs) {
			for (int x = 0; x < amtOfHalfSecs + 1; x++) {
				sleep(random(48, 53));
				if (chatRes.typing) {
					return false;
				}
			}
			return true;
		}

		private void turnCamera() {
			final char[] LR = new char[] { KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT };
			final char[] UD = new char[] { KeyEvent.VK_DOWN, KeyEvent.VK_UP };
			final char[] LRUD = new char[] { KeyEvent.VK_LEFT,
					KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_UP };
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
	}

	private class Area {
		int minX, minY, maxX, maxY;

		public Area(final int minX, final int minY, final int maxX,
				final int maxY) {
			this.minX = minX;
			this.minY = minY;
			this.maxX = maxX;
			this.maxY = maxY;
		}

		public boolean inArea(final RSTile t) {
			if (t.getX() >= minX && t.getX() <= maxX && t.getY() >= minY
					&& t.getY() <= maxY) {
				return true;
			}
			return false;
		}
	}

	// --------------CLASSES---------------\\
	private static class AStar {
		private static class Node {
			public int x, y;
			public Node prev;
			public double g, f;

			public Node(final int x, final int y) {
				this.x = x;
				this.y = y;
				g = f = 0;
			}

			public boolean equals(final Object o) {
				if (o instanceof Node) {
					final Node n = (Node) o;
					return x == n.x && y == n.y;
				}
				return false;
			}

			public int hashCode() {
				return x << 4 | y;
			}

			public RSTile toRSTile(final int baseX, final int baseY) {
				return new RSTile(x + baseX, y + baseY);
			}

			public String toString() {
				return "(" + x + "," + y + ")";
			}
		}

		public static final int WALL_NORTH_WEST = 0x1;
		public static final int WALL_NORTH = 0x2;
		public static final int WALL_NORTH_EAST = 0x4;
		public static final int WALL_EAST = 0x8;
		public static final int WALL_SOUTH_EAST = 0x10;
		public static final int WALL_SOUTH = 0x20;
		public static final int WALL_SOUTH_WEST = 0x40;
		public static final int WALL_WEST = 0x80;

		public static final int BLOCKED = 0x100;

		private Walking walking;
		private Game game;

		private int[][] flags;
		private int off_x, off_y;

		public boolean canMoveEast(final RSTile tile) {
			final int base_x = game.getBaseX(), base_y = game.getBaseY();
			final int x = tile.getX() - base_x, y = tile.getY() - base_y;
			final RSTile off = walking.getCollisionOffset(game.getPlane());
			final int flags = walking.getCollisionFlags(game.getPlane())[x
					- off.getX()][y - off.getY()];
			return (flags & WALL_EAST) == 0;
		}

		private double dist(final Node start, final Node end) {
			if (start.x != end.x && start.y != end.y) {
				return 1.41421356;
			} else {
				return 1.0;
			}
		}

		public RSTile[] findPath(final RSTile start, final RSTile end) {
			final int base_x = game.getBaseX(), base_y = game.getBaseY();
			final int curr_x = start.getX() - base_x, curr_y = start.getY()
					- base_y;
			final int dest_x = end.getX() - base_x, dest_y = end.getY()
					- base_y;

			// load client data
			flags = walking.getCollisionFlags(game.getPlane());
			final RSTile offset = walking.getCollisionOffset(game.getPlane());
			off_x = offset.getX();
			off_y = offset.getY();

			// loaded region only
			if (flags == null || curr_x < 0 || curr_y < 0
					|| curr_x >= flags.length || curr_y >= flags.length
					|| dest_x < 0 || dest_y < 0 || dest_x >= flags.length
					|| dest_y >= flags.length) {
				return null;
			}

			// structs
			final HashSet<Node> open = new HashSet<Node>();
			final HashSet<Node> closed = new HashSet<Node>();
			Node curr = new Node(curr_x, curr_y);
			final Node dest = new Node(dest_x, dest_y);

			curr.f = heuristic(curr, dest);
			open.add(curr);

			// search
			while (!open.isEmpty()) {
				curr = lowest_f(open);
				if (curr.equals(dest)) {
					// reconstruct from pred tree
					return path(curr, base_x, base_y);
				}
				open.remove(curr);
				closed.add(curr);
				for (final Node next : successors(curr)) {
					if (!closed.contains(next)) {
						final double t = curr.g + dist(curr, next);
						boolean use_t = false;
						if (!open.contains(next)) {
							open.add(next);
							use_t = true;
						} else if (t < next.g) {
							use_t = true;
						}
						if (use_t) {
							next.prev = curr;
							next.g = t;
							next.f = t + heuristic(next, dest);
						}
					}
				}
			}

			// no path
			return null;
		}

		private double heuristic(final Node start, final Node end) {
			double dx = start.x - end.x;
			double dy = start.y - end.y;
			if (dx < 0) {
				dx = -dx;
			}
			if (dy < 0) {
				dy = -dy;
			}
			return dx < dy ? dy : dx;
		}

		public void init(final Game game, final Walking walking) {
			this.game = game;
			this.walking = walking;
		}

		private Node lowest_f(final Set<Node> open) {
			Node best = null;
			for (final Node t : open) {
				if (best == null || t.f < best.f) {
					best = t;
				}
			}
			return best;
		}

		private RSTile[] path(final Node end, final int base_x, final int base_y) {
			final LinkedList<RSTile> path = new LinkedList<RSTile>();
			Node p = end;
			while (p != null) {
				path.addFirst(p.toRSTile(base_x, base_y));
				p = p.prev;
			}
			return path.toArray(new RSTile[path.size()]);
		}

		private java.util.List<Node> successors(final Node t) {
			final LinkedList<Node> tiles = new LinkedList<Node>();
			final int x = t.x, y = t.y;
			final int f_x = x - off_x, f_y = y - off_y;
			final int here = flags[f_x][f_y];
			if (f_y > 0 && (here & WALL_SOUTH) == 0
					&& (flags[f_x][f_y - 1] & BLOCKED) == 0) {
				tiles.add(new Node(x, y - 1));
			}
			if (f_x > 0 && (here & WALL_WEST) == 0
					&& (flags[f_x - 1][f_y] & BLOCKED) == 0) {
				tiles.add(new Node(x - 1, y));
			}
			if (f_y < 103 && (here & WALL_NORTH) == 0
					&& (flags[f_x][f_y + 1] & BLOCKED) == 0) {
				tiles.add(new Node(x, y + 1));
			}
			if (f_x < 103 && (here & WALL_EAST) == 0
					&& (flags[f_x + 1][f_y] & BLOCKED) == 0) {
				tiles.add(new Node(x + 1, y));
			}
			if (f_x > 0 && f_y > 0
					&& (here & (WALL_SOUTH_WEST | WALL_SOUTH | WALL_WEST)) == 0
					&& (flags[f_x - 1][f_y - 1] & BLOCKED) == 0
					&& (flags[f_x][f_y - 1] & (BLOCKED | WALL_WEST)) == 0
					&& (flags[f_x - 1][f_y] & (BLOCKED | WALL_SOUTH)) == 0) {
				tiles.add(new Node(x - 1, y - 1));
			}
			if (f_x > 0 && f_y < 103
					&& (here & (WALL_NORTH_WEST | WALL_NORTH | WALL_WEST)) == 0
					&& (flags[f_x - 1][f_y + 1] & BLOCKED) == 0
					&& (flags[f_x][f_y + 1] & (BLOCKED | WALL_WEST)) == 0
					&& (flags[f_x - 1][f_y] & (BLOCKED | WALL_NORTH)) == 0) {
				tiles.add(new Node(x - 1, y + 1));
			}
			if (f_x < 103 && f_y > 0
					&& (here & (WALL_SOUTH_EAST | WALL_SOUTH | WALL_EAST)) == 0
					&& (flags[f_x + 1][f_y - 1] & BLOCKED) == 0
					&& (flags[f_x][f_y - 1] & (BLOCKED | WALL_EAST)) == 0
					&& (flags[f_x + 1][f_y] & (BLOCKED | WALL_SOUTH)) == 0) {
				tiles.add(new Node(x + 1, y - 1));
			}
			if (f_x > 0 && f_y < 103
					&& (here & (WALL_NORTH_EAST | WALL_NORTH | WALL_EAST)) == 0
					&& (flags[f_x + 1][f_y + 1] & BLOCKED) == 0
					&& (flags[f_x][f_y + 1] & (BLOCKED | WALL_EAST)) == 0
					&& (flags[f_x + 1][f_y] & (BLOCKED | WALL_NORTH)) == 0) {
				tiles.add(new Node(x + 1, y + 1));
			}
			return tiles;
		}
	}

	// -----------CHAT RESPONDER-----------\\
	private class ChatResponder extends Thread {
		long lastSaidHi = System.currentTimeMillis() - 110000,
				lastDenyBot = System.currentTimeMillis() - 110000;
		long lastLevelUp = System.currentTimeMillis() - 300000,
				nextCustom = System.currentTimeMillis() - 1000000;
		long lastSaidLevel = System.currentTimeMillis() - 110000,
				nextModAlert = System.currentTimeMillis(),
				sayNo = System.currentTimeMillis();
		int level = 0; // records firemaking level
		boolean run = true, doLevelRes = false, doCustomRes = false;
		boolean typing = false; // read by antiban (true = suppress antiban)
		boolean wait = false; // written by antiban (true = chat responder will
								// wait)
		boolean pause = false; // true if look away from screen is active

		// Chat Responder Customization
		String[] tradeRes = { "No thanks", "No thx", "Nope", "Im fine" },
				greetingRes = { "hi!", "hi.", "hi", "hello", "hello!",
						"hello.", "hello..", "yo", "yo!", "yes?", "what",
						"what?", "hey!" }, botterRes = { "huh", "zzz", "...",
						"???", "?????", "what", "what?", "no", "nop", "nope" },
				levelRes = { "yay", "haha", ":)", "yay!", "yay!!!",
						"finally..." }, customDetect = {}, customRes = {};
		double customTO = 160000, customTOR = 30000;

		private boolean findText(final String t, final String[] check) {
			final String[] m = check;
			for (final String element : m) {
				if (t.contains(element)) {
					return true;
				}
			}
			return false;
		}

		private String getChatMessage() {
			try {
				String text = null;
				for (int x = 280; x >= 180; x--) {
					if (interfaces.get(137).getComponent(x).getText() != null) {
						if (interfaces.get(137).getComponent(x).getText().contains("<col=")) {
							text = interfaces.get(137).getComponent(x).getText();
							break;
						}
					}
				}
				return text;
			} catch (final Exception e) {
			}
			return null;
		}

		private void response(final String m) {
			if (doLevelRes) {
				if (level > 0
						&& skills.getCurrentLevel(Skills.FIREMAKING) > level
						&& System.currentTimeMillis() - 200000 >= lastLevelUp) {
					lastLevelUp = System.currentTimeMillis();
					if (random(0, 11) <= 7) {
						resCount++;
						sleepNE(random(200, 600));
						final String[] r = levelRes;
						final int ra = random(0, r.length);
						sendText(r[ra]);
						log("[Response] Level Up Response: " + r[ra]);
						sleepNE(random(150, 250));
					}
					level = skills.getCurrentLevel(Skills.FIREMAKING);
					return;
				}
				level = skills.getCurrentLevel(Skills.FIREMAKING);
			}
			if (System.currentTimeMillis() - 150000 >= lastSaidLevel) {
				if (findText(m, new String[] { "fm", "firemak", "fremak" })
						&& findText(m, new String[] { "level", "levl", "lvel",
								"lvl" })) {
					lastSaidLevel = System.currentTimeMillis();
					resCount++;
					sleepNE(random(600, 2000));
					final int random = random(1, 11);
					if (random == 1) {
						sendText("fm lvl "
								+ skills.getCurrentLevel(Skills.FIREMAKING));
					} else if (random == 2) {
						sendText("level: "
								+ skills.getCurrentLevel(Skills.FIREMAKING));
					} else if (random == 3) {
						sendText("" + skills.getCurrentLevel(Skills.FIREMAKING));
					} else if (random == 4) {
						sendText("mines "
								+ skills.getCurrentLevel(Skills.FIREMAKING));
					} else if (random == 5) {
						sendText("lv "
								+ skills.getCurrentLevel(Skills.FIREMAKING));
					} else if (random == 6) {
						sendText(Integer.toString(skills.getCurrentLevel(Skills.FIREMAKING)));
					} else if (random > 6) {
						sleepNE(random(100, 200));
						keyboard.sendKey((char) KeyEvent.VK_ENTER);
						sleepNE(random(800, 1200));
						keyboard.sendKey('S');
						sleepNE(random(800, 1200));
						keyboard.sendKey('P');
						sleepNE(random(800, 1200));
						keyboard.sendKey('2');
					}
					log("[Response] Answered to Level Question: '" + m + "'");
					sleepNE(random(200, 300));
					return;
				}
			}
			if (findText(m, new String[] { "bottin", "botin", "botttin",
					"botter", "bottter", "boter", "bootin", "boottin",
					"booter", "bootter" })) {
				if (m.contains("?")
						|| m.contains(getMyPlayer().getName().toLowerCase())
						|| m.contains("!")) {
					if (System.currentTimeMillis() - 130000 >= lastDenyBot) {
						lastDenyBot = System.currentTimeMillis();
						resCount++;
						sleepNE(random(600, 2000));
						final String[] bot = botterRes;
						final int random3 = random(0, bot.length);
						sendText(bot[random3]);
						log("[Response] Answered to Botting Message: '" + m
								+ "'");
						sleepNE(random(150, 250));
						return;
					}
				}
			}
			if (findText(m, new String[] { "hi ", "hello", "hi<", "hey", "hi!",
					"hi.", "yo!", "yo.", "yo<" })) {
				if (System.currentTimeMillis() - 130000 >= lastSaidHi) {
					lastSaidHi = System.currentTimeMillis();
					resCount++;
					sleepNE(random(600, 1600));
					final String[] hi = greetingRes;
					final int random2 = random(0, hi.length);
					sendText(hi[random2]);
					log("[Response] Answered to Greeting: '" + m + "'");
					sleepNE(random(150, 250));
					return;
				}
			}
			if (doCustomRes && findText(m, customDetect)
					&& System.currentTimeMillis() > nextCustom) {
				nextCustom = (long) (System.currentTimeMillis() + random(customTO
						- customTOR, customTO + customTOR));
				resCount++;
				sleepNE(random(500, 1400));
				final int r = random(0, customRes.length);
				sendText(customRes[r]);
				log("[Response] Custom Response: '" + m + "'");
				sleepNE(random(150, 250));
				return;
			}
			sleepNE(random(650, 750));
		}

		public void run() {
			while (!thePainter.savedStats || getChatMessage() == null) {
				sleepNE(200);
			}
			while (run) {
				try {
					if (game.getClientState() == 10 && !pause) {
						if (useChatRes && tradeResponse) {
							if (sayNo < System.currentTimeMillis()) {
								tradeResponse = false;
								final int timeOut = random(110000, 130000);
								sayNo = System.currentTimeMillis() + timeOut;
								sleepNE(random(300, 700));
								final String[] res = tradeRes;
								final int rand = random(0, res.length);
								sendText(res[rand]);
								log("[Response] Said No to a Trade Request. Timeout: "
										+ timeOut / 1000 + " sec");
							}
						}
						final String m = getChatMessage().toLowerCase();
						if (m != null
								&& !m.equals(lastMessage)
								&& m.contains(getMyPlayer().getName().toLowerCase()
										+ ": <") != true) {
							if (useChatRes) {
								response(m);
							} else {
								sleepNE(random(700, 850));
							}
							lastMessage = m;
						} else {
							sleepNE(random(600, 700));
						}
					} else {
						sleepNE(random(300, 400));
					}
				} catch (final java.lang.Throwable t) {
				}
			}
		}

		private void sendText(final String text) {
			final char[] chs = text.toCharArray();
			typing = true;
			if (wait) {
				for (int i = 0; i < 21; i++) {
					sleepNE(10);
					if (!wait) {
						i = 21;
					}
				}
			}
			for (final char element : chs) {
				keyboard.sendKey(element);
				sleepNE(random(280, 550));
			}
			keyboard.sendKey((char) KeyEvent.VK_ENTER);
			typing = false;
		}

		private void sleepNE(final int ms) {
			try {
				Thread.sleep(ms);
			} catch (final Exception e) {
			}
		}
	}

	private class ChatResponderGUI {
		private static final long serialVersionUID = 1L;

		private JFrame WhiteBearGUI;

		private JPanel panel1;

		private JTabbedPane tabbedPane1;

		private JPanel panel6;

		private JTextArea textArea1;

		private JTextArea textArea2;

		private JButton button2;

		private JTextArea textArea4;

		private JTextArea textArea5;
		private JButton button3;
		private JTextArea textArea6;
		private JPanel panel4;
		private JLabel label17;
		private JLabel label18;
		private JLabel label20;
		private JLabel label30;
		private JTextArea textArea3;
		private JTextArea textArea7;
		private JTextArea textArea8;
		private JTextArea textArea9;
		private JLabel label19;
		private JButton button4;
		private JCheckBox radioButton2;
		private JPanel panel3;
		private JCheckBox radioButton1;
		private JLabel label8;
		private JFormattedTextField formattedTextField1;
		private JLabel label9;
		private JFormattedTextField formattedTextField3;
		private JButton button5;
		private JLabel label21;
		private JTextArea textArea10;
		private JTextArea textArea11;
		private JLabel label22;
		private JButton button1;
		private JLabel label1;

		private ChatResponderGUI() {
			initComponentx();
		}

		private void back1ActionPerformed(final ActionEvent e) {
			tabbedPane1.setSelectedIndex(0);
		}

		private void back2ActionPerformed(final ActionEvent e) {
			tabbedPane1.setSelectedIndex(0);
		}

		private void button1ActionPerformed(final ActionEvent e) {
			try {
				chatRes.tradeRes = textArea3.getText().toLowerCase().split("/");
				chatRes.greetingRes = textArea7.getText().toLowerCase().split("/");
				chatRes.botterRes = textArea8.getText().toLowerCase().split("/");
				chatRes.levelRes = textArea9.getText().toLowerCase().split("/");
				chatRes.customDetect = textArea10.getText().toLowerCase().split("/");
				chatRes.customRes = textArea11.getText().toLowerCase().split("/");
				chatRes.doLevelRes = radioButton2.isSelected();
				chatRes.doCustomRes = radioButton1.isSelected();
				chatRes.customTO = Integer.parseInt(formattedTextField1.getText());
				chatRes.customTOR = Integer.parseInt(formattedTextField3.getText());

				WBini.setProperty("CRuseLevelRes", String.valueOf(radioButton2.isSelected() ? true
						: false));
				WBini.setProperty("CRuseCustomRes", String.valueOf(radioButton1.isSelected() ? true
						: false));
				WBini.setProperty("CRtradeRes", textArea3.getText());
				WBini.setProperty("CRgreetingRes", textArea7.getText());
				WBini.setProperty("CRbotterRes", textArea8.getText());
				WBini.setProperty("CRlevelRes", textArea9.getText());
				WBini.setProperty("CRdetection", textArea10.getText());
				WBini.setProperty("CRresponse", textArea11.getText());
				WBini.setProperty("CRcustomTO", formattedTextField1.getText());
				WBini.setProperty("CRcustomTOR", formattedTextField3.getText());
				try {
					WBini.store(new FileWriter(new File(Configuration.Paths.getScriptCacheDirectory(), "WhiteBearAIOFiremaker.ini")), "The GUI Settings for White Bear AIO Firemaker (Version: "
							+ Double.toString(properties.version()) + ")");
				} catch (final IOException ioe) {
					log.warning("[GUI] Error occurred when saving GUI settings!");
				}
				chatResGUI = false;
				WhiteBearGUI.dispose();
			} catch (final java.lang.Exception ex) {
				log.severe("Error occurred when saving GUI options.");
			}
		}

		private void button2ActionPerformed(final ActionEvent e) {
			tabbedPane1.setSelectedIndex(1);
		}

		private void button3ActionPerformed(final ActionEvent e) {
			tabbedPane1.setSelectedIndex(2);
		}

		private void initComponentx() {
			WhiteBearGUI = new JFrame();
			panel1 = new JPanel();
			tabbedPane1 = new JTabbedPane();
			panel6 = new JPanel();
			textArea1 = new JTextArea();
			textArea2 = new JTextArea();
			button2 = new JButton();
			textArea4 = new JTextArea();
			textArea5 = new JTextArea();
			button3 = new JButton();
			textArea6 = new JTextArea();
			panel4 = new JPanel();
			label17 = new JLabel();
			label18 = new JLabel();
			label20 = new JLabel();
			label30 = new JLabel();
			textArea3 = new JTextArea();
			textArea7 = new JTextArea();
			textArea8 = new JTextArea();
			textArea9 = new JTextArea();
			label19 = new JLabel();
			button4 = new JButton();
			radioButton2 = new JCheckBox();
			panel3 = new JPanel();
			radioButton1 = new JCheckBox();
			label8 = new JLabel();
			formattedTextField1 = new JFormattedTextField();
			label9 = new JLabel();
			formattedTextField3 = new JFormattedTextField();
			button5 = new JButton();
			label21 = new JLabel();
			textArea10 = new JTextArea();
			textArea11 = new JTextArea();
			label22 = new JLabel();
			button1 = new JButton();
			label1 = new JLabel();

			// ======== WhiteBearGUI ========
			{
				WhiteBearGUI.setAlwaysOnTop(true);
				WhiteBearGUI.setBackground(Color.black);
				WhiteBearGUI.setResizable(false);
				WhiteBearGUI.setMinimumSize(new Dimension(405, 405));
				WhiteBearGUI.setTitle("White Bear AIO Firemaker");
				WhiteBearGUI.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				WhiteBearGUI.setFont(new Font("Century Gothic", Font.PLAIN, 12));
				final Container WhiteBearGUIContentPane = WhiteBearGUI.getContentPane();
				WhiteBearGUIContentPane.setLayout(null);

				// ======== panel1 ========
				{
					panel1.setBackground(Color.black);
					panel1.setForeground(Color.green);
					panel1.setMinimumSize(new Dimension(100, 200));
					panel1.setLayout(null);

					// ======== tabbedPane1 ========
					{
						tabbedPane1.setFont(new Font("Century Gothic", Font.PLAIN, 12));
						tabbedPane1.setForeground(new Color(0, 153, 0));
						tabbedPane1.setEnabled(false);

						// ======== panel6 ========
						{
							panel6.setBackground(Color.black);
							panel6.setLayout(null);

							// ---- textArea1 ----
							textArea1.setText(" This GUI allows you to change Chat Responder\nsettings. Just click start if you do not know what\nto do.");
							textArea1.setLineWrap(true);
							textArea1.setFont(new Font("Century Gothic", Font.PLAIN, 14));
							textArea1.setTabSize(0);
							textArea1.setBackground(Color.black);
							textArea1.setForeground(new Color(204, 255, 0));
							textArea1.setEditable(false);
							textArea1.setBorder(null);
							textArea1.setOpaque(false);
							textArea1.setRequestFocusEnabled(false);
							textArea1.setFocusable(false);
							panel6.add(textArea1);
							textArea1.setBounds(20, 10, 330, 60);

							// ---- textArea2 ----
							textArea2.setText(" For responses, separate each response with /\nE.g. For hi/hello/yes?, the possible responses\nare hi, hello and yes?. When the bot needs to\nrespond, it will randomly pick one response");
							textArea2.setLineWrap(true);
							textArea2.setFont(new Font("Century Gothic", Font.PLAIN, 14));
							textArea2.setTabSize(0);
							textArea2.setBackground(Color.black);
							textArea2.setForeground(new Color(204, 255, 0));
							textArea2.setEditable(false);
							textArea2.setBorder(null);
							textArea2.setOpaque(false);
							textArea2.setRequestFocusEnabled(false);
							textArea2.setFocusable(false);
							panel6.add(textArea2);
							textArea2.setBounds(20, 80, 330, 78);

							// ---- button2 ----
							button2.setText("Customize Responses");
							button2.setBackground(Color.black);
							button2.setFont(new Font("Century Gothic", Font.BOLD, 12));
							button2.setForeground(new Color(0, 102, 51));
							button2.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(final ActionEvent e) {
									button2ActionPerformed(e);
								}
							});
							panel6.add(button2);
							button2.setBounds(190, 157, 160, 23);

							// ---- textArea4 ----
							textArea4.setText("and use it.");
							textArea4.setLineWrap(true);
							textArea4.setFont(new Font("Century Gothic", Font.PLAIN, 14));
							textArea4.setTabSize(0);
							textArea4.setBackground(Color.black);
							textArea4.setForeground(new Color(204, 255, 0));
							textArea4.setEditable(false);
							textArea4.setBorder(null);
							textArea4.setOpaque(false);
							textArea4.setRequestFocusEnabled(false);
							textArea4.setFocusable(false);
							panel6.add(textArea4);
							textArea4.setBounds(20, 156, 85, 20);

							// ---- textArea5 ----
							textArea5.setText(" You can also set a custom detection, reply");
							textArea5.setLineWrap(true);
							textArea5.setFont(new Font("Century Gothic", Font.PLAIN, 14));
							textArea5.setTabSize(0);
							textArea5.setBackground(Color.black);
							textArea5.setForeground(new Color(204, 255, 0));
							textArea5.setEditable(false);
							textArea5.setBorder(null);
							textArea5.setOpaque(false);
							textArea5.setRequestFocusEnabled(false);
							textArea5.setFocusable(false);
							panel6.add(textArea5);
							textArea5.setBounds(20, 190, 330, 20);

							// ---- button3 ----
							button3.setText("Custom detection");
							button3.setBackground(Color.black);
							button3.setFont(new Font("Century Gothic", Font.BOLD, 12));
							button3.setForeground(new Color(0, 102, 51));
							button3.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(final ActionEvent e) {
									button3ActionPerformed(e);
								}
							});
							panel6.add(button3);
							button3.setBounds(210, 207, 140, 23);

							// ---- textArea6 ----
							textArea6.setText("and timeout.");
							textArea6.setLineWrap(true);
							textArea6.setFont(new Font("Century Gothic", Font.PLAIN, 14));
							textArea6.setTabSize(0);
							textArea6.setBackground(Color.black);
							textArea6.setForeground(new Color(204, 255, 0));
							textArea6.setEditable(false);
							textArea6.setBorder(null);
							textArea6.setOpaque(false);
							textArea6.setRequestFocusEnabled(false);
							textArea6.setFocusable(false);
							panel6.add(textArea6);
							textArea6.setBounds(20, 209, 100, 20);

							{ // compute preferred size
								final Dimension preferredSize = new Dimension();
								for (int i = 0; i < panel6.getComponentCount(); i++) {
									final Rectangle bounds = panel6.getComponent(i).getBounds();
									preferredSize.width = Math.max(bounds.x
											+ bounds.width, preferredSize.width);
									preferredSize.height = Math.max(bounds.y
											+ bounds.height, preferredSize.height);
								}
								final Insets insets = panel6.getInsets();
								preferredSize.width += insets.right;
								preferredSize.height += insets.bottom;
								panel6.setMinimumSize(preferredSize);
								panel6.setPreferredSize(preferredSize);
							}
						}
						tabbedPane1.addTab("Info", panel6);

						// ======== panel4 ========
						{
							panel4.setBackground(Color.black);
							panel4.setLayout(null);

							// ---- label17 ----
							label17.setText("Trade Response");
							label17.setBackground(new Color(51, 51, 51));
							label17.setForeground(new Color(255, 255, 102));
							label17.setFont(new Font("Century Gothic", Font.BOLD, 12));
							label17.setHorizontalAlignment(SwingConstants.LEFT);
							panel4.add(label17);
							label17.setBounds(5, 15, 110, 20);

							// ---- label18 ----
							label18.setText("Greeting Response");
							label18.setBackground(new Color(51, 51, 51));
							label18.setForeground(new Color(255, 255, 102));
							label18.setFont(new Font("Century Gothic", Font.BOLD, 12));
							label18.setHorizontalAlignment(SwingConstants.LEFT);
							panel4.add(label18);
							label18.setBounds(5, 65, 120, 20);

							// ---- label20 ----
							label20.setText("Botter! Response");
							label20.setBackground(new Color(51, 51, 51));
							label20.setForeground(new Color(255, 255, 102));
							label20.setFont(new Font("Century Gothic", Font.BOLD, 12));
							panel4.add(label20);
							label20.setBounds(5, 115, 115, 20);

							// ---- label30 ----
							label30.setText("Level up (yourself)");
							label30.setBackground(new Color(51, 51, 51));
							label30.setForeground(new Color(255, 255, 102));
							label30.setFont(new Font("Century Gothic", Font.BOLD, 12));
							panel4.add(label30);
							label30.setBounds(5, 165, 115, 20);

							// ---- textArea3 ----
							textArea3.setForeground(new Color(255, 255, 204));
							textArea3.setBackground(Color.gray);
							textArea3.setText("no thanks/no thx/nope/im fine");
							textArea3.setFont(new Font("Century Gothic", Font.PLAIN, 12));
							textArea3.setLineWrap(true);
							panel4.add(textArea3);
							textArea3.setBounds(130, 15, 225, 37);

							// ---- textArea7 ----
							textArea7.setForeground(new Color(255, 255, 204));
							textArea7.setBackground(Color.gray);
							textArea7.setText("hi!/hi./hi/hello/hello!/hello./hello../yo/yo!/yes?/what/what?/hey!");
							textArea7.setFont(new Font("Century Gothic", Font.PLAIN, 12));
							textArea7.setLineWrap(true);
							panel4.add(textArea7);
							textArea7.setBounds(130, 65, 225, 37);

							// ---- textArea8 ----
							textArea8.setForeground(new Color(255, 255, 204));
							textArea8.setBackground(Color.gray);
							textArea8.setText("huh/zzz/.../???/?????/what/what?/no/nop/nope");
							textArea8.setFont(new Font("Century Gothic", Font.PLAIN, 12));
							textArea8.setLineWrap(true);
							panel4.add(textArea8);
							textArea8.setBounds(130, 115, 225, 37);

							// ---- textArea9 ----
							textArea9.setForeground(new Color(255, 255, 204));
							textArea9.setBackground(Color.gray);
							textArea9.setText("yay/haha/:)/yay!/yay!!!/finally...");
							textArea9.setFont(new Font("Century Gothic", Font.PLAIN, 12));
							textArea9.setLineWrap(true);
							panel4.add(textArea9);
							textArea9.setBounds(130, 165, 225, 37);

							// ---- label19 ----
							label19.setText("(70% chance to talk)");
							label19.setBackground(new Color(51, 51, 51));
							label19.setForeground(new Color(255, 255, 102));
							label19.setFont(new Font("Century Gothic", Font.BOLD, 12));
							panel4.add(label19);
							label19.setBounds(4, 182, 130, 20);

							// ---- button4 ----
							button4.setText("Back");
							button4.setBackground(Color.black);
							button4.setFont(new Font("Century Gothic", Font.BOLD, 12));
							button4.setForeground(new Color(0, 102, 51));
							button4.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(final ActionEvent e) {
									back1ActionPerformed(e);
								}
							});
							panel4.add(button4);
							button4.setBounds(285, 215, 70, 23);

							// ---- radioButton2 ----
							radioButton2.setText("Use Level up Response");
							radioButton2.setBackground(Color.black);
							radioButton2.setForeground(Color.yellow);
							radioButton2.setFont(new Font("Century Gothic", Font.BOLD, 13));
							panel4.add(radioButton2);
							radioButton2.setBounds(10, 210, 183, 25);

							{ // compute preferred size
								final Dimension preferredSize = new Dimension();
								for (int i = 0; i < panel4.getComponentCount(); i++) {
									final Rectangle bounds = panel4.getComponent(i).getBounds();
									preferredSize.width = Math.max(bounds.x
											+ bounds.width, preferredSize.width);
									preferredSize.height = Math.max(bounds.y
											+ bounds.height, preferredSize.height);
								}
								final Insets insets = panel4.getInsets();
								preferredSize.width += insets.right;
								preferredSize.height += insets.bottom;
								panel4.setMinimumSize(preferredSize);
								panel4.setPreferredSize(preferredSize);
							}
						}
						tabbedPane1.addTab("Responses", panel4);

						// ======== panel3 ========
						{
							panel3.setBackground(Color.black);
							panel3.setLayout(null);

							// ---- radioButton1 ----
							radioButton1.setText("Use Custom Detection");
							radioButton1.setBackground(Color.black);
							radioButton1.setForeground(Color.yellow);
							radioButton1.setFont(new Font("Century Gothic", Font.BOLD, 13));
							panel3.add(radioButton1);
							radioButton1.setBounds(17, 15, 183, 25);

							// ---- label8 ----
							label8.setText("Timeout (seconds):");
							label8.setBackground(new Color(51, 51, 51));
							label8.setForeground(new Color(255, 255, 102));
							label8.setFont(new Font("Century Gothic", Font.BOLD, 12));
							panel3.add(label8);
							label8.setBounds(25, 160, 125, 20);

							// ---- formattedTextField1 ----
							formattedTextField1.setFont(new Font("Century Gothic", Font.PLAIN, 12));
							formattedTextField1.setForeground(new Color(255, 255, 204));
							formattedTextField1.setBackground(Color.gray);
							formattedTextField1.setText("160");
							panel3.add(formattedTextField1);
							formattedTextField1.setBounds(155, 160, 70, 23);

							// ---- label9 ----
							label9.setText("\u00b1");
							label9.setBackground(new Color(51, 51, 51));
							label9.setForeground(new Color(255, 255, 102));
							label9.setFont(new Font("Century Gothic", Font.BOLD, 12));
							panel3.add(label9);
							label9.setBounds(230, 160, 15, 20);

							// ---- formattedTextField3 ----
							formattedTextField3.setFont(new Font("Century Gothic", Font.PLAIN, 12));
							formattedTextField3.setForeground(new Color(255, 255, 204));
							formattedTextField3.setBackground(Color.gray);
							formattedTextField3.setText("30");
							panel3.add(formattedTextField3);
							formattedTextField3.setBounds(245, 160, 59, 23);

							// ---- button5 ----
							button5.setText("Back");
							button5.setBackground(Color.black);
							button5.setFont(new Font("Century Gothic", Font.BOLD, 12));
							button5.setForeground(new Color(0, 102, 51));
							button5.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(final ActionEvent e) {
									back2ActionPerformed(e);
								}
							});
							panel3.add(button5);
							button5.setBounds(285, 215, 70, 23);

							// ---- label21 ----
							label21.setText("Detect:");
							label21.setBackground(new Color(51, 51, 51));
							label21.setForeground(new Color(255, 255, 102));
							label21.setFont(new Font("Century Gothic", Font.BOLD, 12));
							label21.setHorizontalAlignment(SwingConstants.LEFT);
							panel3.add(label21);
							label21.setBounds(25, 50, 55, 20);

							// ---- textArea10 ----
							textArea10.setForeground(new Color(255, 255, 204));
							textArea10.setBackground(Color.gray);
							textArea10.setText("i love u/i luv u/i love you/i luv you/i lov u/i love you");
							textArea10.setFont(new Font("Century Gothic", Font.PLAIN, 12));
							textArea10.setLineWrap(true);
							panel3.add(textArea10);
							textArea10.setBounds(95, 50, 235, 37);

							// ---- textArea11 ----
							textArea11.setForeground(new Color(255, 255, 204));
							textArea11.setBackground(Color.gray);
							textArea11.setText("yuck/yuk/gross/eww/zzz/.../zzzz/....");
							textArea11.setFont(new Font("Century Gothic", Font.PLAIN, 12));
							textArea11.setLineWrap(true);
							panel3.add(textArea11);
							textArea11.setBounds(95, 100, 235, 37);

							// ---- label22 ----
							label22.setText("Respond:");
							label22.setBackground(new Color(51, 51, 51));
							label22.setForeground(new Color(255, 255, 102));
							label22.setFont(new Font("Century Gothic", Font.BOLD, 12));
							label22.setHorizontalAlignment(SwingConstants.LEFT);
							panel3.add(label22);
							label22.setBounds(25, 100, 65, 20);

							{ // compute preferred size
								final Dimension preferredSize = new Dimension();
								for (int i = 0; i < panel3.getComponentCount(); i++) {
									final Rectangle bounds = panel3.getComponent(i).getBounds();
									preferredSize.width = Math.max(bounds.x
											+ bounds.width, preferredSize.width);
									preferredSize.height = Math.max(bounds.y
											+ bounds.height, preferredSize.height);
								}
								final Insets insets = panel3.getInsets();
								preferredSize.width += insets.right;
								preferredSize.height += insets.bottom;
								panel3.setMinimumSize(preferredSize);
								panel3.setPreferredSize(preferredSize);
							}
						}
						tabbedPane1.addTab("Custom", panel3);

					}
					panel1.add(tabbedPane1);
					tabbedPane1.setBounds(15, 45, 370, 275);

					// ---- button1 ----
					button1.setText("Start Firemaking!");
					button1.setBackground(Color.black);
					button1.setFont(new Font("Century Gothic", Font.BOLD, 18));
					button1.setForeground(new Color(0, 102, 51));
					button1.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(final ActionEvent e) {
							button1ActionPerformed(e);
						}
					});
					panel1.add(button1);
					button1.setBounds(25, 325, 350, 55);

					// ---- label1 ----
					label1.setText("Chat Responder Customization");
					label1.setBackground(new Color(51, 51, 51));
					label1.setForeground(new Color(153, 255, 153));
					label1.setFont(new Font("Century Gothic", Font.BOLD, 20));
					panel1.add(label1);
					label1.setBounds(50, 5, 315, 40);

					{ // compute preferred size
						final Dimension preferredSize = new Dimension();
						for (int i = 0; i < panel1.getComponentCount(); i++) {
							final Rectangle bounds = panel1.getComponent(i).getBounds();
							preferredSize.width = Math.max(bounds.x
									+ bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y
									+ bounds.height, preferredSize.height);
						}
						final Insets insets = panel1.getInsets();
						preferredSize.width += insets.right;
						preferredSize.height += insets.bottom;
						panel1.setMinimumSize(preferredSize);
						panel1.setPreferredSize(preferredSize);
					}
				}
				WhiteBearGUIContentPane.add(panel1);
				panel1.setBounds(0, 0, 400, 395);

				WhiteBearGUIContentPane.setPreferredSize(new Dimension(405, 420));
				WhiteBearGUI.setSize(405, 420);
				WhiteBearGUI.setLocationRelativeTo(WhiteBearGUI.getOwner());
				loadSettings();
			}
		}

		public boolean loadSettings() {
			try {
				WBini.load(new FileInputStream(new File(Configuration.Paths.getScriptCacheDirectory(), "WhiteBearAIOFiremaker.ini")));
			} catch (final FileNotFoundException e) {
				log.warning("[GUI] Settings file was not found!");
				return false;
			} catch (final IOException e) {
				log.warning("[GUI] Error occurred when loading settings!");
				return false;
			}
			try {
				if (WBini.getProperty("CRuseLevelRes") != null) {
					radioButton2.setSelected(Boolean.parseBoolean(WBini.getProperty("CRuseLevelRes")));
				}
				if (WBini.getProperty("CRuseCustomRes") != null) {
					radioButton1.setSelected(Boolean.parseBoolean(WBini.getProperty("CRuseCustomRes")));
				}
				if (WBini.getProperty("CRtradeRes") != null) {
					textArea3.setText(WBini.getProperty("CRtradeRes"));
				}
				if (WBini.getProperty("CRgreetingRes") != null) {
					textArea7.setText(WBini.getProperty("CRgreetingRes"));
				}
				if (WBini.getProperty("CRbotterRes") != null) {
					textArea8.setText(WBini.getProperty("CRbotterRes"));
				}
				if (WBini.getProperty("CRlevelRes") != null) {
					textArea9.setText(WBini.getProperty("CRlevelRes"));
				}
				if (WBini.getProperty("CRdetection") != null) {
					textArea10.setText(WBini.getProperty("CRdetection"));
				}
				if (WBini.getProperty("CRresponse") != null) {
					textArea11.setText(WBini.getProperty("CRresponse"));
				}

				if (WBini.getProperty("CRcustomTO") != null) {
					formattedTextField1.setText(WBini.getProperty("CRcustomTO"));
				}
				if (WBini.getProperty("CRcustomTOR") != null) {
					formattedTextField3.setText(WBini.getProperty("CRcustomTOR"));
				}
			} catch (final java.lang.Exception e) {
				log.warning("[GUI] Settings file is corrupt, using default settings!");
			}
			return true;
		}
	}

	private class Disallowed {
		String loadPath = "";
		RSTile[] tiles = null;
		Area[] areas = null;
		LinkedList<RSTile> tilesList = new LinkedList<RSTile>();
		LinkedList<Area> areasList = new LinkedList<Area>();

		public Disallowed(final String loadPath) {
			this.loadPath = loadPath;
			tiles = null;
		}

		public void addTile(final RSTile... ts) {
			try {
				tiles = tilesList.toArray(new RSTile[tilesList.size()]);
				final BufferedWriter fw = new BufferedWriter(new FileWriter(loadPath, true));
				for (final RSTile t : ts) {
					tilesList.add(t);
					fw.write(t.getX() + "," + t.getY());
					fw.newLine();
				}
				fw.close();
			} catch (final Throwable thr) {
				log.warning("Error occurred while adding disallowed tile.");
			}
		}

		public void loadData() {
			try {
				final File file = new File(loadPath);
				if (!file.exists()) {
					file.createNewFile();
				} else {
					final BufferedReader fr = new BufferedReader(new FileReader(loadPath));
					String set = fr.readLine();
					while (set != null) {
						try {
							final String[] pc = set.split(",");
							if (pc.length >= 4) {
								final int minX = Integer.parseInt(pc[0].trim());
								final int minY = Integer.parseInt(pc[1].trim());
								final int maxX = Integer.parseInt(pc[2].trim());
								final int maxY = Integer.parseInt(pc[3].trim());
								areasList.add(new Area(minX, minY, maxX, maxY));
							} else if (pc.length >= 2) {
								final int x = Integer.parseInt(pc[0].trim());
								final int y = Integer.parseInt(pc[1].trim());
								tilesList.add(new RSTile(x, y));
							}
						} catch (final Throwable tt) {
						}
						set = fr.readLine();
					}
					fr.close();
				}
				tiles = tilesList.toArray(new RSTile[tilesList.size()]);
				areas = areasList.toArray(new Area[areasList.size()]);
			} catch (final Throwable thr) {
				log.warning("Error occurred while loading data.");
			}
		}
	}

	private class FireLane {
		int y;
		int startX;
		int endX;
		int cost;
		int uLength, uStart, uEnd;
		RSComponent hovering;

		public FireLane(final int y, final int startX, final int endX,
				final int cost) {
			this.y = y;
			this.startX = startX;
			this.endX = endX;
			this.cost = cost;
			uLength = -1;
			uEnd = -1;
			uStart = -1;
			hovering = null;
		}

		public boolean hasNext() {
			return getMyPlayer().getLocation().getX() > uEnd;
		}

		public boolean isHovering() {
			return hovering != null;
		}

		public int length() {
			return endX - startX + 1;
		}

		public void paint(final Graphics g, final boolean onlyUsed) {
			g.setColor(new Color(0, 0, 255, 100));
			final int start = onlyUsed ? uStart : startX;
			final int end = onlyUsed ? uEnd : endX;
			for (int x = end; x <= start; x++) {
				final RSTile t = new RSTile(x, y);
				final Point pn = calc.tileToScreen(t, 0, 0, 0);
				final Point px = calc.tileToScreen(t, 1, 0, 0);
				final Point py = calc.tileToScreen(t, 0, 1, 0);
				final Point pxy = calc.tileToScreen(t, 1, 1, 0);
				if (py.x != -1 && pxy.x != -1 && px.x != -1 && pn.x != -1) {
					g.fillPolygon(new int[] { py.x, pxy.x, px.x, pn.x }, new int[] {
							py.y, pxy.y, px.y, pn.y }, 4);
				}
			}
		}

		public int scan() {
			uStart = -1;
			uEnd = -1;
			uLength = -1;
			int tStart = -1, tLength = 0;
			boolean cont = false;
			for (int x = startX; x >= endX; x--) {
				final RSTile t = new RSTile(x, y);
				if (objects.getTopAt(t, 1) == null && !pDisallowed(t)) {
					if (!cont) {
						tLength = 1;
						tStart = x;
						cont = true;
					} else {
						tLength++;
					}
					if (x == endX) {
						cont = false;
						if (tLength > uLength) {
							uLength = tLength;
							uEnd = x + 1;
							uStart = tStart;
						}
					}
				} else {
					if (cont) {
						cont = false;
						if (tLength > uLength) {
							uLength = tLength;
							uEnd = x + 1;
							uStart = tStart;
						}
						tLength = 0;
					}
				}
			}
			cost = calc.pathLengthTo(new RSTile(uStart, y), false);
			return uLength;
		}
	}

	private enum State {
		Antiban, Firemake, Bank, GoBank, Move, Generate
	}

	// -----------------GUI----------------\\
	private class WhiteBearGUI {
		private static final long serialVersionUID = 1L;
		public boolean useSetting = true;

		private JFrame WhiteBearGUI;

		private JPanel panel1;

		private JTabbedPane tabbedPane1;

		private JPanel panel6;

		private JCheckBox radioButton12;

		private JTextArea textArea1;

		private JLabel label3;
		private JComboBox logCombo;
		private JCheckBox radioButton25;
		private JButton button2;
		private JPanel panel2;
		private JFormattedTextField jTextField;
		private JCheckBox radioButton22;
		private JLabel label4;
		private JComboBox clrSelected;
		private JLabel label5;
		private JLabel label6;
		private JLabel label16;
		private JFormattedTextField textSecond;
		private JLabel label15;
		private JFormattedTextField textMinute;
		private JLabel label14;
		private JFormattedTextField textHour;
		private JLabel label13;
		private JFormattedTextField jTextField2;
		private JLabel label21;
		private JCheckBox radioButton23;
		private JLabel label30;
		private JFormattedTextField tfTextFont;
		private JPanel panel3;
		private JCheckBox radioButton1;
		private JCheckBox radioButton2;
		private JLabel label8;
		private JFormattedTextField formattedTextField1;
		private JLabel label9;
		private JLabel label10;
		private JFormattedTextField formattedTextField3;
		private JLabel label11;
		private JFormattedTextField formattedTextField2;
		private JLabel label12;
		private JFormattedTextField formattedTextField4;
		private JCheckBox radioButton3;
		private JPanel panel5;
		private JCheckBox check3;
		private JCheckBox check2;
		private JCheckBox check4;
		private JLabel label22;
		private JFormattedTextField jTextField3;
		private JLabel label23;
		private JFormattedTextField jTextField4;
		private JLabel label24;
		private JLabel label25;
		private JButton button1;
		private JLabel label1;
		private JLabel label2;

		private WhiteBearGUI() {
			initComponents();
		}

		private void button1ActionPerformed(final ActionEvent e) {
			if (chatResGUI) {
				log.severe("Chat Responder GUI is still active!");
			} else {
				logId = logCombo.getSelectedIndex();

				colour = (String) clrSelected.getSelectedItem();
				useBreaking = radioButton1.isSelected();
				randomBreaking = radioButton2.isSelected();
				thePainter.antialias = !radioButton22.isSelected();
				thePainter.font = tfTextFont.getText();
				checkUpdates = radioButton25.isSelected();
				useFkeys = !radioButton23.isSelected();
				useChatRes = radioButton12.isSelected();
				antiban.checkFriend = check2.isSelected();
				antiban.checkExperience = check3.isSelected();
				antiban.screenLookaway = check4.isSelected();
				breakLogout = radioButton3.isSelected();
				minMS = Integer.parseInt(jTextField.getText());
				maxMS = Integer.parseInt(jTextField2.getText());
				if (minMS >= maxMS) {
					maxMS = minMS + 1;
				}
				midTime = Integer.parseInt(formattedTextField1.getText());
				randTime = Integer.parseInt(formattedTextField3.getText());
				midLength = Integer.parseInt(formattedTextField2.getText());
				randLength = Integer.parseInt(formattedTextField4.getText());
				if (midTime < 10) {
					midTime = 10;
				} else if (midTime >= 50001) {
					midTime = 50000;
				}
				if (randTime < 3) {
					randTime = 3;
				} else if (randTime >= 20001) {
					randTime = 20000;
				}
				if (randTime > midTime) {
					randTime = midTime - 1;
				}

				if (midLength < 2) {
					midLength = 2;
				} else if (midLength >= 35001) {
					midLength = 35000;
				}
				if (randLength < 1) {
					randLength = 1;
				} else if (randLength >= 15001) {
					randLength = 15000;
				}
				if (randLength > midLength) {
					randLength = midLength - 1;
				}
				final long hour = Long.parseLong(textHour.getText());
				final long minute = Long.parseLong(textMinute.getText());
				final long second = Long.parseLong(textSecond.getText());
				if (hour <= 0 && minute <= 0 && second <= 0) {
					stopTime = -1;
				} else {
					long tempTime = 0;
					if (hour > 1) {
						final long tempHr = tempTime;
						tempTime = tempHr + hour * 3600000;
					}
					if (minute > 1) {
						final long tempMin = tempTime;
						tempTime = tempMin + minute * 60000;
					}
					if (second > 1) {
						final long tempSec = tempTime;
						tempTime = tempSec + second * 1000;
					}
					stopTime = hour * 3600000 + minute * 60000 + second * 1000;
				}
				WBini.setProperty("UseSetting", String.valueOf(useSetting ? true
						: false));
				if (useSetting) {
					WBini.setProperty("LogId", String.valueOf(logCombo.getSelectedIndex()));
					WBini.setProperty("UseChatRes", String.valueOf(radioButton12.isSelected() ? true
							: false));
					WBini.setProperty("CheckUpdate", String.valueOf(radioButton25.isSelected() ? true
							: false));
					WBini.setProperty("PaintColour", String.valueOf(clrSelected.getSelectedIndex()));
					WBini.setProperty("PaintFont", tfTextFont.getText());
					WBini.setProperty("Fkeys", String.valueOf(radioButton23.isSelected() ? true
							: false));
					WBini.setProperty("MinMouseSpeed", jTextField.getText());
					WBini.setProperty("MaxMouseSpeed", jTextField2.getText());
					WBini.setProperty("Antialias", String.valueOf(radioButton22.isSelected() ? true
							: false));
					WBini.setProperty("Breaking", String.valueOf(radioButton1.isSelected() ? true
							: false));
					WBini.setProperty("RandomBreak", String.valueOf(radioButton2.isSelected() ? true
							: false));
					WBini.setProperty("CheckFriend", String.valueOf(check2.isSelected() ? true
							: false));
					WBini.setProperty("CheckExperience", String.valueOf(check3.isSelected() ? true
							: false));
					WBini.setProperty("ScreenLookaway", String.valueOf(check4.isSelected() ? true
							: false));
					WBini.setProperty("TimeoutA1", jTextField3.getText());
					WBini.setProperty("TimeoutA2", jTextField4.getText());
					WBini.setProperty("BreakLogout", String.valueOf(radioButton3.isSelected() ? true
							: false));
					WBini.setProperty("MidTime", formattedTextField1.getText());
					WBini.setProperty("RandTime", formattedTextField3.getText());
					WBini.setProperty("MidLength", formattedTextField2.getText());
					WBini.setProperty("RandLength", formattedTextField4.getText());
					WBini.setProperty("AutoStopH", textHour.getText());
					WBini.setProperty("AutoStopM", textMinute.getText());
					WBini.setProperty("AutoStopS", textSecond.getText());
				}
				try {
					WBini.store(new FileWriter(new File(Configuration.Paths.getScriptCacheDirectory(), "WhiteBearAIOFiremaker.ini")), "The GUI Settings for White Bear AIO Firemaker (Version: "
							+ Double.toString(properties.version()) + ")");
				} catch (final Throwable ioe) {
					log.warning("[GUI] Error occurred when saving GUI settings!");
				}

				guiStart = true;
				WhiteBearGUI.dispose();
			}
		}

		private void button2ActionPerformed(final ActionEvent e) {
			if (!chatResGUI) {
				chatResGUI = true;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						final ChatResponderGUI chatGUI = new ChatResponderGUI();
						chatGUI.WhiteBearGUI.setVisible(true);
					}
				});
			}
		}

		private void initComponents() {
			WhiteBearGUI = new JFrame();
			panel1 = new JPanel();
			tabbedPane1 = new JTabbedPane();
			panel6 = new JPanel();
			radioButton12 = new JCheckBox();
			textArea1 = new JTextArea();
			label3 = new JLabel();
			logCombo = new JComboBox();
			radioButton25 = new JCheckBox();
			button2 = new JButton();
			panel2 = new JPanel();
			jTextField = new JFormattedTextField();
			radioButton22 = new JCheckBox();
			label4 = new JLabel();
			clrSelected = new JComboBox();
			label5 = new JLabel();
			label6 = new JLabel();
			label16 = new JLabel();
			textSecond = new JFormattedTextField();
			label15 = new JLabel();
			textMinute = new JFormattedTextField();
			label14 = new JLabel();
			textHour = new JFormattedTextField();
			label13 = new JLabel();
			jTextField2 = new JFormattedTextField();
			label21 = new JLabel();
			radioButton23 = new JCheckBox();
			label30 = new JLabel();
			tfTextFont = new JFormattedTextField();
			panel3 = new JPanel();
			radioButton1 = new JCheckBox();
			radioButton2 = new JCheckBox();
			label8 = new JLabel();
			formattedTextField1 = new JFormattedTextField();
			label9 = new JLabel();
			label10 = new JLabel();
			formattedTextField3 = new JFormattedTextField();
			label11 = new JLabel();
			formattedTextField2 = new JFormattedTextField();
			label12 = new JLabel();
			formattedTextField4 = new JFormattedTextField();
			radioButton3 = new JCheckBox();
			panel5 = new JPanel();
			check3 = new JCheckBox();
			check2 = new JCheckBox();
			check4 = new JCheckBox();
			label22 = new JLabel();
			jTextField3 = new JFormattedTextField();
			label23 = new JLabel();
			jTextField4 = new JFormattedTextField();
			label24 = new JLabel();
			label25 = new JLabel();
			button1 = new JButton();
			label1 = new JLabel();
			label2 = new JLabel();

			// ======== WhiteBearGUI ========
			{
				WhiteBearGUI.setAlwaysOnTop(true);
				WhiteBearGUI.setBackground(Color.black);
				WhiteBearGUI.setResizable(false);
				WhiteBearGUI.setMinimumSize(new Dimension(405, 405));
				WhiteBearGUI.setTitle("White Bear AIO Firemaker");
				WhiteBearGUI.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				WhiteBearGUI.setFont(new Font("Century Gothic", Font.PLAIN, 12));
				final Container WhiteBearGUIContentPane = WhiteBearGUI.getContentPane();
				WhiteBearGUIContentPane.setLayout(null);

				// ======== panel1 ========
				{
					panel1.setBackground(Color.black);
					panel1.setForeground(Color.green);
					panel1.setMinimumSize(new Dimension(100, 200));
					panel1.setLayout(null);

					// ======== tabbedPane1 ========
					{
						tabbedPane1.setFont(new Font("Century Gothic", Font.PLAIN, 12));
						tabbedPane1.setForeground(new Color(0, 153, 0));

						// ======== panel6 ========
						{
							panel6.setBackground(Color.black);
							panel6.setLayout(null);

							// ---- radioButton12 ----
							radioButton12.setText("Use Chat Responder");
							radioButton12.setBackground(Color.black);
							radioButton12.setForeground(Color.yellow);
							radioButton12.setFont(new Font("Century Gothic", Font.BOLD, 13));
							radioButton12.setSelected(true);
							panel6.add(radioButton12);
							radioButton12.setBounds(17, 183, 158, radioButton12.getPreferredSize().height);

							// ---- textArea1 ----
							textArea1.setText(" Set screen size as Fixed\n Set graphic detail as Minimum\n Have logs and tinderbox in main tab of Bank");
							textArea1.setLineWrap(true);
							textArea1.setFont(new Font("Century Gothic", Font.PLAIN, 14));
							textArea1.setTabSize(0);
							textArea1.setBackground(Color.black);
							textArea1.setForeground(new Color(204, 255, 0));
							textArea1.setEditable(false);
							textArea1.setBorder(null);
							textArea1.setOpaque(false);
							textArea1.setRequestFocusEnabled(false);
							textArea1.setFocusable(false);
							panel6.add(textArea1);
							textArea1.setBounds(20, 9, 330, 62);

							// ---- label3 ----
							label3.setText("Log Type");
							label3.setBackground(new Color(51, 51, 51));
							label3.setForeground(new Color(255, 255, 102));
							label3.setFont(new Font("Century Gothic", Font.BOLD, 14));
							panel6.add(label3);
							label3.setBounds(20, 78, 65, 20);

							// ---- logCombo ----
							logCombo.setBackground(Color.black);
							logCombo.setForeground(new Color(51, 51, 51));
							logCombo.setBorder(null);
							logCombo.setFont(new Font("Century Gothic", Font.BOLD, 12));
							logCombo.setModel(new DefaultComboBoxModel(new String[] {
									"Normal Logs", "Oak Logs", "Willow Logs",
									"Teak Logs", "Maple Logs", "Mahogany Logs",
									"Arctic Pine Logs", "Eucalyptus Logs",
									"Yew Logs", "Magic Logs" }));
							logCombo.setSelectedIndex(0);
							logCombo.setMaximumRowCount(10);
							panel6.add(logCombo);
							logCombo.setBounds(100, 77, 135, 25);

							// ---- radioButton25 ----
							radioButton25.setText("Check for Updates");
							radioButton25.setBackground(Color.black);
							radioButton25.setForeground(Color.yellow);
							radioButton25.setFont(new Font("Century Gothic", Font.BOLD, 13));
							radioButton25.setSelected(true);
							panel6.add(radioButton25);
							radioButton25.setBounds(17, 212, 150, 25);

							// ---- button2 ----
							button2.setText("Customize");
							button2.setBackground(Color.black);
							button2.setFont(new Font("Century Gothic", Font.BOLD, 12));
							button2.setForeground(new Color(0, 102, 51));
							button2.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(final ActionEvent e) {
									button2ActionPerformed(e);
								}
							});
							panel6.add(button2);
							button2.setBounds(185, 184, 95, 23);

							{ // compute preferred size
								final Dimension preferredSize = new Dimension();
								for (int i = 0; i < panel6.getComponentCount(); i++) {
									final Rectangle bounds = panel6.getComponent(i).getBounds();
									preferredSize.width = Math.max(bounds.x
											+ bounds.width, preferredSize.width);
									preferredSize.height = Math.max(bounds.y
											+ bounds.height, preferredSize.height);
								}
								final Insets insets = panel6.getInsets();
								preferredSize.width += insets.right;
								preferredSize.height += insets.bottom;
								panel6.setMinimumSize(preferredSize);
								panel6.setPreferredSize(preferredSize);
							}
						}
						tabbedPane1.addTab("Info", panel6);

						// ======== panel2 ========
						{
							panel2.setBackground(Color.black);
							panel2.setLayout(null);

							// ---- jTextField ----
							jTextField.setFont(new Font("Century Gothic", Font.PLAIN, 12));
							jTextField.setForeground(new Color(255, 255, 204));
							jTextField.setBackground(Color.gray);
							jTextField.setText("4");
							panel2.add(jTextField);
							jTextField.setBounds(120, 86, 35, 23);

							// ---- radioButton22 ----
							radioButton22.setText("Disable Paint Antialias");
							radioButton22.setBackground(Color.black);
							radioButton22.setForeground(Color.yellow);
							radioButton22.setFont(new Font("Century Gothic", Font.BOLD, 13));
							panel2.add(radioButton22);
							radioButton22.setBounds(17, 117, 175, 25);

							// ---- label4 ----
							label4.setText("Paint Colour");
							label4.setBackground(new Color(51, 51, 51));
							label4.setForeground(new Color(255, 255, 102));
							label4.setFont(new Font("Century Gothic", Font.BOLD, 14));
							panel2.add(label4);
							label4.setBounds(20, 14, 90, 20);

							// ---- clrSelected ----
							clrSelected.setBackground(Color.black);
							clrSelected.setForeground(new Color(51, 51, 51));
							clrSelected.setBorder(null);
							clrSelected.setFont(new Font("Century Gothic", Font.BOLD, 12));
							clrSelected.setModel(new DefaultComboBoxModel(new String[] {
									"Black", "Blue", "Green", "Red", "Purple",
									"Brown" }));
							clrSelected.setSelectedIndex(0);
							panel2.add(clrSelected);
							clrSelected.setBounds(118, 13, 110, 25);

							// ---- label5 ----
							label5.setText("Mouse Speed");
							label5.setBackground(new Color(51, 51, 51));
							label5.setForeground(new Color(255, 255, 102));
							label5.setFont(new Font("Century Gothic", Font.BOLD, 14));
							panel2.add(label5);
							label5.setBounds(20, 86, 100, 20);

							// ---- label6 ----
							label6.setText("(higher = slower)");
							label6.setBackground(new Color(51, 51, 51));
							label6.setForeground(new Color(255, 255, 102));
							label6.setFont(new Font("Century Gothic", Font.BOLD, 12));
							panel2.add(label6);
							label6.setBounds(225, 86, 105, 20);

							// ---- label16 ----
							label16.setText("(hr:min:sec)");
							label16.setBackground(new Color(51, 51, 51));
							label16.setForeground(new Color(255, 255, 102));
							label16.setFont(new Font("Century Gothic", Font.BOLD, 12));
							panel2.add(label16);
							label16.setBounds(220, 213, 80, 20);

							// ---- textSecond ----
							textSecond.setFont(new Font("Century Gothic", Font.PLAIN, 12));
							textSecond.setForeground(new Color(255, 255, 204));
							textSecond.setBackground(Color.gray);
							textSecond.setText("0");
							panel2.add(textSecond);
							textSecond.setBounds(185, 213, 30, 23);

							// ---- label15 ----
							label15.setText(":");
							label15.setBackground(new Color(51, 51, 51));
							label15.setForeground(new Color(255, 255, 102));
							label15.setFont(new Font("Century Gothic", Font.BOLD, 12));
							panel2.add(label15);
							label15.setBounds(175, 213, 10, 20);

							// ---- textMinute ----
							textMinute.setFont(new Font("Century Gothic", Font.PLAIN, 12));
							textMinute.setForeground(new Color(255, 255, 204));
							textMinute.setBackground(Color.gray);
							textMinute.setText("0");
							panel2.add(textMinute);
							textMinute.setBounds(140, 213, 30, 23);

							// ---- label14 ----
							label14.setText(":");
							label14.setBackground(new Color(51, 51, 51));
							label14.setForeground(new Color(255, 255, 102));
							label14.setFont(new Font("Century Gothic", Font.BOLD, 12));
							panel2.add(label14);
							label14.setBounds(135, 213, 10, 20);

							// ---- textHour ----
							textHour.setFont(new Font("Century Gothic", Font.PLAIN, 12));
							textHour.setForeground(new Color(255, 255, 204));
							textHour.setBackground(Color.gray);
							textHour.setText("0");
							panel2.add(textHour);
							textHour.setBounds(100, 213, 30, 23);

							// ---- label13 ----
							label13.setText("Auto Stop:");
							label13.setBackground(new Color(51, 51, 51));
							label13.setForeground(new Color(255, 255, 102));
							label13.setFont(new Font("Century Gothic", Font.BOLD, 14));
							panel2.add(label13);
							label13.setBounds(20, 213, 75, 20);

							// ---- jTextField2 ----
							jTextField2.setFont(new Font("Century Gothic", Font.PLAIN, 12));
							jTextField2.setForeground(new Color(255, 255, 204));
							jTextField2.setBackground(Color.gray);
							jTextField2.setText("6");
							panel2.add(jTextField2);
							jTextField2.setBounds(180, 86, 35, 23);

							// ---- label21 ----
							label21.setText("to");
							label21.setBackground(new Color(51, 51, 51));
							label21.setForeground(new Color(255, 255, 102));
							label21.setFont(new Font("Century Gothic", Font.BOLD, 12));
							panel2.add(label21);
							label21.setBounds(162, 86, 15, 20);

							// ---- radioButton23 ----
							radioButton23.setText("Disable F-keys");
							radioButton23.setBackground(Color.black);
							radioButton23.setForeground(Color.yellow);
							radioButton23.setFont(new Font("Century Gothic", Font.BOLD, 13));
							panel2.add(radioButton23);
							radioButton23.setBounds(17, 178, 133, 25);

							// ---- label30 ----
							label30.setText("Paint Font");
							label30.setBackground(new Color(51, 51, 51));
							label30.setForeground(new Color(255, 255, 102));
							label30.setFont(new Font("Century Gothic", Font.BOLD, 14));
							panel2.add(label30);
							label30.setBounds(20, 50, 90, 20);

							// ---- tfTextFont ----
							tfTextFont.setFont(new Font("Century Gothic", Font.PLAIN, 12));
							tfTextFont.setForeground(new Color(255, 255, 204));
							tfTextFont.setBackground(Color.gray);
							tfTextFont.setText("sansserif");
							panel2.add(tfTextFont);
							tfTextFont.setBounds(119, 50, 108, 23);

							{ // compute preferred size
								final Dimension preferredSize = new Dimension();
								for (int i = 0; i < panel2.getComponentCount(); i++) {
									final Rectangle bounds = panel2.getComponent(i).getBounds();
									preferredSize.width = Math.max(bounds.x
											+ bounds.width, preferredSize.width);
									preferredSize.height = Math.max(bounds.y
											+ bounds.height, preferredSize.height);
								}
								final Insets insets = panel2.getInsets();
								preferredSize.width += insets.right;
								preferredSize.height += insets.bottom;
								panel2.setMinimumSize(preferredSize);
								panel2.setPreferredSize(preferredSize);
							}
						}
						tabbedPane1.addTab("Option", panel2);

						// ======== panel3 ========
						{
							panel3.setBackground(Color.black);
							panel3.setLayout(null);

							// ---- radioButton1 ----
							radioButton1.setText("Use Breaking");
							radioButton1.setBackground(Color.black);
							radioButton1.setForeground(Color.yellow);
							radioButton1.setFont(new Font("Century Gothic", Font.BOLD, 13));
							panel3.add(radioButton1);
							radioButton1.setBounds(17, 20, 120, 25);

							// ---- radioButton2 ----
							radioButton2.setText("Completely Random");
							radioButton2.setBackground(Color.black);
							radioButton2.setForeground(Color.yellow);
							radioButton2.setFont(new Font("Century Gothic", Font.BOLD, 13));
							radioButton2.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(final ActionEvent e) {
									radioButton2ActionPerformed(e);
								}
							});
							panel3.add(radioButton2);
							radioButton2.setBounds(175, 20, 170, 25);

							// ---- label8 ----
							label8.setText("Time between breaks:");
							label8.setBackground(new Color(51, 51, 51));
							label8.setForeground(new Color(255, 255, 102));
							label8.setFont(new Font("Century Gothic", Font.BOLD, 12));
							panel3.add(label8);
							label8.setBounds(20, 65, 140, 20);

							// ---- formattedTextField1 ----
							formattedTextField1.setFont(new Font("Century Gothic", Font.PLAIN, 12));
							formattedTextField1.setForeground(new Color(255, 255, 204));
							formattedTextField1.setBackground(Color.gray);
							formattedTextField1.setText("90");
							panel3.add(formattedTextField1);
							formattedTextField1.setBounds(160, 65, 45, 23);

							// ---- label9 ----
							label9.setText("\u00b1");
							label9.setBackground(new Color(51, 51, 51));
							label9.setForeground(new Color(255, 255, 102));
							label9.setFont(new Font("Century Gothic", Font.BOLD, 12));
							panel3.add(label9);
							label9.setBounds(225, 65, 15, 20);

							// ---- label10 ----
							label10.setText("(time unit: minutes)");
							label10.setBackground(new Color(51, 51, 51));
							label10.setForeground(new Color(255, 255, 102));
							label10.setFont(new Font("Century Gothic", Font.BOLD, 12));
							panel3.add(label10);
							label10.setBounds(240, 135, 110, 20);

							// ---- formattedTextField3 ----
							formattedTextField3.setFont(new Font("Century Gothic", Font.PLAIN, 12));
							formattedTextField3.setForeground(new Color(255, 255, 204));
							formattedTextField3.setBackground(Color.gray);
							formattedTextField3.setText("90");
							panel3.add(formattedTextField3);
							formattedTextField3.setBounds(240, 65, 45, 23);

							// ---- label11 ----
							label11.setText("Length of breaks:");
							label11.setBackground(new Color(51, 51, 51));
							label11.setForeground(new Color(255, 255, 102));
							label11.setFont(new Font("Century Gothic", Font.BOLD, 12));
							panel3.add(label11);
							label11.setBounds(20, 110, 110, 20);

							// ---- formattedTextField2 ----
							formattedTextField2.setFont(new Font("Century Gothic", Font.PLAIN, 12));
							formattedTextField2.setForeground(new Color(255, 255, 204));
							formattedTextField2.setBackground(Color.gray);
							formattedTextField2.setText("8");
							panel3.add(formattedTextField2);
							formattedTextField2.setBounds(160, 110, 45, 23);

							// ---- label12 ----
							label12.setText("\u00b1");
							label12.setBackground(new Color(51, 51, 51));
							label12.setForeground(new Color(255, 255, 102));
							label12.setFont(new Font("Century Gothic", Font.BOLD, 12));
							panel3.add(label12);
							label12.setBounds(225, 110, 15, 20);

							// ---- formattedTextField4 ----
							formattedTextField4.setFont(new Font("Century Gothic", Font.PLAIN, 12));
							formattedTextField4.setForeground(new Color(255, 255, 204));
							formattedTextField4.setBackground(Color.gray);
							formattedTextField4.setText("2");
							panel3.add(formattedTextField4);
							formattedTextField4.setBounds(240, 110, 45, 23);

							// ---- radioButton3 ----
							radioButton3.setText("Logout before break starts");
							radioButton3.setBackground(Color.black);
							radioButton3.setForeground(Color.yellow);
							radioButton3.setFont(new Font("Century Gothic", Font.BOLD, 13));
							panel3.add(radioButton3);
							radioButton3.setBounds(17, 170, 208, 25);

							{ // compute preferred size
								final Dimension preferredSize = new Dimension();
								for (int i = 0; i < panel3.getComponentCount(); i++) {
									final Rectangle bounds = panel3.getComponent(i).getBounds();
									preferredSize.width = Math.max(bounds.x
											+ bounds.width, preferredSize.width);
									preferredSize.height = Math.max(bounds.y
											+ bounds.height, preferredSize.height);
								}
								final Insets insets = panel3.getInsets();
								preferredSize.width += insets.right;
								preferredSize.height += insets.bottom;
								panel3.setMinimumSize(preferredSize);
								panel3.setPreferredSize(preferredSize);
							}
						}
						tabbedPane1.addTab("Breaking", panel3);

						// ======== panel5 ========
						{
							panel5.setBackground(Color.black);
							panel5.setLayout(null);

							// ---- check3 ----
							check3.setText("Check firemaking exp");
							check3.setBackground(Color.black);
							check3.setForeground(Color.yellow);
							check3.setFont(new Font("Century Gothic", Font.BOLD, 13));
							check3.setSelected(true);
							panel5.add(check3);
							check3.setBounds(17, 17, 173, 25);

							// ---- check2 ----
							check2.setText("Check friends");
							check2.setBackground(Color.black);
							check2.setForeground(Color.yellow);
							check2.setFont(new Font("Century Gothic", Font.BOLD, 13));
							panel5.add(check2);
							check2.setBounds(200, 17, 145, 25);

							// ---- check4 ----
							check4.setText("Take short random breaks");
							check4.setBackground(Color.black);
							check4.setForeground(Color.yellow);
							check4.setFont(new Font("Century Gothic", Font.BOLD, 13));
							panel5.add(check4);
							check4.setBounds(17, 66, 208, 25);

							// ---- label22 ----
							label22.setText("Timeout");
							label22.setBackground(new Color(51, 51, 51));
							label22.setForeground(new Color(255, 255, 102));
							label22.setFont(new Font("Century Gothic", Font.BOLD, 14));
							panel5.add(label22);
							label22.setBounds(20, 100, 70, 20);

							// ---- jTextField3 ----
							jTextField3.setFont(new Font("Century Gothic", Font.PLAIN, 12));
							jTextField3.setForeground(new Color(255, 255, 204));
							jTextField3.setBackground(Color.gray);
							jTextField3.setText("50");
							panel5.add(jTextField3);
							jTextField3.setBounds(119, 100, 40, 23);

							// ---- label23 ----
							label23.setText("Max:");
							label23.setBackground(new Color(51, 51, 51));
							label23.setForeground(new Color(255, 255, 102));
							label23.setFont(new Font("Century Gothic", Font.BOLD, 12));
							panel5.add(label23);
							label23.setBounds(167, 100, 38, 20);

							// ---- jTextField4 ----
							jTextField4.setFont(new Font("Century Gothic", Font.PLAIN, 12));
							jTextField4.setForeground(new Color(255, 255, 204));
							jTextField4.setBackground(Color.gray);
							jTextField4.setText("120");
							panel5.add(jTextField4);
							jTextField4.setBounds(203, 100, 40, 23);

							// ---- label24 ----
							label24.setText("(in seconds)");
							label24.setBackground(new Color(51, 51, 51));
							label24.setForeground(new Color(255, 255, 102));
							label24.setFont(new Font("Century Gothic", Font.BOLD, 12));
							panel5.add(label24);
							label24.setBounds(251, 100, 83, 20);

							// ---- label25 ----
							label25.setText("Min:");
							label25.setBackground(new Color(51, 51, 51));
							label25.setForeground(new Color(255, 255, 102));
							label25.setFont(new Font("Century Gothic", Font.BOLD, 12));
							panel5.add(label25);
							label25.setBounds(86, 100, 36, 20);

							{ // compute preferred size
								final Dimension preferredSize = new Dimension();
								for (int i = 0; i < panel5.getComponentCount(); i++) {
									final Rectangle bounds = panel5.getComponent(i).getBounds();
									preferredSize.width = Math.max(bounds.x
											+ bounds.width, preferredSize.width);
									preferredSize.height = Math.max(bounds.y
											+ bounds.height, preferredSize.height);
								}
								final Insets insets = panel5.getInsets();
								preferredSize.width += insets.right;
								preferredSize.height += insets.bottom;
								panel5.setMinimumSize(preferredSize);
								panel5.setPreferredSize(preferredSize);
							}
						}
						tabbedPane1.addTab("Antiban", panel5);

					}
					panel1.add(tabbedPane1);
					tabbedPane1.setBounds(15, 55, 370, 275);

					// ---- button1 ----
					button1.setText("Start Firemaking!");
					button1.setBackground(Color.black);
					button1.setFont(new Font("Century Gothic", Font.BOLD, 18));
					button1.setForeground(new Color(0, 102, 51));
					button1.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(final ActionEvent e) {
							button1ActionPerformed(e);
						}
					});
					panel1.add(button1);
					button1.setBounds(25, 335, 350, 55);

					// ---- label1 ----
					label1.setText("White Bear AIO Firemaker");
					label1.setBackground(new Color(51, 51, 51));
					label1.setForeground(new Color(153, 255, 153));
					label1.setFont(new Font("Century Gothic", Font.BOLD, 24));
					panel1.add(label1);
					label1.setBounds(45, 5, 315, 50);

					// ---- label2 ----
					label2.setText("Version: " + properties.version());
					label2.setBackground(new Color(51, 51, 51));
					label2.setForeground(new Color(204, 255, 0));
					label2.setFont(new Font("Century Gothic", Font.PLAIN, 12));
					panel1.add(label2);
					label2.setBounds(300, 51, 83, 20);

					{ // compute preferred size
						final Dimension preferredSize = new Dimension();
						for (int i = 0; i < panel1.getComponentCount(); i++) {
							final Rectangle bounds = panel1.getComponent(i).getBounds();
							preferredSize.width = Math.max(bounds.x
									+ bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y
									+ bounds.height, preferredSize.height);
						}
						final Insets insets = panel1.getInsets();
						preferredSize.width += insets.right;
						preferredSize.height += insets.bottom;
						panel1.setMinimumSize(preferredSize);
						panel1.setPreferredSize(preferredSize);
					}
				}
				WhiteBearGUIContentPane.add(panel1);
				panel1.setBounds(0, 0, 400, 405);

				WhiteBearGUIContentPane.setPreferredSize(new Dimension(405, 425));
				WhiteBearGUI.setSize(405, 425);
				WhiteBearGUI.setLocationRelativeTo(WhiteBearGUI.getOwner());
				loadSettings();
			}
		}

		public boolean loadSettings() {
			try {
				WBini.load(new FileInputStream(new File(Configuration.Paths.getScriptCacheDirectory(), "WhiteBearAIOFiremaker.ini")));
			} catch (final FileNotFoundException e) {
				return false;
			} catch (final IOException e) {
				log.warning("[GUI] Error occurred when loading settings!");
				return false;
			}
			try {
				if (WBini.getProperty("UseSetting") != null) {
					useSetting = Boolean.parseBoolean(WBini.getProperty("UseSetting"));
				}
			} catch (final java.lang.Exception e) {
			}
			if (useSetting) {
				try {
					if (WBini.getProperty("LogId") != null) {
						logCombo.setSelectedIndex(Integer.valueOf(WBini.getProperty("LogId")));
					}

					if (WBini.getProperty("UseChatRes") != null) {
						radioButton12.setSelected(Boolean.parseBoolean(WBini.getProperty("UseChatRes")));
					}
					if (WBini.getProperty("CheckUpdate") != null) {
						radioButton25.setSelected(Boolean.parseBoolean(WBini.getProperty("CheckUpdate")));
					}
					if (WBini.getProperty("PaintColour") != null) {
						clrSelected.setSelectedIndex(Integer.valueOf(WBini.getProperty("PaintColour")));
					}
					if (WBini.getProperty("PaintFont") != null) {
						tfTextFont.setText(WBini.getProperty("PaintFont"));
					}
					if (WBini.getProperty("Fkeys") != null) {
						radioButton23.setSelected(Boolean.parseBoolean(WBini.getProperty("Fkeys")));
					}
					if (WBini.getProperty("MinMouseSpeed") != null) {
						jTextField.setText(WBini.getProperty("MinMouseSpeed"));
					}
					if (WBini.getProperty("MaxMouseSpeed") != null) {
						jTextField2.setText(WBini.getProperty("MaxMouseSpeed"));
					}
					if (WBini.getProperty("Antialias") != null) {
						radioButton22.setSelected(Boolean.parseBoolean(WBini.getProperty("Antialias")));
					}
					if (WBini.getProperty("Breaking") != null) {
						radioButton1.setSelected(Boolean.parseBoolean(WBini.getProperty("Breaking")));
					}
					if (WBini.getProperty("RandomBreak") != null) {
						radioButton2.setSelected(Boolean.parseBoolean(WBini.getProperty("RandomBreak")));
					}
					if (WBini.getProperty("CheckFriend") != null) {
						check2.setSelected(Boolean.parseBoolean(WBini.getProperty("CheckFriend")));
					}
					if (WBini.getProperty("CheckExperience") != null) {
						check3.setSelected(Boolean.parseBoolean(WBini.getProperty("CheckExperience")));
					}
					if (WBini.getProperty("ScreenLookaway") != null) {
						check4.setSelected(Boolean.parseBoolean(WBini.getProperty("ScreenLookaway")));
					}
					if (WBini.getProperty("TimeoutA1") != null) {
						jTextField3.setText(WBini.getProperty("TimeoutA1"));
					}
					if (WBini.getProperty("TimeoutA2") != null) {
						jTextField4.setText(WBini.getProperty("TimeoutA2"));
					}
					if (WBini.getProperty("BreakLogout") != null) {
						radioButton3.setSelected(Boolean.parseBoolean(WBini.getProperty("BreakLogout")));
					}
					if (WBini.getProperty("MidTime") != null) {
						formattedTextField1.setText(WBini.getProperty("MidTime"));
					}
					if (WBini.getProperty("RandTime") != null) {
						formattedTextField3.setText(WBini.getProperty("RandTime"));
					}
					if (WBini.getProperty("MidLength") != null) {
						formattedTextField2.setText(WBini.getProperty("MidLength"));
					}
					if (WBini.getProperty("RandLength") != null) {
						formattedTextField4.setText(WBini.getProperty("RandLength"));
					}
					if (WBini.getProperty("AutoStopH") != null) {
						textHour.setText(WBini.getProperty("AutoStopH"));
					}
					if (WBini.getProperty("AutoStopM") != null) {
						textMinute.setText(WBini.getProperty("AutoStopM"));
					}
					if (WBini.getProperty("AutoStopS") != null) {
						textSecond.setText(WBini.getProperty("AutoStopS"));
					}

					if (WBini.getProperty("CRuseLevelRes") != null) {
						chatRes.doLevelRes = Boolean.parseBoolean(WBini.getProperty("CRuseLevelRes"));
					}
					if (WBini.getProperty("CRuseCustomRes") != null) {
						chatRes.doCustomRes = Boolean.parseBoolean(WBini.getProperty("CRuseCustomRes"));
					}
					if (WBini.getProperty("CRtradeRes") != null) {
						chatRes.tradeRes = WBini.getProperty("CRtradeRes").toLowerCase().split("/");
					}
					if (WBini.getProperty("CRgreetingRes") != null) {
						chatRes.greetingRes = WBini.getProperty("CRgreetingRes").toLowerCase().split("/");
					}
					if (WBini.getProperty("CRbotterRes") != null) {
						chatRes.botterRes = WBini.getProperty("CRbotterRes").toLowerCase().split("/");
					}
					if (WBini.getProperty("CRlevelRes") != null) {
						chatRes.levelRes = WBini.getProperty("CRlevelRes").toLowerCase().split("/");
					}
					if (WBini.getProperty("CRdetection") != null) {
						chatRes.customDetect = WBini.getProperty("CRdetection").toLowerCase().split("/");
					}
					if (WBini.getProperty("CRresponse") != null) {
						chatRes.customRes = WBini.getProperty("CRresponse").toLowerCase().split("/");
					}
					if (WBini.getProperty("CRcustomTO") != null) {
						chatRes.customTO = Integer.parseInt(WBini.getProperty("CRcustomTO"));
					}
					if (WBini.getProperty("CRcustomTOR") != null) {
						chatRes.customTOR = Integer.parseInt(WBini.getProperty("CRcustomTOR"));
					}
				} catch (final java.lang.Exception e) {
					log.warning("[GUI] Settings file is corrupt, using default settings!");
				}
			}
			return true;
		}

		private void radioButton2ActionPerformed(final ActionEvent e) {
			randomBreaking = radioButton2.isSelected();
			if (randomBreaking == true) {
				formattedTextField1.setEnabled(false);
				formattedTextField2.setEnabled(false);
				formattedTextField3.setEnabled(false);
				formattedTextField4.setEnabled(false);
			} else {
				formattedTextField1.setEnabled(true);
				formattedTextField2.setEnabled(true);
				formattedTextField3.setEnabled(true);
				formattedTextField4.setEnabled(true);
			}
		}
	}

	private class WhiteBearPaint {
		public class MouseWatcher implements Runnable {

			Rectangle rect = null;

			MouseWatcher(final Rectangle rect) {
				this.rect = rect;
			}

			public void run() {
				Point mouse1 = new Point(p);
				while (rect.contains(mouse1)) {
					try {
						mouse1 = new Point(p);
						Thread.sleep(50);
					} catch (final Exception e) {
					}
				}
			}
		}

		Rectangle clr1 = new Rectangle(210, 43, 15, 15);
		Rectangle clr2 = new Rectangle(227, 43, 15, 15);
		Rectangle clr3 = new Rectangle(244, 43, 15, 15);
		Rectangle clr4 = new Rectangle(261, 43, 15, 15);
		Rectangle clr5 = new Rectangle(278, 43, 15, 15);
		Rectangle clr6 = new Rectangle(295, 43, 15, 15);
		Rectangle cr1 = new Rectangle(210, 61, 15, 15);
		Rectangle logOut = new Rectangle(295, 79, 55, 15);
		Rectangle logOut2 = new Rectangle(320, 220, 200, 70);
		Rectangle logOutYes = new Rectangle(338, 255, 80, 20);

		Rectangle logOutNo = new Rectangle(423, 255, 80, 20);
		Rectangle r = new Rectangle(7, 345, 408, 114);
		Rectangle r1 = new Rectangle(420, 345, 77, 20);
		Rectangle r2 = new Rectangle(420, 369, 77, 20);
		Rectangle r3 = new Rectangle(420, 392, 77, 20);
		Rectangle r4 = new Rectangle(420, 415, 77, 20);
		Rectangle r5 = new Rectangle(420, 439, 77, 20);
		Rectangle r6 = new Rectangle(420, 439, 77, 20);
		Rectangle r2c = new Rectangle(415, 369, 5, 20);
		Rectangle r3c = new Rectangle(415, 392, 5, 20);
		Rectangle r4c = new Rectangle(415, 415, 5, 20);
		Rectangle r5c = new Rectangle(415, 439, 5, 20);

		Rectangle r6c = new Rectangle(415, 439, 5, 20);
		Rectangle sb1 = new Rectangle(12, 370, 398, 16);
		boolean savedStats = false, antialias = false;
		int currentTab = 0, lastTab = 0;
		int start_exp = 0, start_lvl = 0;
		int gained_exp = 0, gained_lvl = 0;
		int paintX = 7, paintY = 344;
		Point p = new Point(0, 0);
		Color fonts, normalBack, hiddenPaint, lines;

		String font = "sansserif";
		Thread mouseWatcher = new Thread();

		final NumberFormat nf = NumberFormat.getInstance();
		long time_ScriptStart = System.currentTimeMillis();

		long runTime = System.currentTimeMillis() - time_ScriptStart;

		public void drawPaint(final Graphics g, final Rectangle rect) {
			g.setColor(normalBack);
			g.fillRect(r1.x, r1.y, r1.width, r1.height);
			g.fillRect(r2.x, r2.y, r2.width, r2.height);
			g.fillRect(r3.x, r3.y, r3.width, r3.height);
			g.fillRect(r4.x, r4.y, r4.width, r4.height);
			g.fillRect(r5.x, r5.y, r5.width, r5.height);
			g.fillRect(r6.x, r6.y, r6.width, r6.height);
			g.fillRect(rect.x, rect.y, rect.width, rect.height);
			g.fillRect(r.x, r.y, r.width, r.height);
			g.setColor(fonts);
			g.setFont(new Font(font, Font.PLAIN, 10));
			drawString(g, "X", r1, 4);
			drawString(g, "Main", r2, 4);
			drawString(g, "Info", r3, 4);
			drawString(g, "Stats", r4, 4);
			drawString(g, "Etc", r5, 4);
			drawString(g, "Setting", r6, 4);
			g.setColor(normalBack);
		}

		public void drawStat(final Graphics g, final int index, final int count) {
			g.setFont(new Font(font, Font.PLAIN, 11));
			g.setColor(new Color(97, 97, 97, 185));
			final int half = sb1.height / 2;
			g.fillRect(sb1.x, sb1.y, sb1.width, half);
			g.setColor(new Color(60, 60, 60, 185));
			g.fillRect(sb1.x, sb1.y + half, sb1.width, half);
			final int percent = skills.getPercentToNextLevel(Skills.FIREMAKING);
			g.setColor(new Color(255 - 2 * percent, (int) (1.7 * percent), 0, 150));
			g.fillRect(sb1.x + 2, sb1.y + 2, (int) ((double) (sb1.width - 4) / 100.0 * (double) percent), sb1.height - 4);
			g.setColor(Color.WHITE);
			g.drawString("Firemaking", sb1.x + 3, sb1.y + 12);
			drawStringEnd(g, percent + "%", sb1, -3, 4);
		}

		public void drawStats(final Graphics g) {
			if (savedStats == true) {
				final int stats = Skills.FIREMAKING;
				gained_exp = skills.getCurrentExp(stats) - start_exp;
				gained_lvl = skills.getCurrentLevel(stats) - start_lvl;
				drawStat(g, 1, 1);
			}
		}

		public void drawString(final Graphics g, final String str,
				final Rectangle rect, final int offset) {
			final FontMetrics font = g.getFontMetrics();
			final Rectangle2D bounds = font.getStringBounds(str, g);
			final int width = (int) bounds.getWidth();
			g.drawString(str, rect.x + (rect.width - width) / 2, rect.y
					+ rect.height / 2 + offset);
		}

		public void drawStringEnd(final Graphics g, final String str,
				final Rectangle rect, final int xOffset, final int yOffset) {
			final FontMetrics font = g.getFontMetrics();
			final Rectangle2D bounds = font.getStringBounds(str, g);
			final int width = (int) bounds.getWidth();
			g.drawString(str, rect.x + rect.width - width + xOffset, rect.y
					+ rect.height / 2 + yOffset);
		}

		public void drawStringMain(final Graphics g, final String str,
				final String val, final Rectangle rect, final int xOffset,
				final int yOffset, final int index, final boolean leftSide) {
			final FontMetrics font = g.getFontMetrics();
			final Rectangle2D bounds = font.getStringBounds(val, g);
			final int indexMult = 17;
			final int width = (int) bounds.getWidth();
			if (leftSide) {
				g.drawString(str, rect.x + xOffset, rect.y + yOffset + index
						* indexMult);
				g.drawString(val, rect.x + rect.width / 2 - width - xOffset, rect.y
						+ yOffset + index * indexMult);
			} else {
				g.drawString(str, rect.x + rect.width / 2 + xOffset, rect.y
						+ yOffset + index * indexMult);
				g.drawString(val, rect.x + rect.width - width - xOffset, rect.y
						+ yOffset + index * indexMult);
			}
		}

		public String formatTime(final long milliseconds) {
			final long t_seconds = milliseconds / 1000;
			final long t_minutes = t_seconds / 60;
			final long t_hours = t_minutes / 60;
			final long seconds = t_seconds % 60;
			final long minutes = t_minutes % 60;
			final long hours = t_hours % 500;
			return nf.format(hours) + ":" + nf.format(minutes) + ":"
					+ nf.format(seconds);
		}

		public void paint(final Graphics g) {
			// Redefine locations of all rectangles //
			r = new Rectangle(paintX, paintY, 408, 114);
			r1 = new Rectangle(paintX + 393, paintY + 117, 15, 18);
			r2 = new Rectangle(paintX + 181, paintY + 117, 40, 18);
			r3 = new Rectangle(paintX + 225, paintY + 117, 35, 18);
			r4 = new Rectangle(paintX + 264, paintY + 117, 40, 18);
			r5 = new Rectangle(paintX + 308, paintY + 117, 30, 18);
			r6 = new Rectangle(paintX + 343, paintY + 117, 45, 18);
			r2c = new Rectangle(paintX + 181, paintY + 114, 40, 3);
			r3c = new Rectangle(paintX + 225, paintY + 114, 35, 3);
			r4c = new Rectangle(paintX + 264, paintY + 114, 40, 3);
			r5c = new Rectangle(paintX + 308, paintY + 114, 30, 3);
			r6c = new Rectangle(paintX + 343, paintY + 114, 45, 3);

			sb1 = new Rectangle(paintX + 5, paintY + 25, 398, 16);
			clr1 = new Rectangle(paintX + 15, paintY + 43, 15, 15);
			clr2 = new Rectangle(paintX + 32, paintY + 43, 15, 15);
			clr3 = new Rectangle(paintX + 49, paintY + 43, 15, 15);
			clr4 = new Rectangle(paintX + 66, paintY + 43, 15, 15);
			clr5 = new Rectangle(paintX + 83, paintY + 43, 15, 15);
			clr6 = new Rectangle(paintX + 100, paintY + 43, 15, 15);
			cr1 = new Rectangle(paintX + 15, paintY + 61, 15, 15);
			logOut = new Rectangle(paintX + 100, paintY + 79, 55, 15);
			// =========================================================//
			g.setFont(new Font(font, Font.PLAIN, 12));
			if (exitStage == 1) {
				g.setColor(new Color(0, 0, 0, 100));
				g.fillRect(logOut2.x, logOut2.y, logOut2.width, logOut2.height);
				g.setColor(Color.WHITE);
				g.drawString("Logout: Are you sure?", logOut2.x + 10, logOut2.y + 22);
				g.setColor(Color.RED);
				g.fillRect(logOutYes.x, logOutYes.y, logOutYes.width, logOutYes.height);
				g.setColor(Color.GREEN);
				g.fillRect(logOutNo.x, logOutNo.y, logOutNo.width, logOutNo.height);
				g.setColor(Color.BLACK);
				g.drawString("YES", logOutYes.x + 28, logOutYes.y + 14);
				g.drawString("NO", logOutNo.x + 29, logOutNo.y + 14);
			}

			runTime = System.currentTimeMillis() - time_ScriptStart;
			totalTime = formatTime((int) runTime);

			currentTab = paintTab();

			final NumberFormat formatter = new DecimalFormat("#,###,###");

			if (game.getClientState() == 10) {
				gainedEXP = skills.getCurrentExp(Skills.FIREMAKING) - start_exp;
				totalFires = (int) ((double) gainedEXP / exp[logId]);
				totalMoney = totalFires * priceGuide;
			}

			switch (currentTab) {
			case -1: // PAINT OFF
				g.setColor(hiddenPaint);
				g.fillRect(r1.x, r1.y, r1.width, r1.height);
				g.setColor(fonts);
				drawString(g, "O", r1, 5);
				break;
			case 0: // DEFAULT TAB - MAIN
				drawPaint(g, r2c);
				g.setColor(lines);
				g.drawLine(r.x + 204, r.y + 22, r.x + 204, r.y + 109);
				g.setColor(fonts);
				g.setFont(new Font(font, Font.BOLD, 14));
				drawString(g, properties.name(), r, -40);
				g.setFont(new Font(font, Font.PLAIN, 12));
				drawStringMain(g, "Runtime: ", totalTime, r, 20, 35, 0, true);
				if (isActive()) {
					drawStringMain(g, "", status, r, 20, 35, 0, false);
				} else {
					drawStringMain(g, "", "Script Paused", r, 20, 35, 0, false);
				}
				int firePerHour = 0;
				int moneyPerHour = 0;
				if (runTime / 1000 > 0) {
					firePerHour = (int) (3600000.0 / (double) runTime * totalFires);
					moneyPerHour = (int) (3600000.0 / (double) runTime * totalMoney);
				}
				drawStringMain(g, "Fires Made: ", formatter.format((long) totalFires), r, 20, 35, 2, true);
				drawStringMain(g, "Fires / Hour: ", formatter.format((long) firePerHour), r, 20, 35, 3, true);

				drawStringMain(g, "Money Lost: ", formatter.format((long) totalMoney), r, 20, 35, 2, false);
				drawStringMain(g, "Money / Hour: ", formatter.format((long) moneyPerHour), r, 20, 35, 3, false);
				break;
			case 1: // INFO
				drawPaint(g, r3c);
				g.setColor(lines);
				g.drawLine(r.x + 204, r.y + 22, r.x + 204, r.y + 109);
				g.setColor(fonts);
				g.setFont(new Font(font, Font.BOLD, 14));
				drawString(g, properties.name(), r, -40);
				g.setFont(new Font(font, Font.PLAIN, 12));
				drawStringMain(g, "Version: ", Double.toString(properties.version()), r, 20, 35, 0, true);
				if (foundType == true) {
					drawStringMain(g, "Amt of " + name[logId] + " in Bank:", "", r, 20, 35, 2, true);
					drawStringMain(g, "", formatter.format((long) bankCount), r, 20, 35, 3, true);
					drawStringMain(g, name[logId] + " Prices", "", r, 20, 35, 0, false);
					drawStringMain(g, "Price Guide:", Integer.toString(priceGuide)
							+ " coins", r, 20, 35, 2, false);
					drawStringMain(g, "Worth:", formatter.format((long) (bankCount * priceGuide)), r, 20, 35, 4, true);
				} else {
					drawStringMain(g, "Log prices not loaded!", "", r, 20, 35, 0, false);
				}
				break;
			case 2: // STATS
				drawPaint(g, r4c);
				g.setColor(lines);
				g.drawLine(r.x + 204, r.y + 43, r.x + 204, r.y + 109);
				drawStats(g);
				g.setColor(fonts);
				g.setFont(new Font(font, Font.BOLD, 14));
				drawString(g, properties.name(), r, -40);
				g.setFont(new Font(font, Font.PLAIN, 12));
				final int xpTL = skills.getExpToNextLevel(Skills.FIREMAKING);
				final int xpHour = (int) (3600000.0 / (double) runTime * gained_exp);
				final int TTL = (int) ((double) xpTL / (double) xpHour * 3600000);
				drawStringMain(g, "Current Level:", skills.getCurrentLevel(Skills.FIREMAKING)
						+ "", r, 20, 35, 2, true);
				drawStringMain(g, "Level Gained:", gained_lvl + " lvl", r, 20, 35, 3, true);
				drawStringMain(g, "Time to Lvl:", formatTime(TTL), r, 20, 35, 4, true);

				drawStringMain(g, "XP Gained:", formatter.format((long) gained_exp)
						+ "xp", r, 20, 35, 2, false);
				drawStringMain(g, "XP / Hour:", formatter.format((long) xpHour)
						+ "xp", r, 20, 35, 3, false);
				drawStringMain(g, "XP to Lvl:", formatter.format((long) xpTL)
						+ "xp", r, 20, 35, 4, false);
				break;
			case 3: // ETC
				drawPaint(g, r5c);
				g.setColor(lines);
				g.drawLine(r.x + 204, r.y + 22, r.x + 204, r.y + 109);
				g.setColor(fonts);
				g.setFont(new Font(font, Font.BOLD, 14));
				drawString(g, properties.name(), r, -40);
				g.setFont(new Font(font, Font.PLAIN, 12));
				if (useBreaking == true) {
					if (randomBreaking == true) {
						drawStringMain(g, "Break Distance:", "Random", r, 20, 35, 0, true);
						drawStringMain(g, "Break Length:", "Random", r, 20, 35, 1, true);
					} else {
						drawStringMain(g, "Break Distance:", Integer.toString(midTime)
								+ " ±" + Integer.toString(randTime), r, 20, 35, 0, true);
						drawStringMain(g, "Break Length:", Integer.toString(midLength)
								+ " ±" + Integer.toString(randLength), r, 20, 35, 1, true);
					}
					drawStringMain(g, "Next Break:", (String) formatTime((int) (nextBreak - System.currentTimeMillis())), r, 20, 35, 3, true);
					drawStringMain(g, "Break Length:", (String) formatTime((int) nextLength), r, 20, 35, 4, true);
				} else {
					drawStringMain(g, "Breaking is disabled!", "", r, 20, 35, 0, true);
				}
				drawStringMain(g, "Camera Turns:", Integer.toString(camTurned), r, 20, 35, 0, false);
				if (useChatRes) {
					drawStringMain(g, "Chat Response:", Integer.toString(resCount), r, 20, 35, 3, false);
				} else {
					drawStringMain(g, "Chat Responder is disabled!", "", r, 20, 35, 3, false);
				}
				if (useRemote) {
					drawStringMain(g, "Remote Control:", "Enabled", r, 20, 35, 4, false);
				} else {
					drawStringMain(g, "Remote Control is disabled!", "", r, 20, 35, 4, false);
				}
				break;
			case 4:
				drawPaint(g, r6c);
				g.setColor(lines);
				g.drawLine(r.x + 204, r.y + 22, r.x + 204, r.y + 109);
				g.setColor(fonts);
				g.setFont(new Font(font, Font.BOLD, 14));
				drawString(g, properties.name(), r, -40);
				g.setFont(new Font(font, Font.PLAIN, 12));
				g.setColor(Color.WHITE);
				g.drawString("Settings", paintX + 15, paintY + 31);
				if (useChatRes == true) {
					g.setColor(Color.GREEN);
					g.drawString("Chat Responder ON", cr1.x + 19, cr1.y + 13);
				} else {
					g.setColor(Color.RED);
					g.drawString("Chat Responder OFF", cr1.x + 19, cr1.y + 13);
				}
				g.setColor(new Color(0, 0, 0, 190));
				g.fillRect(clr1.x, clr1.y, clr1.width, clr1.height);
				g.fillRect(cr1.x, cr1.y, cr1.width, cr1.height);
				g.setColor(new Color(0, 0, 70, 190));
				g.fillRect(clr2.x, clr2.y, clr2.width, clr2.height);
				g.setColor(new Color(0, 70, 0, 190));
				g.fillRect(clr3.x, clr3.y, clr3.width, clr3.height);
				g.setColor(new Color(65, 0, 0, 190));
				g.fillRect(clr4.x, clr4.y, clr4.width, clr4.height);
				g.setColor(new Color(65, 0, 65, 190));
				g.fillRect(clr5.x, clr5.y, clr5.width, clr5.height);
				g.setColor(new Color(82, 41, 0, 190));
				g.fillRect(clr6.x, clr6.y, clr6.width, clr6.height);
				g.setColor(Color.WHITE);
				g.drawString("T", cr1.x + 4, cr1.y + 12);
				if (exitStage == 0) {
					g.setColor(new Color(0, 0, 0, 160));
					g.fillRect(logOut.x, logOut.y, logOut.width, logOut.height);
					g.setColor(Color.YELLOW);
					g.drawString("Log Out", logOut.x + 6, logOut.y + 12);
				}
				if (counter < 1) {
					g.setColor(new Color(0, 0, 0, 160));
					g.fillRect(logOut.x + 125, logOut.y, logOut.width + 53, logOut.height);
					g.setColor(Color.YELLOW);
					g.drawString("Take Screenshot", logOut.x + 131, logOut.y + 12);
				}
				break;
			}
			if (counter > 1) {
				proggiePaint(g);
			}
			if (counter == 400) {
				counter = 398;
			}
			if (counter < 398 && counter > 0) {
				counter -= 1;
			}
		}

		public int paintTab() {
			final Point mouse1 = new Point(p);
			if (mouseWatcher.isAlive()) {
				return currentTab;
			}
			if (thePainter.currentTab == 4 && game.isLoggedIn() == true) {
				if (clr1.contains(mouse1)) {
					colour = "Black";
					setColour();
				}
				if (clr2.contains(mouse1)) {
					colour = "Blue";
					setColour();
				}
				if (clr3.contains(mouse1)) {
					colour = "Green";
					setColour();
				}
				if (clr4.contains(mouse1)) {
					colour = "Red";
					setColour();
				}
				if (clr5.contains(mouse1)) {
					colour = "Purple";
					setColour();
				}
				if (clr6.contains(mouse1)) {
					colour = "Brown";
					setColour();
				}
				if (cr1.contains(mouse1)) {
					mouseWatcher = new Thread(new MouseWatcher(cr1));
					mouseWatcher.start();
					if (useChatRes == true) {
						useChatRes = false;
					} else {
						useChatRes = true;
					}
				}
			}
			if (r1.contains(mouse1)) {
				mouseWatcher = new Thread(new MouseWatcher(r1));
				mouseWatcher.start();
				if (currentTab == -1) {
					return lastTab;
				} else {
					lastTab = currentTab;
					return -1;
				}
			}
			if (currentTab == -1) {
				return currentTab;
			}
			if (r2.contains(mouse1)) {
				return 0;
			}
			if (r3.contains(mouse1)) {
				return 1;
			}
			if (r4.contains(mouse1)) {
				return 2;
			}
			if (r5.contains(mouse1)) {
				return 3;
			}
			if (r6.contains(mouse1)) {
				return 4;
			}
			return currentTab;
		}

		public void proggiePaint(final Graphics g) {
			final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
			final Calendar cal = Calendar.getInstance();
			final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
			final String date = sdf.format(cal.getTime());
			if (paintX <= 140 && paintY <= 86) {
				// If main paint is on original location of proggiepaint
				g.setFont(new Font(font, Font.PLAIN, 12));
				g.setColor(normalBack);
				g.fillRect(7, 235, 133, 76);
				g.setColor(fonts);
				g.drawString(date, 17, 257);
				g.drawString("Version " + Double.toString(properties.version()), 17, 279);
				g.drawString(name[logId], 17, 301);
			} else {
				g.setFont(new Font(font, Font.PLAIN, 12));
				g.setColor(normalBack);
				g.fillRect(7, 10, 133, 76);
				g.setColor(fonts);
				g.drawString(date, 17, 32);
				g.drawString("Version " + Double.toString(properties.version()), 17, 54);
				g.drawString(name[logId], 17, 76);
			}
		}

		public void saveStats() {
			if (skills.getCurrentLevel(Skills.FIREMAKING) != 0
					&& game.isLoggedIn()) {
				nf.setMinimumIntegerDigits(2);
				final int stats = Skills.FIREMAKING;
				start_exp = skills.getCurrentExp(stats);
				start_lvl = skills.getCurrentLevel(stats);
				savedStats = true;
			}
		}

		public void setColour() {
			if (colour.equals("Blue")) {
				fonts = Color.YELLOW;
				normalBack = new Color(0, 0, 70, 230);
				hiddenPaint = new Color(0, 0, 70, 120);
				lines = new Color(19, 51, 200, 200);
			} else if (colour.equals("Green")) {
				fonts = Color.YELLOW;
				normalBack = new Color(0, 70, 0, 230);
				hiddenPaint = new Color(0, 70, 0, 120);
				lines = new Color(19, 200, 51, 200);
			} else if (colour.equals("Red")) {
				fonts = Color.YELLOW;
				normalBack = new Color(65, 0, 0, 230);
				hiddenPaint = new Color(65, 0, 0, 120);
				lines = new Color(205, 0, 0, 200);
			} else if (colour.equals("Purple")) {
				fonts = new Color(255, 122, 224, 250);
				normalBack = new Color(65, 0, 65, 230);
				hiddenPaint = new Color(65, 0, 65, 120);
				lines = new Color(180, 0, 180, 200);
			} else if (colour.equals("Brown")) {
				fonts = new Color(51, 204, 0, 250);
				normalBack = new Color(82, 41, 0, 230);
				hiddenPaint = new Color(82, 41, 0, 120);
				lines = new Color(142, 91, 0, 200);
			} else {
				fonts = Color.WHITE;
				normalBack = new Color(0, 0, 0, 230);
				hiddenPaint = new Color(0, 0, 0, 130);
				lines = new Color(100, 100, 100, 200);
			}
		}
	}

	// ------------VARIABLES--------------\\
	final ScriptManifest properties = getClass().getAnnotation(ScriptManifest.class);
	WhiteBearPaint thePainter = new WhiteBearPaint();
	ChatResponder chatRes;
	Antiban antiban = new Antiban();
	AStar pf = new AStar();
	private final Properties WBini = new Properties();
	private int runEnergy = random(55, 88), lvlAmt = 0, minMS = 4, maxMS = 6,
			c = 0;
	private int priceGuide = 0, bankCount = 0;
	private int exitStage = 0, camTurned = 0, resCount = 0;
	private int midTime = 90, randTime = 120, midLength = 10, randLength = 20;
	private int counter = 0;
	private final int relogAfter = -1;
	private int gainedEXP = 0, totalFires = 0, totalMoney = 0;
	private long stopTime = -1, nextBreak = System.currentTimeMillis(),
			nextLength = 60000;
	private long endTime = System.currentTimeMillis(),
			nextRun = System.currentTimeMillis(), disallow = 0;
	private boolean unactivated = true, foundType = false, breakLogout = false;

	private boolean logOutInfo = false, tradeResponse = false,
			makeFire = false, laneActive = false;

	private boolean useChatRes = true, useFkeys = true, guiStart = false,
			chatResGUI = false;
	private boolean useBreaking = false, randomBreaking = false,
			checkUpdates = false;
	private final boolean checkDisallow = true;
	private boolean currentlyBreaking = false;
	private final boolean useRemote = false;
	private boolean doingRemote = false;
	private final boolean doRelog = false;
	private boolean logOutR = false, reloaded = false;
	private String totalTime = "00:00:00", status = "Loading...", colour,
			lastMessage = null;
	private static double[] exp = { 40, 60, 90, 105, 135, 157.5, 125, 193.5,
			202.5, 303.8 };

	private static int[] logs = { 1511, 1521, 1519, 6333, 1517, 6332, 10810,
			12581, 1515, 1513 };

	private static String[] name = { "Logs", "Oak logs", "Willow logs",
			"Teak logs", "Maple logs", "Mahogany logs", "Arctic Pine logs",
			"Eucalyptus logs", "Yew logs", "Magic logs" };

	private int logId = 0;

	private final int minimumLength = 3;

	private RSTile startLoc = null;

	private FireLane[] lanes = null;

	private FireLane use = null;

	private final Disallowed sTiles = new Disallowed(Configuration.Paths.getScriptCacheDirectory()
			+ File.separator + "sDisallowed.txt");

	private final Disallowed pTiles = new Disallowed(Configuration.Paths.getScriptCacheDirectory()
			+ File.separator + "pDisallowed.txt");

	// --------------METHODS---------------\\
	private void activate() {
		if (!game.isFixed()) {
			log.warning("Your screen size is not Fixed!");
			log.warning("The script will encounter problems if you don't change it to fixed!");
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final WhiteBearGUI gui = new WhiteBearGUI();
				gui.WhiteBearGUI.setVisible(true);
			}
		});
		while (!guiStart) {
			sleep(100);
		}
		startLoc = getMyPlayer().getLocation();
		if (checkDisallow) {
			URLConnection url = null;
			BufferedReader in = null;
			BufferedWriter out = null;
			try {
				url = new URL("http://dl.dropbox.com/u/15393327/sDisallowed.txt").openConnection();
				in = new BufferedReader(new InputStreamReader(url.getInputStream()));
				out = new BufferedWriter(new FileWriter(Configuration.Paths.getScriptCacheDirectory()
						+ File.separator + "sDisallowed.txt"));
				String inp;
				while ((inp = in.readLine()) != null) {
					out.write(inp);
					out.newLine();
					out.flush();
				}
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (final Throwable e) {
				log.info("Error occurred when downloading Disallowed Tiles data.");
				final String data[] = { "3163,3487,grand_exchange_start",
						"3164,3487", "3165,3487", "3166,3487", "3167,3488",
						"3167,3489", "3167,3490", "3167,3491", "3166,3492",
						"3165,3492", "3164,3492", "3163,3492", "3162,3491",
						"3162,3490", "3162,3489",
						"3162,3488,grand_exchange_end",
						"3179,3432,3194,3446,varrock_west_bank",
						"3250,3416,3257,3423,varrock_east_bank",
						"3249,3431,3257,3437,north_building_var_east_bank",
						"3258,3435,3260,3437,north_building_var_east_bank",
						"3272,3428,var_east_gate", "3272,3429,var_east_gate",
						"3091,3488,3098,3499,edgeville_bank",
						"3088,3240,3097,3246,dranor_village_bank",
						"3009,3353,3018,3358,falador_east_bank",
						"2327,3686,2332,3693,piscatoris_bank",
						"3265,3159,3272,3173,al_kharid_bank",
						"2609,3088,2616,3097,yanille_bank",
						"1709,5603,fist_of_guthix(start)", "1710,5602",
						"1717,5601", "1718,5600",
						"1702,5600,fist_of_guthix(end)",
						"3119,3496,3124,3496,edgeville_water(start)",
						"3117,3495,3123,3495", "3113,3494,3121,3494",
						"3111,3493,3119,3493", "3110,3492,3117,3492",
						"3109,3490,3113,3491", "3108,3488,3111,3489",
						"3107,3487,3110,3487", "3106,3485,3109,3486",
						"3106,3484,3108,3484,edgeville_water(end)",
						"3172,3447,var_west_block" };
				try {
					out = new BufferedWriter(new FileWriter(Configuration.Paths.getScriptCacheDirectory()
							+ File.separator + "sDisallowed.txt"));
					for (final String s : data) {
						out.write(s);
						out.newLine();
						out.flush();
					}
					if (out != null) {
						out.close();
					}
				} catch (final Throwable t) {
				}
			}
		}
		sTiles.loadData();
		pTiles.loadData();
		thePainter.setColour();
		if (stopTime > 0) {
			log.info("Script will stop after "
					+ thePainter.formatTime((int) stopTime));
			final long stoppingTime = stopTime + System.currentTimeMillis();
			stopTime = stoppingTime;
		}
		if (checkUpdates) {
			URLConnection url2 = null;
			BufferedReader in2 = null;
			try {
				url2 = new URL("http://dl.dropbox.com/u/15393327/Updater/firemaker.txt").openConnection();
				in2 = new BufferedReader(new InputStreamReader(url2.getInputStream()));
				final String ver = in2.readLine();
				final String link = in2.readLine();
				final String update = in2.readLine();
				if (Double.parseDouble(ver) > properties.version()) {
					log.warning("Latest version: " + ver
							+ "! Please update the script!");
					log.info("In this update: " + update);
					log.info("Download from: " + link);
				} else if (Double.parseDouble(ver) < properties.version()) {
					log.info("You are using a beta version of this script!");
				} else {
					log.info("You are using the latest version of this script!");
				}
				if (in2 != null) {
					in2.close();
				}
			} catch (final java.lang.Exception e) {
				log.warning("An error occurred while checking for update!");
			}
		}
		antiban.breakingNew();
		boolean per = antiban.personalize();
		while (!per) {
			per = antiban.personalize();
		}
		if (!game.isLoggedIn()) {
			log.warning("You should start the script logged in!");
		}
		thePainter.time_ScriptStart = System.currentTimeMillis();
	}

	/**
	 * Checks if there are any objects on the next firemaking tile.
	 * 
	 * @return if there is object on next firemaking tile
	 */
	private boolean canFiremake() {
		final RSTile loc = getMyPlayer().getLocation();
		if (laneActive) {
			if (makeFire && getMyPlayer().isMoving()
					|| objects.getTopAt(loc, 1) == null) {
				return true;
			} else {
				use = null;
				return false;
			}
		} else {
			if (objects.getTopAt(new RSTile(use.uStart, use.y), 1) != null) {
				use = null;
				return false;
			}
			if (loc.getX() != use.uStart || loc.getY() != use.y) {
				return false;
			}
		}
		return true;
	}

	private void createLanes() {
		status = "Calculating";
		log.info("--- Please wait while I generate the firemaking lanes. ---");
		pf.init(game, walking);
		final LinkedList<FireLane> fireLaneList = new LinkedList<FireLane>();
		final int locX = getMyPlayer().getLocation().getX();
		final int locY = getMyPlayer().getLocation().getY();
		final int lowY = locY - 11;
		final int highY = locY + 11;
		int Tcount = 0, Tlength = 0, Tcost = 0;
		for (int y = lowY; y <= highY; y++) {
			int uStart = -1, uEnd = -1, uLength = 0;
			int cost = -1;
			int tStart = -1, tLength = 0;
			boolean cont = false;
			final int lowX = locX - 22;
			final int highX = locX + 22;
			for (int x = highX; x >= lowX; x--) {
				final RSTile t = new RSTile(x, y);
				final RSTile[] path = pf.findPath(getMyPlayer().getLocation(), t);
				if (path != null && !sDisallowed(t)) {
					if (!cont) {
						cost = path.length;
						tLength = 1;
						tStart = x;
						cont = true;
					} else {
						if (pf.canMoveEast(t)) {
							tLength++;
						} else {
							if (tLength > uLength
									&& isInLane(x + 1, tStart, locX - 10, locX + 10)) {
								uLength = tLength;
								uEnd = x + 1;
								uStart = tStart;
								Tcount++;
								Tcost += cost;
								Tlength += uLength;
							}
							tStart = x;
							tLength = 1;
						}
					}
					if (x == lowX) {
						cont = false;
						if (tLength > uLength
								&& isInLane(x + 1, tStart, locX - 10, locX + 10)) {
							uLength = tLength;
							uEnd = x + 1;
							uStart = tStart;
							Tcount++;
							Tcost += cost;
							Tlength += uLength;
						}
						tLength = 0;
					}
				} else {
					if (cont) {
						cont = false;
						if (tLength > uLength
								&& isInLane(x + 1, tStart, locX - 10, locX + 10)) {
							uLength = tLength;
							uEnd = x + 1;
							uStart = tStart;
							Tcount++;
							Tcost += cost;
							Tlength += uLength;
						}
						tLength = 0;
					}
				}
			}
			if (uLength > minimumLength) {
				fireLaneList.add(new FireLane(y, uStart, uEnd, cost));
			}
		}
		if (fireLaneList.size() == 0) {
			logOutR = true;
			log.warning("Unable to find any lanes suitable for firemaking, logging out.");
		} else {
			lanes = fireLaneList.toArray(new FireLane[fireLaneList.size()]);
			log.info("--- Found " + Tcount + " lanes, average length: "
					+ Tlength / Tcount + ", average cost: " + Tcost / Tcount
					+ " ---");
		}
	}

	private int doAction() {
		switch (getState()) {
		case Generate:
			laneActive = false;
			makeFire = false;
			use = null;
			for (final FireLane fl : lanes) {
				fl.scan();
			}
			final int min = inventory.getCount(logs[logId]);
			FireLane toUse = null;
			int uCost = 999,
			tCost = 999;
			for (final FireLane l : lanes) {
				if (toUse == null) {
					toUse = l;
					uCost = l.cost;
					continue;
				}
				tCost = l.cost;
				if (l.uLength >= min) { // length is greater than amt of logs in
										// inventory
					if (tCost < uCost) { // new tile costs less
						uCost = tCost;
						toUse = l;
					} else if (toUse.length() >= min) {
						// ignore
					} else { // current tile costs less
						final int costDiff = uCost - tCost; // how much is the
															// toUse more cheap
						final int lengthDiff = l.uLength - toUse.uLength; // how
																			// much
																			// longer
																			// is
																			// l
						if (lengthDiff > costDiff) {
							uCost = tCost;
							toUse = l;
						}
					}
				} else {
					if (l.uLength > toUse.uLength) { // new length is longer
						final int lengthDiff = l.uLength - toUse.uLength;
						final int costDiff = tCost - uCost;
						if (lengthDiff >= costDiff) {
							uCost = tCost;
							toUse = l;
						}
					} else { // current length is longer
						if (l.uLength > 3) {
							final int lengthDiff = toUse.uLength - l.uLength;
							final int costDiff = tCost - uCost;
							if (costDiff > lengthDiff + 1) {
								uCost = tCost;
								toUse = l;
							}
						}
					}
				}
			}
			if (toUse != null) {
				if (toUse.uLength - 1 > min) {
					final int reduceable = (toUse.uLength - min) / 2;
					final RSTile test = new RSTile(toUse.uStart - reduceable, toUse.y);
					final int testDiff = test.getX() - startLoc.getX();
					final int startDiff = toUse.uStart - startLoc.getX();
					if (startDiff >= 0) {
						if (testDiff < startDiff && calc.canReach(test, false)) {
							toUse.uStart -= reduceable;
						}
					}
				}
				use = toUse;
			}
			return 10;
		case Move:
			makeFire = false;
			if (!onTile(new RSTile(use.uStart, use.y), "Walk here", 0.5, 0.5, 0)) {
				if (!walking.walkTileMM(new RSTile(use.uStart, use.y), 1, 1)) {
					walking.walkTo(new RSTile(use.uStart, use.y));
				}
			}
			sleep(random(1000, 1200));
			while (valid() && getMyPlayer().isMoving()) {
				sleep(random(45, 70));
				if (random(0, 2) == 0) {
					antiban.main(false);
				}
			}
			return 10;
		case Firemake:
			makeFire = false;
			if (interfaces.get(740).isValid()) {
				interfaces.get(740).getComponent(3).doClick();
				sleep(random(550, 700));
				return 200;
			}
			if (getMyPlayer().getAnimation() != -1) {
				return random(100, 300);
			}
			if (objects.getTopAt(getMyPlayer().getLocation(), 1) != null) {
				sleep(random(100, 160));
			}
			if (objects.getTopAt(getMyPlayer().getLocation(), 1) != null) {
				sleep(random(130, 200));
			}
			if (!laneActive) {
				laneActive = true;
			}
			final RSComponent next = getNextLog();
			if (use.isHovering()) {
				if (use.hovering.getComponentID() == logs[logId]) {
					mouse.click(true);
				} else {
					inventory.getItem(logs[logId]).getComponent().doClick();
				}
				sleep(random(150, 250));
				use.hovering = null;
			} else {
				use.hovering = null;
				if (inventory.getSelectedItem() != null
						&& inventory.getSelectedItem().getID() == logs[logId]) {
					if (!inventory.getItem(590).doAction("Use " + name[logId])) {
						return 100;
					}
					sleep(random(100, 200));
				} else {
					if (inventory.getSelectedItem() == null
							|| inventory.getSelectedItem().getID() != 590) {
						if (!inventory.getItem(590).doAction("Use Tinderbox")) {
							return 100;
						}
						sleep(random(90, 150));
					}
					if (inventory.getSelectedItem() == null
							|| inventory.getSelectedItem().getID() != logs[logId]) {
						if (!inventory.getItem(logs[logId]).doAction("Use")) {
							return 100;
						}
						sleep(random(300, 500));
					}
				}
			}
			c = 0;
			// Wait until player stops moving
			while (valid() && c < 21) {
				c++;
				sleep(50);
				if (!getMyPlayer().isMoving()) {
					break;
				}
				if (interfaces.get(740).isValid()) {
					return 10;
				}
				if (objects.getTopAt(getMyPlayer().getLocation(), 1) != null) {
					break;
				}
				if (disallow + 2000 > System.currentTimeMillis()) {
					disallow = 0;
					use = null;
					makeFire = false;
					return 100;
				}
			}
			if (use.hasNext()) {
				// Hovering
				if (inventory.getSelectedItem() == null) {
					if (next != null) {
						if (inventory.getItem(590).doAction("Use Tinderbox")) {
							sleep(random(100, 200));
							next.doHover();
							use.hovering = next;
						}
					}
				}
			}
			c = 0;
			// Wait until player finish firemaking (check for movement)
			while (valid() && c < 121) {
				if (!makeFire) {
					makeFire = true;
				}
				c++;
				sleep(50);
				if (getMyPlayer().isMoving()) {
					break;
				}
				if (interfaces.get(740).isValid()) {
					return 10;
				}
				if (objects.getTopAt(getMyPlayer().getLocation(), 1) != null) {
					break;
				}
				if (disallow + 2000 > System.currentTimeMillis()) {
					disallow = 0;
					use = null;
					makeFire = false;
					return 100;
				}
			}
			if (makeFire && inventory.contains(logId)
					&& getMyPlayer().isMoving()
					|| objects.getTopAt(getMyPlayer().getLocation(), 1) == null) {
				return 1;
			}
			if (getMyPlayer().isMoving()) {
				sleep(random(250, 400));
			}
			if (!inventory.contains(logs[logId])) {
				if (random(0, 3) == 0) {
					antiban.main(true);
				}
			}
			return random(100, 200);
		case Bank:
			laneActive = false;
			makeFire = false;
			use = null;
			doBank();
			return random(100, 200);
		case GoBank:
			laneActive = false;
			makeFire = false;
			if (!onTile(startLoc, "Walk here", 0.5, 0.5, 0)) {
				if (!walking.walkTileMM(startLoc)) {
					walking.walkTo(startLoc);
				}
			}
			sleep(random(1000, 1200));
			while (valid() && getMyPlayer().isMoving()) {
				sleep(random(45, 70));
				if (random(0, 2) == 0) {
					if (!antiban.lookAway()) {
						antiban.main(false);
					}
				}
			}
			return 10;
		case Antiban:
			antiban.main(false);
			return random(45, 75);
		}
		return 100;
	}

	private void doBank() {
		try {
			if (!bank.isOpen()) {
				bank.open();
			}
			if (bank.isOpen()) {
				if (inventory.getCount(590) > 1) {
					bank.deposit(590, 0);
					sleep(random(350, 500));
					bank.withdraw(590, 1);
				}
				bank.depositAllExcept(new int[] { 590, logs[logId] });
				bankCount = bank.getCount(logs[logId]);
				sleep(random(50, 100));
				c = 0;

				// Check for logs
				if (bank.getCount(logs[logId]) == 0) {
					if (bank.getCount(logs[logId]) == 0) {
						sleep(random(220, 450));
						if (!bank.isOpen()) {
							return;
						}
						if (bank.getCount(logs[logId]) == 0) {
							sleep(random(220, 450));
							if (!bank.isOpen()) {
								return;
							}
							if (bank.getCount(logs[logId]) == 0) {
								log.warning("You have ran out of logs! Logging out.");
								logOutR = true;
								return;
							}
						}
					}
				}
				while (valid() && c < 5) {
					c++;
					if (inventory.contains(590)
							&& inventory.contains(logs[logId])) {
						break;
					}
					if (!inventory.contains(590)) {
						bank.withdraw(590, 1);
					} else {
						bank.withdraw(logs[logId], 0);
					}
					for (int i = 0; i < 20 + c * 3; i++) {
						sleep(50);
						if (bank.getCount(logs[logId]) != bankCount) {
							c = 10;
							i = 55;
							continue;
						}
					}
				}
				bankCount = bank.getCount(logs[logId]);
			}
		} catch (final Exception e) {
		}
	}

	private boolean doLogOut(final boolean toLobby, final boolean stopScript) {
		status = "Logging out";
		while (bank.isOpen()) {
			bank.close();
			mouse.move(random(10, 430), random(10, 465));
			sleep(random(200, 400));
		}
		while (!game.isOnLogoutTab()) {
			mouse.move(random(game.getWidth() - 15, game.getWidth() - 5), random(5, 16));
			mouse.click(true);
			if (bank.isOpen()) {
				bank.close();
			}
			int timesToWait = 0;
			while (!game.isOnLogoutTab() && timesToWait < 5) {
				sleep(random(200, 400));
				timesToWait++;
			}
		}
		final int maximum = 0;
		while (game.isLoggedIn() == true && maximum < 20) {
			if (toLobby) {
				interfaces.get(182).getComponent(2).doClick();
			} else {
				interfaces.get(182).getComponent(6).doClick();
			}
			sleep(1000);
		}
		if (!toLobby && stopScript) {
			stopScript(false);
		}
		return true;
	}

	/**
	 * Checks if the current fire lane can be used
	 * 
	 * @return if fire lane passed check, false if use is null
	 */
	private boolean fireLaneCheck() {
		if (use == null) {
			return false;
		}
		final int min = use.uStart - use.uEnd + 1;
		int amt = 0;
		if (!laneActive) {
			for (int x = use.uStart; x >= use.uEnd; x--) {
				final RSTile t = new RSTile(x, use.y);
				if (objects.getTopAt(t, 1) == null) {
					amt++;
				}
			}
		} else {
			return makeFire && getMyPlayer().isMoving()
					|| objects.getTopAt(getMyPlayer().getLocation(), 1) == null;
		}
		return amt == min;
	}

	private RSComponent getNextLog() {
		RSComponent first = null;
		int firstID = -1;
		for (int i = 0; i < 28; i++) {
			final RSComponent test = interfaces.get(149).getComponent(0).getComponent(i);
			if (test.getComponentID() == logs[logId]) {
				if (first == null) {
					first = test;
					firstID = i;
				} else {
					if (firstID + 1 == i) {
						return test;
					} else if (use.isHovering()) {
						return first;
					} else {
						return test;
					}
				}
			}
		}
		return null;
	}

	private State getState() {
		if (useFkeys && game.getCurrentTab() != 4) {
			keyboard.pressKey((char) KeyEvent.VK_F1);
			sleep(random(50, 110));
			keyboard.releaseKey((char) KeyEvent.VK_F1);
			sleep(random(160, 220));
		}
		if (inventory.contains(590) && inventory.contains(logs[logId])) {
			if (fireLaneCheck()) {
				if (canFiremake()) {
					status = "Firemaking";
					return State.Firemake;
				} else {
					status = "Walking";
					return State.Move;
				}
			} else {
				status = "Scanning Lanes";
				return State.Generate;
			}
		} else {
			/* need to bank */
			if (calc.distanceTo(startLoc) < 2) {
				status = "Banking";
				return State.Bank;
			} else {
				status = "Going to Bank";
				return State.GoBank;
			}
		}
	}

	private boolean isInLane(final int minX, final int maxX, final int checkMX,
			final int checkMY) {
		for (int x = minX; x <= maxX; x++) {
			if (x >= checkMX && x <= checkMY) {
				return true;
			}
		}
		return false;
	}

	// ------------MAIN LOOP--------------\\
	public int loop() {
		try {
			mouse.setSpeed(random(minMS, maxMS));
			if (unactivated) {
				activate();
				unactivated = false;
			}
			if (game.getClientState() != 10) {
				interfaces.getComponent(976, 6).doClick();
				env.enableRandom("Login");
				return random(50, 100);
			}
			env.disableRandom("Login");
			if (counter == 398) {
				env.saveScreenshot(false);
				counter = 397;
			}
			if (logOutR || exitStage == 2) {
				doLogOut(false, true);
				return 1000;
			}
			if (stopTime > 0 && System.currentTimeMillis() > stopTime) {
				log("Stop Time Reached. Logging off in 5 seconds.");
				sleep(random(4950, 5600));
				doLogOut(false, true);
			}
			if (doingRemote) {
				if (doRelog) {
					endTime = System.currentTimeMillis() + relogAfter * 60
							* 1000;
					doLogOut(false, false);
					while (endTime > System.currentTimeMillis()) {
						sleep(100);
					}
					doingRemote = false;
				} else {
					doLogOut(false, true);
					return 100;
				}
			}
			if (antiban.breakingCheck() && useBreaking) {
				final long endTime = System.currentTimeMillis() + nextLength;
				currentlyBreaking = true;
				if (breakLogout) {
					doLogOut(true, false);
				}
				log("Taking a break for "
						+ thePainter.formatTime((int) nextLength));
				while (System.currentTimeMillis() < endTime
						&& currentlyBreaking == true) {
					sleep(1000);
				}
				currentlyBreaking = false;
				antiban.breakingNew();
				return 10;
			}

			if (!thePainter.savedStats) {
				RSTile loc = null;
				if (npcs.getNearest(Bank.NPC_BANKERS) != null) {
					loc = npcs.getNearest(Bank.NPC_BANKERS).getLocation();
				}
				if (loc == null || calc.distanceTo(loc) > 3) {
					if (objects.getNearest(Bank.OBJECT_BANKS) != null) {
						loc = objects.getNearest(Bank.OBJECT_BANKS).getLocation();
					}
				}
				if (loc == null) {
					log.warning("Unable to find bank NPC/object! Start me next to a bank!");
					logOutR = true;
					return 1;
				} else if (calc.distanceTo(loc) > 3) {
					log.warning("Bank more than 3 tiles away! Start me next to a bank!");
					logOutR = true;
					return 1;
				}
				camera.setPitch(true);
				thePainter.saveStats();
				createLanes();
				if (lanes != null) {
					for (final FireLane fl : lanes) {
						fl.scan();
					}
				}
				return 100;
			}

			// interface checks
			interfaces.getComponent(741, 9).doClick();
			interfaces.getComponent(620, 18).doClick();
			interfaces.getComponent(109, 13).doClick();
			interfaces.getComponent(335, 19).doClick();

			if (thePainter.runTime > 32400000 && !reloaded || !foundType) {
				status = (foundType ? "Rel" : "L") + "oading Prices";
				final GEItem it = grandExchange.lookup(logs[logId]);
				priceGuide = it.getGuidePrice();
				if (foundType) {
					reloaded = true;
				}
				foundType = true;
			}
			if (game.getPlane() == 1 && playerInArea(3257, 3423, 3250, 3416)) {// varUP
				if (onTile(new RSTile(3256, 3421), "Climb", 0.5, 0.5, 0)) {
					log("[Antistuck] Trying to climb back down the stairs.");
					sleep(random(1500, 2000));
					while (valid() && getMyPlayer().isMoving()) {
						sleep(random(90, 110));
					}
					sleep(random(1500, 2000));
				}
				return random(50, 100);
			}
			try {
				final RSPlayer modC = antiban.getNearbyMod();
				if (modC != null) {
					if (System.currentTimeMillis() < chatRes.nextModAlert) {
						chatRes.nextModAlert += 150000;
						log.warning("[MOD] There is a Moderator nearby! Name: "
								+ modC.getName());
					}
				}
			} catch (final Exception e) {
			}

			startRunning(runEnergy);
			return doAction();
		} catch (final java.lang.Throwable t) {
			return 100;
		}
	}

	// --------------SERVER MSG------------\\
	public void messageReceived(final MessageEvent arg0) {
		try {
			final String serverString = arg0.getMessage();
			if (arg0.getID() > 0 && arg0.getID() < 10) {
				return;
			}
			if (serverString.contains("You've just advanced")) {
				if (lvlAmt == 0) {
					log("[Alert] You have just leveled, thanks to White Bear AIO Firemaker!");
				} else {
					if (random(1, 3) == 1) {
						log("[Alert] Another level by White Bear AIO Firemaker!");
					} else {
						log("[Alert] Congratulations! Your Firemaking skill have levelled!");
					}
				}
				lvlAmt += 1;
			}
			if (serverString.contains("Oh dear")) {
				log.severe("[Alert] You were killed! Aborting script!");
				logOutR = true;
			}
			if (serverString.contains("wishes to trade with you")) {
				tradeResponse = true;
			}
			if (serverString.contains("can't light")) {
				if (getMyPlayer().isMoving()) {
					return;
				}
				final RSTile t = getMyPlayer().getLocation();
				if (objects.getTopAt(t, 1) != null) {
					disallow = System.currentTimeMillis();
					return;
				}
				disallow = System.currentTimeMillis();
				pTiles.addTile(t);
				log.info("Added to disallowed tile: (" + t.getX() + ","
						+ t.getY() + ").");
			}
			if (serverString.contains("System update in")) {
				log.warning("There will be a system update soon, so we logged out");
				logOutR = true;
			}
		} catch (final Throwable t) {
		}
	}

	// -----------MOUSE LISTENER-----------\\
	public void mouseClicked(final MouseEvent arg0) {
	}

	public void mouseDragged(final MouseEvent arg0) {
		final Point p = arg0.getPoint();
		processPaint(p);
	}

	public void mouseEntered(final MouseEvent arg0) {
	}

	public void mouseExited(final MouseEvent arg0) {
	}

	public void mouseMoved(final MouseEvent e) {
		thePainter.p = e.getPoint();
	}

	public void mousePressed(final MouseEvent e) {
		final Point p = e.getPoint();
		processPaint(p);

		if (thePainter.logOutYes.contains(p) && exitStage == 1) {
			exitStage = 2;
			if (logOutInfo == false) {
				log("You will be logged out when the current loop ends (i.e. in a while)");
				logOutInfo = true;
			}
		}
		if (thePainter.logOutNo.contains(p) && exitStage == 1) {
			exitStage = 0;
		}
		if (thePainter.logOut.contains(p) && exitStage == 0
				&& thePainter.currentTab == 4) {
			thePainter.currentTab = 0;
			exitStage = 1;
		}
		if (thePainter.currentTab == 4
				&& counter == 0
				&& new Rectangle(thePainter.logOut.x + 125, thePainter.logOut.y, thePainter.logOut.width + 53, thePainter.logOut.height).contains(p)) {
			thePainter.currentTab = 0;
			counter = 400;
		}
	}

	public void mouseReleased(final MouseEvent arg0) {
	}

	// --------------ON FINISH-------------\\
	public void onFinish() {
		env.enableRandom("Login");
		counter = 405;
		chatRes.run = false;
		if (game.isLoggedIn() && exitStage < 2 && thePainter.runTime >= 4000000) {
			env.saveScreenshot(false);
		}
		log.info("In just " + totalTime + ", you made " + totalFires
				+ " fires, costing you " + totalMoney + " coins.");
	}

	// --------------ON REPAINT------------\\
	public void onRepaint(final Graphics g) {
		try {
			if (game.isLoggedIn()) {
				final Rectangle nameBlock = new Rectangle(interfaces.get(137).getComponent(54).getAbsoluteX(), interfaces.get(137).getComponent(54).getAbsoluteY() + 2, 89, 13);
				g.setColor(new Color(211, 192, 155, 253));
				try {
					g.fillRect(nameBlock.x, nameBlock.y, nameBlock.width, nameBlock.height);
				} catch (final Exception e) {
				}
			}
			if (use != null) {
				use.paint(g, true);
			}
			g.setFont(new Font("sansserif", Font.PLAIN, 12));
			if (thePainter.antialias == true) {
				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			}
			if (currentlyBreaking) {
				g.setColor(new Color(0, 0, 0));
				g.setColor(Color.BLACK);
				g.setFont(new Font("sansserif", Font.BOLD, 14));
				g.drawString("Currently taking a break!", 10, 30);
			}

			// Paints the Mouse Location
			final Point mousey = mouse.getLocation();
			final int x = (int) mousey.getX();
			final int y = (int) mousey.getY();
			if (System.currentTimeMillis() - mouse.getPressTime() < 900) {
				g.setColor(new Color(255, 0, 0, 170));
			} else {
				g.setColor(new Color(255, 0, 0, 75));
			}
			g.drawLine(x, 0, x, game.getHeight());
			g.drawLine(0, y, game.getWidth(), y);
			g.fillRect(x - 1, y - 1, 3, 3);
		} catch (final Throwable t) {
		}
		try {
			if (thePainter.savedStats == true && game.getClientState() == 10) {
				thePainter.paint(g);
			}
		} catch (final Throwable e) {
		}
	}

	// --------------ON START--------------\\
	public boolean onStart() {
		chatRes = new ChatResponder();
		chatRes.start();
		env.disableRandom("Login");
		return true;
	}

	private boolean onTile(final RSTile tile, final String action,
			final double dx, final double dy, final int height) {
		Point checkScreen;
		try {
			checkScreen = calc.tileToScreen(tile, dx, dy, height);
			if (!calc.pointOnScreen(checkScreen)) {
				if (calc.distanceTo(tile) <= 8) {
					if (getMyPlayer().isMoving()) {
						return false;
					}
					walking.walkTileMM(tile);
					walking.sleep(1000);
					return false;
				}
				return false;
			}
		} catch (final Throwable e) {
		}
		try {
			boolean stop = false;
			for (int i = 0; i <= 50; i++) {
				checkScreen = calc.tileToScreen(tile, dx, dy, height);
				if (!calc.pointOnScreen(checkScreen)) {
					return false;
				}
				mouse.move(checkScreen);
				final Object[] menuItems = menu.getItems();
				for (final Object menuItem : menuItems) {
					if (menuItem.toString().toLowerCase().contains(action.toLowerCase())) {
						stop = true;
						break;
					}
				}
				if (stop) {
					break;
				}
			}
		} catch (final Throwable e) {
		}
		try {
			return menu.doAction(action);
		} catch (final Throwable e) {
		}
		return false;
	}

	private boolean pDisallowed(final RSTile tile) {
		if (pTiles.tiles != null) {
			for (final RSTile t : pTiles.tiles) {
				if (tile.getX() == t.getX() && tile.getY() == t.getY()) {
					return true;
				}
			}
		}
		if (pTiles.areas != null) {
			for (final Area a : pTiles.areas) {
				if (a.inArea(tile)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean playerInArea(final int maxX, final int maxY,
			final int minX, final int minY) {
		final int x = getMyPlayer().getLocation().getX();
		final int y = getMyPlayer().getLocation().getY();
		if (x >= minX && x <= maxX && y >= minY && y <= maxY) {
			return true;
		}
		return false;
	}

	private void processPaint(final Point mouse) {
		final Point p = mouse;
		final int totalWidth = 408, totalHeight = 135, moveHeight = 114;
		final int mouseX = p.x;
		final int mouseY = p.y;
		if (game.getClientState() == 10 && mouseX >= thePainter.paintX
				&& mouseX <= thePainter.paintX + totalWidth
				&& mouseY >= thePainter.paintY
				&& mouseY <= thePainter.paintY + moveHeight) {
			if (thePainter.currentTab != -1 && thePainter.currentTab != 4) {
				thePainter.paintX = mouseX - totalWidth / 2;
				thePainter.paintY = mouseY - totalHeight / 2;
			}
		}
		if (thePainter.paintX < 4) {
			thePainter.paintX = 4;
		}
		if (thePainter.paintY < 4) {
			thePainter.paintY = 4;
		}
		if (thePainter.paintX + totalWidth > 761) {
			thePainter.paintX = 761 - totalWidth;
		}
		if (thePainter.paintY + totalHeight > 494) {
			thePainter.paintY = 494 - totalHeight;
		}
	}

	private boolean sDisallowed(final RSTile tile) {
		if (sTiles.tiles != null) {
			for (final RSTile t : sTiles.tiles) {
				if (tile.getX() == t.getX() && tile.getY() == t.getY()) {
					return true;
				}
			}
		}
		if (sTiles.areas != null) {
			for (final Area a : sTiles.areas) {
				if (a.inArea(tile)) {
					return true;
				}
			}
		}
		return false;
	}

	private void startRunning(final int energy) {
		if (nextRun < System.currentTimeMillis()
				&& walking.getEnergy() >= energy && !walking.isRunEnabled()) {
			nextRun = System.currentTimeMillis() + 7000;
			runEnergy = random(40, 95);
			walking.setRun(true);
			sleep(random(400, 600));
		}
	}

	private boolean valid() {
		return game.isLoggedIn();
	}
}
