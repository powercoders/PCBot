package org.rsbot.script.wrappers;

import org.rsbot.script.methods.Web;
import org.rsbot.script.web.Route;

import java.util.LinkedList;

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
		for (Route route : routes) {
			this.routes.addLast(route);
		}
		oldCount = Web.rs_map.size();
		this.start = start;
		this.end = end;
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

	public RSTile getStart() {
		return start;
	}

	public RSTile getEnd() {
		return end;
	}

	public void update() {
		for (Route route : getRoutes()) {
			if (route != null) {
				route.updateRoute();
			}
		}
	}
}