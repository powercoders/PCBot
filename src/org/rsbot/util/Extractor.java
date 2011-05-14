package org.rsbot.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.rsbot.util.io.IOHelper;

public class Extractor implements Runnable {

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
			IOHelper.write(in, output);
		}
	}
}
