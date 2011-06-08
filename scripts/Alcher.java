import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.rsbot.event.events.MessageEvent;
import org.rsbot.event.listeners.MessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.gui.BotGUI;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Equipment;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Magic;
import org.rsbot.script.methods.Methods;
import org.rsbot.script.methods.Skills;
import org.rsbot.script.wrappers.RSItem;

@ScriptManifest(authors = { "iPhQ" }, keywords = { "Magic" }, name = "iPhQ's Alcher", version = 2.1, description = "Alch it baby - Now works with the new version of powerbot.", website = "http://www.powerbot.org/vb/showthread.php?t=607582")
public class Alcher extends Script implements PaintListener, MouseListener,
		MessageListener {

	class AntiBan implements Runnable {

		public int ID;

		public AntiBan(final int number) {
			ID = number;
			new Thread(this).start();
		}

		@Override
		public void run() {
			switch (ID) {
			case 20:
				log("Performing antiban - rotate camera");
				camera.setAngle(Methods.random(98, 278));
				log("Done");
				break;
			}
		}

	}

	class NewFrame extends java.awt.Frame {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		// Variables declaration - do not modify
		private javax.swing.ButtonGroup afkButtonGroup;

		private javax.swing.JButton jButton1;

		private javax.swing.JButton jButton2;

		private javax.swing.JLabel jLabel1;

		private javax.swing.JLabel jLabel2;

		private javax.swing.JLabel jLabel3;
		private javax.swing.JLabel jLabel4;
		private javax.swing.JLabel jLabel5;
		private javax.swing.JList jList1;
		private javax.swing.JPanel jPanel1;
		private javax.swing.JPanel jPanel2;
		private javax.swing.JPanel jPanel3;
		private javax.swing.JPanel jPanel4;
		private javax.swing.JPanel jPanel5;
		private javax.swing.JRadioButton jRadioButton1;
		private javax.swing.JRadioButton jRadioButton2;
		private javax.swing.JScrollPane jScrollPane1;
		private javax.swing.JTabbedPane jTabbedPane1;
		private javax.swing.JTabbedPane jTabbedPane2;
		private javax.swing.JTextField jTextField1;
		private javax.swing.JTextField jTextField2;
		private DefaultListModel model;

		public NewFrame() {
			initComponents();
		}

		/** Exit the Application */
		private void exitForm(final java.awt.event.WindowEvent evt) {
			stopScript();
			setVisible(false);
		}

		public javax.swing.JList getjList1() {
			return jList1;
		}

		public DefaultListModel getjList1Model() {
			return model;
		}

		// End of variables declaration
		private void initComponents() {
			model = new DefaultListModel();
			afkButtonGroup = new javax.swing.ButtonGroup();
			jPanel1 = new javax.swing.JPanel();
			jLabel1 = new javax.swing.JLabel();
			jPanel2 = new javax.swing.JPanel();
			jLabel2 = new javax.swing.JLabel();
			jScrollPane1 = new javax.swing.JScrollPane();
			jList1 = new javax.swing.JList(model);
			jButton1 = new javax.swing.JButton();
			jTabbedPane1 = new javax.swing.JTabbedPane();
			jPanel3 = new javax.swing.JPanel();
			jTabbedPane2 = new javax.swing.JTabbedPane();
			jPanel5 = new javax.swing.JPanel();
			jPanel4 = new javax.swing.JPanel();
			jRadioButton1 = new javax.swing.JRadioButton();
			jRadioButton2 = new javax.swing.JRadioButton();
			jLabel3 = new javax.swing.JLabel();
			jLabel4 = new javax.swing.JLabel();
			jLabel5 = new javax.swing.JLabel();
			jTextField1 = new javax.swing.JTextField();
			jTextField2 = new javax.swing.JTextField();
			jButton2 = new javax.swing.JButton();
			addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(final java.awt.event.WindowEvent evt) {
					exitForm(evt);
				}
			});
			setTitle("iPhQ's Alcher - Settings");
			jPanel1.setBackground(new java.awt.Color(0, 102, 102));
			jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36));
			jLabel1.setForeground(new java.awt.Color(204, 204, 0));
			jLabel1.setText("iPhQ's Alcher - Settings");
			jPanel2.setBackground(new java.awt.Color(0, 102, 102));
			jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Items in your Inventory", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(255, 255, 0))); // NOI18N

			jLabel2.setForeground(new java.awt.Color(255, 255, 0));
			jLabel2.setText("<html><body>Select item(s) to alch from the <br/> list below (you must be logged in for this to work) :</body></body>");

			jScrollPane1.setViewportView(jList1);

			jButton1.setText("Refresh the List");
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(final java.awt.event.ActionEvent evt) {
					jButton1ActionPerformed(evt);
				}
			});

			final javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
			jPanel2.setLayout(jPanel2Layout);
			jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(16, 16, 16)).addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE).addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)).addContainerGap()));
			jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel2Layout.createSequentialGroup().addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));

			jPanel3.setBackground(new java.awt.Color(0, 102, 102));

			jPanel5.setBackground(new java.awt.Color(0, 102, 102));

			jPanel4.setBackground(new java.awt.Color(0, 102, 102));
			jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Anti-ban Settings", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(255, 255, 0))); // NOI18N

			jRadioButton1.setBackground(new java.awt.Color(0, 102, 102));
			afkButtonGroup.add(jRadioButton1);
			jRadioButton1.setForeground(new java.awt.Color(255, 255, 0));
			jRadioButton1.setSelected(true);
			jRadioButton1.setText("AFK On");

			jRadioButton2.setBackground(new java.awt.Color(0, 102, 102));
			afkButtonGroup.add(jRadioButton2);
			jRadioButton2.setForeground(new java.awt.Color(255, 255, 0));
			jRadioButton2.setText("AFK Off");

			jLabel3.setForeground(new java.awt.Color(255, 255, 0));
			jLabel3.setText("AFK Time (ms [1 second = 1000ms]): ");

			jLabel4.setForeground(new java.awt.Color(255, 255, 0));
			jLabel4.setText("Min: ");

			jLabel5.setForeground(new java.awt.Color(255, 255, 0));
			jLabel5.setText("Max: ");

			jTextField1.setText("5000");

			jTextField2.setText("25000");

			final javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
			jPanel4.setLayout(jPanel4Layout);
			jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel4Layout.createSequentialGroup().addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel4Layout.createSequentialGroup().addGap(56, 56, 56).addComponent(jLabel3)).addGroup(jPanel4Layout.createSequentialGroup().addContainerGap().addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel4Layout.createSequentialGroup().addComponent(jRadioButton1).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 223, Short.MAX_VALUE).addComponent(jRadioButton2)).addGroup(jPanel4Layout.createSequentialGroup().addComponent(jLabel4).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel5).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE))))).addContainerGap()));
			jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel4Layout.createSequentialGroup().addContainerGap().addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jRadioButton1).addComponent(jRadioButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel3).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel4).addComponent(jLabel5))));

			final javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
			jPanel5.setLayout(jPanel5Layout);
			jPanel5Layout.setHorizontalGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel5Layout.createSequentialGroup().addContainerGap().addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));
			jPanel5Layout.setVerticalGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup().addContainerGap().addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE).addContainerGap()));

			jTabbedPane2.addTab("AFK", jPanel5);

			final javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
			jPanel3.setLayout(jPanel3Layout);
			jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addContainerGap().addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE).addContainerGap()));
			jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel3Layout.createSequentialGroup().addContainerGap().addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

			jTabbedPane2.getAccessibleContext().setAccessibleName("AFK Settings");

			jTabbedPane1.addTab("Anti-ban", jPanel3);

			jButton2.setText("Start Script");
			jButton2.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(final java.awt.event.ActionEvent evt) {
					jButton2ActionPerformed(evt);
				}
			});

			final javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
			jPanel1.setLayout(jPanel1Layout);
			jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 718, Short.MAX_VALUE).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE).addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(18, 18, 18).addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE))).addContainerGap()));
			jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jLabel1).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(jTabbedPane1, 0, 0, Short.MAX_VALUE).addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jButton2).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

			final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
			setLayout(layout);
			layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
			layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE));

			pack();
		}// </editor-fold>

		private void jButton1ActionPerformed(
				final java.awt.event.ActionEvent evt) {
			getjList1Model().removeAllElements();
			getjList1().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			for (int i = 0; i < inventory.getItems().length; i++) {
				final RSItem[] itemI = inventory.getItems();
				final int itemid = itemI[i].getID();
				String message = "";
				final int slot = i + 1;
				final String itemname = itemI[i].getName();
				if (itemid != -1) {
					message = "<html><body>Slot: " + slot + " Name: "
							+ itemname + "</body></html>";
					getjList1Model().addElement(message);
				} else {
					if (slot <= 28) {
						message = "<html><body><i>No item in slot: " + slot
								+ "</i></body></html>";
						getjList1Model().addElement(message);
					}
				}

			}
		}

		private void jButton2ActionPerformed(
				final java.awt.event.ActionEvent evt) {
			try {
				if (jList1.getSelectedValue().toString().contains("No item in ")) {
					JOptionPane.showMessageDialog(null, "Please select an item.", "Error", JOptionPane.ERROR_MESSAGE);
				} else {
					jList1.getSelectedIndex();
					final int ID = inventory.getItemAt(jList1.getSelectedIndex()).getID();
					boolean afkon = false;
					if (jRadioButton1.isSelected()) {
						afkon = true;
					} else {
						afkon = false;
					}

					final int afkMin = StringintTo(jTextField1.getText());
					final int afkMax = StringintTo(jTextField2.getText());

					final Object[] options = { "High", "Low" };
					final int n = JOptionPane.showOptionDialog(null, "High or Low alch?", "iPhQ's Alcher | What kind of alching?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, // do
																																																	// not
																																																	// use
																																																	// a
					// custom Icon
					options, // the titles of buttons
					options[0]); // default button title

					boolean high = true;

					if (n == 0) {
						high = true;
					} else {
						high = false;
					}

					startScript(ID, afkon, afkMin, afkMax, high);
					setVisible(false);
				}

			} catch (final Exception e) {
				JOptionPane.showMessageDialog(null, "Please select an item.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public enum Status {
		CHECK, ALCH, WAIT_FOR_GUI;
	}

	public boolean checked = false;
	private JTextField itemIDlabel;
	private final int alchXP = 65;
	private final int lowAlchXP = 31;
	public long startTime = System.currentTimeMillis();
	private String STATUS = "N/A";
	public int startExp;
	public int gainedExp;
	public boolean high;
	private int IDitem;
	private int startLvl;
	private boolean doneWithGUI = false;
	private boolean guiOpen = false;

	private boolean AFKon = true;

	private int minAfkTime;

	private int maxAfkTime;

	Rectangle closeButton = new Rectangle(540, 400, 200, 60);

	Point p;

	boolean showPaint = true;

	private final RenderingHints antialiasing = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

	private final Color color1 = new Color(0, 102, 102);

	private final Color color2 = new Color(0, 0, 0);

	private final Color color3 = new Color(204, 204, 0);

	private final Color color4 = new Color(255, 255, 255);

	private final Color color5 = new Color(153, 255, 255);
	private final Color color6 = new Color(255, 0, 51);
	private final Font font1 = new Font("Arial", 0, 21);

	private final Font font2 = new Font("Arial", 0, 12);

	private final Font font3 = new Font("Arial", 0, 24);

	private final Image img1 = getImage("http://services.runescape.com/m=itemdb_rs/3281_obj_big.gif?id=561");
	private final Image img2 = getImage("http://services.runescape.com/m=itemdb_rs/3281_obj_big.gif?id=554");
	float alpha = 0.0f;
	private int fade = 0; // 0 - fade in, 1 - fade out, 2 - stay still

	private boolean showing = true;
	private final String link = "http://www.powerbot.org/vb/showthread.php?t=607582";

	private NewFrame gui;

	public void afk(final int chance, final int length) {
		if (chance == 1) {
			STATUS = "AFK";
			log("Performing antiban - AFK");
			mouse.moveOffScreen();
			Methods.sleep(length);
			log("Done");
		}
	}

	public void antiban() {
		final int randomBan = Methods.random(0, 500);
		if (randomBan == 5) {
			new AntiBan(20);
		}
		if (randomBan == 20) {

		}
	}

	public void check() {
		if (inventory.getItem(IDitem) == null) {
			log("The item is not in the inventory!");
			log("Sleeping before checking...");
			Methods.sleep(5000);
			game.openTab(Game.TAB_EQUIPMENT);
			if (equipment.containsAll(IDitem)) {
				log("Attempting to unequip your item...");
				Methods.sleep(2000);

				if (equipment.getItem(Equipment.AMMO).getID() == IDitem) {
					equipment.getItem(Equipment.AMMO).doClick(true);
				} else if (equipment.getItem(Equipment.BODY).getID() == IDitem) {
					equipment.getItem(Equipment.BODY).doClick(true);
				} else if (equipment.getItem(Equipment.CAPE).getID() == IDitem) {
					equipment.getItem(Equipment.CAPE).doClick(true);
				} else if (equipment.getItem(Equipment.HANDS).getID() == IDitem) {
					equipment.getItem(Equipment.HANDS).doClick(true);
				} else if (equipment.getItem(Equipment.HELMET).getID() == IDitem) {
					equipment.getItem(Equipment.HELMET).doClick(true);
				} else if (equipment.getItem(Equipment.LEGS).getID() == IDitem) {
					equipment.getItem(Equipment.LEGS).doClick(true);
				} else if (equipment.getItem(Equipment.NECK).getID() == IDitem) {
					equipment.getItem(Equipment.NECK).doClick(true);
				} else if (equipment.getItem(Equipment.RING).getID() == IDitem) {
					equipment.getItem(Equipment.RING).doClick(true);
				} else if (equipment.getItem(Equipment.SHIELD).getID() == IDitem) {
					equipment.getItem(Equipment.SHIELD).doClick(true);
				} else if (equipment.getItem(Equipment.WEAPON).getID() == IDitem) {
					equipment.getItem(Equipment.WEAPON).doClick(true);
				}

				log("Should be unequiped !");
			} else {
			}
		}
	}

	private void createAndWaitforGUI() {
		if (SwingUtilities.isEventDispatchThread()) {
			gui = new NewFrame();
			gui.setVisible(true);
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						gui = new NewFrame();
						gui.setVisible(true);
					}
				});
			} catch (final InvocationTargetException ite) {
			} catch (final InterruptedException ie) {
			}
		}
		sleep(100);
		while (gui.isVisible()) {
			sleep(100);
		}
	}

	private Image getImage(final String url) {
		try {
			return ImageIO.read(new URL(url));
		} catch (final IOException e) {
			return null;
		}
	}

	private Status getStatus() {
		if (!doneWithGUI) {
			return Status.WAIT_FOR_GUI;
		} else if (doneWithGUI == true && checked == false) {
			return Status.CHECK;
		} else {
			return Status.ALCH;
		}
	}

	public int itemToInt() {
		final String text = itemIDlabel.getText();
		try {
			final int number = Integer.parseInt(text);
			return number;
		} catch (final Exception e) {
			log("Your item ID must be a number !");
			this.stopScript();
		}
		return 0;
	}

	@Override
	public int loop() {
		if (game.isLoggedIn()) {

			switch (getStatus()) {
			case CHECK:
				STATUS = "Sleeping :)";
				Methods.sleep(Methods.random(2000, 5000));
				STATUS = "Checking...";
				if (inventory.contains(561)) {
					if (equipment.containsAll(1387)
							|| equipment.containsAll(1401)) {
						if (inventory.contains(IDitem)) {

							checked = true;
						} else {
							log("The item ID does not excist in your inventory !");
							stopScript();
						}
					} else {
						log("You must wear a staff of fire to use this script !");
						this.stopScript();
					}
				} else {
					log("You don't have any nature runes !");
					stopScript();
				}
				break;
			case ALCH:
				STATUS = "Alching...";
				if (high) {
					magic.castSpell(Magic.SPELL_HIGH_LEVEL_ALCHEMY);
				} else {
					magic.castSpell(Magic.SPELL_LOW_LEVEL_ALCHEMY);
				}
				Methods.sleep(Methods.random(100, 1000));

				try {
					if (inventory.getItem(IDitem) == null) {
						check();
					} else {
						inventory.getItem(IDitem).doClick(true);
					}
				} catch (final Exception e) {
					log("There was an error while alching!");
					e.printStackTrace();
				}

				Methods.sleep(Methods.random(1200, 1500));
				antiban();
				if (AFKon) {
					afk(Methods.random(0, 1000), Methods.random(minAfkTime, maxAfkTime));
				}
				break;

			case WAIT_FOR_GUI:
				if (guiOpen == false) {
					createAndWaitforGUI();
					gui.getjList1Model().removeAllElements();
					gui.getjList1().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					for (int i = 0; i < inventory.getItems().length; i++) {
						final RSItem[] itemI = inventory.getItems();
						final int itemid = itemI[i].getID();
						String message = "";
						final int slot = i + 1;
						final String itemname = itemI[i].getName();
						if (itemid != -1) {
							message = "<html><body>Slot: " + slot + " Name: "
									+ itemname + "</body></html>";
							gui.getjList1Model().addElement(message);
						} else {
							if (slot <= 28) {
								message = "<html><body><i>No item in slot: "
										+ slot + "</i></body></html>";
								gui.getjList1Model().addElement(message);
							}
						}

					}
					gui.setVisible(true);
					guiOpen = true;
				}
				break;
			}

			return Methods.random(100, 500);

		}
		return 0;
	}

	@Override
	public void messageReceived(final MessageEvent e) {
		final String message = "You do not have enough Nature Runes to cast this spell.";
		if (e.getMessage().contains(message)) {
			log("Damn...Sounds like you have ran out of nature runes.");
			stopScript(true);
		}
	}

	@Override
	public void mouseClicked(final MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(final MouseEvent arg0) {
	}

	@Override
	public void mouseExited(final MouseEvent arg0) {
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		p = e.getPoint();
		if (closeButton.contains(p)) {
			if (fade == 1) {
				fade = 0;
			} else {
				fade = 1;
			}
		}
	}

	@Override
	public void mouseReleased(final MouseEvent arg0) {
	}

	public void newPaint(final Graphics g1) {
		final int mageLvl = skills.getCurrentLevel(Skills.MAGIC);
		final int lvlsGained = mageLvl - startLvl;

		final Graphics2D g = (Graphics2D) g1;
		g.setRenderingHints(antialiasing);

		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (fade == 0) {
			alpha += 0.05f;
			if (alpha >= 1.0f) {
				showing = true;
				alpha = 1.0f;

			} else {

			}
		} else if (fade == 1) {
			alpha -= 0.05f;
			if (alpha <= 0.0f) {
				showing = false;
				alpha = 0.0f;

			} else {

			}
		} else {

		}

		long millis = System.currentTimeMillis() - startTime;
		final long hours = millis / (1000 * 60 * 60);
		millis -= hours * 1000 * 60 * 60;
		final long minutes = millis / (1000 * 60);
		millis -= minutes * 1000 * 60;
		final long seconds = millis / 1000;
		gainedExp = skills.getCurrentExp(Skills.MAGIC) - startExp;

		float xpsec = 0;
		if ((minutes > 0 || hours > 0 || seconds > 0) && gainedExp > 0) {
			xpsec = (float) gainedExp
					/ (float) (seconds + minutes * 60 + hours * 60 * 60);
		}
		final float xpmin = xpsec * 60;
		final float xphour = xpmin * 60;

		float alchsec = 0;
		if (high) {
			if ((minutes > 0 || hours > 0 || seconds > 0)
					&& gainedExp / alchXP > 0) {
				alchsec = (float) gainedExp / alchXP
						/ (seconds + minutes * 60 + hours * 60 * 60);
			}
		} else {
			if ((minutes > 0 || hours > 0 || seconds > 0)
					&& gainedExp / lowAlchXP > 0) {
				alchsec = (float) gainedExp / alchXP
						/ (seconds + minutes * 60 + hours * 60 * 60);
			}
		}
		final float alchmin = alchsec * 60;
		final float alchhour = alchmin * 60;

		final int expToLevel = skills.getExpToNextLevel(Skills.MAGIC);
		int secToLvl = 0;
		int minToLvl = 0;
		int hourToLvl = 0;
		if (xpsec > 0) {
			secToLvl = (int) (expToLevel / xpsec);
		}
		if (secToLvl >= 60) {
			minToLvl = secToLvl / 60;
			secToLvl -= minToLvl * 60;
		} else {
			minToLvl = 0;
		}
		if (minToLvl >= 60) {
			hourToLvl = minToLvl / 60;
			minToLvl -= hourToLvl * 60;
		} else {
			hourToLvl = 0;
		}

		g.setColor(color1);
		g.fillRect(2, 3, 516, 145);
		g.drawImage(img1, 403, 23, null);
		g.drawImage(img2, 433, 63, null);
		g.setFont(font1);
		g.setColor(color2);
		g.drawString("iPhQ's Alcher", 397, 29);
		g.setColor(color3);
		g.drawString("iPhQ's Alcher", 394, 26);
		g.setFont(font2);
		g.setColor(color4);
		g.drawString("Status: " + STATUS, 12, 25);
		g.drawString("Time Running: " + hours + ":" + minutes + ":" + seconds
				+ "", 12, 42);
		if (high) {
			g.drawString("Alched: " + gainedExp / alchXP, 13, 58);
		} else {
			g.drawString("Alched: " + gainedExp / lowAlchXP, 13, 58);
		}
		g.drawString("XP Gained: " + gainedExp, 12, 75);
		g.drawString("XP/H: " + xphour, 12, 90);
		g.drawString("XPTL: " + expToLevel, 12, 105);
		g.drawString("TTL: " + +hourToLvl + ":" + minToLvl + ":" + secToLvl, 12, 120);
		g.drawString("Levels Gained: " + lvlsGained, 12, 135);
		if (high) {
			g.drawString("Alchs To Next Lvl: " + expToLevel / alchXP, 192, 30);
		} else {
			g.drawString("Alchs To Next Lvl: " + expToLevel / lowAlchXP, 192, 30);
		}
		g.drawString("Alches Per Hour: " + alchhour, 192, 45);
	}

	@Override
	public void onFinish() {
		final Object[] options = { "Visit the thread", "Stop the script" };
		final int n = JOptionPane.showOptionDialog(null, "Thank you for using iPhQ's Alcher.\n Did you find any errors or you want to give me some feedback\n on the script please visit my thread on PowerBot's forums.", "iPhQ's Alcher | Thank you!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, // do
																																																																														// not
																																																																														// use
																																																																														// a
		// custom Icon
		options, // the titles of buttons
		options[0]); // default button title

		if (n == 0) {
			BotGUI.openURL(link);
		} else {

		}
		env.takeScreenshot(false);

	}

	@Override
	public void onRepaint(final Graphics g) {
		if (showing || !showing) {
			g.setColor(color5);
			g.fillRoundRect(540, 400, 200, 60, 16, 16);
			g.setColor(color2);
			g.drawRoundRect(540, 400, 200, 60, 16, 16);
			g.setFont(font3);
			g.setColor(color6);
			g.drawString("Show | Hide Paint", 545, 440);
		}
		newPaint(g);
	}

	@Override
	public boolean onStart() {

		startTime = System.currentTimeMillis();
		startExp = skills.getCurrentExp(Skills.MAGIC);
		startLvl = skills.getCurrentLevel(Skills.MAGIC);

		return true;
	}

	public void scriptStop() {
		stopScript();
	}

	public void startScript(final int itemID, final boolean afkOn,
			final int afkMin, final int afkMax, final boolean highAlch) {
		high = highAlch;
		IDitem = itemID;
		AFKon = afkOn;
		minAfkTime = afkMin;
		maxAfkTime = afkMin;
		doneWithGUI = true;
		guiOpen = false;
	}

	public int StringintTo(final String number) {
		final String text = number;
		try {
			final int number2 = Integer.parseInt(text);
			return number2;
		} catch (final Exception e) {
			JOptionPane.showMessageDialog(null, "Invalid number.", "Error", JOptionPane.ERROR_MESSAGE);
		}
		return 0;
	}

}
