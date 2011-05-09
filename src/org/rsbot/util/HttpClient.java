package org.rsbot.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * @author Paris
 */
public class HttpClient {
	private static final Logger log = Logger.getLogger(HttpClient.class.getName());

	public static HttpURLConnection download(final URL url, final File file) throws IOException {
		final HttpURLConnection con = GlobalConfiguration.getHttpConnection(url);
		con.setUseCaches(true);

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

		final DataInputStream di = new DataInputStream(con.getInputStream());
		byte[] buffer = new byte[con.getContentLength()];
		di.readFully(buffer);
		di.close();
		buffer = ungzip(buffer);

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
