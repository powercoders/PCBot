package org.rsbot.script.background;

import org.rsbot.script.BackgroundScript;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.util.BankCache;
import org.rsbot.script.wrappers.RSItem;

import java.util.HashMap;

@ScriptManifest(name = "Bank Monitor", authors = {"Timer"})
public class BankMonitor extends BackgroundScript {
	private final HashMap<String, Long> updateTimes = new HashMap<String, Long>();

	@Override
	public boolean activateCondition() {
		return bank.isOpen();
	}

	@Override
	public int loop() {
		String accountName = account.getName();
		if (updateTimes.keySet().contains(accountName.toLowerCase())) {
			long lastTime = updateTimes.get(accountName.toLowerCase());
			if (System.currentTimeMillis() - lastTime < 30000) {
				return -1;
			} else {
				updateTimes.remove(accountName.toLowerCase());
			}
		}
		if (bank.isOpen()) {
			RSItem[] rsItems = bank.getItems();
			try {
				BankCache.Save(accountName, rsItems);
			} catch (Exception e) {
				e.printStackTrace();
			}
			updateTimes.put(accountName, System.currentTimeMillis());
		}
		return -1;
	}

	@Override
	public int iterationSleep() {
		return 100;
	}
}