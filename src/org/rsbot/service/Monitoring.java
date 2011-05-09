package org.rsbot.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.rsbot.util.GlobalConfiguration;


/**
 * @author Paris
 */
public class Monitoring {
	private static ConcurrentLinkedQueue<Event> events = null;

	public static void start() {
		if (events == null) {
			events = new ConcurrentLinkedQueue<Event>();
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

		pushState(Type.ENVIRONMENT, "VERSION", Integer.toString(GlobalConfiguration.getVersion()));
		pushState(Type.ENVIRONMENT, "OS", GlobalConfiguration.getCurrentOperatingSystem().toString());
		pushState(Type.ENVIRONMENT, "JAR", Boolean.toString(GlobalConfiguration.RUNNING_FROM_JAR));
	}

	public static void stop() {
		pushState(Type.STOP);
		send();
	}

	public static void send() {
		if (events.isEmpty()) {
			return;
		}
		final StringBuilder s = new StringBuilder(4096);
		while (!events.isEmpty()) {
			s.append(events.poll().toString());
			s.append("\r\n");
		}
		final String log = s.toString();

		try {
			final FileWriter out = new FileWriter(GlobalConfiguration.Paths.getEventsLog());
			out.write(log);
			out.close();
		} catch (IOException ignored) {
		}
	}

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
		StringBuilder s = new StringBuilder(mac.length * 2);
		for (int i = 0; i < mac.length; i++) {
			s.append(String.format("%02X%s", mac[i], i < mac.length - 1 ? "-" : ""));
		}
		return s.toString();
	}

	public static void pushState(final Type type, final String... args) {
		final Event e = new Event(type);
		e.setArgs(args);
		events.add(e);
	}

	public static enum Type { START, STOP, SYSTEM, ENVIRONMENT, SCRIPT, RANDOM }

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
}
