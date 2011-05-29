package org.rsbot.bot;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.rsbot.Configuration;

public class BotStub implements AppletStub, AppletContext {
	private final Map<URL, WeakReference<Image>> IMAGE_CACHE = new HashMap<URL, WeakReference<Image>>();
	private final Map<String, InputStream> INPUT_CACHE = Collections.synchronizedMap(new HashMap<String, InputStream>(2));

	private final Logger log = Logger.getLogger(BotStub.class.getName());
	private final Applet applet;
	private final URL codeBase;
	private final URL documentBase;
	private boolean isActive;
	private final Map<String, String> parameters;

	public BotStub(final RSLoader applet) {
		this.applet = applet;
		final Crawler c = new Crawler("http://www." + applet.getTargetName()
				+ ".com/");
		parameters = c.getParameters();
		final String world_prefix = c.getWorldPrefix();
		try {
			codeBase = new URL("http://world" + world_prefix + "."
					+ applet.getTargetName() + ".com");
			documentBase = new URL("http://world" + world_prefix + "."
					+ applet.getTargetName() + ".com/m0");
		} catch (final MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void appletResize(final int x, final int y) {
		final Dimension size = new Dimension(x, y);
		applet.setSize(size);
		applet.setPreferredSize(size);
	}

	@Override
	public Applet getApplet(final String name) {
		final String thisName = parameters.get("name");
		if (thisName == null) {
			return null;
		}
		return thisName.equals(name) ? applet : null;
	}

	@Override
	public AppletContext getAppletContext() {
		return this;
	}

	@Override
	public Enumeration<Applet> getApplets() {
		final Vector<Applet> apps = new Vector<Applet>();
		apps.add(applet);
		return apps.elements();
	}

	@Override
	public AudioClip getAudioClip(final URL url) {
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED getAudioClip="
				+ url);
	}

	@Override
	public URL getCodeBase() {
		return codeBase;
	}

	@Override
	public URL getDocumentBase() {
		return documentBase;
	}

	@Override
	public Image getImage(final URL url) {
		synchronized (IMAGE_CACHE) {
			WeakReference<Image> ref = IMAGE_CACHE.get(url);
			Image img;
			if (ref == null || (img = ref.get()) == null) {
				img = Toolkit.getDefaultToolkit().createImage(url);
				ref = new WeakReference<Image>(img);
				IMAGE_CACHE.put(url, ref);
			}
			return img;
		}
	}

	@Override
	public String getParameter(final String s) {
		final String parameter = parameters.get(s);
		if (s != null) {
			return parameter;
		}
		return "";
	}

	@Override
	public InputStream getStream(final String key) {
		return INPUT_CACHE.get(key);
	}

	@Override
	public Iterator<String> getStreamKeys() {
		return Collections.unmodifiableSet(INPUT_CACHE.keySet()).iterator();
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	public void setActive(final boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public void setStream(final String key, final InputStream stream)
			throws IOException {
		INPUT_CACHE.put(key, stream);
	}

	@Override
	public void showDocument(final URL url) {
		showDocument(url, "");
	}

	@Override
	public void showDocument(final URL url, final String target) {
		if (url.toString().contains("outofdate")) {
			final String message = Configuration.NAME
					+ " is currently outdated, please wait patiently for a new version.";
			log.severe(message);
			JOptionPane.showMessageDialog(null, message, "Outdated", JOptionPane.WARNING_MESSAGE);
			final File versionFile = new File(Configuration.Paths.getVersionCache());
			if (versionFile.exists() && !versionFile.delete()) {
				log.warning("Unable to clear cache.");
			}
		} else if (!target.equals("tbi")) {
			log.info("Attempting to show: " + url.toString() + " [" + target
					+ "]");
		}
	}

	@Override
	public void showStatus(final String status) {
		log.info("Status: " + status);
	}
}
