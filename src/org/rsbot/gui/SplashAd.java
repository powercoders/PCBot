package org.rsbot.gui;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.rsbot.Configuration;
import org.rsbot.service.Monitoring;
import org.rsbot.service.Monitoring.Type;
import org.rsbot.util.io.HttpClient;
import org.rsbot.util.io.IniParser;

/**
 * @author Paris
 */
public class SplashAd extends JDialog implements MouseListener {
	private static Logger log = Logger.getLogger(SplashAd.class.getName());

	private static final long serialVersionUID = 1L;

	private static final String CACHED_IMAGE = "advert.png";
	private String link;
	private URL image;
	private String text;
	private int display = 5000;

	public SplashAd(final JFrame owner) {
		super(owner);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setUndecorated(true);
		setTitle("Advertisement");
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		if (!sync()) {
			dispose();
			return;
		}

		final File file = new File(Configuration.Paths.getCacheDirectory(), CACHED_IMAGE);
		try {
			HttpClient.download(image, file);
		} catch (final IOException ignored) {
			dispose();
			return;
		}

		if (text != null && text.length() != 0) {
			log.info(text);
		}

		try {
			final BufferedImage img = ImageIO.read(file);
			setSize(img.getWidth(), img.getHeight());
			final JLabel label = new JLabel();
			label.setIcon(new ImageIcon(img));
			add(label);
		} catch (final IOException ignored) {
			dispose();
			return;
		}

		addMouseListener(this);
	}

	private boolean sync() {
		HashMap<String, String> keys = null;

		try {
			final URL source = new URL(Configuration.Paths.URLs.AD_INFO);
			final File cache = new File(Configuration.Paths.getCacheDirectory(), "ads.txt");
			HttpClient.download(source, cache);
			final BufferedReader reader = new BufferedReader(new FileReader(cache));
			keys = IniParser.deserialise(reader).get(IniParser.emptySection);
			reader.close();
		} catch (final Exception e) {
			return false;
		}

		if (keys == null || keys.isEmpty() || !keys.containsKey("enabled") || !parseBool(keys.get("enabled"))) {
			return false;
		}
		if (!keys.containsKey("link")) {
			return false;
		} else {
			link = keys.get("link");
		}
		if (!keys.containsKey("image")) {
			return false;
		} else {
			try {
				image = new URL(keys.get("image"));
			} catch (final MalformedURLException e) {
				return false;
			}
		}
		if (keys.containsKey("text")) {
			text = keys.get("text");
		}
		if (keys.containsKey("display")) {
			display = Integer.parseInt(keys.get("display"));
		}

		return true;
	}

	private boolean parseBool(final String mode) {
		return mode.equals("1") || mode.equalsIgnoreCase("true") || mode.equalsIgnoreCase("yes");
	}

	public void display() {
		setLocationRelativeTo(getOwner());
		setVisible(true);

		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Monitoring.pushState(Type.ENVIRONMENT, "ADS", "CLICK", "false");
				dispose();
			}
		}, display);
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
	}

	@Override
	public void mousePressed(final MouseEvent e) {
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		BotGUI.openURL(link);
		Monitoring.pushState(Type.ENVIRONMENT, "ADS", "CLICK", "true");
		dispose();
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
	}

	@Override
	public void mouseExited(final MouseEvent e) {
	}

}
