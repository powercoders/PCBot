package org.rsbot.service;

import org.rsbot.script.internal.wrappers.TileFlags;
import org.rsbot.script.methods.Web;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.CacheWriter;
import org.rsbot.util.GlobalConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The web queue class, passes data to Cache writer.
 *
 * @author Timer
 */
public class WebQueue {
	private static CacheWriter cacheWriter = null;
	public static boolean weAreBuffering = false;
	public static boolean speedBuffer = false;
	public static int bufferingCount = 0;

	public static void Create() {
		if (cacheWriter == null) {
			cacheWriter = new CacheWriter(GlobalConfiguration.Paths.getWebCache());
		}
	}

	/**
	 * Gets the cache writer.
	 *
	 * @return The cache writer instance.
	 */
	public static CacheWriter getCacheWriter() {
		return cacheWriter;
	}

	/**
	 * Adds collected data to the queue.
	 *
	 * @param theFlagsList The data.
	 */
	public static void Add(final HashMap<RSTile, TileFlags> theFlagsList) {
		Web.map.putAll(theFlagsList);
		final int count = theFlagsList.size();
		new Thread() {
			@Override
			public void run() {
				try {
					String addedString = "";
					final HashMap<RSTile, TileFlags> theFlagsList2 = new HashMap<RSTile, TileFlags>();
					theFlagsList2.putAll(theFlagsList);
					final Map<RSTile, TileFlags> tl = Collections.unmodifiableMap(theFlagsList2);
					bufferingCount = bufferingCount + count;
					final Iterator<Map.Entry<RSTile, TileFlags>> tileFlagsIterator = tl.entrySet().iterator();
					while (tileFlagsIterator.hasNext()) {
						final TileFlags tileFlags = tileFlagsIterator.next().getValue();
						if (tileFlags != null) {
							addedString += tileFlags.toString() + "\n";
							bufferingCount--;
							try {
								weAreBuffering = true;
								if (!speedBuffer) {
									Thread.sleep(10);
								}
							} catch (final InterruptedException ignored) {
							}
						}
					}
					if (bufferingCount < 0) {
						bufferingCount = 0;
					}
					cacheWriter.add(addedString);
					addedString = null;
					theFlagsList2.clear();
					try {
						Thread.sleep(500);//Prevent data loss.
					} catch (final InterruptedException ignored) {
					}
					weAreBuffering = false;
				} catch (final Exception e) {
					bufferingCount = count;
					if (bufferingCount < 0) {
						bufferingCount = 0;
					}
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * Removes a tile from the database.
	 *
	 * @param tile The tile to remove.
	 */
	public static void Remove(final RSTile tile) {
		new Thread() {
			@Override
			public void run() {
				Web.map.remove(tile);
				cacheWriter.remove(tile.getX() + "," + tile.getY() + tile.getZ());
			}
		}.start();
	}

	/**
	 * Removes a string from the data base.
	 *
	 * @param str The string to remove.
	 */
	public static void Remove(final String str) {
		new Thread() {
			@Override
			public void run() {
				cacheWriter.remove(str);
			}
		}.start();
	}

	/**
	 * Checks if the queue is running.
	 *
	 * @return <tt>true</tt> if it's running.
	 */
	public static boolean IsRunning() {
		return CacheWriter.IsRunning();
	}

	/**
	 * Destroys the cache writer.
	 */
	public static void Destroy() {
		speedBuffer = true;
		cacheWriter.destroy();
	}
}
