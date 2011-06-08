import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Magic;
import org.rsbot.script.methods.Skills;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "ElyzianPirate" }, name = "PiratePlanker", version = 1.52, website = "http://www.powerbot.org/vb/showthread.php?t=544986", description = "Fastest planker from N,S,E and W.")
public class PiratePlanker extends Script implements PaintListener,
		MessageListener, MouseListener, MouseMotionListener {

	private class antibanSystem {
		public class CameraHeightThread extends Thread {

			@Override
			public void run() {
				char UD = KeyEvent.VK_UP;
				if (random(0, 2) == 0) {
					UD = KeyEvent.VK_DOWN;
				}
				keyboard.pressKey(UD);
				try {
					Thread.sleep(random(450, 1700));
				} catch (final Exception ignored) {
				}
				keyboard.releaseKey(UD);
			}
		}

		public class CameraRotateThread extends Thread {
			@Override
			public void run() {
				char LR = KeyEvent.VK_RIGHT;
				if (random(0, 2) == 0) {
					LR = KeyEvent.VK_LEFT;
				}
				keyboard.pressKey(LR);
				try {
					Thread.sleep(random(450, 2600));
				} catch (final Exception ignored) {
				}
				keyboard.releaseKey(LR);
			}
		}

		public RSTile examineRandomObject(final int scans) {
			final RSTile start = getMyPlayer().getLocation();
			final ArrayList<RSTile> possibleTiles = new ArrayList<RSTile>();
			for (int h = 1; h < scans * scans; h += 2) {
				for (int i = 0; i < h; i++) {
					for (int j = 0; j < h; j++) {
						final int offset = (h + 1) / 2 - 1;
						if (i > 0 && i < h - 1) {
							j = h - 1;
						}
						final RSTile tile = new RSTile(start.getX() - offset
								+ i, start.getY() - offset + j);
						final RSObject objectToList = objects.getTopAt(tile);
						if (objectToList != null
								&& calc.tileOnScreen(objectToList.getLocation())
								&& objectToList.getLocation() != null) {
							possibleTiles.add(objectToList.getLocation());
						}
					}
				}
			}
			if (possibleTiles.size() == 0) {
				return null;
			}
			if (possibleTiles.size() > 0 && possibleTiles != null) {
				final RSTile objectLoc = possibleTiles.get(randGenerator(0, possibleTiles.size()));
				final Point objectPoint = calc.tileToScreen(objectLoc);
				if (objectPoint != null) {
					try {
						mouse.move(objectPoint);
						if (menu.doAction("xamine")) {
						} else {
						}
						sleep(random(100, 500));
					} catch (final NullPointerException ignored) {
					}
				}
			}
			return null;
		}

		/*
		 * ---------------------------------- ------------ Standard ------------
		 * --------------------------------
		 */
		public void handleAntiban() {
			final int Percentage = random(1, 100);
			final int AFK1 = random(0, 3);
			final int AFK2 = random(4, 8);
			final int chckObj = random(1, (13000 / Percentage));
			final int hover = random(1, (10000 / Percentage));
			final int checkxp = random(1, (13000 / Percentage));
			final int afk = random(1, (10000 / Percentage));
			final int camerahh = random(1, 200);
			final int hoverObject = random(1, (8000 / Percentage));
			if (Percentage != 0) {

				if (chckObj == 5) {
					game.openTab(Game.TAB_SUMMONING);
					sleep(random(300, 500));
					mouse.move(644, 394, 51, 6);
					sleep(random(900, 1600));
					mouse.move(644, 394, 51, 6);
					sleep(random(500, 1000));
					mouse.moveRandomly(500);
					sleep(random(400, 900));
					game.openTab(Game.TAB_MAGIC);
				} else if (hover == 5) {
					hoverPlayer();
					sleep(random(1150, 2800));
					mouse.moveRandomly(750);
					sleep(random(400, 1000));
				}
			} else if (hoverObject == 5) {
				hoverObject();
				sleep(random(1150, 2800));
				mouse.moveRandomly(750);
				sleep(random(400, 1000));
			} else if (checkxp == 5) {
				final int GambleInt5 = random(0, 100);
				if (GambleInt5 > 50) {
					game.openTab(Game.TAB_STATS);
					sleep(random(400, 800));
					mouse.move(584, 364, 20, 10); // Magic LvL
					sleep(random(800, 1200));
					mouse.move(584, 364, 20, 10); // Magic LvL
					sleep(random(900, 1750));
					mouse.moveRandomly(700);
					sleep(random(300, 800));
					game.openTab(Game.TAB_MAGIC);
				}
			} else if (afk == 5) {
				switch (random(1, 4)) {
				case 1:
					sleep(random(AFK1, AFK2));
					break;
				case 2:
					sleep(random(AFK1 / 4, AFK2 / 10));
					mouse.moveRandomly(750);
					sleep(random(AFK1, AFK2));
					break;
				case 3:
					sleep(random(0, 500));
					mouse.moveRandomly(1000);
					sleep(random(AFK1 / 4, AFK2 / 10));
					mouse.moveRandomly(1500);
					sleep(random(AFK1, AFK2));
					break;
				}
			} else if (camerahh == 5) {
				final int randomTurn = random(1, 4);
				switch (randomTurn) {
				case 1:
					new CameraRotateThread().start();
					break;
				case 2:
					new CameraHeightThread().start();
					break;
				case 3:
					final int randomFormation = random(0, 2);
					if (randomFormation == 0) {
						new CameraRotateThread().start();
						new CameraHeightThread().start();
					} else {
						new CameraHeightThread().start();
						mouse.moveRandomly(200);
						new CameraRotateThread().start();
					}
				}
			}
			return;
		}

		public void hoverObject() {
			examineRandomObject(5);
			sleep(randGenerator(50, 1000));
			final int mousemoveAfter2 = randGenerator(0, 4);
			sleep(randGenerator(100, 800));
			if (mousemoveAfter2 == 1 && mousemoveAfter2 == 2) {
				mouse.move(1, 1, 760, 500);
			}
		}

		boolean hoverPlayer() {
			RSPlayer player = null;
			final RSPlayer[] validPlayers = players.getAll();

			player = validPlayers[random(0, validPlayers.length - 1)];
			if (player != null) {
				try {
					final String playerName = player.getName();
					final String myPlayerName = getMyPlayer().getName();
					if (playerName.equals(myPlayerName)) {
						return false;
					}
				} catch (final NullPointerException e) {
				}
				try {
					final RSTile targetLoc = player.getLocation();
					final Point checkPlayer = calc.tileToScreen(targetLoc);
					if (calc.pointOnScreen(checkPlayer) && checkPlayer != null) {
						mouse.click(checkPlayer, 5, 5, false);
					} else {
						return false;
					}
					return true;
				} catch (final Exception ignored) {
				}
			}
			return false;
		}

		int randGenerator(final int min, final int max) {
			return min + (int) (java.lang.Math.random() * (max - min));
		}
	}

	private class Database {
		String reason = "Uknown";
		private boolean shutdownScript = false;
		private int mouseSpeed = 5;
		private boolean canCast = true;
		private boolean firstBanked = false;
		private int logsAvailable;
		private int logID = 6332;
		private final int noBank[] = { 995, 9075, 561 };
		private int levelsGained;
		private int castCount;
		private boolean guiWait = true, guiExit;
		PlankerGUI gui;
	}

	private class MethodProvider {
		private void castPlankMake() {
			if (!magic.isSpellSelected()) {
				magic.castSpell(Magic.SPELL_PLANK_MAKE);
				sleep(100, 180);
			} else {
				final RSItem inventoryLog = inventory.getItem(data.logID);
				if (inventoryLog == null) {
					data.canCast = false;
				}
				if (inventoryLog != null && magic.isSpellSelected()) {
					inventoryLog.doClick(true);
					data.castCount++;
					antiban.handleAntiban();
					sleep(1300);
				}
			}
		}

		public void handleBanking() {
			if (!bank.isOpen()) {
				bank.open();
			} else {
				bank.depositAllExcept(data.noBank);
				sleep(100);
				bank.withdraw(data.logID, 25);
				data.logsAvailable = bank.getCount(data.logID);
				bank.close();
				data.canCast = true;
				data.firstBanked = true;
			}
		}
	}

	private class PaintHandler {
		BufferedImage cursor = null;
		BufferedImage background = null;
		private int startXp;
		private final long startTime = System.currentTimeMillis();
		int xpGained;

		private final Color color1 = new Color(255, 255, 255);
		private final Font font1 = new Font("Simplified Arabic Fixed", 1, 12);

		private void buildPaint(final Graphics g1) {
			long millis = System.currentTimeMillis() - startTime;
			final long hours = millis / (1000 * 60 * 60);
			millis -= hours * 1000 * 60 * 60;
			final long minutes = millis / (1000 * 60);
			millis -= minutes * 1000 * 60;
			final long seconds = millis / 1000;
			xpGained = skills.getCurrentExp(Skills.MAGIC) - startXp;
			final int percTolvl = 100 - skills.getPercentToNextLevel(Skills.MAGIC);
			@SuppressWarnings("unused")
			final int fillamount = (int) (3.8 * percTolvl);
			final float expHour = (float) xpGained
					/ (float) (seconds + minutes * 60 + hours * 60 * 60) * 60
					* 60;
			final float castsHour = (float) data.castCount
					/ (float) (seconds + minutes * 60 + hours * 60 * 60) * 60
					* 60;
			final Graphics2D g = (Graphics2D) g1;
			g.drawImage(background, 3, 101, null);
			g.setFont(font1);
			g.setColor(color1);
			g.drawString("Exp gained: " + xpGained, 16, 165);
			g.drawString("Total casts: " + data.castCount, 16, 185);
			g.drawString("Levels gained: " + data.levelsGained, 16, 205);
			g.drawString("Exp/h: " + Math.round(expHour), 16, 225);
			g.drawString("Casts/h: " + Math.round(castsHour), 16, 245);
			g.drawString("Percent to lvl: " + percTolvl, 16, 265);
			g.drawString("" + hours + ":" + minutes + ":" + seconds + "", 40, 322);
			g.drawString("Logs left: " + data.logsAvailable, 16, 285);
			if (cursor != null) {
				final int mouse_x = mouse.getLocation().x;
				final int mouse_y = mouse.getLocation().y;
				g1.drawImage(cursor, mouse_x - 8, mouse_y - 8, null);
			}
		}
	}

	public class PlankerGUI extends JFrame {
		private static final long serialVersionUID = 1L;

		private JLabel label1;

		private JLabel label2;

		private JComboBox plankTypeBox;

		private JLabel label3;

		private JSlider speedSlider;
		private JLabel label4;
		private JButton startButton;
		private JButton exitButton;
		private JPanel panel1;

		public PlankerGUI() {
			initComponents();
		}

		private void exitButtonActionPerformed(final ActionEvent e) {
			dispose();
		}

		private void initComponents() {
			label1 = new JLabel();
			label2 = new JLabel();
			plankTypeBox = new JComboBox();
			label3 = new JLabel();
			speedSlider = new JSlider();
			label4 = new JLabel();
			startButton = new JButton();
			exitButton = new JButton();
			panel1 = new JPanel();

			// ======== this ========
			setResizable(false);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			final Container contentPane = getContentPane();
			contentPane.setLayout(null);

			// ---- label1 ----
			label1.setText("Pirate Planker");
			label1.setFont(new Font("Simplified Arabic Fixed", Font.BOLD, 24));
			label1.setForeground(new Color(153, 153, 0));
			contentPane.add(label1);
			label1.setBounds(55, 5, 228, 36);

			// ---- label2 ----
			label2.setText("By ElyzianPirate");
			label2.setForeground(new Color(0, 153, 204));
			label2.setFont(label2.getFont().deriveFont(label2.getFont().getStyle()
					| Font.ITALIC, label2.getFont().getSize() + 4f));
			contentPane.add(label2);
			label2.setBounds(140, 40, 105, label2.getPreferredSize().height);

			// ---- plankTypeBox ----
			plankTypeBox.setModel(new DefaultComboBoxModel(new String[] {
					"Regular", "Oak", "Teak", "Mahogany" }));
			contentPane.add(plankTypeBox);
			plankTypeBox.setBounds(new Rectangle(new Point(140, 80), plankTypeBox.getPreferredSize()));

			// ---- label3 ----
			label3.setText("Log type:");
			contentPane.add(label3);
			label3.setBounds(new Rectangle(new Point(10, 85), label3.getPreferredSize()));

			// ---- speedSlider ----
			speedSlider.setMaximum(10);
			speedSlider.setMinimum(1);
			speedSlider.setMajorTickSpacing(1);
			speedSlider.setPaintTicks(true);
			speedSlider.setPaintLabels(true);
			speedSlider.setMinorTickSpacing(1);
			speedSlider.setSnapToTicks(true);
			contentPane.add(speedSlider);
			speedSlider.setBounds(new Rectangle(new Point(85, 110), speedSlider.getPreferredSize()));

			// ---- label4 ----
			label4.setText("Mouse Speed:");
			contentPane.add(label4);
			label4.setBounds(new Rectangle(new Point(10, 125), label4.getPreferredSize()));

			// ---- startButton ----
			startButton.setText("Start");
			startButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					startButtonActionPerformed(e);
				}
			});
			contentPane.add(startButton);
			startButton.setBounds(35, 185, 80, startButton.getPreferredSize().height);

			// ---- exitButton ----
			exitButton.setText("Exit");
			exitButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					exitButtonActionPerformed(e);
				}
			});
			contentPane.add(exitButton);
			exitButton.setBounds(190, 185, 86, exitButton.getPreferredSize().height);

			// ======== panel1 ========
			{

				// JFormDesigner evaluation mark
				panel1.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0), "", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), java.awt.Color.red), panel1.getBorder()));
				panel1.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
					@Override
					public void propertyChange(
							final java.beans.PropertyChangeEvent e) {
						if ("border".equals(e.getPropertyName())) {
							throw new RuntimeException();
						}
					}
				});

				panel1.setLayout(null);

				{ // compute preferred size
					final Dimension preferredSize = new Dimension();
					for (int i = 0; i < panel1.getComponentCount(); i++) {
						final Rectangle bounds = panel1.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
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
			contentPane.add(panel1);
			panel1.setBounds(new Rectangle(new Point(315, 215), panel1.getPreferredSize()));

			{ // compute preferred size
				final Dimension preferredSize = new Dimension();
				for (int i = 0; i < contentPane.getComponentCount(); i++) {
					final Rectangle bounds = contentPane.getComponent(i).getBounds();
					preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
					preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
				}
				final Insets insets = contentPane.getInsets();
				preferredSize.width += insets.right;
				preferredSize.height += insets.bottom;
				contentPane.setMinimumSize(preferredSize);
				contentPane.setPreferredSize(preferredSize);
			}
			pack();
			setLocationRelativeTo(getOwner());
		}

		private void startButtonActionPerformed(final ActionEvent e) {
			dispose();
			data.guiWait = false;
			data.mouseSpeed = speedSlider.getValue();
			if (plankTypeBox.getSelectedIndex() == 0) {
				data.logID = 1511;
			}
			if (plankTypeBox.getSelectedIndex() == 1) {
				data.logID = 1521;
			}
			if (plankTypeBox.getSelectedIndex() == 2) {
				data.logID = 6333;
			}
			if (plankTypeBox.getSelectedIndex() == 3) {
				data.logID = 6332;
			}
			log("We are using: " + data.logID);
		}
	}

	antibanSystem antiban = new antibanSystem();

	Database data = new Database();

	MethodProvider methods = new MethodProvider();

	PaintHandler paint = new PaintHandler();

	@Override
	public int loop() {
		if (data.canCast) {
			methods.castPlankMake();
		} else {
			methods.handleBanking();
		}
		if (data.logsAvailable == 0 && !data.canCast && data.firstBanked
				|| data.shutdownScript) {
			log("Plz f33d me moar!");
			log("Reason: " + data.reason);
			return -1;
		}
		return 100;
	}

	@Override
	public void messageReceived(final MessageEvent e) {
		final String message = e.getMessage();
		if (message.contains("advanced a")) {
			data.levelsGained++;
		}
		if (message.contains("Astral Runes to cast")) {
			data.shutdownScript = true;
			data.reason = "Out of Astral rune.";
		}
		if (message.contains("Nature Runes to cast")) {
			data.shutdownScript = true;
			data.reason = "Out of Nature rune.";
		}
		if (message.contains("coins to convert")) {
			data.shutdownScript = true;
			data.reason = "Out of coins.";
		}
	}

	@Override
	public void mouseClicked(final MouseEvent arg0) {
	}

	@Override
	public void mouseDragged(final MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(final MouseEvent arg0) {
	}

	@Override
	public void mouseExited(final MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(final MouseEvent arg0) {
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		// Point mouse = e.getPoint();
	}

	@Override
	public void mouseReleased(final MouseEvent arg0) {
	}

	@Override
	public void onFinish() {
		log("______________________________________________________________");
		log("Thank you for using PiratePlanker!");
		log("We made: " + data.castCount + " planks, gaining " + paint.xpGained
				+ " exp.");
		log("______________________________________________________________");
	}

	@Override
	public void onRepaint(final Graphics g) {
		paint.buildPaint(g);
	}

	// Basic codes
	@Override
	public boolean onStart() {

		try {
			final URL cursorURL = new URL("http://thedealer.site11.com/resources/Arrow.png");
			final URL backgroundURL = new URL("http://thedealer.site11.com/resources/PlankerPaint.png");
			paint.cursor = ImageIO.read(cursorURL);
			paint.background = ImageIO.read(backgroundURL);
		} catch (final MalformedURLException e) {
			log("Unable to buffer cursor.");
		} catch (final IOException e) {
			log("Unable to open cursor image.");
		}
		log("Images loaded sucessfully!");
		data.gui = new PlankerGUI();
		data.gui.setVisible(true);
		while (data.guiWait) {
			sleep(100);
		}
		mouse.setSpeed(data.mouseSpeed);
		paint.startXp = skills.getCurrentExp(Skills.MAGIC);
		return !data.guiExit;
	}
}
