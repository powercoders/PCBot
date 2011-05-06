package org.rsbot.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.net.URL;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.SecureClassLoader;

import org.rsbot.util.GlobalConfiguration;

/**
 * @author Jacmob
 */
class ScriptClassLoader extends SecureClassLoader {

	private final URL base;

	public ScriptClassLoader(URL url) {
		this.base = url;
	}

	protected PermissionCollection getPermissions(CodeSource codesource) {
		Permissions perms = new Permissions();
		perms.add(new FilePermission("<<ALL FILES>>", ""));
		perms.add(new FilePermission(GlobalConfiguration.Paths.getScriptCacheDirectory() + File.separator + "/-", "read,write,delete"));
		perms.add(new SocketPermission("*.powerbot.org:80", "connect"));
		perms.add(new SocketPermission("*.imageshack.us:80", "connect"));
		perms.add(new SocketPermission("*.tinypic.com:80", "connect"));
		perms.add(new SocketPermission("*.photobucket.com:80", "connect"));
		perms.add(new SocketPermission("i.imgur.com:80", "connect"));
		return perms;
	}

	@SuppressWarnings("rawtypes")
	public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class clazz = findLoadedClass(name);

		if (clazz == null) {
			try {
				InputStream in = getResourceAsStream(name.replace('.', '/') + ".class");
				byte[] buffer = new byte[4096];
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				int n;
				while ((n = in.read(buffer, 0, 4096)) != -1) {
					out.write(buffer, 0, n);
				}
				byte[] bytes = out.toByteArray();
				clazz = defineClass(name, bytes, 0, bytes.length);
				if (resolve) {
					resolveClass(clazz);
				}
			} catch (Exception e) {
				clazz = super.loadClass(name, resolve);
			}
		}

		return clazz;
	}

	public URL getResource(String name) {
		try {
			return new URL(base, name);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public InputStream getResourceAsStream(String name) {
		try {
			return new URL(base, name).openStream();
		} catch (IOException e) {
			return null;
		}
	}

}
