package org.rsbot.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.rsbot.Configuration;
import org.rsbot.Configuration.OperatingSystem;
import org.rsbot.service.Monitoring;
import org.rsbot.util.io.IniParser;

/**
 * @author Paris
 */
public class SettingsManager extends JDialog {
	private static Logger log = Logger.getLogger(SettingsManager.class.getName());
	private static final long serialVersionUID = 1657935322078534422L;
	private Preferences prefs;

	public class Preferences {
		private File store;

		/**
		 * Whether or not to disable ads.
		 */
		public boolean ads = true;

		public boolean confirmations = true;
		public boolean monitoring = true;
		public boolean shutdown = false;
		public int shutdownTime = 10;

		public Preferences(final File store) {
			this.store = store;
		}

		public void load() {
			HashMap<String, String> keys = null;
			try {
				final BufferedReader reader = new BufferedReader(new FileReader(store));
				keys = IniParser.deserialise(reader).get(IniParser.emptySection);
				reader.close();
			} catch (final IOException ignored) {
				log.severe("Failed to load preferences");
			}
			if (keys == null || keys.isEmpty()) {
				return;
			}
			if (keys.containsKey("ads")) {
				ads = IniParser.parseBool(keys.get("ads"));
			}
			if (keys.containsKey("confirmations")) {
				confirmations = IniParser.parseBool(keys.get("confirmations"));
			}
			if (keys.containsKey("monitoring")) {
				monitoring = IniParser.parseBool(keys.get("ads"));
			}
			if (keys.containsKey("shutdown")) {
				shutdown = IniParser.parseBool(keys.get("shutdown"));
			}
			if (keys.containsKey("shutdownTime")) {
				shutdownTime = Integer.parseInt(keys.get("shutdownTime"));
				shutdownTime = Math.max(Math.min(shutdownTime, 60), 3);
			}
		}

		public void save() {
			final HashMap<String, String> keys = new HashMap<String, String>(5);
			keys.put("ads", Boolean.toString(ads));
			keys.put("confirmations", Boolean.toString(confirmations));
			keys.put("monitoring", Boolean.toString(monitoring));
			keys.put("shutdown", Boolean.toString(shutdown));
			keys.put("shutdownTime", Integer.toString(shutdownTime));
			final HashMap<String, HashMap<String, String>> data = new HashMap<String, HashMap<String, String>>(1);
			data.put(IniParser.emptySection, keys);
			try {
				final BufferedWriter out = new BufferedWriter(new FileWriter(store));
				IniParser.serialise(data, out);
				out.close();
			} catch (final IOException ignored) {
				log.severe("Could not save preferences");
			}
		}

		public void commit() {
			Monitoring.setEnabled(monitoring);
		}
	}

	public Preferences getPreferences() {
		return prefs;
	}

	public SettingsManager(final Frame owner, final File store) {
		super(owner, Messages.OPTIONS, true);
		prefs = new Preferences(store);
		prefs.load();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setIconImage(Configuration.getImage(Configuration.Paths.Resources.ICON_WRENCH));

		final JPanel panelOptions = new JPanel(new GridLayout(0, 1));
		panelOptions.setBorder(BorderFactory.createTitledBorder("Display"));
		final JPanel panelInternal = new JPanel(new GridLayout(0, 1));
		panelInternal.setBorder(BorderFactory.createTitledBorder("Internal"));

		final JCheckBox checkAds = new JCheckBox(Messages.DISABLEADS);
		checkAds.setToolTipText("Show advertisment on startup");
		checkAds.setSelected(prefs.ads);

		final JCheckBox checkConf = new JCheckBox(Messages.DISABLECONFIRMATIONS);
		checkConf.setToolTipText("Supress confirmation messages");
		checkConf.setSelected(prefs.confirmations);

		final JCheckBox checkMonitor = new JCheckBox(Messages.DISABLEMONITORING);
		checkMonitor.setToolTipText("Monitor system information to improve development");
		checkMonitor.setSelected(prefs.monitoring);

		final JPanel panelShutdown = new JPanel(new GridLayout(1, 2));
		final JCheckBox checkShutdown = new JCheckBox(Messages.AUTOSHUTDOWN);
		checkShutdown.setToolTipText("Automatic system shutdown after specified period of inactivty");
		checkShutdown.setSelected(prefs.shutdown);
		panelShutdown.add(checkShutdown);
		final SpinnerNumberModel modelShutdown = new SpinnerNumberModel(prefs.shutdownTime, 3, 60, 1);
		final JSpinner valueShutdown = new JSpinner(modelShutdown);
		panelShutdown.add(valueShutdown);
		checkShutdown.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				valueShutdown.setEnabled(((JCheckBox) arg0.getSource()).isSelected());
			}
		});
		checkShutdown.setEnabled(Configuration.getCurrentOperatingSystem() == OperatingSystem.WINDOWS);
		valueShutdown.setEnabled(checkShutdown.isEnabled() && checkShutdown.isSelected());

		panelOptions.add(checkAds);
		panelOptions.add(checkConf);
		panelInternal.add(checkMonitor);
		panelInternal.add(panelShutdown);

		final GridLayout gridAction = new GridLayout(1, 2);
		gridAction.setHgap(5);
		final JPanel panelAction = new JPanel(gridAction);
		final int pad = 6;
		panelAction.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
		panelAction.add(Box.createHorizontalGlue());

		final JButton buttonOk = new JButton("OK");
		buttonOk.setPreferredSize(new Dimension(85, 30));
		buttonOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				prefs.ads = checkAds.isSelected();
				prefs.confirmations = checkConf.isSelected();
				prefs.monitoring = checkMonitor.isSelected();
				prefs.shutdown = checkShutdown.isSelected();
				prefs.shutdownTime = modelShutdown.getNumber().intValue();
				prefs.commit();
				prefs.save();
				dispose();
			}
		});
		final JButton buttonCancel = new JButton("Cancel");
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				checkAds.setSelected(prefs.ads);
				checkConf.setSelected(prefs.confirmations);
				checkMonitor.setSelected(prefs.monitoring);
				checkShutdown.setSelected(prefs.shutdown);
				modelShutdown.setValue(prefs.shutdownTime);
				dispose();
			}
		});

		panelAction.add(buttonOk);
		panelAction.add(buttonCancel);

		final JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.setBorder(panelAction.getBorder());
		panel.add(panelOptions);
		panel.add(panelInternal);

		add(panel);
		add(panelAction, BorderLayout.SOUTH);

		getRootPane().setDefaultButton(buttonOk);
		buttonOk.requestFocus();

		pack();
		setLocationRelativeTo(getOwner());
		setResizable(false);

		addWindowListener(new WindowListener() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				buttonCancel.doClick();
			}
			@Override
			public void windowActivated(WindowEvent arg0) {	}
			@Override
			public void windowClosed(WindowEvent arg0) { }
			@Override
			public void windowDeactivated(WindowEvent arg0) { }
			@Override
			public void windowDeiconified(WindowEvent arg0) { }
			@Override
			public void windowIconified(WindowEvent arg0) { }
			@Override
			public void windowOpened(WindowEvent arg0) { }
		});
	}

	public void display() {
		setVisible(true);
	}
}
