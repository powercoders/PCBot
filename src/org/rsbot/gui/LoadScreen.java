package org.rsbot.gui;

import org.rsbot.Configuration;
import org.rsbot.log.LabelLogHandler;
import org.rsbot.log.LogOutputStream;
import org.rsbot.log.SystemConsoleHandler;
import org.rsbot.script.provider.ScriptDeliveryNetwork;
import org.rsbot.security.RestrictedSecurityManager;
import org.rsbot.util.UpdateChecker;
import org.rsbot.util.io.HttpClient;
import org.rsbot.util.io.IOHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadScreen extends JFrame {
	private final static Logger log = Logger.getLogger(LoadScreen.class.getName());
	private static final long serialVersionUID = 5520543482560560389L;
	public final boolean error;

	public LoadScreen() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception ignored) {
		}
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				System.exit(1);
			}
		});
		setTitle(Configuration.NAME);
		setIconImage(Configuration.getImage(Configuration.Paths.Resources.ICON));
		final JPanel panel = new JPanel(new GridLayout(2, 1));
		final int pad = 10;
		panel.setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
		final JProgressBar progress = new JProgressBar();
		progress.setPreferredSize(new Dimension(350, progress.getPreferredSize().height));
		progress.setIndeterminate(true);
		panel.add(progress);
		final LabelLogHandler handler = new LabelLogHandler();
		Logger.getLogger("").addHandler(handler);
		handler.label.setBorder(BorderFactory.createEmptyBorder(pad, 0, 0, 0));
		final Font font = handler.label.getFont();
		handler.label.setFont(new Font(font.getFamily(), Font.BOLD, font.getSize()));
		handler.label.setPreferredSize(new Dimension(progress.getWidth(), handler.label.getPreferredSize().height + pad));
		panel.add(handler.label);
		log.info("Loading");
		add(panel);
		pack();
		setLocationRelativeTo(getOwner());
		setResizable(false);
		setVisible(true);

		log.info("Registering logs");
		bootstrap();

		log.info("Scanning hosts file for malicious content");
		RestrictedSecurityManager.fixHosts();

		log.info("Extracting resources");
		extractResources();

		log.info("Creating directories");
		Configuration.createDirectories();

		log.info("Enforcing security policy");
		System.setProperty("sun.net.spi.nameservice.nameservers", "8.8.8.8,8.8.4.4"); // Google Public DNS (http://code.google.com/speed/public-dns/)
		System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
		System.setProperty("java.io.tmpdir", Configuration.Paths.getGarbageDirectory());
		System.setSecurityManager(new RestrictedSecurityManager());

		log.info("Downloading resources");
		for (final Entry<String, File> item : Configuration.Paths.getCachableResources().entrySet()) {
			try {
				HttpClient.download(new URL(item.getKey()), item.getValue());
			} catch (final IOException ignored) {
			}
		}

		log.info("Downloading network scripts");
		ScriptDeliveryNetwork.getInstance().list();

		log.info("Checking for updates");

		final String error;

		if (UpdateChecker.isError()) {
			error = "Unable to obtain latest version information";
		} else if (Configuration.RUNNING_FROM_JAR && UpdateChecker.isDeprecatedVersion()) {
			error = "Please update at " + Configuration.Paths.URLs.DOWNLOAD_SHORT;
		} else {
			error = null;
		}

		if (error == null) {
			this.error = false;
			log.info("Loading bot");
			Configuration.registerLogging();
			Logger.getLogger("").removeHandler(handler);
		} else {
			this.error = true;
			progress.setIndeterminate(false);
			log.severe(error);
		}
	}

	private static void bootstrap() {
		Logger.getLogger("").setLevel(Level.INFO);
		Logger.getLogger("").addHandler(new SystemConsoleHandler());
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			private final Logger log = Logger.getLogger("EXCEPTION");

			public void uncaughtException(final Thread t, final Throwable e) {
				log.logp(Level.SEVERE, "EXCEPTION", "", "Unhandled exception in thread " + t.getName() + ": ", e);
			}
		});
		System.setErr(new PrintStream(new LogOutputStream(Logger.getLogger("STDERR"), Level.SEVERE), true));
	}

	public static void extractResources() {
		final ArrayList<String> extract = new ArrayList<String>(2);
		if (Configuration.getCurrentOperatingSystem() == Configuration.OperatingSystem.WINDOWS) {
			extract.add(Configuration.Paths.COMPILE_SCRIPTS_BAT);
			extract.add(Configuration.Paths.COMPILE_FIND_JDK);
		} else {
			extract.add(Configuration.Paths.COMPILE_SCRIPTS_SH);
		}
		for (final String item : extract) {
			final String path = Configuration.Paths.Resources.ROOT + "/" + item;
			final InputStream in;
			try {
				in = Configuration.getResourceURL(path).openStream();
			} catch (final IOException ignored) {
				continue;
			}
			final File output = new File(Configuration.Paths.getHomeDirectory(), item);
			IOHelper.write(in, output);
		}
	}
}
