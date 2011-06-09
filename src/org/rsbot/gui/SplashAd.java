package org.rsbot.gui;

import org.rsbot.Configuration;
import org.rsbot.util.io.HttpClient;
import org.rsbot.util.io.IniParser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * @author Paris
 */
public class SplashAd extends JDialog implements MouseListener {
	private static final Logger log = Logger.getLogger(SplashAd.class.getName());

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
		HashMap<String, String> keys;

		try {
			final File cache = Configuration.Paths.getCachableResources().get(Configuration.Paths.URLs.AD_INFO);
			keys = IniParser.deserialise(cache).get(IniParser.emptySection);
		} catch (final IOException ignored) {
			return false;
		}

		if (keys == null || keys.isEmpty() || !keys.containsKey("enabled") || !IniParser.parseBool(keys.get("enabled"))) {
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

	public void display() {
		setLocationRelativeTo(getOwner());
		setVisible(true);

		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				dispose();
			}
		}, display);
	}

	public void mouseClicked(final MouseEvent e) {
	}

	public void mousePressed(final MouseEvent e) {
	}

	public void mouseReleased(final MouseEvent e) {
		BotGUI.openURL(link);
		dispose();
	}

	public void mouseEntered(final MouseEvent e) {
	}

	public void mouseExited(final MouseEvent e) {
	}
}
