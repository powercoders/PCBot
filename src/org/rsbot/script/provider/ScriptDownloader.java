package org.rsbot.script.provider;

import org.rsbot.Configuration;
import org.rsbot.util.io.HttpClient;
import org.rsbot.util.io.IOHelper;
import org.rsbot.util.io.JavaCompiler;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptDownloader {
	private static final Logger log = Logger.getLogger(ScriptDownloader.class.getName());

	public static void save(String sourceURL) {
		if (!JavaCompiler.isAvailable()) {
			log.warning("JDK is not installed");
			return;
		}
		sourceURL = sourceURL.trim();
		if (sourceURL.startsWith("https:")) {
			sourceURL = "http" + sourceURL.substring(5);
		}
		if (!sourceURL.startsWith("http://")) {
			log.warning("Invalid URL");
			return;
		}
		sourceURL = normalisePastebin(sourceURL);
		log.fine("Downloading: " + sourceURL);
		final File temporaryFile = new File(Configuration.Paths.getGarbageDirectory(), Integer.toString(sourceURL.hashCode()) + ".script.bin");
		HttpURLConnection httpURLConnection = null;
		try {
			httpURLConnection = HttpClient.download(new URL(sourceURL), temporaryFile);
		} catch (Exception e) {
			log.warning("Could not download script");
		}
		String className = classFileName(temporaryFile);
		if (className != null) {
			final File saveTo = new File(Configuration.Paths.getScriptsPrecompiledDirectory());
			if (temporaryFile.renameTo(saveTo)) {
				log.info("Saved precompiled script " + className);
			} else {
				log.warning("Could not save precompiled script " + className);
			}
			return;
		}
		final byte[] scriptBytes = IOHelper.read(temporaryFile);
		if (scriptBytes == null) {
			log.severe("Could not read downloaded file");
			return;
		}
		String source = new String(scriptBytes);
		if (httpURLConnection.getContentType().contains("html")) {
			final int z = source.indexOf("<body");
			if (z != -1) {
				source = source.substring(z);
			}
			source = source.replaceAll("\\<br\\s*\\/?\\s*\\>", "\r\n");
			source = source.replaceAll("\\<.*?\\>", "");
			source = source.replaceAll("&nbsp;", " ");
			source = source.replaceAll("&quot;", "\"");
			source = source.replaceAll("&lt;", "<");
			source = source.replaceAll("&gt;", ">");
			source = source.replaceAll("&amp;", "&");
		}
		final Matcher m = Pattern.compile("public\\s+class\\s+(\\w+)\\s+extends\\s+Script").matcher(source);
		if (!m.find()) {
			log.severe("Specified URL is not a script");
			return;
		}
		className = m.group(1);
		final File saveDirectory = new File(Configuration.Paths.getScriptsSourcesDirectory());
		if (!saveDirectory.exists()) {
			saveDirectory.mkdirs();
		}
		final File classFile = new File(saveDirectory, className + ".java");
		try {
			final FileWriter fileWriterOut = new FileWriter(classFile);
			fileWriterOut.write(source);
			fileWriterOut.close();
		} catch (IOException ignored) {
			log.severe("Could not save script " + className);
			return;
		}
		String compileClassPath;
		if (Configuration.RUNNING_FROM_JAR) {
			compileClassPath = Configuration.Paths.getRunningJarPath();
		} else {
			compileClassPath = new File(Configuration.Paths.ROOT + File.separator + "bin").getAbsolutePath();
		}
		final boolean result = JavaCompiler.run(classFile, compileClassPath);
		if (result) {
			log.info("Compiled script " + className);
		} else {
			log.warning("Could not compile script " + className);
		}
	}

	private static String classFileName(final File path) {
		final int magic = 0xCAFEBABE;
		if (!path.exists()) {
			return null;
		}
		FileReader reader = null;
		int header;
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
				} catch (IOException ignored) {
				}
			}
		}
		return null;
	}

	private static String normalisePastebin(String sourceURL) {
		if (sourceURL.contains("gist.github.com")) {
			return gistRaw(sourceURL);
		}
		final HashMap<String, String> map = new HashMap<String, String>(8);
		map.put("pastebin\\.com/(\\w+)", "pastebin.com/raw.php?i=$1");
		map.put("pastie\\.org/(?:pastes/)?(\\d+)", "pastie.org/pastes/$1/text");
		map.put("pastebin\\.ca/(\\d+)", "pastebin.ca/raw/$1");
		map.put("sprunge\\.us/(\\w+)(?:\\?.*)?", "sprunge.us/$1");
		map.put("codepad\\.org/(\\w+)", "codepad.org/$1/raw.txt");
		for (final Entry<String, String> entry : map.entrySet()) {
			sourceURL = sourceURL.replaceAll(entry.getKey(), entry.getValue());
		}
		return sourceURL;
	}

	private static String gistRaw(final String gistURL) {
		Matcher m = Pattern.compile("gist\\.github\\.com/(\\d+)", Pattern.CASE_INSENSITIVE).matcher(gistURL);
		if (!m.find()) {
			return gistURL;
		}
		final String id = m.group(1);
		String meta;
		try {
			meta = HttpClient.downloadAsString(new URL("http://gist.github.com/api/v1/json/" + id));
		} catch (final Exception ignored) {
			return gistURL;
		}
		m = Pattern.compile("\"files\":\\s*\\[\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE).matcher(meta);
		if (!m.find()) {
			return gistURL;
		}
		final String file = m.group(1);
		return "http://gist.github.com/raw/" + id + "/" + file;
	}
}
