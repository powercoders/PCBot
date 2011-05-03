package org.rsbot.gui;

import org.rsbot.util.GlobalConfiguration;
import org.rsbot.util.HttpAgent;
import org.rsbot.util.IniParser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Timer;
import java.util.logging.Logger;

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

	public SplashAd(JFrame owner) {
		super(owner);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setUndecorated(true);
		setTitle("Advertisement");
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		if (!sync()) {
			dispose();
			return;
		}

		File file = new File(GlobalConfiguration.Paths.getCacheDirectory(), CACHED_IMAGE);
		try {
			HttpAgent.download(image, file);
		} catch (IOException ignored) {
			dispose();
			return;
		}

		if (text != null && text.length() != 0) {
			log.info(text);
		}

		try {
			BufferedImage img = ImageIO.read(file);
			setSize(img.getWidth(), img.getHeight());
			JLabel label = new JLabel();
			label.setIcon(new ImageIcon(img));
			add(label);
		} catch (IOException ignored) {
			dispose();
			return;
		}

		addMouseListener(this);
	}

	private boolean sync() {
		HashMap<String, String> keys = null;

		try {
			URL source = new URL(GlobalConfiguration.Paths.URLs.AD_INFO);
			final File cache = new File(GlobalConfiguration.Paths.getCacheDirectory(), "ads.txt");
			HttpAgent.download(source, cache);
			BufferedReader reader = new BufferedReader(new FileReader(cache));
			keys = IniParser.deserialise(reader).get(IniParser.emptySection);
			reader.close();
		} catch (Exception e) {
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
			} catch (MalformedURLException e) {
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

	private boolean parseBool(String mode) {
		return mode.equals("1") || mode.equalsIgnoreCase("true") || mode.equalsIgnoreCase("yes");
	}

	public void display() {
		setLocationRelativeTo(getOwner());
		setVisible(true);

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				dispose();
			}
		}, display);
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		BotGUI.openURL(link);
		dispose();
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

}
