package org.rsbot.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptDownloader {
	private static final Logger log = Logger.getLogger(ScriptDownloader.class.getName());

	public static void save(String source) {
		source = source.trim();
		if (source.startsWith("https:")) {
			source = "http" + source.substring(5);
		}
		if (!source.startsWith("http://")) {
			log.warning("Invalid URL");
			return;
		}
		source = normalisePastebin(source);
		log.fine("Downloading: " + source);
		log.warning("Not implemented");
	}

	private static String normalisePastebin(String source) {
		if (source.contains("gist.github.com")) {
			return gistRaw(source);
		}
		final HashMap<String, String> map = new HashMap<String, String>(8);
		map.put("pastebin\\.com/(\\w+)", "pastebin.com/raw.php?i=$1");
		map.put("pastie\\.org/pastes/(\\d+)", "pastie.org/pastes/$1/text");
		map.put("pastebin\\.ca/(\\d+)", "pastebin.ca/raw/$1");
		map.put("sprunge\\.us/(\\w+)(?:\\?.*)?", "sprunge.us/$1");
		map.put("codepad\\.org/(\\w+)", "codepad.org/$1/raw.txt");
		for (final Entry<String, String> entry : map.entrySet()) {
			source = source.replaceAll(entry.getKey(), entry.getValue());
		}
		return source;
	}

	private static String gistRaw(final String source) {
		Matcher m = Pattern.compile("gist\\.github\\.com/(\\d+)", Pattern.CASE_INSENSITIVE).matcher(source);
		if (!m.find()) {
			return source;
		}
		final String id = m.group(1);
		String meta;
		try {
			meta = HttpClient.downloadAsString(new URL("http://gist.github.com/api/v1/json/" + id));
		} catch (final Exception ignored) {
			return source;
		}
		m = Pattern.compile("\"files\":\\s*\\[\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE).matcher(meta);
		if (!m.find()) {
			return source;
		}
		final String file = m.group(1);
		return "http://gist.github.com/raw/" + id + "/" + file;
	}
}
