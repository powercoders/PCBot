package org.rsbot.script.provider;

import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.service.ServiceException;
import org.rsbot.util.GlobalConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

/**
 * @author Jacmob
 */
public class FileScriptSource implements ScriptSource {

	private final Logger log = Logger.getLogger(getClass().getSimpleName());

	private final File file;

	public FileScriptSource(final File file) {
		this.file = file;
	}

	public List<ScriptDefinition> list() {
		final LinkedList<ScriptDefinition> defs = new LinkedList<ScriptDefinition>();
		if (file != null) {
			if (file.isDirectory()) {
				try {
					final ClassLoader ldr = new ScriptClassLoader(file.toURI().toURL());
					for (final File f : file.listFiles()) {
						if (isJar(f)) {
							load(new ScriptClassLoader(getJarUrl(f)), defs, new JarFile(f));
						} else {
							load(ldr, defs, f, "");
						}
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
		return defs;
	}

	public Script load(final ScriptDefinition def) throws ServiceException {
		if (!(def instanceof FileScriptDefinition)) {
			throw new IllegalArgumentException("Invalid definition!");
		}
		final FileScriptDefinition fsd = (FileScriptDefinition) def;
		try {
			return fsd.clazz.asSubclass(Script.class).newInstance();
		} catch (final Exception ex) {
			throw new ServiceException(ex.toString());
		}
	}

	private void load(final ClassLoader loader, final LinkedList<ScriptDefinition> scripts, final JarFile jar) {
		final Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			final JarEntry e = entries.nextElement();
			final String name = e.getName().replace('/', '.');
			final String ext = ".class";
			if (name.endsWith(ext) && !name.contains("$")) {
				load(loader, scripts, name.substring(0, name.length() - ext.length()), null);
			}
		}
	}

	private void load(final ClassLoader loader, final LinkedList<ScriptDefinition> scripts, final File file, final String prefix) {
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

	private void load(final ClassLoader loader, final LinkedList<ScriptDefinition> scripts, final String name, final String path) {
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
			def.source = this;
			def.path = path;
			if (manifest.requiresVersion() <= GlobalConfiguration.getVersion()) {
				scripts.add(def);
			}
		}
	}

	private boolean isJar(final File file) {
		return file.getName().endsWith(".jar") || file.getName().endsWith(".dat");
	}

	private URL getJarUrl(final File file) throws IOException {
		URL url = file.toURI().toURL();
		url = new URL("jar:" + url.toExternalForm() + "!/");
		return url;
	}

	private static class FileScriptDefinition extends ScriptDefinition {
		Class<?> clazz;
	}

}
