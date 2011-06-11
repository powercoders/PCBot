package org.rsbot.gui;

import org.rsbot.Configuration;
import org.rsbot.Configuration.OperatingSystem;
import org.rsbot.locale.Messages;
import org.rsbot.service.DRM;
import org.rsbot.service.Preferences;
import org.rsbot.util.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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

		final JPanel panelLogin = new JPanel(new GridLayout(2, 1));
		panelLogin.setBorder(BorderFactory.createTitledBorder("Service Login"));
		final JPanel panelOptions = new JPanel(new GridLayout(0, 1));
		panelOptions.setBorder(BorderFactory.createTitledBorder("Display"));
		final JPanel panelInternal = new JPanel(new GridLayout(0, 1));
		panelInternal.setBorder(BorderFactory.createTitledBorder("Internal"));
		final JPanel panelWeb = new JPanel(new GridLayout(2, 1));
		panelWeb.setBorder(BorderFactory.createTitledBorder("Web UI"));

		final JPanel[] panelLoginOptions = new JPanel[2];
		for (int i = 0; i < panelLoginOptions.length; i++) {
			panelLoginOptions[i] = new JPanel(new GridLayout(1, 2));
		}
		panelLoginOptions[0].add(new JLabel("  Username:"));
		final JTextField textLoginUser = new JTextField(preferences.user);
		textLoginUser.setToolTipText(Configuration.Paths.URLs.HOST + " forum account username, leave blank to log out");
		panelLoginOptions[0].add(textLoginUser);
		panelLoginOptions[1].add(new JLabel("  Password:"));
		final JPasswordField textLoginPass = new JPasswordField(preferences.user.length() == 0 ? "" : DEFAULT_PASSWORD);
		panelLoginOptions[1].add(textLoginPass);
		panelLogin.add(panelLoginOptions[0]);
		panelLogin.add(panelLoginOptions[1]);

		final JCheckBox checkAds = new JCheckBox(Messages.DISABLEADS);
		checkAds.setToolTipText("Show advertisement on startup");
		checkAds.setSelected(preferences.hideAds);

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

		final JCheckBox checkHosts = new JCheckBox(Messages.ALLOWALLHOSTS);
		checkHosts.setToolTipText("Allow connections to all websites (NOT RECOMMENDED)");
		checkHosts.setSelected(preferences.allowAllHosts);

		final JPanel[] panelWebOptions = new JPanel[2];
		for (int i = 0; i < panelWebOptions.length; i++) {
			panelWebOptions[i] = new JPanel(new GridLayout(1, 2));
		}
		final JCheckBox checkWeb = new JCheckBox(Messages.BINDTO);
		checkWeb.setToolTipText("Remote control via web interface");
		checkWeb.setSelected(preferences.web);
		panelWebOptions[0].add(checkWeb);
		final JFormattedTextField textWebBind = new JFormattedTextField(preferences.webBind);
		textWebBind.setToolTipText("Example: localhost:9500");
		panelWebOptions[0].add(textWebBind);
		final JCheckBox checkWebPass = new JCheckBox(Messages.USEPASSWORD);
		checkWebPass.setSelected(preferences.webPassRequire);
		panelWebOptions[1].add(checkWebPass);
		final JPasswordField textWebPass = new JPasswordField(DEFAULT_PASSWORD);
		textWebPass.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent e) {
				textWebPass.setText("");
			}
		});
		panelWebOptions[1].add(textWebPass);
		checkWebPass.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textWebPass.setEnabled(checkWebPass.isSelected() && checkWebPass.isEnabled());
			}
		});
		checkWeb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final boolean enabled = checkWeb.isSelected();
				textWebBind.setEnabled(enabled);
				checkWebPass.setEnabled(enabled);
				for (final ActionListener action : checkWebPass.getActionListeners()) {
					action.actionPerformed(null);
				}
			}
		});
		for (final ActionListener action : checkWeb.getActionListeners()) {
			action.actionPerformed(null);
		}

		panelOptions.add(checkAds);
		panelOptions.add(checkConfirmations);
		panelInternal.add(panelShutdown);
		panelInternal.add(checkHosts);
		panelWeb.add(panelWebOptions[0]);
		panelWeb.add(panelWebOptions[1]);

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
				preferences.hideAds = checkAds.isSelected();
				preferences.confirmations = checkConfirmations.isSelected();
				preferences.shutdown = checkShutdown.isSelected();
				preferences.shutdownTime = modelShutdown.getNumber().intValue();
				preferences.web = checkWeb.isSelected();
				preferences.webBind = textWebBind.getText();
				final String webUser = textLoginUser.getText(), webPass = new String(textWebPass.getPassword());
				if (!webUser.equals(preferences.user) || !webPass.equals(DEFAULT_PASSWORD)) {
					preferences.webPass = StringUtil.sha1sum(webPass);
				}
				preferences.user = webUser;
				preferences.webPassRequire = checkWebPass.isSelected() && checkWebPass.isEnabled();
				final String loginPass = new String(textLoginPass.getPassword());
				if (!loginPass.equals(DEFAULT_PASSWORD)) {
					if (!DRM.login(preferences.user, loginPass)) {
						preferences.user = "";
					}
				}
				preferences.allowAllHosts = checkHosts.isSelected();
				preferences.save();
				preferences.commit();
				textLoginPass.setText(DEFAULT_PASSWORD);
				textWebPass.setText(DEFAULT_PASSWORD);
				dispose();
			}
		});
		final JButton buttonCancel = new JButton("Cancel");
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				checkAds.setSelected(preferences.hideAds);
				checkConfirmations.setSelected(preferences.confirmations);
				checkShutdown.setSelected(preferences.shutdown);
				checkHosts.setSelected(preferences.allowAllHosts);
				modelShutdown.setValue(preferences.shutdownTime);
				dispose();
			}
		});

		panelAction.add(buttonOk);
		panelAction.add(buttonCancel);

		final JPanel panel = new JPanel(new GridLayout(0, 1));
		panel.setBorder(panelAction.getBorder());
		panel.add(panelLogin);
		panel.add(panelOptions);
		panel.add(panelInternal);

		if (!Configuration.RUNNING_FROM_JAR) {
			panel.add(panelWeb); // hide web options from non-development builds for now
		}

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
