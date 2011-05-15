package org.rsbot.gui;

import org.rsbot.Configuration;
import org.rsbot.script.AccountStore;
import org.rsbot.script.provider.ScriptDeliveryNetwork;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Tekk
 * @author Jacmob
 * @author Aion
 * @author Timer
 */
@SuppressWarnings("serial")
public class AccountManager extends JDialog implements ActionListener {

	private static final String FILE_NAME = Configuration.Paths.getAccountsFile();

	private static final String[] RANDOM_REWARDS = {"Cash", "Runes", "Coal", "Essence", "Ore", "Bars", "Gems", "Herbs",
			"Seeds", "Charms", "Surprise", "Emote", "Costume", "Attack",
			"Defence", "Strength", "Constitution", "Range", "Prayer", "Magic",
			"Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking",
			"Crafting", "Smithing", "Mining", "Herblore", "Agility", "Thieving",
			"Slayer", "Farming", "Runecrafting", "Hunter", "Construction",
			"Summoning", "Dungeoneering"};

	private static final String[] VALID_KEYS = {"pin", "reward", "member", "take_breaks"};

	private static final Logger log = Logger.getLogger(AccountManager.class.getName());

	private static final AccountStore accountStore = new AccountStore(new File(FILE_NAME));

	static {
		accountStore.setPassword(ScriptDeliveryNetwork.getInstance().getKey());
		try {
			accountStore.load();
		} catch (final IOException ignored) {
		}
	}

	private static class RandomRewardEditor extends DefaultCellEditor {
		public RandomRewardEditor() {
			super(new JComboBox(RANDOM_REWARDS));
		}
	}

	private static class PasswordCellEditor extends DefaultCellEditor {
		public PasswordCellEditor() {
			super(new JPasswordField());
		}
	}

	private static class PasswordCellRenderer extends DefaultTableCellRenderer {
		@Override
		protected void setValue(final Object value) {
			if (value == null) {
				setText("<none>");
			} else {
				final String str = value.toString();
				final StringBuilder b = new StringBuilder();
				for (int i = 0; i < str.length(); ++i) {
					b.append("*");
				}
				setText(b.toString());
			}
		}
	}

	private class TableSelectionListener implements ListSelectionListener {
		public void valueChanged(final ListSelectionEvent evt) {
			final int row = table.getSelectedRow();
			if (!evt.getValueIsAdjusting()) {
				removeButton.setEnabled(row >= 0 && row < table.getRowCount());
			}
		}
	}

	private class AccountTableModel extends AbstractTableModel {
		public int getRowCount() {
			return accountStore.list().size();
		}

		public int getColumnCount() {
			return VALID_KEYS.length + 2;
		}

		public Object getValueAt(final int row, final int column) {
			if (column == 0) {
				return userForRow(row);
			} else if (column == 1) {
				return accountStore.get(userForRow(row)).getPassword();
			} else {
				final AccountStore.Account acc = accountStore.get(userForRow(row));
				if (acc != null) {
					final String str = acc.getAttribute(VALID_KEYS[column - 2]);
					if (str == null || str.isEmpty()) {
						return null;
					}
					if (getColumnClass(column) == Boolean.class) {
						return Boolean.parseBoolean(str);
					} else if (getColumnClass(column) == Integer.class) {
						return Integer.parseInt(str);
					} else {
						return str;
					}
				}
			}
			return null;
		}

		@Override
		public String getColumnName(final int column) {
			if (column == 0) {
				return "Username";
			} else if (column == 1) {
				return "Password";
			}
			final String str = VALID_KEYS[column - 2];
			final StringBuilder b = new StringBuilder();
			boolean space = true;
			for (char c : str.toCharArray()) {
				if (c == '_') {
					c = ' ';
				}
				b.append(space ? Character.toUpperCase(c) : c);
				space = c == ' ';
			}
			return b.toString();
		}

		@Override
		public Class<?> getColumnClass(final int column) {
			if (getColumnName(column).equals("Member")) {
				return Boolean.class;
			}
			if (getColumnName(column).equals("Take Breaks")) {
				return Boolean.class;
			}
			return Object.class;
		}

		@Override
		public boolean isCellEditable(final int row, final int column) {
			return column > 0;
		}

		@Override
		public void setValueAt(final Object value, final int row, final int column) {
			final AccountStore.Account acc = accountStore.get(userForRow(row));
			if (acc == null) {
				return;
			}
			if (column == 1) {
				acc.setPassword(String.valueOf(value));
			} else {
				acc.setAttribute(getColumnName(column).toLowerCase().replace(' ', '_'), String.valueOf(value));
			}
			fireTableCellUpdated(row, column);
		}

		public String userForRow(final int row) {
			final Iterator<AccountStore.Account> it = accountStore.list().iterator();
			for (int k = 0; it.hasNext() && k < row; k++) {
				it.next();
			}
			if (it.hasNext()) {
				return it.next().getUsername();
			}
			return null;
		}
	}

	private JTable table;
	private JButton removeButton;

	private AccountManager() {
		super(Frame.getFrames()[0], "Account Manager", true);
		setIconImage(Configuration.getImage(Configuration.Paths.Resources.ICON_REPORTKEY));
	}

	public void actionPerformed(final ActionEvent e) {
		if (e.getSource() instanceof JButton) {
			final String label = ((JButton) e.getSource()).getText();
			if (label.equals("Done")) {
				try {
					accountStore.save();
				} catch (final IOException ioe) {
					ioe.printStackTrace();
					log.info("Failed to save accounts...  Please report this.");
				}
				dispose();
			} else if (button.getToolTipText().equals("Add")) {
				final String str = JOptionPane.showInputDialog(getParent(), "Enter the account username:", "New Account", JOptionPane.QUESTION_MESSAGE);
				if (str == null || str.isEmpty()) {
					return;
				}
				accountStore.add(new AccountStore.Account(str));
				accountStore.get(str).setAttribute("reward", RANDOM_REWARDS[0]);
				final int row = table.getRowCount();
				((AccountTableModel) table.getModel()).fireTableRowsInserted(row, row);
			} else if (label.equals("Remove")) {
				final int row = table.getSelectedRow();
				final String user = ((AccountTableModel) table.getModel()).userForRow(row);
				if (user != null) {
					accountStore.remove(user);
					((AccountTableModel) table.getModel()).fireTableRowsDeleted(row, row);
				}
			}
		}
	}

	/**
	 * Creates and displays the main GUI This GUI has the list and the main	 * buttons
	 */
	public void showGUI() {
		final JScrollPane scrollPane = new JScrollPane();
		table = new JTable(new AccountTableModel());
		final JPanel bar = new JPanel();
		removeButton = new JButton();
		final JButton newButton = new JButton();
		final JButton doneButton = new JButton();
		setTitle("Account Manager");
		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout(5, 5));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(new TableSelectionListener());
		final TableColumnModel cm = table.getColumnModel();
		cm.getColumn(cm.getColumnIndex("Password")).setCellRenderer(new PasswordCellRenderer());
		cm.getColumn(cm.getColumnIndex("Password")).setCellEditor(new PasswordCellEditor());
		cm.getColumn(cm.getColumnIndex("Pin")).setCellRenderer(new PasswordCellRenderer());
		cm.getColumn(cm.getColumnIndex("Pin")).setCellEditor(new PasswordCellEditor());
		cm.getColumn(cm.getColumnIndex("Reward")).setCellEditor(new RandomRewardEditor());
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setViewportView(table);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		final GridBagLayout gbl = new GridBagLayout();
		bar.setLayout(gbl);
		gbl.rowHeights = new int[]{0, 0};
		gbl.rowWeights = new double[]{0.0, 1.0E-4};
		newButton.setText("Add");
		bar.add(newButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
		removeButton.setText("Remove");
		bar.add(removeButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
		doneButton.setText("Done");
		bar.add(doneButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
		newButton.addActionListener(this);
		doneButton.addActionListener(this);
		removeButton.addActionListener(this);
		contentPane.add(bar, BorderLayout.SOUTH);
		final int row = table.getSelectedRow();
		removeButton.setEnabled(row >= 0 && row < table.getRowCount());
		table.clearSelection();
		doneButton.requestFocus();
		setPreferredSize(new Dimension(600, 300));
		pack();
		setLocationRelativeTo(getOwner());
		setResizable(false);
		setVisible(true);
	}

	/**
	 * Access the list of names for loaded accounts
	 *
	 * @return Array of the names.
	 */
	public static String[] getAccountNames() {
		try {
			final List<String> theList = new ArrayList<String>();
			final Collection<AccountStore.Account> accountCollection = AccountManager.accountStore.list();
			for (final AccountStore.Account anAccountCollection : accountCollection) {
				final AccountStore.Account account = anAccountCollection;
				theList.add(account.getUsername());
			}
			return theList.toArray(new String[theList.size()]);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static AccountManager getInstance() {
		return new AccountManager();
	}

	/**
	 * Access the account password of the given string
	 *
	 * @param name The name of the account
	 * @return Password or an empty string
	 */
	public static String getPassword(final String name) {
		final AccountStore.Account values = AccountManager.accountStore.get(name);
		String pass = values.getPassword();
		if (pass == null) {
			pass = "";
		}
		return pass;
	}

	/**
	 * Access the account pin of the given string
	 *
	 * @param name The name of the account
	 * @return Pin or an empty string
	 */
	public static String getPin(final String name) {
		final AccountStore.Account values = AccountManager.accountStore.get(name);
		String pin = values.getAttribute("pin");
		if (pin == null) {
			pin = "-1";
		}
		return pin;
	}

	/**
	 * Access the account desired reward of the given string
	 *
	 * @param name The name of the account
	 * @return The desired reward
	 */
	public static String getReward(final String name) {
		final AccountStore.Account values = AccountManager.accountStore.get(name);
		final String reward = values.getAttribute("reward");
		if (reward == null) {
			return "Cash";
		}
		return reward;
	}

	/**
	 * Access the account state of the given string
	 *
	 * @param name Name of the account
	 * @return true if the account is member, false if it isn't
	 */
	public static boolean isMember(final String name) {
		final AccountStore.Account values = AccountManager.accountStore.get(name);
		final String member = values.getAttribute("member");
		return member != null && member.equalsIgnoreCase("true");
	}

	/**
	 * Access the account state of the given string
	 *
	 * @param name Name of the account
	 * @return true if the account is member, false if it isn't
	 */
	public static boolean isTakingBreaks(final String name) {
		final AccountStore.Account values = AccountManager.accountStore.get(name);
		final String member = values.getAttribute("take_breaks");
		return member != null && member.equalsIgnoreCase("true");
	}

	/**
	 * Check if the string is a valid key
	 *
	 * @param key The key
	 * @return true if the object is supported, false if it isn't
	 */
	@SuppressWarnings("unused")
	private static boolean isValidKey(final String key) {
		for (final String check : VALID_KEYS) {
			if (key.equalsIgnoreCase(check)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the given string is a valid pin
	 *
	 * @param pin The pin
	 * @return true if the pin is valid, false if it isn't
	 */
	@SuppressWarnings("unused")
	private static boolean isValidPin(final String pin) {
		if (pin.length() == 4) {
			for (int i = 0; i < pin.length(); i++) {
				final char charAt = pin.charAt(i);
				if (charAt < '0' || charAt > '9') {
					return false;
				}
			}
			return true;
		}
		return false;
	}

}