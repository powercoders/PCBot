package org.rsbot.script.background;

import org.rsbot.Configuration;
import org.rsbot.script.BackgroundScript;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Web;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.service.WebQueue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

@ScriptManifest(name = "Web Data Loader", authors = {"Timer"})
public class WebLoader extends BackgroundScript {
	private static final Object lock = new Object();

	@Override
	public boolean activateCondition() {
		return !Web.loaded;
	}

	@Override
	public int loop() {
		synchronized (lock) {
			if (Web.loaded) {
				deactivate(getID());
			}
			if (!Web.loaded) {
				try {
					if (!new File(Configuration.Paths.getWebDatabase()).exists()) {
						Web.loaded = true;
						deactivate(getID());
						return -1;
					}
					final BufferedReader br = new BufferedReader(new FileReader(Configuration.Paths.getWebDatabase()));
					String line;
					final HashMap<RSTile, Integer> flagsArray = new HashMap<RSTile, Integer>();
					while ((line = br.readLine()) != null) {
						final String[] d = line.split("k");
						if (d.length == 2) {
							final String[] tD = d[0].split(",");
							if (tD.length == 3) {
								try {
									final RSTile tile = new RSTile(Integer.parseInt(tD[0]), Integer.parseInt(tD[1]), Integer.parseInt(tD[2]));
									final int flag = Integer.parseInt(d[1]);
									if (flagsArray.containsKey(tile)) {
										WebQueue.Remove(line);//Line is double, remove from file--bad collection!
									} else {
										flagsArray.put(tile, flag);
									}
								} catch (final Exception e) {
								}
							} else {
								WebQueue.Remove(line);//Line is bad, remove from file.
							}
						} else {
							WebQueue.Remove(line);//Line is bad, remove from file.
						}
					}
					Web.map.putAll(flagsArray);
					Web.loaded = true;
				} catch (final Exception e) {
					log("Failed to load the web.. trying again.");
				}
			}
			if (Web.loaded) {
				deactivate(getID());
			}
		}
		return -1;
	}

	@Override
	public int iterationSleep() {
		return 5000;
	}
}
