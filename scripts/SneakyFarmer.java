import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Magic;
import org.rsbot.script.methods.Skills;
import org.rsbot.script.util.Timer;
import org.rsbot.script.wrappers.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

@ScriptManifest(authors = {"MrSneaky"}, keywords = {"Farming", "mrsneaky", "herb", "farmer"}, name = "SneakyFarmer", description = "Farms herbs at 3 patches", version = 1.60)
public class SneakyFarmer extends Script implements MessageListener, PaintListener, MouseListener {

	private static String scriptVersion = "1.60";
	private static int herbPlot[] = {8150, 8151, 8152};
	private int seedID;
	private int grimyID;
	private int notedID;
	private int startingNoteSize;
	private static int rakeID = 5341;
	private static int dibberID = 5343;
	private static int spadeID = 952;
	private static int compostID = 6034;
	private static int bucketID = 1925;
	private static int weedsID = 6055;
	private static int plantCureID = 6036;
	private static int vialID = 229;
	private static int leprechaunID[] = {3021, 7557};
	private static int leprechaunInterface = 125;
	private static int closeInterface = 34;
	private int numSeedsPlanted = 0;
	private int numHerbsFarmed = 0;
	private int marketPriceOfHerbs;
	private int marketPriceOfSeeds;
	private static String rake = "Rake";
	private static String pick = "Pick";
	private static String inspect = "Inspect";
	private static String clear = "Clear";
	private String status = "Starting Script";
	private String seedType;
	private boolean scriptRunning = true;
	private RSArea camelotFarmingArea = new RSArea(new RSTile(2804, 3458), new RSTile(2815, 3469));
	private RSArea faladorFarmingArea = new RSArea(new RSTile(3050, 3303), new RSTile(3061, 3315));
	private RSArea ardougneFarmingArea = new RSArea(new RSTile(2660, 3370), new RSTile(2672, 3381));
	private RSArea camelotTeleArea = new RSArea(new RSTile(2752, 3472), new RSTile(2762, 3482));
	private RSArea faladorTeleArea = new RSArea(new RSTile(2957, 3374), new RSTile(2970, 3384));
	private RSArea ardougneTeleArea = new RSArea(new RSTile(2656, 3298), new RSTile(2666, 3311));
	private Line[] camelotPath = {new Line(2755, 3481, 2755, 3475), new Line(2765, 3478, 2765, 3475), new Line(2772, 3478, 2773, 3472), new Line(2782, 3476, 2780, 3463), new Line(2790, 3473, 2789, 3463), new Line(2803, 3473, 2801, 3470), new Line(2814, 3465, 2814, 3461)};
	private Line[] faladorPath = {new Line(2960, 3384, 2962, 3376), new Line(2971, 3381, 2971, 3378), new Line(2983, 3378, 2982, 3373), new Line(2988, 3373, 2986, 3370), new Line(2993, 3370, 2992, 3369), new Line(2997, 3367, 2996, 3365), new Line(3007, 3361, 3001, 3352), new Line(3008, 3349, 3006, 3349), new Line(3008, 3340, 3006, 3340), new Line(3008, 3335, 3006, 3335), new Line(3008, 3324, 3005, 3324), new Line(3009, 3322, 3006, 3317), new Line(3020, 3322, 3020, 3314), new Line(3030, 3324, 3030, 3314), new Line(3042, 3322, 3042, 3314), new Line(3052, 3318, 3052, 3313), new Line(3055, 3309, 3060, 3309)};
	//private Line[] ardougnePath = {new Line(2659, 3296, 2669, 3305), new Line(2651, 3316, 2654, 3318), new Line(2648, 3324, 2651, 3324), new Line(2646, 3330, 2647, 3333), new Line(2637, 3332, 2641, 3335), new Line(2633, 3343, 2638, 3344), new Line(2635, 3351, 2638, 3351), new Line(2635, 3364, 2640, 3364), new Line(2641, 3375, 2645, 3372), new Line(2644, 3377, 2647, 3376), new Line(2646, 3382, 2649, 3379), new Line(2652, 3383, 2652, 3381), new Line(2658, 3382, 2658, 3381), new Line(2669, 3377, 2670, 3373)};
	private final Line[] ardougnePath = {new Line(2653, 3311, 2653, 3298), new Line(2672, 3306, 2672, 3304), new Line(2690, 3307, 2692, 3305), new Line(2689, 3320, 2695, 3321), new Line(2688, 3328, 2690, 3328), new Line(2677, 3345, 2686, 3347), new Line(2675, 3358, 2686, 3363), new Line(2666, 3373, 2670, 3377)};
	private AntiBan antiBan;
	private Timer runTime;
	private Timer breakTimer;
	private ArrayList<RSTile> pathList;
	private boolean hidePaint = false;
	private Rectangle closePaint = new Rectangle(425, 9, 83, 26);
	private Point p;

	private enum Location {
		CAMELOT, FALADOR, ARDOUGNE;

		public String toString() {
			switch (this) {
				case CAMELOT:
					return "Camelot";
				case FALADOR:
					return "Falador";
				case ARDOUGNE:
					return "Ardougne";
				default:
					throw new IllegalArgumentException();
			}
		}
	}

	private enum State {CHECK_CROPS, PICK, RAKE, COMPOST, PLANT, CLEAR, CURE, WALKING, TELEPORT, BREAK}

	private Location currentLocation;
	private State currentState;

	public boolean onStart() {
		log("Welcome to Sneaky's Herb farmer");

		if (game.isLoggedIn()) {
			camera.setPitch(100);
			antiBan = new AntiBan(this);
			antiBan.start();
		} else {
			log("Please log in before starting this script.");
			scriptRunning = false;
			return false;
		}

		if (!camelotFarmingArea.contains(getMyPlayer().getLocation())) {
			log("Not in Camelot farming patch. Please start the script there.");
			scriptRunning = false;
			return false;
		}

		// Start GUI to select which Herb to farm
		HerbFarmerGUI GUI = new HerbFarmerGUI();
		GUI.setVisible(true);
		while (GUI.isVisible()) {
			sleep(50);
		}

		currentLocation = Location.CAMELOT;
		currentState = State.CHECK_CROPS;

		notedID = grimyID + 1;
		if (inventory.contains(notedID)) {
			startingNoteSize = inventory.getItem(notedID).getStackSize();
		}

		runTime = new Timer(0);
		marketPriceOfHerbs = grandExchange.lookup(grimyID).getGuidePrice();
		marketPriceOfSeeds = grandExchange.lookup(seedID).getGuidePrice();
		log("You are farming " + seedType);
		log("The market price of each " + seedType + " seed is: " + marketPriceOfSeeds + "gp");
		log("The market price of each " + seedType + " herb is: " + marketPriceOfHerbs + "gp");

		// check if inventory contains seeds, rake, dibber, spade
		if (!checkInventory()) {
			log("You do not have all of the required materials in your inventory");
			log("(Seeds, rake, dibber, and spade). Stopping script.");
			scriptRunning = false;
			return false;
		}

		return true;
	}

	public void onFinish() {
		log("Thank you for using Sneaky's Herb Farmer!");
		env.saveScreenshot(false);
		scriptRunning = false;
	}

	public int loop() {
		if (checkInventory() || status == "Logging back in") {
			if (status == "Logging back in") {
				int timeout = 200;
				while (game.getClientState() != 10 && timeout > 0) {
					sleep(100);
					timeout++;
				}
				if (timeout == 0) {
					return 100;
				}
				sleep(random(4000, 5000)); // Wait while information is loaded from server
			}

			switch (currentState) {
				case CHECK_CROPS:
					checkCrops();
					break;
				case PICK:
					pickHerbs();
					break;
				case RAKE:
					rakePatch();
					break;
				case COMPOST:
					compostPatch();
					break;
				case PLANT:
					plantSeed();
					break;
				case CLEAR:
					clearHerbs();
					break;
				case CURE:
					cureHerbs();
					break;
				case WALKING:
					switch (currentLocation) {
						case CAMELOT:
							walk(Location.FALADOR, State.CHECK_CROPS);
							break;
						case FALADOR:
							walk(Location.ARDOUGNE, State.CHECK_CROPS);
							break;
						case ARDOUGNE:
							walk(Location.CAMELOT, State.BREAK);
							break;
					}
					break;
				case TELEPORT:
					switch (currentLocation) {
						case CAMELOT:
							teleport(Location.FALADOR);
							break;
						case FALADOR:
							teleport(Location.ARDOUGNE);
							break;
						case ARDOUGNE:
							teleport(Location.CAMELOT);
							break;
					}
					break;
				case BREAK:
					handleBreak();
					break;
			}
			performMaintainance();
			return random(1000, 2000);
		}
		return -1;
	}

	private void checkCrops() {
		status = "Checking crops";
		RSObject plot = objects.getNearest(herbPlot);
		if (plot != null) {
			if (!plot.isOnScreen() || calc.distanceTo(plot) > 3) {
				walking.walkTileMM(plot.getLocation(), 1, 1);
				sleep(random(3000, 4000));
			}
			plot.doAction(inspect);
		}
	}

	private void pickHerbs() {
		status = "Picking herbs";
		RSObject plot = objects.getNearest(herbPlot);
		if (plot != null) {
			plot.doAction(pick);
			sleep(random(1250, 2000));
			Timer idleTimer = new Timer(random(1250, 2000));
			while (idleTimer.isRunning()) {
				if (players.getMyPlayer().getAnimation() != -1) {
					idleTimer.reset();
				}
				sleep(random(50, 200));
			}
		}
		currentState = State.CHECK_CROPS;
	}

	private void cureHerbs() {
		status = "Curing diseased herbs";
		RSObject plot = objects.getNearest(herbPlot);
		if (plot != null) {
			if (inventory.contains(plantCureID)) {
				int tmp = inventory.getCount(plantCureID);
				for (int i = 0; i < 5; i++) {
					if (tmp == inventory.getCount(plantCureID) && currentState == State.CURE) {
						if (i > 0) {
							camera.setAngle(random(0, 360));
						}
						inventory.getItem(plantCureID).doClick(true);
						plot.doClick();
						sleep(random(3000, 4000));
					} else {
						break;
					}
				}
			}
			sleep(random(200, 300));
		}
		currentState = State.CHECK_CROPS;
	}

	private void clearHerbs() {
		status = "Clearing dead herbs";
		RSObject plot = objects.getNearest(herbPlot);
		if (plot != null) {
			plot.doAction(clear);
			sleep(random(1250, 2000));
			Timer idleTimer = new Timer(random(1250, 2000));
			while (idleTimer.isRunning()) {
				if (players.getMyPlayer().getAnimation() != -1) {
					idleTimer.reset();
				}
				sleep(random(50, 200));
			}
		}
		currentState = State.CHECK_CROPS;
	}

	private void rakePatch() {
		status = "Raking the patch";
		RSObject plot = objects.getNearest(herbPlot);
		if (plot != null) {
			plot.doAction(rake);
			sleep(random(1250, 2000));
			Timer idleTimer = new Timer(random(1250, 2000));
			while (idleTimer.isRunning()) {
				if (players.getMyPlayer().getAnimation() != -1) {
					idleTimer.reset();
				}
				sleep(random(50, 200));
			}
		}
		currentState = State.CHECK_CROPS;
	}

	private void compostPatch() {
		status = "Adding supercompost";
		RSObject plot = objects.getNearest(herbPlot);
		if (plot != null) {
			if (!inventory.contains(compostID)) {
				RSNPC leprechaun = npcs.getNearest(leprechaunID);
				if (!leprechaun.isOnScreen() || calc.distanceTo(leprechaun) > 4) {
					walking.walkTileMM(leprechaun.getLocation(), 1, 1);
					sleep(random(3000, 4000));
				}

				leprechaun.doAction("Exchange");
				sleep(random(1500, 2000));
				mouse.click(359, 265, 10, 10, true);
				sleep(random(300, 900));
				interfaces.getComponent(leprechaunInterface, closeInterface).doClick();
				sleep(random(200, 300));
				if (!plot.isOnScreen() || calc.distanceTo(plot) > 4) {
					walking.walkTileMM(plot.getLocation(), 1, 1);
					sleep(random(3000, 4000));
				}
			}
			if (inventory.contains(compostID)) {
				int tmp = inventory.getCount(compostID);
				for (int i = 0; i < 5; i++) {
					if (tmp == inventory.getCount(compostID) && currentState == State.COMPOST) {
						if (i > 0) {
							camera.setAngle(random(0, 360));
						}
						if (inventory.contains(compostID)) {
							inventory.getItem(compostID).doClick(true);
							plot.doClick();
						}
						sleep(random(4000, 5000));
					} else {
						break;
					}
				}
			}
		}
		currentState = State.CHECK_CROPS;
	}

	private void plantSeed() {
		status = "Planting seed";
		RSObject plot = objects.getNearest(herbPlot);
		if (plot != null && inventory.contains(seedID)) {
			int tmp = inventory.getItem(seedID).getStackSize();
			for (int i = 0; i < 5; i++) {
				if (tmp == inventory.getItem(seedID).getStackSize() && currentState == State.PLANT) {
					if (i > 0) {
						camera.setAngle(random(0, 360));
					}
					inventory.getItem(seedID).doClick(true);
					plot.doClick();
					sleep(random(2000, 3000));
				} else {
					if (currentState == State.PLANT) {
						numSeedsPlanted++;
					}
					break;
				}
			}
		}
		currentState = State.CHECK_CROPS;
	}

	private void noteHerbs() {
		if (inventory.containsOneOf(grimyID)) {
			status = "Noting herbs";
			RSNPC leprechaun = npcs.getNearest(leprechaunID);
			if (!leprechaun.isOnScreen() || calc.distanceTo(leprechaun) > 4) {
				walking.walkTileMM(leprechaun.getLocation(), 1, 1);
				sleep(random(3000, 4000));
			}
			int tmp = inventory.getCount(grimyID);
			for (int i = 0; i < 5; i++) {
				if (tmp == inventory.getCount(grimyID)) {
					inventory.getItem(grimyID).doAction("Use");
					sleep(200, 300);
					if (inventory.isItemSelected()) {
						leprechaun.doAction("Use");
						sleep(3000, 4000);
					}
				} else {
					break;
				}
			}
			RSObject plot = objects.getNearest(herbPlot);
			if (plot != null) {
				if (!plot.isOnScreen()) {
					walking.walkTileMM(plot.getLocation(), 1, 1);
					sleep(random(3000, 4000));
				}
			}
		}
	}

	private void checkEnergy() {
		if (walking.getEnergy() > random(55, 80)) {
			if (!walking.isRunEnabled()) {
				walking.setRun(true);
			}
			sleep(random(200, 300));
		}
	}

	// Walk to the specified location, and set the nextState once you get there
	private void walk(Location loc, State nextState) {
		if (getFarmingArea(loc).contains(getMyPlayer().getLocation())) {
			currentLocation = loc;
			currentState = nextState; // This is the last spot of each run. Break here.
		} else {
			checkEnergy();
			status = "Walking to " + loc.toString() + " herb patch";
			step(pathList);
		}
	}

	// Teleport to the specified location
	private void teleport(Location loc) {
		status = "Teleporting to " + loc.toString();
		if (magic.castSpell(getSpell(loc))) {
			sleep(random(9000, 11000));
			if (getTeleArea(loc).contains(getMyPlayer().getLocation())) { // Teleport successful if player is in the tele area
				currentState = State.WALKING;
				pathList = generatePath(getPath(loc));
			}
		}
	}

	private int getSpell(Location loc) {
		switch (loc) {
			case CAMELOT:
				return Magic.SPELL_CAMELOT_TELEPORT;
			case FALADOR:
				return Magic.SPELL_FALADOR_TELEPORT;
			case ARDOUGNE:
				return Magic.SPELL_ARDOUGNE_TELEPORT;
		}
		return -1; // Error
	}

	private Line[] getPath(Location loc) {
		switch (loc) {
			case CAMELOT:
				return camelotPath;
			case FALADOR:
				return faladorPath;
			case ARDOUGNE:
				return ardougnePath;
		}
		return new Line[]{new Line(0, 0, 0, 0)}; // Error
	}

	private RSArea getFarmingArea(Location loc) {
		switch (loc) {
			case CAMELOT:
				return camelotFarmingArea;
			case FALADOR:
				return faladorFarmingArea;
			case ARDOUGNE:
				return ardougneFarmingArea;
		}
		return new RSArea(0, 0, 0, 0); // Error
	}

	private RSArea getTeleArea(Location loc) {
		switch (loc) {
			case CAMELOT:
				return camelotTeleArea;
			case FALADOR:
				return faladorTeleArea;
			case ARDOUGNE:
				return ardougneTeleArea;
		}
		return new RSArea(0, 0, 0, 0); // Error
	}

	private boolean checkInventory() {
		return (inventory.contains(seedID) && inventory.contains(rakeID) && inventory.contains(dibberID) && inventory.contains(spadeID));
	}

	private void dropAll(ArrayList<Integer> ids) {
		for (RSItem item : inventory.getItems()) {
			if (ids.contains(item.getID())) {
				if (item.doAction("Drop")) {
					sleep(random(500, 700));
				}
			}
		}
	}

	private void updateHerbCount() {
		if (inventory.contains(notedID)) {
			numHerbsFarmed = inventory.getItem(notedID).getStackSize() - startingNoteSize;
		} else {
			numHerbsFarmed = 0;
		}
	}

	private void performMaintainance() {
		ArrayList<Integer> toDrop = new ArrayList<Integer>();
		toDrop.add(weedsID);
		toDrop.add(bucketID);
		toDrop.add(vialID);
		dropAll(toDrop);
		noteHerbs();
		updateHerbCount();
	}

	private void handleBreak() {
		game.logout(false);
		int sleepLength = random(900000, 1500000); // 15-25 minutes (grow a little then check on them)
		breakTimer = new Timer(sleepLength);
		status = "Breaking while herbs grow: ";
		sleep(sleepLength);
		status = "Logging back in";
		currentState = State.CHECK_CROPS;
	}

	public void messageReceived(MessageEvent e) {
		if (e.getID() != MessageEvent.MESSAGE_SERVER) {
			return;
		}
		String msg = e.getMessage();
		if (msg.contains("patch is fully")) {
			currentState = State.PICK;
		} else if (msg.contains("infected")) {
			currentState = State.CLEAR;
		} else if (msg.contains("needs attending")) {
			currentState = State.CURE;
		} else if (msg.contains("something growing")) {
			currentState = State.TELEPORT;
		} else if (msg.contains("weeding")) {
			currentState = State.RAKE;
		} else if (msg.contains("not been treated")) {
			currentState = State.COMPOST;
		} else if (msg.contains("supercompost. The patch is empty")) {
			currentState = State.PLANT;
		}
	}

	//START: Code generated using Enfilade's Easel
	private Image getImage(String url) {
		try {
			return ImageIO.read(new URL(url));
		} catch (IOException e) {
			return null;
		}
	}

	private final Color color1 = new Color(0, 1, 0);
	private final Color color2 = new Color(50, 110, 20);
	private final Color color3 = new Color(254, 255, 254);

	private final Font font1 = new Font("Arial", 1, 12);

	private final Image img1 = getImage("http://dl.dropbox.com/u/4287505/RSBot/sneakyFarmer/sneakyFarmerPaint.png");

	public void onRepaint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		String paintText = "Hide Paint";

		if (hidePaint) {
			paintText = "Show Paint";
			g.setColor(color1);
			g.fillRoundRect(closePaint.x, closePaint.y, closePaint.width, closePaint.height, 16, 16);
			g.setColor(color3);
			g.drawString(paintText, 435, 27);
		} else {
			g.setColor(color2);
			g.fillRoundRect(closePaint.x, closePaint.y, closePaint.width, closePaint.height, 16, 16);
			g.setColor(color3);
			g.drawString(paintText, 438, 27);

			final int skillPercent = skills.getPercentToNextLevel(Skills.FARMING);
			final int skillXP = skills.getExpToNextLevel(Skills.FARMING);
			final String xpToLevel;
			if (skillXP >= 1000) {
				xpToLevel = Integer.toString(skillXP / 1000) + "k";
			} else {
				xpToLevel = Integer.toString(skillXP);
			}
			final int nextLevel = skills.getRealLevel(Skills.FARMING) + 1;
			final int profit = marketPriceOfHerbs * numHerbsFarmed - marketPriceOfSeeds * numSeedsPlanted;
			final String profitMade;
			if (profit >= 100000) {
				profitMade = Integer.toString(profit / 1000) + "k";
			} else {
				profitMade = Integer.toString(profit);
			}

			String displayStatus = status;
			if (displayStatus == "Breaking while herbs grow: ") {
				displayStatus += breakTimer.toRemainingString();
			}

			int mouseX, mouseY;
			mouseX = (int) mouse.getLocation().getX();
			mouseY = (int) mouse.getLocation().getY();

			g.setColor(color1);
			g.drawLine(mouseX - 1000, mouseY, mouseX + 1000, mouseY);
			g.drawLine(mouseX, mouseY - 1000, mouseX, mouseY + 1000);
			g.fillRect(69, 320, 450, 21);
			g.setColor(color2);
			g.fillRect(69, 320, 450 * skillPercent / 100, 21);
			g.drawImage(img1, 0, 243, null);
			g.setFont(font1);
			g.drawString("Version: " + scriptVersion, 431, 471);
			g.setColor(color3);
			g.drawString(skillPercent + "% to level " + nextLevel + " (" + xpToLevel + " xp)", 216, 334);
			g.setColor(color1);
			g.drawString("Time Running: " + runTime.toElapsedString(), 102, 398);
			g.drawString("Herbs Farmed: " + numHerbsFarmed, 319, 399);
			g.drawString("Profit Made: " + profitMade, 319, 416);
			g.drawString("Seeds Planted: " + numSeedsPlanted, 102, 415);
			g.drawString("Seed Type: " + seedType, 102, 431);
			g.drawString("Status: " + displayStatus, 102, 449);

			if (currentLocation == Location.ARDOUGNE) {
				for (int i = 1; i < camelotPath.length; i++) {
					camelotPath[i].drawTo(g1, camelotPath[i - 1]);
				}
			}
			if (currentLocation == Location.CAMELOT) {
				for (int i = 1; i < faladorPath.length; i++) {
					faladorPath[i].drawTo(g1, faladorPath[i - 1]);
				}
			}
			if (currentLocation == Location.FALADOR) {
				for (int i = 1; i < ardougnePath.length; i++) {
					ardougnePath[i].drawTo(g1, ardougnePath[i - 1]);
				}
			}
		}
	}
	//END: Code generated using Enfilade's Easel

	public void mouseClicked(MouseEvent e) {
		p = e.getPoint();
		if (closePaint.contains(p)) {
			hidePaint = !hidePaint;
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public class HerbFarmerGUI extends JFrame {
		private static final long serialVersionUID = 1L;
		private JLabel label1;
		private JComboBox comboBox1;
		private JButton button1;

		public HerbFarmerGUI() {
			initComponents();
		}

		private void initComponents() {
			label1 = new JLabel();
			comboBox1 = new JComboBox();
			button1 = new JButton();

			//======== this ========
			setTitle("SneakyFarmer");
			Container contentPane = getContentPane();
			contentPane.setLayout(null);

			//---- label1 ----
			label1.setText("Which herb to farm:");
			label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 1f));
			contentPane.add(label1);
			label1.setBounds(10, 10, 125, 35);

			//---- comboBox1 ----
			comboBox1.setModel(new DefaultComboBoxModel(new String[]{
					"Guam",
					"Marrentill",
					"Tarromin",
					"Harralander",
					"Ranarr",
					"Toadflax",
					"Irit",
					"Avantoe",
					"Kwuarm",
					"Snapdragon",
					"Cadantine",
					"Lantadyme",
					"Dwarf Weed",
					"Torstol"
			}));
			contentPane.add(comboBox1);
			comboBox1.setBounds(135, 10, 90, 35);

			//---- button1 ----
			button1.setText("Start Farming!");
			button1.setFont(button1.getFont().deriveFont(button1.getFont().getSize() + 7f));
			contentPane.add(button1);
			button1.setBounds(10, 50, 215, 35);
			button1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					startActionPerformed(e);
				}
			});

			{ // compute preferred size
				Dimension preferredSize = new Dimension();
				for (int i = 0; i < contentPane.getComponentCount(); i++) {
					Rectangle bounds = contentPane.getComponent(i).getBounds();
					preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
					preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
				}
				Insets insets = contentPane.getInsets();
				preferredSize.width += insets.right;
				preferredSize.height += insets.bottom;
				contentPane.setMinimumSize(preferredSize);
				contentPane.setPreferredSize(preferredSize);
			}
			pack();
			setLocationRelativeTo(getOwner());
		}

		private void startActionPerformed(ActionEvent e) {
			seedType = comboBox1.getSelectedItem().toString();
			if (seedType.equals("Guam")) {
				seedID = 5291;
				grimyID = 199;
			} else if (seedType.equals("Marrentill")) {
				seedID = 5292;
				grimyID = 201;
			} else if (seedType.equals("Tarromin")) {
				seedID = 5293;
				grimyID = 203;
			} else if (seedType.equals("Harralander")) {
				seedID = 5294;
				grimyID = 205;
			} else if (seedType.equals("Ranarr")) {
				seedID = 5295;
				grimyID = 207;
			} else if (seedType.equals("Toadflax")) {
				seedID = 5296;
				grimyID = 3049;
			} else if (seedType.equals("Irit")) {
				seedID = 5297;
				grimyID = 209;
			} else if (seedType.equals("Avantoe")) {
				seedID = 5298;
				grimyID = 211;
			} else if (seedType.equals("Kwuarm")) {
				seedID = 5299;
				grimyID = 213;
			} else if (seedType.equals("Snapdragon")) {
				seedID = 5300;
				grimyID = 3051;
			} else if (seedType.equals("Cadantine")) {
				seedID = 5301;
				grimyID = 215;
			} else if (seedType.equals("Lantadyme")) {
				seedID = 5302;
				grimyID = 2485;
			} else if (seedType.equals("Dwarf Weed")) {
				seedID = 5303;
				grimyID = 217;
			} else if (seedType.equals("Torstol")) {
				seedID = 5304;
				grimyID = 219;
			}

			setVisible(false);
			dispose();
		}
	}

	// Credits go to Unknown500 for the AntiBan skeleton code
	public class AntiBan extends Thread {

		private SneakyFarmer parent;
		private Random randomGenerator;

		AntiBan(SneakyFarmer parent) {
			this.parent = parent;
			this.randomGenerator = new Random();
		}

		public void run() {
			try {
				while (scriptRunning) {
					if (!parent.isPaused() && parent.game.isLoggedIn() && status != "Logging back in") {
						int rand = randomGenerator.nextInt(30);
						switch (rand) {
							case 1:
								parent.camera.setAngle(parent.random(0, 360));
								break;
							case 2:
								parent.camera.setPitch(parent.random(40, 100));
								break;
							case 3:
								parent.mouse.setSpeed(parent.random(5, 9));
								break;
							case 5:
								parent.camera.setPitch(parent.random(40, 100));
								parent.camera.setAngle(parent.random(0, 360));
								break;
							default:
								break;
						}
					}

					sleep(parent.random(2000, 5000));
				}
			} catch (InterruptedException e) {
				log(e.getMessage());
			}
		}
	}

	//Credits go to Enfilade for this walking method
	private boolean tileInNextRange(RSTile t) {
		return calc.distanceBetween(t, getMyPlayer().getLocation()) < nextStep;
	}

	private int nextStep = 12;

	private int step(ArrayList<RSTile> path) {
		if (calc.distanceBetween(getMyPlayer().getLocation(), path.get(path.size() - 1)) < 2) {
			return path.size();
		}
		RSTile dest = walking.getDestination();
		int index = -1;
		int shortestDist = 0, dist, shortest = -1;
		if (dest != null) {
			for (int i = 0; i < path.size(); i++) {
				dist = (int) calc.distanceBetween(path.get(i), dest);
				if (shortest < 0 || shortestDist > dist) {
					shortest = i;
					shortestDist = dist;
				}
			}
		}
		for (int i = path.size() - 1; i >= 0; i--) {
			if (tileInNextRange(path.get(i))) {
				index = i;
				break;
			}
		}
		if (index >= 0 && (dest == null || (index > shortest) || !getMyPlayer().isMoving())) {
			walking.walkTileMM(path.get(index));
			nextStep = random(12, 15);
			return index;
		}
		return -1;
	}

	private ArrayList<RSTile> generatePath(Line[] lines) {
		double minStep = 5, maxStep = 10, wander = 3;
		if (lines.length < 2) {
			return null;
		}
		ArrayList<RSTile> path = new ArrayList<RSTile>();
		Line l1, l2 = lines[0];
		double distFromCenter = random(0, l2.getDistance() + 1);
		RSTile p = l2.translate((int) distFromCenter);
		distFromCenter = l2.getDistance() / 2 - distFromCenter;
		double centerXdist, centerYdist,
				line1Xdist, line1Ydist,
				line2Xdist, line2Ydist;
		double line1dist, line2dist, centerDist;
		double x, y;
		double distOnLine, last, cap1, cap2, move;
		double distFromCenterX1, distFromCenterY1, distFromCenterX2, distFromCenterY2;
		double force1, force2, slopeX, slopeY, slopeDist;
		boolean finished;
		int lastX = p.getX(), lastY = p.getY(), curX, curY;
		double dist, xdist, ydist;
		for (int i = 1; i < lines.length; i++) {
			l1 = l2;
			l2 = lines[i];
			centerXdist = l2.getCenterX() - l1.getCenterX();
			centerYdist = l2.getCenterY() - l1.getCenterY();
			centerDist = Math.sqrt(centerXdist * centerXdist + centerYdist * centerYdist);
			line1Xdist = l2.getX() - l1.getX();
			line1Ydist = l2.getY() - l1.getY();
			line2Xdist = l2.getX2() - l1.getX2();
			line2Ydist = l2.getY2() - l1.getY2();

			centerXdist /= centerDist;
			centerYdist /= centerDist;
			line1Xdist /= centerDist;
			line1Ydist /= centerDist;
			line2Xdist /= centerDist;
			line2Ydist /= centerDist;
			distOnLine = 0;
			last = 0;
			finished = false;
			while (!finished) {

				distOnLine += random(minStep, maxStep);
				if (distOnLine >= centerDist) {
					distOnLine = centerDist;
					finished = true;
				}
				x = centerXdist * distOnLine + l1.getCenterX();
				y = centerYdist * distOnLine + l1.getCenterY();

				distFromCenterX1 = x - (line1Xdist * distOnLine + l1.getX());
				distFromCenterY1 = y - (line1Ydist * distOnLine + l1.getY());

				distFromCenterX2 = x - (line2Xdist * distOnLine + l1.getX2());
				distFromCenterY2 = y - (line2Ydist * distOnLine + l1.getY2());

				slopeX = distFromCenterX2 - distFromCenterX1;
				slopeY = distFromCenterY2 - distFromCenterY1;
				slopeDist = Math.sqrt(slopeX * slopeX + slopeY * slopeY);
				slopeX /= slopeDist;
				slopeY /= slopeDist;

				line1dist = Math.sqrt(distFromCenterX1 * distFromCenterX1 +
						distFromCenterY1 * distFromCenterY1);
				line2dist = Math.sqrt(distFromCenterX2 * distFromCenterX2 +
						distFromCenterY2 * distFromCenterY2);

				move = (distOnLine - last) / maxStep * wander;

				force1 = line1dist + distFromCenter;
				force2 = line2dist - distFromCenter;

				cap1 = Math.min(move, force1);
				cap2 = Math.min(move, force2);

				if (force1 < 0) {
					distFromCenter -= force1;
				} else if (force2 < 0) {
					distFromCenter += force2;
				} else {
					distFromCenter += random(-cap1, cap2);
				}

				if (finished) {
					RSTile t = l2.translateFromCenter(distFromCenter);
					curX = t.getX();
					curY = t.getY();
				} else {
					curX = (int) Math.round(distOnLine * centerXdist + l1.getCenterX() + distFromCenter * slopeX);
					curY = (int) Math.round(distOnLine * centerYdist + l1.getCenterY() + distFromCenter * slopeY);
				}

				xdist = curX - lastX;
				ydist = curY - lastY;
				dist = Math.sqrt(xdist * xdist + ydist * ydist);
				xdist /= dist;
				ydist /= dist;
				for (int j = 0; j < dist; j++) {
					path.add(new RSTile((int) Math.round(xdist * j + lastX), (int) Math.round(ydist * j + lastY)));
				}

				last = distOnLine;
				lastX = curX;
				lastY = curY;
			}
		}
		return cutUp(path);
	}

	public ArrayList<RSTile> cutUp(ArrayList<RSTile> tiles) {
		ArrayList<RSTile> path = new ArrayList<RSTile>();
		int index = 0;
		while (index < tiles.size()) {
			path.add(tiles.get(index));
			index += random(8, 12);
		}
		if (!path.get(path.size() - 1).equals(tiles.get(tiles.size() - 1))) {
			path.add(tiles.get(tiles.size() - 1));
		}
		return path;
	}

	private final Color POLY_BORDER = new Color(150, 0, 150), POLY_FILL = new Color(150, 0, 150, 80);

	private class Line {
		private int x, y, xdist, ydist, x2, y2, centerX, centerY;
		private RSTile t1, t2;
		private double dist;

		public Line(int x1, int y1, int x2, int y2) {
			t1 = new RSTile(x1, y1);
			t2 = new RSTile(x2, y2);
			x = x1;
			y = y1;
			this.x2 = x2;
			this.y2 = y2;
			xdist = x2 - x1;
			ydist = y2 - y1;
			centerX = x + (int) (0.5 * xdist);
			centerY = y + (int) (0.5 * ydist);
			dist = Math.sqrt(xdist * xdist + ydist * ydist);
		}

		public int getCenterX() {
			return centerX;
		}

		public int getCenterY() {
			return centerY;
		}

		public RSTile getRandomRSTile() {
			double rand = Math.random();
			return new RSTile(x + (int) (xdist * rand), y + (int) (ydist * rand));
		}

		public RSTile getTile1() {
			return t1;
		}

		public RSTile getTile2() {
			return t2;
		}

		public void drawTo(Graphics g, Line line) {
			if (!calc.tileOnMap(t1) || !calc.tileOnMap(t2)) {
				return;
			}
			if (calc.tileOnMap(line.getTile1()) && calc.tileOnMap(line.getTile2())) {
				Point p1 = calc.tileToMinimap(t1);
				Point p2 = calc.tileToMinimap(t2);
				Point p3 = calc.tileToMinimap(line.getTile2());
				Point p4 = calc.tileToMinimap(line.getTile1());
				GeneralPath path = new GeneralPath();
				path.moveTo(p1.x, p1.y);
				path.lineTo(p2.x, p2.y);
				path.lineTo(p3.x, p3.y);
				path.lineTo(p4.x, p4.y);
				path.closePath();
				g.setColor(POLY_FILL);
				((Graphics2D) g).fill(path);
				((Graphics2D) g).draw(path);
			}
			Point last = null, p;
			g.setColor(Color.ORANGE);
			for (RSTile t : pathList) {
				if (calc.tileOnMap(t)) {
					p = calc.tileToMinimap(t);
					g.fillOval(p.x - 2, p.y - 2, 5, 5);
					if (last != null) {
						g.drawLine(p.x, p.y, last.x, last.y);
					}
					last = p;
				} else {
					last = null;
				}
			}
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getX2() {
			return x2;
		}

		public int getY2() {
			return y2;
		}

		public int getXDistance() {
			return xdist;
		}

		public int getYDistance() {
			return ydist;
		}

		public double getDistance() {
			return dist;
		}

		public RSTile translate(double length) {
			return new RSTile((int) Math.round(length * (xdist / dist)) + x, (int) Math.round(length * (ydist / dist)) + y);
		}

		public RSTile translateFromCenter(double length) {
			return new RSTile((int) Math.round(centerX - (xdist / dist) * length), (int) Math.round(centerY - (ydist / dist) * length));
		}
	}
}