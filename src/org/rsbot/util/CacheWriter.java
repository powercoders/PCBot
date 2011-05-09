package org.rsbot.util;

import org.rsbot.service.WebQueue;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * A threaded file writer to cache files.  Supports deletion.
 *
 * @author Timer
 */
public class CacheWriter {
	private final List<String> queue = new ArrayList<String>(), removeQueue = new ArrayList<String>(), removeStack = new ArrayList<String>();
	private final QueueWriter writer;
	private static boolean stopped = false;
	private static final Logger log = Logger.getLogger(CacheWriter.class.getName());

	public CacheWriter(final String fileName) {
		writer = new QueueWriter(fileName);
		writer.start();
	}

	/**
	 * Adds to the writer queue.
	 *
	 * @param list The string.
	 */
	public void add(final String list) {
		queue.add(list);
	}

	/**
	 * Destroys the writer.
	 */
	public void destroy() {
		writer.destroyWriter();
	}

	/**
	 * Gets the queue size.
	 *
	 * @param id The id to grab.
	 * @return The size of the queue.
	 */
	public int queueSize(final int id) {
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
	private class QueueWriter extends Thread {
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
						removeStack.clear();
						removeStack.addAll(removeQueue);
						removeQueue.clear();
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
					try {
						if (!destroy) {
							Thread.sleep(5000);
						}
					} catch (final InterruptedException ignored) {
					}
				} catch (final IOException ignored) {
				}
			}
			stopped = true;
		}

		public void remove(final String str) {
			removeQueue.add(str);
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
	public void remove(final String str) {
		writer.remove(str);
	}

	/**
	 * Checks if it's running.
	 *
	 * @return <tt>true</tt> if it's running.
	 */
	public static boolean IsRunning() {
		return !stopped;
	}
}
