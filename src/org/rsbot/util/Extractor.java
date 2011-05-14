package org.rsbot.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Extractor implements Runnable {
	private static void saveTo(final InputStream in, final File output) {
		OutputStream out = null;
		try {
			out = new FileOutputStream(output);
			final byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} catch (final IOException ignored) {
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (final IOException ignored) {
			}
		}
	}

	public Extractor() {
	}

	public void run() {
		final ArrayList<String> extract = new ArrayList<String>(2);
		if (GlobalConfiguration.getCurrentOperatingSystem() == GlobalConfiguration.OperatingSystem.WINDOWS) {
			extract.add(GlobalConfiguration.Paths.COMPILE_SCRIPTS_BAT);
			extract.add(GlobalConfiguration.Paths.COMPILE_FIND_JDK);
		} else {
			extract.add(GlobalConfiguration.Paths.COMPILE_SCRIPTS_SH);
		}
		for (final String item : extract) {
			final String path = GlobalConfiguration.Paths.Resources.ROOT + "/" + item;
			final InputStream in;
			try {
				in = GlobalConfiguration.getResourceURL(path).openStream();
			} catch (IOException ignored) {
				continue;
			}
			final File output = new File(GlobalConfiguration.Paths.getHomeDirectory(), item);
			saveTo(in, output);
		}
	}

	public void clearDirectory(final File path, final boolean deleteParent) {
		if (!path.exists()) {
			return;
		}
		for (final File file : path.listFiles()) {
			if (file.isDirectory()) {
				clearDirectory(file, true);
			} else {
				file.delete();
			}
		}
		if (deleteParent) {
			path.delete();
		}
	}
}
