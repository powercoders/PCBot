package org.rsbot.service;

import org.rsbot.Configuration;
import org.rsbot.bot.RSLoader;
import org.rsbot.util.io.IniParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

public class Preferences {
	private final Logger log = Logger.getLogger(Preferences.class.getName());
	private static Preferences instance;
	private final File store;

	public boolean confirmations = true;
	public boolean shutdown = false;
	public int shutdownTime = 10;


	private Preferences(final File store) {
		this.store = store;
	}

	public static Preferences getInstance() {
		if (instance == null) {
			instance = new Preferences(new File(Configuration.Paths.getSettingsDirectory(), "preferences.ini"));
		}
		return instance;
	}

	public void load() {
		HashMap<String, String> keys = null;
		try {
			if (!store.exists()) {
				if (!store.createNewFile()) {
					throw new IOException("Could not create a new file.");
				}
			}
			keys = IniParser.deserialise(store).get(IniParser.emptySection);
		} catch (final IOException ignored) {
			log.severe("Failed to load preferences");
		}
		if (keys == null || keys.isEmpty()) {
			return;
		}
		if (keys.containsKey("confirmations")) {
			confirmations = IniParser.parseBool(keys.get("confirmations"));
		}
		if (keys.containsKey("shutdown")) {
			shutdown = IniParser.parseBool(keys.get("shutdown"));
		}
		if (keys.containsKey("shutdownTime")) {
			shutdownTime = Integer.parseInt(keys.get("shutdownTime"));
			shutdownTime = Math.max(Math.min(shutdownTime, 60), 3);
		}
	}

	public void save() {
		final HashMap<String, String> keys = new HashMap<String, String>(3);
		keys.put("confirmations", Boolean.toString(confirmations));
		keys.put("shutdown", Boolean.toString(shutdown));
		keys.put("shutdownTime", Integer.toString(shutdownTime));

		final HashMap<String, HashMap<String, String>> data = new HashMap<String, HashMap<String, String>>(1);
		data.put(IniParser.emptySection, keys);
		try {
			final BufferedWriter out = new BufferedWriter(new FileWriter(store));
			IniParser.serialise(data, out);
			out.close();
		} catch (final IOException ignored) {
			log.severe("Could not save preferences");
		}
	}
}
