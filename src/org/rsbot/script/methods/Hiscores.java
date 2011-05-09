package org.rsbot.script.methods;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * This class is used to fetch the stats of another player.
 * <p/>
 * Example: Hiscores.Stats stats = hiscores.lookup("username"); int attack =
 * stats.getCurrentLevel(Skills.ATTACK);
 *
 * @author Aion
 * @version 0.2
 */
public class Hiscores extends MethodProvider {

	private static final String HOST = "http://hiscore.runescape.com";
	private static final String GET = "/index_lite.ws?player=";

	public Hiscores() {
		super(null);
	}

	/**
	 * Collects data for a given player from the hiscore website.
	 *
	 * @param username The username
	 * @return An instance of Hiscores.Stats; <code>null</code> if unable to
	 *         fetch data.
	 */
	public Stats lookup(final String username) {
		if (username != null && !username.isEmpty()) {
			try {
				final URL url = new URL(Hiscores.HOST + Hiscores.GET + username);
				final BufferedReader br = new BufferedReader(new InputStreamReader(
						url.openStream()));
				String[] html;
				final int[] exps = new int[26];
				final int[] lvls = new int[26];
				final int[] ranks = new int[26];
				for (int i = 0; i < 26; i++) {
					html = br.readLine().split(",");
					exps[i] = Integer.parseInt(html[2]);
					lvls[i] = Integer.parseInt(html[1]);
					ranks[i] = Integer.parseInt(html[0]);
				}
				br.close();
				return new Stats(username, exps, lvls, ranks);
			} catch (final IOException ignored) {
			}
		}
		return null;
	}

	/**
	 * Provides access to High Scores Information.
	 *
	 * @author Jacmob, Aut0r
	 */
	public static class Stats {

		private final String username;

		private final int[] exps;
		private final int[] lvls;
		private final int[] ranks;

		Stats(final String username, final int[] exps, final int[] lvls, final int[] ranks) {
			this.username = username;
			this.exps = exps;
			this.lvls = lvls;
			this.ranks = ranks;
		}

		/**
		 * Gets the experience of a given skill
		 *
		 * @param index The index of the skill
		 * @return The experience or -1
		 */
		public int getExperience(final int index) {
			if (index < 0 || index >= exps.length - 1) {
				throw new IllegalArgumentException("Illegal skill index: "
						+ index);
			}
			return exps[index + 1];
		}

		/**
		 * Gets the level of a given skill
		 *
		 * @param index The index of the skill
		 * @return The level or -1
		 */
		public int getLevel(final int index) {
			if (index < 0 || index >= exps.length - 1) {
				throw new IllegalArgumentException("Illegal skill index: "
						+ index);
			}
			return lvls[index + 1];
		}

		/**
		 * Gets the rank of a given skill
		 *
		 * @param index The index of the skill
		 * @return The rank or -1
		 */
		public int getRank(final int index) {
			if (index < 0 || index >= exps.length - 1) {
				throw new IllegalArgumentException("Illegal skill index: "
						+ index);
			}
			return ranks[index + 1];
		}

		/**
		 * Gets the overall experience
		 *
		 * @return The overall experience or -1
		 */
		public int getOverallExp() {
			return exps[0];
		}

		/**
		 * Gets the overall level (also known as total level)
		 *
		 * @return The overall level or -1
		 */
		public int getOverallLevel() {
			return lvls[0];
		}

		/**
		 * Gets the overall rank
		 *
		 * @return The overall rank or -1
		 */
		public int getOverallRank() {
			return ranks[0];
		}

		/**
		 * Gets the username of this instance
		 *
		 * @return The username
		 */
		public String getUsername() {
			return username;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append(username).append("[overall:");
			sb.append(lvls[0]).append(",").append(ranks[0]);
			sb.append(",").append(exps[0]).append(" ");
			for (int i = 0; i < lvls.length - 1; i++) {
				sb.append(Skills.SKILL_NAMES[i]).append(":");
				sb.append(lvls[i + 1]).append(",").append(ranks[i + 1]);
				sb.append(",").append(exps[i + 1]).append(" ");
			}
			return sb.toString().replaceFirst("\\s+$", "]");
		}

	}

}