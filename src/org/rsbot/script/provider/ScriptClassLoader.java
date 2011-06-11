package org.rsbot.script.provider;

import java.awt.AWTPermission;
import java.io.ByteArrayOutputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.PropertyPermission;

import org.rsbot.Configuration;
import org.rsbot.security.RestrictedSecurityManager;

/**
 */
class ScriptClassLoader extends ClassLoader {
	private final ProtectionDomain domain;
	private final URL base;

	public ScriptClassLoader(final URL url) {
		base = url;
		final CodeSource codeSource = new CodeSource(base, (CodeSigner[]) null);
		domain = new ProtectionDomain(codeSource, getPermissions());
	}

	private Permissions getPermissions() {
		final Permissions ps = new Permissions();
		ps.add(new AWTPermission("accessEventQueue"));
		ps.add(new PropertyPermission("user.home", "read"));
		ps.add(new PropertyPermission("java.vendor", "read"));
		ps.add(new PropertyPermission("java.version", "read"));
		ps.add(new PropertyPermission("os.name", "read"));
		ps.add(new PropertyPermission("os.arch", "read"));
		ps.add(new PropertyPermission("os.version", "read"));
		ps.add(new SocketPermission("*", "resolve"));
		for (String host : RestrictedSecurityManager.ALLOWED_HOSTS) {
			if (host.startsWith(".")) {
				host = "*" + host;
			}
			ps.add(new SocketPermission(host + ":80", "connect,resolve"));
			ps.add(new SocketPermission(host + ":443", "connect,resolve"));
		}
		ps.add(new FilePermission(Configuration.Paths.getScriptCacheDirectory(), "read,write,delete"));
		ps.setReadOnly();
		return ps;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		Class clazz = findLoadedClass(name);

		if (clazz == null) {
			try {
				final InputStream in = getResourceAsStream(name.replace('.', '/') + ".class");
				final byte[] buffer = new byte[4096];
				final ByteArrayOutputStream out = new ByteArrayOutputStream();
				int n;
				while ((n = in.read(buffer, 0, 4096)) != -1) {
					out.write(buffer, 0, n);
				}
				final byte[] bytes = out.toByteArray();
				clazz = defineClass(name, bytes, 0, bytes.length, domain);
				if (resolve) {
					resolveClass(clazz);
				}
			} catch (final Exception e) {
				clazz = super.loadClass(name, resolve);
			}
		}

		return clazz;
	}

	@Override
	public URL getResource(final String name) {
		try {
			return new URL(base, name);
		} catch (final MalformedURLException e) {
			return null;
		}
	}

	@Override
	public InputStream getResourceAsStream(final String name) {
		try {
			return new URL(base, name).openStream();
		} catch (final IOException e) {
			return null;
		}
	}

}
