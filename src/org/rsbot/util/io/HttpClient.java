package org.rsbot.util.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.rsbot.util.GlobalConfiguration;

/**
 * @author Paris
 */
public class HttpClient {
	private static final Logger log = Logger.getLogger(HttpClient.class.getName());

	public static HttpURLConnection download(final URL url, final File file) throws IOException {
		final HttpURLConnection con = getConnection(url);

		if (file.exists()) {
			con.setIfModifiedSince(file.lastModified());
			con.connect();
			if (con.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
				log.fine("Using " + file.getName() + " from cache");
				con.disconnect();
				return con;
			}
		}

		log.fine("Downloading new " + file.getName());

		final byte[] buffer = downloadBinary(con);

		if (!file.exists()) {
			file.createNewFile();
		}
		if (file.exists() && (!file.canRead() || file.canWrite())) {
			file.setReadable(true);
			file.setWritable(true);
		}
		if (file.exists() && file.canRead() && file.canWrite()) {
			final FileOutputStream fos = new FileOutputStream(file);
			fos.write(buffer);
			fos.flush();
			fos.close();
		}

		file.setLastModified(con.getLastModified());

		con.disconnect();
		return con;
	}

	public static String downloadAsString(final URL url) throws IOException {
		final HttpURLConnection con = getConnection(url);
		final byte[] buffer = downloadBinary(con);
		return new String(buffer);
	}

	private static HttpURLConnection getConnection(final URL url) throws IOException {
		final HttpURLConnection con = GlobalConfiguration.getHttpConnection(url);
		con.setUseCaches(true);
		return con;
	}

	private static byte[] downloadBinary(final URLConnection con) throws IOException {
		final DataInputStream di = new DataInputStream(con.getInputStream());
		byte[] buffer = null;
		final int len = con.getContentLength();
		if (len == -1) {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			int b;
			while ((b = di.read()) != -1) {
				out.write(b);
			}
			buffer = out.toByteArray();
		} else {
			buffer = new byte[con.getContentLength()];
			di.readFully(buffer);
		}
		di.close();
		if (buffer != null) {
			buffer = ungzip(buffer);
		}
		return buffer;
	}

	private static byte[] ungzip(final byte[] data) {
		if (data.length < 2) {
			return data;
		}

		final int header = (data[0] | data[1] << 8) ^ 0xffff0000;
		if (header != GZIPInputStream.GZIP_MAGIC) {
			return data;
		}

		try {
			final ByteArrayInputStream b = new ByteArrayInputStream(data);
			final GZIPInputStream gzin = new GZIPInputStream(b);
			final ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);
			for (int c = gzin.read(); c != -1; c = gzin.read()) {
				out.write(c);
			}
			return out.toByteArray();
		} catch (final IOException e) {
			e.printStackTrace();
			return data;
		}
	}
}
