/**
 * Welcome to Himekp's ProScript's
 * This is Version 1.03
 **********ProConstruction**********
 ************By: Himekp*************
 */

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Skills;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@ScriptManifest(authors = {"Himekp"}, keywords = {"Construction"}, name = "ProConstruction", version = 1.03, description = "ProConstruction, By: Himekp", website = "http://www.powerbot.org/vb/showthread.php?t=787874")
public class ProConstruction extends Script implements PaintListener {
	public class ProConstructionGUI extends JPanel {
		private static final long serialVersionUID = 1L;
		// JFormDesigner - Variables declaration - DO NOT MODIFY
		// //GEN-BEGIN:variables
		private JFrame frame1;

		private JLabel label1;

		private JLabel label2;

		private JComboBox comboBox1;
		private JLabel label3;
		private JLabel label4;
		private JComboBox comboBox2;
		private JLabel label5;
		private JSlider slider1;
		private JButton button1;

		// JFormDesigner - End of variables declaration //GEN-END:variables
		public ProConstructionGUI() {
			initComponents();
		}

		private void button1ActionPerformed(final ActionEvent e) {
			TypeofObject = comboBox2.getSelectedItem().toString();
			TypeofServent = comboBox1.getSelectedItem().toString();
			mouseSpeed = slider1.getValue();

			if (TypeofServent.equals("Normal Butler")) {
				Butler = normalButler;
			}
			if (TypeofServent.equals("Demon Butler")) {
				Butler = demonButler;
			}
			if (TypeofObject.equals("Oak Larder")) {
				WhattoBuild = oakLarder;
				WhattoBuild2 = oakLardernull;
				Number = 8;
				Plank = oakPlank;
				Object = 1;
				Number2 = 16;
			}
			if (TypeofObject.equals("Oak Dungeon Door")) {
				WhattoBuild = oakDungeon;
				WhattoBuild2 = oakDungeonnull;
				Number = 10;
				Plank = oakPlank;
				Object = 2;
				Number2 = 20;
			}
			if (TypeofObject.equals("Mahogany Table")) {
				WhattoBuild = mahTable;
				WhattoBuild2 = mahTablenull;
				Number = 6;
				Plank = mahPlank;
				Object = 3;
				Number2 = 12;
			}
			if (TypeofObject.equals("Teak Carved Magic Wardrobe")) {
				WhattoBuild = teakWard;
				WhattoBuild2 = teakWardnull;
				Number = 6;
				Plank = teakPlank;
				Object = 4;
				Number2 = 12;
			}

			if (slider1.getValue() == 10) {
				mouseSpeed = 1;
			} else if (slider1.getValue() == 9) {
				mouseSpeed = 2;
			} else if (slider1.getValue() == 8) {
				mouseSpeed = 3;
			} else if (slider1.getValue() == 7) {
				mouseSpeed = 4;
			} else if (slider1.getValue() == 6) {
				mouseSpeed = 5;
			} else if (slider1.getValue() == 5) {
				mouseSpeed = 6;
			} else if (slider1.getValue() == 4) {
				mouseSpeed = 7;
			} else if (slider1.getValue() == 3) {
				mouseSpeed = 8;
			} else if (slider1.getValue() == 2) {
				mouseSpeed = 9;
			} else if (slider1.getValue() == 1) {
				mouseSpeed = 10;
			} else if (slider1.getValue() == 0) {
				mouseSpeed = 11;
			}
			start = true;
			guiWait = false;
			frame1.dispose();
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY
			// //GEN-BEGIN:initComponents
			frame1 = new JFrame();
			label1 = new JLabel();
			label2 = new JLabel();
			comboBox1 = new JComboBox();
			label3 = new JLabel();
			label4 = new JLabel();
			comboBox2 = new JComboBox();
			label5 = new JLabel();
			slider1 = new JSlider();
			button1 = new JButton();

			// ======== frame1 ========
			{
				final Container frame1ContentPane = frame1.getContentPane();
				frame1ContentPane.setLayout(null);
				frame1.setVisible(true);
				frame1.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

				// ---- label1 ----
				label1.setText("ProConstruction");
				label1.setFont(new Font("Aerosol", Font.BOLD, 36));
				frame1ContentPane.add(label1);
				label1.setBounds(10, 10, 345, label1.getPreferredSize().height);

				// ---- label2 ----
				label2.setText("By: Himekp");
				label2.setFont(new Font("Aerosol", Font.PLAIN, 30));
				frame1ContentPane.add(label2);
				label2.setBounds(new Rectangle(new Point(115, 45), label2.getPreferredSize()));

				// ---- comboBox1 ----
				frame1ContentPane.add(comboBox1);
				comboBox1.setModel(new DefaultComboBoxModel(new String[]{
						"Normal Butler", "Demon Butler"}));
				comboBox1.setBounds(160, 95, 180, 30);

				// ---- label3 ----
				label3.setText("Type of Butler:");
				label3.setFont(new Font("Tahoma", Font.PLAIN, 22));
				label3.setEnabled(false);
				frame1ContentPane.add(label3);
				label3.setBounds(new Rectangle(new Point(10, 95), label3.getPreferredSize()));

				// ---- label4 ----
				label4.setText("What to Make:");
				label4.setFont(new Font("Tahoma", Font.PLAIN, 22));
				label4.setEnabled(false);
				frame1ContentPane.add(label4);
				label4.setBounds(new Rectangle(new Point(10, 135), label4.getPreferredSize()));

				// ---- comboBox2 ----
				frame1ContentPane.add(comboBox2);
				comboBox2.setModel(new DefaultComboBoxModel(new String[]{
						"Oak Larder", "Teak Carved Magic Wardrobe",
						"Mahogany Table", "Oak Dungeon Door"}));
				comboBox2.setBounds(160, 135, 180, 30);

				// ---- label5 ----
				label5.setText("Mouse Speed");
				label5.setEnabled(false);
				label5.setFont(new Font("Tahoma", Font.PLAIN, 24));
				frame1ContentPane.add(label5);
				label5.setBounds(new Rectangle(new Point(100, 170), label5.getPreferredSize()));

				// ---- slider1 ----
				slider1.setMajorTickSpacing(1);
				slider1.setMaximum(10);
				slider1.setSnapToTicks(true);
				slider1.setPaintTicks(true);
				slider1.setPaintLabels(true);
				frame1ContentPane.add(slider1);
				slider1.setBounds(15, 205, 325, slider1.getPreferredSize().height);

				// ---- button1 ----
				button1.setText("Start");
				button1.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(final ActionEvent e) {
						button1ActionPerformed(e);
					}
				});
				button1.setFont(new Font("Tahoma", Font.PLAIN, 22));
				frame1ContentPane.add(button1);
				button1.setBounds(5, 255, 340, 100);

				{ // compute preferred size
					final Dimension preferredSize = new Dimension();
					for (int i = 0; i < frame1ContentPane.getComponentCount(); i++) {
						final Rectangle bounds = frame1ContentPane.getComponent(i).getBounds();
						preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
						preferredSize.height = Math.max(bounds.y
								+ bounds.height, preferredSize.height);
					}
					final Insets insets = frame1ContentPane.getInsets();
					preferredSize.width += insets.right;
					preferredSize.height += insets.bottom;
					frame1ContentPane.setMinimumSize(preferredSize);
					frame1ContentPane.setPreferredSize(preferredSize);
				}
				frame1.pack();
				frame1.setLocationRelativeTo(frame1.getOwner());
			}
			// JFormDesigner - End of component initialization
			// //GEN-END:initComponents
		}
	}

	public int oakPlank = 8778;
	public int oakDungeon = 13345;
	public int oakDungeonnull = 15327;
	public int oakLarder = 13566;
	public int oakLardernull = 15403;
	public int mahTable = 13298;
	public int mahTablenull = 15298;
	public int mahPlank = 8782;
	public int teakWard = 18790;
	public int teakWardnull = 18811;
	public int teakPlank = 8780;
	private int WhattoBuild;
	private int Butler;
	private int WhattoBuild2;
	private int Plank;
	private int Number;
	private int Number2;
	public int normalButler = 4241;
	public int demonButler = 4243;
	public int Saw = 8794;
	public int Hammer = 2347;
	private String TypeofObject = "";
	private String TypeofServent = "";
	public int Object = 0;
	private int mouseSpeed;
	private boolean startScript;
	boolean start;
	public boolean guiWait = true, guiExit = false;
	ProConstructionGUI gui;
	public int startLevel;
	public int XPgained;
	public int XPhour;
	public int LardersBuilt = 0;
	public int EXPgained = 0;
	public int EXPperhour = 0;
	public int planksperhour = 0;
	public int timetilllevel = 0;
	public int currentLevel = 0;
	public int planksUsed = 0;
	public long startTime = System.currentTimeMillis();
	public long currentXP;
	public long startingXP;
	BufferedImage normal = null;

	BufferedImage clicked = null;

	private final Color color1 = new Color(153, 153, 0, 147);

	private final Color color2 = new Color(0, 0, 0);
	private final Color color3 = new Color(255, 255, 255);
	private final BasicStroke stroke1 = new BasicStroke(1);

	private final Font font1 = new Font("Harlow Solid Italic", 0, 22);

	private final Font font2 = new Font("Harlow Solid Italic", 1, 24);
	private final Font font3 = new Font("Arial", 0, 12);
	private final Image img1 = getImage("http://www.top1gaming.com/images/ItemDBImage/geitemimage_8794.gif");

	private final Image img2 = getImage("http://s3.amazonaws.com/readers/2010/07/31/constructionuntrimmed_1.png");

	// START: Code generated using Enfilade's Easel
	private Image getImage(final String url) {
		try {
			return ImageIO.read(new URL(url));
		} catch (final IOException e) {
			return null;
		}
	}

	public boolean getinterfacesoaklarderbutler() {
		if (getMyPlayer().isMoving()) {
			sleep(10);
		}
		if (Object == 1) {
			if (interfaces.get(394).isValid()) {
				interfaces.getComponent(394, 30).doClick();
				sleep(random(1000, 2000));
				planksUsed += Number;
			}
		} else if (Object == 2) {
			if (interfaces.get(394).isValid()) {
				interfaces.getComponent(394, 29).doClick();
				sleep(random(1000, 2000));
				planksUsed += Number;
			}
		} else if (Object == 3) {
			if (interfaces.get(394).isValid()) {
				interfaces.getComponent(394, 54).doClick();
				sleep(random(1000, 2000));
				planksUsed += Number;
			}
		} else if (Object == 4) {
			if (interfaces.get(394).isValid()) {
				interfaces.getComponent(394, 52).doClick();
				sleep(random(1000, 2000));
				planksUsed += Number;
			}
		}
		// ---- Removing the Object ----
		if (objects.getNearest(WhattoBuild) != null
				&& !interfaces.get(228).isValid()) {
			objects.getNearest(WhattoBuild).doAction("Remove");
			sleep(random(1000, 2000));
		}
		// ---- Remove Interface ----
		if (interfaces.get(228).isValid()) {
			interfaces.getComponent(228, 2).doClick();
			sleep(random(1000, 2000));
		}
		// ---- Paying the Butler ----
		if (interfaces.get(242).isValid()) {
			interfaces.getComponent(242, 6).doClick();
			sleep(random(1000, 2000));
		}
		// ---- Paying the Demon Butler ----
		if (interfaces.get(243).isValid()) {
			interfaces.getComponent(243, 7).doClick();
			sleep(random(1000, 2000));
		}
		if (interfaces.get(234).isValid()) {
			interfaces.getComponent(234, 2).doClick();
			sleep(random(1000, 2000));
		}
		if (interfaces.get(232).isValid()) {
			interfaces.getComponent(232, 3).doClick();
			sleep(random(1000, 2000));
		}
		// ---- Building the Object Choosen ----
		if (objects.getNearest(WhattoBuild) == null
				&& inventory.getCount(Plank) > Number) {
			objects.getNearest(WhattoBuild2).doAction("Build");
			sleep(random(1000, 2000));
		}
		if (inventory.getCount(Plank) < Number2
				&& npcs.getNearest(Butler) == null) {
			sleep(random(100, 150));
		}
		// ---- Telling Butler to Fetch from Bank ----
		if (npcs.getNearest(Butler) != null && !interfaces.get(394).isValid()
				&& inventory.getCount(Plank) < Number2) {
			npcs.getNearest(Butler).doAction("Fetch-from-bank");
			sleep(random(1000, 2000));
		}
		if (TypeofServent.equals("Normal Butler")) {
			if (interfaces.get(232).isValid()) {
				interfaces.getComponent(232, 3).doClick();
				sleep(random(750, 1250));
				keyboard.sendText("20", true);
				sleep(random(200, 400));
			}
		}
		if (TypeofServent.equals("Demon Butler")) {
			if (interfaces.get(232).isValid()) {
				interfaces.getComponent(232, 3).doClick();
				sleep(random(750, 1250));
				keyboard.sendText("26", true);
				sleep(random(200, 400));
			}
		}
		if (TypeofObject.equals("Oak Dungeon Door")) {
			if (objects.getNearest(13351) != null) {
				objects.getNearest(13351).doAction("Close");
			}
		}
		return true;
	}

	@Override
	public int loop() {
		mouse.setSpeed(mouseSpeed);
		getinterfacesoaklarderbutler();
		return 100;
	}

	@Override
	public void onFinish() {
		log("Thanks for using my script, remember to post proggies!");
	}

	@Override
	public void onRepaint(final Graphics g1) {
		final Graphics2D g = (Graphics2D) g1;

		long runTime = 0;
		long seconds = 0;
		long minutes = 0;
		long hours = 0;
		runTime = System.currentTimeMillis() - startTime;
		seconds = runTime / 1000;
		if (seconds >= 60) {
			minutes = seconds / 60;
			seconds -= minutes * 60;
		}

		if (minutes >= 60) {
			hours = minutes / 60;
			minutes -= hours * 60;
		}
		currentXP = skills.getCurrentExp(Skills.getIndex("construction"));
		EXPperhour = (int) ((currentXP - startingXP) * 3600000.0 / runTime);
		EXPgained = (int) (currentXP - startingXP);
		currentLevel = skills.getCurrentLevel(Skills.CONSTRUCTION);
		planksperhour = (int) (planksUsed * 3600000.0 / runTime);
		final int percent = skills.getPercentToNextLevel(Skills.CONSTRUCTION);
		g.drawString("Percent till next level: ", 379, 459);
		g.setColor(Color.red);
		g.fillRoundRect(379, 459, 100, 10, 15, 15);
		g.setColor(Color.green);
		g.fillRoundRect(379, 459, percent, 10, 15, 15);
		g.setColor(Color.black);
		g.drawString("" + percent + "%", 412, 469);
		g.setColor(color1);
		g.fillRoundRect(548, 204, 189, 262, 16, 16);
		g.setColor(color2);
		g.setStroke(stroke1);
		g.drawRoundRect(548, 204, 189, 262, 16, 16);
		g.drawImage(img1, 614, 380, null);
		g.setFont(font1);
		g.setColor(color3);
		g.drawString("By: Himekp", 614, 245);
		g.setFont(font2);
		g.drawString("ProConstruction", 548, 226);
		g.drawImage(img2, 552, 267, null);
		g.setFont(font3);
		g.drawString("XP Gained : " + EXPgained, 629, 300);
		g.drawString("XP / Hour : " + EXPperhour, 629, 321);
		g.drawString("Total Time : " + hours + ":" + minutes + ":" + seconds, 628, 279);
		g.drawString("Planks Used : " + planksUsed, 628, 342);
		g.drawString("Planks / Hour : " + planksperhour, 628, 361);
		g.drawString("Current Level : " + currentLevel, 628, 380);

	}

	// END: Code generated using Enfilade's Easel

	@Override
	public boolean onStart() {
		log(Color.blue, "Welcome to Himekp's ProConstruction Bot");
		log(Color.red, "Check for updates by right clicking my script!");
		log(Color.red, "Then check the version and if it isn't V1.03 then theres an update! Recopy and recompile!");
		log("Loading GUI...Please wait...");
		currentXP = skills.getCurrentExp(Skills.CONSTRUCTION);
		startingXP = skills.getCurrentExp(Skills.CONSTRUCTION);
		try {
			final URL cursorURL = new URL("http://i48.tinypic.com/313623n.png");
			final URL cursor80URL = new URL("http://i46.tinypic.com/9prjnt.png");
			normal = ImageIO.read(cursorURL);
			clicked = ImageIO.read(cursor80URL);
		} catch (final MalformedURLException e) {
			log("Unable to buffer cursor.");
		} catch (final IOException e) {
			log("Unable to open cursor image.");
		}
		gui = new ProConstructionGUI();
		gui.setVisible(true);
		log("The higher the Mouse Speed number is, the faster your mouse will be! Speed 6 is recomended!");
		startScript = true;
		while (!startScript) {
			sleep(10);
		}
		return true;
	}
}
