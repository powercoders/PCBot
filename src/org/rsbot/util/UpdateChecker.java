package org.rsbot.util;

import org.rsbot.Configuration;
import org.rsbot.gui.BotGUI;
import org.rsbot.util.io.HttpClient;
import org.rsbot.util.io.IOHelper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * @author Paris
 */
public final class UpdateChecker {
	private static int latest = -1;
	public static boolean error = false;
	private static final Logger log = Logger.getLogger(UpdateChecker.class.getName());

	public static boolean isError() {
		getLatestVersion();
		return error;
	}

	public static boolean isDeprecatedVersion() throws IOException {
		final File cache = Configuration.Paths.getCachableResources().get(Configuration.Paths.URLs.VERSION_KILL);
		final int kill = Integer.parseInt(IOHelper.readString(cache).trim());
		return kill > Configuration.getVersion();
	}

	public static int getLatestVersion() {
		if (latest != -1) {
			return latest;
		}
		try {
			final File cache = Configuration.Paths.getCachableResources().get(Configuration.Paths.URLs.VERSION);
			latest = Integer.parseInt(IOHelper.readString(cache).trim());
		} catch (final Exception ignored) {
			error = true;
		}
		return latest;
	}

	public static void update(final BotGUI instance) throws IOException {
		log.info("Downloading update...");
		final File jarNew = new File(Configuration.NAME + "-" + getLatestVersion() + ".jar");
		HttpClient.download(new URL(Configuration.Paths.URLs.DOWNLOAD), jarNew);
		final String jarOld = Configuration.Paths.getRunningJarPath();
		Runtime.getRuntime().exec("java -jar \"" + jarNew + "\" --delete \"" + jarOld + "\"");
		instance.cleanExit(true);
	}
}
