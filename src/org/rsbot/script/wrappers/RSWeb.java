package org.rsbot.script.wrappers;

import org.rsbot.script.methods.Web;
import org.rsbot.script.web.Route;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A transportation action consisting of a list of routes.
 *
 * @author Timer
 */
public class RSWeb {
	private final LinkedList<Route> routes = new LinkedList<Route>();
	private int oldCount = 0;

	public RSWeb(final Route[] routes) {
		for (Route route : routes) {
			this.routes.addLast(route);
		}
		oldCount = Web.rs_map.size();
	}

	public Route[] getRoutes() {
		return routes.toArray(new Route[routes.size()]);
	}

	public boolean step() {
		if (routes.size() > 0) {
			if (Web.rs_map.size() != oldCount) {
				oldCount = Web.rs_map.size();
				update();
			}
			Route route = routes.poll();
			if (route.execute()) {
				if (!route.finished()) {
					routes.addFirst(route);
				}
				return true;
			} else {
				return false;
			}
		}
		return true;
	}

	public boolean finished() {
		return routes.size() == 0;
	}

	public List<RSTile[]> getPaths() {
		List<RSTile[]> l = new ArrayList<RSTile[]>();
		for (Route route : getRoutes()) {
			if (route != null && route.getPaths().size() > 0) {
				for (RSTile[] rsTiles : route.getPaths()) {
					l.add(rsTiles);
				}
			}
		}
		return l;
	}

	public double getDistance() {
		List<RSTile[]> paths = getPaths();
		double d = 0.0D;
		for (RSTile[] path : paths) {
			RSTile last = null;
			for (RSTile tile : path) {
				if (last != null) {
					d += tile.getX() != last.getX() && tile.getY() != last.getY() ? 1.41421356D : 1.0D;
				}
				last = tile;
			}
		}
		return d;
	}

	public void update() {
		for (Route route : getRoutes()) {
			if (route != null) {
				route.updateRoute();
			}
		}
	}
}