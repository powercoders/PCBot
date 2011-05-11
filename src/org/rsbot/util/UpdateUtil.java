package org.rsbot.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.rsbot.gui.BotGUI;

/**
 * @author Paris
 */
public final class UpdateUtil {
	private static final Logger log = Logger.getLogger(UpdateUtil.class.getName());
	private static int latest = -1;

	public static void check(final BotGUI instance) {
		if (GlobalConfiguration.getVersion() >= getLatestVersion()) {
			return;
		}
		log.info("New version available");
		final int update = JOptionPane.showConfirmDialog(instance,
				"A newer version is available. Do you wish to update?", "Update Found", JOptionPane.YES_NO_OPTION);
		if (update != 0) {
			return;
		}
		try {
			update(instance);
		} catch (final Exception e) {
			log.warning("Unable to apply update");
		}
	}

	public static int getLatestVersion() {
		if (latest != -1) {
			return latest;
		}
		final File cache = new File(GlobalConfiguration.Paths.getCacheDirectory(), "version-latest.txt");
		BufferedReader reader = null;
		try {
			HttpClient.download(new URL(GlobalConfiguration.Paths.URLs.VERSION), cache);
			reader = new BufferedReader(new FileReader(cache));
			final String s = reader.readLine().trim();
			reader.close();
			latest = Integer.parseInt(s);
			return latest;
		} catch (final Exception ignored) {
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (final IOException ignored) {
			}
		}
		log.warning("Unable to obtain latest version information");
		return -1;
	}

	private static void update(final BotGUI instance) throws MalformedURLException, IOException {
		log.info("Downloading update...");
		final File jarNew = new File(GlobalConfiguration.NAME + "-" + getLatestVersion() + ".jar");
		HttpClient.download(new URL(GlobalConfiguration.Paths.URLs.DOWNLOAD), jarNew);
		final String jarOld = GlobalConfiguration.Paths.getRunningJarPath();
		Runtime.getRuntime().exec("java -jar \"" + jarNew + "\" delete \"" + jarOld + "\"");
		instance.cleanExit(true);
	}
}
