package org.rsbot.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.rsbot.Configuration;
import org.rsbot.util.io.HttpClient;
import org.rsbot.util.io.IniParser;

/**
 * @author Paris
 */
public class Monitoring {
	private static class Event {
		private final int time;
		private final Type type;
		private String[] args;

		public Event(final Type type) {
			time = (int) (System.currentTimeMillis() / 1000);
			this.type = type;
		}

		public String[] getArgs() {
			return args;
		}

		public void setArgs(final String[] args) {
			this.args = args;
		}

		@Override
		public String toString() {
			final char p = ' ';
			final StringBuilder s = new StringBuilder(32);
			s.append(time);
			s.append(p);
			s.append(type.toString());
			for (final String arg : getArgs()) {
				s.append(p);
				s.append(arg);
			}
			return s.toString();
		}
	}

	public static enum Type {
		START, STOP, SYSTEM, ENVIRONMENT, SCRIPT, RANDOM
	}

	private final static File logFile = new File(Configuration.Paths.getCacheDirectory(), "events.log");
	private static ConcurrentLinkedQueue<Event> events = null;
	private static boolean enabled = false;

	private static String uri;

	public static boolean send = true;

	private static String getMacAddress() {
		final byte[] mac;
		try {
			mac = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
			if (mac == null) {
				throw new NullPointerException();
			}
		} catch (final Exception ignored) {
			return null;
		}
		final StringBuilder s = new StringBuilder(mac.length * 2);
		for (int i = 0; i < mac.length; i++) {
			s.append(String.format("%02X%s", mac[i], i < mac.length - 1 ? "-"
					: ""));
		}
		return s.toString();
	}

	private static void init() {
		if (events != null) {
			return;
		}

		events = new ConcurrentLinkedQueue<Event>();
		HashMap<String, String> keys;

		try {
			final URL source = new URL(Configuration.Paths.URLs.MONITORING_CONTROL);
			final File cache = new File(Configuration.Paths.getCacheDirectory(), "monitoring-control.txt");
			HttpClient.download(source, cache);
			keys = IniParser.deserialise(cache).get(IniParser.emptySection);
		} catch (final IOException ignored) {
			return;
		}

		if (keys == null || keys.isEmpty() || !keys.containsKey("enabled")
				|| !IniParser.parseBool(keys.get("enabled"))) {
			return;
		}

		if (keys.containsKey("uri")) {
			uri = keys.get(uri);
			enabled = !uri.isEmpty();
		}
	}

	public static boolean isEnabled() {
		return send;
	}

	public static void pushState(final Type type, final String... args) {
		if (!enabled) {
			return;
		}
		final Event e = new Event(type);
		e.setArgs(args);
		events.add(e);
	}

	public static void send() throws IOException, URISyntaxException {
		if (events.isEmpty()) {
			return;
		}
		final StringBuilder s = new StringBuilder(4096);
		while (!events.isEmpty()) {
			s.append(events.poll().toString());
			s.append("\r\n");
		}
		final String log = s.toString();

		final FileWriter out = new FileWriter(logFile);
		out.write(log);
		out.close();

		if (isEnabled()) {
			final URI sync = new URI(uri);

			if (sync.getScheme().equals("udp")) {
				uploadUdp(sync, log.getBytes());
			} else if (sync.getScheme().equals("http")) {
				uploadHttp(sync.toURL(), log);
			}
		}
	}

	public static void setEnabled(final boolean enabled) {
		send = enabled;
	}

	public static void start() {
		init();

		if (!enabled) {
			return;
		}

		events.clear();
		pushState(Type.START);

		final String mac = getMacAddress();
		if (mac != null && !mac.isEmpty()) {
			pushState(Type.SYSTEM, "HOST", mac);
		}

		pushState(Type.SYSTEM, "CPU", "PROCESSORS", Integer.toString(Runtime.getRuntime().availableProcessors()));
		pushState(Type.SYSTEM, "MEMORY", "FREE", Long.toString(Runtime.getRuntime().freeMemory()));
		pushState(Type.SYSTEM, "MEMORY", "MAX", Long.toString(Runtime.getRuntime().maxMemory()));
		pushState(Type.SYSTEM, "MEMORY", "TOTAL", Long.toString(Runtime.getRuntime().totalMemory()));

		long diskCount = 0, diskTotal = 0, diskFree = 0, diskUsable = 0;
		for (final File root : File.listRoots()) {
			diskCount++;
			diskTotal += root.getTotalSpace();
			diskFree += root.getFreeSpace();
			diskUsable += root.getUsableSpace();
		}
		pushState(Type.SYSTEM, "DISK", "COUNT", Long.toString(diskCount));
		pushState(Type.SYSTEM, "DISK", "TOTAL", Long.toString(diskTotal));
		pushState(Type.SYSTEM, "DISK", "FREE", Long.toString(diskFree));
		pushState(Type.SYSTEM, "DISK", "USABLE", Long.toString(diskUsable));

		pushState(Type.ENVIRONMENT, "VERSION", Integer.toString(Configuration.getVersion()));
		pushState(Type.ENVIRONMENT, "OS", Configuration.getCurrentOperatingSystem().toString());
		pushState(Type.ENVIRONMENT, "JAR", Boolean.toString(Configuration.RUNNING_FROM_JAR));
		pushState(Type.ENVIRONMENT, "GIT", Boolean.toString(new File(Configuration.Paths.ROOT, ".git").exists()));
		pushState(Type.ENVIRONMENT, "SVN", Boolean.toString(new File(Configuration.Paths.ROOT, ".svn").exists()));
	}

	public static void stop() {
		if (!enabled) {
			return;
		}
		pushState(Type.STOP);
		try {
			send();
		} catch (final Exception ignored) {
		}
	}

	private static void uploadHttp(final URL url, final String data)
			throws IOException {
		final HttpURLConnection con = HttpClient.getHttpConnection(url);
		con.setDoOutput(true);
		final OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
		out.write(data);
		out.flush();
		con.getInputStream().read();
		out.close();
		con.getInputStream().close();
		con.disconnect();
	}

	private static void uploadUdp(final URI uri, final byte[] data)
			throws IOException {
		final InetAddress host = InetAddress.getByName(uri.getHost());
		final DatagramSocket client = new DatagramSocket(uri.getPort(), host);
		final DatagramPacket payload = new DatagramPacket(data, data.length);
		client.send(payload);
		client.close();
	}
}
