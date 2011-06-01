package org.rsbot.script.provider;

import org.rsbot.Configuration;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.service.ServiceException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * @author Paris
 */
public class FileScriptSource implements ScriptSource {
	private static final Logger log = Logger.getLogger(FileScriptSource.class.getName());
	private final File[] files;

	public FileScriptSource(final File... file) {
		this.files = file;
	}

	public FileScriptSource(final String paths) {
		final String[] split = paths.split(File.pathSeparator);
		files = new File[split.length];
		for (int i = 0; i < split.length; i++) {
			files[i] = new File(split[i]);
		}
	}

	public LinkedList<ScriptDefinition> list() {
		final LinkedList<ScriptDefinition> defs = new LinkedList<ScriptDefinition>();
		for (final File file : files) {
			list(file, defs);
		}
		return defs;
	}

	private void list(final File file, final LinkedList<ScriptDefinition> defs) {
		if (file != null) {
			if (file.isDirectory()) {
				try {
					final ClassLoader loader = new ScriptClassLoader(file.toURI().toURL());
					for (final File item : file.listFiles()) {
						load(item, defs, loader);
					}
				} catch (final IOException ignored) {
				}
			} else if (isJar(file)) {
				try {
					final ClassLoader ldr = new ScriptClassLoader(getJarUrl(file));
					load(ldr, defs, new JarFile(file));
				} catch (final IOException ignored) {
				}
			}
		}
		for (final ScriptDefinition def : defs) {
			def.source = this;
		}
	}

	public Script load(final ScriptDefinition def) throws ServiceException {
		if (!(def instanceof FileScriptDefinition)) {
			throw new IllegalArgumentException("Invalid definition!");
		}
		try {
			return load((FileScriptDefinition) def);
		} catch (final Exception ex) {
			throw new ServiceException(ex.toString());
		}
	}

	public static Script load(final FileScriptDefinition def) throws InstantiationException, IllegalAccessException {
		return def.clazz.asSubclass(Script.class).newInstance();
	}

	public static void load(final File file, final LinkedList<ScriptDefinition> defs, ClassLoader loader) throws IOException {
		if (isJar(file)) {
			load(new ScriptClassLoader(getJarUrl(file)), defs, new JarFile(file));
		} else {
			if (loader == null) {
				loader = new ScriptClassLoader(file.getParentFile().toURI().toURL());
			}
			load(loader, defs, file, "");
		}
	}

	private static void load(final ClassLoader loader, final LinkedList<ScriptDefinition> scripts, final JarFile jar) {
		final Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			final JarEntry e = entries.nextElement();
			final String name = e.getName().replace('/', '.');
			final String ext = ".class";
			if (name.endsWith(ext) && !name.contains("$")) {
				load(loader, scripts, name.substring(0, name.length() - ext.length()), jar.getName());
			}
		}
	}

	private static void load(final ClassLoader loader, final LinkedList<ScriptDefinition> scripts, final File file, final String prefix) {
		if (file.isDirectory()) {
			if (!file.getName().startsWith(".")) {
				for (final File f : file.listFiles()) {
					load(loader, scripts, f, prefix + file.getName() + ".");
				}
			}
		} else {
			String name = prefix + file.getName();
			final String ext = ".class";
			if (name.endsWith(ext) && !name.startsWith(".") && !name.contains("!") && !name.contains("$")) {
				name = name.substring(0, name.length() - ext.length());
				load(loader, scripts, name, file.getAbsolutePath());
			}
		}
	}

	private static void load(final ClassLoader loader, final LinkedList<ScriptDefinition> scripts, final String name, final String path) {
		Class<?> clazz;
		try {
			clazz = loader.loadClass(name);
		} catch (final Exception e) {
			log.warning(name + " is not a valid script and was ignored!");
			e.printStackTrace();
			return;
		} catch (final VerifyError e) {
			log.warning(name + " is not a valid script and was ignored!");
			return;
		}
		if (clazz.isAnnotationPresent(ScriptManifest.class)) {
			final FileScriptDefinition def = new FileScriptDefinition();
			final ScriptManifest manifest = clazz.getAnnotation(ScriptManifest.class);
			def.id = 0;
			def.name = manifest.name();
			def.authors = manifest.authors();
			def.version = manifest.version();
			def.keywords = manifest.keywords();
			def.description = manifest.description();
			def.website = manifest.website();
			def.clazz = clazz;
			def.path = path;
			if (manifest.requiresVersion() <= Configuration.getVersion()) {
				scripts.add(def);
			}
		}
	}

	public static boolean isJar(final File file) {
		return file.getName().endsWith(".jar") || file.getName().endsWith(".dat");
	}

	public static URL getJarUrl(final File file) throws IOException {
		URL url = file.toURI().toURL();
		url = new URL("jar:" + url.toExternalForm() + "!/");
		return url;
	}

	public static class FileScriptDefinition extends ScriptDefinition {
		Class<?> clazz;
	}

}
