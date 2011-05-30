package org.rsbot.script.background;

import org.rsbot.script.BackgroundScript;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.util.BankCache;
import org.rsbot.script.wrappers.RSItem;

import java.util.HashMap;

@ScriptManifest(name = "Bank Monitor", authors = {"Timer"})
public class BankMonitor extends BackgroundScript {
	private final HashMap<String, Long> updateTimes = new HashMap<String, Long>();
	private static final Object lock = new Object();

	@Override
	public boolean activateCondition() {
		return game.isLoggedIn() && bank.isOpen();
	}

	@Override
	public int loop() {
		synchronized (lock) {
			sleep(1500);
			final String accountName = getMyPlayer().getName().trim();
			if (updateTimes.keySet().contains(accountName.toLowerCase())) {
				final long lastTime = updateTimes.get(accountName.toLowerCase());
				if (System.currentTimeMillis() - lastTime < 30000) {
					return -1;
				} else {
					updateTimes.remove(accountName.toLowerCase());
				}
			}
			if (bank.isOpen()) {
				final RSItem[] rsItems = bank.getItems();
				try {
					BankCache.Save(accountName, rsItems);
				} catch (final Exception e) {
					e.printStackTrace();
				}
				updateTimes.put(accountName, System.currentTimeMillis());
			}
		}
		return -1;
	}

	@Override
	public int iterationSleep() {
		return 100;
	}
}