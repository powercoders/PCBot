package org.rsbot.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rsbot.Configuration;
import org.rsbot.Configuration.OperatingSystem;
import org.rsbot.util.io.HttpClient;
import org.rsbot.util.io.IOHelper;

public class ScriptDownloader {
	private static final Logger log = Logger.getLogger(ScriptDownloader.class.getName());

	public static void save(String source) {
		final String javac = findJavac();
		if (javac == null || javac.length() == 0) {
			log.warning("JDK is not installed");
			return;
		}
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
		final File output = new File(Configuration.Paths.getGarbageDirectory(), Integer.toString(source.hashCode()) + ".script.bin");
		HttpURLConnection con = null;
		try {
			con = HttpClient.download(new URL(source), output);
		} catch (Exception e) {
			log.warning("Could not download script");
		}
		String name = classFileName(output);
		if (name != null) {
			final File saveto = new File(Configuration.Paths.getScriptsPrecompiledDirectory());
			if (output.renameTo(saveto)) {
				log.info("Saved precompiled script " + name);
			} else {
				log.warning("Could not save precompiled script " + name);
			}
			return;
		}
		final byte[] bytes = IOHelper.read(output);
		if (bytes == null) {
			log.severe("Could not read downloaded file");
			return;
		}
		String text = new String(bytes);
		if (con.getContentType().contains("html")) {
			text = text.replaceAll("\\<head.*\\<\\/head\\>", "");
			text = text.replaceAll("\\<br\\s*\\/?\\s*\\>", "\r\n");
			text = text.replaceAll("\\<.*?\\>", "");
		}
		final Matcher m = Pattern.compile("public\\s+class\\s+(\\w+)\\s+extends\\s+Script").matcher(text);
		if (!m.find()) {
			log.severe("Specified URL is not a script");
			return;
		}
		name = m.group(1);
		final File dir = new File(Configuration.Paths.getScriptsSourcesDirectory());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		final File saveto = new File(dir, name + ".java");
		try {
			final FileWriter out = new FileWriter(saveto);
			out.write(text);
			out.close();
		} catch (IOException ignored) {
			log.severe("Could not save script " + name);
			return;
		}
		String classpath;
		if (Configuration.RUNNING_FROM_JAR) {
			classpath = Configuration.Paths.getRunningJarPath();
		} else {
			classpath = new File(Configuration.Paths.ROOT + File.separator + "bin").getAbsolutePath();
		}
		try {
			Runtime.getRuntime().exec(new String[]{javac, "-cp", classpath, saveto.getAbsolutePath()});
		} catch (IOException e) {
			log.severe("Could not compile script " + name);
			return;
		}
		log.info("Compiled script " + name);
	}

	private static String readProcess(final String exec) throws IOException {
		final Process process = Runtime.getRuntime().exec(exec);
		final InputStream is = process.getInputStream();
		try {
			process.waitFor();
		} catch (final InterruptedException ignored) {
			return null;
		}
		final StringBuilder s = new StringBuilder(256);
		int r;
		while ((r = is.read()) != -1) {
			s.append((char) r);
		}
		return s.toString();
	}

	private static String classFileName(final File path) {
		final int magic = 0xCAFEBABE;
		if (!path.exists()) {
			return null;
		}
		FileReader reader = null;
		int header = -1;
		try {
			reader = new FileReader(path);
			header = reader.read();
			if (header != magic) {
				return null;
			}
			for (int i = 1; i < 16; i++) {
				reader.read();
			}
			StringBuilder name = new StringBuilder(32);
			int r;
			while ((r = reader.read()) != -1) {
				if (r == 0x7) {
					return name.length() == 0 ? null : name.toString();
				}
				if (name.append((char) r).length() > 0x10000) { // obviously in after over 9000
					return null;
				}
			}
		} catch (IOException ignored) {
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ignored){
				}
			}
		}
		return null;
	}

	private static String findJavac() {
		try {
			if (Configuration.getCurrentOperatingSystem() == OperatingSystem.WINDOWS) {
				String version = readProcess("REG QUERY \"HKLM\\SOFTWARE\\JavaSoft\\Java Development Kit\" /v CurrentVersion");
				version = version.substring(version.indexOf("REG_SZ") + 6).trim();
				String path = readProcess("REG QUERY \"HKLM\\SOFTWARE\\JavaSoft\\Java Development Kit\\" + version + "\" /v JavaHome");
				path = path.substring(path.indexOf("REG_SZ") + 6).trim() + "\\bin\\javac.exe";
				return new File(path).exists() ? path : null;
			} else {
				String which = readProcess("which javac");
				return which == null || which.length() == 0 ? null : which.trim();
			}
		} catch (Exception ignored) {
			return null;
		}
	}

	private static String normalisePastebin(String source) {
		if (source.contains("gist.github.com")) {
			return gistRaw(source);
		}
		final HashMap<String, String> map = new HashMap<String, String>(8);
		map.put("pastebin\\.com/(\\w+)", "pastebin.com/raw.php?i=$1");
		map.put("pastie\\.org/(?:pastes/)?(\\d+)", "pastie.org/pastes/$1/text");
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
