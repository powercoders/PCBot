package org.rsbot.gui;

import org.rsbot.Configuration;
import org.rsbot.Configuration.OperatingSystem;
import org.rsbot.gui.component.Messages;
import org.rsbot.service.Preferences;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * @author Paris
 */
public class SettingsManager extends JDialog {
	private static final long serialVersionUID = 1657935322078534422L;
	private static final String DEFAULT_PASSWORD = "\0\0\0\0\0\0\0\0";
	private final Preferences preferences = Preferences.getInstance();

	public Preferences getPreferences() {
		return preferences;
	}

	public SettingsManager(final Frame owner) {
		super(owner, Messages.OPTIONS, true);
		preferences.load();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setIconImage(Configuration.getImage(Configuration.Paths.Resources.ICON_WRENCH));

		final JPanel panelOptions = new JPanel(new GridLayout(0, 1));
		panelOptions.setBorder(BorderFactory.createTitledBorder("Display"));
		final JPanel panelInternal = new JPanel(new GridLayout(0, 1));
		panelInternal.setBorder(BorderFactory.createTitledBorder("Internal"));

		final JCheckBox checkConfirmations = new JCheckBox(Messages.DISABLECONFIRMATIONS);
		checkConfirmations.setToolTipText("Suppress confirmation messages");
		checkConfirmations.setSelected(preferences.confirmations);

		final JPanel panelShutdown = new JPanel(new GridLayout(1, 2));
		final JCheckBox checkShutdown = new JCheckBox(Messages.AUTOSHUTDOWN);
		checkShutdown.setToolTipText("Automatic system shutdown after specified period of inactivity");
		checkShutdown.setSelected(preferences.shutdown);
		panelShutdown.add(checkShutdown);
		final SpinnerNumberModel modelShutdown = new SpinnerNumberModel(preferences.shutdownTime, 3, 60, 1);
		final JSpinner valueShutdown = new JSpinner(modelShutdown);
		panelShutdown.add(valueShutdown);
		checkShutdown.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				valueShutdown.setEnabled(((JCheckBox) arg0.getSource()).isSelected());
			}
		});
		checkShutdown.setEnabled(Configuration.getCurrentOperatingSystem() == OperatingSystem.WINDOWS);
		valueShutdown.setEnabled(checkShutdown.isEnabled() && checkShutdown.isSelected());

		panelOptions.add(checkConfirmations);
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
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				preferences.confirmations = checkConfirmations.isSelected();
				preferences.shutdown = checkShutdown.isSelected();
				preferences.shutdownTime = modelShutdown.getNumber().intValue();
				preferences.save();
				dispose();
			}
		});
		final JButton buttonCancel = new JButton("Cancel");
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				checkConfirmations.setSelected(preferences.confirmations);
				checkShutdown.setSelected(preferences.shutdown);
				modelShutdown.setValue(preferences.shutdownTime);
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
			public void windowClosing(WindowEvent arg0) {
				buttonCancel.doClick();
			}

			public void windowActivated(WindowEvent arg0) {
			}

			public void windowClosed(WindowEvent arg0) {
			}

			public void windowDeactivated(WindowEvent arg0) {
			}

			public void windowDeiconified(WindowEvent arg0) {
			}

			public void windowIconified(WindowEvent arg0) {
			}

			public void windowOpened(WindowEvent arg0) {
			}
		});
	}

	public void display() {
		setVisible(true);
	}
}
