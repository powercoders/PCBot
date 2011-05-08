package org.rsbot.script.internal;

import org.rsbot.bot.Bot;
import org.rsbot.script.BackgroundScript;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.internal.event.BackgroundScriptListener;

import java.util.*;

public class BackgroundScriptHandler {
	private final HashMap<Integer, BackgroundScript> scripts = new HashMap<Integer, BackgroundScript>();
	private final HashMap<Integer, Thread> scriptThreads = new HashMap<Integer, Thread>();

	private final Set<BackgroundScriptListener> listeners = Collections.synchronizedSet(new HashSet<BackgroundScriptListener>());

	private final Bot bot;

	public BackgroundScriptHandler(Bot bot) {
		this.bot = bot;
	}

	public void addScriptListener(BackgroundScriptListener l) {
		listeners.add(l);
	}

	public void removeScriptListener(BackgroundScriptListener l) {
		listeners.remove(l);
	}

	private void addScriptToPool(BackgroundScript ss, Thread t) {
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

	public void stopScript(int id) {
		BackgroundScript script = scripts.get(id);
		if (script != null) {
			script.deactivate(id);
			scripts.remove(id);
			scriptThreads.remove(id);
			for (BackgroundScriptListener l : listeners) {
				l.scriptStopped(this, script);
			}
		}
	}

	public void runScript(BackgroundScript script) {
		script.init(bot.getMethodContext());
		for (BackgroundScriptListener l : listeners) {
			l.scriptStarted(this, script);
		}
		ScriptManifest prop = script.getClass().getAnnotation(ScriptManifest.class);
		Thread t = new Thread(script, "BackgroundScript-" + prop.name());
		addScriptToPool(script, t);
		t.start();
	}

	public void stopAllScripts() {
		Set<Integer> theSet = scripts.keySet();
		int[] arr = new int[theSet.size()];
		int c = 0;
		for (int i : theSet) {
			arr[c] = i;
			c++;
		}
		for (int id : arr) {
			stopScript(id);
		}
	}

	public void stopScript() {
		Thread curThread = Thread.currentThread();
		for (int i = 0; i < scripts.size(); i++) {
			BackgroundScript script = scripts.get(i);
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
}
