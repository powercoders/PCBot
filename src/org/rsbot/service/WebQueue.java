package org.rsbot.service;

import org.rsbot.Configuration;
import org.rsbot.script.wrappers.RSGameTile;
import org.rsbot.script.methods.Web;
import org.rsbot.script.wrappers.RSTile;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * The web queue class, passes data to Cache writer.
 *
 * @author Timer
 */
public class WebQueue {
	public static boolean weAreBuffering = false, speedBuffer = false;
	public static int bufferingCount = 0;
	private static final List<String> queue = new ArrayList<String>(), removeQueue = new ArrayList<String>(), removeStack = new ArrayList<String>();
	private static QueueWriter writer;
	private static final Logger log = Logger.getLogger(WebQueue.class.getName());
	private static final Object queueLock = new Object();
	private static final Object bufferLock = new Object();
	private static final Object removeLock = new Object();

	static {
		writer = new QueueWriter(Configuration.Paths.getWebDatabase());
		writer.start();
	}

	/**
	 * Adds collected data to the queue.
	 *
	 * @param gameTiles The data.
	 */
	public static void Add(final List<RSGameTile> gameTiles) {
		Web.map.addAll(gameTiles);
		final int count = gameTiles.size();
		new Thread() {
			@Override
			public void run() {
				try {
					final List<RSGameTile> gTList = new ArrayList<RSGameTile>();
					gTList.addAll(gameTiles);
					final List<RSGameTile> tl = Collections.unmodifiableList(gTList);
					bufferingCount = bufferingCount + count;
					final Iterator<RSGameTile> tileFlagsIterator = tl.listIterator();
					while (tileFlagsIterator.hasNext()) {
						final RSGameTile tileFlags = tileFlagsIterator.next();
						if (tileFlags != null) {
							synchronized (queueLock) {
								queue.add(tileFlags.toString());
							}
							synchronized (bufferLock) {
								bufferingCount--;
								try {
									weAreBuffering = true;
									if (!speedBuffer) {
										Thread.sleep(1);
									}
								} catch (final InterruptedException ignored) {
								}
							}
						}
					}
					if (bufferingCount < 0) {
						bufferingCount = 0;
					}
					gTList.clear();
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
		synchronized (removeLock) {
			Web.map.remove(tile);
			Remove(tile.getX() + "," + tile.getY() + tile.getZ());
		}
	}

	/**
	 * Destroys the cache writer.
	 */
	public static void Destroy() {
		speedBuffer = true;
		writer.destroyWriter();
	}

	/**
	 * Gets the queue size.
	 *
	 * @param id The id to grab.
	 * @return The size of the queue.
	 */
	public static int queueSize(final int id) {
		switch (id) {
			case 0:
				return queue.size();
			case 1:
				return removeQueue.size();
			case 2:
				return removeStack.size();
		}
		return -1;
	}

	/**
	 * The threaded writer class.
	 *
	 * @author Timer
	 */
	private static class QueueWriter extends Thread {
		private boolean destroy = false;
		private final File file, tmpFile;

		public QueueWriter(final String fileName) {
			file = new File(fileName);
			tmpFile = new File(fileName + ".tmp");
			if (!file.exists()) {
				log.fine("File not created, creating: " + fileName);
				try {
					if (file.createNewFile()) {
						file.setExecutable(false);
						file.setReadable(true);
						file.setWritable(true);
					}
				} catch (final Exception e) {
					destroy = true;
				}
			}
		}

		/**
		 * The main method...  doesn't stop until all data is written.
		 */
		@Override
		public void run() {
			final List<String> outList = new ArrayList<String>();
			while ((!destroy || queue.size() > 0 || WebQueue.weAreBuffering) && file.exists() && file.canWrite()) {
				try {
					if (removeQueue.size() > 0) {
						synchronized (removeLock) {
							removeStack.clear();
							removeStack.addAll(removeQueue);
							removeQueue.clear();
						}
						final BufferedReader br = new BufferedReader(new FileReader(file));
						final PrintWriter pw = new PrintWriter(new FileWriter(tmpFile));
						String line;
						while ((line = br.readLine()) != null) {
							boolean good = true;
							final Iterator<String> removeLines = removeStack.listIterator();
							while (removeLines.hasNext()) {
								final String str = removeLines.next();
								if (str != null && line.contains(str)) {
									good = false;
									break;
								}
							}
							if (good) {
								pw.println(line);
								pw.flush();
							}
						}
						pw.close();
						br.close();
						if (file.delete()) {
							if (!tmpFile.renameTo(file)) {
								destroyWriter();
								continue;
							}
						}
						removeStack.clear();
					}
					synchronized (queueLock) {
						if (queue.size() > 0) {
							final FileWriter fileWriter = new FileWriter(file, true);
							final BufferedWriter out = new BufferedWriter(fileWriter);
							outList.clear();
							outList.addAll(queue);
							queue.clear();
							final Iterator<String> outLines = outList.listIterator();
							while (outLines.hasNext()) {
								final String line = outLines.next();
								out.write(line + "\n");
							}
							out.flush();
							out.close();
						}
					}
					try {
						if (!destroy) {
							Thread.sleep(5000);
						}
					} catch (final InterruptedException ignored) {
					}
				} catch (final IOException ignored) {
				}
			}
		}

		public void remove(final String str) {
			synchronized (removeLock) {
				removeQueue.add(str);
			}
		}

		public void destroyWriter() {
			destroy = true;
		}
	}

	/**
	 * Adds a string to remove to the queue.
	 *
	 * @param str The string to remove.
	 */
	public static void Remove(final String str) {
		synchronized (removeLock) {
			writer.remove(str);
		}
	}
}