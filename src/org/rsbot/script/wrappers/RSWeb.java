package org.rsbot.script.wrappers;

import java.util.LinkedList;

import org.rsbot.script.methods.Web;
import org.rsbot.script.web.Route;

/**
 * A transportation action consisting of a list of routes.
 * 
 * @author Timer
 */
public class RSWeb {
	private final LinkedList<Route> routes = new LinkedList<Route>();
	private int oldCount = 0;
	private final RSTile start, end;

	public RSWeb(final Route[] routes, final RSTile start, final RSTile end) {
		for (final Route route : routes) {
			this.routes.addLast(route);
		}
		oldCount = Web.rs_map.size();
		this.start = start;
		this.end = end;
	}

	public boolean finished() {
		return routes.size() == 0;
	}

	public RSTile getEnd() {
		return end;
	}

	public Route[] getRoutes() {
		return routes.toArray(new Route[routes.size()]);
	}

	public RSTile getStart() {
		return start;
	}

	public boolean step() {
		if (routes.size() > 0) {
			if (Web.rs_map.size() != oldCount) {
				oldCount = Web.rs_map.size();
				update();
			}
			final Route route = routes.poll();
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

	public void update() {
		for (final Route route : getRoutes()) {
			if (route != null) {
				route.updateRoute();
			}
		}
	}
}
