package org.rsbot.script.provider;

import org.rsbot.Configuration;
import org.rsbot.script.Script;
import org.rsbot.script.provider.FileScriptSource.FileScriptDefinition;
import org.rsbot.service.ServiceException;
import org.rsbot.util.io.HttpClient;
import org.rsbot.util.io.IOHelper;
import org.rsbot.util.io.IniParser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * @author Paris
 */
public class ScriptDeliveryNetwork implements ScriptSource {
	private static final Logger log = Logger.getLogger("ScriptDelivery");
	private static ScriptDeliveryNetwork instance;
	private URL base;
	final File manifest;

	private ScriptDeliveryNetwork() {
		manifest = getFile("manifests");
	}

	public static ScriptDeliveryNetwork getInstance() {
		if (instance == null) {
			instance = new ScriptDeliveryNetwork();
		}
		return instance;
	}

	private static File getFile(final String name) {
		return new File(Configuration.Paths.getCacheDirectory(), "sdn-" + name + ".txt");
	}

	private static void parseManifests(final HashMap<String, HashMap<String, String>> entries, final List<ScriptDefinition> defs) {
		for (final Entry<String, HashMap<String, String>> entry : entries.entrySet()) {
			final ScriptDefinition def = new ScriptDefinition();
			def.path = entry.getKey();
			final HashMap<String, String> values = entry.getValue();
			def.id = Integer.parseInt(values.get("id"));
			def.crc32 = values.containsKey("crc32") ? Long.parseLong(values.get("crc32")) : 0;
			def.name = values.get("name");
			def.version = Double.parseDouble(values.get("version"));
			def.description = values.get("description");
			def.authors = values.get("authors").split(ScriptList.DELIMITER);
			def.keywords = values.get("keywords").split(ScriptList.DELIMITER);
			def.website = values.get("website");
			defs.add(def);
		}
	}

	public void refresh(final boolean force) {
		final File controlFile = getFile("control");
		if (force || !manifest.exists()) {
			try {
				HttpClient.download(new URL(Configuration.Paths.URLs.SDN_CONTROL), controlFile);
				final HashMap<String, String> control = IniParser.deserialise(controlFile).get(IniParser.emptySection);
				if (control == null || !IniParser.parseBool(control.get("enabled")) || !control.containsKey("manifest")) {
					throw new ServiceException("Service currently disabled");
				}
				base = HttpClient.download(new URL(control.get("manifest")), manifest).getURL();
			} catch (final ServiceException e) {
				log.severe(e.getMessage());
			} catch (final IOException ignored) {
				log.warning("Unable to load scripts from the network");
			}
		}
	}

	@Override
	public List<ScriptDefinition> list() {
		final ArrayList<ScriptDefinition> defs = new ArrayList<ScriptDefinition>();
		refresh(false);
		try {
			parseManifests(IniParser.deserialise(manifest), defs);
		} catch (final IOException ignored) {
			log.warning("Error reading network script manifests");
		}
		for (final ScriptDefinition def : defs) {
			def.source = this;
		}
		return defs;
	}

	public Map<String, ScriptDefinition> listMap() {
		final List<ScriptDefinition> list = list();
		final Map<String, ScriptDefinition> map = new LinkedHashMap<String, ScriptDefinition>(list.size());
		for (final ScriptDefinition def : list) {
			map.put(def.path, def);
		}
		return map;
	}

	public List<String> listPaths() {
		final List<ScriptDefinition> list = list();
		final ArrayList<String> files = new ArrayList<String>(list.size());
		for (final ScriptDefinition def : list) {
			files.add(def.path);
		}
		return files;
	}

	private static File getCacheDirectory() {
		final File store = new File(Configuration.Paths.getScriptsNetworkDirectory());
		if (!store.exists()) {
			store.mkdirs();
		}
		if (Configuration.getCurrentOperatingSystem() == Configuration.OperatingSystem.WINDOWS) {
			final String path = "\"" + store.getAbsolutePath() + "\"";
			try {
				Runtime.getRuntime().exec("attrib +H " + path);
			} catch (final IOException ignored) {
			}
		}
		return store;
	}

	public void download(final ScriptDefinition def) {
		final File cache = new File(getCacheDirectory(), def.path);
		try {
			HttpClient.download(new URL(base, def.path), cache);
		} catch (final IOException ignored) {
		}
	}

	public void sync() {
		final ArrayList<Callable<Collection<Object>>> tasks = new ArrayList<Callable<Collection<Object>>>();
		final Map<String, ScriptDefinition> list = listMap();
		for (final File file : getCacheDirectory().listFiles()) {
			final String path = file.getName();
			if (!list.keySet().contains(path)) {
				file.delete();
			} else {
				tasks.add(new Callable<Collection<Object>>() {
					public Collection<Object> call() throws Exception {
						download(list.get(path));
						return null;
					}
				});
			}
		}
		final int threads = 2;
		final ExecutorService executorService = Executors.newFixedThreadPool(threads);
		try {
			executorService.invokeAll(tasks);
		} catch (final InterruptedException ignored) {
		}
	}

	@Override
	public Script load(final ScriptDefinition def) throws ServiceException {
		final File cache = new File(getCacheDirectory(), def.path);
		final LinkedList<ScriptDefinition> defs = new LinkedList<ScriptDefinition>();
		try {
			if (!cache.exists() || IOHelper.crc32(cache) != def.crc32) {
				log.info("Downloading script " + def.name + "...");
				download(def);
			}
			FileScriptSource.load(cache, defs, null);
			return FileScriptSource.load((FileScriptDefinition) defs.getFirst());
		} catch (final Exception ignored) {
			log.severe("Unable to load script");
		}
		return null;
	}
}
