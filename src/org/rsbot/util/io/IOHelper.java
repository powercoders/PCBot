package org.rsbot.util.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Paris
 */
public class IOHelper {
	public static byte[] read(final File in) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		InputStream is = null;
		try {
			is = new FileInputStream(in);
			final byte[] temp = new byte[(int) in.length()];
			int read;
			while ((read = is.read(temp)) > 0) {
				buffer.write(temp, 0, read);
			}
		} catch (final IOException ignored) {
			try {
				buffer.close();
			} catch (final IOException ignored1) {
			}
			buffer = null;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (final IOException ignored) {
			}
		}
		return buffer == null ? null : buffer.toByteArray();
	}

	public static void write(final InputStream in, final File out) {
		OutputStream outStream = null;
		try {
			outStream = new FileOutputStream(out);
			final byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				outStream.write(buf, 0, len);
			}
		} catch (final IOException ignored) {
		} finally {
			try {
				if (outStream != null) {
					outStream.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (final IOException ignored) {
			}
		}
	}

	public static void recursiveDelete(final File path, final boolean deleteParent) {
		if (!path.exists()) {
			return;
		}
		for (final File file : path.listFiles()) {
			if (file.isDirectory()) {
				recursiveDelete(file, true);
			} else {
				file.delete();
			}
		}
		if (deleteParent) {
			path.delete();
		}
	}
}
