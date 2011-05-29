package org.rsbot.script.methods;

import java.util.ArrayList;
import java.util.List;

import org.rsbot.script.wrappers.RSComponent;

/**
 * Clan chat related operations.
 * 
 * @author Debauchery
 */
public class ClanChat extends MethodProvider {
	public static class ClanUser extends FriendChat.User {
		public ClanUser(final String name, final RSComponent rank,
				final RSComponent world) {
			super(name, rank, world);
		}
	}

	public static final int INTERFACE_CLAN_CHAT = 1110;
	public static final int INTERFACE_CLAN_CHAT_CHECK = 55;
	public static final int INTERFACE_CLAN_CHAT_USERS_LIST = 9;
	public static final int INTERFACE_CLAN_CHAT_INFO_BUTTON = 0;
	public static final int INTERFACE_CLAN_CHAT_SETTINGS_BUTTON = 0;
	public static final int INTERFACE_CLAN_CHAT_INFO = 1107;
	public static final int INTERFACE_CLAN_CHAT_INFO_CHANNEL_NAME = 172;
	public static final int INTERFACE_CLAN_CHAT_INFO_CHANNEL_OWNER = 35;
	public static final int INTERFACE_CLAN_CHAT_INFO_CLOSE_BUTTON = 174;
	public static final int INTERFACE_CLAN_CHAT_SETTINGS = 1096;
	public static final int INTERFACE_CLAN_CHAT_SETTINGS_LEAVE = 281;

	public static final int INTERFACE_CLAN_CHAT_SETTINGS_CLOSE_BUTTON = 341;

	ClanChat(final MethodContext ctx) {
		super(ctx);
	}

	/**
	 * Closes clan information interface.
	 * 
	 * @return <tt>true</tt> if closed/has been closed; otherwise <tt>false</tt>
	 */
	public boolean closeInformation() {
		if (informationOpen()) {
			methods.interfaces.getComponent(INTERFACE_CLAN_CHAT_INFO, INTERFACE_CLAN_CHAT_INFO_CLOSE_BUTTON).doClick();
			sleep(random(800, 12000));
		}
		return !informationOpen();
	}

	/**
	 * Closes clan Settings interface.
	 * 
	 * @return <tt>true</tt> if closed/has been closed; otherwise <tt>false</tt>
	 */
	public boolean closeSettings() {
		if (settingsOpen()) {
			methods.interfaces.getComponent(INTERFACE_CLAN_CHAT_SETTINGS, INTERFACE_CLAN_CHAT_SETTINGS_CLOSE_BUTTON).doClick();
		}
		return !settingsOpen();
	}

	/**
	 * Gets the name of the channel.
	 * 
	 * @return The name of the channel or null if none
	 */
	public String getChannelName() {
		String temp = null;
		if (informationOpen() || methods.game.openTab(Game.Tab.CLAN_CHAT)
				&& openInformation()) {
			temp = methods.interfaces.getComponent(INTERFACE_CLAN_CHAT_INFO, INTERFACE_CLAN_CHAT_INFO_CHANNEL_NAME).getText();
			closeInformation();
		}
		return temp != null ? temp.trim() : null;
	}

	/**
	 * Gets the owner of the channel.
	 * 
	 * @return The owner of the channel or null if unavailable
	 */
	public String getOwner() {
		String temp = null;
		if (informationOpen() || methods.game.openTab(Game.Tab.CLAN_CHAT)
				&& openInformation()) {
			temp = methods.interfaces.getComponent(INTERFACE_CLAN_CHAT_INFO, INTERFACE_CLAN_CHAT_INFO_CHANNEL_OWNER).getText();
			closeInformation();
		}
		return temp != null ? temp.trim() : null;
	}

	/**
	 * Gets the first clanmate matching with any of the provided names.
	 * 
	 * @param names
	 *            the names to look for
	 * @return an instance of <code>ClanUser</code> or <code>null</code> if no
	 *         results
	 */
	public ClanUser getUser(final String... names) {
		if (inChannel()) {
			final ClanUser[] clanMates = getUsers();
			for (final String name : names) {
				for (final ClanUser clanMate : clanMates) {
					if (name.equalsIgnoreCase(clanMate.getName())) {
						return clanMate;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Gets the users in the channel.
	 * 
	 * @return The users in the channel or null if unavailable
	 */
	public ClanUser[] getUsers() {
		if (inChannel()) {
			final RSComponent c = methods.interfaces.getComponent(INTERFACE_CLAN_CHAT, INTERFACE_CLAN_CHAT_USERS_LIST);
			if (c != null) {
				final List<ClanUser> mates = new ArrayList<ClanUser>();
				for (final RSComponent user : c.getComponents()) {
					if (user == null || user.getComponentName().isEmpty()) {
						continue;
					}
					final String name = user.getComponentName();
					final int userIndex = user.getComponentIndex();
					RSComponent rank = methods.interfaces.getComponent(INTERFACE_CLAN_CHAT, 10);
					rank = rank.getComponent(userIndex);
					RSComponent world = methods.interfaces.getComponent(INTERFACE_CLAN_CHAT, 11);
					world = world.getComponent(userIndex);
					mates.add(new ClanUser(name, rank, world));
				}
				return mates.toArray(new ClanUser[mates.size()]);
			}
		}
		return new ClanUser[0];
	}

	/**
	 * Returns whether or not we're in a channel.
	 * 
	 * @return <tt>true</tt> if in a channel; otherwise <tt>false</tt>
	 */
	public boolean inChannel() {
		methods.game.openTab(Game.Tab.CLAN_CHAT);
		return methods.game.getTab() == Game.Tab.CLAN_CHAT
				&& methods.interfaces.getComponent(INTERFACE_CLAN_CHAT, INTERFACE_CLAN_CHAT_CHECK).containsText("If you");
	}

	/**
	 * Checks to see if the information interface is open/valid.
	 * 
	 * @return <tt>true</tt> if open; otherwise <tt>false</tt>
	 */
	public boolean informationOpen() {
		return methods.interfaces.get(INTERFACE_CLAN_CHAT_INFO).isValid();
	}

	/**
	 * Opens clan information interface.
	 * 
	 * @return <tt>true</tt> if open/has been opened; otherwise <tt>false</tt>
	 */
	public boolean openInformation() {
		if (!informationOpen() && methods.game.openTab(Game.Tab.CLAN_CHAT)) {
			if (!inChannel()) {
				return false;
			}
			methods.interfaces.getComponent(INTERFACE_CLAN_CHAT_INFO, INTERFACE_CLAN_CHAT_INFO_BUTTON).doClick();
		}
		return informationOpen();
	}

	/**
	 * Opens clan Settings interface.
	 * 
	 * @return <tt>true</tt> if open/has been opened; otherwise <tt>false</tt>
	 */
	public boolean openSettings() {
		if (!settingsOpen() && methods.game.openTab(Game.Tab.CLAN_CHAT)) {
			if (!inChannel()) {
				return false;
			}
			methods.interfaces.getComponent(INTERFACE_CLAN_CHAT_SETTINGS, INTERFACE_CLAN_CHAT_SETTINGS_BUTTON).doClick();
		}
		return settingsOpen();
	}

	/**
	 * Checks to see if the Settings interface is open/valid.
	 * 
	 * @return <tt>true</tt> if open; otherwise <tt>false</tt>
	 */
	public boolean settingsOpen() {
		return methods.interfaces.get(INTERFACE_CLAN_CHAT_SETTINGS).isValid();
	}
}
