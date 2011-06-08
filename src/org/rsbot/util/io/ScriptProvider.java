package org.rsbot.util.io;

import org.rsbot.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class ScriptProvider {
	public void extract() {
		if (Configuration.RUNNING_FROM_JAR) {
			try {
				ClassLoader loader = getClass().getClassLoader();
				URL version = Configuration.class.getClassLoader().getResource(Configuration.Paths.Resources.VERSION);
				String p = version.toString().replace("jar:file:", "").replace("!/" + Configuration.Paths.Resources.VERSION, "");
				try {
					p = URLDecoder.decode(p, "UTF-8");
				} catch (final UnsupportedEncodingException ignored) {
				}
				JarFile jar = new JarFile(new File(p));
				File out = new File(Configuration.Paths.getScriptsExtractedCache());
				FileOutputStream fos = new FileOutputStream(out);
				JarOutputStream jos = new JarOutputStream(fos);
				Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					JarEntry e = entries.nextElement();
					if (e.getName().startsWith("scripts/")) {
						InputStream in = loader.getResourceAsStream(e.getName());
						jos.putNextEntry(new JarEntry(e.getName().substring(8)));
						byte[] buffer = new byte[256];
						while (true) {
							int nRead = in.read(buffer, 0, buffer.length);
							if (nRead < 0) {
								break;
							}
							jos.write(buffer, 0, nRead);
						}
						in.close();
					}
				}
				jos.close();
				fos.close();
			} catch (Exception ignored) {
			}
		}
	}
}