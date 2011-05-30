package org.rsbot.script.methods;

import java.util.regex.Pattern;

import org.rsbot.script.wrappers.RSComponent;

/**
 * Friend chat related operations.
 * 
 * @author Debauchery
 */
public class FriendChat extends MethodProvider {

	public enum ChatRank {
		GUEST(-1), FRIEND(1004), RECRUIT(6226), CORPORAL(6225), SERGEANT(6224), LIEUTENANT(
				6232), CAPTAIN(6233), GENERAL(6231), ADMIN(6228), DEPUTY_OWNER(
				6629), OWNER(6227);

		private final int TEXTURE_ID;

		private ChatRank(final int textureId) {
			TEXTURE_ID = textureId;
		}

		/**
		 * Gets the texture id of this rank.
		 * 
		 * @return the texture id of this rank
		 */
		public int getTextureId() {
			return TEXTURE_ID;
		}
	}

	public interface Friend {
		/**
		 * Gets the name of this user.
		 * 
		 * @return the name of this user
		 */
		public String getName();

		/**
		 * Gets the world number that this user is on.
		 * 
		 * @return the world number or -1 if unavailable
		 */
		public int getWorld();

		/**
		 * Checks whether this user is in lobby.
		 * 
		 * @return <tt>true</tt> if in lobby; otherwise <tt>false</tt>
		 */
		public boolean isInLobby();
	}

	/**
	 * Friends list related operations. Does not yet handle the lobby.
	 * 
	 * @author Timer
	 */
	public class FriendsList {
		public class Friend implements FriendChat.Friend {

			private final String name;
			private int worldNumber;
			private final boolean isOffline;
			private final boolean isInLobby;

			public Friend(final String name, final RSComponent world) {
				this.name = name;
				final String text = world.getText();
				isOffline = text.contains("Of");
				isInLobby = text.contains("Lo");
				if (!isOffline && !isInLobby && !text.endsWith(".")) {
					worldNumber = Integer.parseInt(text);
				} else {
					worldNumber = -1;
				}
			}

			/**
			 * The name of your friend.
			 * 
			 * @return Name of the friend.
			 */
			@Override
			public String getName() {
				return name;
			}

			@Override
			public int getWorld() {
				return worldNumber;
			}

			@Override
			public boolean isInLobby() {
				return isInLobby;
			}

			/**
			 * Checks whether this friend is offline.
			 * 
			 * @return <tt>true</tt> if offline; otherwise <tt>false</tt>
			 */
			public boolean isOffline() {
				return isOffline;
			}

			/**
			 * Checks whether this friend is online.
			 * 
			 * @return <tt>true</tt> if online; otherwise <tt>false</tt>
			 */
			public boolean isOnline() {
				return !isOffline && !isInLobby;
			}
		}

		public static final int FRIENDSLIST = 550;
		public static final int FRIENDSLIST_BUTTON_ADD_FRIEND = 28;
		public static final int FRIENDSLIST_BUTTON_REMOVE_FRIEND = 37;
		public static final int FRIENDSLIST_LABEL_FRIENDS_COUNT = 17;

		public static final int FRIENDSLIST_LIST_FRIENDS = 6;

		/**
		 * Adds a friend.
		 * 
		 * @param user
		 *            the instance of <code>Friend</code> to add
		 * @return <tt>true</tt> if successful; otherwise <tt>false</tt>
		 */
		public boolean add(final FriendChat.Friend user) {
			if (user != null) {
				final Friend friend = getFriend(user.getName());
				if (friend == null) {
					return add(user.getName());
				}
			}
			return false;
		}

		/**
		 * Adds a friend.
		 * 
		 * @param name
		 *            the name of the friend to add
		 * @return <tt>true</tt> if successful; otherwise <tt>false</tt>
		 */
		public boolean add(final String name) {
			if (name != null && !name.isEmpty()) {
				openTab();
				final RSComponent c = methods.interfaces.getComponent(FriendsList.FRIENDSLIST, FriendsList.FRIENDSLIST_BUTTON_ADD_FRIEND);
				if (c != null) {
					c.doClick();
					sleep(random(300, 550));
					methods.keyboard.sendText(name, true);
					sleep(random(600, 800));
					return getFriend(name) != null;
				}
			}
			return false;
		}

		/**
		 * Gets the count of this end-user's friends.
		 * 
		 * @return the count of this end-user's friends
		 */
		public int getCount() {
			openTab();
			final RSComponent c = methods.interfaces.getComponent(FriendsList.FRIENDSLIST, FriendsList.FRIENDSLIST_LABEL_FRIENDS_COUNT);
			if (c != null) {
				final String text = c.getText();
				return Integer.parseInt(text.split(" ")[0]);
			}
			return -1;
		}

		/**
		 * Gets the first friend matching with any of the provided names.
		 * 
		 * @param names
		 *            the names to look for
		 * @return an instance of <code>Friend</code> or <code>null</code> if no
		 *         results
		 */
		public Friend getFriend(final String... names) {
			final Friend[] friends = getFriends();
			for (final String name : names) {
				for (final Friend friend : friends) {
					if (name.equalsIgnoreCase(friend.getName())) {
						return friend;
					}
				}
			}
			return null;
		}

		/**
		 * Gets the end-user's friends from the friends list.
		 * 
		 * @return an array instance of <code>Friend</code>
		 */
		public Friend[] getFriends() {
			openTab();
			final RSComponent list = methods.interfaces.getComponent(FriendsList.FRIENDSLIST, FriendsList.FRIENDSLIST_LIST_FRIENDS);
			if (list != null) {
				final java.util.ArrayList<Friend> friends = new java.util.ArrayList<Friend>();
				for (final RSComponent c : list.getComponents()) {
					if (c == null) {
						continue;
					}
					String name = c.getComponentName();
					name = name.substring(name.indexOf(62) + 1);
					RSComponent world = methods.interfaces.getComponent(FriendsList.FRIENDSLIST, 5);
					world = world.getComponent(c.getIndex());
					friends.add(new Friend(name, world));
				}
				return friends.toArray(new Friend[friends.size()]);
			}
			return new Friend[0];
		}

		/**
		 * Gets all the friends matching with any of the provided names.
		 * 
		 * @param names
		 *            the names to look for
		 * @return an array instance of <code>Friend</code>
		 */
		public Friend[] getFriends(final String... names) {
			final java.util.ArrayList<Friend> friends = new java.util.ArrayList<Friend>();
			for (final String name : names) {
				for (final Friend friend : getFriends()) {
					if (name.equalsIgnoreCase(friend.getName())) {
						friends.add(friend);
						continue;
					}
				}
			}
			return friends.toArray(new Friend[friends.size()]);
		}

		/**
		 * Checks whether the friends list of this end-user is full.
		 * 
		 * @return <tt>true</tt> if full; otherwise <tt>false</tt>
		 */
		public boolean isFull() {
			return getCount() == 200;
		}

		/**
		 * Opens the friends list tab if not already opened.
		 */
		public void openTab() {
			methods.game.openTab(Game.Tab.FRIENDS);
		}

		/**
		 * Removes a friend.
		 * 
		 * @param user
		 *            the instance of <code>Friend</code> to remove
		 * @return <tt>true</tt> if successful; otherwise <tt>false</tt>
		 */
		public boolean remove(final FriendChat.Friend user) {
			if (user != null) {
				final Friend friend = getFriend(user.getName());
				if (friend != null) {
					return remove(friend.getName());
				}
			}
			return false;
		}

		/**
		 * Removes a friend.
		 * 
		 * @param name
		 *            the name of the friend to remove
		 * @return <tt>true</tt> if successful; otherwise <tt>false</tt>
		 */
		public boolean remove(final String name) {
			if (name != null && getFriend(name) != null) {
				final RSComponent c = methods.interfaces.getComponent(FriendsList.FRIENDSLIST, FriendsList.FRIENDSLIST_BUTTON_REMOVE_FRIEND);
				if (c != null) {
					c.doClick();
					sleep(random(300, 550));
					methods.keyboard.sendText(name, true);
					sleep(random(600, 800));
					return getFriend(name) != null;
				}
			}
			return false;
		}
	}

	public static class User implements Friend {

		private final String name;
		private int worldNumber;
		private final boolean isInLobby;
		private ChatRank rank = ChatRank.GUEST;

		public User(final String name, final RSComponent rank,
				final RSComponent world) {
			this.name = name;
			final int textureId = rank.getBackgroundColor();
			for (final ChatRank chatRank : ChatRank.values()) {
				if (chatRank.getTextureId() == textureId) {
					this.rank = chatRank;
					break;
				}
			}
			final String text = world.getText();
			isInLobby = text.contains("Lo");
			if (!text.endsWith(".")) {
				worldNumber = Integer.parseInt(text.substring(text.indexOf(32) + 1));
			} else {
				worldNumber = -1;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getName() {
			return name;
		}

		/**
		 * Gets this user's chat rank.
		 * 
		 * @return this user's chat rank
		 */
		public ChatRank getRank() {
			return rank;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int getWorld() {
			return worldNumber;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isInLobby() {
			return isInLobby;
		}

	}

	private static final Pattern HTML_TAG = Pattern.compile("(^[^<]+>|<[^>]+>|<[^>]+$)");
	public static final int INTERFACE_FRIEND_CHAT = 1109;
	public static final int INTERFACE_FRIEND_CHAT_JOIN_BUTTON = 27;
	public static final int INTERFACE_FRIEND_CHAT_CHANNEL_INFO = 1;
	public static final int INTERFACE_FRIEND_CHAT_USERS_LIST = 5;
	public static final int INTERFACE_JOIN_FRIEND_CHAT = 752;
	public static final int INTERFACE_JOIN_FRIEND_CHAT_LAST_CHANNEL = 3;

	public static final String FRIEND_CHAT_STRING_TALK = "/";

	private String lastCachedChannel = null;

	public FriendsList friendsList = new FriendsList();

	FriendChat(final MethodContext ctx) {
		super(ctx);
	}

	public String getLastCachedChannel() {
		return lastCachedChannel;
	}

	/**
	 * Gets the name of the channel.
	 * 
	 * @return The name of the channel or null if none
	 */
	public String getName() {
		try {
			methods.game.openTab(Game.Tab.FRIENDS_CHAT);
			final String name = stripFormatting(methods.interfaces.getComponent(INTERFACE_FRIEND_CHAT, INTERFACE_FRIEND_CHAT_CHANNEL_INFO).getText());
			return name.substring(name.indexOf("Talking in: " + 12));
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * Gets the owner of the channel.
	 * 
	 * @return The owner of the channel or null if none
	 */
	public String getOwner() {
		try {
			methods.game.openTab(Game.Tab.FRIENDS_CHAT);
			if (getName() != null) {
				lastCachedChannel = getName();
			}
			final String name = stripFormatting(methods.interfaces.getComponent(INTERFACE_FRIEND_CHAT, INTERFACE_FRIEND_CHAT_CHANNEL_INFO).getText());
			return name.substring(name.indexOf("Owner: ") + 7);
		} catch (final Exception e) {
			return null;
		}
	}

	/**
	 * Gets the first user matching with any of the provided names.
	 * 
	 * @param names
	 *            The names to look for.
	 * @return an instance of <code>User</code>.
	 */
	public User getUser(final String... names) {
		if (isInChannel()) {
			final User[] users = getUsers();
			for (final String name : names) {
				for (final User user : users) {
					if (name.equalsIgnoreCase(user.getName())) {
						return user;
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
	public User[] getUsers() {
		if (isInChannel()) {
			final java.util.ArrayList<User> users = new java.util.ArrayList<User>();
			final RSComponent list = methods.interfaces.getComponent(FriendChat.INTERFACE_FRIEND_CHAT, FriendChat.INTERFACE_FRIEND_CHAT_USERS_LIST);
			if (list != null) {
				for (final RSComponent c : list.getComponents()) {
					if (c == null) {
						continue;
					}
					String name = c.getText();
					if (name != null && !name.isEmpty() && name.contains("..")) {
						final String[] actions = c.getActions();
						if (actions != null) {
							for (final String action : actions) {
								if (action != null) {
									if (action.contains("Add")
											|| action.contains("Remove")) {
										name = action.substring(action.indexOf(32, action.indexOf(32) + 1) + 1);
										break;
									}
								}
							}
						}
					}
					final int componentIndex = c.getComponentIndex();
					RSComponent rank = methods.interfaces.getComponent(FriendChat.INTERFACE_FRIEND_CHAT, 6);
					rank = rank.getComponent(componentIndex);
					RSComponent world = methods.interfaces.getComponent(FriendChat.INTERFACE_FRIEND_CHAT, 7);
					world = world.getComponent(componentIndex);
					users.add(new User(name, rank, world));
				}
				return users.toArray(new User[users.size()]);
			}
		}
		return new User[0];
	}

	/**
	 * Returns whether or not we're in a channel.
	 * 
	 * @return <tt>true</tt> if in a channel; otherwise <tt>false</tt>
	 */
	public boolean isInChannel() {
		methods.game.openTab(Game.Tab.FRIENDS_CHAT);
		if (getName() != null) {
			lastCachedChannel = getName();
		}
		return methods.interfaces.getComponent(INTERFACE_FRIEND_CHAT, INTERFACE_FRIEND_CHAT_JOIN_BUTTON).containsAction("Leave chat");
	}

	/**
	 * Joins the given channel. If we are already in a channel, it will leave
	 * it.
	 * 
	 * @param channel
	 *            The channel to join
	 * @return <tt>true</tt> if successful; otherwise <tt>false</tt>
	 */
	public boolean join(final String channel) {
		methods.game.openTab(Game.Tab.FRIENDS_CHAT);
		if (isInChannel()) {
			if (getName() == channel) {
				return true;
			}
			if (!leave()) {
				return false;
			}
		}
		methods.interfaces.getComponent(INTERFACE_FRIEND_CHAT, INTERFACE_FRIEND_CHAT_JOIN_BUTTON).doClick();
		sleep(random(500, 800));
		if (methods.interfaces.get(INTERFACE_JOIN_FRIEND_CHAT).isValid()) {
			final String lastChatCompText = methods.interfaces.getComponent(INTERFACE_JOIN_FRIEND_CHAT, INTERFACE_JOIN_FRIEND_CHAT_LAST_CHANNEL).getComponent(0).getText();
			lastCachedChannel = lastChatCompText.substring(lastChatCompText.indexOf(": ") + 2);
			methods.keyboard.sendText(channel, true);
			sleep(random(1550, 1800));
			if (isInChannel()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Joins the given channel. If we are already in a channel, it will leave
	 * it.
	 * 
	 * @return <tt>true</tt> if successful; otherwise <tt>false</tt>
	 */
	public boolean joinLastChannel() {
		methods.game.openTab(Game.Tab.FRIENDS_CHAT);
		if (isInChannel()) {
			return true;
		}
		methods.interfaces.getComponent(INTERFACE_FRIEND_CHAT, INTERFACE_FRIEND_CHAT_JOIN_BUTTON).doClick();
		sleep(random(500, 800));
		if (methods.interfaces.get(INTERFACE_JOIN_FRIEND_CHAT).isValid()) {
			final String lastChatCompText = methods.interfaces.getComponent(INTERFACE_JOIN_FRIEND_CHAT, INTERFACE_JOIN_FRIEND_CHAT_LAST_CHANNEL).getComponent(0).getText();
			lastCachedChannel = lastChatCompText.substring(lastChatCompText.indexOf(": ") + 2);
			methods.interfaces.getComponent(INTERFACE_JOIN_FRIEND_CHAT, INTERFACE_JOIN_FRIEND_CHAT_LAST_CHANNEL).doClick();
			sleep(random(1550, 1800));
			if (isInChannel()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Leaves the current channel.
	 * 
	 * @return <tt>true</tt> if successful; otherwise <tt>false</tt>
	 */
	public boolean leave() {
		methods.game.openTab(Game.Tab.FRIENDS_CHAT);
		if (isInChannel()) {
			lastCachedChannel = getOwner();
			methods.interfaces.getComponent(INTERFACE_FRIEND_CHAT, INTERFACE_FRIEND_CHAT_JOIN_BUTTON).doClick();
			sleep(random(650, 900));
			return isInChannel();
		}
		return true;
	}

	/**
	 * Sends the given message. It is not necessary to include the slash. This
	 * does not check if the user is on a channel.
	 * 
	 * @param msg
	 *            the message to send
	 */
	public void sendMessage(final String msg) {
		sendMessage(msg, false);
	}

	/**
	 * Sends the given message. It is not necessary to include the slash. This
	 * does not check if the user is on a channel.
	 * 
	 * @param msg
	 *            the message to send
	 * @param instant
	 *            if <tt>true</tt>, message will be sent instantly
	 */
	public void sendMessage(final String msg, final boolean instant) {
		if (msg != null && !msg.isEmpty()) {
			final String message = FRIEND_CHAT_STRING_TALK.concat(msg);
			if (instant) {
				methods.keyboard.sendTextInstant(message, true);
			} else {
				methods.keyboard.sendText(message, true);
			}
		}
	}

	/**
	 * Strips HTML tags.
	 * 
	 * @param input
	 *            The string you want to parse.
	 * @return The parsed {@code String}.
	 */
	private String stripFormatting(final String input) {
		return HTML_TAG.matcher(input).replaceAll("");
	}
}
