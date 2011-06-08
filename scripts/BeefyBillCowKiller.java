import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.rsbot.Configuration;
import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Skills;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSComponent;
import org.rsbot.script.wrappers.RSGroundItem;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPath;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Mr. Byte" }, name = "Beefy Bill Cow Killer", keywords = { "Combat" }, description = "Kills Cows, Loots Hides,"
		+ " Buries Bones, Chops Trees,"
		+ " Makes Fire, Cooks Looted Meat "
		+ "(phew!).", version = 3.31)
public class BeefyBillCowKiller extends Script implements PaintListener,
		MouseListener, MessageListener {

	private enum action {
		FIGHTING, LOOTING, ATTACKING, BANKING, EATING, COOKING, WALKING, CLIMBING, BANKSTAFFS;
	}

	public class BeefyGUI extends JFrame {
		private JPanel dialogPane;
		private JPanel contentPanel;
		private JCheckBox guiBuryBones;
		private JCheckBox guiEatFood;
		private JCheckBox guiPickStaff;
		private JSlider HPPercentToEat;
		private JCheckBox restWhenTired;
		private JCheckBox guiBankMeat;
		private JCheckBox guiBankHides;
		private JComboBox hideBankCount;
		private JLabel howManyLabel;
		private JCheckBox guiGetFood;
		private JCheckBox guiCheckForUpdates;
		private JLabel jLabel1;
		private JCheckBox beVerbose;
		private JButton runButton;
		private JButton cancelButton;

		public BeefyGUI() {
			initComponents();
		}

		private void bankingMeatItemStateChanged(final ItemEvent e) {
			hideBankCount.setEnabled((guiBankHides.isSelected() && !guiBankMeat.isSelected()));
			howManyLabel.setEnabled(hideBankCount.isEnabled());
			guiBuryBones.setEnabled(guiBankHides.isSelected()
					|| guiBankMeat.isSelected());
			guiBuryBones.setSelected(guiBuryBones.isSelected()
					&& (guiBankHides.isSelected() || guiBankMeat.isSelected()));
		}

		private void cancelButtonActionPerformed(final ActionEvent e) {
			guiExit = true;
			gui.dispose();
			return;
		}

		private void getHidesItemStateChanged(final ItemEvent e) {
			hideBankCount.setEnabled((guiBankHides.isSelected() && !guiBankMeat.isSelected()));
			howManyLabel.setEnabled(hideBankCount.isEnabled());
			guiBuryBones.setEnabled(guiBankHides.isSelected()
					|| guiBankMeat.isSelected());
			guiBuryBones.setSelected(guiBuryBones.isSelected()
					&& (guiBankHides.isSelected() || guiBankMeat.isSelected()));
		}

		private void HPPercentToEatStateChanged(final ChangeEvent e) {
			HPPercentToEat.setBorder(new TitledBorder("Eat at "
					+ HPPercentToEat.getValue() + "% HP"));
			return;
		}

		private void iCanHazCheezBurgerActionPerformed(final ActionEvent e) {
			HPPercentToEat.setEnabled(guiEatFood.isSelected());
			if (inventory.containsOneOf(hatchets)
					&& inventory.contains(tinderbox)) {
				guiGetFood.setEnabled(guiEatFood.isSelected());
				guiGetFood.setSelected(guiGetFood.isSelected()
						&& guiEatFood.isSelected());
			}
			return;
		}

		private void initComponents() {

			contentPanel = new JPanel();
			guiBuryBones = new JCheckBox();
			guiPickStaff = new JCheckBox();
			guiEatFood = new JCheckBox();
			HPPercentToEat = new JSlider();
			restWhenTired = new JCheckBox();
			guiBankMeat = new JCheckBox();
			guiBankHides = new JCheckBox();
			hideBankCount = new JComboBox();
			howManyLabel = new JLabel();
			guiGetFood = new JCheckBox();
			runButton = new JButton();
			cancelButton = new JButton();

			setTitle("Beefy Bill Cow Killer v" + version);
			setFont(new Font("Arial", Font.PLAIN, 12));
			setResizable(false);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(final WindowEvent e) {
					thisWindowClosing(e);
				}
			});
			final Container contentPane = getContentPane();
			contentPane.setLayout(new BorderLayout());
			{
				dialogPane = new JPanel();
				contentPane.add(dialogPane, BorderLayout.CENTER);
				dialogPane.setLayout(new BorderLayout());
				dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
				dialogPane.setFont(new Font("Arial", Font.PLAIN, 12));
				dialogPane.setPreferredSize(new java.awt.Dimension(533, 450));
				{
					contentPanel = new JPanel();
					dialogPane.add(contentPanel, BorderLayout.CENTER);
					{
						guiBuryBones = new JCheckBox();
						contentPanel.add(guiBuryBones);
						guiBuryBones.setText("Get/Bury Bones?");
						guiBuryBones.setSelected(true);
						guiBuryBones.setFont(new Font("Arial", Font.PLAIN, 12));
						guiBuryBones.setToolTipText("The much asked for option to turn off bone grabbing. Un-check this to not grab bones.");
						guiBuryBones.setBounds(12, 60, 126, 19);
					}
					{
						guiBankHides = new JCheckBox();
						contentPanel.add(guiBankHides);
						guiBankHides.setText("Bank Hides?");
						guiBankHides.setSelected(true);
						guiBankHides.setFont(new Font("Arial", Font.PLAIN, 12));
						guiBankHides.addItemListener(new ItemListener() {
							public void itemStateChanged(final ItemEvent e) {
								getHidesItemStateChanged(e);
							}
						});
						guiBankHides.setBounds(217, 60, 103, 19);
					}
					{
						guiBankMeat = new JCheckBox();
						contentPanel.add(guiBankMeat);
						guiBankMeat.setText("Banking Meat?");
						guiBankMeat.setSelected(false);
						guiBankMeat.setFont(new Font("Arial", Font.PLAIN, 12));
						guiBankMeat.addItemListener(new ItemListener() {
							public void itemStateChanged(final ItemEvent e) {
								bankingMeatItemStateChanged(e);
							}
						});
						guiBankMeat.setBounds(358, 60, 138, 19);
					}
					{
						guiPickStaff = new JCheckBox();
						contentPanel.add(guiPickStaff);
						guiPickStaff.setText("Get Air Staffs?");
						guiPickStaff.setSelected(true);
						guiPickStaff.setFont(new Font("Arial", Font.PLAIN, 12));
						guiPickStaff.setToolTipText("As requested by HawkenX");
						guiPickStaff.setBounds(12, 90, 126, 19);
					}
					{
						restWhenTired = new JCheckBox();
						contentPanel.add(restWhenTired);
						restWhenTired.setText("Rest when out of run energy");
						restWhenTired.setSelected(true);
						restWhenTired.setFont(new Font("Arial", Font.PLAIN, 12));
						restWhenTired.setToolTipText("You'll be running alot. Rest when you're tired?");
						restWhenTired.setBounds(153, 90, 200, 19);
					}
					{
						guiEatFood = new JCheckBox();
						contentPanel.add(guiEatFood);
						guiEatFood.setText("Use food?");
						guiEatFood.setFont(new Font("Arial", Font.PLAIN, 12));
						guiEatFood.setToolTipText("Check here to eat when injured.");
						guiEatFood.addActionListener(new ActionListener() {
							public void actionPerformed(final ActionEvent e) {
								iCanHazCheezBurgerActionPerformed(e);
							}
						});
						guiEatFood.setBounds(12, 120, 150, 19);
					}
					{
						guiGetFood = new JCheckBox();
						contentPanel.add(guiGetFood);
						guiGetFood.setText("Get Food? (Requires Hatchet/Tinder)");
						guiGetFood.setToolTipText("If you are holding a hatchet and tinder, you can get food.");
						guiGetFood.setFont(new Font("Arial", Font.PLAIN, 12));
						guiGetFood.setBounds(217, 120, 250, 19);
					}
					{
						guiGetFood.setEnabled((guiEatFood.isSelected()
								&& inventory.contains(tinderbox) && inventory.containsOneOf(hatchets)));

						guiCheckForUpdates = new JCheckBox();
						contentPanel.add(guiCheckForUpdates);
						guiCheckForUpdates.setText("Download Updates?");
						guiCheckForUpdates.setToolTipText("Enable this to download updates from Mr. Byte.");
						guiCheckForUpdates.setFont(new Font("Arial", Font.PLAIN, 12));
						guiCheckForUpdates.setBounds(12, 329, 145, 23);

						beVerbose = new JCheckBox();
						contentPanel.add(beVerbose);
						beVerbose.setText("Verbose Log?");
						beVerbose.setToolTipText("Enable extra Log Messages, AKA: Log SPAM!");
						beVerbose.setFont(new Font("Arial", Font.PLAIN, 12));
						beVerbose.setBounds(358, 329, 138, 23);
						beVerbose.setDoubleBuffered(true);
					}
					{
						HPPercentToEat = new JSlider();
						contentPanel.add(HPPercentToEat);
						HPPercentToEat.setMajorTickSpacing(10);
						HPPercentToEat.setMinimum(50);
						HPPercentToEat.setMinorTickSpacing(5);
						HPPercentToEat.setPaintLabels(true);
						HPPercentToEat.setPaintTicks(true);
						HPPercentToEat.setSnapToTicks(true);
						HPPercentToEat.setValue(75);
						HPPercentToEat.setEnabled(guiEatFood.isSelected());
						HPPercentToEat.setBorder(new TitledBorder("Eat at "
								+ HPPercentToEat.getValue() + "% HP"));
						HPPercentToEat.setFont(new Font("Arial", Font.PLAIN, 12));
						HPPercentToEat.setToolTipText("Adjust to the HP% you wish to eat at.");
						HPPercentToEat.setEnabled(guiEatFood.isSelected());
						HPPercentToEat.addChangeListener(new ChangeListener() {
							public void stateChanged(final ChangeEvent e) {
								HPPercentToEatStateChanged(e);
							}
						});
						HPPercentToEat.setBounds(10, 150, 495, 80);
					}
					{
						hideBankCount = new JComboBox();
						contentPanel.add(hideBankCount);
						hideBankCount.setModel(new DefaultComboBoxModel(new String[] {
								"10 Hides", "20 Hides" }));
						hideBankCount.setBounds(206, 295, 110, 30);
					}
					{
						howManyLabel = new JLabel();
						contentPanel.add(howManyLabel);
						howManyLabel.setText("How many cowhides will we bank each run?");
						howManyLabel.setFont(new Font("Arial", Font.PLAIN, 12));
						howManyLabel.setEnabled(true);
						howManyLabel.setHorizontalAlignment(SwingConstants.CENTER);
						howManyLabel.setBounds(10, 265, 513, 30);

					}

					{
						runButton = new JButton();
						contentPanel.add(runButton);
						runButton.setText("Let's Kill Cows!");
						runButton.setFont(new Font("Arial", Font.PLAIN, 12));
						runButton.addActionListener(new ActionListener() {
							public void actionPerformed(final ActionEvent e) {
								runButtonActionPerformed(e);
							}
						});
						runButton.setBounds(0, 365, 177, 33);
						runButton.setSize(211, 34);
					}
					{
						cancelButton = new JButton();
						contentPanel.add(cancelButton);
						cancelButton.setText("NOOO! Cows are Cuuuute!");
						cancelButton.setFont(new Font("Arial", Font.PLAIN, 12));
						cancelButton.addActionListener(new ActionListener() {
							public void actionPerformed(final ActionEvent e) {
								cancelButtonActionPerformed(e);
							}
						});
						cancelButton.setBounds(284, 365, 211, 34);
					}
					{
						jLabel1 = new JLabel();
						contentPanel.add(jLabel1);
						jLabel1.setText("Beefy Bill Cow Killer v" + version);
						jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
						jLabel1.setEnabled(true);
						jLabel1.setFont(new java.awt.Font("Arial", 0, 28));
						jLabel1.setBounds(10, 0, 513, 57);
					}
					contentPanel.setFont(new Font("Arial", Font.PLAIN, 12));
					contentPanel.setLayout(null);
					contentPanel.setPreferredSize(new java.awt.Dimension(501, 360));
				}
			}
			this.setSize(539, 480);
			setLocationRelativeTo(getOwner());
		}

		private void runButtonActionPerformed(final ActionEvent e) {
			runButtonPushed = true;
			iCanHazCheezBurger = guiEatFood.isSelected();
			bankingHides = guiBankHides.isSelected();
			gettingBones = guiBuryBones.isSelected();
			bankingMeats = guiBankMeat.isSelected();
			bankingStaffs = guiPickStaff.isSelected();
			update = guiCheckForUpdates.isSelected();
			verbose = beVerbose.isSelected();

			if (iCanHazCheezBurger) {
				whenToEat = (double) HPPercentToEat.getValue() / 100; // Convert
																		// 75 to
																		// .75
				iCanCookCheezBurger = guiGetFood.isSelected();
			}
			if (!restWhenTired.isSelected()) {
				rndEnergy = 0;
			}
			if (bankingHides) {
				if (hideBankCount.getSelectedItem() == "20 Hides") {
					hidesToBank = 20;
				}
			} else {
				hidesToBank = 0;
			}

			if (bankingHides) {
				loots[1] = cowhideID;
			}
			if (gettingBones) {
				loots[2] = bones;
			}
			if (bankingMeats) {
				loots[0] = rawMeat;
			}
			if (bankingStaffs) {
				loots[4] = airStaff;
			} else {
				junk[2] = airStaff;
			}

			gui.dispose();
			return;
		}

		private void thisWindowClosing(final WindowEvent e) {
			if (!runButtonPushed) {
				guiExit = true;
				return;
			}
			gui.dispose();
			return;
		}
	}

	private boolean isJar = false;
	private static final RSArea billPen = new RSArea(new RSTile[] {
			new RSTile(3154, 3348), new RSTile(3153, 3345),
			new RSTile(3152, 3325), new RSTile(3152, 3323),
			new RSTile(3154, 3316), new RSTile(3156, 3314),
			new RSTile(3160, 3314), new RSTile(3171, 3316),
			new RSTile(3181, 3314), new RSTile(3192, 3306),
			new RSTile(3215, 3308), new RSTile(3205, 3334),
			new RSTile(3179, 3348) }),
			gateArea = new RSArea(3175, 3312, 3178, 3315),
			bankArea = new RSArea(3206, 3217, 3211, 3223),
			lumStairs = new RSArea(3205, 3206, 3208, 3211),
			teleLanding = new RSArea(3218, 3216, 3226, 3221);

	private static final int gate = 45206;

	private static final int[] lumbyStairs = { 36773, 36774, 36775 };

	private static final RSArea keepOut = new RSArea(3152, 3330, 3171, 3341);
	// keepOut is for keeping out of the northern pen, it's just a pain in the
	// ass.
	private static final RSTile[] lumBank2BillPen = { new RSTile(3205, 3209),
			new RSTile(3208, 3209), new RSTile(3213, 3209),
			new RSTile(3215, 3214), new RSTile(3217, 3218),
			new RSTile(3223, 3219), new RSTile(3228, 3219),
			new RSTile(3236, 3219), new RSTile(3236, 3226),
			new RSTile(3231, 3230), new RSTile(3226, 3235),
			new RSTile(3223, 3242), new RSTile(3220, 3248),
			new RSTile(3217, 3252), new RSTile(3213, 3265),
			new RSTile(3215, 3273), new RSTile(3213, 3277),
			new RSTile(3205, 3280), new RSTile(3196, 3280),
			new RSTile(3192, 3289), new RSTile(3189, 3293),
			new RSTile(3189, 3299), new RSTile(3186, 3306),
			new RSTile(3178, 3310), new RSTile(3177, 3315) },
			landingToStairs = { new RSTile(3232, 3218), new RSTile(3224, 3218),
					new RSTile(3217, 3219), new RSTile(3214, 3214),
					new RSTile(3212, 3211), new RSTile(3206, 3209) };
	// safespot is just north of the pen.
	private static final DecimalFormat k = new DecimalFormat("#.#");
	private static final DecimalFormat whole = new DecimalFormat("####");
	private static final String[] skillNames$ = { "Attack", "Defense",
			"Strength", "Constitution", "Range", "Prayer", "Magic" };
	private static final String[] statusNames$ = { "Starting Up", "Cooking",
			"Eating", "Banking", "Fighting", "Looting", "Attacking", "Resting",
			"Burying", "Dumping Junk", "Returning to Pen", "Looking for a cow",
			"Quitting...", "Gathering Arrows" };
	private static final Color[] statusColors = { Color.GREEN.darker(),
			Color.CYAN.darker(), Color.ORANGE.brighter(),
			Color.GREEN.brighter(), Color.RED.brighter() };
	private static final int bones = 526; // , 532, 530, 528, 3183, 2859 };

	/*
	 * It appears that the poisoned variety of arrow is 1 greater than the
	 * normal one. Bronze 882 * Iron 884 * Steel 886 * Mithril 888 * Adamant 890
	 * * Rune 892 Bronze Bolts: 877 Training Arrows: 9706
	 */

	private static final int[] hatchets = { 1349, 1351, 1353, 1355, 1357, 1359,
			1361, 6739 };

	/*
	 * Training Bow: 9705
	 * 
	 * Longbows: plain: 829 Oak: 845 Willow: 847 Maple: 851
	 * 
	 * Shortbows: Plain: 841 Oak: 843 Maple: 849 Willow: 853
	 * 
	 * Crossbow: 837
	 */

	private static final int[] arrows = { 877, 878, 882, 883, 884, 885, 886,
			887, 888, 889, 890, 891, 892, 893, 9706 };

	/*
	 * {conditional meat, conditional bones, conditional air staff, burnt meat,
	 * logs, sling, candles, cake} The 2146's at the beginning are place holders
	 * and harmless if never changed. The candles/cake are from the 10th anniv.
	 * stuff.
	 */

	private static final int[] bows = { 829, 837, 839, 841, 843, 845, 847, 849,
			851, 853, 9705 };
	private final int[] junk = { 2146, 2146, 2146, 2146, 1511, 1521, 19830,
			20114, 20111 };
	private static final int beefyBillID = 246;
	private static final int cowhideID = 1739;
	private static final int beefyBillInterface = 236;
	private RSComponent cookingIface, amountIncreaseButton,
			amountDecreaseButton;
	private static final int[] liveTree = { 1278, 1276 };
	private static final int[] logs = { 1511, 1521 };
	private static final int tinderbox = 590;
	private static final int litFire = 2732;
	private static final int rawMeat = 2132;
	private static final int cookedMeat = 2142;

	private static final int airStaff = 1381;

	private final int[] loots = { 0, 0, 0, 0, 0 };
	private long startTime, lastTele = 0;
	private int[] startXP;
	private int hidesToBank = 10;
	private int rndEnergy = 23;
	private int status = 0, meatHeld, cookedMeatHeld, rawMeatHeld, meatSpace,
			meatsLooted, meatCycles, meatsBanked, meatPrice, meatsValue;

	private int hidePrice, hidesValue, hidesBanked, hidesLooted, hidesHeld,
			bankCycles, hideCost = 0;
	private double whenToEat = 0;
	private boolean gettingBones = false;
	private boolean bankingHides = true;
	private boolean bankingMeats = false;
	private boolean bankingStaffs = false;
	private boolean needMeat = false;
	private boolean guiExit = false, runButtonPushed, iCanHazCheezBurger,
			iCanCookCheezBurger, update, quitNow;
	private String bone$ = "Getting Bones: Yes";
	private String meat$ = "Getting Meats: Yes";

	private String hide$ = "Getting Hides: NO!!";
	private final double version = BeefyBillCowKiller.class.getAnnotation(ScriptManifest.class).version();
	public boolean verbose = false, outOfPen = false;
	BeefyGUI gui;
	private RSNPC cow, bill;
	private RSTile me, tile;
	private int searchTime = 0;

	private int arrowID;

	private boolean ranging;
	private RSPath path;
	Rectangle paintToggle = new Rectangle(390, 343, 120, 15);

	public boolean showPaint = true;

	public String buttonText = "Hide Paint";

	public File PRICE_FILE = new File(new File(Configuration.Paths.getScriptCacheDirectory()), "GEPrices.txt");

	private action action() {
		meatHeld = inventory.getCount(cookedMeat) + inventory.getCount(rawMeat);
		cookedMeatHeld = inventory.getCount(cookedMeat);
		rawMeatHeld = inventory.getCount(rawMeat);
		me = players.getMyPlayer().getLocation();

		if (billPen.contains(me)) {
			if (meatSpace > 0 && cookedMeatHeld < meatSpace && rawMeatHeld > 0) {
				status = 1;
				return action.COOKING;
			} else if (iCanHazCheezBurger
					&& combat.getLifePoints() < skills.getRealLevel(3) * 10
							* whenToEat) {
				status = 2;
				return action.EATING;
			} else if (bankingHides
					&& inventory.getCount(cowhideID) >= hidesToBank
					|| bankingMeats && inventory.getCount(rawMeat) >= 10) {
				status = 3;
				return action.BANKING;
			} else if (players.getMyPlayer().getInteracting() != null) {
				status = 4;
				return action.FIGHTING;
			} else if ((bankingHides || meatHeld < meatSpace || bankingMeats || gettingBones)
					&& findLoot() != null) {
				if (lootCheck()) {
					status = 5;
					return action.LOOTING;
				}
			}
			status = 6;
			if (verbose) {
				log("Attacking");
			}
			return action.ATTACKING;
		} else {
			final int plane = game.getPlane();
			if (lumStairs.contains(me)) {
				return action.CLIMBING;
			}
			if (plane == 2 && bankArea.contains(me)) {
				return action.BANKSTAFFS;
			}

			return action.WALKING;
		}
	}

	private boolean adjustCookLevel(final int toCook) {
		interfaces.getComponent(916, 17);
		amountDecreaseButton = interfaces.getComponent(916, 20);
		amountIncreaseButton = interfaces.getComponent(916, 19);
		amountDecreaseButton.doClick(true);
		sleep(250);
		int count = 0;
		while (Integer.parseInt("0"
				+ interfaces.getComponent(916, 17).getText()) != toCook) {
			if (Integer.parseInt("0"
					+ interfaces.getComponent(916, 17).getText()) < toCook) {
				amountIncreaseButton.doClick(true);
			} else {
				amountDecreaseButton.doClick(true);
			}
			sleep(250, 300);
			count++;
			if (count > 10) {
				return false;
			}
		}
		return true;
	}

	// Subroutines:

	private void bankAtBill(final int itemID, final String name) {
		while (inventory.getCount(itemID) > 0) {
			inventory.getItem(itemID).doClick(true);
			clickNPC(bill, "Use " + name);
			sleep(1500, 1600);
			if (interfaces.getComponent(beefyBillInterface, 1).doClick()) {
				sleep(1500, 2000);
			}
		}
		return;
	}

	private void bePrometheus() {
		if (verbose) {
			log("bePrometheus()");
		}
		int missclicks = 0;
		if (!inventory.containsOneOf(logs)) {
			RSObject tree = objects.getNearest(liveTree);
			RSTile where = null;
			if (verbose) {
				log("I'm a lumberjack and I'm okay...");
			}

			if (tree != null) {
				where = tree.getLocation();
				if (!billPen.contains(where)) {
					if (verbose) {
						log("Tree found...but it's outside the pen, where the Spotted Al's live...");
					}
					tree = null;
				}
			}

			if (tree == null) {
				if (verbose) {
					log("No TREE??");
				}
				walking.walkTileMM(walking.getClosestTileOnMap(billPen.getCentralTile()));
				waitPlayerMoving();
				return;
			}

			if (!tree.isOnScreen()) {
				walking.walkTileMM(walking.getClosestTileOnMap(tree.getLocation()), 3, 3);
			} else {
				walking.walkTileOnScreen(tree.getLocation());
			}
			waitPlayerMoving();

			if (getMyPlayer().getAnimation() == -1) {
				final int logcount = inventory.getCount(logs);

				chopTree(tree);
				waitPlayerMoving();
				waitPlayerActing();
				if (inventory.getCount(logs) == logcount) {
					missclicks++;
					if (verbose) {
						log("Missed: " + missclicks);
					}
					if (missclicks / 2.0 == missclicks / 2) {
						camera.setAngle(random(1, 360));
						camera.setPitch(random(1, 100));
						if (missclicks == 4) {
							missclicks = 0;
							randomWalk();
						}
					}

				}
			}
			return;
		} else {
			// check the tile we're on for stuff:
			if (objects.getTopAt(me) != null) {
				randomBump(me);
				waitPlayerMoving();
				return;
			}
			final int fc = skills.getCurrentExp(Skills.FIREMAKING);
			inventory.getItem(tinderbox).doClick(true);
			sleep(1000, 2000);
			if (inventory.containsOneOf(logs)) {
				inventory.getItem(logs).doClick(true);
			}
			int timer = 0;
			while (fc == skills.getCurrentExp(Skills.FIREMAKING) && timer < 20) {
				sleep(500);
			}
			timer++;
			if (verbose) {
				log("We have created....FIRE!");
			}
			return;
		}
	}

	public int buryBones(final int oldstatus) {
		status = 8;
		if (verbose) {
			log("Burying bones...");
		}
		while (inventory.contains(bones)) {
			inventory.getItem(bones).doClick(true);
			sleep(1000, 1500);
		}
		return oldstatus;
	}

	private void checkDest() {
		if (verbose) {
			log("Destination is: " + walking.getDestination());
		}
		if (walking.getDestination() != null
				&& !billPen.contains(walking.getDestination())) {
			walking.walkTileOnScreen(me);
		}
	}

	private boolean chopTree(final RSObject obj) {
		if (getMyPlayer().isMoving()) {
			for (int i = 0, len = random(2, 5); i < len; ++i) {
				mouse.move(obj.getModel().getPoint());
				sleep(20, 100);
			}
			return menu.doAction("Chop");
		} else {
			return obj.doAction("Chop");
		}
	}

	private boolean clickMenu(final String name, final String action) {
		final String[] menuItems = menu.getItems();
		if (menuItems.length == 0) {
			return false;
		}
		for (final String menuItem : menuItems) {
			if (menuItem.toLowerCase().contains(name.toLowerCase())) {
				if (menuItems[0].toLowerCase().contains(action.toLowerCase())) {
					mouse.click(true);
					sleep(1000);
					return true;
				} else {
					mouse.click(false);
					menu.doAction(action);
					sleep(1000);
					return true;
				}
			}
		}
		return false;
	}

	private boolean clickNPC(final RSNPC npc, final String action) {
		if (npc == null) {
			return false;
		}
		final RSTile tile = npc.getLocation();
		if (tile.getX() < 0 || tile.getY() < 0) {
			return false;
		}

		try {
			Point screenLoc = npc.getScreenLocation();
			if (calc.distanceTo(tile) > 6 || !calc.pointOnScreen(screenLoc)) {
				camera.turnTo(tile);
			}
			if (!calc.pointOnScreen(screenLoc)) {
				walking.walkTileMM(tile);
				return false;
			}
			for (int i = 0; i < 20; i++) {
				screenLoc = npc.getScreenLocation();
				if (!npc.isValid() || !calc.pointOnScreen(screenLoc)) {
					return false;
				}
				mouse.move(randomPoint(screenLoc));
				if (menu.getItems()[0].toLowerCase().contains(npc.getName().toLowerCase())) {
					break;
				}
			}
			return clickMenu(npc.getName(), action);
		} catch (final Exception e) {
			log.log(Level.SEVERE, "clickNPC(RSNPC, String) model error: ", e);
			return false;
		}
	}

	private void climbStairs(final String climb) {
		int z = 0;
		if (climb.contains("up")) {
			z = 2;
		}
		if (game.getPlane() != z && objects.getNearest(lumbyStairs) != null) {
			if (!objects.getNearest(lumbyStairs).doAction(climb)) {
				climbStairs(climb);
			}
			sleep(750, 1500);
		}
		waitPlayerMoving();
		return;
	}

	private int countCheezBurgers() {
		final RSItem[] is = inventory.getItems();
		int foodcount = 0;
		for (final RSItem i : is) {
			if (i.getComponent().getActions() == null
					|| i.getComponent().getActions()[0] == null) {
				continue;
			}
			if (i.getComponent().getActions()[0].contains("Eat")) {
				foodcount++;
			}
		}
		return foodcount;
	}

	private void doCook() {
		if (verbose) {
			log("doCook()");
		}
		cookedMeatHeld = inventory.getCount(cookedMeat);
		rawMeatHeld = inventory.getCount(rawMeat);
		meatHeld = cookedMeatHeld + rawMeatHeld;
		final RSObject fire = objects.getNearest(litFire);
		if (fire == null) {
			return;
		}
		final RSTile fireTile = walking.getClosestTileOnMap(fire.getLocation());
		cookingIface = interfaces.getComponent(905, 14);
		final int toCook = meatSpace - cookedMeatHeld;

		if (!fire.isOnScreen()) {
			camera.turnTo(fire);
			if (!fire.isOnScreen()) {
				walking.walkTileMM(fireTile);
				return;
			}
		}
		if (inventory.getSelectedItem() == null) {
			inventory.getItem(rawMeat).doAction("Use Raw beef");
		}
		sleep(1500, 1750);
		if (inventory.getSelectedItem() == null) {
			doCook();
		}
		if (tiles.doAction(fire.getLocation(), "Raw")) {
			sleep(2500);
		}
		if (cookingIface.isValid()) {
			if (toCook < rawMeatHeld) {
				if (!adjustCookLevel(toCook)) {
					return;
				}
			}
			cookingIface.doClick();
			sleep(1500, 2000);
			waitPlayerActing();
		}
	}

	private void download(final String address, final String localFileName) {
		OutputStream out = null;
		URLConnection conn;
		InputStream in = null;
		try {
			final URL url = new URL(address);

			out = new BufferedOutputStream(new FileOutputStream(localFileName));
			conn = url.openConnection();
			in = conn.getInputStream();

			final byte[] buffer = new byte[1024];
			int numRead;
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
			}
		} catch (final Exception exception) {
			log("Downloading failed!");
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (final IOException ioe) {
				log("Downloading failed!");
			}
		}
	}

	public int dropJunk(final int oldstatus, final int... items) {
		status = 9;
		while (inventory.containsOneOf(items)) {
			inventory.getItem(items).doAction("Drop");
			sleep(1000);
		}
		return oldstatus;
	}

	private int eatFood(final int oldstatus) {
		if (!iCanHazCheezBurger) {
			return oldstatus; // We're not eating...
		}
		status = 2;
		final RSItem food = iHazCheezburgers();
		if (food != null) {
			food.doAction("Eat ");
			return oldstatus;
		} else {
			status = 12;
			log("No More Food.");
			return status;

		}
	}

	private RSGroundItem findLoot() {
		try {
			RSGroundItem loot = groundItems.getNearest(loots);
			if (needMeat) {
				loot = groundItems.getNearest(rawMeat);
			}
			if (loot == null || needMeat) {
				return loot;
			}

			if (gettingBones && bankingMeats && bankingHides
					&& billPen.contains(loot.getLocation())
					&& loot.isOnScreen()) {
				return loot;
			}

			if (!billPen.contains(loot.getLocation())) {
				randomWalk();
				loot = null;
			}

			if (loot != null) {
				if (!loot.isOnScreen()
						&& calc.distanceTo(loot.getLocation()) > 7
						|| !billPen.contains(loot.getLocation())) {
					loot = null;
				}
			}
			return loot;
		} catch (final Exception e) {
			log.log(Level.SEVERE, "pickup() model error: ", e);
			return null;
		}
	}

	private String format(final long time, final boolean seconds) {
		if (time <= 0) {
			return "--:--:--";
		}
		final StringBuilder t = new StringBuilder();
		final long TotalSec = time / 1000;
		final long TotalMin = TotalSec / 60;
		final long TotalHour = TotalMin / 60;
		final int second = (int) TotalSec % 60;
		final int minute = (int) TotalMin % 60;
		final int hour = (int) TotalHour;
		if (hour < 10) {
			t.append("0");
		}
		t.append(hour);
		t.append(":");
		if (minute < 10) {
			t.append("0");
		}
		t.append(minute);
		if (seconds) {
			t.append(":");
			if (second < 10) {
				t.append("0");
			}
			t.append(second);
		} else {
			t.append(":00");
		}
		return t.toString();
	}

	private String getDate() {
		final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		final Date date = new Date();
		return dateFormat.format(date);
	}

	private RSItem iHazCheezburgers() {
		final RSItem[] is = inventory.getItems();
		for (final RSItem i : is) {
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

	@Override
	public int loop() {
		mouse.setSpeed(random(4, 8));
		if (!game.isLoggedIn() || game.isWelcomeScreen()) {
			log("NOT logged in...waiting 15 seconds.");
			return 15000;
		}

		if (interfaces.canContinue()) {
			interfaces.clickContinue();
		}

		while (store.isOpen()) {
			store.close(); /* In case BBill's store is opened by accident... */
		}

		if (inventory.contains(arrowID)) {
			inventory.getItem(arrowID).doClick(true);
		}

		final boolean havebones = inventory.contains(bones);
		cookedMeatHeld = inventory.getCount(cookedMeat);
		rawMeatHeld = inventory.getCount(rawMeat);
		hidesHeld = inventory.getCount(cowhideID);
		meatHeld = cookedMeatHeld + rawMeatHeld;
		needMeat = meatSpace > meatHeld;
		me = players.getMyPlayer().getLocation();
		bill = npcs.getNearest(beefyBillID);

		if (!walking.isRunEnabled() && walking.getEnergy() >= rndEnergy
				&& !bank.isOpen()) {
			walking.setRun(true);
			sleep(500, 750);
		}
		if (walking.getEnergy() < rndEnergy) {
			status = 7;
			rndEnergy = random(22, 35);
			walking.rest(random(90, 100));
		}

		if (teleLanding.contains(me) && inventory.contains(airStaff)) {
			outOfPen = true;
			path = walking.newTilePath(landingToStairs);
		}

		if (billPen.contains(me)) {
			outOfPen = false;
		}

		if (inventory.getCount(airStaff) > 2
				&& System.currentTimeMillis() - lastTele > 1800000) {
			magic.castSpell(magic.SPELL_HOME_TELEPORT);
			sleep(15000, 16500);
			if (teleLanding.contains(players.getMyPlayer().getLocation())) {
				log("Teleport Cast");
				lastTele = System.currentTimeMillis();
				return 500;
			}
		}

		if (!outOfPen && !billPen.contains(me)) {
			returnToPen();
		}

		if (outOfPen && gateArea.contains(me)) {
			openGate();
		}

		switch (action()) {
		case COOKING:
			final RSObject fire = objects.getNearest(litFire);
			if (fire == null) {
				bePrometheus();
			} else {
				final RSTile where = fire.getLocation();
				if (!billPen.contains(where)) {
					if (verbose) {
						log("Fire found outside Bill's Pen...Do NOT want...");
					}
					return 50;
				}
				doCook();
			}
			return random(2000, 3000);

		case EATING:
			eatFood(status);
			if (status == 12) {
				return -1;
			}
			return 50;

		case BANKING:
			if (verbose) {
				log("Hide/Meat limit reached. Going to Bill.");
			}
			bill = npcs.getNearest(beefyBillID);
			if (bill == null) {
				if (verbose) {
					log("Bill not found?");
				}
				walking.walkTileMM(walking.getClosestTileOnMap(billPen.getCentralTile()));
				return random(200, 250);
			}
			camera.setAngle(0);
			sleep(500, 750);
			camera.setPitch(true);
			final RSTile newTile = bill.getLocation();
			if (!bill.isOnScreen()) {
				if (verbose) {
					log("Walking to Bill");
				}
				walking.walkTileMM(walking.getClosestTileOnMap(newTile), 3, 3);
				return 1000;
			} else {
				waitPlayerMoving();
				if (hidesHeld >= hidesToBank) {
					bankAtBill(cowhideID, "cowhide");
					hidesHeld = inventory.getCount(cowhideID);

					if (hidesHeld != hidesToBank) {
						bankCycles++;
					}
					hidesBanked = bankCycles * (hidesToBank - hideCost);
					if (verbose) {
						log("Hides Banked!");
					}
					if (interfaces.canContinue()) {
						interfaces.clickContinue();
					}
				}

				if (rawMeatHeld >= 10) {
					bankAtBill(rawMeat, "raw");
					rawMeatHeld = inventory.getCount(rawMeat);

					if (rawMeatHeld != 10) {
						meatCycles++;
						meatsBanked = meatCycles * 9;
						if (verbose) {
							log("Meats Banked!");
						}
						if (interfaces.canContinue()) {
							interfaces.clickContinue();
						}
					}
				}
			}
			if (interfaces.canContinue()) {
				interfaces.clickContinue();
			}
			return random(50, 80);

		case BANKSTAFFS:
			if (!bank.isOpen() && inventory.contains(airStaff)) {
				bank.open();
				return random(500, 1000);
			} else if (bank.isOpen()) {
				if (inventory.contains(airStaff)) {
					if (!bank.deposit(airStaff, 0)) {
						bank.deposit(airStaff, 0);
					}
					return random(2000, 2200);
				}

				bank.close();
				return random(2000, 2500);
			}
			walking.walkTileMM(objects.getNearest(lumbyStairs).getLocation());
			waitPlayerMoving();
			return random(50, 80);

		case LOOTING:
			final RSGroundItem loot = findLoot();
			if (loot == null) {
				return random(20, 25);
			}
			if (inventory.containsOneOf(junk) || random(1, 100) < 10) {
				if (inventory.isFull()) {
					status = dropJunk(status, junk);
				}
			}
			if (havebones && gettingBones
					&& (inventory.isFull() || random(1, 100) < 30)) {
				status = buryBones(status);
			}
			try {
				final RSTile lootLocation = loot.getLocation();
				final RSGroundItem lootpile[] = groundItems.getAllAt(lootLocation);

				if (needMeat && !bankingMeats) {
					loots[0] = rawMeat;
				} else if (!bankingMeats && !needMeat) {
					loots[0] = 0;
				}

				if (!loot.isOnScreen()) {
					walking.walkTileMM(walking.getClosestTileOnMap(lootLocation));
				}

				waitPlayerMoving();

				for (final RSGroundItem element : lootpile) {
					final int item = element.getItem().getID();
					for (final int loot2 : loots) {
						if (item == loot2 && !inventory.isFull()) {
							takeItem(element);
							waitPlayerMoving();
							sleep(650, 800);
						}
					}
				}

			} catch (final Exception e) {
				if (verbose) {
					log.log(Level.SEVERE, "Looting: ", e);
				}
				return 5;
			}

			return random(300, 400);

		case ATTACKING:
			cow = newNPC();
			if (cow == null) {
				status = 11;
				searchTime++;
				if (searchTime >= 2) {
					randomWalk();
					waitPlayerMoving();
					searchTime = 0;
				}
				return random(50, 100);
			}
			searchTime = 0;

			if (toNPC(cow)) {
				waitPlayerMoving();
				clickNPC(cow, "Attack");
			}
			return 500;

		case FIGHTING:
			try {
				if (players.getMyPlayer().getInteracting() != null
						|| players.getMyPlayer().isInCombat()) {
					while (cow.getHPPercent() > 0
							&& players.getMyPlayer().getInteracting() != null) {
						if (combat.getLifePoints() < skills.getRealLevel(3)
								* 10 * whenToEat) {
							eatFood(status);
						}
						if (status == 12 || quitNow) {
							return -1;
						}
					}
					sleep(3000, 4000);
				}
			} catch (final Exception ignored) {
				return random(10, 15);
			}
			return random(600, 550);

		case CLIMBING:

			final int plane = game.getPlane();

			if (plane == 2 && inventory.contains(airStaff)) {
				walking.walkTileMM(bankArea.getNearestTile(bankArea.getCentralTile()));
				return 6000;
			}

			if (plane == 0 && !inventory.contains(airStaff)) {
				path = walking.newTilePath(lumBank2BillPen);
				path.traverse();
				waitPlayerMoving();
				return 500;
			}

			if (inventory.contains(airStaff) && (plane == 0 || plane == 1)) {
				climbStairs("Climb-up");
				return 500;
			}

			if (!inventory.contains(airStaff) && (plane == 2 || plane == 1)) {
				climbStairs("Climb-down");
				return 500;
			}

		case WALKING:

			path.traverse();
			while (calc.distanceTo(walking.getDestination()) > 5) {
				sleep(25, 26);
			}
			if (calc.distanceTo(lumStairs.getCentralTile()) < 3
					&& inventory.contains(airStaff)) {
				walking.walkTileOnScreen(objects.getNearest(lumbyStairs).getLocation());
			}

			return 50;

		} // switch ends here...`
		return random(100, 200);
	}

	private boolean lootCheck() {
		final RSGroundItem[] items = groundItems.getAll(new Filter<RSGroundItem>() {
			public boolean accept(final RSGroundItem item) {

				for (final int i : loots) {
					if (i == item.getItem().getID() && item.isOnScreen()) {
						return true;
					}
				}
				return false;
			}
		});
		if (items == null) {
			return false;
		}
		int cowcount = 0;
		final int count = items.length;
		int piles = 0, y = 0;
		for (int i = 0; i < count; i++) {

			if (!items[y].equals(items[i])) {
				piles++;
				y = i;
			}
		}
		if (piles == 0 && count > 0) {
			piles = 1;
		}

		final RSNPC[] monsters = npcs.getAll(new Filter<RSNPC>() {
			public boolean accept(final RSNPC monster) {
				return monster.getName().toLowerCase().contains("cow")
						&& !monster.getName().toLowerCase().contains("dairy")
						&& billPen.contains(monster.getLocation())
						&& !monster.isInCombat() && monster.isOnScreen();
			}
		});
		if (monsters == null) {
			return true;
		}

		cowcount = monsters.length;
		if (verbose) {
			log("Piles = " + piles);
			log("Cows = " + cowcount);
			String z$ = "Looting...";
			if (piles < cowcount) {
				z$ = "Attacking...";
			}
			log(z$);
		}
		return piles >= cowcount;
	}

	@Override
	public void messageReceived(final MessageEvent e) {
		final String message = e.getMessage().toLowerCase();
		String[] parse;

		if (e.getID() == MessageEvent.MESSAGE_SERVER) {

			if (verbose) {
				log(message + " " + e.getID());
			}

			if (message.contains("minutes to cast")) {
				parse = message.split(" ");
				if (parse[5] != null) {
					try {
						int x = Integer.parseInt(parse[5]);
						if (x > 0) {
							x = x * 60000;
						}
						lastTele = System.currentTimeMillis() + x - 1800000;
					} catch (final Exception ignored) {
						log("Oops");
					}
				}
			}

			if (message.contains("can't light a fire")) {
				if (verbose) {
					log("Silly, tried to light a fire on a bad spot...");
				}
			}
			if (ranging) {
				if (message.contains("bow isn't powerful enough")) {
					log("Bow too weak for arrows. Quitting.");
					quitNow = true;
				}
				if (message.contains("no ammo")) {
					if (equipment.getItem(10).getID() == -1) {
						log("Out of arrows. Quitting.");
						quitNow = true;
					}
				}
			}
			if (message.contains("runes to cast")) {
				log("Out of runes for chosen spell. Quitting.");
				quitNow = true;
			}
		}
	}

	public void mouseClicked(final MouseEvent e) {
		final Point q = e.getPoint();
		if (paintToggle.contains(q)) {
			showPaint = !showPaint;
			if (showPaint) {
				buttonText = "Hide Paint";
			} else {
				buttonText = "Show Paint";
			}
		}
	}

	public void mouseEntered(final MouseEvent e) {
	}

	public void mouseExited(final MouseEvent e) {
	}

	public void mousePressed(final MouseEvent e) {
	}

	public void mouseReleased(final MouseEvent e) {
	}

	public RSNPC newNPC() {

		final RSNPC[] monsters = npcs.getAll(new Filter<RSNPC>() {
			public boolean accept(final RSNPC monster) {
				return monster.getName().toLowerCase().contains("cow")
						&& !monster.getName().toLowerCase().contains("dairy")
						&& billPen.contains(monster.getLocation())
						&& !monster.isInCombat();
			}
		});

		if (monsters == null) {
			return null;
		}
		RSNPC closest = null;
		int Dist = 20;
		for (final RSNPC monster : monsters) {
			final int distance = calc.distanceTo(monster.getLocation());
			if (distance < Dist) {
				Dist = distance;
				closest = monster;
			}
		}
		return closest;
	}

	public void onRepaint(final Graphics render) {
		final int x = 7;
		int y = 390, z = 343;
		final int len = 520, fntSize = 11, spcng = 12;
		if (!game.isFixed()) {
			y = game.getHeight() - 113;
			z = y - 47;
			paintToggle = new Rectangle(390, z, 120, 15);
		}
		final Graphics2D g = (Graphics2D) render;
		final long runTime = System.currentTimeMillis() - startTime;
		double eph, gph, hph, mph = 0;
		int exp;
		long ttl, etl;

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// render "+" over mouse.
		final Point m = mouse.getLocation();
		g.drawLine((int) m.getX() - 3, (int) m.getY() + 3, (int) m.getX() + 3, (int) m.getY() - 3);
		g.drawLine((int) m.getX() - 3, (int) m.getY() - 3, (int) m.getX() + 3, (int) m.getY() + 3);

		g.setFont(new Font("Arial", Font.PLAIN, fntSize));
		g.setColor(new Color(255, 0, 0, 220));
		if (showPaint) {

			hidesLooted = hidesHeld + hidesBanked;
			meatsLooted = rawMeatHeld + meatsBanked;
			hidesValue = hidesLooted * hidePrice;
			meatsValue = meatsLooted * meatPrice;
			hph = hidesLooted * 3600000D
					/ (System.currentTimeMillis() - startTime);
			mph = meatsLooted * 3600000D
					/ (System.currentTimeMillis() - startTime);
			gph = hidePrice * hph + meatPrice * mph;

			g.setColor(new Color(0, 0, 0, 220));
			g.fillRoundRect(x - 6, y - 50, len, spcng * 11 + 8, 5, 5);
			g.setColor(Color.WHITE);
			g.drawRoundRect(x - 6, y - 50, len, spcng * 11 + 8, 5, 5);
			g.drawString("Beefy Bill Cow Killer Run Time: "
					+ format(runTime, true) + " v" + version + " Mr. Byte", x, (y - spcng * 3));
			if (bankingHides || bankingMeats) {
				if (bankingHides && !bankingMeats) {
					g.drawString("Hides Banked: " + hidesBanked
							+ "     Hides Looted: " + hidesLooted
							+ "     Market Value: " + hidesValue, x, y - spcng);
					g.drawString("Hides Looted/hr: " + whole.format(hph)
							+ "  Gold Per Hour: " + whole.format(gph), x, y
							+ spcng * 7);
				} else if (bankingMeats && bankingHides) {
					g.drawString("Hides Banked: " + hidesBanked
							+ "   Hides Looted: " + hidesLooted
							+ "   Meats Banked: " + meatsBanked
							+ "   Meats Looted: " + meatsLooted
							+ "   Market Value: " + (hidesValue + meatsValue), x, y
							- spcng);
					g.drawString("Hides Looted/hr: " + whole.format(hph)
							+ " Meats Looted/hr:" + whole.format(mph)
							+ "  Gold Per Hour: " + whole.format(gph), x, y
							+ spcng * 7);

				} else if (bankingMeats && !bankingHides) {
					g.drawString("Meats Banked: " + meatsBanked
							+ "   Meats Looted: " + meatsLooted
							+ "   Market Value: " + meatsValue, x, y - spcng);
					g.drawString("Hides Looted/hr: " + whole.format(hph)
							+ " Meats Looted/hr:" + whole.format(mph)
							+ "  Gold Per Hour: " + whole.format(gph), x, y
							+ spcng * 7);
				}
			}
			g.drawString(bone$ + "   " + meat$ + "   " + hide$, x, y - spcng
					* 2);
			g.setFont(new Font("Arial", Font.PLAIN, fntSize + 2));
			g.setColor(statusColors[status / 5]);
			g.drawString("Status: " + statusNames$[status], x + len
					- (statusNames$[status].length() + 8) * 7, y + spcng * 7);
			g.setFont(new Font("Arial", Font.PLAIN, fntSize));
			int colour = 0;
			for (int i = 0; i < skillNames$.length; i++) {
				exp = skills.getCurrentExp(i) - startXP[i];
				if (exp > 0) {
					colour++;
					eph = exp * 3600000D
							/ (System.currentTimeMillis() - startTime);
					etl = skills.getExpToNextLevel(i);
					ttl = (long) (skills.getExpToNextLevel(i) * 3600000D / eph);
					g.setFont(new Font("Arial", Font.PLAIN, fntSize));
					g.setColor(new Color(250, 32 + colour * 32, 32 + colour * 32));
					String exp$ = (int) exp + " ";
					String eph$ = (int) eph + " ";
					if (exp > 999) {
						exp$ = k.format(exp / 1000D) + "K ";
					}
					if (eph > 999) {
						eph$ = k.format(eph / 1000D) + "K ";
					}
					g.drawString(skillNames$[i] + ": " + exp$
							+ "Pts. Earned - " + eph$ + "Pts/H - " + etl
							+ " Exp to Level - Time to Level: "
							+ format(ttl, false), x, y);

					y += spcng;
				}
			}
		}
		g.setColor(new Color(255, 0, 0, 220));
		g.fillRoundRect(395, z, 120, 15, 7, 7);
		g.setColor(Color.WHITE);
		g.drawRoundRect(395, z, 120, 15, 7, 7);
		g.drawString(buttonText, 430, z + 12);
	}

	@Override
	public boolean onStart() {

		final String className = this.getClass().getName().replace('.', '/');
		final String classJar = this.getClass().getResource("/" + className
				+ ".class").toString();
		if (classJar.startsWith("jar:")) {
			log("*** running from jar!");
			isJar = true;
		}

		// Are we At Bill's?
		me = players.getMyPlayer().getLocation();
		if (billPen.contains(me)) {
			log("At Beefy Bills...Good!");
		} else {
			log("Go to Bill's Pen, then restart.");
			return false;
		}
		gui = new BeefyGUI();
		gui.setVisible(true);
		while (gui.isVisible()) {
			sleep(50);
		}
		if (guiExit) {
			log("Quitting Beefy Bill Cow Killer");
			return false;
		}

		for (final int i : bows) {
			if (i == equipment.getItem(equipment.WEAPON).getID()) {
				ranging = true;
				for (final int y : arrows) {
					if (y == equipment.getItem(equipment.AMMO).getID()) {
						arrowID = y;
						loots[3] = y;
						if (verbose) {
							log("BowID: " + i + " ArrowID: " + arrowID);
						}
						break;
					}
				}
				break;
			}
		}

		if (verbose) {
			log("Verbose logging. Look for Updates: " + update
					+ "Getting Bones: " + gettingBones);
			log(" Getting Hides: " + bankingHides + " iCanHazCheezBurger: "
					+ iCanHazCheezBurger + " whenToEat: " + whenToEat);
		}

		if (whenToEat == 1) {
			log("Eat if under 100% HP??!???");
		}
		// Do we have the room for HidesToBank after discounting Bones and
		// Hides??
		log("You have "
				+ (inventory.getCount() - inventory.getCount(cowhideID) - inventory.getCount(bones))
				+ " misc items, " + countCheezBurgers() + " of that is food, "
				+ +inventory.getCount(cowhideID) + " cowhides and "
				+ inventory.getCount(bones) + " bones in your inventory.");

		if (bankingHides) {
			if (bankingMeats) {
				if (28 - inventory.getCount() + inventory.getCount(cowhideID)
						+ inventory.getCount(bones)
						+ inventory.getCount(rawMeat) < 20) {
					log("Not enough inventory space for banking both meats and hides. Not banking meats.");
					bankingMeats = false;
					loots[0] = 0; // don't get Meats.
				}
			} else {
				if (28 - inventory.getCount() + inventory.getCount(cowhideID)
						+ inventory.getCount(bones) < hidesToBank) {
					if (28 - inventory.getCount()
							+ inventory.getCount(cowhideID)
							+ inventory.getCount(bones) < 10) {
						log("Not enough inventory space...");
						log("Please restart after having 10 or 20 slots available, not including bones or hides.");
						return false;
					} else {
						hidesToBank = 10;
						log("You don't have room for 20 hides! Banking 10 hides a shot...");
					}
				}
			}
		}

		if (iCanCookCheezBurger) {
			final int foodcount = countCheezBurgers();
			meatSpace = 28
					- (inventory.getCount() - inventory.getCount(cowhideID)
							- inventory.getCount(bones)
							- inventory.getCount(rawMeat) - foodcount)
					- hidesToBank;
			if (meatSpace > 4) {
				meatSpace = 4;
			}
		} // limit it to 4 meats.

		if (!iCanHazCheezBurger) {
			whenToEat = 0;
		}

		if (!gettingBones) {
			bone$ = "NOT getting bones.";
			log("NOT picking up bones.");
		} else {
			log("Bones will be gathered and buried.");
		}
		if (meatSpace == 0) {
			if (!bankingMeats) {
				log("NOT banking or cooking meats...");
				meat$ = "NOT looting meats.";
			}
		} else if (bankingMeats) {
			log("Up to "
					+ meatSpace
					+ " meats will be gathered and cooked for your gastromomic pleasure.");
			log("Also, we are banking meats.");
			meat$ = "Banking/Cooking meats";
		} else {
			meat$ = "Cooking meats";
		}
		log("Getting prices, please wait...");

		if (!readPrices()) {
			hidePrice = grandExchange.lookup(cowhideID).getGuidePrice();
			meatPrice = grandExchange.lookup(rawMeat).getGuidePrice();
			writePrices();
		}

		if (bankingHides) {
			hide$ = hidesToBank + " Hides per bank run";
			hideCost = hidesToBank / 10;
		}

		if (rndEnergy == 0) {
			log("No rest for the Wicked.");
		} else {
			log("Resting when tired.");
		}

		// Load array with starting values for XP
		startXP = new int[7];
		for (int i = 0; i < 7; i++) {
			startXP[i] = skills.getCurrentExp(i);
		}
		startTime = System.currentTimeMillis();
		log("XP array loaded, Start Time set.");

		log.severe("Note: in the event of PowerBot being down, you can find info about");
		log.severe("this script at http://www.LetTheSmokeOut.com. In addition, all updates");
		log.severe("are hosted there as well.");

		if (update) {
			return updater(); // check for new version, based on GUI pref.
		}

		return true;
	}

	private void openGate() {
		if (verbose) {
			log("openGate()");
		}
		try {
			while (objects.getNearest(gate) != null
					&& objects.getNearest(gate).isOnScreen()) {
				objects.getNearest(gate).doAction("Open");
				waitPlayerMoving();
				waitPlayerActing();
				sleep(500, 750);
			}
		} catch (final Exception e) {
			log.log(Level.SEVERE, "Gate opened by other!");
			return;
		}
	}

	/*
	 * Download method from RSBot UpdateUtil by TheShadow Copied to here because
	 * it wouldn't let me call it from here...some static thing (shrug)
	 */

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
			final int k = str.startsWith("+") ? 1 : -1;
			str = str.substring(1);
			return Double.parseDouble(str.substring(0, str.length() - 1)) * k;
		}
		return -1D;
	}

	private void randomBump(final RSTile loc) {
		int xpos = loc.getX(), ypos = loc.getY();
		final RSArea bumpZone = new RSArea(xpos - 1, ypos + 1, xpos + 1, ypos - 1);
		final RSTile[] tilesArray = bumpZone.getTileArray();
		final RSTile newTile = tilesArray[random(0, tilesArray.length)];
		if (newTile.equals(loc) || objects.getTopAt(newTile) != null) {
			randomBump(me);
		}
		xpos = newTile.getX();
		ypos = newTile.getY();
		if (verbose) {
			log("Randomly bumped to: " + xpos + ", " + ypos + ".");
		}
		walking.walkTileOnScreen(newTile);
		waitPlayerMoving();
		sleep(1500, 2000);
		return;
	}

	private Point randomPoint(final Point click) {
		final int dif = 2;
		return new Point(click.x + random(-dif, dif), click.y
				+ random(-dif, dif));
	}

	private void randomWalk() {
		randomWalk(0);
		return;
	}

	private void randomWalk(int badCount) {
		final RSTile[] tilesArray = billPen.getTileArray();
		me = players.getMyPlayer().getLocation();
		final RSTile newTile = tilesArray[random(0, tilesArray.length - 1)];
		if (keepOut.contains(newTile) && !keepOut.contains(me)
				|| newTile.equals(me)) {
			log("Tile in 'keepOut' or equal to current location.");
			if (badCount < 10) {
				randomWalk(badCount + 1);
			} else {
				badCount = 0;
				returnToPen();
				return;
			}
		}

		final int xpos = newTile.getX(), ypos = newTile.getY();

		if (verbose) {
			log("Randomly walking to: " + xpos + ", " + ypos + ".");
		}
		walking.walkTo(newTile);
		waitPlayerMoving();
		return;
	}

	private boolean readPrices() {
		try {

			final BufferedReader in = new BufferedReader(new FileReader(PRICE_FILE));
			String line;
			String[] opts = {};

			while ((line = in.readLine()) != null) {
				if (line.contains(":")) {
					opts = line.split(":");
					if (!opts[0].equals(getDate()) || opts[2] == "Cowhide"
							|| opts[2] == "Raw Meat") {
						log("Old data in file");
						return false; // need new prices, different date.
					}
					if (Integer.parseInt(opts[1]) == cowhideID) {
						hidePrice = Integer.parseInt(opts[2]);
					}
					if (Integer.parseInt(opts[1]) == rawMeat) {
						meatPrice = Integer.parseInt(opts[2]);
					}
				}
			}
			in.close();
		} catch (final IOException ignored) {
			return false;
		}

		return true;
	}

	private void returnToPen() {
		status = 10;
		tile = gateArea.getCentralTile();
		path = walking.getPath(tile);
		walkPath(path);
		return;
	}

	private String stripFormatting(final String str) {
		if (str != null && !str.isEmpty()) {
			return str.replaceAll("(^[^<]+>|<[^>]+>|<[^>]+$)", "");
		}
		return "";
	}

	private void takeItem(final RSGroundItem item) {
		final String name = item.getItem().getName();
		if (getMyPlayer().isMoving()) {
			for (int i = 0, len = random(2, 5); i < len; ++i) {
				mouse.move(item.getModel().getPoint());
				sleep(20, 100);
			}
			menu.doAction("Take " + name);
		} else {
			tiles.doAction(item.getLocation(), "Take " + name);
		}
		return;
	}

	// M-i-c, K-e-y, M-o-u-s-e'd extra listener garbahhhge.

	private boolean toNPC(final RSNPC npc) {
		if (!npc.isOnScreen()) {
			camera.turnTo(npc);
		}

		if (!npc.isOnScreen()) {
			walking.walkTileMM(walking.getClosestTileOnMap(npc.getLocation()));
			while (players.getMyPlayer().isMoving()
					|| walking.getDestination() != null) {
				if (npc.isInCombat()) {
					return false;
				}
				sleep(10, 15);
			}
		}
		return true;
	}

	public boolean updater() {
		final Pattern UPDATER_VERSION_PATTERN = Pattern.compile("version\\s*=\\s*([0-9.]+)");
		final String scriptName = BeefyBillCowKiller.class.getAnnotation(ScriptManifest.class).name();

		final String scriptHost = "http://letthesmokeout.com/RSBot/";

		final String className = this.getClass().getName();

		final String javaName = className + ".java";
		final String jarName = className + ".jar";
		final String javaURL = scriptHost + javaName;
		final String jarURL = scriptHost + jarName;
		final String localJavaName = Configuration.Paths.getScriptsSourcesDirectory()
				+ File.separator + javaName;
		final String localJarName = Configuration.Paths.getScriptsPrecompiledDirectory()
				+ File.separator + jarName;
		double revision = -1.0;

		String dialog$ = "An update is available, do you wish to download it now?";
		if (isJar) {
			dialog$ = dialog$
					+ " It will be downloaded into your Precompiled folder.";
		} else {
			dialog$ = dialog$ + "  It will be saved to your scripts folder.";
		}

		try {
			/*
			 * Get the current version from the Script Manifest annotation
			 * defined at the top of script's class
			 */
			final URL url = new URL(javaURL);

			/* Open a stream to the newest script file hosted on server */
			final BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line, lines = "";
			Matcher m;

			/*
			 * Look for the "version = x.x" string in the newer file to figure
			 * out the newest version number
			 */

			while ((line = in.readLine()) != null) {
				lines += line + "\n";
				if ((m = UPDATER_VERSION_PATTERN.matcher(line)).find()) {
					revision = Double.parseDouble(m.group(1));
					break;
				}
			}
			/* Check if the updater was unable to read the newest version number */
			if (revision < 0) {
				in.close();
				log("Unable to find the new version number. Update failed");
				return false;
			}

			if (version < revision) {
				if (JOptionPane.showConfirmDialog(null, dialog$, scriptName
						+ " v" + revision + " is available!", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					if (isJar) {
						download(jarURL, localJarName);
						log.severe("New .jar downloaded. Please restart RSBot, or the new .jar will not appear!");
						return false;
					} else {
						download(javaURL, localJavaName);
						log.severe("Exiting, please review code, recompile scripts and restart.");
						return false;
					}
				} else { // update declined...
					log.severe("Updater: New version available. Download at "
							+ javaURL);
					log.severe("Updater: and save it into " + localJavaName);
				}
			}

			if (version == revision) {
				log("Updater: You have the newest available version!");
			}
			if (version > revision) {
				log.severe("Updater: You have a newer version than available!");
			}
			if (in != null) {
				in.close();
			}
		} catch (final IOException e) {
			log.severe("Updater:  Problem getting version. - go to LetTheSmokeOut.com for details on updating.");
		}
		return true;
	}

	private void waitPlayerActing() {
		while (players.getMyPlayer().getAnimation() != -1) {
			sleep(100, 150);
		}
	}

	private void waitPlayerMoving() {
		if (!outOfPen) {
			checkDest();
		}
		while (players.getMyPlayer().isMoving()
				|| walking.getDestination() != null) {
			sleep(10, 15);
		}
	}

	private boolean walkPath(final RSPath p) {
		walking.setRun(true);
		final int end = calc.distanceTo(p.getEnd());
		while (calc.distanceTo(p.getEnd()) < end + end / 2
				&& calc.distanceTo(p.getEnd()) > 3) {
			p.traverse();
			if (walking.getDestination() != null) {
				while (calc.distanceTo(walking.getDestination()) > random(1, 3)) {
					sleep(10, 20);
				}
			}
		}
		return true;
	}

	private boolean writePrices() {
		try {
			final BufferedWriter out = new BufferedWriter(new FileWriter(PRICE_FILE));
			out.write(getDate() + ":" + cowhideID + ":" + hidePrice + "\n");
			out.write(getDate() + ":" + rawMeat + ":" + meatPrice);
			out.close();
			;
		} catch (final Exception ignored) {
			return false;
		}
		return true;
	}
	
	/*
	 * Changelog:
	 * 
	 * v3.25 Changelog started. Changed GE price storage method to use item ID's
	 * rather than names Eliminated the skills[i] array, as it was not really
	 * needed. Cleaned up sleep(random(x,x)) sleep(x,x) Changed update checker
	 * to now look on server at script file and extract the posted version from
	 * it instead of reading a version file.
	 * 
	 * v3.26 Added safety-checks to the ID field in the price file, due to
	 * errors if reading old price file with latest code.
	 * 
	 * v3.27 Improved some visuals on the updater GUI, changed the restart
	 * message for jar's to reflect the fact that you need to restart the bot
	 * for the new .jar to appear. Removed compile code, as it's not working
	 * locally...
	 * 
	 * v3.28 Fixed issue in randomWalk() causing script to hang and bot to idle
	 * and timeout. Improved portability of update method.
	 * 
	 * v3.29 Added "Get Air Staffs" option.
	 * 
	 * v3.30 Moved price file location to comply with new security stuff.
	 * 
	 * v3.31 Changed "GlobalConfiguration" to "Configuration" to match bot
	 * change. Added jar detection logic.
	 */
	
}
