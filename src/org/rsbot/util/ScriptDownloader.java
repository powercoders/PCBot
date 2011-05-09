package org.rsbot.util;

import java.util.logging.Logger;

public class ScriptDownloader {
	private static final Logger log = Logger.getLogger(ScriptDownloader.class.getName());

	public static void save(String source) {
		source = source.trim();
		if (!source.startsWith("http://")) {
			log.warning("Invalid URL");
			return;
		}
		log.warning("Not implemented");
	}
}
