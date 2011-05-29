package org.rsbot.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;

import org.rsbot.Configuration;
import org.rsbot.util.StringUtil;
import org.rsbot.util.io.HttpClient;
import org.rsbot.util.io.IOHelper;

public class DRM {
	private static Logger log = Logger.getLogger(DRM.class.getName());
	public static final String DEFAULTKEY = "0000000000000000000000000000000000000000";

	public static String getServiceKey() {
		final File key = new File(Configuration.Paths.getServiceKey());
		if (key.exists() && key.canRead()) {
			return StringUtil.newStringUtf8(IOHelper.read(key));
		} else {
			return DEFAULTKEY;
		}
	}

	public static boolean login(final String user, final String pass) {
		if (user == null || user.length() == 0) {
			if (new File(Configuration.Paths.getServiceKey()).delete()) {
				log.info("Successfully logged out of services");
			}
			return false;
		}
		try {
			final URL source = new URL(Configuration.Paths.URLs.SERVICELOGIN
					+ "?u=" + URLEncoder.encode(user, "UTF-8") + "&p="
					+ URLEncoder.encode(pass, "UTF-8"));
			final String key = HttpClient.downloadAsString(source);
			if (key == null || key.length() == 0) {
				log.warning("Invalid service username or password");
				return false;
			}
			final BufferedWriter out = new BufferedWriter(new FileWriter(Configuration.Paths.getServiceKey()));
			out.write(key);
			out.close();
			log.info("Successfully logged into services");
			return true;
		} catch (final IOException ignored) {
			log.warning("Unable to log into services");
			return false;
		}
	}
}
