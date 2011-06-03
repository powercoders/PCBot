package org.rsbot.util;

import java.io.File;
import java.io.IOException;

import org.rsbot.Configuration;
import org.rsbot.util.io.IOHelper;

/**
 * @author Paris
 */
public final class UpdateChecker {
	private static int latest = -1;
	public static boolean error = false;

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
}
