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
					final BufferedReader bufferedReader = new BufferedReader(new FileReader(Configuration.Paths.getWebDatabase()));
					String dataLine;
					final HashMap<RSTile, Integer> mapData = new HashMap<RSTile, Integer>();
					while ((dataLine = bufferedReader.readLine()) != null) {
						final String[] storeData = dataLine.split("k");
						if (storeData.length == 2) {
							final String[] tileData = storeData[0].split(",");
							if (tileData.length == 3) {
								try {
									final RSTile tile = new RSTile(Integer.parseInt(tileData[0]), Integer.parseInt(tileData[1]), Integer.parseInt(tileData[2]));
									final int tileFlag = Integer.parseInt(storeData[1]);
									if (mapData.containsKey(tile)) {
										WebQueue.Remove(dataLine);//Line is double, remove from file--bad collection!
									} else {
										mapData.put(tile, tileFlag);
									}
								} catch (final Exception e) {
								}
							} else {
								WebQueue.Remove(dataLine);//Line is bad, remove from file.
							}
						} else {
							WebQueue.Remove(dataLine);//Line is bad, remove from file.
						}
					}
					Web.rs_map.putAll(mapData);
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
