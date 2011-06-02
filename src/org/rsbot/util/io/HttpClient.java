package org.rsbot.util.io;

import org.rsbot.Configuration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * @author Paris
 */
public class HttpClient {
	private static final Logger log = Logger.getLogger(HttpClient.class.getName());
	static String httpUserAgent = null;

	public static String getHttpUserAgent() {
		if (httpUserAgent == null) {
			httpUserAgent = getDefaultHttpUserAgent();
		}
		return httpUserAgent;
	}

	private static String getDefaultHttpUserAgent() {
		final boolean x64 = System.getProperty("sun.arch.data.model").equals("64");
		final String os;
		switch (Configuration.getCurrentOperatingSystem()) {
			case MAC:
				os = "Macintosh; Intel Mac OS X 10_6_6";
				break;
			case LINUX:
				os = "X11; Linux " + (x64 ? "x86_64" : "i686");
				break;
			default:
				os = "Windows NT 6.1" + (x64 ? "; WOW64" : "");
				break;
		}
		final StringBuilder buf = new StringBuilder(125);
		buf.append("Mozilla/5.0 (").append(os).append(")");
		buf.append(" AppleWebKit/534.24 (KHTML, like Gecko) Chrome/11.0.696.68 Safari/534.24");
		return buf.toString();
	}

	public static HttpURLConnection getHttpConnection(final URL url) throws IOException {
		final HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		con.addRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		con.addRequestProperty("Accept-Encoding", "gzip,deflate");
		con.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
		con.addRequestProperty("Host", url.getHost());
		con.addRequestProperty("User-Agent", getHttpUserAgent());
		con.setConnectTimeout(10000);
		return con;
	}

	private static HttpURLConnection getConnection(final URL url) throws IOException {
		final HttpURLConnection con = getHttpConnection(url);
		con.setUseCaches(true);
		return con;
	}

	public static HttpURLConnection download(final URL url, final File file) throws IOException {
		return download(getConnection(url), file);
	}

	public static HttpURLConnection download(final HttpURLConnection con, final File file) throws IOException {
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
		return downloadAsString(getConnection(url));
	}

	public static String downloadAsString(final HttpURLConnection con) throws IOException {
		final byte[] buffer = downloadBinary(con);
		return new String(buffer);
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
