package org.rsbot.util.io;

import org.rsbot.Configuration;
import org.rsbot.Configuration.OperatingSystem;

import javax.tools.ToolProvider;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;

public class JavaCompiler {
	private final static String JAVACARGS = "-g:none";

	public static boolean run(final File source, final String classPath) {
		final javax.tools.JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
		try {
			if (javac != null) {
				return compileNative(javac, new FileInputStream(source), classPath) == 0;
			} else {
				compileSystem(source, classPath);
				return true;
			}
		} catch (final IOException ignored) {
		}
		return false;
	}

	public static boolean isAvailable() {
		return !(ToolProvider.getSystemJavaCompiler() == null && findJavac() == null);
	}

	public static boolean compileWeb(final String source, final File out) {
		try {
			HttpClient.download(new URL(source + "?v=" + Integer.toString(Configuration.getVersion()) + "&s=" + URLEncoder.encode(source, "UTF-8")), out);
		} catch (final Exception ignored) {
			return false;
		}
		if (out.length() == 0) {
			out.delete();
		}
		return out.exists();
	}

	private static int compileNative(final javax.tools.JavaCompiler javac, final InputStream source, final String classPath) throws FileNotFoundException {
		final FileOutputStream[] out = new FileOutputStream[2];
		for (int i = 0; i < 2; i++) {
			out[i] = new FileOutputStream(new File(Configuration.Paths.getGarbageDirectory(), "compile." + Integer.toString(i) + ".txt"));
		}
		return javac.run(source, out[0], out[1], JAVACARGS, "-cp", classPath);
	}

	private static void compileSystem(final File source, final String classPath) throws IOException {
		String javac = findJavac();
		if (javac == null) {
			throw new IOException();
		}
		Runtime.getRuntime().exec(new String[]{javac, JAVACARGS, "-cp", classPath, source.getAbsolutePath()});
	}

	private static String findJavac() {
		try {
			if (Configuration.getCurrentOperatingSystem() == OperatingSystem.WINDOWS) {
				String currentVersion = readProcess("REG QUERY \"HKLM\\SOFTWARE\\JavaSoft\\Java Development Kit\" /v CurrentVersion");
				currentVersion = currentVersion.substring(currentVersion.indexOf("REG_SZ") + 6).trim();
				String binPath = readProcess("REG QUERY \"HKLM\\SOFTWARE\\JavaSoft\\Java Development Kit\\" + currentVersion + "\" /v JavaHome");
				binPath = binPath.substring(binPath.indexOf("REG_SZ") + 6).trim() + "\\bin\\javac.exe";
				return new File(binPath).exists() ? binPath : null;
			} else {
				String whichQuery = readProcess("which javac");
				return whichQuery == null || whichQuery.length() == 0 ? null : whichQuery.trim();
			}
		} catch (Exception ignored) {
			return null;
		}
	}

	private static String readProcess(final String exec) throws IOException {
		final Process compiler = Runtime.getRuntime().exec(exec);
		final InputStream is = compiler.getInputStream();
		try {
			compiler.waitFor();
		} catch (final InterruptedException ignored) {
			return null;
		}
		final StringBuilder result = new StringBuilder(256);
		int r;
		while ((r = is.read()) != -1) {
			result.append((char) r);
		}
		return result.toString();
	}
}
