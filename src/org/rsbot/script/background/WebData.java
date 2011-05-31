package org.rsbot.script.background;

import org.rsbot.script.BackgroundScript;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Web;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.service.WebQueue;

import java.util.HashMap;

@ScriptManifest(name = "Web Data Collector", authors = {"Timer"})
public class WebData extends BackgroundScript {
	private RSTile lastBase = null;
	private int lastPlane = -1;
	public final HashMap<RSTile, Integer> collectionMap = new HashMap<RSTile, Integer>();
	private static final Object botCollectionLock = new Object();

	@Override
	public boolean activateCondition() {
		final RSTile curr_base = game.getMapBase();
		final int curr_plane = game.getPlane();
		return Web.isLoaded() && game.isLoggedIn() && ((lastBase == null || !lastBase.equals(curr_base)) || (lastPlane == -1 || lastPlane != curr_plane));
	}

	@Override
	public int loop() {
		try {
			sleep(5000);
			final RSTile curr_base = game.getMapBase();
			final int curr_plane = game.getPlane();
			collectionMap.clear();
			if (!curr_base.equals(game.getMapBase())) {
				return -1;
			}
			lastBase = curr_base;
			lastPlane = curr_plane;
			final int flags[][] = walking.getCollisionFlags(curr_plane);
			for (int i = 3; i < 102; i++) {
				for (int j = 3; j < 102; j++) {
					final RSTile collectingTile = new RSTile(curr_base.getX() + i, curr_base.getY() + j, curr_plane);
					final int base_x = game.getBaseX(), base_y = game.getBaseY();
					final int curr_x = collectingTile.getX() - base_x, curr_y = collectingTile.getY() - base_y;
					final RSTile offset = walking.getCollisionOffset(curr_plane);
					final int off_x = offset.getX();
					final int off_y = offset.getY();
					final int flagIndex_x = curr_x - off_x, flagIndex_y = curr_y - off_y;
					final int here = flags[flagIndex_x][flagIndex_y];
					synchronized (botCollectionLock) {
						if (!Web.rs_map.containsKey(collectingTile) && (!RSTile.Walkable(here) || RSTile.Questionable(here))) {
							collectionMap.put(collectingTile, here);
						} else {
							if (Web.rs_map.containsKey(collectingTile) && Web.rs_map.get(collectingTile) != here) {
								WebQueue.Remove(collectingTile);
								lastBase = null;
								lastPlane = -1;
							}
						}
					}
				}
			}
			WebQueue.Add(collectionMap);
			return -1;
		} catch (final Exception ignored) {
		}
		return -1;
	}

	@Override
	public int iterationSleep() {
		return 1000;
	}
}