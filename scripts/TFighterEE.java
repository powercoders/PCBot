import org.rsbot.Configuration;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Skills;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

/**
 * Change log: v1.61: Made SDN compatible v1.60: Stable: Potions and ash
 * scattering added. v1.55: Unstable: Potions and ash scattering added. v1.50:
 * Project branched by Zalgo2462. Bone burial added. v1.02: Includes click here
 * to continue hotfix. v1.01: Prioritize looting over combat. Safespotting +
 * looting is now possible. Implemented new LoopAction structure internally.
 * v1.00: Milestone release. Added: -Safespot ability -Central clicking
 * -Clicking continue -Antiban - performs camera + mouse at same time!
 * (sometimes) -Declared stable v0.96: Hopefully finally fixed food v0.95: Small
 * error, caused null pointer v0.94: Fixed eating. v0.93: Hide paint by clicking
 * it. v0.92: Loot support, mainly. Many small changes. v0.91: Oops, forgot to
 * add mouse speed settings! v0.9: Initial release
 */
@ScriptManifest(name = "TFighterEE", authors = {"!@!@!", "Zalgo2462"}, version = 1.61, description = "TFighter by !@!@! with additions by Zalgo2462", website = "http://www.powerbot.org/vb/showthread.php?t=477661")
public class TFighterEE extends Script implements PaintListener, MouseListener {

	private class AttackLoop implements LoopAction {

		public boolean activate() {
			return !u.npcs.isInCombat();
		}

		public int loop() {
			final RSNPC inter = u.npcs.getInteracting();
			final RSNPC n = inter != null ? inter : u.npcs.getNPC();
			if (n != null) {
				final int result = u.npcs.clickNPC(n, "Attack " + n.getName());
				if (result == 0) {
					if (!useSafespot) {
						waitWhileMoving();
					} else {
						waitForAnim();
					}
					return random(300, 500);
				} else if (result == 1) {
					waitWhileMoving();
					return random(0, 200);
				}
			} else {
				if (calc.distanceTo(startTile) > 5) {
					walking.walkTileMM(walking.getClosestTileOnMap(startTile));
					waitWhileMoving();
				} else {
					antiban();
				}
			}
			return random(50, 200);
		}

	}

	private class BonesLoop implements LoopAction {
		private final int[] BONE_IDS = new int[]{526, 528, 530, 532, 534,
				536, 2859, 3123, 3125, 3183, 6182, 20268, 20266, 20264};

		public boolean activate() {
			return inventory.getCount(BONE_IDS) != 0;
		}

		public int loop() {
			for (final RSItem item : inventory.getItems(BONE_IDS)) {
				item.doClick(true);
				waitForInvChange(inventory.getCount(true));
				return random(20, 150);
			}
			return random(50, 200);
		}

	}

	private class Eating {

		private final int[] B2P_TAB_ID = new int[]{8015};
		private final int[] BONES_ID = new int[]{526, 532, 530, 528, 3183,
				2859};

		private int toEatAtPercent = getRandomEatPercent();

		/**
		 * Breaks a B2P tab.
		 */
		private void breakB2pTab() {
			final RSItem i = inventory.getItem(B2P_TAB_ID);
			if (i != null) {
				i.doClick(true);
			}
		}

		/**
		 * Attempts to eat food.
		 *
		 * @return True if we ate.
		 */
		private boolean eatFood() {
			final RSItem i = getFood();
			for (int j = 0; j < 3; j++) {
				if (i == null) {
					break;
				}
				if (i.doAction("Eat")) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Finds food based on inventory actions.
		 *
		 * @return The RSItem of food, or null if none was found.
		 */
		private RSItem getFood() {
			for (final RSItem i : inventory.getItems()) {
				if (i == null || i.getID() == -1) {
					continue;
				}
				if (i.getComponent().getActions() == null
						|| i.getComponent().getActions()[0] == null) {
					continue;
				}
				if (i.getComponent().getActions()[0].contains("Eat")) {
					return i;
				}
			}
			return null;
		}

		/**
		 * Returns an integer representing the current health percentage.
		 *
		 * @return The current health percentage.
		 */
		public int getHPPercent() {
			try {
				return (int) (Integer.parseInt(interfaces.get(748).getComponent(8).getText().trim())
						/ (double) (skills.getRealLevel(Skills.CONSTITUTION) * 10) * 100);
			} catch (final Exception e) {
				return 100;
			}
		}

		/**
		 * Returns a random integer of when to eat.
		 *
		 * @return A random integer of the percent to eat at.
		 */
		private int getRandomEatPercent() {
			return random(45, 60);
		}

		/**
		 * Checks if we have at least one B2P tab.
		 *
		 * @return True if we have a tab.
		 */
		private boolean haveB2pTab() {
			return inventory.getCount(B2P_TAB_ID) > 0;
		}

		/**
		 * Checks if the inventory contains bones, for B2P.
		 *
		 * @return True if we have bones.
		 */
		private boolean haveBones() {
			return inventory.getCount(BONES_ID) > 0;
		}

		/**
		 * Checks if we have food.
		 *
		 * @return True if we have food.
		 */
		private boolean haveFood() {
			return getFood() != null;
		}

		/**
		 * Checks whether you need to eat or not.
		 *
		 * @return True if we need to eat.
		 */
		private boolean needEat() {
			if (getHPPercent() <= toEatAtPercent) {
				toEatAtPercent = getRandomEatPercent();
				return true;
			}
			return false;
		}
	}

	@SuppressWarnings("serial")
	private class FighterGUI extends JFrame {

		private final File file = new File(Configuration.Paths.getScriptCacheDirectory()
				+ File.separator + "TFighter.txt");

		private JCheckBox useMulti, useRadius, useSafe, useCentral,
				prioritizeLoot, useBones;
		private JTextField npcBox, lootBox, mouseSpeedBox;

		private final ActionListener onStart = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				saveProperties();
				startScript = true;
				mouseSpeedMin = Integer.parseInt(mouseSpeedBox.getText().split(",")[0]);
				mouseSpeedMax = Integer.parseInt(mouseSpeedBox.getText().split(",")[0]);
				utilizeMultiwayCombat = useMulti.isSelected();
				onlyInRadius = useRadius.isSelected();
				useSafespot = useSafe.isSelected();
				useCentralClicking = useCentral.isSelected();
				buryBones = useBones.isSelected();
				TFighterEE.this.prioritizeLoot = prioritizeLoot.isSelected();
				if (onlyInRadius) {
					u.npcs.maxRadius = Integer.parseInt(JOptionPane.showInputDialog("Enter the max radius. Example: 10"));
				}
				String[] ids = npcBox.getText().split(",");
				ArrayList<Integer> idList = new ArrayList<Integer>();
				ArrayList<String> nameList = new ArrayList<String>();
				for (int i = 0; i < ids.length; i++) {
					if (ids[i] != null && !ids[i].equals("")) {
						try {
							final int id = Integer.parseInt(ids[i]);
							idList.add(id);
						} catch (final Exception e1) {
							nameList.add(ids[i]);
						}
					}
				}
				u.npcs.npcIDs = idList.size() > 0 ? toIntArray(idList.toArray(new Integer[0]))
						: new int[0];
				u.npcs.npcNames = nameList.size() > 0 ? nameList.toArray(new String[0])
						: new String[0];

				ids = lootBox.getText().split(",");
				idList = new ArrayList<Integer>();
				nameList = new ArrayList<String>();
				for (int i = 0; i < ids.length; i++) {
					if (ids[i] != null && !ids[i].equals("")) {
						try {
							final int id = Integer.parseInt(ids[i]);
							idList.add(id);
						} catch (final Exception e1) {
							nameList.add(ids[i]);
						}
					}
				}
				u.loot.lootIDs = idList.size() > 0 ? toIntArray(idList.toArray(new Integer[0]))
						: new int[0];
				u.loot.lootNames = nameList.size() > 0 ? nameList.toArray(new String[0])
						: new String[0];
				dispose();
			}
		};

		private FighterGUI() {
			init();
			pack();
			setVisible(true);
		}

		private void init() {
			final Properties props = loadProperties();
			final JPanel north = new JPanel(new FlowLayout());
			north.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			{
				final JLabel title = new JLabel("TFighterEE");
				title.setFont(new Font("Arial", Font.PLAIN, 28));
				north.add(title);
			}
			add(north, BorderLayout.NORTH);

			final JPanel center = new JPanel();
			center.setLayout(new BoxLayout(center, BoxLayout.PAGE_AXIS));
			center.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			{
				final JLabel mouseSpeedLabel = new JLabel("Enter your desired mouse speed (max,min)");
				mouseSpeedBox = new JTextField("4,7");

				final JLabel npcLabel1 = new JLabel("Enter the IDs and/or names of the NPCs to fight.");
				final JLabel npcLabel2 = new JLabel("You can mix and match these, all in the same box!");
				npcBox = new JTextField("2,5,1,Chicke");

				final JLabel lootLabel = new JLabel("Enter the IDs and/or names of items to loot.");
				lootBox = new JTextField("arrow,feather");

				useMulti = new JCheckBox("Utilize multiway combat");
				useRadius = new JCheckBox("Only attack within a radius");
				useSafe = new JCheckBox("Use safespot?");
				useCentral = new JCheckBox("Use central point on NPC? (instead of random)");
				useBones = new JCheckBox("Bury the bones you pick up?");

				final JLabel prioritizeLabel = new JLabel("If selected, you will loot while in combat.");
				prioritizeLoot = new JCheckBox("Prioritize loot over combat?");

				mouseSpeedLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
				mouseSpeedBox.setAlignmentX(JTextField.CENTER_ALIGNMENT);

				npcLabel1.setAlignmentX(JLabel.CENTER_ALIGNMENT);
				npcLabel2.setAlignmentX(JLabel.CENTER_ALIGNMENT);
				npcBox.setAlignmentX(JTextField.CENTER_ALIGNMENT);

				lootLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
				lootBox.setAlignmentX(JTextField.CENTER_ALIGNMENT);

				useMulti.setAlignmentX(JCheckBox.CENTER_ALIGNMENT);
				useRadius.setAlignmentX(JCheckBox.CENTER_ALIGNMENT);
				useSafe.setAlignmentX(JCheckBox.CENTER_ALIGNMENT);
				useCentral.setAlignmentX(JCheckBox.CENTER_ALIGNMENT);
				useBones.setAlignmentX(JCheckBox.CENTER_ALIGNMENT);

				prioritizeLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
				prioritizeLoot.setAlignmentX(JComboBox.CENTER_ALIGNMENT);

				if (props.getProperty("mouseSpeed") != null) {
					mouseSpeedBox.setText(props.getProperty("mouseSpeed"));
				}
				if (props.getProperty("npcBox") != null) {
					npcBox.setText(props.getProperty("npcBox"));
				}
				if (props.getProperty("lootBox") != null) {
					lootBox.setText(props.getProperty("lootBox"));
				}
				if (props.getProperty("useMulti") != null) {
					if (props.getProperty("useMulti").equals("true")) {
						useMulti.setSelected(true);
					}
				}
				if (props.getProperty("useRadius") != null) {
					if (props.getProperty("useRadius").equals("true")) {
						useRadius.setSelected(true);
					}
				}
				if (props.getProperty("useSafe") != null) {
					if (props.getProperty("useSafe").equals("true")) {
						useSafe.setSelected(true);
					}
				}
				if (props.getProperty("useCentral") != null) {
					if (props.getProperty("useCentral").equals("true")) {
						useCentral.setSelected(true);
					}
				}
				if (props.getProperty("prioritizeLoot") != null) {
					if (props.get("prioritizeLoot").equals("true")) {
						prioritizeLoot.setSelected(true);
					}
				}
				if (props.getProperty("useBones") != null) {
					if (props.get("useBones").equals("true")) {
						useBones.setSelected(true);
					}
				}

				center.add(mouseSpeedLabel);
				center.add(mouseSpeedBox);

				center.add(new JLabel(" "));

				center.add(npcLabel1);
				center.add(npcLabel2);
				center.add(npcBox);

				center.add(new JLabel(" "));

				center.add(lootLabel);
				center.add(lootBox);

				center.add(new JLabel(" "));

				center.add(useMulti);
				center.add(useRadius);
				center.add(useSafe);
				center.add(useCentral);
				center.add(useBones);
				center.add(new JLabel(" "));

				center.add(prioritizeLabel);
				center.add(prioritizeLoot);
			}
			add(center, BorderLayout.CENTER);

			final JPanel south = new JPanel(new FlowLayout());
			south.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			{
				final JButton start = new JButton("Start script!");
				start.setAlignmentX(JButton.CENTER_ALIGNMENT);
				start.addActionListener(onStart);
				south.add(start);
			}
			add(south, BorderLayout.SOUTH);

			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setTitle("TFighter GUI");
		}

		private Properties loadProperties() {
			try {
				if (!file.exists()) {
					file.createNewFile();
				}
				final Properties p = new Properties();
				p.load(new FileInputStream(file));
				return p;
			} catch (final Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		private void saveProperties() {
			final Properties p = new Properties();
			p.put("mouseSpeed", mouseSpeedBox.getText());
			p.put("npcBox", npcBox.getText());
			p.put("lootBox", lootBox.getText());
			p.put("useMulti", Boolean.toString(useMulti.isSelected()));
			p.put("useRadius", Boolean.toString(useRadius.isSelected()));
			p.put("useSafe", Boolean.toString(useSafe.isSelected()));
			p.put("useCentral", Boolean.toString(useCentral.isSelected()));
			p.put("useBones", Boolean.toString(useBones.isSelected()));
			p.put("prioritizeLoot", Boolean.toString(prioritizeLoot.isSelected()));
			try {
				p.store(new FileOutputStream(file), "");
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		private int[] toIntArray(final Integer[] ints) {
			final int[] done = new int[ints.length];
			for (int i = 0; i < done.length; i++) {
				done[i] = ints[i].intValue();
			}
			return done;
		}
	}

	private class InCombatLoop implements LoopAction {

		public boolean activate() {
			return u.npcs.isInCombat();
		}

		public int loop() {
			antiban();
			return random(50, 200);
		}

	}

	private interface LoopAction {
		public boolean activate();

		public int loop();
	}

	private class Loot {

		private int[] lootIDs = new int[0];
		private String[] lootNames = new String[0];

		private final Map<String, Integer> lootTaken = new HashMap<String, Integer>();

		private final Filter<RSGroundItem> lootFilter = new Filter<RSGroundItem>() {
			@Override
			public boolean accept(final RSGroundItem t) {
				// Skip if we can't hold it
				RSItem i;
				if (inventory.isFull()
						&& ((i = inventory.getItem(t.getItem().getID())) == null || i.getStackSize() <= 1)) {
					return false;
				}
				// Skip if its out of radius or far away
				if (onlyInRadius
						&& calc.distanceBetween(t.getLocation(), startTile) > u.npcs.maxRadius
						|| calc.distanceTo(t.getLocation()) > 25) {
					return false;
				}
				// Check ID/name
				boolean good = false;
				final int id = t.getItem().getID();
				for (final int iD : lootIDs) {
					if (iD == id) {
						good = true;
					}
				}
				final String name = t.getItem().getName();
				for (final String s : lootNames) {
					if (name != null
							&& name.toLowerCase().contains(s.toLowerCase())) {
						good = true;
					}
				}
				return good;
			}
		};

		private void addItem(final String name, final int count) {
			if (lootTaken.get(name) != null) {
				final int newCount = count + lootTaken.get(name);
				lootTaken.remove(name);
				lootTaken.put(name, newCount);
			} else {
				lootTaken.put(name, count);
			}
		}

		/**
		 * Gets the nearest loot, based on the filter
		 *
		 * @return The nearest item to loot, or null if none.
		 */
		private RSGroundItem getLoot() {
			return groundItems.getNearest(lootFilter);
		}

		private Map<String, Integer> getLootTaken() {
			final HashMap<String, Integer> m = new HashMap<String, Integer>();
			m.putAll(lootTaken);
			return m;
		}

		/**
		 * Attempts to take an item.
		 *
		 * @param item The item to take.
		 * @return -1 if error, 0 if taken, 1 if walked
		 */
		private int takeItem(final RSGroundItem item) {
			if (item == null) {
				return -1;
			}
			final String action = "Take " + item.getItem().getName();
			if (item.isOnScreen()) {
				for (int i = 0; i < 5; i++) {
					if (menu.isOpen()) {
						mouse.moveRandomly(300, 500);
					}
					final Point p = calc.tileToScreen(item.getLocation(), random(0.48, 0.52), random(0.48, 0.52), 0);
					if (!calc.pointOnScreen(p)) {
						continue;
					}
					mouse.move(p, 3, 3);
					if (menu.contains(action)) {
						if (menu.getItems()[0].contains(action)) {
							mouse.click(true);
							return 0;
						} else {
							mouse.click(false);
							sleep(random(100, 200));
							if (menu.doAction(action)) {
								return 0;
							}
						}
					}
				}
			} else {
				walking.walkTileMM(walking.getClosestTileOnMap(item.getLocation()));
				return 1;
			}
			return -1;
		}

	}

	private class LootLoop implements LoopAction {

		private RSGroundItem loot = null;

		public boolean activate() {
			return (loot = u.loot.getLoot()) != null;
		}

		@Override
		public int loop() {
			final int origCount = inventory.getCount(true);
			final String name = loot.getItem().getName();
			final int count = loot.getItem().getStackSize();
			final int result = u.loot.takeItem(loot);
			if (result == 0) {
				waitWhileMoving();
				if (waitForInvChange(origCount)) {
					u.loot.addItem(name, count);
				}
			} else if (result == 1) {
				waitWhileMoving();
			}
			return random(50, 200);
		}

	}

	private class NPCs {

		private int[] npcIDs = new int[0];
		private String[] npcNames = new String[0];

		private int maxRadius = 10;

		/**
		 * The filter we use!
		 */
		private final Filter<RSNPC> npcFilter = new Filter<RSNPC>() {
			@Override
			public boolean accept(final RSNPC t) {
				return isOurNPC(t)
						&& t.isValid()
						&& (!onlyInRadius || calc.distanceBetween(t.getLocation(), startTile) < maxRadius)
						&& (utilizeMultiwayCombat || !t.isInCombat()
						&& t.getInteracting() == null)
						&& t.getHPPercent() != 0;
			}
		};

		/**
		 * Will only return an on screen NPC. Based on npcFilter.
		 */
		private final Filter<RSNPC> npcOnScreenFilter = new Filter<RSNPC>() {
			@Override
			public boolean accept(final RSNPC n) {
				return npcFilter.accept(n)
						&& getPointOnScreen(n.getModel(), true) != null;
			}
		};

		/**
		 * Clicks an NPC based on its model.
		 *
		 * @param npc    The NPC to click.
		 * @param action The action to perform.
		 * @return 0 if the NPC was clicked, 1 if we walked to it, or -1 if
		 *         nothing happened.
		 */
		private int clickNPC(final RSNPC npc, final String action) {
			for (int i = 0; i < 10; i++) {
				if (isPartiallyOnScreen(npc.getModel())) {
					final Point p = useCentralClicking ? getCentralPoint(npc.getModel())
							: getPointOnScreen(npc.getModel(), false);
					if (p == null || !calc.pointOnScreen(p)) {
						continue;
					}
					mouse.move(p, useCentralClicking ? 3 : 0, useCentralClicking ? 3
							: 0);
					final String[] items = menu.getItems();
					if (items.length > 0 && items[0].contains(action)) {
						mouse.click(true);
						return 0;
					} else if (menu.contains(action)) {
						mouse.click(false);
						sleep(random(100, 200));
						for (int x = 0; x < 4; x++) {
							if (!menu.contains(action)) {
								break;
							}
							if (menu.doAction(action)) {
								return 0;
							}
						}
					}
				} else {
					if (!useSafespot) {
						walking.walkTileMM(closerTile(npc.getLocation(), 1), 2, 2);
						return 1;
					} else {
						final int angle = camera.getCharacterAngle(npc);
						if (calc.distanceTo(npc) < 10
								&& Math.abs(angle - camera.getAngle()) > 20) {
							camera.setAngle(angle + random(-20, 20));
						}
					}
				}
			}
			return -1;
		}

		/**
		 * Gets a closer tile to us within dist.
		 *
		 * @param t    The tile to start with.
		 * @param dist The max dist.
		 * @return A closer tile.
		 */
		private RSTile closerTile(final RSTile t, final int dist) {
			final RSTile loc = getMyPlayer().getLocation();
			int newX = t.getX(), newY = t.getY();
			for (int i = 1; i < dist; i++) {
				newX = t.getX() != loc.getX() ? (t.getX() < loc.getX() ? newX--
						: newX++) : newX;
				newY = t.getY() != loc.getY() ? (t.getY() < loc.getY() ? newY--
						: newY++) : newY;
			}
			return new RSTile(newX, newY);
		}

		/**
		 * Calculates the distance between two points.
		 *
		 * @param p1 The first point.
		 * @param p2 The second point.
		 * @return The distance between the two points, using the distance
		 *         formula.
		 */
		private double distanceBetween(final Point p1, final Point p2) {
			return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)
					* (p1.y - p2.y));
		}

		/**
		 * Generates a rough central point. Performs the calculation by first
		 * generating a rough point, and then finding the point closest to the
		 * rough point that is actually on the RSModel.
		 *
		 * @param m The RSModel to test.
		 * @return The rough central point.
		 */
		private Point getCentralPoint(final RSModel m) {
			if (m == null) {
				return null;
			}
			try {
				/* Add X and Y of all points, to get a rough central point */
				int x = 0, y = 0, total = 0;
				for (final Polygon poly : m.getTriangles()) {
					for (int i = 0; i < poly.npoints; i++) {
						x += poly.xpoints[i];
						y += poly.ypoints[i];
						total++;
					}
				}
				final Point central = new Point(x / total, y / total);
				/*
				 * Find a real point on the NPC that is closest to the central
				 * point
				 */
				Point curCentral = null;
				double dist = 20000;
				for (final Polygon poly : m.getTriangles()) {
					for (int i = 0; i < poly.npoints; i++) {
						final Point p = new Point(poly.xpoints[i], poly.ypoints[i]);
						if (!calc.pointOnScreen(p)) {
							continue;
						}
						final double dist2 = distanceBetween(central, p);
						if (curCentral == null || dist2 < dist) {
							curCentral = p;
							dist = dist2;
						}
					}
				}
				return curCentral;
			} catch (final Exception e) {
			}
			return null;
		}

		/**
		 * Returns the interacting NPC that matches our description, if any.
		 *
		 * @return The closest interacting NPC that matches the filter.
		 */
		private RSNPC getInteracting() {
			RSNPC npc = null;
			int dist = 20;
			for (final RSNPC n : npcs.getAll()) {
				if (!isOurNPC(n)) {
					continue;
				}
				final RSCharacter inter = n.getInteracting();
				if (inter != null && inter instanceof RSPlayer
						&& inter.equals(getMyPlayer())
						&& calc.distanceTo(n) < dist) {
					dist = calc.distanceTo(n);
					npc = n;
				}
			}
			return npc;
		}

		/**
		 * Returns the nearest NPC.
		 *
		 * @return The nearest NPC that matches the filter.
		 */
		private RSNPC getNPC() {
			final RSNPC onScreen = npcs.getNearest(npcOnScreenFilter);
			if (onScreen != null) {
				return onScreen;
			}
			return npcs.getNearest(npcFilter);
		}

		/**
		 * Gets a point on a model that is on screen.
		 *
		 * @param m     The RSModel to test.
		 * @param first If true, it will return the first point that it finds on
		 *              screen.
		 * @return A random point on screen of an object.
		 */
		private Point getPointOnScreen(final RSModel m, final boolean first) {
			if (m == null) {
				return null;
			}
			final ArrayList<Point> list = new ArrayList<Point>();
			try {
				final Polygon[] tris = m.getTriangles();
				for (final Polygon p : tris) {
					for (int j = 0; j < p.xpoints.length; j++) {
						final Point pt = new Point(p.xpoints[j], p.ypoints[j]);
						if (calc.pointOnScreen(pt)) {
							if (first) {
								return pt;
							}
							list.add(pt);
						}
					}
				}
			} catch (final Exception e) {
			}
			return list.size() > 0 ? list.get(random(0, list.size())) : null;
		}

		/**
		 * Checks if we are in combat.
		 *
		 * @return True if we are in combat.
		 */
		private boolean isInCombat() {
			return getMyPlayer().getInteracting() instanceof RSNPC;
		}

		private boolean isOurNPC(final RSNPC t) {
			final int id = t.getID();
			final String name = t.getName();
			boolean good = false;
			for (final int i : npcIDs) {
				if (id == i) {
					good = true;
				}
			}
			for (final String s : npcNames) {
				if (name.toLowerCase().contains(s.toLowerCase())) {
					good = true;
				}
			}
			return good;
		}

		/**
		 * Checks if a model is partially on screen.
		 *
		 * @param m The RSModel to check.
		 * @return True if any point on the model is on screen.
		 */
		private boolean isPartiallyOnScreen(final RSModel m) {
			return getPointOnScreen(m, true) != null;
		}
	}

	private class Potion {

		private final int[] MAGIC_POTIONS = new int[]{3040, 3042, 3044, 3046,
				11513, 11515, 13520, 13521, 13522, 13523};

		private final int[] PRAYER_POTIONS = new int[]{2434, 139, 141, 143,
				11465, 11467};

		private final int[] RANGE_POTIONS = new int[]{2444, 169, 171, 173,
				11509, 11511, 13524, 13525, 15326, 15327};

		private final int[] ENERGY_POTIONS = new int[]{3008, 3010, 3012,
				3014, 3016, 3018, 3020, 3022, 11453, 11455, 11481, 11483};

		private final int[] COMBAT_POTIONS = new int[]{9739, 9741, 9743,
				9745, 11445, 11447};

		private final int[] ATTACK_POTIONS = new int[]{2428, 121, 123, 125,
				2436, 145, 147, 149, 11429, 11431, 11429, 11431, 11429, 11431,
				11469, 11471, 15308, 15309, 15310, 15311};

		private final int[] STRENGTH_POTIONS = new int[]{113, 115, 117, 119,
				2440, 157, 159, 161, 11443, 11441, 11485, 11487, 15312, 15313,
				15314, 15315};

		private final int[] DEFENSE_POTIONS = new int[]{2432, 133, 135, 137,
				2442, 163, 165, 167, 11457, 11459, 11497, 11499, 15316, 15317,
				15318, 15319};

		private final int[] ANTIPOISON = new int[]{2446, 175, 177, 179, 2448,
				181, 183, 185, 5952, 5954, 5956, 5958, 5943, 5945, 5947, 5949,
				11433, 11435, 11501, 11503};

		private final int[] ZAMORAK_POTIONS = new int[]{2450, 189, 191, 193,
				11521, 11523};

		private final int[] SARADOMIN_POTIONS = new int[]{6685, 6687, 6689,
				6691};

		private final int[] OVERLOAD_POTIONS = new int[]{15332, 15333, 15334,
				15335};

		private final int[] VIAL = new int[]{229};

		private HashMap<String, RSItem[]> getPotions() {
			final HashMap<String, RSItem[]> potions = new HashMap<String, RSItem[]>();
			potions.put("MAGIC", inventory.getItems(MAGIC_POTIONS));
			potions.put("PRAYER", inventory.getItems(PRAYER_POTIONS));
			potions.put("RANGE", inventory.getItems(RANGE_POTIONS));
			potions.put("ENERGY", inventory.getItems(ENERGY_POTIONS));
			potions.put("COMBAT", inventory.getItems(COMBAT_POTIONS));
			potions.put("ATTACK", inventory.getItems(ATTACK_POTIONS));
			potions.put("STRENGTH", inventory.getItems(STRENGTH_POTIONS));
			potions.put("DEFENSE", inventory.getItems(DEFENSE_POTIONS));
			potions.put("ANTIPOISON", inventory.getItems(ANTIPOISON));
			potions.put("ZAMORAK", inventory.getItems(ZAMORAK_POTIONS));
			potions.put("SARADOMIN", inventory.getItems(SARADOMIN_POTIONS));
			potions.put("OVERLOAD", inventory.getItems(OVERLOAD_POTIONS));
			return potions;
		}

		private boolean needPot() {
			final HashMap<String, RSItem[]> potions = u.pot.getPotions();

			if (inventory.getItems(VIAL).length != 0) {
				for (final RSItem i : inventory.getItems(VIAL)) {
					final int n = inventory.getCount(true);
					i.doAction("Drop Vial");
					waitForInvChange(n);
				}
			}

			if (potions.get("MAGIC").length != 0
					&& !statIsBoosted(Skills.MAGIC)) {
				return true;
			}

			if (potions.get("PRAYER").length != 0
					&& statIsBoosted(Skills.PRAYER)) {
				return true;
			}

			if (potions.get("RANGE").length != 0
					&& !statIsBoosted(Skills.RANGE)) {
				return true;
			}
			if (potions.get("ENERGY").length != 0
					&& walking.getEnergy() < random(40, 70)) {
				return true;
			}

			if (potions.get("COMBAT").length != 0
					&& (!statIsBoosted(Skills.ATTACK) || !statIsBoosted(Skills.STRENGTH))) {
				return true;
			}

			if (potions.get("ATTACK").length != 0
					&& !statIsBoosted(Skills.ATTACK)) {
				return true;
			}

			if (potions.get("STRENGTH").length != 0
					&& !statIsBoosted(Skills.STRENGTH)) {
				return true;
			}

			if (potions.get("DEFENSE").length != 0
					&& !statIsBoosted(Skills.DEFENSE)) {
				return true;
			}

			if (potions.get("ANTIPOISON").length != 0 && combat.isPoisoned()) {
				return true;
			}

			if (potions.get("ZAMORAK").length != 0
					&& (!statIsBoosted(Skills.ATTACK) || !statIsBoosted(Skills.STRENGTH))) {
				return true;
			}

			if (potions.get("SARADOMIN").length != 0
					&& !statIsBoosted(Skills.DEFENSE)) {
				return true;
			}

			if (potions.get("OVERLOAD").length != 0
					&& (!statIsBoosted(Skills.ATTACK)
					|| !statIsBoosted(Skills.STRENGTH)
					|| !statIsBoosted(Skills.DEFENSE)
					|| !statIsBoosted(Skills.RANGE) || !statIsBoosted(Skills.MAGIC))) {
				return true;
			}
			return false;
		}

		private boolean statIsBoosted(final int Skill) {
			return skills.getCurrentLevel(Skill) != skills.getRealLevel(Skill);
		}

		public int usePotions() {
			final HashMap<String, RSItem[]> potions = u.pot.getPotions();

			if (!u.pot.statIsBoosted(Skills.MAGIC)
					&& (potions.get("MAGIC").length != 0 || potions.get("OVERLOAD").length != 0)) {
				if (potions.get("MAGIC").length != 0) {
					potions.get("MAGIC")[0].doClick(true);
					return random(2000, 2500);
				} else if (potions.get("OVERLOAD").length != 0) {
					potions.get("OVERLOAD")[0].doClick(true);
					return random(2000, 2500);
				}
			}

			if (skills.getRealLevel(Skills.PRAYER)
					- skills.getCurrentLevel(Skills.PRAYER) >= random(Math.floor(7 + skills.getRealLevel(Skills.PRAYER) / 4) - 2, Math.floor(7 + skills.getRealLevel(Skills.PRAYER) / 4) + 2)
					&& potions.get("PRAYER").length != 0) {
				return random(2000, 2500);
			}

			if (!u.pot.statIsBoosted(Skills.RANGE)
					&& (potions.get("RANGE").length != 0 || potions.get("OVERLOAD").length != 0)) {
				if (potions.get("RANGE").length != 0) {
					potions.get("RANGE")[0].doClick(true);
					return random(2000, 2500);
				} else if (potions.get("OVERLOAD").length != 0) {
					potions.get("OVERLOAD")[0].doClick(true);
					return random(2000, 2500);
				}
			}

			if (walking.getEnergy() < random(40, 70)
					&& potions.get("ENERGY").length != 0) {
				potions.get("ENERGY")[0].doClick(true);

				return random(2000, 2500);
			}

			if (!u.pot.statIsBoosted(Skills.STRENGTH)
					&& (potions.get("STRENGTH").length != 0
					|| potions.get("COMBAT").length != 0
					|| potions.get("ZAMORAK").length != 0 || potions.get("OVERLOAD").length != 0)) {
				if (potions.get("COMBAT").length != 0) {
					potions.get("COMBAT")[0].doClick(true);
					return random(2000, 2500);
				} else if (potions.get("STRENGTH").length != 0) {
					potions.get("STRENGTH")[0].doClick(true);
					return random(2000, 2500);
				} else if (potions.get("ZAMORAK").length != 0) {
					potions.get("ZAMORAK")[0].doClick(true);
					return random(2000, 2500);
				} else if (potions.get("OVERLOAD").length != 0) {
					potions.get("OVERLOAD")[0].doClick(true);
					return random(2000, 2500);
				}
			}

			if (!u.pot.statIsBoosted(Skills.DEFENSE)
					&& (potions.get("DEFENSE").length != 0
					|| potions.get("SARADOMIN").length != 0 || potions.get("OVERLOAD").length != 0)) {
				if (potions.get("DEFENSE").length != 0) {
					potions.get("DEFENSE")[0].doClick(true);
					return random(2000, 2500);
				} else if (potions.get("SARADOMIN").length != 0) {
					potions.get("SARADOMIN")[0].doClick(true);
					return random(2000, 2500);
				} else if (potions.get("OVERLOAD").length != 0) {
					potions.get("OVERLOAD")[0].doClick(true);
					return random(2000, 2500);
				}
			}

			if (!u.pot.statIsBoosted(Skills.ATTACK)
					&& (potions.get("ATTACK").length != 0
					|| potions.get("COMBAT").length != 0
					|| potions.get("ZAMORAK").length != 0 || potions.get("OVERLOAD").length != 0)) {
				if (potions.get("COMBAT").length != 0) {
					potions.get("COMBAT")[0].doClick(true);
					return random(2000, 2500);
				} else if (potions.get("ATTACK").length != 0) {
					potions.get("ATTACK")[0].doClick(true);
					return random(2000, 2500);
				} else if (potions.get("ZAMORAK").length != 0) {
					potions.get("ZAMORAK")[0].doClick(true);
					return random(2000, 2500);
				} else if (potions.get("OVERLOAD").length != 0) {
					potions.get("OVERLOAD")[0].doClick(true);
					return random(2000, 2500);
				}
			}

			if (combat.isPoisoned() && potions.get("ANTIPOISON").length != 0) {
				potions.get("ANTIPOISON")[0].doClick(true);
				return random(2000, 2500);
			}

			return random(50, 200);

		}

	}

	private class SafespotLoop implements LoopAction {

		public boolean activate() {
			return useSafespot && calc.distanceTo(startTile) > 0;
		}

		public int loop() {
			if (!calc.tileOnScreen(startTile)) {
				walking.walkTileMM(startTile);
			} else {
				tiles.doAction(startTile, "Walk");
			}
			waitWhileMoving();
			return random(200, 500);
		}

	}

	private class SkillWatcher {

		private final Map<Integer, Integer> startExpMap = new HashMap<Integer, Integer>();
		private final int[] SKILLS_TO_WATCH = new int[]{Skills.SLAYER,
				Skills.CONSTITUTION, Skills.ATTACK, Skills.STRENGTH,
				Skills.DEFENSE, Skills.RANGE, Skills.MAGIC, Skills.PRAYER};

		/**
		 * Returns the amount of exp gained in the specified skill.
		 *
		 * @param skill The skill see Skills.*
		 * @return
		 */
		private int getExpGainedIn(final int skill) {
			if (startExpMap.get(skill) == null) {
				return -1;
			}
			return skills.getCurrentExp(skill) - startExpMap.get(skill);
		}

		/**
		 * Returns a map of skill names and exp gained.
		 *
		 * @return A map of exp gains and skill names.
		 */
		private Map<String, Integer> getExpGainedMap() {
			final Map<String, Integer> map = new HashMap<String, Integer>();
			for (final int i : SKILLS_TO_WATCH) {
				final int gained = getExpGainedIn(i);
				if (gained != 0) {
					map.put(Skills.SKILL_NAMES[i], gained);
				}
			}
			return map;
		}

		/**
		 * Basically sets start exp for all skills we are watching.
		 */
		private void poll() {
			for (final int skill : SKILLS_TO_WATCH) {
				if (startExpMap.containsKey(skill)) {
					startExpMap.remove(skill);
				}
				startExpMap.put(skill, skills.getCurrentExp(skill));
			}
		}

	}

	private class Util {
		private final NPCs npcs = new NPCs();
		private final Eating eat = new Eating();
		private final Potion pot = new Potion();
		private final Loot loot = new Loot();
		private final SkillWatcher sw = new SkillWatcher();
	}

	private final static ScriptManifest mani = TFighterEE.class.getAnnotation(ScriptManifest.class);

	private final Util u = new Util();

	private RSTile startTile;

	private long nextAntiban = 0;

	private int badFoodCount = 0;

	private int startTime = 0;

	private int mouseSpeedMin = 4, mouseSpeedMax = 7;

	private boolean startScript, showPaint;

	private boolean onlyInRadius = false;

	private boolean utilizeMultiwayCombat = false;

	private boolean useSafespot = false;

	private boolean useCentralClicking = true;

	private boolean prioritizeLoot = false;

	private boolean buryBones = false;

	private final List<LoopAction> loopActions = new LinkedList<LoopAction>();

	/**
	 * Performs a random action, always. Actions: move mouse, move mouse off
	 * screen, move camera.
	 */
	private void antiban() {
		if (System.currentTimeMillis() > nextAntiban) {
			nextAntiban = System.currentTimeMillis() + random(2000, 30000);
		} else {
			return;
		}
		final Thread mouseThread = new Thread() {
			public void run() {
				switch (random(0, 5)) {
					case 0:
						mouse.moveOffScreen();
						break;
					case 1:
						mouse.move(random(0, game.getWidth()), random(0, game.getHeight()));
						break;
					case 2:
						mouse.move(random(0, game.getWidth()), random(0, game.getHeight()));
						break;
				}
			}
		};
		final Thread keyThread = new Thread() {
			public void run() {
				switch (random(0, 4)) {
					case 0:
						camera.setAngle(camera.getAngle() + random(-100, 100));
						break;
					case 1:
						camera.setAngle(camera.getAngle() + random(-100, 100));
						break;
					case 2:
						camera.setAngle(camera.getAngle() + random(-100, 100));
						break;
				}
			}
		};
		if (random(0, 2) == 0) {
			keyThread.start();
			sleep(random(0, 600));
			mouseThread.start();
		} else {
			mouseThread.start();
			sleep(random(0, 600));
			keyThread.start();
		}
		while (keyThread.isAlive() || mouseThread.isAlive()) {
			sleep(random(30, 100));
		}
	}

	/**
	 * True if click continue interface is valid.
	 *
	 * @return True if you can click continue.
	 */
	private boolean canContinue() {
		return getContinueInterface() != null;
	}

	/**
	 * True if we successfully clicked continue.
	 *
	 * @return True if we clicked continue.
	 */
	private boolean clickContinue() {
		final RSComponent c = getContinueInterface();
		if (c != null) {
			return c.doClick();
		}
		return false;
	}

	private void drawMouse(final Graphics g) {
		final int x = mouse.getLocation().x, y = mouse.getLocation().y;

		g.setColor(System.currentTimeMillis() - mouse.getPressTime() < 300 ? Color.CYAN
				: Color.RED);
		g.fillOval(x - 6, y - 6, 12, 12);
		g.setColor(Color.ORANGE);
		g.fillOval(x - 3, y - 3, 6, 6);
		g.drawLine(x - 10, y - 10, x + 10, y + 10);
		g.drawLine(x - 10, y + 10, x + 10, y - 10);
	}

	/**
	 * Gets the "Click here to continue" button on any interface.
	 *
	 * @return The "Click here to continue" button.
	 */
	private RSComponent getContinueInterface() {
		for (final RSInterface iface : interfaces.getAll()) {
			// skip chat
			if (iface.getIndex() == 137) {
				continue;
			}
			for (final RSComponent c : iface.getComponents()) {
				if (c != null && c.isValid()
						&& c.containsText("Click here to continue")
						&& c.getAbsoluteX() > 100 && c.getAbsoluteY() > 300) {
					return c;
				}
			}
		}
		return null;
	}

	@Override
	public int loop() {
		if (random(0, 3) == 0 || mouse.getSpeed() < mouseSpeedMin
				|| mouse.getSpeed() > mouseSpeedMax) {
			mouse.setSpeed(random(mouseSpeedMin, mouseSpeedMax));
		}
		if (camera.getPitch() < 90) {
			camera.setPitch(true);
			return random(50, 100);
		}
		if (!walking.isRunEnabled() && walking.getEnergy() > random(60, 90)) {
			walking.setRun(true);
			return random(1200, 1600);
		}
		if (canContinue()) {
			clickContinue();
			return random(1200, 1600);
		}
		if (game.getCurrentTab() != Game.TAB_INVENTORY) {
			game.openTab(Game.TAB_INVENTORY);
			return random(700, 1500);
		}
		if (u.eat.needEat()) {
			if (u.eat.haveFood()) {
				badFoodCount = 0;
				u.eat.eatFood();
			} else if (u.eat.haveB2pTab() && u.eat.haveBones()) {
				u.eat.breakB2pTab();
				return random(2600, 3000);
			} else {
				badFoodCount++;
				if (badFoodCount > 5) {
					log("You ran out of food! Stopping.");
					stopScript();
				}
			}
			return random(1200, 1600);
		}

		if (u.pot.needPot()) {
			return u.pot.usePotions();
		}

		for (final LoopAction a : loopActions) {
			if (a != null && a.activate()) {
				return a.loop();
			}
		}
		return random(50, 200);
	}

	/**
	 * Formats the given value into a clock format that follows the form of
	 * 00:00:00
	 *
	 * @param millis The total millis to be evaluated
	 * @return A String representation of millis, formatted as a clock
	 */
	private String millisToTime(final int millis) {
		final int hours = millis / (60 * 1000 * 60);
		final int minutes = (millis - hours * 60 * 1000 * 60) / (60 * 1000);
		final int seconds = (millis - hours * 60 * 1000 * 60 - minutes * 60 * 1000) / 1000;
		return (hours >= 10 ? hours + ":" : "0" + hours + ":")
				+ (minutes >= 10 ? minutes + ":" : "0" + minutes + ":")
				+ (seconds >= 10 ? seconds : "0" + seconds);
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		final RSComponent inter = interfaces.get(137).getComponent(0);
		if (inter.getArea().contains(e.getPoint())) {
			showPaint = !showPaint;
		}
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
	}

	@Override
	public void mouseExited(final MouseEvent e) {
	}

	@Override
	public void mousePressed(final MouseEvent e) {
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
	}

	@Override
	public void onRepaint(final Graphics g) {
		if (showPaint) {
			final NumberFormat nf = NumberFormat.getIntegerInstance();
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			// Variables
			final RSComponent inter = interfaces.get(137).getComponent(0);
			final int x = inter.getLocation().x;
			int y = inter.getLocation().y;

			// Counters
			final int runTime = (int) System.currentTimeMillis() - startTime;

			// Background
			g.setColor(new Color(198, 226, 255));
			g.fillRect(x, y, inter.getWidth() + 5, inter.getHeight() + 5);

			// Simple things
			g.setColor(Color.BLACK);
			g.setFont(new Font("Arial", Font.PLAIN, 16));
			g.drawString("TFighter Enhanced Edition (v" + mani.version() + ")", x + 10, y += g.getFontMetrics().getMaxAscent() + 10);
			g.setFont(new Font("Arial", Font.PLAIN, 14));
			g.drawString("By !@!@! & Zalgo2462", x + 10, y += g.getFontMetrics().getMaxAscent() + 5);
			g.setFont(new Font("Arial", Font.PLAIN, 12));
			g.drawString("Run time: " + millisToTime(runTime), x + 20, y += g.getFontMetrics().getMaxAscent() + 5);

			// Exp gains
			g.setFont(new Font("Arial", Font.BOLD, 12));
			g.drawString("Experience gained:", x + 20, y += g.getFontMetrics().getMaxAscent() + 15);
			g.setFont(new Font("Arial", Font.PLAIN, 11));

			int skillLayoutNumber = 0;
			final int skillYStart = y + g.getFontMetrics().getMaxAscent();
			for (final Map.Entry<String, Integer> entry : u.sw.getExpGainedMap().entrySet()) {

				skillLayoutNumber++;

				final double expPerSec = entry.getValue()
						/ (double) (runTime / 1000);
				final int expPerHour = (int) Math.round(expPerSec * 3600);

				if (skillLayoutNumber < 5 || skillLayoutNumber > 5) {
					g.drawString(entry.getKey() + ": "
							+ nf.format(entry.getValue()) + " (p/hr: "
							+ nf.format(expPerHour) + ")", x + 25, y += g.getFontMetrics().getMaxAscent());
				} else if (skillLayoutNumber == 5) {
					g.drawString(entry.getKey() + ": "
							+ nf.format(entry.getValue()) + " (p/hr: "
							+ nf.format(expPerHour) + ")", x + 175, skillYStart);
				}

			}

			// Loot
			y = inter.getLocation().y;
			g.setFont(new Font("Arial", Font.BOLD, 12));
			g.drawString("Loot taken:", x + 280, y += g.getFontMetrics().getMaxAscent() + 15);
			g.setFont(new Font("Arial", Font.PLAIN, 11));
			final Map<String, Integer> loot = u.loot.getLootTaken();
			for (final Map.Entry<String, Integer> entry : loot.entrySet()) {
				g.drawString(entry.getKey() + " x" + entry.getValue(), x + 285, y += g.getFontMetrics().getMaxAscent());
			}
		}
		drawMouse(g);
	}

	public boolean onStart() {
		if (!game.isLoggedIn()) {
			log("Start logged in.");
			return false;
		}
		showPaint = true;
		startScript = false;
		final FighterGUI gui = new FighterGUI();
		while (!startScript) {
			if (!gui.isVisible()) {
				return false;
			}
			sleep(100);
		}

		startTile = getMyPlayer().getLocation();
		u.sw.poll();
		startTime = (int) System.currentTimeMillis();

		LoopAction[] actions;
		if (prioritizeLoot) {
			actions = new LoopAction[]{(buryBones ? new BonesLoop() : null),
					new LootLoop(), (useSafespot ? new SafespotLoop() : null),
					new InCombatLoop(), new AttackLoop()};
		} else {
			actions = new LoopAction[]{
					(useSafespot ? new SafespotLoop() : null),
					new InCombatLoop(), (buryBones ? new BonesLoop() : null),
					new LootLoop(), new AttackLoop()};
		}
		for (final LoopAction a : actions) {
			loopActions.add(a);
		}

		return true;
	}

	/**
	 * Used in safe spotting. Waits for an animation.
	 */
	private void waitForAnim() {
		final long timer = System.currentTimeMillis();
		while (System.currentTimeMillis() - timer < 2500
				&& getMyPlayer().getAnimation() == -1
				&& (System.currentTimeMillis() - timer < 1000 || getMyPlayer().getInteracting() != null)) {
			sleep(random(50, 100));
		}
	}

	/**
	 * Waits until the inventory count changes
	 */
	private boolean waitForInvChange(final int origCount) {
		final long start = System.currentTimeMillis();
		while (inventory.getCount(true) == origCount
				&& System.currentTimeMillis() - start < 2000) {
			sleep(random(20, 70));
		}
		return inventory.getCount(true) != origCount;
	}

	/**
	 * Waits until we are no longer moving.
	 */
	private void waitWhileMoving() {
		final long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < 1500
				&& !getMyPlayer().isMoving()) {
			sleep(random(50, 200));
		}
		while (getMyPlayer().isMoving()) {
			sleep(random(20, 50));
		}
	}
}
