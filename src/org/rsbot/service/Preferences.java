package org.rsbot.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import org.rsbot.Configuration;
import org.rsbot.bot.RSLoader;
import org.rsbot.util.io.IniParser;

public class Preferences {
	private final Logger log = Logger.getLogger(Preferences.class.getName());
	private static Preferences instance;
	private final File store;

	public boolean hideAds = false;
	public String user = "";
	public boolean confirmations = true;
	public boolean shutdown = false;
	public int shutdownTime = 10;
	public boolean web = false;
	public String webBind = "localhost:9500";
	public boolean webPassRequire = false;
	public String webPass = "";
	public boolean patchBeta = false;
	public boolean sdnShow = true;

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
		if (keys.containsKey("user")) {
			user = keys.get("user");
		}
		if (keys.containsKey("hideAds")) {
			hideAds = IniParser.parseBool(keys.get("hideAds"));
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
		if (keys.containsKey("web")) {
			web = IniParser.parseBool(keys.get("web"));
		}
		if (keys.containsKey("webBind")) {
			webBind = keys.get("webBind");
		}
		if (keys.containsKey("webPassRequire")) {
			webPassRequire = IniParser.parseBool(keys.get("webPassRequire"));
		}
		if (keys.containsKey("webPass")) {
			webPass = keys.get("webPass");
		}
		if (keys.containsKey("patchBeta")) {
			patchBeta = IniParser.parseBool(keys.get("patchBeta"));
			RSLoader.runBeta = Configuration.RUNNING_FROM_JAR ? false : patchBeta;
		}
		if (keys.containsKey("sdnShow")) {
			sdnShow = IniParser.parseBool(keys.get("sdnShow"));
		}
	}

	public void save() {
		final HashMap<String, String> keys = new HashMap<String, String>(10);
		keys.put("user", user);
		keys.put("hideAds", Boolean.toString(hideAds));
		keys.put("confirmations", Boolean.toString(confirmations));
		keys.put("shutdown", Boolean.toString(shutdown));
		keys.put("shutdownTime", Integer.toString(shutdownTime));
		keys.put("web", Boolean.toString(web));
		keys.put("webBind", webBind);
		keys.put("webPassRequire", Boolean.toString(webPassRequire));
		keys.put("webPass", webPass);
		keys.put("patchBeta", Boolean.toString(patchBeta));
		keys.put("sdnShow", Boolean.toString(sdnShow));
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
