import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Skills;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;

@ScriptManifest(authors = {"BlackWood"}, name = "BW Herblore", version = 1.8, description = "Herblore Done Right!", website = "http://www.powerbot.org/vb/showthread.php?t=660521")
public class BWHerblore extends Script implements PaintListener,
		MessageListener, MouseListener {

	public class BWHerbloreGUI extends JFrame {

		private static final long serialVersionUID = 1L;

		private JComboBox comboBox1;

		private JComboBox comboBox2;

		private JButton button1;

		public BWHerbloreGUI() {
			initComponents();
		}

		private void initComponents() {
			final String[] MethodList = {"Cleaning", "Unfs", "Completes",
					"Full Completes", "Extremes", "Tars"};
			final String[] HerbList = {"Guam", "Marrentill", "Tarromin",
					"Harralander", "Ranarr", "Toadflax", "Spirit Weed", "Irit",
					"Avantoe", "Kwuarm", "Snapdragon", "Cadantine",
					"Lantadyme", "Dwarf Weed", "Torstol"};
			final String[] UnfList = {"Guam Potion", "Marrentill Potion",
					"Tarromin Potion", "Harralander Potion", "Ranarr Potion",
					"Toadflax Potion", "Spirit Weed Potion", "Irit Potion",
					"Avantoe Potion", "Kwuarm Potion", "Snapdragon Potion",
					"Cadantine Potion", "Lantadyme Potion",
					"Dwarf Weed Potion", "Torstol Potion"};
			final String[] CompleteList = {"Attack", "Anti-Poison",
					"Strength", "Serum 207", "Stat Restore", "Energy",
					"Defence", "Agility", "Combat", "Prayer", "Summoning",
					"Super Attack", "Super Anti-Poison", "Fishing",
					"Super Energy", "Hunter", "Super Strength", "Fletching",
					"Weapon Poison", "Super Restore", "Super Defence",
					"Anti-Fire", "Ranging", "Magic", "Zamorak", "Saradomin"};
			final String[] ExtremeList = {"Attack", "Strength", "Defence",
					"Magic", "Ranging"};
			final String[] TarList = {"Guam", "Marrentill", "Tarromin",
					"Harralander"};
			comboBox1 = new JComboBox(MethodList);
			comboBox2 = new JComboBox(HerbList);
			button1 = new JButton("Start");

			// ======== this ========
			final Container contentPane = getContentPane();
			contentPane.setLayout(null);

			contentPane.add(comboBox1);
			comboBox1.setBounds(25, 5, 115, 30);
			comboBox1.setSelectedItem(MethodList[0]);
			contentPane.add(comboBox2);
			comboBox2.setBounds(25, 45, 115, 30);
			comboBox2.setSelectedItem(UnfList[0]);
			comboBox1.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					if (comboBox1.getSelectedItem() == "Cleaning") {
						comboBox2.removeAllItems();
						for (final String ListPart : HerbList) {
							comboBox2.addItem(ListPart);
						}
					}
					if (comboBox1.getSelectedItem() == "Unfs") {
						comboBox2.removeAllItems();
						for (final String ListPart : UnfList) {
							comboBox2.addItem(ListPart);
						}
					}
					if (comboBox1.getSelectedItem() == "Completes") {
						comboBox2.removeAllItems();
						for (final String ListPart : CompleteList) {
							comboBox2.addItem(ListPart);
						}
					}
					if (comboBox1.getSelectedItem() == "Full Completes") {
						comboBox2.removeAllItems();
						for (final String ListPart : CompleteList) {
							comboBox2.addItem(ListPart);
						}
					}
					if (comboBox1.getSelectedItem() == "Extremes") {
						comboBox2.removeAllItems();
						for (final String ListPart : ExtremeList) {
							comboBox2.addItem(ListPart);
						}
					}
					if (comboBox1.getSelectedItem() == "Tars") {
						comboBox2.removeAllItems();
						for (final String ListPart : TarList) {
							comboBox2.addItem(ListPart);
						}
					}
				}
			});
			contentPane.add(button1);
			button1.setBounds(25, 85, 115, 30);
			button1.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					StartPressed(e);
				}
			});

			{ // compute preferred size
				final Dimension preferredSize = new Dimension();
				for (int i = 0; i < contentPane.getComponentCount(); i++) {
					final Rectangle bounds = contentPane.getComponent(i).getBounds();
					preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
					preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
				}
				final Insets insets = contentPane.getInsets();
				preferredSize.width += insets.right + 25;
				preferredSize.height += insets.bottom + 5;
				contentPane.setMinimumSize(preferredSize);
				contentPane.setPreferredSize(preferredSize);
			}
			pack();
			setLocationRelativeTo(getOwner());
		}

		void StartPressed(final ActionEvent e) {
			StartedScript = true;
			Duration = System.currentTimeMillis();
			if (comboBox1.getSelectedItem().equals("Cleaning")) {
				Cleaning = true;
				Role = "Herb Cleaning";
				if (comboBox2.getSelectedItem().equals("Guam")) {
					Chosen = PotionStats.ATTACK;
				}
				if (comboBox2.getSelectedItem().equals("Marrentill")) {
					Chosen = PotionStats.ANTIPOISON;
				}
				if (comboBox2.getSelectedItem().equals("Tarromin")) {
					Chosen = PotionStats.STRENGTH;
				}
				if (comboBox2.getSelectedItem().equals("Harralander")) {
					Chosen = PotionStats.STATRESTORE;
				}
				if (comboBox2.getSelectedItem().equals("Ranarr")) {
					Chosen = PotionStats.DEFENCE;
				}
				if (comboBox2.getSelectedItem().equals("Toadflax")) {
					Chosen = PotionStats.AGILITY;
				}
				if (comboBox2.getSelectedItem().equals("Spirit Weed")) {
					Chosen = PotionStats.SUMMONING;
				}
				if (comboBox2.getSelectedItem().equals("Irit")) {
					Chosen = PotionStats.SUPERATTACK;
				}
				if (comboBox2.getSelectedItem().equals("Avantoe")) {
					Chosen = PotionStats.FISHING;
				}
				if (comboBox2.getSelectedItem().equals("Kwuarm")) {
					Chosen = PotionStats.SUPERSTRENGTH;
				}
				if (comboBox2.getSelectedItem().equals("Snapdragon")) {
					Chosen = PotionStats.SUPERRESTORE;
				}
				if (comboBox2.getSelectedItem().equals("Cadantine")) {
					Chosen = PotionStats.SUPERDEFENCE;
				}
				if (comboBox2.getSelectedItem().equals("Lantadyme")) {
					Chosen = PotionStats.ANTIFIRE;
				}
				if (comboBox2.getSelectedItem().equals("Dwarf Weed")) {
					Chosen = PotionStats.RANGING;
				}
				if (comboBox2.getSelectedItem().equals("Torstol")) {
					Chosen = PotionStats.ZAMORAK;
				}
			}
			if (comboBox1.getSelectedItem().equals("Unfs")) {
				Unfs = true;
				Role = "Unfs";
				if (comboBox2.getSelectedItem().equals("Guam Potion")) {
					Chosen = PotionStats.ATTACK;
				}
				if (comboBox2.getSelectedItem().equals("Marrentill Potion")) {
					Chosen = PotionStats.ANTIPOISON;
				}
				if (comboBox2.getSelectedItem().equals("Tarromin Potion")) {
					Chosen = PotionStats.STRENGTH;
				}
				if (comboBox2.getSelectedItem().equals("Harralander Potion")) {
					Chosen = PotionStats.STATRESTORE;
				}
				if (comboBox2.getSelectedItem().equals("Ranarr Potion")) {
					Chosen = PotionStats.DEFENCE;
				}
				if (comboBox2.getSelectedItem().equals("Toadflax Potion")) {
					Chosen = PotionStats.AGILITY;
				}
				if (comboBox2.getSelectedItem().equals("Spirit Weed Potion")) {
					Chosen = PotionStats.SUMMONING;
				}
				if (comboBox2.getSelectedItem().equals("Irit Potion")) {
					Chosen = PotionStats.SUPERATTACK;
				}
				if (comboBox2.getSelectedItem().equals("Avantoe Potion")) {
					Chosen = PotionStats.FISHING;
				}
				if (comboBox2.getSelectedItem().equals("Kwuarm Potion")) {
					Chosen = PotionStats.SUPERSTRENGTH;
				}
				if (comboBox2.getSelectedItem().equals("Snapdragon Potion")) {
					Chosen = PotionStats.SUPERRESTORE;
				}
				if (comboBox2.getSelectedItem().equals("Cadantine Potion")) {
					Chosen = PotionStats.SUPERDEFENCE;
				}
				if (comboBox2.getSelectedItem().equals("Lantadyme Potion")) {
					Chosen = PotionStats.ANTIFIRE;
				}
				if (comboBox2.getSelectedItem().equals("Dwarf Weed Potion")) {
					Chosen = PotionStats.RANGING;
				}
				if (comboBox2.getSelectedItem().equals("Torstol Potion")) {
					Chosen = PotionStats.ZAMORAK;
				}
			}
			if (comboBox1.getSelectedItem().equals("Completes")) {
				Completes = true;
				Role = "Completes";
				if (comboBox2.getSelectedItem().equals("Attack")) {
					Chosen = PotionStats.ATTACK;
				}
				if (comboBox2.getSelectedItem().equals("Anti-Poison")) {
					Chosen = PotionStats.ANTIPOISON;
				}
				if (comboBox2.getSelectedItem().equals("Strength")) {
					Chosen = PotionStats.STRENGTH;
				}
				if (comboBox2.getSelectedItem().equals("Serum 207")) {
					Chosen = PotionStats.SERUM207;
				}
				if (comboBox2.getSelectedItem().equals("Stat Restore")) {
					Chosen = PotionStats.STATRESTORE;
				}
				if (comboBox2.getSelectedItem().equals("Energy")) {
					Chosen = PotionStats.ENERGY;
				}
				if (comboBox2.getSelectedItem().equals("Defence")) {
					Chosen = PotionStats.DEFENCE;
				}
				if (comboBox2.getSelectedItem().equals("Agility")) {
					Chosen = PotionStats.AGILITY;
				}
				if (comboBox2.getSelectedItem().equals("Combat")) {
					Chosen = PotionStats.COMBAT;
				}
				if (comboBox2.getSelectedItem().equals("Prayer")) {
					Chosen = PotionStats.PRAYER;
				}
				if (comboBox2.getSelectedItem().equals("Summoning")) {
					Chosen = PotionStats.SUMMONING;
				}
				if (comboBox2.getSelectedItem().equals("Super Attack")) {
					Chosen = PotionStats.SUPERATTACK;
				}
				if (comboBox2.getSelectedItem().equals("Super Anti-Poison")) {
					Chosen = PotionStats.SUPERANTIPOISON;
				}
				if (comboBox2.getSelectedItem().equals("Fishing")) {
					Chosen = PotionStats.FISHING;
				}
				if (comboBox2.getSelectedItem().equals("Super Energy")) {
					Chosen = PotionStats.SUPERENERGY;
				}
				if (comboBox2.getSelectedItem().equals("Hunter")) {
					Chosen = PotionStats.HUNTER;
				}
				if (comboBox2.getSelectedItem().equals("Super Strength")) {
					Chosen = PotionStats.SUPERSTRENGTH;
				}
				if (comboBox2.getSelectedItem().equals("Fletching")) {
					Chosen = PotionStats.FLETCHING;
				}
				if (comboBox2.getSelectedItem().equals("Weapon Poison")) {
					Chosen = PotionStats.WEAPONPOISON;
				}
				if (comboBox2.getSelectedItem().equals("Super Restore")) {
					Chosen = PotionStats.SUPERRESTORE;
				}
				if (comboBox2.getSelectedItem().equals("Super Defence")) {
					Chosen = PotionStats.SUPERDEFENCE;
				}
				if (comboBox2.getSelectedItem().equals("Anti-Fire")) {
					Chosen = PotionStats.ANTIFIRE;
				}
				if (comboBox2.getSelectedItem().equals("Ranging")) {
					Chosen = PotionStats.RANGING;
				}
				if (comboBox2.getSelectedItem().equals("Magic")) {
					Chosen = PotionStats.MAGIC;
				}
				if (comboBox2.getSelectedItem().equals("Zamorak")) {
					Chosen = PotionStats.ZAMORAK;
				}
				if (comboBox2.getSelectedItem().equals("Saradomin")) {
					Chosen = PotionStats.SARADOMIN;
				}
			}
			if (comboBox1.getSelectedItem() == "Full Completes") {
				FullCompletes = true;
				Role = "Full Completes";
				if (comboBox2.getSelectedItem().equals("Attack")) {
					Chosen = PotionStats.ATTACK;
				}
				if (comboBox2.getSelectedItem().equals("Anti-Poison")) {
					Chosen = PotionStats.ANTIPOISON;
				}
				if (comboBox2.getSelectedItem().equals("Strength")) {
					Chosen = PotionStats.STRENGTH;
				}
				if (comboBox2.getSelectedItem().equals("Serum 207")) {
					Chosen = PotionStats.SERUM207;
				}
				if (comboBox2.getSelectedItem().equals("Stat Restore")) {
					Chosen = PotionStats.STATRESTORE;
				}
				if (comboBox2.getSelectedItem().equals("Energy")) {
					Chosen = PotionStats.ENERGY;
				}
				if (comboBox2.getSelectedItem().equals("Defence")) {
					Chosen = PotionStats.DEFENCE;
				}
				if (comboBox2.getSelectedItem().equals("Agility")) {
					Chosen = PotionStats.AGILITY;
				}
				if (comboBox2.getSelectedItem().equals("Combat")) {
					Chosen = PotionStats.COMBAT;
				}
				if (comboBox2.getSelectedItem().equals("Prayer")) {
					Chosen = PotionStats.PRAYER;
				}
				if (comboBox2.getSelectedItem().equals("Summoning")) {
					Chosen = PotionStats.SUMMONING;
				}
				if (comboBox2.getSelectedItem().equals("Super Attack")) {
					Chosen = PotionStats.SUPERATTACK;
				}
				if (comboBox2.getSelectedItem().equals("Super Anti-Poison")) {
					Chosen = PotionStats.SUPERANTIPOISON;
				}
				if (comboBox2.getSelectedItem().equals("Fishing")) {
					Chosen = PotionStats.FISHING;
				}
				if (comboBox2.getSelectedItem().equals("Super Energy")) {
					Chosen = PotionStats.SUPERENERGY;
				}
				if (comboBox2.getSelectedItem().equals("Hunter")) {
					Chosen = PotionStats.HUNTER;
				}
				if (comboBox2.getSelectedItem().equals("Super Strength")) {
					Chosen = PotionStats.SUPERSTRENGTH;
				}
				if (comboBox2.getSelectedItem().equals("Fletching")) {
					Chosen = PotionStats.FLETCHING;
				}
				if (comboBox2.getSelectedItem().equals("Weapon Poison")) {
					Chosen = PotionStats.WEAPONPOISON;
				}
				if (comboBox2.getSelectedItem().equals("Super Restore")) {
					Chosen = PotionStats.SUPERRESTORE;
				}
				if (comboBox2.getSelectedItem().equals("Super Defence")) {
					Chosen = PotionStats.SUPERDEFENCE;
				}
				if (comboBox2.getSelectedItem().equals("Anti-Fire")) {
					Chosen = PotionStats.ANTIFIRE;
				}
				if (comboBox2.getSelectedItem().equals("Ranging")) {
					Chosen = PotionStats.RANGING;
				}
				if (comboBox2.getSelectedItem().equals("Magic")) {
					Chosen = PotionStats.MAGIC;
				}
				if (comboBox2.getSelectedItem().equals("Zamorak")) {
					Chosen = PotionStats.ZAMORAK;
				}
				if (comboBox2.getSelectedItem().equals("Saradomin")) {
					Chosen = PotionStats.SARADOMIN;
				}
			}
			if (comboBox1.getSelectedItem().equals("Extremes")) {
				Extremes = true;
				Role = "Extremes";
				if (comboBox2.getSelectedItem().equals("Attack")) {
					EChosen = ExtremeStats.ATTACK;
				}
				if (comboBox2.getSelectedItem().equals("Strength")) {
					EChosen = ExtremeStats.STRENGTH;
				}
				if (comboBox2.getSelectedItem().equals("Defence")) {
					EChosen = ExtremeStats.DEFENCE;
				}
				if (comboBox2.getSelectedItem().equals("Magic")) {
					EChosen = ExtremeStats.MAGIC;
				}
				if (comboBox2.getSelectedItem().equals("Ranging")) {
					EChosen = ExtremeStats.RANGING;
				}
			}
			if (comboBox1.getSelectedItem().equals("Tars")) {
				Taring = true;
				Role = "Tar Making";
				if (comboBox2.getSelectedItem().equals("Guam")) {
					TChosen = TarStats.GUAM;
				}
				if (comboBox2.getSelectedItem().equals("Marrentill")) {
					TChosen = TarStats.MARRENTILL;
				}
				if (comboBox2.getSelectedItem().equals("Tarromin")) {
					TChosen = TarStats.TARROMIN;
				}
				if (comboBox2.getSelectedItem().equals("Harralander")) {
					TChosen = TarStats.HARRALANDER;
				}
			}
			dispose();
		}
	}

	// Order: Complete, Ingredient, Outcome (Finished, Product, etc.)
	enum ExtremeStats {
		ATTACK(145, 261, 15309), STRENGTH(157, 267, 15313), DEFENCE(163, 2481,
				15317), MAGIC(3042, 9594, 15321), RANGING(169, 12539, 15325);

		private final int CompleteID;
		private final int IngredientID;
		private final int OutcomeID;

		private ExtremeStats(final int CompleteID, final int IngredientID,
		                     final int OutcomeID) {
			this.CompleteID = CompleteID;
			this.IngredientID = IngredientID;
			this.OutcomeID = OutcomeID;
		}
	}

	// Order: Grimy, Clean, Unf, Ingredient, Complete
	enum PotionStats {
		ATTACK(199, 249, 91, 221, 121), ANTIPOISON(201, 251, 93, 235, 175), STRENGTH(
				203, 253, 95, 225, 115), SERUM207(203, 253, 95, 592, 3410), STATRESTORE(
				205, 255, 97, 223, 127), ENERGY(205, 255, 97, 1975, 3010), DEFENCE(
				207, 257, 99, 239, 133), AGILITY(3049, 2998, 3002, 2152, 3034), COMBAT(
				205, 255, 97, 9736, 9741), PRAYER(207, 257, 99, 231, 139), SUMMONING(
				12174, 12172, 12181, 12109, 12142), SUPERATTACK(209, 259, 101,
				221, 145), SUPERANTIPOISON(209, 259, 101, 235, 181), FISHING(
				211, 261, 103, 231, 151), SUPERENERGY(211, 261, 103, 2970, 3018), HUNTER(
				211, 261, 103, 10111, 10000), SUPERSTRENGTH(213, 263, 105, 225,
				157), FLETCHING(14836, 14854, 14856, 11525, 14848), WEAPONPOISON(
				213, 263, 105, 241, 187), SUPERRESTORE(3051, 3000, 3004, 223,
				3026), SUPERDEFENCE(215, 265, 107, 239, 163), ANTIFIRE(2485,
				2481, 2483, 241, 2454), RANGING(217, 267, 109, 245, 169), MAGIC(
				2485, 2481, 2483, 3138, 3042), ZAMORAK(219, 269, 111, 247, 189), SARADOMIN(
				3049, 2998, 3002, 6693, 6687);

		private final int GrimyID;
		private final int CleanID;
		private final int UnfID;
		private final int IngredientID;
		private final int CompleteID;

		private PotionStats(final int GrimyID, final int CleanID,
		                    final int UnfID, final int IngredientID, final int CompleteID) {
			this.GrimyID = GrimyID;
			this.CleanID = CleanID;
			this.UnfID = UnfID;
			this.IngredientID = IngredientID;
			this.CompleteID = CompleteID;
		}
	}

	enum State {
		FINISHED, MIXING, INTERFACE, BANK, WITHDRAW, CLEAN, MIXUNF, MIXCOMPLETE, UNFDONE, COMPLETEDONE, SLEEP, CHECKEXP, MOVECAMERA, MOUSEOFFSCREEN, MOVEMOUSE, NULL
	}

	// Order: Grimy, Clean, Outcome (Finished, Product, etc.)
	enum TarStats {
		GUAM(199, 249, 10142), MARRENTILL(201, 251, 10143), TARROMIN(203, 253,
				10144), HARRALANDER(205, 255, 10145);

		private final int GrimyID;
		private final int CleanID;
		private final int OutcomeID;

		private TarStats(final int GrimyID, final int CleanID,
		                 final int OutcomeID) {
			this.GrimyID = GrimyID;
			this.CleanID = CleanID;
			this.OutcomeID = OutcomeID;
		}
	}

	boolean isPaintShowing = true;
	Rectangle PaintButton = new Rectangle(492, 344, 16, 15);
	Point p;
	boolean StartedScript = false;
	boolean HasItemIDs = false;
	boolean isInteracting = false;
	int VialID = 227;
	int CleanID;
	int UnfID;
	int IngredientID;

	int CompleteID;
	int GrimyID;
	int OutcomeID;

	int SwampTarID = 1939;
	int Mortar = 233;

	int StartEXP = 0;
	int EXPGained = 0;

	int Made = 0;
	long Duration;

	String Role;
	boolean Unfs = false;

	State UnfState;
	boolean Completes = false;

	State CompleteState;
	boolean FullCompletes = false;

	State FullState;
	boolean Cleaning = false;

	State CleanState;

	boolean Extremes = false;

	State ExtremeState;
	boolean Taring = false;
	State TarState;

	State AntiBans;

	BWHerbloreGUI gui;

	PotionStats Chosen;

	ExtremeStats EChosen;

	TarStats TChosen;

	private final Font font1 = new Font("Arial", 1, 15);

	private final Image MouseUp = getImage("http://dl.dropbox.com/u/22127840/Pictures/Mouse%201.png");

	private final Image MouseDown = getImage("http://dl.dropbox.com/u/22127840/Pictures/Mouse%202.png");

	private final Image img1 = getImage("http://i842.photobucket.com/albums/zz348/monkey123502/Layout.jpg");

	private final Image img2 = getImage("http://i842.photobucket.com/albums/zz348/monkey123502/ExitButton.png");

	private final Image img3 = getImage("http://www.global-rs.com/img/herblore_vials.gif");

	private final Image img4 = getImage("http://services.runescape.com/m=avatar-rs/avatar.png?id=4474933");

	private final Image img5 = getImage("http://dl.dropbox.com/u/22127840/Pictures/S0oDSX1304285019.png");

	private final Image img6 = getImage("http://dl.dropbox.com/u/22127840/Pictures/TuvlVR1304284961.png");

	private final Image img7 = getImage("http://dl.dropbox.com/u/22127840/Pictures/Open%20Button.png");

	State AntiBans() {
		if (getMyPlayer().getAnimation() != -1 && !bank.isOpen()
				&& random(1, 100) == 70) {
			return State.CHECKEXP;
		} else if ((getMyPlayer().getAnimation() != -1 || Cleaning == true
				&& inventoryContainsBoth(GrimyID, CleanID))
				&& !bank.isOpen() && random(1, 20) == 10) {
			return State.MOVECAMERA;
		} else if (getMyPlayer().getAnimation() != -1 && !bank.isOpen()
				&& random(1, 20) == 15) {
			return State.MOUSEOFFSCREEN;
		} else if (getMyPlayer().getAnimation() != -1 && !bank.isOpen()
				&& random(1, 20) == 20) {
			return State.MOVEMOUSE;
		} else {
			return State.NULL;
		}
	}

	void CleanBank() {
		if (!inventory.contains(GrimyID) && inventory.getCount() > 0) {
			bank.depositAll();
			sleep(random(600, 650));
		}
		if (!inventory.contains(GrimyID) && inventory.getCount() < 1) {
			if (bank.getItem(GrimyID) != null) {
				bank.withdraw(GrimyID, 0);
				sleep(random(350, 425));
			}
		}
		if (inventory.contains(GrimyID) && bank.isOpen()) {
			bank.close();
		}
	}

	State CleanState() {
		if (!inventory.contains(GrimyID) && !bank.isOpen()
				|| inventory.getCount() < 1) {
			return State.BANK;
		} else if (bank.isOpen()) {
			return State.WITHDRAW;
		} else if (inventory.contains(GrimyID) && !bank.isOpen()) {
			return State.CLEAN;
		} else {
			return State.NULL;
		}
	}

	void clickInterface(final int Parent, final int Child) {
		if (interfaces.get(Parent).getComponent(Child).isValid()) {
			interfaces.get(Parent).getComponent(Child).doClick(true);
			isInteracting = true;
			sleep(1250);
		} else {
			sleep(100);
		}
	}

	void CompleteBank() {
		if (!inventoryContainsBoth(UnfID, IngredientID)
				&& inventory.getCount() > 0) {
			bank.depositAll();
			sleep(random(600, 650));
		}
		if (!inventoryContainsBoth(UnfID, IngredientID)
				&& inventory.getCount() < 1) {
			if (!inventory.contains(UnfID) && !inventory.contains(IngredientID)
					&& bank.getItem(UnfID) != null) {
				bank.withdraw(UnfID, 14);
				sleep(random(350, 425));
			}
		}
		if ((inventory.getCount(UnfID) == 14 || inventory.getCount(UnfID) > 0)
				&& !inventory.contains(IngredientID)
				&& bank.getItem(IngredientID) != null) {
			bank.withdraw(IngredientID, 0);
			sleep(random(350, 425));
		} else {
			if (inventory.getCount(UnfID) > 14) {
				bank.depositAll();
				sleep(random(450, 575));
			}
		}
		if (inventoryContainsBoth(UnfID, IngredientID)) {
			bank.close();
		}
	}

	State CompleteState() {
		if (getMyPlayer().getAnimation() != -1) {
			return State.SLEEP;
		} else if (!inventoryContainsBoth(UnfID, IngredientID)
				&& isInteracting == true) {
			return State.FINISHED;
		} else if (inventoryContainsBoth(UnfID, IngredientID)
				&& isInteracting == false && !bank.isOpen()
				&& !interfaces.get(905).getComponent(14).isValid()) {
			return State.MIXING;
		} else if (interfaces.get(905).getComponent(14).isValid()) {
			return State.INTERFACE;
		} else if (!inventoryContainsBoth(UnfID, IngredientID)
				&& inventory.getCount() > 0 && !bank.isOpen()
				|| inventory.getCount() < 1) {
			return State.BANK;
		} else if (bank.isOpen()) {
			return State.WITHDRAW;
		} else {
			return State.NULL;
		}
	}

	String convertDurationToString(long t) {
		t /= 1000;
		final StringBuilder s = new StringBuilder();
		s.append(':');
		if (t % 60 < 10) {
			s.append('0');
		}
		s.append(t % 60);
		t /= 60;
		s.insert(0, t % 60).insert(0, ':');
		if (t % 60 < 10) {
			s.insert(1, '0');
		}
		t /= 60;
		s.insert(0, t % 24);
		if (t % 24 < 10) {
			s.insert(0, '0');
		}
		return new String(s);
	}

	void ExtremeBank() {
		if (EChosen != ExtremeStats.RANGING) {
			if (!inventoryContainsBoth(CompleteID, IngredientID)
					&& inventory.getCount() > 0) {
				bank.depositAll();
				sleep(random(600, 650));
			}
			if (!inventoryContainsBoth(CompleteID, IngredientID)
					&& inventory.getCount() < 1) {
				if (!inventory.contains(CompleteID)
						&& !inventory.contains(IngredientID)
						&& bank.getItem(CompleteID) != null) {
					bank.withdraw(CompleteID, 14);
					sleep(random(350, 425));
				}
			}
			if ((inventory.getCount(CompleteID) == 14 || inventory.getCount(CompleteID) > 0)
					&& !inventory.contains(IngredientID)
					&& bank.getItem(IngredientID) != null) {
				bank.withdraw(IngredientID, 0);
				sleep(random(350, 425));
			} else {
				if (inventory.getCount(CompleteID) > 14) {
					bank.depositAll();
					sleep(random(450, 575));
				}
			}
			if (inventoryContainsBoth(CompleteID, IngredientID)) {
				bank.close();
			}
		} else {
			if (EChosen == ExtremeStats.RANGING) {
				if (!inventory.contains(CompleteID)
						&& inventory.getCountExcept(IngredientID) > 0) {
					bank.depositAllExcept(IngredientID);
					sleep(random(600, 650));
				}
				if (!inventory.contains(CompleteID)
						&& inventory.getCountExcept(IngredientID) < 1) {
					bank.withdraw(CompleteID, 0);
					sleep(random(350, 425));
				}
				if (inventoryContainsBoth(CompleteID, IngredientID)) {
					bank.close();
				}
			}
		}
	}

	State ExtremeState() {
		if (!inventoryContainsBoth(CompleteID, IngredientID)
				&& isInteracting == true) {
			return State.FINISHED;
		} else if (inventoryContainsBoth(CompleteID, IngredientID)
				&& isInteracting == false && !bank.isOpen()
				&& !interfaces.get(905).getComponent(14).isValid()) {
			return State.MIXING;
		} else if (interfaces.get(905).getComponent(14).isValid()) {
			return State.INTERFACE;
		} else if ((EChosen != ExtremeStats.RANGING && inventory.getCount() > 0 || EChosen == ExtremeStats.RANGING
				&& inventory.getCountExcept(IngredientID) > 0)
				&& !inventoryContainsBoth(CompleteID, IngredientID)
				&& !bank.isOpen() || inventory.getCount() < 1) {
			return State.BANK;
		} else if (bank.isOpen()) {
			return State.WITHDRAW;
		} else {
			return State.NULL;
		}
	}

	void FullBank() {
		if (!inventoryContainsBoth(VialID, CleanID)
				&& !inventoryContainsBoth(UnfID, IngredientID)
				&& inventory.getCount() > 0) {
			bank.depositAll();
			sleep(random(600, 650));
		}
		if (!inventoryContainsBoth(VialID, CleanID)
				&& !inventoryContainsBoth(UnfID, IngredientID)
				&& inventory.getCount() < 1) {
			if (!inventory.contains(VialID) && !inventory.contains(CleanID)
					&& !inventory.contains(IngredientID)
					&& bank.getItem(VialID) != null) {
				bank.withdraw(VialID, 9);
			}
		}
		if ((inventory.getCount(VialID) == 9 || inventory.getCount(VialID) > 0)
				&& !inventory.contains(CleanID)
				&& !inventory.contains(IngredientID)
				&& bank.getItem(CleanID) != null) {
			bank.withdraw(CleanID, 9);
			sleep(random(350, 425));
		} else {
			if (inventory.getCount(VialID) > 9) {
				bank.depositAll();
				sleep(random(450, 575));
			}
		}
		if ((inventory.getCount(VialID) == 9 || inventory.getCount(VialID) > 0)
				&& (inventory.getCount(CleanID) == 9 || inventory.getCount(CleanID) > 0)
				&& !inventory.contains(IngredientID)
				&& bank.getItem(IngredientID) != null) {
			bank.withdraw(IngredientID, 0);
			sleep(random(350, 425));
		} else {
			if (inventory.getCount(VialID) > 9
					|| inventory.getCount(CleanID) > 9) {
				bank.depositAll();
				sleep(random(450, 575));
			}
		}
		if (inventoryContainsBoth(VialID, CleanID)
				&& inventory.contains(IngredientID)) {
			bank.close();
		}
	}

	State FullState() {
		if (!inventoryContainsBoth(VialID, CleanID)
				&& !inventory.contains(CompleteID) && isInteracting == true) {
			return State.UNFDONE;
		} else if (inventoryContainsBoth(VialID, CleanID)
				&& isInteracting == false && !bank.isOpen()
				&& !interfaces.get(905).getComponent(14).isValid()) {
			return State.MIXUNF;
		} else if (!inventoryContainsBoth(VialID, CleanID)
				&& !inventoryContainsBoth(UnfID, IngredientID)
				&& isInteracting == true) {
			return State.COMPLETEDONE;
		} else if (!inventoryContainsBoth(VialID, CleanID)
				&& inventoryContainsBoth(UnfID, IngredientID)
				&& isInteracting == false && !bank.isOpen()
				&& !interfaces.get(905).getComponent(14).isValid()) {
			return State.MIXCOMPLETE;
		} else if (interfaces.get(905).getComponent(14).isValid()) {
			return State.INTERFACE;
		} else if (!inventoryContainsBoth(VialID, CleanID)
				&& !inventoryContainsBoth(UnfID, IngredientID)
				&& inventory.getCount() > 0 && !bank.isOpen()
				|| inventory.getCount() < 1) {
			return State.BANK;
		} else if (bank.isOpen()) {
			return State.WITHDRAW;
		} else {
			return State.NULL;
		}
	}

	Image getImage(final String url) {
		try {
			return ImageIO.read(new URL(url));
		} catch (final IOException e) {
			return null;
		}
	}

	double getRot(final int ticks) {
		return System.currentTimeMillis() % (360 * ticks) / ticks;
	}

	boolean inventoryContainsBoth(final int ID1, final int ID2) {
		return inventory.contains(ID1) && inventory.contains(ID2);
	}

	public int loop() {
		try {
			if (interfaces.canContinue()) {
				interfaces.clickContinue();
			}
			if (Cleaning == false && Extremes == false && Taring == false
					&& StartedScript == true && HasItemIDs == false) {
				GrimyID = Chosen.GrimyID;
				CleanID = Chosen.CleanID;
				UnfID = Chosen.UnfID;
				IngredientID = Chosen.IngredientID;
				CompleteID = Chosen.CompleteID;
				log("~~~~~ Information About Your Potion ~~~~~");
				log("GrimyID ID: " + GrimyID + " | GrimyID Name: "
						+ grandExchange.lookup(GrimyID).getName());
				log("CleanID ID: " + CleanID + " | CleanID Name: "
						+ grandExchange.lookup(CleanID).getName());
				log("UnfID ID: " + UnfID + " | UnfID Name: "
						+ grandExchange.lookup(UnfID).getName());
				log("IngredientID For CompleteID ID: " + IngredientID
						+ " | IngredientID Name: "
						+ grandExchange.lookup(IngredientID).getName());
				log("CompleteID ID: " + CompleteID + " | CompleteID Name: "
						+ grandExchange.lookup(CompleteID).getName());
				HasItemIDs = true;
			}
			if (Cleaning == true && StartedScript == true
					&& HasItemIDs == false) {
				GrimyID = Chosen.GrimyID;
				CleanID = Chosen.CleanID;
				log("~~~~~ Information About Your Herb ~~~~~");
				log("GrimyID ID: " + GrimyID + " | GrimyID Name: "
						+ grandExchange.lookup(GrimyID).getName());
				log("CleanID ID: " + CleanID + " | CleanID Name: "
						+ grandExchange.lookup(CleanID).getName());
				HasItemIDs = true;
			}
			if (Extremes == true && StartedScript == true
					&& HasItemIDs == false) {
				CompleteID = EChosen.CompleteID;
				IngredientID = EChosen.IngredientID;
				OutcomeID = EChosen.OutcomeID;
				log("~~~~~ Information About Your Extreme ~~~~~");
				log("Potion ID: " + CompleteID + " | Potion Name: "
						+ grandExchange.lookup(CompleteID).getName());
				log("IngredientID ID: " + IngredientID);
				log("OutcomeID ID: " + OutcomeID);
				HasItemIDs = true;
			}
			if (Taring == true && StartedScript == true && HasItemIDs == false) {
				GrimyID = TChosen.GrimyID;
				CleanID = TChosen.CleanID;
				OutcomeID = TChosen.OutcomeID;
				log("~~~~~ Information About Your SwampTarID ~~~~~");
				log("GrimyID ID: " + GrimyID + " | GrimyID Name: "
						+ grandExchange.lookup(GrimyID).getName());
				log("CleanID ID: " + CleanID + " | CleanID Name: "
						+ grandExchange.lookup(CleanID).getName());
				log("OutcomeID ID: " + OutcomeID + " | OutcomeID Name: "
						+ grandExchange.lookup(OutcomeID).getName());
				HasItemIDs = true;
			}
			if (game.isLoggedIn()) {
				AntiBans = AntiBans();
				switch (AntiBans) {
					case CHECKEXP:
						skills.doHover(Skills.getIndex("Herblore"));
						sleep(500, 1500);
						break;
					case MOVECAMERA:
						camera.setAngle(random(100, 359));
						sleep(500, 1500);
						break;
					case MOUSEOFFSCREEN:
						mouse.moveOffScreen();
						sleep(random(2000, 4000));
						break;
					case MOVEMOUSE:
						mouse.moveSlightly();
						sleep(300, 700);
						mouse.moveRandomly(40, 860);
						break;
				}
			}
			if (Cleaning == true && StartedScript == true && HasItemIDs == true) {
				CleanState = CleanState();
				switch (CleanState) {
					case CLEAN:
						MouseKeyAll(GrimyID, false, "CleanID");
						break;
					case BANK:
						bank.open();
						break;
					case WITHDRAW:
						while (bank.isOpen()) {
							CleanBank();
						}
						break;
				}
			}
			if (Unfs == true && StartedScript == true && HasItemIDs == true) {
				UnfState = UnfState();
				switch (UnfState) {
					case FINISHED:
						isInteracting = false;
						break;
					case MIXING:
						Mix(VialID, CleanID);
						break;
					case INTERFACE:
						clickInterface(905, 14);
						break;
					case BANK:
						bank.open();
						break;
					case WITHDRAW:
						while (bank.isOpen()) {
							UnfBank();
						}
						break;
				}
			}
			if (Completes == true && StartedScript == true
					&& HasItemIDs == true) {
				CompleteState = CompleteState();
				switch (CompleteState) {
					case FINISHED:
						isInteracting = false;
						break;
					case MIXING:
						Mix(UnfID, IngredientID);
						break;
					case INTERFACE:
						clickInterface(905, 14);
						break;
					case BANK:
						bank.open();
						break;
					case WITHDRAW:
						while (bank.isOpen()) {
							CompleteBank();
						}
						break;
				}
			}
			if (FullCompletes == true && StartedScript == true
					&& HasItemIDs == true) {
				FullState = FullState();
				switch (FullState) {
					case SLEEP:
						sleep(random(1000, 1500));
						break;
					case UNFDONE:
						isInteracting = false;
						break;
					case MIXUNF:
						Mix(VialID, CleanID);
						break;
					case COMPLETEDONE:
						isInteracting = false;
						break;
					case MIXCOMPLETE:
						Mix(UnfID, IngredientID);
						break;
					case INTERFACE:
						clickInterface(905, 14);
						break;
					case BANK:
						bank.open();
						break;
					case WITHDRAW:
						;
						while (bank.isOpen()) {
							FullBank();
						}
						break;
				}
			}
			if (Extremes == true && StartedScript == true && HasItemIDs == true) {
				ExtremeState = ExtremeState();
				switch (ExtremeState) {
					case FINISHED:
						isInteracting = false;
						break;
					case MIXING:
						Mix(CompleteID, IngredientID);
						break;
					case INTERFACE:
						clickInterface(905, 14);
						break;
					case BANK:
						bank.open();
						break;
					case WITHDRAW:
						while (bank.isOpen()) {
							ExtremeBank();
						}
						break;
				}
			}
			if (Taring == true && StartedScript == true && HasItemIDs == true) {
				TarState = TarState();
				switch (TarState) {
					case FINISHED:
						isInteracting = false;
						break;
					case MIXING:
						Mix(SwampTarID, CleanID);
						break;
					case INTERFACE:
						clickInterface(905, 14);
						break;
					case BANK:
						bank.open();
						break;
					case WITHDRAW:
						while (bank.isOpen()) {
							TarBank();
						}
						break;
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void messageReceived(final MessageEvent arg0) {
		final String message = arg0.getMessage().toLowerCase();
		if (Cleaning == true && message.contains("clean")) {
			Made++;
		}
		if (Unfs == true && message.contains("into the vial")) {
			Made++;
		}
		if (Completes == true && message.contains("into your potion")
				|| FullCompletes == true
				&& message.contains("into your potion")) {
			Made++;
		}
		if (Taring == true && message.contains("mix the")) {
			Made = Made + 15;
		}
		if (Extremes == true && message.contains("You carefully mix the")) {
			Made++;
		}
	}

	void Mix(final int ID1, final int ID2) {
		try {
			if (isInteracting == false && inventoryContainsBoth(ID1, ID2)
					&& !interfaces.get(905).getComponent(14).isValid()
					&& getMyPlayer().getAnimation() == -1) {
				inventory.getItem(ID1).doAction("Use");
				sleep(random(150, 250));
				inventory.getItem(ID2).doClick(true);
				sleep(random(1100, 1250));
			}
		} catch (final NullPointerException e) {
			e.printStackTrace();
		}
	}

	public void mouseClicked(final MouseEvent e) {
		p = e.getPoint();
		if (PaintButton.contains(p) && !isPaintShowing) {
			isPaintShowing = true;
		} else if (PaintButton.contains(p) && isPaintShowing) {
			isPaintShowing = false;
		}
	}

	public void mouseEntered(final MouseEvent e) {
	}

	public void mouseExited(final MouseEvent e) {
	}

	// If you use my MouseKey Method.. GIVE CREDIT Or don't use it! ~ BlackWood
	// ~
	void MouseKeyAll(final int ID, final boolean UseMenu,
	                 final String Interaction) {
		int[] MousePath = {};
		final int[] MousePath1 = {0, 1, 4, 5, 8, 9, 12, 13, 16, 17, 20, 21,
				24, 25, 26, 27, 23, 22, 19, 18, 15, 14, 11, 10, 7, 6, 3, 2};
		final int[] MousePath2 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
				13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27};
		final int[] MousePath3 = {0, 4, 8, 12, 16, 20, 24, 1, 5, 9, 13, 17,
				21, 25, 2, 6, 10, 14, 18, 22, 26, 3, 7, 11, 15, 19, 23, 27};
		final int[] MousePath4 = {27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17,
				16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0};
		switch (random(1, 4)) {
			case 1:
				MousePath = MousePath1;
				break;
			case 2:
				MousePath = MousePath2;
				break;
			case 3:
				MousePath = MousePath3;
				break;
			case 4:
				MousePath = MousePath4;
				break;
		}
		for (int Slot = 0; Slot <= 27; Slot++) {
			if (inventory.contains(ID)) {
				if (inventory.getItemAt(MousePath[Slot]).getID() == ID) {
					final Point Item = new Point(inventory.getItemAt(MousePath[Slot]).getComponent().getCenter().x, inventory.getItemAt(MousePath[Slot]).getComponent().getCenter().y);
					if (!menu.isOpen()) {
						mouse.hop(Item.x, Item.y, 2, 2);
						sleep(115, 130);
						if (UseMenu == false) {
							mouse.click(true);
						}
					}
					if (UseMenu == true) {
						if (!menu.isOpen()) {
							mouse.click(false);
							sleep(115, 130);
						}
						if (menu.contains(Interaction)) {
							final int Action = menu.getIndex(Interaction);
							if (Action != -1 && menu.getItems() != null
									&& menu.getLocation() != null) {
								final Point Options = menu.getLocation();
								mouse.hop(Options.getLocation().x + 7, Options.y
										+ 20 + 15 * Action + random(3, 12), 2, 2);
								sleep(50, 65);
								mouse.click(true);
							}
						}
					}
				}
			}
		}
	}

	public void mousePressed(final MouseEvent e) {
	}

	public void mouseReleased(final MouseEvent e) {
	}

	public void onRepaint(final Graphics g1) {
		if (StartedScript == true) {
			final Graphics2D g = (Graphics2D) g1;
			if (StartEXP == 0) {
				StartEXP = skills.getCurrentExp(Skills.getIndex("Herblore"));
			}
			EXPGained = skills.getCurrentExp(Skills.getIndex("Herblore"))
					- StartEXP;
			if (isPaintShowing == true) {
				g.drawImage(img1, 3, 336, null);
				g.drawImage(img2, 488, 344, null);
				g.drawImage(img3, 373, 303, null);
				g.drawImage(img4, 196, 349, null);
				g.drawImage(img5, 225, 436, null);
				g.drawImage(img6, 268, 405, null);
				g.setFont(font1);
				g.setColor(Color.RED);
				g.drawString("Duration: "
						+ convertDurationToString(System.currentTimeMillis()
						- Duration), 10, 467);
				g.drawString("Interactions: " + Made, 10, 358);
				g.drawString("Interactions /H: "
						+ (int) (Made * 3600000D / (System.currentTimeMillis() - Duration)), 10, 377);
				g.drawString("EXP Gained: " + EXPGained, 11, 407);
				g.drawString("EXP /H: "
						+ (int) (EXPGained * 3600000D / (System.currentTimeMillis() - Duration)), 10, 424);
				g.drawString("Role: " + Role, 10, 450);
			}
			if (isPaintShowing == false) {
				g.drawImage(img7, 488, 344, null);
			}
			g.rotate(Math.toRadians(getRot(5)), mouse.getLocation().x, mouse.getLocation().y);
			g.drawImage(MouseUp, mouse.getLocation().x - 12, mouse.getLocation().y - 12, null);
			if (mouse.isPressed()) {
				g.rotate(Math.toRadians(getRot(8)), mouse.getLocation().x, mouse.getLocation().y);
				g.drawImage(MouseDown, mouse.getLocation().x - 12, mouse.getLocation().y - 12, null);
			}
		}
	}

	public boolean onStart() {
		try {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					gui = new BWHerbloreGUI();
					gui.setVisible(true);
				}
			});
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	void TarBank() {
		if (!inventoryContainsBoth(SwampTarID, CleanID)
				&& inventory.getCount() > 0) {
			bank.depositAllExcept(SwampTarID, Mortar, CleanID);
			sleep(random(600, 650));
		}
		if (!inventory.contains(Mortar)) {
			bank.withdraw(Mortar, 1);
			sleep(random(350, 425));
		}
		if (!inventory.contains(SwampTarID)) {
			bank.withdraw(SwampTarID, 0);
		}
		if (inventory.contains(SwampTarID) && !inventory.contains(CleanID)) {
			bank.withdraw(CleanID, 0);
			sleep(random(350, 425));
		}
		if (inventoryContainsBoth(SwampTarID, CleanID)
				&& inventory.contains(Mortar)) {
			bank.close();
		}
	}

	State TarState() {
		if (!inventoryContainsBoth(SwampTarID, CleanID)
				&& isInteracting == true) {
			return State.FINISHED;
		} else if (!inventory.contains(CleanID) && !bank.isOpen()
				|| inventory.getCount() < 1) {
			return State.BANK;
		} else if (bank.isOpen()) {
			return State.WITHDRAW;
		} else if (inventory.contains(CleanID) && !bank.isOpen()
				&& isInteracting == false && !bank.isOpen()
				&& !interfaces.get(905).getComponent(14).isValid()) {
			return State.MIXING;
		} else if (interfaces.get(905).getComponent(14).isValid()) {
			return State.INTERFACE;
		} else {
			return State.NULL;
		}
	}

	void UnfBank() {
		if (!inventoryContainsBoth(VialID, CleanID) && inventory.getCount() > 0) {
			bank.depositAll();
			sleep(random(600, 650));
		}
		if (!inventoryContainsBoth(VialID, CleanID) && inventory.getCount() < 1) {
			if (!inventory.contains(VialID) && !inventory.contains(CleanID)
					&& bank.getItem(VialID) != null) {
				bank.withdraw(VialID, 14);
				sleep(random(350, 425));
			}
		}
		if ((inventory.getCount(VialID) == 14 || inventory.getCount(VialID) > 0)
				&& !inventory.contains(CleanID)
				&& bank.getItem(CleanID) != null) {
			bank.withdraw(CleanID, 0);
			sleep(random(350, 425));
		} else {
			if (inventory.getCount(VialID) > 14) {
				bank.depositAll();
				sleep(random(450, 575));
			}
		}
		if (inventoryContainsBoth(VialID, CleanID)) {
			bank.close();
		}
	}

	State UnfState() {
		if (!inventoryContainsBoth(VialID, CleanID) && isInteracting == true) {
			return State.FINISHED;
		} else if (inventoryContainsBoth(VialID, CleanID)
				&& isInteracting == false && !bank.isOpen()
				&& !interfaces.get(905).getComponent(14).isValid()) {
			return State.MIXING;
		} else if (interfaces.get(905).getComponent(14).isValid()) {
			return State.INTERFACE;
		} else if (!inventoryContainsBoth(VialID, CleanID)
				&& inventory.getCount() > 0 && !bank.isOpen()
				|| inventory.getCount() < 1) {
			return State.BANK;
		} else if (bank.isOpen()) {
			return State.WITHDRAW;
		} else {
			return State.NULL;
		}
	}

}
