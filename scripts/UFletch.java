import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Environment;
import org.rsbot.script.methods.Game.Tab;
import org.rsbot.script.methods.GrandExchange.GEItem;
import org.rsbot.script.methods.Skills;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSItem;

@ScriptManifest(authors = { "Fletch To 99" }, keywords = "Fletching", name = "UFletch", website = "http://www.universalscripts.org/", version = 2.28, description = "The best fletcher!", requiresVersion = 245)
/**
 * All-in-One Fletching script for RSBot 2.XX
 * @author Fletch To 99
 */
public class UFletch extends Script implements PaintListener, MouseListener,
		MouseMotionListener, MessageListener {

	public class beeper implements Runnable {
		private String firstMessage = "";

		public void beep() {
			try {
				for (int i = 0; i < 3; i++) {
					java.awt.Toolkit.getDefaultToolkit().beep();
					Thread.sleep(250);
				}
				Thread.sleep(random(100, 500));
			} catch (final Exception e) {
			}
			return;
		}

		public String m() {
			final RSInterface chatBox = interfaces.get(137);
			for (int i = 281; i >= 180; i--) {// Valid text is from 180 to 281
				final String text = chatBox.getComponent(i).getText();
				if (!text.isEmpty() && text.contains("<")) {
					return text;
				}
			}
			return "";
		}

		@Override
		public void run() {
			while (!b.isInterrupted()) {
				text();
				try {
					Thread.sleep(random(50, 150));
				} catch (final InterruptedException e) {
				}
			}
		}

		public void text() {
			if (!m().toLowerCase().isEmpty()
					&& !m().toLowerCase().equals(firstMessage)) {
				beep();
				firstMessage = m().toLowerCase();
			}
		}
	}

	private static interface constants {
		final String[] optionMethod = { "Fletching Bow", "Stringing Bow",
				"Fletch then String", "Add Stocks to limbs",
				"Stringing Crossbow", "Chop, Fletch, Drop or Shaft", "Arrows",
				"Bolts", "Darts" };
		final String[] optionLog = { "Normal", "Oak", "Willow", "Maple", "Yew",
				"Magic", "N/A" };
		final String[] optionBow = { "Short", "Long", "Shafts", "Stocks",
				"C'Bow (u)", "N/A" };
		final String[] optionKnife = { "Normal", "clay", "N/A" };
		final String[] optionAxe = { "Bronze", "Iron", "Steel", "Mith",
				"Adamant", "Rune", "Dragon", "N/A" };
		final String[] optionColor = { "Black", "Red", "Orange", "Blue",
				"Green", "Yellow", "Pink", "White", "Tan" };
		final Color TAN = new Color(220, 202, 169);
		final int BOW_STRING_ID = 1777, CBOW_STRING_ID = 943, FEATHER_ID = 314,
				FEATHER_SHAFT_ID = 53, SHAFT_ID = 52, xpIsClose = 13020000;
		final MenuItem item1 = new MenuItem("Stop");
		final MenuItem item2 = new MenuItem("Pause");
		final MenuItem item3 = new MenuItem("Resume");
		final MenuItem item4 = new MenuItem("Open Gui");
		final MenuItem item5 = new MenuItem("Help");
		final Font titleFont = new Font("Allerta", Font.PLAIN, 25);
		final Font textFont = new Font("Allerta", Font.PLAIN, 11);
		final Rectangle BUTTON = new Rectangle(450, 316, 65, 21);
		final Rectangle RECT_ONE = new Rectangle(5, 4, 129, 36);
		final Rectangle RECT_TWO = new Rectangle(136, 4, 129, 21);
		final Rectangle RECT_THREE = new Rectangle(266, 4, 129, 21);
		final Rectangle HIDE = new Rectangle(5, 316, 65, 21);
		final Rectangle PAUSE = new Rectangle(449, 292, 66, 22);
		final Rectangle STOP = new Rectangle(4, 292, 66, 22);
	}

	public class gui extends JFrame {
		private static final long serialVersionUID = 1L;

		// JFormDesigner - Variables declaration - DO NOT MODIFY
		// //GEN-BEGIN:variables
		private JTabbedPane tabbedPane1;

		private JPanel panel4;

		private JLabel label24;

		private JLabel label25;

		private JLabel label2;

		private JComboBox comboBox2;

		private JLabel label3;

		private JComboBox comboBox3;

		private JLabel label4;
		private JLabel label5;
		private JComboBox comboBox4;
		private JComboBox comboBox5;
		private JLabel label6;
		private JLabel label7;
		private JComboBox comboBox1;
		private JLabel label8;
		private JLabel label9;
		private JLabel label10;
		private JLabel label11;
		private JLabel label14;
		private JLabel label12;
		private JTextField textField1;
		private JLabel label13;
		private JPanel panel2;
		private JLabel label31;
		private JLabel label35;
		private JLabel label36;
		private JLabel label37;
		private JLabel label38;
		private JButton button3;
		private JLabel label39;
		private JLabel label40;
		private JCheckBox checkBox4;
		private JCheckBox checkBox8;
		private JCheckBox checkBox9;
		private JCheckBox checkBox10;
		private JCheckBox checkBox11;
		private JCheckBox checkBox12;
		private JCheckBox checkBox13;
		private JCheckBox checkBox14;
		private JLabel label21;
		private JComboBox comboBox8;
		private JComboBox comboBox9;
		private JLabel label43;
		private JLabel label44;
		private JLabel label45;
		private JLabel label46;
		private JComboBox comboBox10;
		private JComboBox comboBox11;
		private JComboBox comboBox12;
		private JComboBox comboBox13;
		private JComboBox comboBox14;
		private JComboBox comboBox15;
		private JLabel label47;
		private JLabel label64;
		private JCheckBox checkBox17;
		private JLabel label65;
		private JCheckBox checkBox18;
		private JPanel panel1;
		private JLabel label17;
		private JTextField textField2;
		private JLabel label18;
		private JLabel label1;
		private JButton button4;
		private JPanel panel3;
		private JSlider slider1;
		private JLabel label15;
		private JLabel label16;
		private JCheckBox checkBox6;
		private JLabel label23;
		private JCheckBox checkBox5;
		private JLabel label27;
		private JCheckBox checkBox1;
		private JCheckBox checkBox2;
		private JLabel label28;
		private JCheckBox checkBox3;
		private JLabel label29;
		private JLabel label30;
		private JLabel label48;
		private JCheckBox checkBox15;
		private JLabel label49;
		private JCheckBox checkBox16;
		private JLabel label19;
		private JCheckBox checkBox7;
		private JProgressBar progressBar1;
		private JLabel label20;
		private JButton button2;
		private JButton button1;

		// JFormDesigner - End of variables declaration //GEN-END:variables
		public gui() {
			initComponents();
		}

		private void button1ActionPerformed(final ActionEvent e) {
			setVisible(false);
			log("Task: "
					+ constants.optionLog[gui.comboBox2.getSelectedIndex()]
					+ " "
					+ constants.optionBow[gui.comboBox3.getSelectedIndex()]
					+ " "
					+ constants.optionMethod[gui.comboBox1.getSelectedIndex()]);
			Mouse1 = (int) slider1.getValue();
			if (Mouse1 == 100) {
				Mouse2 = random(1, 2);
			} else if (Mouse1 == 90) {
				Mouse2 = random(2, 4);
			} else if (Mouse1 == 80) {
				Mouse2 = random(3, 5);
			} else if (Mouse1 == 70) {
				Mouse2 = random(4, 6);
			} else if (Mouse1 == 60) {
				Mouse2 = random(5, 7);
			} else if (Mouse1 == 50) {
				Mouse2 = random(6, 8);
			} else if (Mouse1 == 40) {
				Mouse2 = random(7, 9);
			} else if (Mouse1 == 30) {
				Mouse2 = random(8, 10);
			} else if (Mouse1 == 20) {
				Mouse2 = random(10, 12);
			} else if (Mouse1 == 10) {
				Mouse2 = random(12, 14);
			}
			mouse.setSpeed(Mouse2);
			button4.setEnabled(false);
			textField2.setEnabled(false);
			saveSettings();
		}

		private void button2ActionPerformed(final ActionEvent e) {
			try {
				Desktop.getDesktop().browse(new URL("http://www.universalscripts.org/ufletch/highscores.php").toURI());
			} catch (final MalformedURLException e1) {
			} catch (final IOException e1) {
			} catch (final URISyntaxException e1) {
			}
		}

		private void button3ActionPerformed(final ActionEvent e) {
			try {
				Desktop.getDesktop().browse(new URL("http://www.universalscripts.org/ufletch/ufletch").toURI());
			} catch (final MalformedURLException e1) {
			} catch (final IOException e1) {
			} catch (final URISyntaxException e1) {
			}
		}

		private void button4ActionPerformed(final ActionEvent e) {
			createSignature();
			name = textField2.getText();
			siggy = getImage("", false, "http://www.universalscripts.org/ufletch/UFletch_generate.php?user="
					+ name);
			label1 = new JLabel(new ImageIcon(siggy));
		}

		private String getMessage() {
			URLConnection url = null;
			BufferedReader in = null;
			if (connection) {
				try {
					url = new URL("http://www.universalscripts.org/ufletch/Images/message.txt").openConnection();
					in = new BufferedReader(new InputStreamReader(url.getInputStream()));
					return in.readLine();
				} catch (final MalformedURLException e) {
				} catch (final IOException e) {
				}
			}
			return "Error getting message.";
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY
			// //GEN-BEGIN:initComponents
			tabbedPane1 = new JTabbedPane();
			panel4 = new JPanel();
			label24 = new JLabel();
			label25 = new JLabel();
			label2 = new JLabel(new ImageIcon(logsImage));
			comboBox2 = new JComboBox(constants.optionLog);
			label3 = new JLabel(new ImageIcon(bow));
			comboBox3 = new JComboBox(constants.optionBow);
			label4 = new JLabel(new ImageIcon(knife));
			label5 = new JLabel(new ImageIcon(axe));
			comboBox4 = new JComboBox(constants.optionKnife);
			comboBox5 = new JComboBox(constants.optionAxe);
			label6 = new JLabel(new ImageIcon(settings));
			label7 = new JLabel(new ImageIcon(settings));
			comboBox1 = new JComboBox(constants.optionMethod);
			label8 = new JLabel();
			label9 = new JLabel();
			label10 = new JLabel();
			label11 = new JLabel();
			label14 = new JLabel();
			label12 = new JLabel();
			textField1 = new JTextField();
			label13 = new JLabel();
			panel2 = new JPanel();
			label31 = new JLabel();
			label35 = new JLabel();
			label36 = new JLabel();
			label37 = new JLabel();
			label38 = new JLabel();
			label21 = new JLabel();
			button3 = new JButton();
			label39 = new JLabel();
			label40 = new JLabel();
			checkBox4 = new JCheckBox();
			checkBox8 = new JCheckBox();
			checkBox9 = new JCheckBox();
			checkBox10 = new JCheckBox();
			checkBox11 = new JCheckBox();
			checkBox12 = new JCheckBox();
			checkBox13 = new JCheckBox();
			checkBox14 = new JCheckBox();
			comboBox8 = new JComboBox(constants.optionColor);
			comboBox9 = new JComboBox(constants.optionColor);
			label43 = new JLabel();
			label44 = new JLabel();
			label45 = new JLabel();
			label46 = new JLabel();
			comboBox10 = new JComboBox(constants.optionColor);
			comboBox11 = new JComboBox(constants.optionColor);
			comboBox12 = new JComboBox(constants.optionColor);
			comboBox13 = new JComboBox(constants.optionColor);
			comboBox14 = new JComboBox(constants.optionColor);
			comboBox15 = new JComboBox(constants.optionColor);
			label47 = new JLabel(new ImageIcon(brush));
			label64 = new JLabel();
			checkBox17 = new JCheckBox();
			label65 = new JLabel();
			checkBox18 = new JCheckBox();
			panel1 = new JPanel();
			label17 = new JLabel();
			textField2 = new JTextField();
			label18 = new JLabel();
			label1 = new JLabel(new ImageIcon(siggy));
			button4 = new JButton();
			panel3 = new JPanel();
			slider1 = new JSlider();
			label15 = new JLabel();
			label16 = new JLabel();
			checkBox6 = new JCheckBox();
			label23 = new JLabel();
			checkBox5 = new JCheckBox();
			label27 = new JLabel();
			checkBox1 = new JCheckBox();
			checkBox2 = new JCheckBox();
			label28 = new JLabel(new ImageIcon(pic));
			checkBox3 = new JCheckBox();
			label29 = new JLabel();
			label30 = new JLabel();
			label48 = new JLabel();
			checkBox15 = new JCheckBox();
			label49 = new JLabel();
			checkBox16 = new JCheckBox();
			label19 = new JLabel();
			checkBox7 = new JCheckBox();
			progressBar1 = new JProgressBar();
			label20 = new JLabel();
			button2 = new JButton();
			button1 = new JButton();

			// ======== this ========
			final Container contentPane = getContentPane();
			contentPane.setLayout(null);

			// ======== tabbedPane1 ========
			{
				tabbedPane1.setTabPlacement(SwingConstants.LEFT);

				// ======== panel4 ========
				{
					panel4.setLayout(null);

					// ---- label24 ----
					label24.setText("Message:");
					label24.setFont(new Font("Tahoma", Font.PLAIN, 17));
					label24.setForeground(Color.red);
					panel4.add(label24);
					label24.setBounds(0, 265, 75, 25);

					// ---- label25 ----
					label25.setText(getMessage());
					label25.setFont(new Font("Tahoma", Font.PLAIN, 17));
					label25.setForeground(Color.blue);
					panel4.add(label25);
					label25.setBounds(80, 265, 340, 25);

					// ---- label2 ----
					panel4.add(label2);
					label2.setBounds(new Rectangle(new Point(5, 35), label2.getPreferredSize()));

					// ---- comboBox2 ----
					comboBox2.setForeground(Color.cyan);
					panel4.add(comboBox2);
					comboBox2.setBounds(50, 35, 160, 30);

					// ---- label3 ----
					panel4.add(label3);
					label3.setBounds(new Rectangle(new Point(5, 110), label3.getPreferredSize()));

					// ---- comboBox3 ----
					comboBox3.setForeground(Color.blue);
					panel4.add(comboBox3);
					comboBox3.setBounds(50, 110, 160, 30);

					// ---- label4 ----
					panel4.add(label4);
					label4.setBounds(220, 110, 25, label4.getPreferredSize().height);

					// ---- label5 ----
					panel4.add(label5);
					label5.setBounds(220, 35, label5.getPreferredSize().width, 30);

					// ---- comboBox4 ----
					comboBox4.setForeground(Color.green);
					panel4.add(comboBox4);
					comboBox4.setBounds(255, 110, 170, 30);

					// ---- comboBox5 ----
					comboBox5.setForeground(Color.red);
					panel4.add(comboBox5);
					comboBox5.setBounds(255, 35, 170, 30);

					// ---- label6 ----
					panel4.add(label6);
					label6.setBounds(new Rectangle(new Point(5, 180), label6.getPreferredSize()));

					// ---- label7 ----
					panel4.add(label7);
					label7.setBounds(375, 180, 47, 50);

					// ---- comboBox1 ----
					comboBox1.setForeground(Color.magenta);
					panel4.add(comboBox1);
					comboBox1.setBounds(60, 180, 310, 50);

					// ---- label8 ----
					label8.setText("Method To Perform");
					label8.setFont(label8.getFont().deriveFont(Font.PLAIN, label8.getFont().getSize() + 17f));
					panel4.add(label8);
					label8.setBounds(95, 145, 240, 40);

					// ---- label9 ----
					label9.setText("Bow Type");
					label9.setFont(label9.getFont().deriveFont(label9.getFont().getSize() + 17f));
					panel4.add(label9);
					label9.setBounds(65, 75, 125, 35);

					// ---- label10 ----
					label10.setText("Wood Type");
					label10.setFont(label10.getFont().deriveFont(label10.getFont().getSize() + 17f));
					panel4.add(label10);
					label10.setBounds(new Rectangle(new Point(60, 0), label10.getPreferredSize()));

					// ---- label11 ----
					label11.setText("Object Type");
					label11.setFont(label11.getFont().deriveFont(label11.getFont().getSize() + 17f));
					panel4.add(label11);
					label11.setBounds(new Rectangle(new Point(265, 0), label11.getPreferredSize()));

					// ---- label14 ----
					label14.setText("Knife Type");
					label14.setFont(label14.getFont().deriveFont(label14.getFont().getSize() + 17f));
					panel4.add(label14);
					label14.setBounds(new Rectangle(new Point(275, 75), label14.getPreferredSize()));

					// ---- label12 ----
					label12.setText("Amount to Fletch:");
					label12.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel4.add(label12);
					label12.setBounds(0, 235, 135, 25);

					// ---- textField1 ----
					textField1.setText("0");
					panel4.add(textField1);
					textField1.setBounds(135, 235, 90, 25);

					// ---- label13 ----
					label13.setText("0 for Unilimted Fletching!");
					label13.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel4.add(label13);
					label13.setBounds(230, 235, 190, 25);
				}
				tabbedPane1.addTab("Main Settings", panel4);
				// ======== panel2 ========
				{
					panel2.setLayout(null);

					// ---- label31 ----
					label31.setText("Enable Paint:");
					label31.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel2.add(label31);
					label31.setBounds(5, 55, label31.getPreferredSize().width, 25);

					// ---- label35 ----
					label35.setText("RSBot Mouse Lines:");
					label35.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel2.add(label35);
					label35.setBounds(new Rectangle(new Point(5, 125), label35.getPreferredSize()));

					// ---- label36 ----
					label36.setText("RSBot Mouse Crosshair:");
					label36.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel2.add(label36);
					label36.setBounds(new Rectangle(new Point(5, 145), label36.getPreferredSize()));

					// ---- label37 ----
					label37.setText("Your Mouse Lines:");
					label37.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel2.add(label37);
					label37.setBounds(new Rectangle(new Point(5, 165), label37.getPreferredSize()));

					// ---- label38 ----
					label38.setText("Your Mouse Crosshair:");
					label38.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel2.add(label38);
					label38.setBounds(5, 185, 165, label38.getPreferredSize().height);

					// ---- button3 ----
					button3.setText("Visit Universalscripts.org! :D");
					button3.setFont(button3.getFont().deriveFont(button3.getFont().getSize() + 19f));
					button3.setForeground(Color.red);
					button3.setBackground(new Color(255, 0, 51));
					button3.addActionListener(new ActionListener() {
						public void actionPerformed(final ActionEvent e) {
							button3ActionPerformed(e);
						}
					});
					panel2.add(button3);
					button3.setBounds(0, 210, 425, 80);

					// ---- label39 ----
					label39.setText("Text Color:");
					label39.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel2.add(label39);
					label39.setBounds(new Rectangle(new Point(5, 80), label39.getPreferredSize()));

					// ---- label40 ----
					label40.setText("Paint Main Color:");
					label40.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel2.add(label40);
					label40.setBounds(new Rectangle(new Point(5, 105), label40.getPreferredSize()));

					// ---- checkBox4 ----
					checkBox4.setFont(new Font("Tahoma", Font.PLAIN, 16));
					checkBox4.setSelected(true);
					panel2.add(checkBox4);
					checkBox4.setBounds(95, 55, checkBox4.getPreferredSize().width, 25);

					// ---- checkBox11 ----
					checkBox11.setFont(new Font("Tahoma", Font.PLAIN, 16));
					checkBox11.setSelected(true);
					panel2.add(checkBox11);
					checkBox11.setBounds(new Rectangle(new Point(145, 125), checkBox11.getPreferredSize()));

					// ---- checkBox12 ----
					checkBox12.setFont(new Font("Tahoma", Font.PLAIN, 16));
					checkBox12.setSelected(true);
					panel2.add(checkBox12);
					checkBox12.setBounds(new Rectangle(new Point(175, 145), checkBox12.getPreferredSize()));

					// ---- checkBox13 ----
					checkBox13.setFont(new Font("Tahoma", Font.PLAIN, 16));
					checkBox13.setSelected(true);
					panel2.add(checkBox13);
					checkBox13.setBounds(new Rectangle(new Point(135, 165), checkBox13.getPreferredSize()));

					// ---- checkBox14 ----
					checkBox14.setFont(new Font("Tahoma", Font.PLAIN, 16));
					checkBox14.setSelected(true);
					panel2.add(checkBox14);
					checkBox14.setBounds(new Rectangle(new Point(165, 185), checkBox14.getPreferredSize()));

					// ---- label43 ----
					label43.setText("Color:");
					label43.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel2.add(label43);
					label43.setBounds(new Rectangle(new Point(240, 125), label43.getPreferredSize()));

					// ---- label44 ----
					label44.setText("Color:");
					label44.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel2.add(label44);
					label44.setBounds(new Rectangle(new Point(195, 145), label44.getPreferredSize()));

					// ---- label45 ----
					label45.setText("Color:");
					label45.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel2.add(label45);
					label45.setBounds(235, 165, 45, label45.getPreferredSize().height);

					// ---- label46 ----
					label46.setText("Color:");
					label46.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel2.add(label46);
					label46.setBounds(new Rectangle(new Point(185, 185), label46.getPreferredSize()));
					panel2.add(comboBox10);
					comboBox10.setBounds(285, 125, 70, comboBox10.getPreferredSize().height);
					panel2.add(comboBox11);
					comboBox11.setBounds(240, 145, 70, comboBox11.getPreferredSize().height);
					panel2.add(comboBox12);
					comboBox12.setBounds(95, 80, 70, comboBox12.getPreferredSize().height);
					panel2.add(comboBox13);
					comboBox13.setBounds(130, 105, 70, comboBox13.getPreferredSize().height);
					panel2.add(comboBox14);
					comboBox14.setBounds(280, 165, 70, comboBox14.getPreferredSize().height);
					panel2.add(comboBox15);
					comboBox15.setBounds(230, 185, 65, comboBox15.getPreferredSize().height);

					// ---- label47 ----
					panel2.add(label47);
					label47.setBounds(250, 5, 135, 115);

					// ---- label64 ----
					label64.setText("Circles:");
					label64.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel2.add(label64);
					label64.setBounds(new Rectangle(new Point(165, 125), label64.getPreferredSize()));

					// ---- checkBox17 ----
					checkBox17.setFont(new Font("Tahoma", Font.PLAIN, 16));
					checkBox17.setSelected(true);
					panel2.add(checkBox17);
					checkBox17.setBounds(new Rectangle(new Point(220, 125), checkBox17.getPreferredSize()));

					// ---- label65 ----
					label65.setText("Cricles:");
					label65.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel2.add(label65);
					label65.setBounds(new Rectangle(new Point(155, 165), label65.getPreferredSize()));

					// ---- checkBox18 ----
					checkBox18.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel2.add(checkBox18);
					checkBox18.setBounds(new Rectangle(new Point(210, 165), checkBox18.getPreferredSize()));

					// ---- label21 ----
					label21.setText("Paint Settings");
					label21.setFont(label21.getFont().deriveFont(label21.getFont().getSize() + 30f));
					label21.setForeground(Color.blue);
					panel2.add(label21);
					label21.setBounds(new Rectangle(new Point(0, 5), label21.getPreferredSize()));
				}
				tabbedPane1.addTab("Paint Settings", panel2);

				// ======== panel1 ========
				{
					panel1.setLayout(null);

					// ---- label17 ----
					label17.setText("Signature name:");
					label17.setFont(label17.getFont().deriveFont(label17.getFont().getStyle()
							& ~Font.BOLD));
					panel1.add(label17);
					label17.setBounds(0, 5, 80, 25);

					// ---- textField2 ----
					textField2.setText("All");
					panel1.add(textField2);
					textField2.setBounds(80, 5, 95, 25);

					// ---- label18 ----
					label18.setText("No Spaces Please!");
					panel1.add(label18);
					label18.setBounds(180, 5, 88, 25);

					// ---- label1 ----
					label1.addMouseListener(new MouseAdapter() {
						@Override
						public void mouseClicked(final MouseEvent e) {
							label1MouseClicked(e);
						}
					});
					panel1.add(label1);
					label1.setBounds(0, 25, 500, 275);

					// ---- button4 ----
					button4.setText("Generate Signature");
					button4.setForeground(new Color(0, 204, 0));
					button4.setBackground(Color.green);
					button4.addActionListener(new ActionListener() {
						public void actionPerformed(final ActionEvent e) {
							button4ActionPerformed(e);
						}
					});
					panel1.add(button4);
					button4.setBounds(275, 5, 145, 25);
				}
				tabbedPane1.addTab("Signature", panel1);

				// ======== panel3 ========
				{
					panel3.setLayout(null);

					// ---- slider1 ----
					slider1.setSnapToTicks(true);
					slider1.setPaintTicks(true);
					slider1.setMajorTickSpacing(10);
					panel3.add(slider1);
					slider1.setBounds(105, 130, 315, 30);

					// ---- label15 ----
					label15.setText("Mousespeed:");
					label15.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel3.add(label15);
					label15.setBounds(5, 130, 100, 30);

					// ---- label16 ----
					label16.setText("Save Settings for this account:");
					label16.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel3.add(label16);
					label16.setBounds(0, 195, 215, 25);

					// ---- checkBox6 ----
					checkBox6.setSelected(true);
					checkBox6.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel3.add(checkBox6);
					checkBox6.setBounds(215, 200, 21, 16);

					// ---- label23 ----
					label23.setText("logout 20k experince before 99 fletching:");
					label23.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel3.add(label23);
					label23.setBounds(5, 85, 300, 20);

					// ---- checkBox5 ----
					checkBox5.setSelected(true);
					panel3.add(checkBox5);
					checkBox5.setBounds(300, 85, 21, 21);

					// ---- label27 ----
					label27.setText("when done:");
					label27.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel3.add(label27);
					label27.setBounds(35, 165, 90, 25);

					// ---- checkBox1 ----
					checkBox1.setText(" Upon Level:");
					checkBox1.setSelected(true);
					checkBox1.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel3.add(checkBox1);
					checkBox1.setBounds(125, 165, 115, 25);

					// ---- checkBox2 ----
					checkBox2.setSelected(true);
					checkBox2.setFont(new Font("Tahoma", Font.PLAIN, 16));
					checkBox2.setText("Getting 99:");
					panel3.add(checkBox2);
					checkBox2.setBounds(240, 165, 110, 25);

					// ---- label28 ----
					panel3.add(label28);
					label28.setBounds(new Rectangle(new Point(0, 165), label28.getPreferredSize()));

					// ---- checkBox3 ----
					checkBox3.setFont(new Font("Tahoma", Font.PLAIN, 16));
					checkBox3.setSelected(true);
					panel3.add(checkBox3);
					checkBox3.setBounds(350, 165, checkBox3.getPreferredSize().width, 26);

					// ---- label29 ----
					label29.setText("Developed by: Fletch to 99");
					label29.setForeground(Color.blue);
					label29.setFont(label29.getFont().deriveFont(Font.BOLD, label29.getFont().getSize() + 20f));
					panel3.add(label29);
					label29.setBounds(5, 35, 415, 50);

					// ---- label30 ----
					label30.setText("UFletch: FREE, AIO, FLAWLESS!");
					label30.setForeground(Color.green);
					label30.setFont(label30.getFont().deriveFont(label30.getFont().getStyle()
							| Font.BOLD, label30.getFont().getSize() + 15f));
					panel3.add(label30);
					label30.setBounds(new Rectangle(new Point(5, 5), label30.getPreferredSize()));

					// ---- label48 ----
					label48.setText("Message Notification:");
					label48.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel3.add(label48);
					label48.setBounds(new Rectangle(new Point(5, 105), label48.getPreferredSize()));

					// ---- checkBox15 ----
					checkBox15.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel3.add(checkBox15);
					checkBox15.setBounds(new Rectangle(new Point(160, 105), checkBox15.getPreferredSize()));

					// ---- label49 ----
					label49.setText("Message Beep:");
					label49.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel3.add(label49);
					label49.setBounds(new Rectangle(new Point(185, 105), label49.getPreferredSize()));

					// ---- checkBox16 ----
					checkBox16.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel3.add(checkBox16);
					checkBox16.setBounds(new Rectangle(new Point(290, 105), checkBox16.getPreferredSize()));

					// ---- label19 ----
					label19.setText("Load Settings:");
					label19.setFont(new Font("Tahoma", Font.PLAIN, 16));
					panel3.add(label19);
					label19.setBounds(245, 195, label19.getPreferredSize().width, 25);

					// ---- checkBox7 ----
					checkBox7.setFont(new Font("Tahoma", Font.PLAIN, 16));
					checkBox7.setSelected(true);
					panel3.add(checkBox7);
					checkBox7.setBounds(350, 200, checkBox7.getPreferredSize().width, 15);

					// ---- progressBar1 ----
					progressBar1.setStringPainted(true);
					panel3.add(progressBar1);
					progressBar1.setBounds(0, 240, 210, 50);

					// ---- label20 ----
					label20.setText("Percentage to next level");
					label20.setFont(new Font("Tahoma", Font.PLAIN, 16));
					label20.setForeground(Color.red);
					panel3.add(label20);
					label20.setBounds(new Rectangle(new Point(20, 220), label20.getPreferredSize()));

					// ---- button2 ----
					button2.setText("Highscores");
					button2.setFont(new Font("Tahoma", Font.PLAIN, 16));
					button2.setForeground(Color.red);
					button2.setBackground(new Color(255, 0, 51));
					panel3.add(button2);
					button2.setBounds(210, 225, 215, 65);
					button2.addActionListener(new ActionListener() {
						public void actionPerformed(final ActionEvent e) {
							button2ActionPerformed(e);
						}
					});

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
				tabbedPane1.addTab("Other Settings", panel3);

			}
			contentPane.add(tabbedPane1);
			tabbedPane1.setBounds(5, 0, 515, 295);

			// ---- button1 ----
			button1.setText("Start UFletch!");
			button1.setForeground(Color.blue);
			button1.setFont(new Font("Tahoma", Font.PLAIN, 26));
			button1.setBackground(new Color(51, 51, 255));
			button1.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					button1ActionPerformed(e);
				}
			});
			contentPane.add(button1);
			button1.setBounds(0, 295, 520, 45);

			comboBox12.setSelectedIndex(0);
			comboBox13.setSelectedIndex(8);
			comboBox8.setSelectedIndex(1);
			comboBox9.setSelectedIndex(3);
			comboBox10.setSelectedIndex(7);
			comboBox11.setSelectedIndex(6);
			comboBox14.setSelectedIndex(5);
			comboBox15.setSelectedIndex(2);

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
			setVisible(true);
			// JFormDesigner - End of component initialization
			// //GEN-END:initComponents
		}

		private void label1MouseClicked(final MouseEvent e) {
			try {
				Desktop.getDesktop().browse(new URL("http://www.universalscripts.org/ufletch/UFletch_generate.php?user="
						+ gui.textField2.getText()).toURI());
			} catch (final MalformedURLException e1) {
			} catch (final IOException e1) {
			} catch (final URISyntaxException e1) {
			}
		}

	}

	private class MouseCirclePathPoint extends Point {
		private static final long serialVersionUID = 1L;

		private final long finishTime;

		private final double lastingTime;

		public MouseCirclePathPoint(final int x, final int y,
				final int lastingTime) {
			super(x, y);
			this.lastingTime = lastingTime;
			finishTime = System.currentTimeMillis() + lastingTime;
		}

		public boolean isUp() {
			return System.currentTimeMillis() > finishTime;
		}

		private int toColor(final double d) {
			return Math.min(255, Math.max(0, (int) d));
		}

		public double toTime(final double d) {
			return d * (finishTime - System.currentTimeMillis()) / lastingTime;
		}
	}

	private class MouseCirclePathPoint2 extends Point {
		private static final long serialVersionUID = 1L;

		private final long finishTime;

		private final double lastingTime;

		public MouseCirclePathPoint2(final int x, final int y,
				final int lastingTime) {
			super(x, y);
			this.lastingTime = lastingTime;
			finishTime = System.currentTimeMillis() + lastingTime;
		}

		public boolean isUp() {
			return System.currentTimeMillis() > finishTime;
		}

		private int toColor(final double d) {
			return Math.min(255, Math.max(0, (int) d));
		}

		public double toTime(final double d) {
			return d * (finishTime - System.currentTimeMillis()) / lastingTime;
		}
	}

	private class MousePathPoint extends Point {
		private static final long serialVersionUID = 1L;

		private final long finishTime;

		private final double lastingTime;

		public MousePathPoint(final int x, final int y, final int lastingTime) {
			super(x, y);
			this.lastingTime = lastingTime;
			finishTime = System.currentTimeMillis() + lastingTime;
		}

		public boolean isUp() {
			return System.currentTimeMillis() > finishTime;
		}

		private final int toColor(final double d) {
			return Math.min(255, Math.max(0, (int) d));
		}

		public double toTime(final double d) {
			return d * (finishTime - System.currentTimeMillis()) / lastingTime;
		}
	}

	private class MousePathPoint2 extends Point {
		private static final long serialVersionUID = 1L;

		private final long finishTime;

		private final double lastingTime;

		public MousePathPoint2(final int x, final int y, final int lastingTime) {
			super(x, y);
			this.lastingTime = lastingTime;
			finishTime = System.currentTimeMillis() + lastingTime;
		}

		public boolean isUp() {
			return System.currentTimeMillis() > finishTime;
		}

		private final int toColor(final double d) {
			return Math.min(255, Math.max(0, (int) d));
		}

		public double toTime(final double d) {
			return d * (finishTime - System.currentTimeMillis()) / lastingTime;
		}
	}

	public class trayInfo extends MenuItem {
		private static final long serialVersionUID = 1L;
		private final PopupMenu menu = new PopupMenu();
		public TrayIcon systray;

		public trayInfo() {
			initComponents();
		}

		private void initComponents() {
			if (!SystemTray.isSupported()) {
				JOptionPane.showMessageDialog(null, "SystemTray not supported");
			} else {
				menu.add(constants.item1);
				constants.item1.addActionListener(new ActionListener() {
					public void actionPerformed(final ActionEvent e) {
						item1ActionPerformed(e);
					}
				});
				menu.add(constants.item2);
				constants.item2.addActionListener(new ActionListener() {
					public void actionPerformed(final ActionEvent e) {
						item2ActionPerformed(e);
					}
				});
				menu.add(constants.item3);
				constants.item3.addActionListener(new ActionListener() {
					public void actionPerformed(final ActionEvent e) {
						item3ActionPerformed(e);
					}
				});
				menu.add(constants.item4);
				constants.item4.addActionListener(new ActionListener() {
					public void actionPerformed(final ActionEvent e) {
						item4ActionPerformed(e);
					}
				});
				menu.add(constants.item5);
				constants.item5.addActionListener(new ActionListener() {
					public void actionPerformed(final ActionEvent e) {
						item5ActionPerformed(e);
					}
				});
				try {
					systray = new TrayIcon(icon.getScaledInstance(SystemTray.getSystemTray().getTrayIconSize().width, SystemTray.getSystemTray().getTrayIconSize().height, 0), "UFletch", menu);
					SystemTray.getSystemTray().add(systray);
					tray = true;
				} catch (final Exception e) {
					log("Error setting up system tray!");
					e.printStackTrace();
					tray = false;
				}
			}
		}

		private void item1ActionPerformed(final ActionEvent e) {
			stopScript(false);
		}

		private void item2ActionPerformed(final ActionEvent e) {
			pause = true;
			paused = "Resume";
		}

		private void item3ActionPerformed(final ActionEvent e) {
			pause = false;
			paused = "Pause";
			env.setUserInput(Environment.INPUT_KEYBOARD);
			log("Resuming..");
		}

		private void item4ActionPerformed(final ActionEvent e) {
			gui.button1.setText("Update");
			gui.setVisible(true);
		}

		private void item5ActionPerformed(final ActionEvent e) {
			gui.tabbedPane1.setSelectedIndex(4);
			gui.button1.setText("Update");
			gui.setVisible(true);
		}
	}

	private int amount = 0, startXP = 0, startLevel = 0, fletched = 0,
			strung = 0, currentexp = 0, Mouse1 = 50, Mouse2 = 8, xpGained = 0,
			xpToLevel = 0, daysTNL = 0, hoursTNL = 0, minsTNL = 0, secTNL = 0,
			fail = 0, full = 0;
	private final long startTime = System.currentTimeMillis();
	private long ct = System.currentTimeMillis();
	private boolean has99 = false, tray = false, pause = false,
			fletchAndString = false, fullPaint = false, tabOne = false,
			tabTwo = false, tabThree = false, connection = true;

	private String status = null, name = "All", buttonOption = "Unhide",
			paused = "Pause";
	private Point p = new Point();
	private Image icon = null, fletchIcon = null, logsImage = null,
			settings = null, bow = null, axe = null, knife = null,
			brush = null, siggy = null, pic = null;
	private gui gui = null;

	private trayInfo trayInfo = null;

	private beeper beep = null;

	private Thread b = null;

	private final LinkedList<MousePathPoint> mousePath = new LinkedList<MousePathPoint>();

	private final LinkedList<MousePathPoint2> mousePath2 = new LinkedList<MousePathPoint2>();

	private final LinkedList<MouseCirclePathPoint> mouseCirclePath = new LinkedList<MouseCirclePathPoint>();

	private final LinkedList<MouseCirclePathPoint2> mouseCirclePath2 = new LinkedList<MouseCirclePathPoint2>();

	private final RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	private void antiban() {
		status = "Antiban:";
		pauseScript();
		final int r = random(1, 200);
		if (r == 1) {
			status = "Antiban: Mouse";
			mouse.moveRandomly(100, 200);
			sleep(random(2000, 2500));
		}
		if (r == 6) {
			status = "Antiban: Mouse";
			mouse.moveRandomly(25, 150);
			sleep(random(1000, 2500));
		}
		if (r == 12) {
			status = "Antiban: Stats";
			if (game.getTab() != Tab.STATS) {
				game.openTab(Tab.STATS);
				sleep(350, 500);
				mouse.move(random(615, 665), random(350, 375));
				sleep(1000, 1200);
				if (game.getTab() != Tab.INVENTORY) {
					game.openTab(Tab.INVENTORY);
					sleep(random(100, 200));
				}
			}
		}
		if (r == 19) {
			status = "Antiban: AFK";
			sleep(random(2000, 2500));
		}
		if (r == 26) {
			status = "Antiban: Camera";

			camera.setAngle(random(0, 300));
			camera.setPitch(random(35, 85));
			sleep(random(1750, 1950));
		}
	}

	private void cfd() {
		amount = Integer.parseInt(gui.textField1.getText());
		if (!inventory.contains(getAxeId())
				|| !inventory.contains(getKnifeId())) {
			log("Get a axe and knife before starting");
			log("If you have the supplys...");
			log("select the right item in the gui!");
			log("Script stopping");
			sleep(2000);
			stopScript(false);
			sleep(500);
		}
		if (amount == 0 && !isBusy() && !interfaces.get(905).isValid()) {
			chopLogs();
		} else if (fletched <= amount && !isBusy()
				&& !interfaces.get(905).isValid()) {
			chopLogs();
		} else if (fletched >= amount && amount != 0) {
			log("Done the amount required!");
			stopScript();
		}

		if (inventory.contains(getLogId())
				&& inventory.containsOneOf(getKnifeId()) && amount == 0
				&& !isBusy() && inventory.isFull()) {
			if (getBowType() == 1 || getBowType() == 2) {
				fletchLogs();
				full = 0;
			} else if (getBowType() == 3) {
				fletchShafts();
				full = 0;
			} else if (getBowType() == 4) {
				fletchStocks();
				full = 0;
			}
		} else if (inventory.contains(getLogId()) && fletched <= amount
				&& !isBusy() && inventory.isFull()) {
			if (getBowType() == 1 || getBowType() == 2) {
				fletchLogs();
				full = 0;
			} else if (getBowType() == 3) {
				fletchShafts();
				full = 0;
			} else if (getBowType() == 4) {
				fletchStocks();
				full = 0;
			}
		} else if (fletched >= amount && amount != 0) {
			log("Done the amount required!");
			stopScript();
		}

		if (inventory.contains(getUnstrungId())
				&& inventory.containsOneOf(getKnifeId()) && amount == 0
				&& !isBusy() && inventory.isFull() && getBowType() != 3) {
			drop();
		} else if (inventory.contains(getUnstrungId()) && fletched <= amount
				&& !isBusy() && inventory.isFull()) {
			drop();
		} else if (fletched >= amount && amount != 0 && getBowType() != 3) {
			log("Done the amount required!");
			stopScript();
		}
		while (isBusy() && !interfaces.get(740).isValid()) {
			antiban();
			sleep(random(200, 250));
		}
		clickContinue();
		if (gui.checkBox5.isSelected()) {
			checkfor99();
		}
	}

	private void checkfor99() {
		currentexp = skills.getCurrentExp(Skills.FLETCHING);
		if (currentexp >= constants.xpIsClose) {
			status = "Check 99: Logging out";
			if (bank.isOpen()) {
				bank.close();
			}
			stopScript(true);
		}
	}

	private void chopLogs() {
		walk();
		status = "Chop: Logs";
		if (objects.getNearest(getTreeId()) != null
				&& getMyPlayer().getAnimation() == -1
				&& !getMyPlayer().isMoving()) {
			if (objects.getNearest(getTreeId()) != null
					&& !isBusy()
					&& calc.tileOnScreen(objects.getNearest(getTreeId()).getLocation())) {
				objects.getNearest(getTreeId()).doAction("Chop");
				sleep(random(1000, 1250));
				if (full > 5) {
					log("Inventory was to full, error!");
					log("Clearing out inventory!");
					full = 0;
					drop();
				}
				if (fail > 3) {
					status = "Fail: getting new tree.";
					walking.walkTileMM(getMyPlayer().getLocation().randomize(10, 10));
					sleep(800);
					while (getMyPlayer().isMoving() || isBusy()) {
						sleep(250);
					}
					fail = 0;
					walk();
				}
				drop();
			}
		}
	}

	private void clickContinue() {
		if (interfaces.get(740).isValid()) {
			status = "Level up: Clicking Continue";
			sleep(50, 75);
			if (gui.checkBox2.isSelected()) {
				env.saveScreenshot(true);
			}
			if (gui.checkBox3.isSelected()
					&& skills.getRealLevel(Skills.FLETCHING) == 99 && !has99) {
				log("If you have 99 already, Disable at 99 for screenshots!");
				env.saveScreenshot(true);
				has99 = true;
			}
			if (tray) {
				trayInfo.systray.displayMessage("Level UP", "You are now level: "
						+ skills.getCurrentLevel(Skills.FLETCHING), TrayIcon.MessageType.INFO);
			}
			sleep(150, 1500);
			interfaces.get(740).getComponent(3).doClick(true);
			sleep(150, 400);
		}
	}

	private boolean closeSWIFace() {
		if (interfaces.get(276).isValid()) {
			sleep(random(100, 200));
			interfaces.get(276).getComponent(76).doClick(true);
			sleep(random(300, 400));
		}
		return !interfaces.get(276).isValid();
	}

	private void createCBowU() {
		status = "Creating: Stocks with limbs";
		try {
			if (bank.isOpen()) {
				bank.close();
			}
			sleep(50, 100);
			if (!interfaces.get(905).isValid() && !isBusy()) {
				if (random(1, 2) == 1) {
					inventory.getItem(getUnstrungId()).doClick(true);
					sleep(200, 400);
					inventory.getItem(getObjectId()).doClick(true);
				} else {
					inventory.getItem(getUnstrungId()).doClick(true);
					sleep(200, 400);
					inventory.getItem(getObjectId()).doClick(true);
				}
			}
			sleep(50, 100);
			mouse.moveRandomly(150, 500);
			sleep(400, 450);
			if (interfaces.get(905).isValid()) {
				interfaces.getComponent(905, 14).doClick();
			}
			sleep(50, 200);
		} catch (final Exception e) {
		}
	}

	private void createGui() {
		gui = new gui();
		loadSettings();
		gui.progressBar1.setValue(skills.getPercentToNextLevel(Skills.FLETCHING));
		gui.checkBox16.setSelected(false);
		if (gui.textField2.getText().equals("All")) {
			gui.textField2.setEnabled(true);
			gui.button4.setEnabled(true);
		} else {
			gui.textField2.setEnabled(false);
			gui.button4.setEnabled(false);
		}
		name = gui.textField2.getText();
		if (name != "All") {
			siggy = getImage("", false, "http://www.universalscripts.org/ufletch/UFletch_generate.php?user="
					+ name);
			gui.label1 = new JLabel(new ImageIcon(siggy));
		}
	}

	private void createSignature() {
		if (!connection) {
			try {
				URL url;
				URLConnection urlConn;
				url = new URL("http://www.universalscripts.org/ufletch/UFletch_submit.php");
				urlConn = url.openConnection();
				urlConn.setRequestProperty("User-Agent", "UFletchAgent");
				urlConn.setDoInput(true);
				urlConn.setDoOutput(true);
				urlConn.setUseCaches(false);
				urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				String content = "";
				final String[] stats = { "auth", "secs", "mins", "hours",
						"days", "fletched", "strung", "expgained" };
				final Object[] data = { gui.textField2.getText(), 0, 0, 0, 0,
						0, 0, 0 };
				for (int i = 0; i < stats.length; i++) {
					content += stats[i] + "=" + data[i] + "&";
				}
				content = content.substring(0, content.length() - 1);
				final OutputStreamWriter wr = new OutputStreamWriter(urlConn.getOutputStream());
				wr.write(content);
				wr.flush();
				final BufferedReader rd = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
				String line;
				while ((line = rd.readLine()) != null) {
					log(Color.GREEN, line);
				}
				wr.close();
				rd.close();
			} catch (final Exception e) {
			}
		}
	}

	private void drop() {
		status = "Drop: Fletched items";
		for (final RSItem i : inventory.getItems()) {
			if (i.getID() != 15545 && i.getID() != 15544) {
				final GEItem item = grandExchange.lookup(i.getID());
				if (item.getGuidePrice() < 3000) {
					if (item.getID() != getAxeId()
							&& item.getID() != getKnifeId()
							&& item.getID() != 15544 && item.getID() != 15545
							&& item.getID() != 52) {
						i.doAction("drop");
					}
				}
			}
		}
	}

	private void featherArrows() {
		status = "Making: Arrows";
		try {
			if (bank.isOpen()) {
				bank.close();
			}
			sleep(50, 100);
			if (!interfaces.get(905).isValid() && !isBusy()) {
				if (random(1, 2) == 1) {
					inventory.getItem(constants.SHAFT_ID).doClick(true);
					sleep(200, 400);
					inventory.getItem(constants.FEATHER_ID).doClick(true);
				} else {
					inventory.getItem(constants.SHAFT_ID).doClick(true);
					sleep(200, 400);
					inventory.getItem(constants.FEATHER_ID).doClick(true);
				}
			}
			sleep(50, 100);
			mouse.moveRandomly(150, 500);
			sleep(400, 450);
			if (interfaces.get(905).isValid()) {
				interfaces.getComponent(905, 14).doClick();
			}
			sleep(50, 200);
		} catch (final Exception e) {
		}
	}

	private void featherBolts() {
		status = "Making: Bolts";
		try {
			if (bank.isOpen()) {
				bank.close();
			}
			if (!interfaces.get(905).isValid() && !isBusy()) {
				if (random(1, 2) == 1) {
					inventory.getItem(constants.FEATHER_ID).doClick(true);
					sleep(100, 200);
					inventory.getItem(getObjectId()).doClick(true);
				} else {
					inventory.getItem(constants.FEATHER_ID).doClick(true);
					sleep(100, 200);
					inventory.getItem(getObjectId()).doClick(true);
				}
			}
			if (random(1, 28) == 4) {
				antiban();
			}
		} catch (final Exception e) {
		}
	}

	private void featherDarts() {
		status = "Making: Darts";
		try {
			if (bank.isOpen()) {
				bank.close();
			}
			sleep(50, 100);
			if (!interfaces.get(905).isValid() && !isBusy()) {
				if (random(1, 2) == 1) {
					inventory.getItem(constants.FEATHER_ID).doClick(true);
					sleep(200, 400);
					inventory.getItem(getObjectId()).doClick(true);
				} else {
					inventory.getItem(constants.FEATHER_ID).doClick(true);
					sleep(200, 400);
					inventory.getItem(getObjectId()).doClick(true);
				}
			}
			sleep(50, 100);
			mouse.moveRandomly(150, 500);
			sleep(400, 450);
			if (interfaces.get(905).isValid()) {
				interfaces.getComponent(905, 14).doClick();
			}
			sleep(50, 200);
		} catch (final Exception e) {
		}
	}

	private void fletch() {
		amount = Integer.parseInt(gui.textField1.getText());
		if (!inventory.contains(getKnifeId()) && amount == 0 && !isBusy()
				&& !interfaces.get(905).isValid()) {
			withdrawKnife();
			sleep(random(200, 250));
		}
		if (!inventory.contains(getKnifeId()) && fletched <= amount
				&& !isBusy() && !interfaces.get(905).isValid()) {
			withdrawKnife();
			sleep(random(200, 250));
		}
		if (!inventory.contains(getLogId()) && amount == 0 && !isBusy()
				&& !interfaces.get(905).isValid()) {
			if (getBowType() == 1 || getBowType() == 2) {
				withdrawLogs();
			} else if (getBowType() == 3) {
				withdrawShafts();
			} else if (getBowType() == 4) {
				withdrawStocks();
			}
			sleep(random(200, 250));
		} else if (!inventory.contains(getLogId()) && fletched <= amount
				&& !isBusy() && !interfaces.get(905).isValid()) {
			if (getBowType() == 1 || getBowType() == 2) {
				withdrawLogs();
			} else if (getBowType() == 3) {
				withdrawShafts();
			} else if (getBowType() == 4) {
				withdrawStocks();
			}
			sleep(random(200, 250));
		} else if (fletchAndString && !isBusy() && fletched >= amount
				&& amount > 0) {
			gui.comboBox1.setSelectedItem("String");
		} else if (fletchAndString && !isBusy() && amount == 0
				&& bank.getItem(getLogId()) == null
				&& !interfaces.get(905).isValid()) {
			sleep(random(50, 100));
			if (fletchAndString && !isBusy() && amount == 0
					&& bank.getItem(getLogId()) == null
					&& !interfaces.get(905).isValid()
					&& inventory.getCount() < 1) {
				gui.comboBox1.setSelectedItem("String");
			}
		} else if (fletched >= amount && amount != 0 && !fletchAndString) {
			log("Fletched amount logging out!");
			stopScript();
		}
		if (inventory.contains(getLogId())
				&& inventory.containsOneOf(getKnifeId()) && amount == 0
				&& !isBusy()) {
			if (getBowType() == 1 || getBowType() == 2) {
				fletchLogs();
			} else if (getBowType() == 3) {
				fletchShafts();
			} else if (getBowType() == 4) {
				fletchStocks();
			}
			sleep(random(200, 250));
		} else if (inventory.contains(getLogId())
				&& inventory.containsOneOf(getKnifeId()) && fletched <= amount
				&& !isBusy()) {
			if (getBowType() == 1 || getBowType() == 2) {
				fletchLogs();
			} else if (getBowType() == 3) {
				fletchShafts();
			} else if (getBowType() == 4) {
				fletchStocks();
			}
			sleep(random(100, 250));
		} else if (fletchAndString && !isBusy() && fletched >= amount
				&& amount > 0) {
			gui.comboBox1.setSelectedItem("String");
		} else if (fletchAndString && !isBusy() && amount == 0
				&& bank.getItem(getLogId()) == null
				&& !interfaces.get(905).isValid()) {
			sleep(random(50, 100));
			if (fletchAndString && !isBusy() && amount == 0
					&& bank.getItem(getLogId()) == null
					&& !interfaces.get(905).isValid()
					&& inventory.getCount() < 1) {
				gui.comboBox1.setSelectedItem("String");
			}
		} else if (fletched >= amount && amount != 0 && !isBusy()
				&& !fletchAndString) {
			log("Fletched amount logging out!");
			stopScript();
		}
		if (isBusy() && !interfaces.get(740).isValid()) {
			antiban();
			sleep(random(200, 250));
		}
		clickContinue();
		if (gui.checkBox5.isSelected()) {
			checkfor99();
		}
	}

	private void fletchAndString() {
		fletchAndString = true;
		gui.comboBox1.setSelectedItem("Fletching Bow");
	}

	private void fletchLogs() {
		status = "Fletching: UBows";
		try {
			if (bank.isOpen()) {
				bank.close();
			}
			sleep(50, 100);
			if (!interfaces.get(905).isValid() && !isBusy()
					&& inventory.containsOneOf(getKnifeId())) {
				if (random(1, 2) == 1) {
					inventory.getItem(getLogId()).doClick(true);
					sleep(200, 400);
					inventory.getItem(getKnifeId()).doClick(true);
				} else {
					inventory.getItem(getKnifeId()).doClick(true);
					sleep(200, 400);
					inventory.getItem(getLogId()).doClick(true);
				}
			}
			sleep(50, 100);
			mouse.move(random(35, 448), random(500, 355));
			sleep(400, 450);
			if (interfaces.get(905).isValid()) {
				if (getBowType() == 1) {
					status = "Fletching: short";
					if (getLogId() == 1511) {
						sleep(200, 250);
						interfaces.get(905).getComponent(15).doAction("Make All");
					} else {
						sleep(200, 250);
						interfaces.get(905).getComponent(14).doAction("Make All");
					}
				}
				if (getBowType() == 2) {
					status = "Fletching: long";
					if (getLogId() == 1511) {
						sleep(200, 250);
						interfaces.get(905).getComponent(16).doAction("Make All");
					} else {
						sleep(200, 250);
						interfaces.get(905).getComponent(15).doAction("Make All");
					}
				}
			}
			sleep(50, 200);
		} catch (final Exception e) {
		}
	}

	private void fletchShafts() {
		status = "Fletching: Shafts";
		try {
			if (bank.isOpen()) {
				bank.close();
			}
			sleep(50, 100);
			if (!interfaces.get(905).isValid() && !isBusy()) {
				if (random(1, 2) == 1) {
					inventory.getItem(getLogId()).doClick(true);
					sleep(200, 400);
					inventory.getItem(getKnifeId()).doClick(true);
				} else {
					inventory.getItem(getKnifeId()).doClick(true);
					sleep(200, 400);
					inventory.getItem(getLogId()).doClick(true);
				}
			}
			sleep(50, 100);
			mouse.moveRandomly(150, 500);
			sleep(400, 450);
			if (interfaces.get(905).isValid()) {
				if (getLogId() == 1511) {
					sleep(200, 250);
					interfaces.get(905).getComponent(14).doClick(true);
				} else if (getLogId() != 1511) {
					log("Please select normal logs!");
					stopScript();
				}
			}
			sleep(50, 200);
		} catch (final Exception e) {
		}
	}

	private void fletchStocks() {
		status = "Fletching: Stocks";
		try {
			if (bank.isOpen()) {
				bank.close();
			}
			sleep(50, 100);
			if (!interfaces.get(905).isValid() && !isBusy()) {
				if (random(1, 2) == 1) {
					inventory.getItem(getLogId()).doClick(true);
					sleep(200, 400);
					inventory.getItem(getKnifeId()).doClick(true);
				} else {
					inventory.getItem(getKnifeId()).doClick(true);
					sleep(200, 400);
					inventory.getItem(getLogId()).doClick(true);
				}
			}
			sleep(50, 100);
			mouse.moveRandomly(150, 500);
			sleep(400, 450);
			if (interfaces.get(905).isValid()) {
				if (getLogId() == 1513) {
					log("Please slect a different log!");
					stopScript();
				} else if (getLogId() != 1513) {
					if (getLogId() == 1511) {
						sleep(200, 250);
						interfaces.get(905).getComponent(17).doClick(true);
					} else {
						sleep(200, 250);
						interfaces.get(905).getComponent(17).doClick(true);
					}
				}
			}
			sleep(50, 200);
		} catch (final Exception e) {
		}
	}

	private int getAxeId() {
		if (gui.comboBox5.getSelectedIndex() == 0) {
			return 1351;
		} else if (gui.comboBox5.getSelectedIndex() == 1) {
			return 1349;
		} else if (gui.comboBox5.getSelectedIndex() == 2) {
			return 1361;
		} else if (gui.comboBox5.getSelectedIndex() == 3) {
			return 1355;
		} else if (gui.comboBox5.getSelectedIndex() == 4) {
			return 1357;
		} else if (gui.comboBox5.getSelectedIndex() == 5) {
			return 1359;
		} else if (gui.comboBox5.getSelectedIndex() == 6) {
			return 6739;
		}
		return -1;
	}

	private int getBowType() {
		if (gui.comboBox3.getSelectedIndex() == 0) {
			return 1;
		} else if (gui.comboBox3.getSelectedIndex() == 1) {
			return 2;
		} else if (gui.comboBox3.getSelectedIndex() == 2) {
			return 3;
		} else if (gui.comboBox3.getSelectedIndex() == 3) {
			return 4;
		} else if (gui.comboBox3.getSelectedIndex() == 4) {
			return 5;
		}
		return -1;
	}

	private int getBSId() {
		if (getMethod() == 5) {
			return constants.CBOW_STRING_ID;
		} else {
			return constants.BOW_STRING_ID;
		}
	}

	private void getExtraImages() {
		URL url;
		try {
			log(Color.GREEN.darker(), "Please wait while we check the connection to universalscipts.org");
			url = new URL("http://www.universalscripts.org/ufletch/index2.php");
			final URLConnection connection = url.openConnection();
			connection.connect();
			final HttpURLConnection httpConnection = (HttpURLConnection) connection;
			final int code = httpConnection.getResponseCode();
			if (code == 200) {
				icon = getImage("fav.png", true, "http://www.universalscripts.org/ufletch/Images/fav.png");
				fletchIcon = getImage("fletchIcon.png", true, "http://www.universalscripts.org/ufletch/Images/fletchIcon.png");

				settings = getImage("settings.png", true, "http://www.universalscripts.org/ufletch/Images/settings.png");
				bow = getImage("bow.png", true, "http://www.universalscripts.org/ufletch/Images/bow.png");
				logsImage = getImage("logs.png", true, "http://www.universalscripts.org/ufletch/Images/logs.png");
				axe = getImage("axe.png", true, "http://www.universalscripts.org/ufletch/Images/axe.png");
				knife = getImage("knife.png", true, "http://www.universalscripts.org/ufletch/Images/knife.png");
				brush = getImage("paint.png", true, "http://www.universalscripts.org/ufletch/Images/paint.png");
				siggy = getImage("", false, "http://www.universalscripts.org/ufletch/UFletch_generate.php?user="
						+ name);
				pic = getImage("camera.png", true, "http://www.universalscripts.org/ufletch/Images/camera.png");
			}
		} catch (final MalformedURLException e) {
		} catch (final IOException e) {
			log.severe("Error Connecting to universalscripts.org");
			log.severe("Using backup image!");
			connection = false;
			icon = pic();
			fletchIcon = pic();
			settings = pic();
			bow = pic();
			logsImage = pic();
			axe = pic();
			knife = pic();
			brush = pic();
			siggy = pic();
			pic = pic();
		}

	}

	private void getExtraInfo() {
		if (gui.checkBox16.isSelected()) {
			beep = new beeper();
			b = new Thread(beep);
			b.start();
		}
		trayInfo = new trayInfo();
		if (tray) {
			trayInfo.systray.displayMessage("Welcome!", "Thanks for using UFletch!", TrayIcon.MessageType.INFO);
		}
		gui.checkBox16.setEnabled(false);
		sleep(random(50, 75));
		sleep(random(400, 500));
		startXP = skills.getCurrentExp(Skills.FLETCHING);
		startLevel = skills.getCurrentLevel(Skills.FLETCHING);
		sleep(random(100, 250));
	}

	private int getHourly(final int input) {
		final double millis = System.currentTimeMillis() - startTime;
		return (int) (input / millis * 3600000);
	}

	public Image getImage(final String fileName, final boolean save,
			final String url) {
		final Logger log = Logger.getLogger(this.getClass().getName());
		final File dir = new File(getCacheDirectory() + "/Images");
		try {
			if (!dir.exists()) {
				if (!dir.mkdir()) {
					return null;
				}
			}
		} catch (final Exception e) {
			return null;
		}
		ImageIO.setCacheDirectory(dir);
		final File f = new File(getCacheDirectory() + "/Images/" + fileName);
		final File loc = new File(getCacheDirectory() + "/Images/");
		if (save) {
			try {
				if (loc.exists()) {
					if (f.exists()) {
						if (connection) {
							log(Color.GREEN, "Successfully loaded Image from scripts folder.");
						}
						return ImageIO.read(f.toURI().toURL());
					}
				}
				final Image img = ImageIO.read(new URL(url));
				if (img != null) {
					if (!loc.exists()) {
						loc.mkdir();
					}
					ImageIO.write((RenderedImage) img, "PNG", f);
					if (connection) {
						log.info("Saved Image to Scripts folder successfully.");
					}
					return img;
				}
			} catch (final IOException e) {
				return pic();
			}
		} else if (!save) {
			try {
				return ImageIO.read(new URL(url));
			} catch (final MalformedURLException e) {
			} catch (final IOException e) {
				return pic();
			}
		}
		return null;
	}

	private int getKnifeId() {
		if (gui.comboBox4.getSelectedIndex() == 0) {
			return 946;
		} else if (gui.comboBox4.getSelectedIndex() == 1) {
			return 14111;
		}
		return -1;
	}

	private int getLogId() {
		if (gui.comboBox2.getSelectedIndex() == 0) {
			return 1511;
		} else if (gui.comboBox2.getSelectedIndex() == 1) {
			return 1521;
		} else if (gui.comboBox2.getSelectedIndex() == 2) {
			return 1519;
		} else if (gui.comboBox2.getSelectedIndex() == 3) {
			return 1517;
		} else if (gui.comboBox2.getSelectedIndex() == 4) {
			return 1515;
		} else if (gui.comboBox2.getSelectedIndex() == 5) {
			return 1513;
		}
		return -1;
	}

	private int getMethod() {
		if (gui.comboBox1.getSelectedIndex() == 0) {
			return 1;
		} else if (gui.comboBox1.getSelectedIndex() == 1) {
			return 2;
		} else if (gui.comboBox1.getSelectedIndex() == 2) {
			return 3;
		} else if (gui.comboBox1.getSelectedIndex() == 3) {
			return 4;
		} else if (gui.comboBox1.getSelectedIndex() == 4) {
			return 5;
		} else if (gui.comboBox1.getSelectedIndex() == 5) {
			return 6;
		} else if (gui.comboBox1.getSelectedIndex() == 6) {
			return 7;
		} else if (gui.comboBox1.getSelectedIndex() == 7) {
			return 8;
		} else if (gui.comboBox1.getSelectedIndex() == 8) {
			return 9;
		}
		return -1;
	}

	private int getObjectId() {
		if (getMethod() == 4) {
			if (gui.comboBox5.getSelectedIndex() == 0) {
				return 9420;
			} else if (gui.comboBox5.getSelectedIndex() == 1) {
				return 9423;
			} else if (gui.comboBox5.getSelectedIndex() == 2) {
				return 9425;
			} else if (gui.comboBox5.getSelectedIndex() == 3) {
				return 9427;
			} else if (gui.comboBox5.getSelectedIndex() == 4) {
				return 9429;
			} else if (gui.comboBox5.getSelectedIndex() == 5) {
				return 9431;
			}
		} else if (getMethod() == 7) {
			if (gui.comboBox5.getSelectedIndex() == 0) {
				return 39;
			} else if (gui.comboBox5.getSelectedIndex() == 1) {
				return 40;
			} else if (gui.comboBox5.getSelectedIndex() == 2) {
				return 41;
			} else if (gui.comboBox5.getSelectedIndex() == 3) {
				return 42;
			} else if (gui.comboBox5.getSelectedIndex() == 4) {
				return 43;
			} else if (gui.comboBox5.getSelectedIndex() == 5) {
				return 44;
			}
		} else if (getMethod() == 8) {
			if (gui.comboBox5.getSelectedIndex() == 0) {
				return 9375;
			} else if (gui.comboBox5.getSelectedIndex() == 1) {
				return 9377;
			} else if (gui.comboBox5.getSelectedIndex() == 2) {
				return 9378;
			} else if (gui.comboBox5.getSelectedIndex() == 3) {
				return 9379;
			} else if (gui.comboBox5.getSelectedIndex() == 4) {
				return 9380;
			} else if (gui.comboBox5.getSelectedIndex() == 5) {
				return 9381;
			}
		} else if (getMethod() == 9) {
			if (gui.comboBox5.getSelectedIndex() == 0) {
				return 819;
			} else if (gui.comboBox5.getSelectedIndex() == 1) {
				return 820;
			} else if (gui.comboBox5.getSelectedIndex() == 2) {
				return 821;
			} else if (gui.comboBox5.getSelectedIndex() == 3) {
				return 822;
			} else if (gui.comboBox5.getSelectedIndex() == 4) {
				return 823;
			} else if (gui.comboBox5.getSelectedIndex() == 5) {
				return 824;
			} else if (gui.comboBox5.getSelectedIndex() == 6) {
				return 11232;
			}
		}
		return -1;
	}

	private Color getPaintColors(final JComboBox box) {
		if (box.getSelectedIndex() == 0) {
			return Color.BLACK;
		} else if (box.getSelectedIndex() == 1) {
			return Color.RED.brighter();
		} else if (box.getSelectedIndex() == 2) {
			return Color.ORANGE;
		} else if (box.getSelectedIndex() == 3) {
			return Color.CYAN.brighter();
		} else if (box.getSelectedIndex() == 4) {
			return Color.GREEN.brighter();
		} else if (box.getSelectedIndex() == 5) {
			return Color.YELLOW;
		} else if (box.getSelectedIndex() == 6) {
			return Color.PINK;
		} else if (box.getSelectedIndex() == 7) {
			return Color.WHITE;
		} else if (box.getSelectedIndex() == 8) {
			return constants.TAN;
		}
		return Color.BLACK;
	}

	private double getRot(final double rot, final boolean negative,
			final double speed) {
		if (negative) {
			return Math.toRadians(-System.currentTimeMillis() % rot / speed);
		}
		return Math.toRadians(System.currentTimeMillis() % rot / speed);
	}

	private String getRuntime() {
		try {
			long millis = System.currentTimeMillis() - startTime;
			final long days = millis / (1000 * 60 * 60 * 24);
			millis -= days * 1000 * 60 * 60 * 24;
			final long hours = millis / (1000 * 60 * 60);
			millis -= hours * 1000 * 60 * 60;
			final long minutes = millis / (1000 * 60);
			millis -= minutes * 1000 * 60;
			final long seconds = millis / 1000;
			return days + ":" + hours + ":" + minutes + ":" + seconds;
		} catch (final Exception e) {
			return "0:0:0:0";
		}
	}

	private int[] getTreeId() {
		if (gui.comboBox2.getSelectedIndex() == 0) {
			return new int[] { 1278, 1276, 38787, 38760, 38788, 38784, 38783,
					38782 };
		} else if (gui.comboBox2.getSelectedIndex() == 1) {
			return new int[] { 1281, 38731 };
		} else if (gui.comboBox2.getSelectedIndex() == 2) {
			return new int[] { 5551, 5552, 5553, 1308, 38616, 38617, 38627 };
		} else if (gui.comboBox2.getSelectedIndex() == 3) {
			return new int[] { 1307 };
		} else if (gui.comboBox2.getSelectedIndex() == 4) {
			return new int[] { 1309, 38755 };
		} else if (gui.comboBox2.getSelectedIndex() == 5) {
			return new int[] { 1306 };
		}
		return null;
	}

	private int getUnstrungId() {
		if (getBowType() == 1) { // 1=Shortbows 2=Longbows 3=Shafts 4=Stocks
									// 5=c'bow
			if (gui.comboBox2.getSelectedIndex() == 0) {
				return 50;
			} else if (gui.comboBox2.getSelectedIndex() == 1) {
				return 54;
			} else if (gui.comboBox2.getSelectedIndex() == 2) {
				return 60;
			} else if (gui.comboBox2.getSelectedIndex() == 3) {
				return 64;
			} else if (gui.comboBox2.getSelectedIndex() == 4) {
				return 68;
			} else if (gui.comboBox2.getSelectedIndex() == 5) {
				return 72;
			}
		} else if (getBowType() == 2) {
			if (gui.comboBox2.getSelectedIndex() == 0) {
				return 48;
			} else if (gui.comboBox2.getSelectedIndex() == 1) {
				return 56;
			} else if (gui.comboBox2.getSelectedIndex() == 2) {
				return 58;
			} else if (gui.comboBox2.getSelectedIndex() == 3) {
				return 62;
			} else if (gui.comboBox2.getSelectedIndex() == 4) {
				return 66;
			} else if (gui.comboBox2.getSelectedIndex() == 5) {
				return 70;
			}
		} else if (getBowType() == 3) {
			if (gui.comboBox2.getSelectedIndex() == 0) {
				return 52;
			} else {
				return -1;
			}
		} else if (getBowType() == 4) {
			if (gui.comboBox2.getSelectedIndex() == 0) {
				return 9440;
			} else if (gui.comboBox2.getSelectedIndex() == 1) {
				return 9442;
			} else if (gui.comboBox2.getSelectedIndex() == 2) {
				return 9444;
			} else if (gui.comboBox2.getSelectedIndex() == 3) {
				return 9448;
			} else if (gui.comboBox2.getSelectedIndex() == 4) {
				return 9452;
			}
		} else if (getBowType() == 5) {
			if (gui.comboBox5.getSelectedIndex() == 0) {
				return 9454;
			} else if (gui.comboBox5.getSelectedIndex() == 1) {
				return 9457;
			} else if (gui.comboBox5.getSelectedIndex() == 2) {
				return 9459;
			} else if (gui.comboBox5.getSelectedIndex() == 3) {
				return 9461;
			} else if (gui.comboBox5.getSelectedIndex() == 4) {
				return 9463;
			} else if (gui.comboBox5.getSelectedIndex() == 5) {
				return 9465;
			}
		}
		return -1;
	}

	private String getValue(final boolean selected) {
		if (selected) {
			return "true";
		}
		return "false";
	}

	private boolean isBusy() {
		if (getMethod() == 2 || getMethod() == 4 || getMethod() == 5) {
			if (getMyPlayer().getAnimation() == -1) {
				for (int i = 0; i < 50; i++) {
					sleep(50);
					if (getMyPlayer().getAnimation() != -1
							|| inventory.getCount() == 28
							|| inventory.getCount() == 0) {
						break;
					}
				}
			}
		}
		if (getMethod() == 7 || getMethod() == 9) {
			return System.currentTimeMillis() < ct + 100;
		}
		sleep(25);
		return getMyPlayer().getAnimation() != -1 && !getMyPlayer().isMoving();
	}

	private void loadSettings() {
		final Properties props = new Properties();
		final File f = new File(getCacheDirectory() + File.separator
				+ "UFletch." + account.getName());
		try {
			props.load(new FileInputStream(f));
		} catch (final IOException e) {
		}
		if (props.getProperty("Method") != null) {
			gui.comboBox1.setSelectedItem(props.getProperty("Method"));
		}
		if (props.getProperty("LogType") != null) {
			gui.comboBox2.setSelectedItem(props.getProperty("LogType"));
		}
		if (props.getProperty("BowType") != null) {
			gui.comboBox3.setSelectedItem(props.getProperty("BowType"));
		}
		if (props.getProperty("Knife") != null) {
			gui.comboBox4.setSelectedItem(props.getProperty("Knife"));
		}
		if (props.getProperty("AxeType") != null) {
			gui.comboBox5.setSelectedItem(props.getProperty("AxeType"));
		}
		if (props.getProperty("Color1") != null) {
			gui.comboBox12.setSelectedItem(props.getProperty("Color1"));
		}
		if (props.getProperty("Color2") != null) {
			gui.comboBox13.setSelectedItem(props.getProperty("Color2"));
		}
		if (props.getProperty("Color3") != null) {
			gui.comboBox8.setSelectedItem(props.getProperty("Color3"));
		}
		if (props.getProperty("Color4") != null) {
			gui.comboBox9.setSelectedItem(props.getProperty("Color4"));
		}
		if (props.getProperty("Color5") != null) {
			gui.comboBox10.setSelectedItem(props.getProperty("Color5"));
		}
		if (props.getProperty("Color6") != null) {
			gui.comboBox11.setSelectedItem(props.getProperty("Color6"));
		}
		if (props.getProperty("Color7") != null) {
			gui.comboBox14.setSelectedItem(props.getProperty("Color7"));
		}
		if (props.getProperty("Color8") != null) {
			gui.comboBox15.setSelectedItem(props.getProperty("Color8"));
		}
		if (props.getProperty("Amount") != null) {
			gui.textField1.setText(props.getProperty("Amount"));
		}
		if (props.getProperty("Name") != null) {
			gui.textField2.setText(props.getProperty("Name"));
		}
		if (props.getProperty("WhenDone") != null) {
			if (props.getProperty("WhenDone").contains("true")) {
				gui.checkBox1.setSelected(true);
			}
		}
		if (props.getProperty("UponLvl") != null) {
			if (props.getProperty("UponLvl").contains("true")) {
				gui.checkBox2.setSelected(true);
			}
		}
		if (props.getProperty("Getting99") != null) {
			if (props.getProperty("Getting99").contains("true")) {
				gui.checkBox3.setSelected(true);
			}
		}
		if (props.getProperty("Before99") != null) {
			if (props.getProperty("Before99").contains("true")) {
				gui.checkBox5.setSelected(true);
			}
		}
		if (props.getProperty("Save") != null) {
			if (props.getProperty("Save").contains("true")) {
				gui.checkBox6.setSelected(true);
			}
		}
		if (props.getProperty("Load") != null) {
			if (props.getProperty("Load").contains("true")) {
				gui.checkBox7.setSelected(true);
			}
		}
		if (props.getProperty("Paint") != null) {
			if (props.getProperty("Paint").contains("true")) {
				gui.checkBox4.setSelected(true);
			}
		}
		if (props.getProperty("Chat") != null) {
			if (props.getProperty("Chat").contains("true")) {
				gui.checkBox8.setSelected(true);
			}
		}
		if (props.getProperty("Inventory") != null) {
			if (props.getProperty("Inventory").contains("true")) {
				gui.checkBox9.setSelected(true);
			}
		}
		if (props.getProperty("Bar") != null) {
			if (props.getProperty("Bar").contains("true")) {
				gui.checkBox10.setSelected(true);
			}
		}
		if (props.getProperty("BotLine") != null) {
			if (props.getProperty("BotLine").contains("true")) {
				gui.checkBox11.setSelected(true);
			}
		}
		if (props.getProperty("UserLine") != null) {
			if (props.getProperty("UserLine").contains("true")) {
				gui.checkBox13.setSelected(true);
			}
		}
		if (props.getProperty("BotCross") != null) {
			if (props.getProperty("BotCross").contains("true")) {
				gui.checkBox12.setSelected(true);
			}
		}
		if (props.getProperty("UserCross") != null) {
			if (props.getProperty("UserCross").contains("true")) {
				gui.checkBox14.setSelected(true);
			}
		}
		if (props.getProperty("BotCircle") != null) {
			if (props.getProperty("BotCircle").contains("true")) {
				gui.checkBox17.setSelected(true);
			}
		}
		if (props.getProperty("UserCircle") != null) {
			if (props.getProperty("UserCircle").contains("true")) {
				gui.checkBox18.setSelected(true);
			}
		}
		if (props.getProperty("Message") != null) {
			if (props.getProperty("Message").contains("true")) {
				gui.checkBox15.setSelected(true);
			}
		}
		if (props.getProperty("Beep") != null) {
			if (props.getProperty("Beep").contains("true")) {
				gui.checkBox16.setSelected(true);
			}
		}
		if (props.getProperty("Speed") != null) {
			if (props.getProperty("Speed").contains("true")) {
				gui.slider1.setValue(Integer.parseInt(props.getProperty("Speed")));
			}
		}
	}

	public int loop() {
		try {
			if (getMethod() == 1) {
				fletch();
				closeSWIFace();
				pauseScript();
				return random(200, 250);
			} else if (getMethod() == 2) {
				string();
				closeSWIFace();
				pauseScript();
				return random(200, 250);
			} else if (getMethod() == 3) {
				fletchAndString();
				closeSWIFace();
				pauseScript();
				return random(200, 250);
			} else if (getMethod() == 4) {
				stocksWithLimbs();
				closeSWIFace();
				pauseScript();
				return random(200, 250);
			} else if (getMethod() == 5) {
				stringCBow();
				closeSWIFace();
				pauseScript();
				return random(200, 250);
			} else if (getMethod() == 6) {
				cfd();
				closeSWIFace();
				pauseScript();
				return random(200, 250);
			} else if (getMethod() == 7) {
				makeArrows();
				closeSWIFace();
				pauseScript();
				return random(200, 250);
			} else if (getMethod() == 8) {
				makeBolts();
				closeSWIFace();
				pauseScript();
				return random(200, 250);
			} else if (getMethod() == 9) {
				makeDarts();
				closeSWIFace();
				pauseScript();
				return random(200, 250);
			}
		} catch (final Exception e) {
			log(Color.BLUE, "If you see this message please report the following");
			log(Color.BLUE, "Error to fletch to 99!");
			e.printStackTrace();
		}
		return -1;
	}

	public void makeArrows() {
		amount = Integer.parseInt(gui.textField1.getText());

		if (inventory.contains(constants.SHAFT_ID)
				&& inventory.contains(constants.FEATHER_ID)
				&& inventory.contains(getObjectId()) && amount == 0
				&& !isBusy()) {
			featherArrows();
			sleep(random(200, 250));
		} else if (inventory.contains(constants.SHAFT_ID)
				&& inventory.contains(constants.FEATHER_ID)
				&& inventory.contains(getObjectId()) && fletched <= amount
				&& !isBusy()) {
			featherArrows();
			sleep(random(200, 250));
		} else if (!inventory.contains(constants.SHAFT_ID)
				&& !inventory.contains(constants.FEATHER_ID)
				&& inventory.contains(getObjectId()) && amount == 0
				&& !isBusy()) {
			tipArrows();
			sleep(random(200, 250));
		} else if (!inventory.contains(constants.SHAFT_ID)
				&& !inventory.contains(constants.FEATHER_ID)
				&& inventory.contains(getObjectId()) && fletched <= amount
				&& !isBusy()) {
			tipArrows();
			sleep(random(200, 250));
		} else if (!isBusy()
				&& (!inventory.contains(constants.SHAFT_ID)
						|| !inventory.contains(constants.FEATHER_ID)
						|| !inventory.contains(getObjectId()) || !inventory.contains(constants.FEATHER_SHAFT_ID))) {
			log.severe("Out of supplys");
			stopScript();
		} else if (fletched >= amount && amount != 0) {
			log("strung the chosen amount of bows!");
			stopScript();
		}

		if (isBusy()) {
			antiban();
			sleep(random(400, 500));
		}
		clickContinue();
		if (gui.checkBox5.isSelected()) {
			checkfor99();
		}
	}

	private void makeBolts() {
		amount = Integer.parseInt(gui.textField1.getText());
		if (inventory.contains(getObjectId())
				&& inventory.contains(constants.FEATHER_ID) && amount == 0
				&& !isBusy()) {
			featherBolts();
			sleep(random(200, 250));
		} else if (inventory.contains(getObjectId())
				&& inventory.contains(constants.FEATHER_ID)
				&& fletched <= amount && !isBusy()) {
			featherBolts();
			sleep(random(200, 250));
		} else if (!isBusy()
				&& (!inventory.contains(constants.FEATHER_ID) || !inventory.contains(getObjectId()))) {
			log.severe("Out of supplys");
			stopScript();
		} else if (fletched >= amount && amount != 0) {
			log("strung the chosen amount of bows!");
			stopScript();
		}
		clickContinue();
		if (gui.checkBox5.isSelected()) {
			checkfor99();
		}
	}

	private void makeDarts() {
		amount = Integer.parseInt(gui.textField1.getText());
		if (inventory.contains(getObjectId())
				&& inventory.contains(constants.FEATHER_ID) && amount == 0
				&& !isBusy()) {
			featherDarts();
			sleep(random(200, 250));
		} else if (inventory.contains(getObjectId())
				&& inventory.contains(constants.FEATHER_ID)
				&& fletched <= amount && !isBusy()) {
			featherDarts();
			sleep(random(200, 250));
		} else if (!isBusy()
				&& (!inventory.contains(constants.FEATHER_ID) || !inventory.contains(getObjectId()))) {
			log.severe("Out of supplys");
			stopScript();
		} else if (fletched >= amount && amount != 0) {
			log("strung the chosen amount of bows!");
			stopScript();
		}

		if (isBusy()) {
			antiban();
			sleep(random(400, 500));
		}
		clickContinue();
		if (gui.checkBox5.isSelected()) {
			checkfor99();
		}
	}

	private boolean manageLogin() {
		for (int i = 0; i < 80; i++) {
			sleep(80);
			if (game.isLoggedIn()) {
				break;
			}
		}
		return game.isLoggedIn();
	}

	@Override
	public void messageReceived(final MessageEvent message) {
		try {
			final String m = message.getMessage().toLowerCase();
			final int person = message.getID();
			if (m.contains("you carefully cut")
					&& person == MessageEvent.MESSAGE_ACTION) {
				fletched++;
			}
			if (m.contains("you carefully cut the wood into 15")
					&& person == MessageEvent.MESSAGE_ACTION) {
				fletched--;
				fletched += 15;
			}
			if (m.contains("you attach the stock")
					&& person == MessageEvent.MESSAGE_ACTION) {
				fletched++;
			}
			if (m.contains("you add a string")
					&& person == MessageEvent.MESSAGE_ACTION) {
				strung++;
			}
			if (m.contains("you attach arrow")
					&& person == MessageEvent.MESSAGE_ACTION) {
				fletched += 15;
			}
			if (m.contains("you attach")
					&& person == MessageEvent.MESSAGE_ACTION) {
				ct = System.currentTimeMillis() + 2000;
			}
			if (m.contains("you finish making")
					&& person == MessageEvent.MESSAGE_ACTION) {
				ct = System.currentTimeMillis() + 2000;
				fletched += 10;
			}
			if (m.contains("you fletch 10")
					&& person == MessageEvent.MESSAGE_ACTION) {
				fletched += 10;
			}
			if (m.contains("you can't reach that")
					&& person == MessageEvent.MESSAGE_ACTION) {
				fail++;
			}
			if (m.contains("your inventory is too full")
					&& person == MessageEvent.MESSAGE_ACTION) {
				fail++;
				full++;
			}
			if (m.contains("you need a")
					&& person == MessageEvent.MESSAGE_ACTION) {
				log("not high enough level! Stopping!");
				stopScript(true);
			}
			if (gui.checkBox15.isSelected()) {
				if (person == MessageEvent.MESSAGE_CHAT
						|| person == MessageEvent.MESSAGE_CLAN_CHAT
						|| person == MessageEvent.MESSAGE_PRIVATE_IN) {
					if (tray) {
						trayInfo.systray.displayMessage(message.getSender()
								+ ":", message.getMessage(), TrayIcon.MessageType.WARNING);
					}
				}
			}
		} catch (final Exception e) {
		}
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		if (e.getPoint() != null) {
			if (constants.BUTTON.contains(e.getPoint())) {
				gui.button1.setText("Update!");
				gui.setVisible(true);
			}
			if (constants.HIDE.contains(e.getPoint())) {
				if (fullPaint) {
					fullPaint = false;
					buttonOption = "Unhide";
				} else if (!fullPaint) {
					fullPaint = true;
					buttonOption = "Hide";
				}
			}
			if (constants.PAUSE.contains(e.getPoint()) && paused == "Pause") {
				pause = true;
				paused = "Resume";
			} else if (constants.PAUSE.contains(e.getPoint())
					&& paused == "Resume") {
				pause = false;
				paused = "Pause";
				env.setUserInput(Environment.INPUT_KEYBOARD);
			}
			if (constants.STOP.contains(e.getPoint())) {
				pauseScript();
				stopScript();
			}
		}
	}

	@Override
	public void mouseDragged(final MouseEvent e) {
		if (e.getPoint() != null) {
			p = e.getPoint().getLocation();
			if (!fullPaint) {
				if (constants.RECT_ONE.contains(e.getPoint())) {
					tabOne = true;
				}
				if (constants.RECT_TWO.contains(e.getPoint())) {
					tabTwo = true;
				}
				if (constants.RECT_THREE.contains(e.getPoint())) {
					tabThree = true;
				}

				if (!constants.RECT_ONE.contains(e.getPoint())) {
					tabOne = false;
				}
				if (!constants.RECT_TWO.contains(e.getPoint())) {
					tabTwo = false;
				}
				if (!constants.RECT_THREE.contains(e.getPoint())) {
					tabThree = false;
				}
			}
		}
	}

	public void mouseEntered(final MouseEvent e) {
	}

	public void mouseExited(final MouseEvent e) {
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		if (e.getPoint() != null) {
			p = e.getPoint().getLocation();
			if (!fullPaint) {
				if (constants.RECT_ONE.contains(e.getPoint())) {
					tabOne = true;
				}
				if (constants.RECT_TWO.contains(e.getPoint())) {
					tabTwo = true;
				}
				if (constants.RECT_THREE.contains(e.getPoint())) {
					tabThree = true;
				}

				if (!constants.RECT_ONE.contains(e.getPoint())) {
					tabOne = false;
				}
				if (!constants.RECT_TWO.contains(e.getPoint())) {
					tabTwo = false;
				}
				if (!constants.RECT_THREE.contains(e.getPoint())) {
					tabThree = false;
				}
			}
		}
	}

	public void mousePressed(final MouseEvent e) {
	}

	public void mouseReleased(final MouseEvent e) {
	}

	@SuppressWarnings("deprecation")
	public void onFinish() {
		updateSignature();
		log.info("Thanks for using UFletch. Have a good one ;)");
		if (gui.checkBox1.isSelected()) {
			env.saveScreenshot(true);
		}
		if (tray) {
			SystemTray.getSystemTray().remove(trayInfo.systray);
		}
		if (gui.checkBox16.isSelected()) {
			b.interrupt();
			b.suspend();
		}
	}

	public void onRepaint(final Graphics render) {
		if (!game.isLoggedIn()) {
			return;
		}
		if (gui.checkBox4.isSelected()) {
			final Graphics2D g = (Graphics2D) render.create();
			g.setRenderingHints(rh);
			long millis = System.currentTimeMillis() - startTime;
			final long hours = millis / (1000 * 60 * 60);
			millis -= hours * 1000 * 60 * 60;
			final long minutes = millis / (1000 * 60);
			millis -= minutes * 1000 * 60;
			final long seconds = millis / 1000;
			xpGained = skills.getCurrentExp(Skills.FLETCHING) - startXP;
			xpToLevel = skills.getExpToNextLevel(Skills.FLETCHING);
			final float xpsec = (float) xpGained
					/ (float) (seconds + minutes * 60 + hours * 60 * 60);
			final float xpmin = xpsec * 60;
			final float xphour = xpmin * 60;
			final float xpDay = xphour * 24;
			if (xpGained > 0) {
				daysTNL = (int) Math.floor(xpToLevel / xpDay);
				hoursTNL = (int) Math.floor(xpToLevel / xphour);
				minsTNL = (int) Math.floor((xpToLevel / xphour - hoursTNL) * 60);
				secTNL = (int) Math.floor(((xpToLevel / xphour - hoursTNL) * 60 - minsTNL) * 60);
			}

			// ==========> Status <==========
			g.setColor(Color.BLACK);
			g.drawRect(280, 316, 164, 22);
			g.setColor(new Color(getPaintColors(gui.comboBox13).getRed(), getPaintColors(gui.comboBox13).getGreen(), getPaintColors(gui.comboBox13).getBlue(), 127));
			g.fillRect(281, 317, 163, 21);
			g.fillRect(281, 317, 163, 11);
			g.setColor(getPaintColors(gui.comboBox12));
			g.setFont(constants.textFont);
			g.drawString("Status: " + status, 288, 332);

			// =========> Show All <=========
			g.setColor(Color.BLACK);
			g.drawRect(4, 316, 66, 22);
			g.setColor(new Color(getPaintColors(gui.comboBox13).getRed(), getPaintColors(gui.comboBox13).getGreen(), getPaintColors(gui.comboBox13).getBlue(), 127));
			g.fillRect(5, 317, 65, 21);
			g.fillRect(5, 317, 65, 11);
			g.setColor(getPaintColors(gui.comboBox12));
			g.setFont(constants.textFont);
			g.drawString(buttonOption, 16, 332);
			// =========> Show GUI <=========
			g.setColor(Color.BLACK);
			g.drawRect(449, 316, 66, 22);
			g.setColor(new Color(getPaintColors(gui.comboBox13).getRed(), getPaintColors(gui.comboBox13).getGreen(), getPaintColors(gui.comboBox13).getBlue(), 127));
			g.fillRect(450, 317, 65, 21);
			g.fillRect(450, 317, 65, 11);
			g.setColor(getPaintColors(gui.comboBox12));
			g.setFont(constants.textFont);
			g.drawString("Open GUI", 460, 332);
			// =========> Stop <=========
			g.setColor(Color.BLACK);
			g.drawRect(4, 292, 66, 22);
			g.setColor(new Color(getPaintColors(gui.comboBox13).getRed(), getPaintColors(gui.comboBox13).getGreen(), getPaintColors(gui.comboBox13).getBlue(), 127));
			g.fillRect(5, 293, 65, 21);
			g.fillRect(5, 293, 65, 11);
			g.setColor(getPaintColors(gui.comboBox12));
			g.setFont(constants.textFont);
			g.drawString("Stop", 18, 308);
			// =========> Pause <=========
			g.setColor(Color.BLACK);
			g.drawRect(449, 292, 66, 22);
			g.setColor(new Color(getPaintColors(gui.comboBox13).getRed(), getPaintColors(gui.comboBox13).getGreen(), getPaintColors(gui.comboBox13).getBlue(), 127));
			g.fillRect(450, 293, 65, 21);
			g.fillRect(450, 293, 65, 11);
			g.setColor(getPaintColors(gui.comboBox12));
			g.setFont(constants.textFont);
			g.drawString(paused, 466, 308);
			// ==========> TAB #1 <==========
			if (tabOne || fullPaint) {
				g.setColor(Color.BLACK);
				g.drawRect(4, 3, 131, 68);
				g.setColor(new Color(getPaintColors(gui.comboBox13).getRed(), getPaintColors(gui.comboBox13).getGreen(), getPaintColors(gui.comboBox13).getBlue(), 127));
				g.fillRect(5, 4, 130, 67);
				g.setColor(getPaintColors(gui.comboBox12));
				g.setFont(constants.textFont);
				g.drawString("Runtime: " + getRuntime(), 10, 50);
				g.drawString("TTL: " + daysTNL + ":" + hoursTNL + ":" + minsTNL
						+ ":" + secTNL, 10, 65);
			} else {
				g.setColor(Color.BLACK);
				g.drawRect(4, 3, 131, 40);
				g.setColor(new Color(getPaintColors(gui.comboBox13).getRed(), getPaintColors(gui.comboBox13).getGreen(), getPaintColors(gui.comboBox13).getBlue(), 127));
				g.fillRect(5, 4, 130, 39);
			}
			g.setColor(new Color(getPaintColors(gui.comboBox13).getRed(), getPaintColors(gui.comboBox13).getGreen(), getPaintColors(gui.comboBox13).getBlue(), 127));
			g.fillRect(5, 4, 130, 20);
			g.setColor(getPaintColors(gui.comboBox12));
			g.setFont(constants.titleFont);
			g.drawString("UFletch", 12, 30);

			// ==========> TAB #2 <==========
			if (tabTwo || fullPaint) {
				g.setColor(Color.BLACK);
				g.drawRect(135, 3, 131, 68);
				g.setColor(new Color(getPaintColors(gui.comboBox13).getRed(), getPaintColors(gui.comboBox13).getGreen(), getPaintColors(gui.comboBox13).getBlue(), 127));
				g.fillRect(136, 4, 130, 67);
				g.setColor(getPaintColors(gui.comboBox12));
				g.setFont(constants.textFont);
				g.drawString("XP/h: "
						+ getHourly(skills.getCurrentExp(Skills.FLETCHING)
								- startXP), 141, 50);
				g.drawString("XPTL: "
						+ skills.getExpToNextLevel(Skills.FLETCHING), 141, 65);
			} else {
				g.setColor(Color.BLACK);
				g.drawRect(135, 3, 131, 40);
				g.setColor(new Color(getPaintColors(gui.comboBox13).getRed(), getPaintColors(gui.comboBox13).getGreen(), getPaintColors(gui.comboBox13).getBlue(), 127));
				g.fillRect(136, 4, 130, 39);
			}

			g.setColor(new Color(getPaintColors(gui.comboBox13).getRed(), getPaintColors(gui.comboBox13).getGreen(), getPaintColors(gui.comboBox13).getBlue(), 127));
			g.fillRect(136, 4, 130, 20);
			g.setColor(getPaintColors(gui.comboBox12));
			g.setFont(constants.textFont);
			g.drawImage(fletchIcon, 142, 11, null);
			g.drawString("Level: " + skills.getRealLevel(Skills.FLETCHING)
					+ " / " + startLevel, 170, 20);
			g.drawString("XP Gained: " + xpGained, 170, 35);
			// ==========> TAB #3 <==========

			if (tabThree || fullPaint) {
				g.setColor(Color.BLACK);
				g.drawRect(265, 3, 131, 68);
				g.setColor(new Color(getPaintColors(gui.comboBox13).getRed(), getPaintColors(gui.comboBox13).getGreen(), getPaintColors(gui.comboBox13).getBlue(), 127));
				g.fillRect(266, 4, 130, 67);
			} else {
				g.setColor(Color.BLACK);
				g.drawRect(265, 3, 131, 40);
				g.setColor(new Color(getPaintColors(gui.comboBox13).getRed(), getPaintColors(gui.comboBox13).getGreen(), getPaintColors(gui.comboBox13).getBlue(), 127));
				g.fillRect(266, 4, 130, 39);
			}
			g.setColor(new Color(getPaintColors(gui.comboBox13).getRed(), getPaintColors(gui.comboBox13).getGreen(), getPaintColors(gui.comboBox13).getBlue(), 127));
			g.fillRect(266, 4, 130, 20);
			g.setColor(getPaintColors(gui.comboBox12));
			g.setFont(constants.textFont);
			g.drawImage(bow, 270, 11, null);
			if (getMethod() == 2) {
				g.drawString("Strung: " + strung, 305, 20);
				g.drawString("Strung/H: " + getHourly(strung), 305, 35);
			} else {
				g.drawString("Fletched: " + fletched, 305, 20);
				g.drawString("Fletched/H: " + getHourly(fletched), 305, 35);
			}

			// =========> PROGRESS <=========
			final int lengthGreen = skills.getPercentToNextLevel(Skills.FLETCHING) * 2;
			final int lengthRed = 200 - lengthGreen;
			GradientPaint green;
			green = new GradientPaint(316 + 22F, 316 + 22F, new Color(90, 168, 51, 255), 316 + 22F, 316F, new Color(134, 205, 99, 255));
			g.setPaint(green);
			g.fillRect(76, 317, lengthGreen, 21);
			GradientPaint red;
			red = new GradientPaint(316 + 22F, 316 + 22F, new Color(181, 55, 55, 255), 316 + 22F, 316F, new Color(207, 104, 103, 255));
			g.setPaint(red);
			g.fillRect((275 - lengthRed), 317, lengthRed, 21);
			final String progress = skills.getPercentToNextLevel(Skills.FLETCHING)
					+ "% to "
					+ (skills.getCurrentLevel(Skills.FLETCHING) + 1)
					+ " Fletching.";
			final int stringW = g.getFontMetrics().stringWidth(progress);
			final int positionW = (200 - stringW) / 2;
			final int stringH = g.getFontMetrics().getHeight();
			final int positionH = (22 - stringH) / 2;
			g.setColor(getPaintColors(gui.comboBox12));
			g.drawString(progress, 76 + positionW, 327 + positionH);
			g.setColor(Color.BLACK);
			g.drawRect(75, 316, 200, 22);
			final Rectangle clip = new Rectangle(76, 317, lengthGreen, 21);
			g.setClip(clip);
			g.drawString(progress, 76 + positionW, 327 + positionH);
			g.setClip(null);

			// ==========> MOUSE! <==========
			final Point m = mouse.getLocation();
			g.setColor(getPaintColors(gui.comboBox12));
			if (gui.checkBox11.isSelected() && !gui.checkBox17.isSelected()) {
				final Point clientCursor = mouse.getLocation();
				while (!mousePath.isEmpty() && mousePath.peek().isUp()) {
					mousePath.remove();
				}
				final MousePathPoint mpp = new MousePathPoint(clientCursor.x, clientCursor.y, 3000);
				if (mousePath.isEmpty() || !mousePath.getLast().equals(mpp)) {
					mousePath.add(mpp);
				}
				MousePathPoint lastPoint = null;
				for (final MousePathPoint a : mousePath) {
					if (lastPoint != null) {
						g.setColor(new Color(getPaintColors(gui.comboBox14).getRed(), getPaintColors(gui.comboBox14).getGreen(), getPaintColors(gui.comboBox14).getBlue(), a.toColor(a.toTime(256))));
						g.drawLine(a.x, a.y, lastPoint.x, lastPoint.y);
					}
					lastPoint = a;
				}
			} else if (gui.checkBox11.isSelected()
					&& gui.checkBox17.isSelected()) {
				while (!mouseCirclePath.isEmpty()
						&& mouseCirclePath.peek().isUp()) {
					mouseCirclePath.remove();
				}
				final MouseCirclePathPoint mp = new MouseCirclePathPoint(m.x, m.y, 3000);
				if (mouseCirclePath.isEmpty()
						|| !mouseCirclePath.getLast().equals(mp)) {
					mouseCirclePath.add(mp);
				}
				MouseCirclePathPoint lastPoint = null;
				for (final MouseCirclePathPoint a : mouseCirclePath) {
					if (lastPoint != null) {
						g.setColor(new Color(getPaintColors(gui.comboBox10).getRed(), getPaintColors(gui.comboBox10).getGreen(), getPaintColors(gui.comboBox10).getBlue(), a.toColor(a.toTime(256))));
						g.fillOval(a.x - a.toColor(a.toTime(10)) / 2, a.y
								- a.toColor(a.toTime(10)) / 2, a.toColor(a.toTime(10)), a.toColor(a.toTime(10)));
						g.setColor(new Color(0, 0, 0, a.toColor(a.toTime(256))));
						g.drawOval(a.x - a.toColor(a.toTime(10)) / 2, a.y
								- a.toColor(a.toTime(10)) / 2, a.toColor(a.toTime(10)), a.toColor(a.toTime(10)));
					}
					lastPoint = a;
				}
			}

			if (gui.checkBox13.isSelected() && !gui.checkBox18.isSelected()) {
				while (!mousePath2.isEmpty() && mousePath2.peek().isUp()) {
					mousePath2.remove();
				}
				final MousePathPoint2 mpp = new MousePathPoint2(p.x, p.y, 3000);
				if (mousePath2.isEmpty() || !mousePath2.getLast().equals(mpp)) {
					mousePath2.add(mpp);
				}
				MousePathPoint2 lastPoint = null;
				for (final MousePathPoint2 z : mousePath2) {
					if (lastPoint != null) {
						g.setColor(new Color(getPaintColors(gui.comboBox14).getRed(), getPaintColors(gui.comboBox14).getGreen(), getPaintColors(gui.comboBox14).getBlue(), z.toColor(z.toTime(256))));
						g.drawLine(z.x, z.y, lastPoint.x, lastPoint.y);
					}
					lastPoint = z;
				}
			} else if (gui.checkBox13.isSelected()
					&& gui.checkBox18.isSelected()) {
				while (!mouseCirclePath2.isEmpty()
						&& mouseCirclePath2.peek().isUp()) {
					mouseCirclePath2.remove();
				}
				final MouseCirclePathPoint2 mp = new MouseCirclePathPoint2(p.x, p.y, 3000);
				if (mouseCirclePath2.isEmpty()
						|| !mouseCirclePath2.getLast().equals(mp)) {
					mouseCirclePath2.add(mp);
				}
				MouseCirclePathPoint2 lastPoint = null;
				for (final MouseCirclePathPoint2 a : mouseCirclePath2) {
					if (lastPoint != null) {
						g.setColor(new Color(getPaintColors(gui.comboBox14).getRed(), getPaintColors(gui.comboBox14).getGreen(), getPaintColors(gui.comboBox14).getBlue(), a.toColor(a.toTime(256))));
						g.fillOval(a.x - a.toColor(a.toTime(10)) / 2, a.y
								- a.toColor(a.toTime(10)) / 2, a.toColor(a.toTime(10)), a.toColor(a.toTime(10)));
						g.setColor(new Color(0, 0, 0, a.toColor(a.toTime(256))));
						g.drawOval(a.x - a.toColor(a.toTime(10)) / 2, a.y
								- a.toColor(a.toTime(10)) / 2, a.toColor(a.toTime(10)), a.toColor(a.toTime(10)));
					}
					lastPoint = a;
				}
			}
			if (gui.checkBox12.isSelected()) {
				final int gW = game.getWidth();
				final int gH = game.getHeight();
				final Point localPoint = mouse.getLocation();
				g.setColor(getPaintColors(gui.comboBox11));
				g.drawLine(0, localPoint.y, gW, localPoint.y);
				g.drawLine(localPoint.x, 0, localPoint.x, gH);
			}
			if (gui.checkBox14.isSelected()) {
				final int gW = game.getWidth();
				final int gH = game.getHeight();
				g.setColor(getPaintColors(gui.comboBox15));
				g.drawLine(0, p.y, gW, p.y);
				g.drawLine(p.x, 0, p.x, gH);
			}
			final Graphics2D g1 = (Graphics2D) render.create();
			final Graphics2D g2 = (Graphics2D) render.create();
			final Graphics2D g3 = (Graphics2D) render.create();
			final Graphics2D g4 = (Graphics2D) render.create();
			final Graphics2D g5 = (Graphics2D) render.create();
			g1.setRenderingHints(rh);
			g2.setRenderingHints(rh);
			g3.setRenderingHints(rh);
			g4.setRenderingHints(rh);
			g5.setRenderingHints(rh);
			g1.setPaint(Color.CYAN);
			g2.setPaint(Color.WHITE);
			g3.setPaint(Color.GREEN);
			g4.setPaint(Color.BLACK);
			g5.setPaint(Color.MAGENTA);
			g1.rotate(getRot(Integer.MAX_VALUE, false, 9D), m.x, m.y);
			g2.rotate(getRot(Integer.MAX_VALUE, true, 8D), m.x, m.y);
			g3.rotate(getRot(Integer.MAX_VALUE, false, 7D), m.x, m.y);
			g4.rotate(getRot(Integer.MAX_VALUE, true, 6D), m.x, m.y);
			g5.rotate(getRot(Integer.MAX_VALUE, false, 5D), m.x, m.y);
			g1.drawLine(m.x, m.y - 5, m.x, m.y + 5);
			g1.drawLine(m.x - 5, m.y, m.x + 5, m.y);
			g2.drawArc(m.x - 6, m.y - 6, 12, 12, 0, 90);
			g2.drawArc(m.x - 6, m.y - 6, 12, 12, 180, 90);
			g2.drawArc(m.x - 7, m.y - 7, 14, 14, 0, 90);
			g2.drawArc(m.x - 7, m.y - 7, 14, 14, 180, 90);
			g3.drawArc(m.x - 9, m.y - 9, 18, 18, 0, 90);
			g3.drawArc(m.x - 9, m.y - 9, 18, 18, 180, 90);
			g3.drawArc(m.x - 10, m.y - 10, 20, 20, 0, 90);
			g3.drawArc(m.x - 10, m.y - 10, 20, 20, 180, 90);
			g4.drawArc(m.x - 12, m.y - 12, 24, 24, 0, 90);
			g4.drawArc(m.x - 13, m.y - 13, 26, 26, 0, 90);
			g5.drawArc(m.x - 15, m.y - 15, 30, 30, 180, 90);
			g5.drawArc(m.x - 14, m.y - 14, 28, 28, 180, 90);

		}
	}

	public boolean onStart() {
		if (!manageLogin()) {
			JOptionPane.showMessageDialog(null, "Please completely login!");
			return false;
		}
		getExtraImages();
		createGui();
		while (gui.isVisible()) {
			sleep(100);
		}
		getExtraInfo();
		return game.isLoggedIn() && !gui.isVisible();
	}

	private boolean openBank() {
		return bank.open();
	}

	private void pauseScript() {
		if (pause) {
			log("Pausing...");
			status = "Paused";
			while (pause) {
				sleep(400, 600);
			}
		}
	}

	private Image pic() {
		return getImage("blank.png", true, "http://i.imgur.com/7nw5i.png");
	}

	private void saveSettings() {
		final Properties p = new Properties();
		p.setProperty("Method", (String) gui.comboBox1.getSelectedItem());
		p.setProperty("LogType", (String) gui.comboBox2.getSelectedItem());
		p.setProperty("BowType", (String) gui.comboBox3.getSelectedItem());
		p.setProperty("Knife", (String) gui.comboBox4.getSelectedItem());
		p.setProperty("AxeType", (String) gui.comboBox5.getSelectedItem());
		p.setProperty("Color1", (String) gui.comboBox12.getSelectedItem());
		p.setProperty("Color2", (String) gui.comboBox13.getSelectedItem());
		p.setProperty("Color3", (String) gui.comboBox8.getSelectedItem());
		p.setProperty("Color4", (String) gui.comboBox9.getSelectedItem());
		p.setProperty("Color5", (String) gui.comboBox10.getSelectedItem());
		p.setProperty("Color6", (String) gui.comboBox11.getSelectedItem());
		p.setProperty("Color7", (String) gui.comboBox14.getSelectedItem());
		p.setProperty("Color8", (String) gui.comboBox15.getSelectedItem());
		p.setProperty("Amount", (String) gui.textField1.getText());
		p.setProperty("Name", (String) gui.textField2.getText());
		p.setProperty("WhenDone", getValue(gui.checkBox1.isSelected()));
		p.setProperty("UponLvl", getValue(gui.checkBox2.isSelected()));
		p.setProperty("Getting99", getValue(gui.checkBox3.isSelected()));
		p.setProperty("Before99", getValue(gui.checkBox5.isSelected()));
		p.setProperty("Save", getValue(gui.checkBox6.isSelected()));
		p.setProperty("Load", getValue(gui.checkBox7.isSelected()));
		p.setProperty("Paint", getValue(gui.checkBox4.isSelected()));
		p.setProperty("Chat", getValue(gui.checkBox8.isSelected()));
		p.setProperty("Inventory", getValue(gui.checkBox9.isSelected()));
		p.setProperty("Bar", getValue(gui.checkBox10.isSelected()));
		p.setProperty("BotLine", getValue(gui.checkBox11.isSelected()));
		p.setProperty("BotCross", getValue(gui.checkBox12.isSelected()));
		p.setProperty("UserLine", getValue(gui.checkBox13.isSelected()));
		p.setProperty("UserCross", getValue(gui.checkBox14.isSelected()));
		p.setProperty("BotCircle", getValue(gui.checkBox17.isSelected()));
		p.setProperty("UserCircle", getValue(gui.checkBox18.isSelected()));
		p.setProperty("Message", getValue(gui.checkBox15.isSelected()));
		p.setProperty("Beep", getValue(gui.checkBox16.isSelected()));
		p.setProperty("Speed", String.valueOf(gui.slider1.getValue()));
		try {
			p.store(new FileOutputStream(getCacheDirectory() + File.separator
					+ "UFletch." + account.getName()), "UFletch settings");
		} catch (final IOException e) {
		}
	}

	private void stocksWithLimbs() {
		amount = Integer.parseInt(gui.textField1.getText());
		if (!inventory.contains(getUnstrungId())
				|| !inventory.contains(getObjectId()) && amount == 0
				&& !isBusy() && !interfaces.get(905).isValid()) {
			withdrawCBowU();
			sleep(random(200, 250));
		} else if (!inventory.contains(getUnstrungId())
				|| !inventory.contains(getObjectId()) && fletched <= amount
				&& !isBusy() && !interfaces.get(905).isValid()) {
			withdrawCBowU();
			sleep(random(200, 250));
		} else if (fletched >= amount && amount != 0) {
			log("Made the chosen amount of c'bows");
			stopScript();
		}
		if (inventory.contains(getUnstrungId())
				&& inventory.contains(getObjectId()) && amount == 0
				&& !isBusy()) {
			createCBowU();
			sleep(random(200, 250));
		} else if (inventory.contains(getUnstrungId())
				&& inventory.contains(getObjectId()) && fletched <= amount
				&& !isBusy()) {
			createCBowU();
			sleep(random(200, 250));
		} else if (fletched >= amount && amount != 0) {
			log("Made the chosen amount of c'bows");
			stopScript();
		}

		if (isBusy() && !interfaces.get(740).isValid()
				&& inventory.contains(getBSId())
				&& inventory.contains(getUnstrungId())) {
			antiban();
			sleep(random(400, 500));
		}
		clickContinue();
		if (gui.checkBox5.isSelected()) {
			checkfor99();
		}
	}

	private void string() {
		amount = Integer.parseInt(gui.textField1.getText());
		if (!inventory.contains(getUnstrungId())
				|| !inventory.contains(getBSId()) && amount == 0 && !isBusy()
				&& !interfaces.get(905).isValid()) {
			withdrawStrings();
			sleep(random(200, 250));
		} else if (!inventory.contains(getUnstrungId())
				|| !inventory.contains(getBSId()) && strung <= amount
				&& !isBusy() && !interfaces.get(905).isValid()) {
			withdrawStrings();
			sleep(random(200, 250));
		} else if (strung >= amount && amount != 0) {
			log("strung the chosen amount of bows!");
			stopScript();
		}
		if (inventory.contains(getUnstrungId())
				&& inventory.contains(getBSId()) && amount == 0 && !isBusy()) {
			stringBows();
			sleep(random(200, 250));
		} else if (inventory.contains(getUnstrungId())
				&& inventory.contains(getBSId()) && strung <= amount
				&& !isBusy()) {
			stringBows();
			sleep(random(200, 250));
		} else if (strung >= amount && amount != 0) {
			log("strung the chosen amount of bows!");
			stopScript();
		}

		if (isBusy() && !interfaces.get(740).isValid()
				&& inventory.contains(getBSId())
				&& inventory.contains(getUnstrungId())) {
			antiban();
			sleep(random(400, 500));
		}
		clickContinue();
		if (gui.checkBox5.isSelected()) {
			checkfor99();
		}
	}

	private void stringBows() {
		status = "Stringing: Bows";
		try {
			if (bank.isOpen()) {
				bank.close();
			}
			if (!interfaces.get(905).isValid() && !isBusy()
					&& inventory.contains(getBSId())
					&& inventory.contains(getUnstrungId())) {
				if (random(1, 2) == 1) {
					inventory.getItem(getUnstrungId()).doClick(true);
					sleep(200, 400);
					inventory.getItem(getBSId()).doClick(true);
					sleep(random(200, 400));
				} else {
					inventory.getItem(getBSId()).doClick(true);
					sleep(200, 400);
					inventory.getItem(getUnstrungId()).doClick(true);
					sleep(random(200, 400));
				}
			}
			sleep(50, 100);
			mouse.moveRandomly(150, 500);
			sleep(400, 450);
			if (interfaces.get(905).isValid()) {
				sleep(200, 250);
				interfaces.get(905).getComponent(14).doAction("Make All");
				sleep(50, 200);
			}
		} catch (final Exception e) {
		}
	}

	private void stringCBow() {
		amount = Integer.parseInt(gui.textField1.getText());
		if (!inventory.contains(getUnstrungId())
				|| !inventory.contains(getBSId()) && amount == 0 && !isBusy()
				&& !interfaces.get(905).isValid()) {
			withdrawCBowStrings();
			sleep(random(200, 250));
		} else if (!inventory.contains(getUnstrungId())
				|| !inventory.contains(getBSId()) && strung <= amount
				&& !isBusy() && !interfaces.get(905).isValid()) {
			withdrawCBowStrings();
			sleep(random(200, 250));
		} else if (strung >= amount && amount != 0) {
			log("strung the chosen amount of bows!");
			stopScript();
		}
		if (inventory.contains(getUnstrungId())
				&& inventory.contains(getBSId()) && amount == 0 && !isBusy()) {
			stringBows();
			sleep(random(200, 250));
		} else if (inventory.contains(getUnstrungId())
				&& inventory.contains(getBSId()) && strung <= amount
				&& !isBusy()) {
			stringBows();
			sleep(random(200, 250));
		} else if (strung >= amount && amount != 0) {
			log("strung the chosen amount of bows!");
			stopScript();
		}

		if (isBusy() && !interfaces.get(740).isValid()
				&& inventory.contains(getBSId())
				&& inventory.contains(getUnstrungId())) {
			antiban();
			sleep(random(400, 500));
		}
		clickContinue();
		if (gui.checkBox5.isSelected()) {
			checkfor99();
		}
	}

	private void tipArrows() {
		status = "Making: Arrows";
		try {
			if (bank.isOpen()) {
				bank.close();
			}
			sleep(50, 100);
			if (!interfaces.get(905).isValid() && !isBusy()) {
				if (random(1, 2) == 1) {
					inventory.getItem(constants.FEATHER_SHAFT_ID).doClick(true);
					sleep(200, 400);
					inventory.getItem(getObjectId()).doClick(true);
				} else {
					inventory.getItem(getObjectId()).doClick(true);
					sleep(200, 400);
					inventory.getItem(constants.FEATHER_SHAFT_ID).doClick(true);
				}
			}
			sleep(50, 100);
			mouse.moveRandomly(150, 500);
			sleep(400, 450);
			if (interfaces.get(905).isValid()) {
				interfaces.getComponent(905, 14).doClick();
			}
			sleep(50, 200);
		} catch (final Exception e) {
		}
	}

	private void updateSignature() {
		if (connection) {
			try {
				final long xpGained = skills.getCurrentExp(Skills.FLETCHING)
						- startXP;
				long millis = System.currentTimeMillis() - startTime;
				final long days = millis / (1000 * 60 * 60 * 24);
				millis -= days * 1000 * 60 * 60 * 24;
				final long hours = millis / (1000 * 60 * 60);
				millis -= hours * 1000 * 60 * 60;
				final long minutes = millis / (1000 * 60);
				millis -= minutes * 1000 * 60;
				final long seconds = millis / 1000;
				URL url;
				URLConnection urlConn;
				url = new URL("http://www.universalscripts.org/ufletch/UFletch_submit.php");
				urlConn = url.openConnection();
				urlConn.setRequestProperty("User-Agent", "UFletchAgent");
				urlConn.setDoInput(true);
				urlConn.setDoOutput(true);
				urlConn.setUseCaches(false);
				urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				String content = "";
				final String[] stats = { "auth", "secs", "mins", "hours",
						"days", "fletched", "strung", "expgained" };
				final Object[] data = { gui.textField2.getText(), seconds,
						minutes, hours, days, fletched, strung, xpGained };
				for (int i = 0; i < stats.length; i++) {
					content += stats[i] + "=" + data[i] + "&";
				}
				content = content.substring(0, content.length() - 1);
				final OutputStreamWriter wr = new OutputStreamWriter(urlConn.getOutputStream());
				wr.write(content);
				wr.flush();
				final BufferedReader rd = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
				String line;
				while ((line = rd.readLine()) != null) {
					log(line);
				}
				wr.close();
				rd.close();
			} catch (final Exception e) {
			}
		}
	}

	public void walk() {
		status = "Walking";
		if (objects.getNearest(getTreeId()) != null
				&& getMyPlayer().getAnimation() == -1
				&& !getMyPlayer().isMoving()) {
			if (objects.getNearest(getTreeId()) != null && !isBusy()) {
				camera.setPitch(random(90, 100));
				walking.walkTileMM(objects.getNearest(getTreeId()).getLocation());
			}
			sleep(500);
			while (getMyPlayer().isMoving() || isBusy()) {
				sleep(250);
			}
		} else if (objects.getNearest(getTreeId()) == null) {
			log("Tree out of reach, please start closer to the tree!");
			stopScript();
		}
	}

	private void withdrawCBowStrings() {
		status = "Banking: C'Bows";
		try {
			if (!inventory.contains(getBSId())
					|| !inventory.contains(getUnstrungId())) {
				openBank();
				if (bank.isOpen()) {
					if (inventory.getCount() > 0) {
						bank.depositAll();
						sleep(50);
						for (int i = 0; i < 20; i++) {
							sleep(25);
							if (inventory.getCount() == 0) {
								break;
							}
						}
					}
					if (inventory.getCount(getUnstrungId()) != 14) {
						if (inventory.getCount(getUnstrungId()) > 0) {
							bank.deposit(getUnstrungId(), 0);
						}
						if (bank.getCount(getUnstrungId()) > 0) {
							bank.withdraw(getUnstrungId(), 14);
							sleep(100);
							for (int i = 0; i < 25; i++) {
								sleep(200);
								if (inventory.contains(getUnstrungId())) {
									break;
								}
							}
						} else if (bank.isOpen()) {
							if (bank.getCount(getUnstrungId()) == 0) {
								log("No more c'bows (u) in bank.");
								stopScript(true);
							}
						}
					}
					sleep(100);
					if (inventory.getCount(getBSId()) != 14) {
						if (inventory.getCount(getBSId()) > 0) {
							bank.deposit(getUnstrungId(), 0);
						}
						if (bank.getCount(getBSId()) > 0) {
							bank.withdraw(getBSId(), 14);
							sleep(100);
							for (int i = 0; i < 25; i++) {
								sleep(200);
								if (inventory.contains(getBSId())) {
									break;
								}
							}
						} else if (bank.isOpen()) {
							if (bank.getCount(getBSId()) == 0) {
								log("No more Bow String in bank.");
								stopScript(true);
							}
						}
					}
				}
			}
		} catch (final Exception e) {
		}
	}

	private void withdrawCBowU() {
		status = "Banking: C'bows";
		try {
			if (!inventory.contains(getObjectId())
					|| !inventory.contains(getUnstrungId())) {
				openBank();
				if (bank.isOpen()) {
					if (inventory.getCount() > 0) {
						bank.depositAll();
						sleep(50);
						for (int i = 0; i < 20; i++) {
							sleep(25);
							if (inventory.getCount() == 0) {
								break;
							}
						}
					}
					if (inventory.getCount(getUnstrungId()) != 14) {
						if (inventory.getCount(getUnstrungId()) > 0) {
							bank.deposit(getUnstrungId(), 0);
						}
						if (bank.getCount(getUnstrungId()) > 0) {
							bank.withdraw(getUnstrungId(), 14);
							sleep(100);
							for (int i = 0; i < 25; i++) {
								sleep(200);
								if (inventory.contains(getUnstrungId())) {
									break;
								}
							}
						} else if (bank.isOpen()) {
							if (bank.getCount(getUnstrungId()) == 0) {
								log("No more bows (u) in bank.");
								stopScript(true);
							}
						}
					}
					sleep(100);
					if (inventory.getCount(getObjectId()) != 14) {
						if (inventory.getCount(getObjectId()) > 0) {
							bank.deposit(getUnstrungId(), 0);
						}
						if (bank.getCount(getObjectId()) > 0) {
							bank.withdraw(getObjectId(), 14);
							sleep(100);
							for (int i = 0; i < 25; i++) {
								sleep(200);
								if (inventory.contains(getObjectId())) {
									break;
								}
							}
						} else if (bank.isOpen()) {
							if (bank.getCount(getObjectId()) == 0) {
								log("No more bows (u) in bank.");
								stopScript(true);
							}
						}
					}
				}
			}
		} catch (final Exception e) {
		}
	}

	private void withdrawKnife() {
		status = "Banking: Knife";
		try {
			sleep(10, 20);
			openBank();
			sleep(200, 400);
			if (bank.isOpen()) {
				sleep(100, 250);
				if (!inventory.contains(getKnifeId())) {
					if (inventory.getCount() > 0) {
						bank.depositAll();
					}
					sleep(100, 150);
					if (getMethod() != 3) {
						if (bank.getItem(getKnifeId()) == null) {
							log("could not find a knife, logging out!");
							stopScript();
						}
					}
					bank.withdraw(getKnifeId(), 1);
					sleep(50, 100);
				}
			}
		} catch (final Exception e) {
		}
	}

	private void withdrawLogs() {
		status = "Banking: Logs";
		try {
			sleep(10, 20);
			if (openBank()) {
				if (bank.isOpen()) {
					sleep(200, 400);
					if (bank.depositAllExcept(getKnifeId())) {
						for (int i = 0; i < 10; i++) {
							sleep(30);
							if (inventory.getCount() == 0) {
								break;
							}
						}
					}
					if (bank.getItem(getLogId()) == null) {
						if (fletchAndString) {
							gui.comboBox1.setSelectedItem("String");
						} else {
							log("could not find any Logs, logging out!");
							stopScript();
						}
					}
					bank.withdraw(getLogId(), 0);
					sleep(50, 100);
					for (int i = 0; i < 25; i++) {
						sleep(50);
						if (inventory.contains(getLogId())) {
							break;
						}
					}
				}
			}
		} catch (final Exception e) {
		}
	}

	private void withdrawShafts() {
		status = "Banking: Shafts";
		try {
			sleep(10, 20);
			if (openBank()) {
				if (getLogId() != 1511) {
					log("Please select normal logs!");
					stopScript();
				} else if (getLogId() == 1511 && bank.isOpen()) {
					sleep(200, 400);
					bank.depositAllExcept(getKnifeId());
					sleep(100, 150);
					if (bank.getItem(getLogId()) == null) {
						log("Out of logs, Logging out!");
						stopScript();
					}
					bank.withdraw(getLogId(), 0);
					for (int i = 0; i < 200; i++) {
						sleep(50);
						if (inventory.contains(getLogId())) {
							bank.close();
							break;
						}
					}
					sleep(30, 50);
				}
			}
		} catch (final Exception e) {
		}
	}

	private void withdrawStocks() {
		status = "Banking: Stocks";
		try {
			sleep(10, 20);
			if (openBank()) {
				if (getLogId() == 1513) {
					log("Please select a different log!");
					stopScript();
				} else if (getLogId() != 1513 && bank.isOpen()) {
					sleep(200, 400);
					bank.depositAllExcept(getKnifeId());
					sleep(100, 150);
					if (bank.getItem(getLogId()) == null) {
						log("Out of logs, Logging out!");
						stopScript();
					}
					bank.withdraw(getLogId(), 0);
					for (int i = 0; i < 200; i++) {
						sleep(50);
						if (inventory.contains(getLogId())) {
							bank.close();
							break;
						}
					}
					sleep(30, 50);
				}
			}
		} catch (final Exception e) {
		}
	}

	private void withdrawStrings() {
		status = "Banking: Stringing";
		try {
			if (!inventory.contains(getBSId())
					|| !inventory.contains(getUnstrungId())) {
				openBank();
				if (bank.isOpen()) {
					if (inventory.getCount() > 0) {
						bank.depositAll();
						sleep(50);
						for (int i = 0; i < 20; i++) {
							sleep(25);
							if (inventory.getCount() == 0) {
								break;
							}
						}
					}
					if (inventory.getCount(getUnstrungId()) != 14) {
						if (inventory.getCount(getUnstrungId()) > 0) {
							bank.deposit(getUnstrungId(), 0);
						}
						if (bank.getCount(getUnstrungId()) > 0) {
							bank.withdraw(getUnstrungId(), 14);
							sleep(100);
							for (int i = 0; i < 25; i++) {
								sleep(75);
								if (inventory.contains(getUnstrungId())) {
									break;
								}
							}
						} else if (bank.isOpen()) {
							if (bank.getCount(getUnstrungId()) == 0) {
								log("No more bows (u) in bank.");
								stopScript(true);
							}
						}
					}
					sleep(100);
					if (inventory.getCount(getBSId()) != 14) {
						if (inventory.getCount(getBSId()) > 0) {
							bank.deposit(getUnstrungId(), 0);
						}
						if (bank.getCount(getBSId()) > 0) {
							bank.withdraw(getBSId(), 14);
							sleep(100);
							for (int i = 0; i < 25; i++) {
								sleep(75);
								if (inventory.contains(getBSId())) {
									break;
								}
							}
						} else if (bank.isOpen()) {
							if (bank.getCount(getBSId()) == 0) {
								log("No more bows (u) in bank.");
								stopScript(true);
							}
						}
					}
				}
			}
		} catch (final Exception e) {
		}
	}
}
