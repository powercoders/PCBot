package org.rsbot.script.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.rsbot.bot.Bot;
import org.rsbot.script.BackgroundScript;
import org.rsbot.script.ScriptManifest;

public class BackgroundScriptHandler {
	private final HashMap<Integer, BackgroundScript> scripts = new HashMap<Integer, BackgroundScript>();
	private final HashMap<Integer, Thread> scriptThreads = new HashMap<Integer, Thread>();

	private final Bot bot;

	public BackgroundScriptHandler(final Bot bot) {
		this.bot = bot;
	}

	private void addScriptToPool(final BackgroundScript ss, final Thread t) {
		for (int off = 0; off < scripts.size(); ++off) {
			if (!scripts.containsKey(off)) {
				scripts.put(off, ss);
				ss.setID(off);
				scriptThreads.put(off, t);
				return;
			}
		}
		ss.setID(scripts.size());
		scripts.put(scripts.size(), ss);
		scriptThreads.put(scriptThreads.size(), t);
	}

	public Bot getBot() {
		return bot;
	}

	public Map<Integer, BackgroundScript> getRunningScripts() {
		return Collections.unmodifiableMap(scripts);
	}

	public void runScript(final BackgroundScript script) {
		script.init(bot.getMethodContext());
		final ScriptManifest prop = script.getClass().getAnnotation(ScriptManifest.class);
		final Thread t = new Thread(script, "BackgroundScript-" + prop.name());
		t.setDaemon(true);
		t.setPriority(Thread.MIN_PRIORITY);
		addScriptToPool(script, t);
		t.start();
	}

	public void stopAllScripts() {
		final Set<Integer> theSet = scripts.keySet();
		final int[] arr = new int[theSet.size()];
		int c = 0;
		for (final int i : theSet) {
			arr[c] = i;
			c++;
		}
		for (final int id : arr) {
			stopScript(id);
		}
	}

	public void stopScript() {
		final Thread curThread = Thread.currentThread();
		for (int i = 0; i < scripts.size(); i++) {
			final BackgroundScript script = scripts.get(i);
			if (script != null && script.isRunning()) {
				if (scriptThreads.get(i) == curThread) {
					stopScript(i);
				}
			}
		}
		if (curThread == null) {
			throw new ThreadDeath();
		}
	}

	public void stopScript(final int id) {
		final BackgroundScript script = scripts.get(id);
		if (script != null) {
			script.deactivate(id);
			scripts.remove(id);
			scriptThreads.remove(id);
		}
	}
}
