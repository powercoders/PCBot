package org.pcbot.net.methods;

import org.rsbot.script.methods.Lobby;
import org.rsbot.script.methods.MethodContext;
import org.rsbot.script.methods.MethodProvider;
import org.rsbot.script.wrappers.RSComponent;

public class Worlds extends MethodProvider {
	public static final int INTERFACE_MEMBERS_TEXTURE = 1531;
	public static final int INTERFACE_WORLD_SELECT_STARS = 70;

	public Worlds(final MethodContext ctx) {
		super(ctx);
	}

	/**
	 * Opens the world selection tab.
	 *
	 * @return <tt>true</tt> if opened.
	 */
	public boolean openTab() {
		return methods.lobby.inLobby() && methods.lobby.open(Lobby.TAB_WORLD_SELECT);
	}

	/**
	 * Gets the component index of a server.
	 *
	 * @param server The server.
	 * @return The index of the server, -1 if not found.
	 */
	public int getWorldIndex(final int server) {
		if (openTab()) {
			int index = -1;
			for (int i = 0; i < methods.interfaces.getComponent(Lobby.WORLD_SELECT_INTERFACE, Lobby.WORLD_SELECT_INTERFACE_WORLD_NAME).getComponents().length; i++) {
				final RSComponent comp = methods.interfaces.getComponent(Lobby.WORLD_SELECT_INTERFACE, Lobby.WORLD_SELECT_INTERFACE_WORLD_NAME).getComponents()[i];
				if (comp != null) {
					final String number = comp.getText();
					if (Integer.parseInt(number) == server) {
						index = i;
						break;
					}
				}
			}
			return index;
		}
		return -1;
	}

	/**
	 * Gets the average ping of a server.
	 *
	 * @param server The server.
	 * @return The average ping.
	 */
	public long averagePing(final int server) {
		long ping = 0;
		for (int i = 0; i < 5; i++) {
			ping += ping(server);
		}
		return Math.round(ping / 5);
	}

	/**
	 * Gets the raw ping of a server.
	 *
	 * @param server The server.
	 * @return The raw ping.
	 */
	public long ping(final int server) {
		try {
			if (server == -1) {
				return -1;
			}
			final String hostAddress = "world" + Integer.toString(server) + ".runescape.com";
			java.net.SocketAddress socketAddress = new java.net.InetSocketAddress(java.net.InetAddress.getByName(hostAddress), 80);
			java.net.Socket socket = new java.net.Socket();
			long startTime = System.currentTimeMillis();
			socket.connect(socketAddress);
			boolean connected = socket.isConnected();
			socket.close();
			return !connected ? -1 : System.currentTimeMillis() - startTime;
		} catch (Exception e) {
			return -1;
		}
	}
}