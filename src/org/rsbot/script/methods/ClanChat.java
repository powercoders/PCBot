package org.rsbot.script.methods;

import org.rsbot.script.wrappers.RSComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Clan chat related operations.
 *
 * @author Debauchery
 */
public class ClanChat extends MethodProvider {
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

	public static class ClanUser extends FriendChat.User {
		public ClanUser(String name, RSComponent rank, RSComponent world) {
			super(name, rank, world);
		}
	}

	/**
	 * Gets the owner of the channel.
	 *
	 * @return The owner of the channel or null if unavailable
	 */
	public String getOwner() {
		String temp = null;
		if (informationOpen() || (methods.game.openTab(Game.Tab.CLAN_CHAT) && openInformation())) {
			temp = methods.interfaces.getComponent(INTERFACE_CLAN_CHAT_INFO, INTERFACE_CLAN_CHAT_INFO_CHANNEL_OWNER).getText();
			closeInformation();
		}
		return temp != null ? temp.trim() : null;
	}

	/**
	 * Gets the name of the channel.
	 *
	 * @return The name of the channel or null if none
	 */
	public String getChannelName() {
		String temp = null;
		if (informationOpen() || (methods.game.openTab(Game.Tab.CLAN_CHAT) && openInformation())) {
			temp = methods.interfaces.getComponent(INTERFACE_CLAN_CHAT_INFO, INTERFACE_CLAN_CHAT_INFO_CHANNEL_NAME).getText();
			closeInformation();
		}
		return temp != null ? temp.trim() : null;
	}

	/**
	 * Gets the users in the channel.
	 *
	 * @return The users in the channel or null if unavailable
	 */
	public ClanUser[] getUsers() {
		if (inChannel()) {
			RSComponent c = methods.interfaces.getComponent(INTERFACE_CLAN_CHAT, INTERFACE_CLAN_CHAT_USERS_LIST);
			if (c != null) {
				List<ClanUser> mates = new ArrayList<ClanUser>();
				for (RSComponent user : c.getComponents()) {
					if (user == null || user.getComponentName().isEmpty()) {
						continue;
					}
					String name = user.getComponentName();
					int userIndex = user.getComponentIndex();
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
	 * Gets the first clanmate matching with any of the provided names.
	 *
	 * @param names the names to look for
	 * @return an instance of <code>ClanUser</code> or <code>null</code> if no results
	 */
	public ClanUser getUser(String... names) {
		if (inChannel()) {
			ClanUser[] clanMates = getUsers();
			for (String name : names) {
				for (ClanUser clanMate : clanMates) {
					if (name.equalsIgnoreCase(clanMate.getName())) {
						return clanMate;
					}
				}
			}
		}
		return null;
	}


	/**
	 * Returns whether or not we're in a channel.
	 *
	 * @return <tt>true</tt> if in a channel; otherwise <tt>false</tt>
	 */
	public boolean inChannel() {
		methods.game.openTab(Game.Tab.CLAN_CHAT);
		return methods.game.getTab() == Game.Tab.CLAN_CHAT && methods.interfaces.getComponent(INTERFACE_CLAN_CHAT, INTERFACE_CLAN_CHAT_CHECK).containsText("If you");
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
	 * Checks to see if the information interface is open/valid.
	 *
	 * @return <tt>true</tt> if open; otherwise <tt>false</tt>
	 */
	public boolean informationOpen() {
		return methods.interfaces.get(INTERFACE_CLAN_CHAT_INFO).isValid();
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
	 * Checks to see if the Settings interface is open/valid.
	 *
	 * @return <tt>true</tt> if open; otherwise <tt>false</tt>
	 */
	public boolean settingsOpen() {
		return methods.interfaces.get(INTERFACE_CLAN_CHAT_SETTINGS).isValid();
	}
}